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
