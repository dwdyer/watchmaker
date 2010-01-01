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

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import org.uncommons.maths.statistics.DataSet;

/**
 * Utility methods used by different evolution implementations.  This class exists to
 * avoid duplication of this logic among multiple evolution implementations.
 * @author Daniel Dyer
 */
public final class EvolutionUtils
{
    private EvolutionUtils()
    {
        // Prevents instantiation of utility class.
    }


    /**
     * Given data about the current population and a set of termination conditions, determines
     * whether or not the evolution should continue.
     * @param data The current state of the population.
     * @param conditions One or more termination conditions.  The evolution should not continue if
     * any of these is satisfied.
     * @param <T> The type of entity that is being evolved.
     * @return A list of satisfied termination conditions if the evolution has reached some
     * pre-specified state, an empty list if the evolution should stop because of a thread
     * interruption, or null if the evolution should continue.
     */
    public static <T> List<TerminationCondition> shouldContinue(PopulationData<T> data,
                                                                TerminationCondition... conditions)
    {
        // If the thread has been interrupted, we should abort and return whatever
        // result we currently have.
        if (Thread.currentThread().isInterrupted())
        {
            return Collections.emptyList();
        }
        // Otherwise check the termination conditions for the evolution.
        List<TerminationCondition> satisfiedConditions = new LinkedList<TerminationCondition>();
        for (TerminationCondition condition : conditions)
        {
            if (condition.shouldTerminate(data))
            {
                satisfiedConditions.add(condition);
            }
        }
        return satisfiedConditions.isEmpty() ? null : satisfiedConditions;
    }


    /**
     * Sorts an evaluated population in descending order of fitness
     * (descending order of fitness score for natural scores, ascending
     * order of scores for non-natural scores).
     *
     * @param evaluatedPopulation The population to be sorted (in-place).
     * @param naturalFitness True if higher fitness scores mean fitter individuals, false otherwise.
     * @param <T> The type of entity that is being evolved.
     */
    public static <T> void sortEvaluatedPopulation(List<EvaluatedCandidate<T>> evaluatedPopulation,
                                                   boolean naturalFitness)
    {
        // Sort candidates in descending order according to fitness.
        if (naturalFitness) // Descending values for natural fitness.
        {
            Collections.sort(evaluatedPopulation, Collections.reverseOrder());
        }
        else // Ascending values for non-natural fitness.
        {
            Collections.sort(evaluatedPopulation);
        }
    }



    /**
     * Gets data about the current population, including the fittest candidate
     * and statistics about the population as a whole.
     *
     * @param evaluatedPopulation Population of candidate solutions with their
     * associated fitness scores.
     * @param naturalFitness True if higher fitness scores mean fitter individuals, false otherwise.
     * @param eliteCount The number of candidates preserved via elitism.
     * @param iterationNumber The zero-based index of the current generation/epoch.
     * @param startTime The time at which the evolution began, expressed as a number of milliseconds since
     * 00:00 on 1st January 1970.
     * @param <T> The type of entity that is being evolved.
     * @return Statistics about the current generation of evolved individuals.
     */
    public static <T> PopulationData<T> getPopulationData(List<EvaluatedCandidate<T>> evaluatedPopulation,
                                                          boolean naturalFitness,
                                                          int eliteCount,
                                                          int iterationNumber,
                                                          long startTime)
    {
        DataSet stats = new DataSet(evaluatedPopulation.size());
        for (EvaluatedCandidate<T> candidate : evaluatedPopulation)
        {
            stats.addValue(candidate.getFitness());
        }
        return new PopulationData<T>(evaluatedPopulation.get(0).getCandidate(),
                                     evaluatedPopulation.get(0).getFitness(),
                                     stats.getArithmeticMean(),
                                     stats.getStandardDeviation(),
                                     naturalFitness,
                                     stats.getSize(),
                                     eliteCount,
                                     iterationNumber,
                                     System.currentTimeMillis() - startTime);
    }
}
