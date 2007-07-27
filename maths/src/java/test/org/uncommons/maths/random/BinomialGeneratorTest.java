// ============================================================================
//   Copyright 2006, 2007 Daniel W. Dyer
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
package org.uncommons.maths.random;

import java.util.Random;
import org.testng.annotations.Test;
import org.uncommons.maths.AdjustableNumberGenerator;
import org.uncommons.maths.Maths;
import org.uncommons.maths.NumberGenerator;
import org.uncommons.maths.stats.SampleDataSet;

/**
 * Unit test for the Binomial number generator.
 * @author Daniel Dyer
 */
public class BinomialGeneratorTest
{
    private final Random rng = new MersenneTwisterRNG();

    /**
     * Check that the observed mean and standard deviation are consistent
     * with the specified distribution parameters.
     */
    @Test
    public void testDistribution()
    {
        final int n = 20;
        final double p = 0.163;
        NumberGenerator<Integer> generator = new BinomialGenerator(n, // Number of trials.
                                                                   p, // Probability of success in each.
                                                                   rng);
        checkDistribution(generator, n, p);
    }


    @Test
    public void testDynamicParameters()
    {
        final int initialN = 20;
        final double initialP = 0.163;
        AdjustableNumberGenerator<Integer> nGenerator = new AdjustableNumberGenerator<Integer>(initialN);
        AdjustableNumberGenerator<Double> pGenerator = new AdjustableNumberGenerator<Double>(initialP);
        NumberGenerator<Integer> generator = new BinomialGenerator(nGenerator,
                                                                   pGenerator,
                                                                   rng);
        checkDistribution(generator, initialN, initialP);

        // Adjust parameters and ensure that the generator output conforms to this new distribution.
        final int adjustedN = 14;
        nGenerator.setValue(adjustedN);
        final double adjustedP = 0.32;
        pGenerator.setValue(adjustedP);

        checkDistribution(generator, adjustedN, adjustedP);
    }


    /**
     * The probability of succes in any single trial is invalid if it is greater than 1.
     * Further, it needs to be less than 1 to be useful.  This test ensures that an
     * appropriate exception is thrown if the probability is greater than or equal to 1.
     * Not throwing an exception is an error because it permits undetected bugs in
     * programs that use {@link BinomialGenerator}.
     */
    @Test(expectedExceptions = IllegalArgumentException.class)
    public void testProbabilityTooHigh()
    {
        new BinomialGenerator(5, 1d, rng);
    }


    /**
     * The probability of succes in any single trial must be greater than zero to
     * be useful.  This test ensures that an appropriate exception is thrown if the
     * probability is not positive.  Not throwing an exception is an error because it
     * permits undetected bugs in programs that use {@link BinomialGenerator}.
     */
    @Test(expectedExceptions = IllegalArgumentException.class)
    public void testProbabilityTooLow()
    {
        new BinomialGenerator(5, 0d, rng);
    }


    /**
     * The number of trials must be greater than zero to be useful.  This test ensures
     * that an appropriate exception is thrown if the number is not positive.  Not
     * throwing an exception is an error because it permits undetected bugs in
     * programs that use {@link BinomialGenerator}.
     */
    @Test(expectedExceptions = IllegalArgumentException.class)
    public void testTrialsTooLow()
    {
        new BinomialGenerator(0, 0.5d, rng);
    }


    private void checkDistribution(NumberGenerator<Integer> generator,
                                   int n,
                                   double p)
    {
        final double expectedMean = n * p;
        final double expectedStandardDeviation = Math.sqrt(n * p * (1 - p));

        final int iterations = 10000;
        SampleDataSet data = new SampleDataSet(iterations);
        for (int i = 0; i < iterations; i++)
        {
            data.addValue(generator.nextValue());
        }
        assert Maths.approxEquals(data.getArithmeticMean(), expectedMean, 0.01)
                : "Observed mean outside acceptable range: " + data.getArithmeticMean();
        assert Maths.approxEquals(data.getStandardDeviation(), expectedStandardDeviation, 0.02)
                : "Observed standard deviation outside acceptable range: " + data.getStandardDeviation();
    }
}
