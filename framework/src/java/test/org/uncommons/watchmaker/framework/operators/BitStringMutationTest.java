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

import org.testng.annotations.Test;
import org.uncommons.watchmaker.framework.types.BitString;
import org.uncommons.watchmaker.framework.EvolutionaryOperator;
import org.uncommons.maths.random.MersenneTwisterRNG;
import java.util.List;
import java.util.Arrays;

/**
 * Unit test for mutation of bit strings.
 * @author Daniel Dyer
 */
public class BitStringMutationTest
{
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
        population = mutation.apply(population, new MersenneTwisterRNG());
        BitString mutated = population.get(0);
        assert !mutated.equals(original) : "Mutation should be different from original.";
        assert mutated.getLength() == 9 : "Mutated bit string changed length.";
        int set = mutated.countSetBits();
        int unset = mutated.countUnsetBits();
        assert set == 3 : "Mutated bit string has wrong number of 1s: " + set;
        assert unset == 6 : "Mutated bit string has wrong number of 0s: " + unset;
        assert mutated.toString().equals("000011010") : "Wrong bits set.";
    }
}
