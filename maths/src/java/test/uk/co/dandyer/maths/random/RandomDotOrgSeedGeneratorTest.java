package uk.co.dandyer.maths.random;

import org.testng.annotations.Test;

/**
 * Unit test for the seed generator that connects to random.org to get seed
 * data.
 * @author Daniel Dyer
 */
public class RandomDotOrgSeedGeneratorTest
{
    @Test
    public void testGenerator() throws SeedException
    {
        SeedGenerator generator = new RandomDotOrgSeedGenerator();
        byte[] seed = generator.generateSeed(32);
        assert seed.length == 32 : "Failed to generate seed of correct length";
    }
}
