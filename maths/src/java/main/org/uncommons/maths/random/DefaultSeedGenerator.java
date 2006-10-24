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

/**
 * Seed generator that maintains multiple strategies for seed
 * generation and will delegate to the best one available for the
 * current operating environment.
 * @author Daniel Dyer
 */
public class DefaultSeedGenerator implements SeedGenerator
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
                // Ignore and try the next generator...
            }
            catch (SecurityException ex)
            {
                // Might be thrown if resource access is restricted (such as in
                // an applet sandbox).
                // Ignore and try the next generator...
            }
        }
        // This shouldn't happen as at least one the generators should be
        // able to generate a seed.
        throw new IllegalStateException("All available seed generation strategies failed.");
    }
}
