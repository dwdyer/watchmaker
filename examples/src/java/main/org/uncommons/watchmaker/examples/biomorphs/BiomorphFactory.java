//=============================================================================
// Copyright 2006-2010 Daniel W. Dyer
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
//     http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.
//=============================================================================
package org.uncommons.watchmaker.examples.biomorphs;

import java.util.Random;
import org.uncommons.watchmaker.framework.factories.AbstractCandidateFactory;

/**
 * Candidate factory for creating random biomorphs.
 * @author Daniel Dyer
 */
public class BiomorphFactory extends AbstractCandidateFactory<Biomorph>
{
    /**
     * Generates a random biomorph by providing a random value for each gene.
     * @param rng The source of randomness used to generate the biomoprh. 
     * @return A randomly-generated biomorph.
     */
    public Biomorph generateRandomCandidate(Random rng)
    {
        int[] genes = new int[Biomorph.GENE_COUNT];
        for (int i = 0; i < Biomorph.GENE_COUNT - 1; i++)
        {
            // First 8 genes have values between -5 and 5.
            genes[i] = rng.nextInt(11) - 5;
        }
        // Last genes ha a value between 1 and 7.
        genes[Biomorph.LENGTH_GENE_INDEX] = rng.nextInt(Biomorph.LENGTH_GENE_MAX) + 1;
        return new Biomorph(genes);
    }
}
