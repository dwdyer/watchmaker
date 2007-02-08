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

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import org.uncommons.watchmaker.framework.EvolutionaryOperator;

/**
 * Mutation operator for biomorphs.  Mutates each individual gene
 * according to some mutation probability.
 * @author Daniel Dyer
 */
public class BiomorphMutation implements EvolutionaryOperator<Biomorph>
{
    private final double mutationProbability;

    /**
     * @param mutationProbability The probability that a given gene
     * is changed.
     */
    public BiomorphMutation(double mutationProbability)
    {
        if (mutationProbability < 0 || mutationProbability > 1)
        {
            throw new IllegalArgumentException("Mutation probability must be between 0 and 1.");
        }
        this.mutationProbability = mutationProbability;
    }

    
    @SuppressWarnings({"unchecked"})
    public <S extends Biomorph> List<S> apply(List<S> selectedCandidates, Random rng)
    {
        List<S> mutatedPopulation = new ArrayList<S>(selectedCandidates.size());
        for (Biomorph biomorph : selectedCandidates)
        {
            mutatedPopulation.add((S) mutateBiomorph(biomorph, rng));
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
        assert genes.length == 9 : "Biomorphs must have 9 genes.";
        for (int i = 0; i < 8; i++)
        {
            if (rng.nextDouble() < mutationProbability)
            {
                boolean increase = rng.nextBoolean();
                genes[i] =+ (increase ? 1 : -1);
                if (genes[i] > 5)
                {
                    genes[i] = -5;
                }
                else if (genes[i] < -5)
                {
                    genes[i] = 5;
                }
            }
        }
        boolean increase = rng.nextBoolean();
        genes[8] =+ (increase ? 1 : -1);
        if (genes[8] > 8)
        {
            genes[8] = 1;
        }
        else if (genes[8] < 1)
        {
            genes[8] = 8;
        }
        return new Biomorph(genes);
    }
}
