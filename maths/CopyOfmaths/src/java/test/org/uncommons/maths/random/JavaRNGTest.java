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

import org.testng.annotations.Test;

/**
 * Unit test for the JDK RNG.
 * @author Daniel Dyer
 */
public class JavaRNGTest
{
    /**
     * Test to ensure that two distinct RNGs with the same seed return the
     * same sequence of numbers.
     */
    @Test
    public void testRepeatability()
    {
        // Create an RNG using the default seeding strategy.
        JavaRNG rng = new JavaRNG();
        // Create second RNG using same seed.
        JavaRNG duplicateRNG = new JavaRNG(rng.getSeed());
        assert RNGTestUtils.testEquivalence(rng, duplicateRNG, 1000) : "Generated sequences do not match.";
    }

    // Don't bother testing the distribution of the output for this RNG, it's beyond our control.
}
