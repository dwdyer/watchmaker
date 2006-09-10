package uk.co.dandyer.watchmaker.framework;

import java.util.BitSet;
import java.util.List;
import java.util.Random;
import java.util.ArrayList;
import uk.co.dandyer.maths.random.RandomSequence;

/**
 * Cross-over with a configurable number of points (fixed or random) for
 * bit sets.
 * @author Daniel Dyer
 */
public class BitSetCrossover extends AbstractCrossover<BitSet>
{
    /**
     * Default is single-point cross-over.
     */
    public BitSetCrossover()
    {
        this(1);
    }

    /**
     * Cross-over with a fixed number of cross-over points.
     */
    public BitSetCrossover(int crossoverPoints)
    {
        super(crossoverPoints);
    }

    /**
     * Cross-over with a variable number of cross-over points.
     */
    public BitSetCrossover(RandomSequence<Integer> crossoverPointsVariable)
    {
        super(crossoverPointsVariable);
    }


    protected List<? extends BitSet> reproduce(BitSet parent1,
                                               BitSet parent2,
                                               int numberOfCrossoverPoints,
                                               Random rng)
    {
        if (parent1.length() != parent2.length())
        {
            throw new IllegalArgumentException("Cannot perform cross-over with different length parents.");
        }
        BitSet offspring1 = new BitSet(parent1.length());
        offspring1.or(parent1);
        BitSet offspring2 = new BitSet(parent2.length());
        offspring2.or(parent2);
        // Apply as many cross-overs as required.
        for (int i = 0; i < numberOfCrossoverPoints; i++)
        {
            int crossoverIndex = rng.nextInt(parent1.length());
            for (int j = 0; j < crossoverIndex; j++)
            {
                boolean temp = offspring1.get(j);
                offspring1.set(j, offspring2.get(j));
                offspring2.set(j, temp);
            }
        }
        List<BitSet> result = new ArrayList<BitSet>(2);
        result.add(offspring1);
        result.add(offspring2);
        return result;
    }
}
