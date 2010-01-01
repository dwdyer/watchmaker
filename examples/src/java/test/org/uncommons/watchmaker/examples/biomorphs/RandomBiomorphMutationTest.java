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

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import org.testng.annotations.Test;
import org.uncommons.maths.random.Probability;
import org.uncommons.watchmaker.examples.ExamplesTestUtils;
import org.uncommons.watchmaker.framework.EvolutionaryOperator;

/**
 * Unit test for random mutation operator for biomorphs.
 * @author Daniel Dyer
 */
public class RandomBiomorphMutationTest
{
    @Test
    public void testValidity()
    {
        EvolutionaryOperator<Biomorph> mutation = new RandomBiomorphMutation(Probability.ONE); // Mutate every gene.
        List<Biomorph> population = new ArrayList<Biomorph>(3);
        population.add(new Biomorph(new int[]{5, -4, -3, -2, -1, 0, 1, 2, 8}));
        population.add(new Biomorph(new int[]{-5, 4, 4, -5, 5, 3, 0, 2, 2}));
        population.add(new Biomorph(new int[]{-4, -1, 0, 0, 3, 0, 4, 5, 4}));
        Random rng = ExamplesTestUtils.getRNG();
        for (int i = 0; i < 20; i++) // Perform several mutations to cover more possibilties.
        {
            population = mutation.apply(population, rng);
            for (Biomorph biomorph : population)
            {
                // Returns 9 genes, last one is the length gene.
                int[] genes = biomorph.getGenotype();
                for (int j = 0; j < Biomorph.GENE_COUNT - 1; j++)
                {
                    assert genes[j] >= Biomorph.GENE_MIN && genes[j] <= Biomorph.GENE_MAX
                        : "Gene " + j + " is out of range: " + genes[j];
                }
                int length = biomorph.getLengthPhenotype();
                assert length >= Biomorph.LENGTH_GENE_MIN && length <= Biomorph.LENGTH_GENE_MAX
                    : "Length gene is out of range: " + length;
            }
        }
    }
}
