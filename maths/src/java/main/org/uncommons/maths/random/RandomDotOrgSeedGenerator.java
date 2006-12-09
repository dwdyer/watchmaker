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
package org.uncommons.maths.random;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.StreamTokenizer;
import java.net.URL;
import java.net.URLConnection;

/**
 * Connects to the random.org website and downloads a set of random bits
 * to use as seed data.  It is generally better to use the
 * {@link DevRandomSeedGenerator} where possible, as it
 * should be much quicker. This seed generator is most useful on Microsoft Windows
 * and other platforms that do not provide /dev/random.
 * @author Daniel Dyer
 */
public class RandomDotOrgSeedGenerator implements SeedGenerator
{
    private static final int CACHE_SIZE = 1024;
    private static final byte[] CACHE = new byte[CACHE_SIZE];

    /** The URL from which the random bytes are retrieved. */
    private static final String RANDOM_URL = "http://www.random.org/cgi-bin/randbyte?nbytes=" + CACHE_SIZE + "&format=d";
    /** Used to identify the client to the random.org service. */
    private static final String USER_AGENT = RandomDotOrgSeedGenerator.class.getName();

    private static int cacheOffset = CACHE_SIZE;

    public byte[] generateSeed(int length) throws SeedException
    {
        synchronized (CACHE)
        {
            // First make sure there are enough bytes to generate a seed
            // of the requested length.
            if (cacheOffset + length >= CACHE_SIZE)
            {
                try
                {
                    refreshCache();
                }
                catch (IOException ex)
                {
                    throw new SeedException("Failed downloading bytes from random.org", ex);
                }
            }
            byte[] seedData = new byte[length];
            System.arraycopy(CACHE, cacheOffset, seedData, 0, length);
            cacheOffset += length;
            return seedData;
        }
    }


    private void refreshCache() throws IOException
    {
        URL url = new URL(RANDOM_URL);
        URLConnection connection = url.openConnection();
        connection.setRequestProperty("User-Agent", USER_AGENT);

        StreamTokenizer tokenizer = new StreamTokenizer(new InputStreamReader(connection.getInputStream()));

        int	index = -1;
        while(tokenizer.nextToken() != StreamTokenizer.TT_EOF)
        {
            if(tokenizer.ttype != StreamTokenizer.TT_NUMBER)
            {
                throw new IOException("Received invalid data.");
            }
            CACHE[++index] = (byte) tokenizer.nval;
        }
        if (index < CACHE.length - 1)
        {
            throw new IOException("Insufficient data received.");
        }
        cacheOffset = 0;
    }
}
