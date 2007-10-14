// ============================================================================
//   Copyright 2006, 2007 Daniel W. Dyer
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
import org.uncommons.util.binary.BitString;
import org.uncommons.watchmaker.framework.EvolutionaryOperator;

/**
 * Unit test for mutation of bit strings.
 * @author Daniel Dyer
 */
public class BitStringMutationTest
{
    private final Random rng = new MersenneTwisterRNG();

    /**
     * Ensures that mutation occurs correctly.  Because of the random
     * aspect we can't actually make many assertions.  This just ensures
     * that there are no unexpected exceptions and that the length of
     * the bit strings remains as expected.
     */
    @Test
    public void testRandomMutation()
    {
        EvolutionaryOperator<BitString> mutation = new BitStringMutation(0.5d);
        BitString original = new BitString("111100101");
        List<BitString> population = Arrays.asList(original);
        for (int i = 0; i < 20; i++) // Perform several iterations to get different mutations.
        {
            population = mutation.apply(population, rng);
            BitString mutated = population.get(0);
            assert mutated.getLength() == 9 : "Mutated bit string changed length.";
        }
    }


    /**
     * Ensures that mutation occurs correctly.  Uses a probability of 1 to
     * make the outcome predictable (all bits will be flipped).
     */
    @Test
    public void testMutateAllBits()
    {
        EvolutionaryOperator<BitString> mutation = new BitStringMutation(1d);
        BitString original = new BitString("111100101");
        List<BitString> population = Arrays.asList(original);
        population = mutation.apply(population, rng);
        BitString mutated = population.get(0);
        assert !mutated.equals(original) : "Mutation should be different from original.";
        assert mutated.getLength() == 9 : "Mutated bit string changed length.";
        int set = mutated.countSetBits();
        int unset = mutated.countUnsetBits();
        assert set == 3 : "Mutated bit string has wrong number of 1s: " + set;
        assert unset == 6 : "Mutated bit string has wrong number of 0s: " + unset;
        assert mutated.toString().equals("000011010") : "Wrong bits set.";
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
        new BitStringMutation(0d);
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
        new BitStringMutation(1.001d);
    }
}
