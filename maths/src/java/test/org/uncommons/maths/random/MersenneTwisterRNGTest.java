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
package org.uncommons.maths.random;

import java.security.GeneralSecurityException;
import java.util.Arrays;
import org.testng.annotations.Test;

/**
 * Unit test for the cellular automaton RNG.
 * @author Daniel Dyer
 */
public class MersenneTwisterRNGTest
{
    /**
     * Test to ensure that two distinct RNGs with the same seed return the
     * same sequence of numbers.
     */
    @Test
    public void testRepeatability() throws GeneralSecurityException
    {
        MersenneTwisterRNG rng = new MersenneTwisterRNG(); // Use default seeding strategy.
        byte[] seed = rng.getSeed();

        int[] originalInts = new int[1000];
        double[] originalDoubles = new double[1000];
        for (int i = 0; i < 1000; i++)
        {
            originalInts[i] = rng.nextInt();
            originalDoubles[i] = rng.nextDouble();
        }

        MersenneTwisterRNG duplicateRNG = new MersenneTwisterRNG(seed);
        int[] repeatedInts = new int[1000];
        double[] repeatedDoubles = new double[1000];
        for (int i = 0; i < 1000; i++)
        {
            repeatedInts[i] = duplicateRNG.nextInt();
            repeatedDoubles[i] = duplicateRNG.nextDouble();
        }

        assert Arrays.equals(originalInts, repeatedInts) : "Generated int sequences do not match.";
        assert Arrays.equals(originalDoubles, repeatedDoubles) : "Generated double sequences do not match.";
    }
}
