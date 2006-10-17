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


    public List<BitSet> apply(List<BitSet> selectedCandidates, Random rng)
    {
        List<BitSet> mutatedPopulation = new ArrayList<BitSet>(selectedCandidates.size());
        for (BitSet b : selectedCandidates)
        {
            mutatedPopulation.add(mutateBitSet(b, rng));
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
