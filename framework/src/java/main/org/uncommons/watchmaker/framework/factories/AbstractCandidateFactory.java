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
package org.uncommons.watchmaker.framework.factories;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import org.uncommons.watchmaker.framework.CandidateFactory;

/**
 * Convenient base class for implementations of
 * {@link org.uncommons.watchmaker.framework.CandidateFactory}.
 * @param <T> The type of entity evolved by this engine.
 * @author Daniel Dyer
 */
public abstract class AbstractCandidateFactory<T> implements CandidateFactory<T>
{
    /**
     * Randomly, create an initial population of candidates.  If some
     * control is required over the composition of the initial population,
     * consider the overloaded {@link #generateInitialPopulation(int,Collection,Random)}
     * method.
     * @param populationSize The number of candidates to randomly create.
     * @param rng The random number generator to use when creating the random
     * candidates.
     * @return A randomly generated initial population of candidate solutions.
     */
    public List<T> generateInitialPopulation(int populationSize, Random rng)
    {
        List<T> population = new ArrayList<T>(populationSize);
        for (int i = 0; i < populationSize; i++)
        {
            population.add(generateRandomCandidate(rng));
        }
        return Collections.unmodifiableList(population);
    }


    /**
     * {@inheritDoc}
     * If the number of seed candidates is less than the required population
     * size, the remainder of the population will be generated randomly via
     * the {@link #generateRandomCandidate(Random)} method.
     */
    public List<T> generateInitialPopulation(int populationSize,
                                             Collection<T> seedCandidates,
                                             Random rng)
    {
        if (seedCandidates.size() > populationSize)
        {
            throw new IllegalArgumentException("Too many seed candidates for specified population size.");
        }
        List<T> population = new ArrayList<T>(populationSize);
        population.addAll(seedCandidates);
        for (int i = seedCandidates.size(); i < populationSize; i++)
        {
            population.add(generateRandomCandidate(rng));
        }
        return Collections.unmodifiableList(population);
    }
}
