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

import java.util.List;
import org.testng.annotations.Test;
import org.uncommons.watchmaker.examples.ExamplesTestUtils;
import org.uncommons.watchmaker.framework.CandidateFactory;

/**
 * Unit test for the biomorph candidate factory.
 * @author Daniel Dyer
 */
public class BiomorphFactoryTest
{
    /**
     * Ensures that biomorphs created by the factory are valid.
     */
    @Test
    public void testValidity()
    {
        CandidateFactory<Biomorph> factory = new BiomorphFactory();
        List<Biomorph> biomorphs = factory.generateInitialPopulation(20, ExamplesTestUtils.getRNG());
        for (Biomorph biomorph : biomorphs)
        {
            // Returns 9 genes, last one is the length gene.
            int[] genes = biomorph.getGenotype();
            for (int i = 0; i < Biomorph.GENE_COUNT - 1; i++)
            {
                assert genes[i] >= Biomorph.GENE_MIN && genes[i] <= Biomorph.GENE_MAX
                    : "Gene " + i + " is out of range: " + genes[i];
            }
            int length = biomorph.getLengthPhenotype();
            assert length >= Biomorph.LENGTH_GENE_MIN && length <= Biomorph.LENGTH_GENE_MAX
                : "Length gene is out of range: " + length;
        }
    }
}
