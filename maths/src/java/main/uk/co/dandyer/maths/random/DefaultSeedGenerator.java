package uk.co.dandyer.maths.random;

/**
 * Seed generator that maintains multiple strategies for seed
 * generation and will delegate to the best one available for the
 * current operating environment.
 * @author Daniel Dyer
 */
final class DefaultSeedGenerator implements SeedGenerator
{
    /** Singleton instance. */
    private static final DefaultSeedGenerator INSTANCE = new DefaultSeedGenerator();

    /** Delegate generators. */
    private static final SeedGenerator[] GENERATORS = new SeedGenerator[]
    {
        new DevRandomSeedGenerator(),
        new RandomDotOrgSeedGenerator(),
        new SecureRandomSeedGenerator()
    };


    public static DefaultSeedGenerator getInstance()
    {
        return INSTANCE;
    }


    private DefaultSeedGenerator()
    {
        // Private constructor prevents external instantiation.
    }


    /**
     * Generates a seed by trying each of the available strategies in
     * turn until one succeeds.  Tries the most suitable strategy first
     * and eventually degrades to the least suitable but guaranteed to
     * work strategy.
     */
    public byte[] generateSeed(int length)
    {
        for (SeedGenerator generator : GENERATORS)
        {
            try
            {
                return generator.generateSeed(length);
            }
            catch (SeedException ex)
            {
                // Ignore and try the next generator.
            }
        }
        // This shouldn't happen as at least one the generators should be
        // able to generate a seed.
        throw new IllegalStateException("All available seed generation strategies failed.");
    }
}
