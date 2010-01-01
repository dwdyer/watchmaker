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
import org.uncommons.watchmaker.framework.EvolutionaryOperator;

/**
 * Non-random mutation of a population of biomorphs.  This ensures that each selected candidate
 * is mutated differently.  This is the mutation used by Dawkins in his original experiment.
 * @author Daniel Dyer
 */
public class DawkinsBiomorphMutation implements EvolutionaryOperator<Biomorph>
{
    /**
     * Mutate a population of biomorphs non-randomly, ensuring that each selected
     * candidate is mutated differently. 
     * @param selectedCandidates {@inheritDoc}
     * @param rng A source of randomness (not used since this mutation is non-random).
     * @return {@inheritDoc}
     */
    public List<Biomorph> apply(List<Biomorph> selectedCandidates, Random rng)
    {
        List<Biomorph> mutatedPopulation = new ArrayList<Biomorph>(selectedCandidates.size());
        int mutatedGene = 0;
        int mutation = 1;
        for (Biomorph b : selectedCandidates)
        {
            int[] genes = b.getGenotype();

            mutation *= -1; // Alternate between incrementing and decrementing.
            if (mutation == 1) // After gene has been both incremented and decremented, move to next one.
            {
                mutatedGene = (mutatedGene + 1) % Biomorph.GENE_COUNT;
            }
            genes[mutatedGene] += mutation;
            int min = mutatedGene == Biomorph.LENGTH_GENE_INDEX ? Biomorph.LENGTH_GENE_MIN : Biomorph.GENE_MIN;
            int max = mutatedGene == Biomorph.LENGTH_GENE_INDEX ? Biomorph.LENGTH_GENE_MAX : Biomorph.GENE_MAX;
            if (genes[mutatedGene] > max)
            {
                genes[mutatedGene] = min;
            }
            else if (genes[mutatedGene] < min)
            {
                genes[mutatedGene] = max;
            }

            mutatedPopulation.add(new Biomorph(genes));
        }
        return mutatedPopulation;
    }
}
