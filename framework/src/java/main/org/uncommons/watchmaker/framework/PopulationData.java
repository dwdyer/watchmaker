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
    private final boolean naturalFitness;
    private final int populationSize;
    private final int eliteCount;
    private final int generationNumber;
    private final long elapsedTime;

    /**
     * @param bestCandidate The fittest candidate present in the population.
     * @param bestCandidateFitness The fitness score for the fittest candidate
     * in the population.
     * @param meanFitness The arithmetic mean of fitness scores for each member
     * of the population.
     * @param fitnessStandardDeviation A measure of the variation in fitness
     * scores.
     * @param naturalFitness True if higher fitness scores are better, false
     * otherwise. 
     * @param populationSize The number of individuals in the population.
     * @param eliteCount The number of candidates preserved via elitism.
     * @param generationNumber The (zero-based) number of the last generation
     * that was processed.
     * @param elapsedTime The number of milliseconds since the start of the
     */
    public PopulationData(T bestCandidate,
                          double bestCandidateFitness,
                          double meanFitness,
                          double fitnessStandardDeviation,
                          boolean naturalFitness,
                          int populationSize,
                          int eliteCount,
                          int generationNumber,
                          long elapsedTime)
    {
        this.bestCandidate = bestCandidate;
        this.bestCandidateFitness = bestCandidateFitness;
        this.meanFitness = meanFitness;
        this.fitnessStandardDeviation = fitnessStandardDeviation;
        this.naturalFitness = naturalFitness;
        this.populationSize = populationSize;
        this.eliteCount = eliteCount;
        this.generationNumber = generationNumber;
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
     * Indicates whether the fitness scores are natural or non-natural.
     * @return True if higher fitness scores indicate fitter individuals, false
     * otherwise.
     */
    public boolean isNaturalFitness()
    {
        return naturalFitness;
    }

    
    /**
     * @return The number of individuals in the current population.
     */
    public int getPopulationSize()
    {
        return populationSize;
    }


    /**
     * @return The number of candidates preserved via elitism.
     */
    public int getEliteCount()
    {
        return eliteCount;
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
