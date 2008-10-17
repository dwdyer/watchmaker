// ============================================================================
//   Copyright 2006, 2007, 2008 Daniel W. Dyer
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
import org.uncommons.watchmaker.framework.interactive.InteractiveSelection;
import org.uncommons.watchmaker.framework.interactive.NullFitnessEvaluator;

/**
 * <p>Single-threaded {@link EvolutionEngine}.  All work is completed synchronously
 * on the request thread.  This implementation does not take advantage of the
 * parallelism offered by multi-processor, multi-core or hyper-threaded machines.</p>
 *
 * <p>This evolution engine is suitable for restricted/managed environments
 * (where it is not permitted for applications to manage their own threads).
 * Most applications should use the {@link ConcurrentEvolutionEngine} instead.</p>
 *
 * @param <T> The type of entity that is to be evolved.
 * @author Daniel Dyer
 * @see ConcurrentEvolutionEngine
 * @see CandidateFactory
 * @see FitnessEvaluator
 * @see SelectionStrategy
 * @see EvolutionaryOperator
 */
public class SequentialEvolutionEngine<T> extends AbstractEvolutionEngine<T>
{
    /**
     * Creates a new evolution engine by specifying the various components required by
     * an evolutionary algorithm.
     * @param candidateFactory Factory used to create the initial population that is
     * iteratively evolved.
     * @param evolutionScheme The combination of evolutionary operators used to evolve
     * the population at each generation.
     * @param fitnessEvaluator A function for assigning fitness scores to candidate
     * solutions.
     * @param selectionStrategy A strategy for selecting which candidates survive to
     * be evolved.
     * @param rng The source of randomness used by all stochastic processes (including
     * evolutionary operators and selection strategies).
     */
    public SequentialEvolutionEngine(CandidateFactory<T> candidateFactory,
                                     EvolutionaryOperator<? super T> evolutionScheme,
                                     FitnessEvaluator<? super T> fitnessEvaluator,
                                     SelectionStrategy<? super T> selectionStrategy,
                                     Random rng)
    {
        super(candidateFactory,
              evolutionScheme,
              fitnessEvaluator,
              selectionStrategy,
              rng);
    }


    /**
     * Creates a new evolution engine for an interactive evolutionary algorithm.  It
     * is not necessary to specify a fitness evaluator for interactive evolution.
     * @param candidateFactory Factory used to create the initial population that is
     * iteratively evolved.
     * @param evolutionScheme The combination of evolutionary operators used to evolve
     * the population at each generation.
     * @param selectionStrategy Interactive selection strategy configured with appropriate
     * console.
     * @param rng The source of randomness used by all stochastic processes (including
     * evolutionary operators and selection strategies).
     */
    public SequentialEvolutionEngine(CandidateFactory<T> candidateFactory,
                                     EvolutionaryOperator<? super T> evolutionScheme,
                                     InteractiveSelection<T> selectionStrategy,
                                     Random rng)
    {
        this(candidateFactory,
             evolutionScheme,
             new NullFitnessEvaluator(), // No fitness evaluations to perform.
             selectionStrategy,
             rng);
    }


    /**
     * {@inheritDoc} 
     */
    protected List<EvaluatedCandidate<T>> evaluatePopulation(List<T> population)
    {
        List<EvaluatedCandidate<T>> evaluatedPopulation = new ArrayList<EvaluatedCandidate<T>>(population.size());
        for (T candidate : population)
        {
            evaluatedPopulation.add(new EvaluatedCandidate<T>(candidate,
                                                              getFitnessEvaluator().getFitness(candidate,
                                                                                               population)));
        }
        return evaluatedPopulation;
    }
}
