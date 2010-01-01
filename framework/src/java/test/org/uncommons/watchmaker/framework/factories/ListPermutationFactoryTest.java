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
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import org.testng.annotations.Test;
import org.uncommons.watchmaker.framework.CandidateFactory;
import org.uncommons.watchmaker.framework.FrameworkTestUtils;

/**
 * Unit test for the list permutation candidate factory.  Checks that it
 * correctly generates populations of permutations.
 * @author Daniel Dyer
 */
public class ListPermutationFactoryTest
{
    private final int candidateLength = 10;
    private final int populationSize = 5;
    private final List<Integer> elements = new ArrayList<Integer>(candidateLength);
    {
        for (int i = 1; i <= candidateLength; i++)
        {
            elements.add(i);
        }
    }


    /**
     * Generate a completely random population.  Checks to make
     * sure that the correct number of candidate solutions is
     * generated and that each is valid.
     */
    @Test
    public void testUnseededPopulation()
    {
        CandidateFactory<List<Integer>> factory = new ListPermutationFactory<Integer>(elements);
        List<List<Integer>> population = factory.generateInitialPopulation(populationSize, FrameworkTestUtils.getRNG());
        assert population.size() == populationSize : "Wrong size population generated: " + population.size();

        validatePopulation(population);
    }


    /**
     * Generate a random population with some seed candidates.  Checks to make
     * sure that the correct number of candidate solutions is generated and that
     * each is valid.
     */
    @Test
    public void testSeededPopulation()
    {
        CandidateFactory<List<Integer>> factory = new ListPermutationFactory<Integer>(elements);

        List<Integer> seed1 = new ArrayList<Integer>(elements);
        List<Integer> seed2 = new ArrayList<Integer>(elements);
        Collections.reverse(elements);
        List<List<Integer>> seeds = new ArrayList<List<Integer>>(2);
        seeds.add(seed1);
        seeds.add(seed2);

        List<List<Integer>> population = factory.generateInitialPopulation(populationSize,
                                                                           seeds,
                                                                           FrameworkTestUtils.getRNG());
        // Check that the seed candidates appear in the generated population.
        assert population.contains(seed1) : "Population does not contain seed candidate 1.";
        assert population.contains(seed2) : "Population does not contain seed candidate 2.";

        validatePopulation(population);
    }


    /**
     * It is an error if the number of seed candidates is greater than the
     * population size.  In this case an exception should be thrown.  Not
     * throwing an exception is wrong because it would permit undetected bugs
     * in programs that use the factory.
     */
    @Test(expectedExceptions = IllegalArgumentException.class)
    public void testTooManySeedCandidates()
    {
        CandidateFactory<List<Integer>> factory = new ListPermutationFactory<Integer>(elements);

        List<List<Integer>> seeds = new LinkedList<List<Integer>>();
        seeds.add(elements);
        seeds.add(elements);
        seeds.add(elements);
        // The following call should cause an exception since the 3 seed candidates
        // won't fit into a population of size 2.
        factory.generateInitialPopulation(2, seeds, FrameworkTestUtils.getRNG());
    }


    /**
     * Make sure each candidate is valid (contains each element exactly once).
     * @param population The population to be validated.
     */
    private void validatePopulation(List<List<Integer>> population)
    {
        assert population.size() == populationSize : "Wrong size population generated: " + population.size();
        for (List<Integer> candidate : population)
        {
            assert candidate.size() == candidateLength : "Wrong size candidate generated: " + candidate.size();
            for (int i = 1; i < candidateLength; i++)
            {
                assert candidate.contains(i) : "Candidate is missing element " + i + ".";
            }
        }
    }
}
