// ============================================================================
//   Copyright 2006 Daniel W. Dyer
//
//   Licensed under the Apache License, Version 2.0 (the "License");
//   you may not use this file except in compliance with the License.
//   You may obtain a copy of the License at
//
//       http://www.apache.org/licenses/LICENSE-2.0
//
//   Unless required by applicable law or agreed to in writing, software
//   distributed under the License is distributed on an "AS IS" BASIS,
//   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
//   See the License for the specific language governing permissions and
//   limitations under the License.
// ============================================================================
package org.uncommons.watchmaker.examples.travellingsalesman;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;
import org.uncommons.maths.random.MersenneTwisterRNG;
import org.uncommons.maths.random.PoissonGenerator;
import org.uncommons.watchmaker.framework.CandidateFactory;
import org.uncommons.watchmaker.framework.EvolutionEngine;
import org.uncommons.watchmaker.framework.EvolutionObserver;
import org.uncommons.watchmaker.framework.EvolutionaryOperator;
import org.uncommons.watchmaker.framework.PopulationData;
import org.uncommons.watchmaker.framework.SelectionStrategy;
import org.uncommons.watchmaker.framework.StandaloneEvolutionEngine;
import org.uncommons.watchmaker.framework.factories.ListPermutationFactory;
import org.uncommons.watchmaker.framework.operators.ListOrderMutation;

/**
 * Evolutionary algorithm for finding (approximate) solutions to the
 * travelling salesman problem.
 * @author Daniel Dyer
 */
public class EvolutionaryTravellingSalesman implements TravellingSalesmanStrategy
{
    private final DistanceLookup distances;
    private final SelectionStrategy<? super List<String>> selectionStrategy;
    private final int populationSize;
    private final int eliteCount;
    private final int generationCount;

    /**
     * Creates an evolutionary Travelling Salesman solver with the
     * specified configuration.
     * @param distances Information about the distances between cities.
     * @param selectionStrategy The selection implementation to use for
     * the evolutionary algorithm.
     * @param populationSize The number of candidates in the population
     * of evolved routes.
     * @param eliteCount The number of candidates to preserve via elitism
     * at each generation.
     * @param generationCount The number of iterations of evolution to perform.
     */
    public EvolutionaryTravellingSalesman(DistanceLookup distances,
                                          SelectionStrategy<? super List<String>> selectionStrategy,
                                          int populationSize,
                                          int eliteCount,
                                          int generationCount)
    {
        if (eliteCount < 0 || eliteCount >= populationSize)
        {
            throw new IllegalArgumentException("Elite count must be non-zero and less than population size.");
        }
        this.distances = distances;
        this.selectionStrategy = selectionStrategy;
        this.populationSize = populationSize;
        this.eliteCount = eliteCount;
        this.generationCount = generationCount;
    }


    /**
     * {@inheritDoc}
     */
    public String getDescription()
    {
        String selectionName = selectionStrategy.getClass().getSimpleName();
        return "Evolution (pop: " + populationSize + ", gen: " + generationCount
                + ", elite: " + eliteCount + ", " + selectionName + ")";
    }


    /**
     * Calculates the shortest route using a generational evolutionary
     * algorithm with a single ordered mutation operator and truncation
     * selection.
     * @param cities The list of destinations, each of which must be visited
     * once.
     * @param progressListener Call-back for receiving the status of the
     * algorithm as it progresses.
     * @return The (approximate) shortest route that visits each of the
     * specified cities once.
     */
    public List<String> calculateShortestRoute(Collection<String> cities,
                                               final ProgressListener progressListener)
    {
        Random rng = new MersenneTwisterRNG();
        EvolutionaryOperator<List<?>> evolutionStrategy = new ListOrderMutation(new PoissonGenerator(1.5, rng),
                                                                                new PoissonGenerator(1.5, rng));
        CandidateFactory<List<String>> candidateFactory
            = new ListPermutationFactory<String>(new LinkedList<String>(cities));
        EvolutionEngine<List<String>> engine
            = new StandaloneEvolutionEngine<List<String>>(candidateFactory,
                                                          evolutionStrategy,
                                                          new RouteEvaluator(distances),
                                                          selectionStrategy,
                                                          rng);
        engine.addEvolutionObserver(new EvolutionObserver<List<String>>()
        {
            public void populationUpdate(PopulationData<List<String>> data)
            {
                progressListener.updateProgress(((double) data.getGenerationNumber() + 1) / generationCount * 100);
            }
        });
        return engine.evolve(populationSize,
                             eliteCount,
                             generationCount);
    }
}
