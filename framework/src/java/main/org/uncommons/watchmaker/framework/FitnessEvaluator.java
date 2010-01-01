//=============================================================================
// Copyright 2006-2010 Daniel W. Dyer
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
//     http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.
//=============================================================================
package org.uncommons.watchmaker.framework;

import java.util.List;

/**
 * Calculates the fitness score of a given candidate of the appropriate type.
 * Fitness evaluations may be executed concurrently and therefore any access
 * to mutable shared state should be properly synchronised.
 * @param <T> The type of evolvable entity that can be evaluated.
 * @author Daniel Dyer
 */
public interface FitnessEvaluator<T>
{
    /**
     * Calculates a fitness score for the given candidate.  Whether
     * a higher score indicates a fitter candidate or not depends on
     * whether the fitness scores are natural (see {@link #isNatural}).
     * This method must always return a value greater than or equal to
     * zero.  Framework behaviour is undefined for negative fitness scores.
     * @param candidate The candidate solution to calculate fitness for.
     * @param population The entire population.  This will include the
     * specified candidate.  This is provided for fitness evaluators that
     * evaluate individuals in the context of the population that they are
     * part of (e.g. a program that evolves game-playing strategies may wish
     * to play each strategy against each of the others).  This parameter
     * can be ignored by simple fitness evaluators.  When iterating
     * over the population, a simple reference equality check (==) can be
     * used to identify which member of the population is the specified
     * candidate.
     * @return The fitness score for the specified candidate.  Must always be
     * a non-negative value regardless of natural or non-natural evaluation is
     * being used.
     */
    double getFitness(T candidate,
                      List<? extends T> population);

    /**
     * <p>Specifies whether this evaluator generates <i>natural</i> fitness
     * scores or not.</p>
     * <p>Natural fitness scores are those in which the fittest
     * individual in a population has the highest fitness value.  In this
     * case the algorithm is attempting to maximise fitness scores.
     * There need not be a specified maximum possible value.</p>
     * <p>In contrast, <i>non-natural</i> fitness evaluation results in fitter
     * individuals being assigned lower scores than weaker individuals.
     * In the case of non-natural fitness, the algorithm is attempting to
     * minimise fitness scores.</p>
     * <p>An example of a situation in which non-natural fitness scores are
     * preferable is when the fitness corresponds to a cost and the algorithm
     * is attempting to minimise that cost.</p>
     * <p>The terminology of <i>natural</i> and <i>non-natural</i> fitness scores
     * is introduced by the Watchmaker Framework to describe the two types of fitness
     * scoring that exist within the framework.  It does not correspond to either
     * <i>standardised fitness</i> or <i>normalised fitness</i> in the EA
     * literature.  Standardised fitness evaluation generates non-natural
     * scores with a score of zero corresponding to the best possible fitness.
     * Normalised fitness evaluation is similar to standardised fitness but
     * with the scores adjusted to fall within the range 0 - 1.</p>
     * @return True if a high fitness score means a fitter candidate
     * or false if a low fitness score means a fitter candidate.
     */
    boolean isNatural();
}
