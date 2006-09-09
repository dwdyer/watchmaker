package uk.co.dandyer.maths.random;

/**
 * Strategy interface for seeding random number generators.
 * @author Daniel Dyer
 */
public interface SeedGenerator
{
    /**
     * Generate a seed value for a random number generator.
     * @param length The length of the seed to generate (in bytes).
     * @return A byte array containing the seed data.
     * @throws SeedException If a seed cannot be generated for any reason.
     */
    byte[] generateSeed(int length) throws SeedException;
}
