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

import org.testng.annotations.Test;
import org.uncommons.maths.Maths;
import org.uncommons.maths.NumberGenerator;
import org.uncommons.maths.stats.SampleDataSet;

/**
 * Unit test for the Binomial number generator.
 * @author Daniel Dyer
 */
public class BinomialGeneratorTest
{
    /**
     * Check that the observed mean and standard deviation are consistent
     * with the specified distribution parameters.
     */
    @Test
    public void testDistribution()
    {
        final int n = 20;
        final double p = 0.163;
        final double mean = n * p;
        final double standardDeviation = Math.sqrt(n * p * (1 - p));
        NumberGenerator<Integer> generator = new BinomialGenerator(n, // Number of trials.
                                                                   p, // Probability of success in each.
                                                                   new MersenneTwisterRNG());
        final int iterations = 10000;
        SampleDataSet data = new SampleDataSet(iterations);
        for (int i = 0; i < iterations; i++)
        {
            data.addValue(generator.nextValue());
        }
        assert Maths.approxEquals(data.getArithmeticMean(), mean, 0.25)
                : "Observed mean outside acceptable range: " + data.getArithmeticMean();
        assert Maths.approxEquals(data.getStandardDeviation(), standardDeviation, 0.25)
                : "Observed standard deviation outside acceptable range: " + data.getStandardDeviation();
    }
}
