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
import java.util.Random;

/**
 * Creates new populations of candidates.  For most implementations it
 * will be easiest just to extend {@link org.uncommons.watchmaker.framework.factories.AbstractCandidateFactory} and
 * implement the method to generate a single random candidate.
 * @param <T> The type of evolvable entity created by the factory.
 * @author Daniel Dyer
 */
public interface CandidateFactory<T>
{
    /**
     * Creates an initial population of candidates.  If more control is required
     * over the composition of the initial population, consider the overloaded
     * {@link #generateInitialPopulation(int,Collection,Random)} method.
     * @param populationSize The number of candidates to create.
     * @param rng The random number generator to use when creating the initial
     * candidates.
     * @return An initial population of candidate solutions.
     */
    List<T> generateInitialPopulation(int populationSize,
                                      Random rng);

    /**
     * Sometimes it is desirable to seed the initial population with some
     * known good candidates, or partial solutions, in order to provide some
     * hints for the evolution process.  This method generates an initial
     * population, seeded with some initial candidates.  If the number of seed
     * candidates is less than the required population size, the factory should
     * generate additional candidates to fill the remaining spaces in the
     * population.
     * @param populationSize The size of the initial population.
     * @param seedCandidates Candidates to seed the population with.  Number
     * of candidates must be no bigger than the population size.
     * @param rng The random number generator to use when creating additional
     * candidates to fill the population when the number of seed candidates is
     * insufficient.  This can be null if and only if the number of seed
     * candidates provided is sufficient to fully populate the initial population.
     * @return An initial population of candidate solutions, including the
     * specified seed candidates.
     */
    List<T> generateInitialPopulation(int populationSize,
                                      Collection<T> seedCandidates,
                                      Random rng);

    /**
     * Randomly create a single candidate solution.
     * @param rng The random number generator to use when creating the random
     * candidate.
     * @return A randomly-initialised candidate.
     */
    T generateRandomCandidate(Random rng);
}
