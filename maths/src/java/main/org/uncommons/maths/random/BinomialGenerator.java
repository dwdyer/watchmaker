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
import org.uncommons.maths.ConstantGenerator;
import org.uncommons.maths.NumberGenerator;

/**
 * Discrete random sequence that follows a binomial distribution.
 * @author Daniel Dyer
 */
public class BinomialGenerator implements NumberGenerator<Integer>
{
    private final Random rng;
    private final NumberGenerator<Integer> n;
    private final NumberGenerator<Double> p;


    /**
     * <p>Creates a generator of normally-distributed values.  The number of
     * trials ({@literal n}) and the probability of success in each trial
     * ({@literal p}) are determined by the provided {@link NumberGenerator}s.
     * This means that the statistical parameters of this generator may change
     * over time.  One example of where this is useful is if the {@literal n}
     * and {@literal p} generators are attached to GUI controls that allow a
     * user to tweak the parameters while a program is running.</p>
     * <p>To create a Binomial generator with a constant {@literal n} and
     * {@literal p}, use the {@link #BinomialGenerator(int, double, Random)}
     * constructor instead.</p>
     * @param n A {@link NumberGenerator} that provides the number of trials for
     * the Binomial distribution used for the next generated value.  This generator
     * must produce only positive values.
     * @param p A {@link NumberGenerator} that provides the probability of succes
     * in a single trial for the Binomial distribution used for the next
     * generated value.  This generator must produce only values in the range 0 - 1.
     * @param rng The source of randomness.
     */
    public BinomialGenerator(NumberGenerator<Integer> n,
                             NumberGenerator<Double> p,
                             Random rng)
    {
        this.n = n;
        this.p = p;
        this.rng = rng;
    }


    /**
     * Creates a generator of Binomially-distributed values from a distribution
     * with the specified parameters.
     * @param n The number of trials (and therefore the maximum possible value returned
     * by this sequence).
     * @param p The probability (between 0 and 1) of success in any one trial.
     * @param rng The source of randomness used to generate the binomial values.
     */
    public BinomialGenerator(int n,
                             double p,
                             Random rng)
    {
        this(new ConstantGenerator<Integer>(n),
             new ConstantGenerator<Double>(p),
             rng);
        if (n <= 0)
        {
            throw new IllegalArgumentException("n must be a positive integer.");
        }
        if (p <= 0 || p >= 1)
        {
            throw new IllegalArgumentException("p must be between 0 and 1.");
        }
    }


    /**
     * {@inheritDoc}
     */
    public Integer nextValue()
    {
        // TO DO: When n is large, apply an approximation using the normal distribution
        // to improve performance.
        int x = 0;
        for (int i = 0; i < n.nextValue(); i++)
        {
            double d = rng.nextDouble();
            if (d < p.nextValue())
            {
                ++x;
            }
        }
        return x;
    }
}
