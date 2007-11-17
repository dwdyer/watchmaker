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
import org.uncommons.maths.stats.DataSet;

/**
 * Unit test for the normally-distributed number generator.
 * @author Daniel Dyer
 */
public class GaussianGeneratorTest
{
    private final Random rng = new MersenneTwisterRNG();


    /**
     * Check that the observed mean and standard deviation are consistent
     * with the specified distribution parameters.
     */
    @Test
    public void testDistribution()
    {
        final double mean = 147;
        final double standardDeviation = 17;
        NumberGenerator<Double> generator = new GaussianGenerator(mean,
                                                                  standardDeviation,
                                                                  rng);
        checkDistribution(generator, mean, standardDeviation);
    }


    @Test
    public void testDynamicParameters()
    {
        final double initialMean = 147;
        final double initialStandardDeviation = 17;
        AdjustableNumberGenerator<Double> meanGenerator = new AdjustableNumberGenerator<Double>(initialMean);
        AdjustableNumberGenerator<Double> standardDeviationGenerator = new AdjustableNumberGenerator<Double>(initialStandardDeviation);
        NumberGenerator<Double> generator = new GaussianGenerator(meanGenerator,
                                                                  standardDeviationGenerator,
                                                                  rng);
        checkDistribution(generator, initialMean, initialStandardDeviation);

        // Adjust parameters and ensure that the generator output conforms to this new
        // distribution.
        final double adjustedMean = 73;
        final double adjustedStandardDeviation = 9;
        meanGenerator.setValue(adjustedMean);
        standardDeviationGenerator.setValue(adjustedStandardDeviation);
        
        checkDistribution(generator, adjustedMean, adjustedStandardDeviation);
    }


    private void checkDistribution(NumberGenerator<Double> generator,
                                   double expectedMean,
                                   double expectedStandardDeviation)
    {
        final int iterations = 10000;
        DataSet data = new DataSet(iterations);
        for (int i = 0; i < iterations; i++)
        {
            data.addValue(generator.nextValue());
        }
        assert Maths.approxEquals(data.getArithmeticMean(), expectedMean, 0.02)
                : "Observed mean outside acceptable range: " + data.getArithmeticMean();
        assert Maths.approxEquals(data.getSampleStandardDeviation(), expectedStandardDeviation, 0.02)
                : "Observed standard deviation outside acceptable range: " + data.getSampleStandardDeviation();
    }
}
