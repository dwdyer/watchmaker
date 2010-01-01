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
package org.uncommons.watchmaker.framework;

import java.util.Random;
import org.uncommons.maths.random.XORShiftRNG;

/**
 * Utility methods for Watchmaker Framework unit tests.  Provides
 * access to shared resources used by tests.
 * @author Daniel Dyer
 */
public final class FrameworkTestUtils
{
    private static final Random RNG = new XORShiftRNG();

    private FrameworkTestUtils()
    {
        // Prevent instantiation.
    }


    /**
     * Returns the singleton RNG shared by all tests.  It might be preferable
     * to have a separate RNG for each test (for true separation) but this
     * causes problems.  Seeding dozens of RNGs can exhaust the system's
     * available entropy (the Uncommons Maths RNGs seed themselves from
     * /dev/random by default).
     * @return A random number generator.
     */
    public static Random getRNG()
    {
        return RNG;
    }
}
