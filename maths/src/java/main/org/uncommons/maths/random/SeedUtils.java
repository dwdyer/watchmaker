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

/**
 * Utility methods for working with seeds for random number generators.
 * @author Daniel Dyer
 */
public class SeedUtils
{
    private static final char[] HEX_CHARS = new char[]{'0', '1', '2', '3',
                                                       '4', '5', '6', '7',
                                                       '8', '9', 'A', 'B',
                                                       'C', 'D', 'E', 'F'};

    private SeedUtils()
    {
        // Prevents instantiation of utility class.
    }


    public static String convertSeedDataToHexString(byte[] seed)
    {
        StringBuilder buffer = new StringBuilder(seed.length * 2);
        for (int i = 0; i < seed.length; i++)
        {
            buffer.append(HEX_CHARS[(seed[i] >>> 4) & 0x0F]);
            buffer.append(HEX_CHARS[seed[i] & 0x0F]);
        }
        return buffer.toString();
    }


    public static byte[] convertHexStringToSeedData(String hex)
    {
        if (hex.length() % 2 != 0)
        {
            throw new IllegalArgumentException("Hex string must have even number of characters.");
        }
        byte[] seed = new byte[hex.length() / 2];
        for (int i = 0; i < seed.length; i++)
        {
            int index = i * 2;
            seed[i] = (byte) Integer.parseInt(hex.substring(index, index + 2), 16);
        }
        return seed;
    }
}
