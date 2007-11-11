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

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

/**
 * RNG seed strategy that gets data from {@literal /dev/random} on systems
 * that provide it (e.g. Solaris/Linux).
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
            while (count < length)
            {
                int bytesRead = file.read(randomSeed, count, length - count);
                if (bytesRead == -1)
                {
                    throw new SeedException("EOF encountered reading random data.");
                }
                count += bytesRead;
            }
            System.out.println(length + " bytes of seed data acquired from /dev/random");
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
