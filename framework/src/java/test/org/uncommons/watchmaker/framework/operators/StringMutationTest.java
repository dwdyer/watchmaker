// ============================================================================
//   Copyright 2006, 2007, 2008 Daniel W. Dyer
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
package org.uncommons.watchmaker.framework.operators;

import java.util.Arrays;
import java.util.List;
import java.util.Random;
import org.testng.annotations.Test;
import org.uncommons.maths.random.MersenneTwisterRNG;

/**
 * Unit test for string mutation operator.
 * @author Daniel Dyer
 */
public class StringMutationTest
{
    private final char[] alphabet = new char[]{'a', 'b', 'c', 'd'};

    @Test
    public void testMutation()
    {        
        StringMutation mutation = new StringMutation(alphabet, 0.5d);
        String individual1 = "abcd";
        String individual2 = "abab";
        String individual3 = "cccc";
        List<String> population = Arrays.asList(individual1, individual2, individual3);
        Random rng = new MersenneTwisterRNG();
        for (int i = 0; i < 20; i++) // Perform several iterations.
        {
            population = mutation.apply(population, rng);
            assert population.size() == 3 : "Population size changed after mutation: " + population.size();
            for (String individual : population) // Check that each individual is still valid.
            {
                assert individual.length() == 4 : "Individual size changed after mutation: " + individual.length();
                for (char c : individual.toCharArray())
                {
                    assert c >= 'a' && c <= 'd' : "Mutation introduced invalid character: " + c; 
                }
            }
        }
    }


    /**
     * The probability of mutation must be greater than 0 to be useful (less than zero
     * is invalid and equal to zero removes all uncertainty).  This test ensures that an
     * appropriate exception is thrown if the probability is not positive.  Not throwing
     * an exception is an error because it permits undetected bugs in evolutionary
     * programs.
     */
    @Test(expectedExceptions = IllegalArgumentException.class)
    public void testProbabilityTooLow()
    {
        new StringMutation(alphabet, 0d);
    }


    /**
     * The probability of mutation must be less than or equal to 1 to be valid.
     * This test ensures that an appropriate exception is thrown if the probability is
     * greater than or equal to one.  Not throwing an exception is an error because it
     * permits undetected bugs in evolutionary programs.
     */
    @Test(expectedExceptions = IllegalArgumentException.class)
    public void testProbabilityTooHigh()
    {
        new StringMutation(alphabet, 1.001d);
    }

}
