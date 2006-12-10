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
package org.uncommons.watchmaker.framework.operators;

import java.util.ArrayList;
import java.util.BitSet;
import java.util.List;
import java.util.Random;
import org.uncommons.watchmaker.framework.EvolutionaryOperator;

/**
 * Mutation of individual bits in a {@link BitSet} according to some
 * probability.
 * @author Daniel Dyer
 */
public class BitSetMutation implements EvolutionaryOperator<BitSet>
{
    private final double mutationProbability;


    /**
     * @param mutationProbability The probability of a single bit being flipped.
     */
    public BitSetMutation(double mutationProbability)
    {
        this.mutationProbability = mutationProbability;
    }


    @SuppressWarnings("unchecked")
    public <S extends BitSet> List<S> apply(List<S> selectedCandidates, Random rng)
    {
        List<S> mutatedPopulation = new ArrayList<S>(selectedCandidates.size());
        for (BitSet b : selectedCandidates)
        {
            mutatedPopulation.add((S) mutateBitSet(b, rng));
        }
        return mutatedPopulation;
    }


    private BitSet mutateBitSet(BitSet bitSet, Random rng)
    {
        BitSet mutatedBitSet = (BitSet) bitSet.clone();
        for (int i = 0; i < mutatedBitSet.length(); i++)
        {
            if (rng.nextDouble() <= mutationProbability)
            {
                mutatedBitSet.flip(i);
            }
        }
        return mutatedBitSet;
    }
}
