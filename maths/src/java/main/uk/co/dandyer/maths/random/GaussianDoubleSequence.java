package uk.co.dandyer.maths.random;

import java.util.Random;

/**
 * Random sequence with values drawn from a continuous normal distribution.
 * @author Daniel Dyer
 */
public class GaussianDoubleSequence implements RandomSequence<Double>
{
    private final Random generator;
    private final double mean;
    private final double standardDeviation;


    public GaussianDoubleSequence(double mean,
                                  double standardDeviation,
                                  Random generator)
    {
        this.generator = generator;
        this.mean = mean;
        this.standardDeviation = standardDeviation;
    }


    public Double nextValue()
    {
        return generator.nextGaussian() * standardDeviation + mean;
    }
}
