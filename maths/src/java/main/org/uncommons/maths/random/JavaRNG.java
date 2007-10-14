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
import org.uncommons.util.binary.BinaryUtils;

/**
 * <p>This is the default {@link Random JDK RNG} extended
 * to implement the {@link RepeatableRNG} interface (for consitency with
 * the other RNGs in this package).</p>
 *
 * <p>The {@link MersenneTwisterRNG} should be used in preference to this
 * class because it is statistically more random and performs slightly
 * better.</p>
 *
 * @author Daniel Dyer
 */
public class JavaRNG extends Random implements RepeatableRNG
{
    private static final int SEED_SIZE_BYTES = 8;

    private final byte[] seed;


    /**
     * Creates a new RNG and seeds it using the default seeding strategy.
     */
    public JavaRNG()
    {
        this(DefaultSeedGenerator.getInstance().generateSeed(SEED_SIZE_BYTES));
    }


    /**
     * Seed the RNG using the provided seed generation strategy.
     * @param seedGenerator The seed generation strategy that will provide
     * the seed value for this RNG.
     * @throws SeedException If there is a problem generating a seed.
     */
    public JavaRNG(SeedGenerator seedGenerator) throws SeedException
    {
        this(seedGenerator.generateSeed(SEED_SIZE_BYTES));
    }


    /**
     * Creates an RNG and seeds it with the specified seed data.
     * @param seed The seed data used to initialise the RNG.
     */
    public JavaRNG(byte[] seed)
    {
        super(convertBytesToLong(seed));
        if (seed == null || seed.length != SEED_SIZE_BYTES)
        {
            throw new IllegalArgumentException("Java RNG requires a 64-bit (8-byte) seed.");
        }
        this.seed = seed.clone();

        // Always log seed so that an indentical RNG can be created later if necessary.
        System.out.println("Standard Java RNG created with seed " + BinaryUtils.convertBytesToHexString(seed));
    }


    /**
     * {@inheritDoc}
     */    
    public byte[] getSeed()
    {
        return seed.clone();
    }


    /**
     * Utility method to convert an array of bytes into a long.  Byte ordered is
     * assumed to be big-endian.
     */
    private static long convertBytesToLong(byte[] bytes)
    {
        if (bytes.length > 8)
        {
            throw new IllegalArgumentException("Number of bytes must be less than or equal to 8.");
        }
        long value = 0;
        for (byte b : bytes)
        {
            value <<= 8;
            value += b;
        }
        return value;

    }
}
