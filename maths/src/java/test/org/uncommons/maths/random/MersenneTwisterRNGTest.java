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

import org.testng.Reporter;
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
    public void testRepeatability()
    {
        // Create an RNG using the default seeding strategy.
        MersenneTwisterRNG rng = new MersenneTwisterRNG();
        // Create second RNG using same seed.
        MersenneTwisterRNG duplicateRNG = new MersenneTwisterRNG(rng.getSeed());
        assert RNGTestUtils.testEquivalence(rng, duplicateRNG) : "Generated sequences do not match.";
    }


    /**
     * Test to ensure that the output from the RNG is broadly as expected.  This will not
     * detect the subtle statistical anomalies that would be picked up by Diehard, but it
     * provides a simple check for major problems with the output.
     */
    @Test
    public void testUniformity()
    {
        MersenneTwisterRNG rng = new MersenneTwisterRNG();
        double pi = RNGTestUtils.calculateMonteCarloValueForPi(rng);
        Reporter.log("Monte Carlo value for Pi: " + pi);
        assert pi > 3.11 && pi < 3.17 : "Monte Carlo value for Pi is outside acceptable range.";
    }
}
