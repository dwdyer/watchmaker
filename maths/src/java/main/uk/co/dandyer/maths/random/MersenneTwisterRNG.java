// ============================================================================
//   Copyright 2006 Daniel W. Dyer
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
package uk.co.dandyer.maths.random;

import java.util.Random;

/**
 * <p>Random number generator based on the Mersenne Twister algorithm developed
 * by Makoto Matsumoto and Takuji Nishimura
 * (<a href="http://www.math.sci.hiroshima-u.ac.jp/~m-mat/MT/emt.html"
 * >http://www.math.sci.hiroshima-u.ac.jp/~m-mat/MT/emt.html</a>).</p>
 *
 * <p>This is a very fast random number generator with good statistical
 * properties (it passes the full DIEHARD suite).  This is the best RNG
 * for most experiments.  If a non-linear generator is required, use
 * the slower {@link AESCounterRNG} RNG.</p>
 *
 * <p>This PRNG is deterministic, which can be advantageous for testing purposes
 * since the output is repeatable.  If multiple instances of this class are created
 * with the same seed they will all have identical output.</p>
 *
 * <p>This code is translated from the original C version and assumes that we
 * will always seed from an array of bytes.  I don't pretend to know the
 * meanings of the magic numbers or how it works, it just does.</p>
 *
 * @author Daniel Dyer
 */
public class MersenneTwisterRNG extends Random implements RepeatableRNG
{
    // The actual seed size isn't that important, but it should be a multiple of 4.
    private static final int SEED_SIZE_BYTES = 16;

    // Magic numbers from original C version.
    private static final int N = 624;
    private static final int M = 397;
    private static final int[] MAG01 = {0, 0x9908b0df};
    private static final int UPPER_MASK = 0x80000000;
    private static final int LOWER_MASK = 0x7fffffff;
    private static final int BOOTSTRAP_SEED = 19650218;
    private static final int BOOTSTRAP_FACTOR = 1812433253;
    private static final int SEED_FACTOR1 = 1664525;
    private static final int SEED_FACTOR2 = 1566083941;
    private static final int GENERATE_MASK1 = 0x9d2c5680;
    private static final int GENERATE_MASK2 = 0xefc60000;

    private final byte[] seed;

    private final int[] mt = new int[N]; // State vector.
    private int mt_index = 0; // Index into state vector.


    public MersenneTwisterRNG()
    {
        this(DefaultSeedGenerator.getInstance().generateSeed(SEED_SIZE_BYTES));
    }


    /**
     * Seed the RNG using the provided seed generation strategy.
     * @param seedGenerator The seed generation strategy that will provide
     * the seed value for this RNG.
     * @throws SeedException If there is a problem generating a seed.
     */
    public MersenneTwisterRNG(SeedGenerator seedGenerator) throws SeedException
    {
        this(seedGenerator.generateSeed(SEED_SIZE_BYTES));
    }



    public MersenneTwisterRNG(byte[] seed)
    {
        this.seed = seed;
        int[] seedInts = convertBytesToInts(seed);

        // This section is translated from the init_genrand code in the C version.
        mt[0] = BOOTSTRAP_SEED;
        for (mt_index = 1; mt_index < N; mt_index++)
        {
            mt[mt_index] = (BOOTSTRAP_FACTOR
                    * (mt[mt_index - 1] ^ (mt[mt_index - 1] >>> 30))
                    + mt_index);
        }

        // This section is translated from the init_by_array code in the C version.
        int i = 1;
        int j = 0;
        for (int k = Math.max(N, seedInts.length); k > 0; k--)
        {
            mt[i] = (mt[i] ^ ((mt[i - 1] ^ (mt[i - 1] >>> 30)) * SEED_FACTOR1)) + seedInts[j] + j;
            i++;
            j++;
            if (i >= N)
            {
                mt[0] = mt[N - 1];
                i = 1;
            }
            if (j >= seedInts.length)
            {
                j = 0;
            }
        }
        for (int k = N-1; k > 0; k--)
        {
            mt[i] = (mt[i] ^ ((mt[i - 1] ^ (mt[i - 1] >>> 30)) * SEED_FACTOR2)) - i;
            i++;
            if (i >= N)
            {
                mt[0] = mt[N - 1];
                i = 1;
            }
        }
        mt[0] = UPPER_MASK; // Most significant bit is 1 - guarantees non-zero initial array.
    }


    public byte[] getSeed()
    {
        return seed;
    }


    @Override
    protected final synchronized int next(int bits)
    {
        int y;
        if (mt_index >= N) // Generate N ints at a time.
        {
            int kk;
            for (kk = 0; kk < N - M; kk++)
            {
                y = (mt[kk] & UPPER_MASK) | (mt[kk + 1] & LOWER_MASK);
                mt[kk] = mt[kk + M] ^ (y >>> 1) ^ MAG01[y & 0x1];
            }
            for (;kk < N - 1; kk++)
            {
                y = (mt[kk] & UPPER_MASK) | (mt[kk + 1] & LOWER_MASK);
                mt[kk] = mt[kk + (M - N)] ^ (y >>> 1) ^ MAG01[y & 0x1];
            }
            y = (mt[N - 1] & UPPER_MASK) | (mt[0] & LOWER_MASK);
            mt[N - 1] = mt[M - 1] ^ (y >>> 1) ^ MAG01[y & 0x1];

            mt_index = 0;
        }

        y = mt[mt_index++];

        // Tempering
        y ^= (y >>> 11);
        y ^= (y << 7) & GENERATE_MASK1;
        y ^= (y << 15) & GENERATE_MASK2;
        y ^= (y >>> 18);

        return y >>> (32 - bits);
    }


    /**
     * Helper method to convert an array of bytes to an array of ints.
     */
    private int[] convertBytesToInts(byte[] bytes)
    {
        assert bytes.length % 4 == 0 : "Number of seed bytes must be multiple of 4.";
        int[] ints = new int[bytes.length / 4];
        for (int i = 0; i < bytes.length; i += 4)
        {
            ints[i / 4] = (bytes[i] << 24) + (bytes[i + 1] << 16) + (bytes[i + 2] << 8) + bytes[i + 3]; 
        }
        return ints;
    }
}
