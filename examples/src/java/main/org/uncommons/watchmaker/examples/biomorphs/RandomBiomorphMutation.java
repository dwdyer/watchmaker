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
import org.uncommons.maths.random.Probability;
import org.uncommons.watchmaker.framework.EvolutionaryOperator;

/**
 * Mutation operator for biomorphs.  Mutates each individual gene
 * according to some mutation probability.
 * @author Daniel Dyer
 */
public class RandomBiomorphMutation implements EvolutionaryOperator<Biomorph>
{
    private final Probability mutationProbability;

    /**
     * @param mutationProbability The probability that a given gene
     * is changed.
     */
    public RandomBiomorphMutation(Probability mutationProbability)
    {
        this.mutationProbability = mutationProbability;
    }


    /**
     * Randomly mutate each selected candidate.
     * @param selectedCandidates {@inheritDoc}
     * @param rng {@inheritDoc}
     * @return {@inheritDoc}
     */
    public List<Biomorph> apply(List<Biomorph> selectedCandidates, Random rng)
    {
        List<Biomorph> mutatedPopulation = new ArrayList<Biomorph>(selectedCandidates.size());
        for (Biomorph biomorph : selectedCandidates)
        {
            mutatedPopulation.add(mutateBiomorph(biomorph, rng));
        }
        return mutatedPopulation;
    }


    /**
     * Mutates a single biomorph.
     * @param biomorph The biomorph to mutate.
     * @param rng The source of randomness to use for mutation.
     * @return A mutated version of the biomorph.
     */
    private Biomorph mutateBiomorph(Biomorph biomorph, Random rng)
    {
        int[] genes = biomorph.getGenotype();
        assert genes.length == Biomorph.GENE_COUNT : "Biomorphs must have " + Biomorph.GENE_COUNT + " genes.";
        for (int i = 0; i < Biomorph.GENE_COUNT - 1; i++)
        {
            if (mutationProbability.nextEvent(rng))
            {
                boolean increase = rng.nextBoolean();
                genes[i] += (increase ? 1 : -1);
                if (genes[i] > Biomorph.GENE_MAX)
                {
                    genes[i] = Biomorph.GENE_MIN;
                }
                else if (genes[i] < Biomorph.GENE_MIN)
                {
                    genes[i] = Biomorph.GENE_MAX;
                }
            }
        }
        boolean increase = rng.nextBoolean();
        genes[Biomorph.LENGTH_GENE_INDEX] += (increase ? 1 : -1);
        if (genes[Biomorph.LENGTH_GENE_INDEX] > Biomorph.LENGTH_GENE_MAX)
        {
            genes[Biomorph.LENGTH_GENE_INDEX] = Biomorph.LENGTH_GENE_MIN;
        }
        else if (genes[Biomorph.LENGTH_GENE_INDEX] < Biomorph.LENGTH_GENE_MIN)
        {
            genes[Biomorph.LENGTH_GENE_INDEX] = Biomorph.LENGTH_GENE_MAX;
        }
        return new Biomorph(genes);
    }
}
