// ============================================================================
//   Copyright 2007 Daniel W. Dyer
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
package org.uncommons.watchmaker.examples.biomorphs;

import java.util.Random;
import org.uncommons.watchmaker.framework.factories.AbstractCandidateFactory;

/**
 * Candidate factory for creating random biomorphs.
 * @author Daniel Dyer
 */
public class BiomorphFactory extends AbstractCandidateFactory<Biomorph>
{
    protected Biomorph generateRandomCandidate(Random rng)
    {
        int[] genes = new int[9];
        for (int i = 0; i < 8; i++)
        {
            // First 8 genes have values between -5 and 5.
            genes[i] = rng.nextInt(11) - 5;
        }
        // Last genes has a value between 1 and 8.
        genes[8] = rng.nextInt(8) + 1;
        return new Biomorph(genes);
    }
}
