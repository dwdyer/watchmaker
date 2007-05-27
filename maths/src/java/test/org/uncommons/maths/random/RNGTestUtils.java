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

import java.util.Arrays;
import java.util.Random;

/**
 * Provides methods used for testing the operation of RNG implementations.
 * @author Daniel Dyer
 */
final class RNGTestUtils
{
    private RNGTestUtils()
    {
        // Prevents instantiation of utility class.
    }

    /**
     * Test to ensure that two distinct RNGs with the same seed return the
     * same sequence of numbers.
     * @return true if the two RNGs produce the same sequence of values, false
     * otherwise.
     */
    public static boolean testEquivalence(Random rng1,
                                          Random rng2)
    {
        int[] originalInts = new int[1000];
        double[] originalDoubles = new double[1000];
        for (int i = 0; i < 1000; i++)
        {
            originalInts[i] = rng1.nextInt();
            originalDoubles[i] = rng1.nextDouble();
        }

        int[] repeatedInts = new int[1000];
        double[] repeatedDoubles = new double[1000];
        for (int i = 0; i < 1000; i++)
        {
            repeatedInts[i] = rng2.nextInt();
            repeatedDoubles[i] = rng2.nextDouble();
        }

        return Arrays.equals(originalInts, repeatedInts)
               && Arrays.equals(originalDoubles, repeatedDoubles);
    }


    /**
     * This is a rudimentary check to ensure that the output of a given RNG
     * is approximately uniformly distributed.  If the RNG output is not
     * uniformly distributed, this method will return a poor estimate for the
     * value of pi.
     * @param rng The RNG to test.
     * @return An approximation of pi generated using the provided RNG.
     */
    public static double calculateMonteCarloValueForPi(Random rng)
    {
        int iterations = 50000;
        int totalInsideQuadrant = 0;
        for (int i = 0; i < iterations; i++)
        {
            double x = rng.nextDouble();
            double y = rng.nextDouble();
            if (isInQuadrant(x, y))
            {
                ++totalInsideQuadrant;
            }
        }
        return 4 * ((double) totalInsideQuadrant / iterations);
    }


    private static boolean isInQuadrant(double x, double y)
    {
        double distance = Math.sqrt((x * x) + (y * y));
        return distance <= 1;
    }
}
