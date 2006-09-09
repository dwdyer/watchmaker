package uk.co.dandyer.maths.random;

import java.util.Random;

/**
 * Random sequence with values drawn from a continuous uniform distribution.
 * @author Daniel Dyer
 */
public class UniformDoubleSequence implements RandomSequence<Double>
{
    private final Random generator;
    private final double range;
    private final double minimumValue;

    public UniformDoubleSequence(Random generator,
                                 double minimumValue,
                                 double maximumValue)
    {
        this.generator = generator;
        this.minimumValue = minimumValue;
        this.range = maximumValue - minimumValue + 1;
    }

    public Double nextValue()
    {
        return generator.nextDouble() * range + minimumValue;
    }
}
