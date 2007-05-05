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

import java.security.GeneralSecurityException;
import java.security.SecureRandom;
import java.util.Random;

/**
 * Performs timings on the various random number generator (RNG) implementations
 * available for use.  This class does not perform any
 * statistical tests on the output of the RNGs, it simply measures throughput.
 * @author Daniel Dyer
 */
public class RNGBenchmark implements Runnable
{
    private static final int ITERATIONS = 1000000;

    private final Random rng;
    private final int iterations;

    public RNGBenchmark(Random rng,
                        int iterations)
    {
        this.rng = rng;
        this.iterations = iterations;
    }


    public void run()
    {
        System.out.println("Testing " + rng.getClass().getName() + "...");
        long startTime = System.currentTimeMillis();
        for (int i = 1; i <= iterations; i++)
        {
            rng.nextInt(i);
            rng.nextDouble();
            rng.nextGaussian();
        }
        long elapsedTime = System.currentTimeMillis() - startTime;
        double seconds = ((double) elapsedTime) / 1000;
        System.out.println("Completed " + iterations + " iterations in " + seconds + " seconds.\n");
    }


    public static void main(String[] args) throws GeneralSecurityException
    {
        System.out.println("------------------------------------------------------------");
        new RNGBenchmark(new JavaRNG(), ITERATIONS).run();
        new RNGBenchmark(new SecureRandom(), ITERATIONS).run();
        new RNGBenchmark(new AESCounterRNG(), ITERATIONS).run();
        new RNGBenchmark(new MersenneTwisterRNG(), ITERATIONS).run();
        new RNGBenchmark(new CellularAutomatonRNG(), ITERATIONS).run();
        System.out.println("------------------------------------------------------------");
    }
}
