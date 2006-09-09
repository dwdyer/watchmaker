package uk.co.dandyer.maths.random;

import java.security.SecureRandom;
import java.util.Random;

/**
 * {@link uk.co.dandyer.maths.random.SeedGenerator} implementation that uses Java's bundled
 * {@link java.security.SecureRandom} RNG to generate random seed data.
 * This is not the ideal seeding strategy because it inflicts the
 * limitations of SecureRandom on the RNG being seeded.  However, it
 * is the only seeding strategy that is guaranteed to work on all
 * platforms and therefore is provided as a fall-back option should
 * none of the other provided {@link uk.co.dandyer.maths.random.SeedGenerator} implementations be
 * useable.
 * @author Daniel Dyer
 */
public class SecureRandomSeedGenerator implements SeedGenerator
{
    private static final Random SOURCE = new SecureRandom();

    public byte[] generateSeed(int length) throws SeedException
    {
        byte[] bytes = new byte[length];
        SOURCE.nextBytes(bytes);
        return bytes;
    }
}
