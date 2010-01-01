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

import java.util.Arrays;
import java.util.List;
import org.testng.annotations.Test;
import org.uncommons.watchmaker.framework.CandidateFactory;
import org.uncommons.watchmaker.framework.FrameworkTestUtils;

/**
 * @author Daniel Dyer
 */
public class StringFactoryTest
{
    private final int candidateLength = 8;
    private final int populationSize = 5;

    private final char[] alphabet = {'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j'};


    /**
     * Generate a completely random population.  Checks to make
     * sure that the correct number of candidate solutions is
     * generated and that each is valid.
     */
    @Test
    public void testUnseededPopulation()
    {
        CandidateFactory<String> factory = new StringFactory(alphabet, candidateLength);
        List<String> population = factory.generateInitialPopulation(populationSize, FrameworkTestUtils.getRNG());
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
        CandidateFactory<String> factory = new StringFactory(alphabet, candidateLength);

        String seed1 = "cdefghij";
        String seed2 = "bbbbbbbb";

        List<String> population = factory.generateInitialPopulation(populationSize,
                                                                    Arrays.asList(seed1, seed2),
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
        CandidateFactory<String> factory = new StringFactory(alphabet, candidateLength);
        // The following call should cause an exception since the 3 seed candidates
        // won't fit into a population of size 2.
        factory.generateInitialPopulation(2,
                                          Arrays.asList("abcdefgh", "ijklmnop", "qrstuvwx"),
                                          FrameworkTestUtils.getRNG());
    }




    /**
     * Make sure each candidate is valid (is the right length and contains only
     * valid characters).
     * @param population The population to be validated.
     */
    private void validatePopulation(List<String> population)
    {
        for (String candidate : population)
        {
            assert candidate.length() == candidateLength : "Wrong length candidate: " + candidate.length();
            for (char c : candidate.toCharArray())
            {
                assert c >= 'a' && c <= 'j' : "Invalid character: " + c;
            }
        }
    }

}
