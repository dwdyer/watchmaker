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
package uk.co.dandyer.watchmaker.framework;

import java.util.Collection;

/**
 * @author Daniel Dyer
 */
public interface EvolutionEngine<T>
{
    /**
     * <p>Returns the ratio of the elite to the total population.  This parameter is used
     * to implement elitism in selection.  In elitism, a proportion of the population with
     * the best fitness scores are preserved unchanged in the subsequent generation.
     * Candidate solutions that are preserved unchanged through elitism remain eligible
     * for selection for breeding the remainder of the next generation.</p>
     *
     * <p>The size of the elite sub-set depends on the elite ratio, which has a value greater
     * than or equal to zero (no elitism) and less than 1 (no evolution).  The default value
     * is zero.</p>
     */
    double getEliteRatio();


    /**
     * <p>Configures the ratio of the elite to the total population.  This parameter is used
     * to implement elitism in selection.  In elitism, a propotion of the population with
     * the best fitness scores are preserved unchanged in the subsequent generation.
     * Candidate solutions that are preserved unchanged through elitism remain eligible
     * for selection for breeding the remainder of the next generation.</p>
     *
     * <p>The size of the elite sub-set depends on the elite ratio, which has a value greater
     * than or equal to zero (no elitism) and less than 1 (no evolution).  The default value
     * is zero.</p>
     */
    void setEliteRatio(double eliteRatio);


    /**
     * @param populationSize The number of candidate solutions present in the population
     * at any point in time.
     * @param generationCount The number of iterations to perform (including the
     * creation and evaluation of the initial population, which counts as the first
     * generation).
     * @return The best solution found by the evolutionary process.
     * @see #evolve(int, double, long)
     */
    T evolve(int populationSize,
             int generationCount);


    /**
     * @param populationSize The number of candidate solutions present in the population
     * at any point in time.
     * @param seedCandidates A set of candidates to seed the population with.  The size of
     * this collection must be no greater than the specified population size.
     * @param generationCount The number of iterations to perform (including the
     * creation and evaluation of the initial population, which counts as the first
     * generation).
     * @return The best solution found by the evolutionary process.
     * @see #evolve(int, int)
     * @see #evolve(int, java.util.Collection, double, long)
     */
    T evolve(int populationSize,
             Collection<T> seedCandidates,
             int generationCount);


    /**
     * Runs the evolution until a target fitness score has been achieved by at least
     * one candidate solution.  To prevent this method from executing indefinitely,
     * a timeout is also specified.
     * @param populationSize The number of candidate solutions present in the population
     * at any point in time.
     * @param targetFitness The minimum satisfactory fitness score.  The evolution will
     * continue until a candidate is found with an equal or higher score, or the
     * execution times out.
     * @param timeout How long (in milliseconds) the evolution is allowed to run for
     * without finding a matching candidate.
     * @see #evolve(int, int)
     * @see #evolve(int, java.util.Collection, double, long)
     */
    T evolve(int populationSize,
             double targetFitness,
             long timeout);


    /**
     * Runs the evolution until a target fitness score has been achieved by at least
     * one candidate solution.  To prevent this method from executing indefinitely,
     * a timeout is also specified.
     * @param populationSize The number of candidate solutions present in the population
     * at any point in time.
     * @param seedCandidates A set of candidates to seed the population with.  The size of
     * this collection must be no greater than the specified population size.
     * @param targetFitness The minimum satisfactory fitness score.  The evolution will
     * continue until a candidate is found with an equal or higher score, or the
     * execution times out.
     * @param timeout How long (in milliseconds) the evolution is allowed to run for
     * without finding a matching candidate.
     * @see #evolve(int, int)
     */
    T evolve(int populationSize,
             Collection<T> seedCandidates,
             double targetFitness,
             long timeout);


    /**
     * Adds a listener to receive status updates on the evolution progress.
     * @see #removeEvolutionObserver(uk.co.dandyer.watchmaker.framework.EvolutionObserver)
     */
    void addEvolutionObserver(EvolutionObserver<? super T> observer);


    /**
     * Removes an evolution progress listener.
     * @see #addEvolutionObserver(uk.co.dandyer.watchmaker.framework.EvolutionObserver)
     */
    void removeEvolutionObserver(EvolutionObserver<? super T> observer);
}
