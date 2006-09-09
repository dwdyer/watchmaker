package uk.co.dandyer.maths.random;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

/**
 * RNG seed strategy that gets data from /dev/random on systems that
 * provide it (e.g. Solaris/Linux).
 * @author Daniel Dyer
 */
public class DevRandomSeedGenerator implements SeedGenerator
{
    private static final File DEV_RANDOM = new File("/dev/random");

    public byte[] generateSeed(int length) throws SeedException
    {
        FileInputStream file = null;
        try
        {
            file = new FileInputStream(DEV_RANDOM);
            byte[] randomSeed = new byte[length];
            int count = 0;
            while (count < length && count != -1)
            {
                count += file.read(randomSeed, count, length - count);
            }
            return randomSeed;
        }
        catch (IOException ex)
        {
            throw new SeedException("Failed reading from " + DEV_RANDOM.getName(), ex);
        }
        finally
        {
            if (file != null)
            {
                try
                {
                    file.close();
                }
                catch (IOException ex)
                {
                    // Ignore.
                }
            }
        }
    }
}
