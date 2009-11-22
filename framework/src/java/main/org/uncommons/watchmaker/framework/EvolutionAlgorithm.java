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

import java.util.List;
import java.util.Random;

/**
 * Strategy interface implemented by classes that provide ways of evolving a
 * population of individuals.
 * @param <T> The type of entity that can be evolved by an instance of this interface.
 * @author Daniel Dyer
 */
public interface EvolutionAlgorithm<T>
{
    /**
     * This method performs a single step/iteration of the evolutionary process.
     * @param evaluatedPopulation The population at the beginning of the process.
     * @param eliteCount The number of the fittest individuals that must be preserved.
     * @param rng A source of randomness.
     * @return The updated population after the evolutionary process has proceeded
     * by one step/iteration.
     */
    List<EvaluatedCandidate<T>> evolvePopulation(List<EvaluatedCandidate<T>> evaluatedPopulation,
                                                 int eliteCount,
                                                 Random rng);

    
    /**
     * Takes a population, assigns a fitness score to each member and returns
     * the members with their scores attached, sorted in descending order of
     * fitness (descending order of fitness score for natural scores, ascending
     * order of scores for non-natural scores).
     * @param population The population to evaluate (each candidate is assigned
     * a fitness score).
     * @return The evaluated population (a list of candidates with attached fitness
     * scores).
     */
    List<EvaluatedCandidate<T>> evaluatePopulation(List<T> population);
}
