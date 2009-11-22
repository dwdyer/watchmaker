// ============================================================================
//   Copyright 2006-2009 Daniel W. Dyer
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
package org.uncommons.watchmaker.framework;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * An implementation of steady-state evolution, which is a type of evolutionary algorithm
 * where a population is changed incrementally, with one individual evolved at a time.  This
 * differs from {@link GenerationalEvolution} in which the entire population is evolved in
 * parallel.
 * @see GenerationalEvolution
 * @author Daniel Dyer
 */
public class SteadyStateEvolution<T> implements PopulationEvolution<T>
{
    private final EvolutionaryOperator<T> evolutionScheme;
    private final FitnessEvaluator<? super T> fitnessEvaluator;
    private final SelectionStrategy<? super T> selectionStrategy;
    private final int selectionSize;


    public SteadyStateEvolution(EvolutionaryOperator<T> evolutionScheme,
                                FitnessEvaluator<? super T> fitnessEvaluator,                                
                                SelectionStrategy<? super T> selectionStrategy,
                                int selectionSize)
    {
        this.fitnessEvaluator = fitnessEvaluator;
        this.evolutionScheme = evolutionScheme;
        this.selectionStrategy = selectionStrategy;
        this.selectionSize = selectionSize;
    }


    public List<EvaluatedCandidate<T>> evolvePopulation(List<EvaluatedCandidate<T>> evaluatedPopulation,
                                                        int eliteCount,
                                                        Random rng)
    {
        EvolutionUtils.sortEvaluatedPopulation(evaluatedPopulation, fitnessEvaluator.isNatural());
        List<T> selectedCandidates = selectionStrategy.select(evaluatedPopulation,
                                                              fitnessEvaluator.isNatural(),
                                                              selectionSize,
                                                              rng);
        for (int i = 0; i < selectionSize; i++)
        {
            selectedCandidates.add(evaluatedPopulation.get(rng.nextInt(evaluatedPopulation.size())).getCandidate());
        }
        List<EvaluatedCandidate<T>> offspring = evaluatePopulation(evolutionScheme.apply(selectedCandidates, rng));

        doReplacement(evaluatedPopulation, offspring, eliteCount, rng);

        return evaluatedPopulation;
    }


    /**
     * Add the offspring to the population, removing the same number of existing individuals to make
     * space for them.
     * This method randomly chooses which individuals should be replaced, but it can be over-ridden
     * in sub-classes if alternative behaviour is required.
     */
    protected void doReplacement(List<EvaluatedCandidate<T>> existingPopulation,
                                 List<EvaluatedCandidate<T>> newCandidates,
                                 int eliteCount,
                                 Random rng)
    {
        assert newCandidates.size() < existingPopulation.size() - eliteCount : "Too many new candidates for replacement.";
        for (EvaluatedCandidate<T> candidate : newCandidates)
        {
            // Replace a randomly selected individual, but not one of the "elite" individuals at the
            // beginning of the sorted population.
            existingPopulation.set(rng.nextInt(existingPopulation.size() - eliteCount) + eliteCount, candidate);
        }
    }


    /**
     * {@inheritDoc}
     */
    public List<EvaluatedCandidate<T>> evaluatePopulation(List<T> population)
    {
        List<EvaluatedCandidate<T>> evaluatedPopulation = new ArrayList<EvaluatedCandidate<T>>(population.size());
        for (T candidate : population)
        {
            evaluatedPopulation.add(new EvaluatedCandidate<T>(candidate,
                                                              fitnessEvaluator.getFitness(candidate, population)));
        }
        return evaluatedPopulation;
    }
}
