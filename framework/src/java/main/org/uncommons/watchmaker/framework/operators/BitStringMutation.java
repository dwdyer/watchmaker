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
package org.uncommons.watchmaker.framework.operators;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import org.uncommons.maths.binary.BitString;
import org.uncommons.maths.number.ConstantGenerator;
import org.uncommons.maths.number.NumberGenerator;
import org.uncommons.maths.random.Probability;
import org.uncommons.watchmaker.framework.EvolutionaryOperator;

/**
 * Mutation of individual bits in a {@link BitString} according to some
 * probability.
 * @see org.uncommons.maths.binary.BitString
 * @author Daniel Dyer
 */
public class BitStringMutation implements EvolutionaryOperator<BitString>
{
    private final NumberGenerator<Probability> mutationProbability;
    private final NumberGenerator<Integer> mutationCount;


    /**
     * Creates a mutation operator for bit strings with the specified probability that a given
     * bit string will be mutated, with exactly one bit being flipped.
     * @param mutationProbability The probability of a candidate being mutated.
     */
    public BitStringMutation(Probability mutationProbability)
    {
        this(new ConstantGenerator<Probability>(mutationProbability),
             new ConstantGenerator<Integer>(1));
    }


    /**
     * Creates a mutation operator for bit strings, with the probability that any
     * given bit will be flipped governed by the specified number generator.
     * @param mutationProbability The (possibly variable) probability of a candidate
     * bit string being mutated at all.
     * @param mutationCount The (possibly variable) number of bits that will be flipped
     * on any candidate bit string that is selected for mutation.
     */
    public BitStringMutation(NumberGenerator<Probability> mutationProbability,
                             NumberGenerator<Integer> mutationCount)
    {
        this.mutationProbability = mutationProbability;
        this.mutationCount = mutationCount;
    }


    public List<BitString> apply(List<BitString> selectedCandidates, Random rng)
    {
        List<BitString> mutatedPopulation = new ArrayList<BitString>(selectedCandidates.size());
        for (BitString b : selectedCandidates)
        {
            mutatedPopulation.add(mutateBitString(b, rng));
        }
        return mutatedPopulation;
    }


    /**
     * Mutate a single bit string.  Zero or more bits may be flipped.  The
     * probability of any given bit being flipped is governed by the probability
     * generator configured for this mutation operator.
     * @param bitString The bit string to mutate.
     * @param rng A source of randomness.
     * @return The mutated bit string.
     */
    private BitString mutateBitString(BitString bitString, Random rng)
    {
        if (mutationProbability.nextValue().nextEvent(rng))
        {
            BitString mutatedBitString = bitString.clone();
            int mutations = mutationCount.nextValue();
            for (int i = 0; i < mutations; i++)
            {
                mutatedBitString.flipBit(rng.nextInt(mutatedBitString.getLength()));
            }
            return mutatedBitString;
        }
        return bitString;
    }
}
