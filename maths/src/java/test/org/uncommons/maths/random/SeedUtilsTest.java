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
import org.testng.annotations.Test;

/**
 * Unit test for RNG seed utility methods.
 * @author Daniel Dyer
 */
public class SeedUtilsTest
{
    @Test
    public void testSeedToHexString()
    {
        byte[] seed = new byte[] {124, 11, 0, -76, -3, 127, -128, -1};
        String expectedHex = "7C0B00B4FD7F80FF";
        String generatedHex = SeedUtils.convertSeedDataToHexString(seed);
        assert generatedHex.equals(expectedHex) : "Wrong hex string: " + generatedHex;
    }


    @Test
    public void testHexStringToSeed()
    {
        String hex = "7C0B00B4FD7F80FF";
        byte[] expectedSeed = new byte[] {124, 11, 0, -76, -3, 127, -128, -1};
        byte[] generatedSeed = SeedUtils.convertHexStringToSeedData(hex);
        assert Arrays.equals(generatedSeed, expectedSeed) : "Wrong seed: " + Arrays.toString(generatedSeed);
    }
}
