// ============================================================================
//   Copyright 2006 Daniel W. Dyer
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
package org.uncommons.maths.stats;

import java.util.Random;
import org.uncommons.maths.NumberSequence;

/**
 * Discrete random sequence that follows a binomial distribution.
 * @author Daniel Dyer
 */
public class BinomialSequence implements NumberSequence<Integer>
{
    private final Random rng;
    private final int n;
    private final double p;

    /**
     * @param n The number of trials (and therefore the maximum possible value returned
     * by this sequence).
     * @param p The probability (between 0 and 1) of success in any one trial.
     * @param rng
     */
    public BinomialSequence(int n,
                            double p,
                            Random rng)
    {
        if (n <= 0)
        {
            throw new IllegalArgumentException("n must be a positive integer.");
        }
        if (p < 0 || p > 1)
        {
            throw new IllegalArgumentException("p must be between 0 and 1.");
        }
        this.n = n;
        this.p = p;
        this.rng = rng;
    }


    public Integer nextValue()
    {
        // TO DO: When n is large, apply an approximation using the normal distribution
        // to improve performance.
        int x = 0;
        for (int i = 0; i < n; i++)
        {
            double d = rng.nextDouble();
            if (d < p)
            {
                ++x;
            }
        }
        return x;
    }
}
