// ============================================================================
//   Copyright 2006, 2007 Daniel W. Dyer
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

import java.util.Collection;

/**
 * Operations for classes that provide an evolution implementation.
 * @param <T> The type of entity evolved by the evolution engine.
 * @author Daniel Dyer
 */
public interface EvolutionEngine<T>
{
    /**
     * @param populationSize The number of candidate solutions present in the population
     * at any point in time.
     * @param eliteCount The number of candidates preserved via elitism..  In elitism, a
     * sub-set of the population with the best fitness scores are preserved unchanged in
     * the subsequent generation.  Candidate solutions that are preserved unchanged through
     * elitism remain eligible for selection for breeding the remainder of the next generation.
     * This value must be non-negative and less than the population size.  A value of zero
     * means that no elitism will be applied.
     * @param generationCount The number of iterations to perform (including the
     * creation and evaluation of the initial population, which counts as the first
     * generation).
     * @return The fittest solution found by the evolutionary process.
     * @see #evolve(int,int,double,long)
     */
    T evolve(int populationSize,
             int eliteCount,
             int generationCount);


    /**
     * @param populationSize The number of candidate solutions present in the population
     * at any point in time.
     * @param eliteCount The number of candidates preserved via elitism..  In elitism, a
     * sub-set of the population with the best fitness scores are preserved unchanged in
     * the subsequent generation.  Candidate solutions that are preserved unchanged through
     * elitism remain eligible for selection for breeding the remainder of the next generation.
     * This value must be non-negative and less than the population size.  A value of zero
     * means that no elitism will be applied.
     * @param generationCount The number of iterations to perform (including the
     * creation and evaluation of the initial population, which counts as the first
     * generation).
     * @param seedCandidates A set of candidates to seed the population with.  The size of
     * this collection must be no greater than the specified population size.
     * @return The fittest solution found by the evolutionary process.
     * @see #evolve(int,int,int)
     * @see #evolve(int,int,double,long,Collection)
     */
    T evolve(int populationSize,
             int eliteCount,
             int generationCount,
             Collection<T> seedCandidates);


    /**
     * Runs the evolution until a target fitness score has been achieved by at least
     * one candidate solution.  To prevent this method from executing indefinitely,
     * a timeout is also specified.
     * @param populationSize The number of candidate solutions present in the population
     * at any point in time.
     * @param eliteCount The number of candidates preserved via elitism..  In elitism, a
     * sub-set of the population with the best fitness scores are preserved unchanged in
     * the subsequent generation.  Candidate solutions that are preserved unchanged through
     * elitism remain eligible for selection for breeding the remainder of the next generation.
     * This value must be non-negative and less than the population size.  A value of zero
     * means that no elitism will be applied.
     * @param targetFitness The minimum satisfactory fitness score.  The evolution will
     * continue until a candidate is found with an equal or higher score, or the
     * execution times out.
     * @param timeout How long (in milliseconds) the evolution is allowed to run for
     * without finding a matching candidate.
     * @return An evolved candidate that has a fitness score greater than or equal to
     * {@code targetFitness}.
     * @see #evolve(int,int,int)
     * @see #evolve(int,int,double,long,Collection)
     */
    T evolve(int populationSize,
             int eliteCount,
             double targetFitness,
             long timeout);


    /**
     * Runs the evolution until a target fitness score has been achieved by at least
     * one candidate solution.  To prevent this method from executing indefinitely,
     * a timeout is also specified.
     * @param populationSize The number of candidate solutions present in the population
     * at any point in time.
     * @param eliteCount The number of candidates preserved via elitism..  In elitism, a
     * sub-set of the population with the best fitness scores are preserved unchanged in
     * the subsequent generation.  Candidate solutions that are preserved unchanged through
     * elitism remain eligible for selection for breeding the remainder of the next generation.
     * This value must be non-negative and less than the population size.  A value of zero
     * means that no elitism will be applied.
     * @param targetFitness The minimum satisfactory fitness score.  The evolution will
     * continue until a candidate is found with an equal or higher score, or the
     * execution times out.
     * @param timeout How long (in milliseconds) the evolution is allowed to run for
     * without finding a matching candidate.
     * @param seedCandidates A set of candidates to seed the population with.  The size of
     * this collection must be no greater than the specified population size.
     * @return An evolved candidate that has a fitness score greater than or equal to
     * {@code targetFitness}.
     * @see #evolve(int,int,int)
     */
    T evolve(int populationSize,
             int eliteCount,
             double targetFitness,
             long timeout,
             Collection<T> seedCandidates);


    /**
     * Adds a listener to receive status updates on the evolution progress.
     * @param observer An evolution observer call-back.
     * @see #removeEvolutionObserver(EvolutionObserver)
     */
    void addEvolutionObserver(EvolutionObserver<T> observer);


    /**
     * Removes an evolution progress listener.
     * @param observer An evolution observer call-back.
     * @see #addEvolutionObserver(EvolutionObserver)
     */
    void removeEvolutionObserver(EvolutionObserver<T> observer);
}
