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
package org.uncommons.watchmaker.framework.operators;

import java.util.Arrays;
import java.util.List;
import org.testng.annotations.Test;
import org.uncommons.maths.random.Probability;
import org.uncommons.watchmaker.framework.FrameworkTestUtils;

/**
 * Unit test for string mutation operator.
 * @author Daniel Dyer
 */
public class StringMutationTest
{
    private final char[] alphabet = {'a', 'b', 'c', 'd'};

    @Test
    public void testMutation()
    {        
        StringMutation mutation = new StringMutation(alphabet, Probability.EVENS);
        String individual1 = "abcd";
        String individual2 = "abab";
        String individual3 = "cccc";
        List<String> population = Arrays.asList(individual1, individual2, individual3);
        for (int i = 0; i < 20; i++) // Perform several iterations.
        {
            population = mutation.apply(population, FrameworkTestUtils.getRNG());
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
}
