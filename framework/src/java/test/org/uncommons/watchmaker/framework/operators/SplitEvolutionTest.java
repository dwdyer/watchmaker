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

import java.util.ArrayList;
import java.util.List;
import org.testng.annotations.Test;
import org.uncommons.watchmaker.framework.FrameworkTestUtils;

/**
 * Unit test for forked evolutionary schemes.
 * @author Daniel Dyer
 */
public class SplitEvolutionTest
{
    /**
     * Make sure that the correct proportions are mutated correctly.
     */
    @Test
    public void testSplit()
    {
        List<Integer> population = new ArrayList<Integer>(10);
        for (int i = 10; i <= 100; i += 10)
        {
            population.add(i);
        }
        // Increment 30% of the numbers and decrement the other 70%.
        SplitEvolution<Integer> evolutionScheme = new SplitEvolution<Integer>(new IntegerAdjuster(1),
                                                                              new IntegerAdjuster(-1),
                                                                              0.3d);
        population = evolutionScheme.apply(population, FrameworkTestUtils.getRNG());
        int aggregate = 0;
        int incrementedCount = 0;
        int decrementedCount = 0;
        for (Integer i : population)
        {
            aggregate += i;
            if (i % 10 == 1)
            {
                ++incrementedCount;
            }
            else if (i % 10 == 9)
            {
                ++decrementedCount;
            }
            else
            {
                assert false : "Mutation failed.";
            }
        }
        assert incrementedCount == 3 : "Should be 3 incremented candidates, is " + incrementedCount;
        assert decrementedCount == 7 : "Should be 7 decremented candidates, is " + decrementedCount;
        assert aggregate == 546 : "Aggregate should be 546 after mutation, is " + aggregate;
    }


    /**
     * Make sure that the split cannot be set-up with a negative weight.  If
     * this is attempted, an IllegalArgumentException should be thrown.
     */
    @Test(expectedExceptions = IllegalArgumentException.class)
    public void testNegativeWeight()
    {
        // Negative weight.
        new SplitEvolution<Integer>(new IntegerAdjuster(1), new IdentityOperator<Integer>(), -0.01d);
    }


    /**
     * Make sure that the split cannot be set-up with a weight greater than 1.  If
     * this is attempted, an IllegalArgumentException should be thrown.
     */
    @Test(expectedExceptions = IllegalArgumentException.class)
    public void testWeightTooHigh()
    {
        // Weight too high (must be less than or equal to one).
        new SplitEvolution<Integer>(new IntegerAdjuster(1), new IdentityOperator<Integer>(), 1.01d);
    }
}
