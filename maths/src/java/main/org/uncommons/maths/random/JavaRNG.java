package org.uncommons.maths.random;

import java.util.Random;

/**
 * <p>This is the default {@link java.util.Random JDK RNG} extended
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


    public JavaRNG(byte[] seed)
    {
        super(convertBytesToLong(seed));
        this.seed = seed;
    }


    public byte[] getSeed()
    {
        return seed;
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
