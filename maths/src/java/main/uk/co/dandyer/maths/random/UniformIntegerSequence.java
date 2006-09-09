package uk.co.dandyer.maths.random;

import java.util.Random;

/**
 * Random sequence with values drawn from a discrete uniform distribution.
 * @author Daniel Dyer
 */
public class UniformIntegerSequence implements RandomSequence<Integer>
{
    private final Random generator;
    private final int range;
    private final int minimumValue;

    public UniformIntegerSequence(Random generator,
                                  int minimumValue,
                                  int maximumValue)
    {
        this.generator = generator;
        this.minimumValue = minimumValue;
        this.range = maximumValue - minimumValue + 1;
    }

    public Integer nextValue()
    {
        return generator.nextInt(range) + minimumValue;
    }
}
