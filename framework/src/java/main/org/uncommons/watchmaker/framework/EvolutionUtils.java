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
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import org.uncommons.maths.statistics.DataSet;

/**
 * Utility methods used by different evolution implementations.  This class exists to
 * avoid duplication of this logic among multiple evolution implementations.
 * @author Daniel Dyer
 */
public class EvolutionUtils
{
    private EvolutionUtils()
    {
        // Prevents instantiation of utility class.
    }


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
     * Convert a list of {@link EvaluatedCandidate}s into a simple list of candidates.
     * @param evaluatedCandidates The population of candidate objects to relieve of their
     * evaluation wrappers.
     * @return The candidates, stripped of their fitness scores.
     */
    public static <T> List<T> toCandidateList(List<EvaluatedCandidate<T>> evaluatedCandidates)
    {
        List<T> candidates = new ArrayList<T>(evaluatedCandidates.size());
        for (EvaluatedCandidate<T> evaluatedCandidate : evaluatedCandidates)
        {
            candidates.add(evaluatedCandidate.getCandidate());
        }
        return candidates;
    }



    /**
     * Gets data about the current population, including the fittest candidate
     * and statistics about the population as a whole.
     *
     * @param evaluatedPopulation Population of candidate solutions with their
     * associated fitness scores.
     * @param eliteCount The number of candidates preserved via elitism.
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