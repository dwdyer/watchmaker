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
import java.util.Iterator;
import java.util.List;
import java.util.Random;

/**
 * This class implements the evolution technique used by a typical generational
 * evolutionary algorithm.
 * @author Daniel Dyer
 */
class GenerationalEvolution<T> implements EvolutionType<T>
{
    private final FitnessEvaluator<? super T> fitnessEvaluator;
    private final SelectionStrategy<? super T> selectionStrategy;
    private final EvolutionaryOperator<T> evolutionScheme;
    private final FitnessEvaluationStrategy evaluationStrategy;


    GenerationalEvolution(EvolutionaryOperator<T> evolutionScheme,
                          FitnessEvaluator<? super T> fitnessEvaluator,
                          SelectionStrategy<? super T> selectionStrategy,
                          FitnessEvaluationStrategy evaluationStrategy)
    {
        this.fitnessEvaluator = fitnessEvaluator;
        this.selectionStrategy = selectionStrategy;
        this.evolutionScheme = evolutionScheme;
        this.evaluationStrategy = evaluationStrategy;
    }


    public List<EvaluatedCandidate<T>> evolvePopulation(List<EvaluatedCandidate<T>> evaluatedPopulation,
                                                        int eliteCount,
                                                        Random rng)
    {
        List<T> population = new ArrayList<T>(evaluatedPopulation.size());

        // First perform any elitist selection.
        List<T> elite = new ArrayList<T>(eliteCount);
        Iterator<EvaluatedCandidate<T>> iterator = evaluatedPopulation.iterator();
        while (elite.size() < eliteCount)
        {
            elite.add(iterator.next().getCandidate());
        }
        // Then select candidates that will be operated on to create the evolved
        // portion of the next generation.
        population.addAll(selectionStrategy.select(evaluatedPopulation,
                                                   fitnessEvaluator.isNatural(),
                                                   evaluatedPopulation.size() - eliteCount,
                                                   rng));
        // Then evolve the population.
        population = evolutionScheme.apply(population, rng);
        // When the evolution is finished, add the elite to the population.
        population.addAll(elite);
        return this.evaluationStrategy.evaluatePopulation(population, fitnessEvaluator);
    }
}
