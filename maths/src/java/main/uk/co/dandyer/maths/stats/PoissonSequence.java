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
package uk.co.dandyer.maths.stats;

import uk.co.dandyer.maths.NumberSequence;
import java.util.Random;

/**
 * Random sequence with values drawn from a Poisson distribution.
 * @author Daniel Dyer
 */
public class PoissonSequence implements NumberSequence<Integer>
{
    private final Random rng;
    private final double mean;

    public PoissonSequence(double mean,
                           Random rng)
    {
        this.mean = mean;
        this.rng = rng;
    }

    public Integer nextValue()
    {
        int x = 0;
        double t = 0.0;
        while (true)
        {
            t -= Math.log(rng.nextDouble()) / mean;
            if (t > 1.0)
            {
                break;
            }
            ++x;
        }
        return x;
    }
}
