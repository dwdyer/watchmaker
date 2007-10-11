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

import java.util.Random;
import org.uncommons.maths.stats.SampleDataSet;

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
     * @param rng1 The first RNG.  Its output is compared to that of {@code rng2}.
     * @param rng2 The second RNG.  Its output is compared to that of {@code rng1}.
     * @param iterations The number of values to generate from each RNG and
     * compare.
     * @return true if the two RNGs produce the same sequence of values, false
     * otherwise.
     */
    public static boolean testEquivalence(Random rng1,
                                          Random rng2,
                                          int iterations)
    {
        for (int i = 0; i < iterations; i++)
        {
            if (rng1.nextInt() != rng2.nextInt())
            {
                return false;
            }
        }
        return true;
    }


    /**
     * This is a rudimentary check to ensure that the output of a given RNG
     * is approximately uniformly distributed.  If the RNG output is not
     * uniformly distributed, this method will return a poor estimate for the
     * value of pi.
     * @param rng The RNG to test.
     * @param iterations The number of random points to generate for use in the
     * calculation.  This value needs to be sufficiently large in order to
     * produce a reasonably accurate result (assuming the RNG is uniform).
     * Less than 10,000 is not particularly useful.  100,000 should be sufficient.
     * @return An approximation of pi generated using the provided RNG.
     */
    public static double calculateMonteCarloValueForPi(Random rng,
                                                       int iterations)
    {
        // Assumes a quadrant of a circle of radius 1, bounded by a box with
        // sides of length 1.  The area of the square is therefore 1 square unit
        // and the area of the quadrant is (pi * r^2) / 4.
        int totalInsideQuadrant = 0;
        // Generate the specified number of random points and count how many fall
        // within the quadrant and how many do not.  We expect the number of points
        // in the quadrant (expressed as a fraction of the total number of points)
        // to be pi/4.  Therefore pi = 4 * ratio.
        for (int i = 0; i < iterations; i++)
        {
            double x = rng.nextDouble();
            double y = rng.nextDouble();
            if (isInQuadrant(x, y))
            {
                ++totalInsideQuadrant;
            }
        }
        // From these figures we can deduce an approximate value for Pi.
        return 4 * ((double) totalInsideQuadrant / iterations);
    }


    /**
     * Uses Pythagoras' theorem to determine whether the specified coordinates
     * fall within the area of the quadrant of a circle of radius 1 that is
     * centered on the origin.
     * @param x The x-coordinate of the point (must be between 0 and 1).
     * @param y The y-coordinate of the point (must be between 0 and 1).
     * @return True if the point is within the quadrant, false otherwise.
     */
    private static boolean isInQuadrant(double x, double y)
    {
        double distance = Math.sqrt((x * x) + (y * y));
        return distance <= 1;
    }


    /**
     * Generates a sequence of values from a given random number generator and
     * then calculates the standard deviation of the sample.
     * @param rng The RNG to use.
     * @param maxValue The maximum value for generated integers (values will be
     * in the range [0, maxValue)).
     * @param iterations The number of values to generate and use in the standard
     * deviation calculation.
     * @return The standard deviation of the generated sample.
     */
    public static double calculateSampleStandardDeviation(Random rng,
                                                          int maxValue,
                                                          int iterations)
    {
        SampleDataSet dataSet = new SampleDataSet(iterations);
        for (int i = 0; i < iterations; i++)
        {
            dataSet.addValue(rng.nextInt(maxValue));
        }
        return dataSet.getStandardDeviation();
    }
}
