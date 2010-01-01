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

import java.util.Collection;
import java.util.List;

/**
 * Operations for classes that provide an evolution implementation.
 * @param <T> The type of entity evolved by the evolution engine.
 * @author Daniel Dyer
 */
public interface EvolutionEngine<T>
{
    /**
     * Execute the evolutionary algorithm until one of the termination conditions is met,
     * then return the fittest candidate from the final generation.  To return the
     * entire population rather than just the fittest candidate, use the
     * {@link #evolvePopulation(int, int, TerminationCondition[])} method instead.
     * @param populationSize The number of candidate solutions present in the population
     * at any point in time.
     * @param eliteCount The number of candidates preserved via elitism.  In elitism, a
     * sub-set of the population with the best fitness scores are preserved unchanged in
     * the subsequent generation.  Candidate solutions that are preserved unchanged through
     * elitism remain eligible for selection for breeding the remainder of the next generation.
     * This value must be non-negative and less than the population size.  A value of zero
     * means that no elitism will be applied.
     * @param conditions One or more conditions that may cause the evolution to terminate.
     * @return The fittest solution found by the evolutionary process.
     * @see #evolve(int, int, Collection, TerminationCondition[])
     */
    T evolve(int populationSize,
             int eliteCount,
             TerminationCondition... conditions);


    /**
     * Execute the evolutionary algorithm until one of the termination conditions is met,
     * then return the fittest candidate from the final generation.  To return the
     * entire population rather than just the fittest candidate, use the
     * {@link #evolvePopulation(int, int, Collection, TerminationCondition[])}
     * method instead.
     * @param populationSize The number of candidate solutions present in the population
     * at any point in time.
     * @param eliteCount The number of candidates preserved via elitism.  In elitism, a
     * sub-set of the population with the best fitness scores are preserved unchanged in
     * the subsequent generation.  Candidate solutions that are preserved unchanged through
     * elitism remain eligible for selection for breeding the remainder of the next generation.
     * This value must be non-negative and less than the population size.  A value of zero
     * means that no elitism will be applied.
     * @param seedCandidates A set of candidates to seed the population with.  The size of
     * this collection must be no greater than the specified population size.
     * @param conditions One or more conditions that may cause the evolution to terminate.
     * @return The fittest solution found by the evolutionary process.
     * @see #evolve(int,int,TerminationCondition[])
     */
    T evolve(int populationSize,
             int eliteCount,
             Collection<T> seedCandidates,
             TerminationCondition... conditions);


    /**
     * Execute the evolutionary algorithm until one of the termination conditions is met,
     * then return all of the candidates from the final generation.  To return just the
     * fittest candidate rather than the entire population, use the
     * {@link #evolve(int, int, TerminationCondition[])} method instead.
     * @param populationSize The number of candidate solutions present in the population
     * at any point in time.
     * @param eliteCount The number of candidates preserved via elitism.  In elitism, a
     * sub-set of the population with the best fitness scores are preserved unchanged in
     * the subsequent generation.  Candidate solutions that are preserved unchanged through
     * elitism remain eligible for selection for breeding the remainder of the next generation.
     * This value must be non-negative and less than the population size.  A value of zero
     * means that no elitism will be applied.
     * @param conditions One or more conditions that may cause the evolution to terminate.
     * @return The fittest solution found by the evolutionary process.
     * @see #evolve(int, int, Collection, TerminationCondition[])
     * @see #evolvePopulation(int, int, Collection, TerminationCondition[])
     */
    List<EvaluatedCandidate<T>> evolvePopulation(int populationSize,
                                                 int eliteCount,
                                                 TerminationCondition... conditions);


    /**
     * Execute the evolutionary algorithm until one of the termination conditions is met,
     * then return all of the candidates from the final generation.  To return just the
     * fittest candidate rather than the entire population, use the
     * {@link #evolve(int, int, Collection, TerminationCondition[])} method instead.
     * @param populationSize The number of candidate solutions present in the population
     * at any point in time.
     * @param eliteCount The number of candidates preserved via elitism.  In elitism, a
     * sub-set of the population with the best fitness scores are preserved unchanged in
     * the subsequent generation.  Candidate solutions that are preserved unchanged through
     * elitism remain eligible for selection for breeding the remainder of the next generation.
     * This value must be non-negative and less than the population size.  A value of zero
     * means that no elitism will be applied.
     * @param seedCandidates A set of candidates to seed the population with.  The size of
     * this collection must be no greater than the specified population size.
     * @param conditions One or more conditions that may cause the evolution to terminate.
     * @return The fittest solution found by the evolutionary process.
     * @see #evolve(int, int, Collection, TerminationCondition[])
     * @see #evolvePopulation(int, int, Collection, TerminationCondition[])
     */
    List<EvaluatedCandidate<T>> evolvePopulation(int populationSize,
                                                 int eliteCount,
                                                 Collection<T> seedCandidates,
                                                 TerminationCondition... conditions);


    /**
     * Adds a listener to receive status updates on the evolution progress.
     * @param observer An evolution observer call-back.
     * @see #removeEvolutionObserver(EvolutionObserver)
     */
    void addEvolutionObserver(EvolutionObserver<? super T> observer);


    /**
     * Removes an evolution progress listener.
     * @param observer An evolution observer call-back.
     * @see #addEvolutionObserver(EvolutionObserver)
     */
    void removeEvolutionObserver(EvolutionObserver<? super T> observer);


    /**
     * Returns a list of all {@link TerminationCondition}s that are satisfied by the current
     * state of the evolution engine.  Usually this list will contain only one item, but it
     * is possible that mutliple termination conditions will become satisfied at the same
     * time.  In this case the condition objects in the list will be in the same order that
     * they were specified when passed to the engine.
     *
     * If the evolution has not yet terminated (either because it is still in progress or
     * because it hasn't even been started) then an IllegalStateException will be thrown.
     *
     * If the evolution terminated because the request thread was interrupted before any
     * termination conditions were satisfied then this method will return an empty list.
     *
     * @throws IllegalStateException If this method is invoked on an evolution engine before
     * evolution is started or while it is still in progress.
     *
     * @return A list of statisfied conditions.  The list is guaranteed to be non-null.  The
     * list may be empty because it is possible for evolution to terminate without any conditions
     * being matched.  The only situation in which this occurs is when the request thread is
     * interrupted.
     */
    List<TerminationCondition> getSatisfiedTerminationConditions();
}