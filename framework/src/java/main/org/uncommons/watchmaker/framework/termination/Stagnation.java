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
package org.uncommons.watchmaker.framework.termination;

import org.uncommons.watchmaker.framework.PopulationData;
import org.uncommons.watchmaker.framework.TerminationCondition;

/**
 * A {@link TerminationCondition} that halts evolution if no improvement in fitness
 * is observed within a specified number of generations.
 * @author Daniel Dyer
 */
public class Stagnation implements TerminationCondition
{
    private final int generationLimit;
    private final boolean naturalFitness;
    private final boolean usePopulationAverage;

    private double bestFitness;
    private int fittestGeneration;

    /**
     * Creates a {@link TerminationCondition} that will halt evolution after the
     * specified number of generations passes without any improvement in the population's
     * fittest individual.
     * @param generationLimit The number of generations without improvement that
     * will lead to termination.
     * @param naturalFitness True if higher fitness scores are better, false otherwise.
     */
    public Stagnation(int generationLimit,
                      boolean naturalFitness)
    {
        this(generationLimit, naturalFitness, false);
    }


    /**
     * Creates a {@link TerminationCondition} that will halt evolution after the
     * specified number of generations passes without any improvement in the population's
     * fitness (either the fittest individual or the mean fitness of the entire population,
     * depending on the final parameter).
     * @param generationLimit The number of generations without improvement that
     * will lead to termination.
     * @param naturalFitness True if higher fitness scores are better, false otherwise.
     * @param usePopulationAverage If true uses the mean fitness of the population as the
     * criteria, otherwise uses the fittest individual.
     */
    public Stagnation(int generationLimit,
                      boolean naturalFitness,
                      boolean usePopulationAverage)
    {
        this.generationLimit = generationLimit;
        this.naturalFitness = naturalFitness;
        this.usePopulationAverage = usePopulationAverage;
    }


    /**
     * {@inheritDoc}
     */
    public boolean shouldTerminate(PopulationData<?> populationData)
    {
        double fitness = getFitness(populationData);
        if (populationData.getGenerationNumber() == 0 || hasFitnessImproved(fitness))
        {
            bestFitness = fitness;
            fittestGeneration = populationData.getGenerationNumber();
        }

        return populationData.getGenerationNumber() - fittestGeneration >= generationLimit;
    }


    /**
     * Determines the fitness of the current population (either best fitness or
     * mean fitness depending on how the termination condition is configured).
     * @param populationData Data about the current generation.
     * @return The fitness measure used to decide whether the evolution has stagnated
     * or not.
     */
    private double getFitness(PopulationData<?> populationData)
    {
        return usePopulationAverage
               ? populationData.getMeanFitness()
               : populationData.getBestCandidateFitness();
    }


    /**
     * Determine whether the population fitness is better than the best seen so far.
     * @param fitness The fitness of the current population (either best fitness or mean
     * fitness depending on how the termination condition is configured).
     * @return True if the fitness has improved in the current generation, false otherwise.
     */
    private boolean hasFitnessImproved(double fitness)
    {
        return (naturalFitness && fitness > bestFitness)
            || (!naturalFitness && fitness < bestFitness);
    }
}
