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
package org.uncommons.watchmaker.framework;

/**
 * Immutable data object containing statistics about the state of
 * an evolved population and a reference to the fittest candidate
 * solution in the population.
 * @param <T> The type of evolved entity present in the population
 * that this data describes.
 * @see EvolutionObserver
 * @author Daniel Dyer
 */
public final class PopulationData<T>
{
    private final T bestCandidate;
    private final double bestCandidateFitness;
    private final double meanFitness;
    private final double fitnessStandardDeviation;
    private final int populationSize;
    private final int generationNumber;
    private final long elapsedTime;

    public PopulationData(T bestCandidate,
                          double bestCandidateFitness,
                          double meanFitness,
                          double fitnessStandardDeviation,
                          int populationSize,
                          int generationCount,
                          long elapsedTime)
    {
        this.bestCandidate = bestCandidate;
        this.bestCandidateFitness = bestCandidateFitness;
        this.meanFitness = meanFitness;
        this.fitnessStandardDeviation = fitnessStandardDeviation;
        this.populationSize = populationSize;
        this.generationNumber = generationCount;
        this.elapsedTime = elapsedTime;
    }


    /**
     * @return The fittest candidate present in the population.
     * @see #getBestCandidateFitness()
     */
    public T getBestCandidate()
    {
        return bestCandidate;
    }


    /**
     * @return The fitness score of the fittest candidate.
     * @see #getBestCandidateFitness()
     */
    public double getBestCandidateFitness()
    {
        return bestCandidateFitness;
    }


    /**
     * Returns the average fitness score of population members.
     * @return The arithmetic mean fitness of individual candidates.
     */
    public double getMeanFitness()
    {
        return meanFitness;
    }


    /**
     * Returns a statistical measure of variation in fitness scores within
     * the population. 
     * @return Population standard deviation for fitness scores.
     */
    public double getFitnessStandardDeviation()
    {
        return fitnessStandardDeviation;
    }


    /**
     * @return The number of individuals in the current population.
     */
    public int getPopulationSize()
    {
        return populationSize;
    }


    /**
     * @return The number of this generation (zero-based).
     */
    public int getGenerationNumber()
    {
        return generationNumber;
    }


    /**
     * Returns the amount of time (in milliseconds) since the
     * start of the evolutionary algorithm's execution.
     * @return How long (in milliseconds) the algorithm has been running.
     */
    public long getElapsedTime()
    {
        return elapsedTime;
    }
}
