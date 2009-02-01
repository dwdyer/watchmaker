// ============================================================================
//   Copyright 2006-2009 Daniel W. Dyer
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
package org.uncommons.watchmaker.framework;

import java.util.Random;
import org.testng.Reporter;
import org.testng.annotations.Test;
import org.uncommons.maths.Maths;
import org.uncommons.maths.random.MersenneTwisterRNG;

/**
 * Unit test for {@link Probability} value type.
 * @author Daniel Dyer
 */
public class ProbabilityTest
{
    private final Random rng = new MersenneTwisterRNG();

    /**
     * Negative probabilities are invalid.
     */
    @Test(expectedExceptions = IllegalArgumentException.class)
    public void testProbabilityTooLow()
    {
        new Probability(-0.01); // Should throw an IllegalArgumentException.
    }


    /**
     * Probabilities greater than one are invalid.
     */
    @Test(expectedExceptions = IllegalArgumentException.class)
    public void testProbabilityTooHigh()
    {
        new Probability(1.01); // Should throw an IllegalArgumentException.
    }


    @Test
    public void testZeroIntegerConversion()
    {
        assert Probability.ZERO.longValue() == 0 : "Invalid integer conversion of zero probability.";
    }


    @Test
    public void testOneIntegerConversion()
    {
        assert Probability.ONE.longValue() == 1 : "Invalid integer conversion of one probability.";
    }


    @Test(expectedExceptions = ArithmeticException.class)
    public void testFractionalIntegerConversion()
    {
        Probability.EVENS.longValue(); // Should throw an ArithmeticException.
    }


    @Test
    public void testEquality()
    {
        Probability p1 = new Probability(0.75d);
        Probability p2 = new Probability(0.75d);
        assert p1.equals(p2) : "Numerically equivalent probabilities should be considered equal.";
        assert p2.equals(p1) : "Equality must be reflective.";
        assert p1.hashCode() == p2.hashCode() : "Equal probabilities must have identical hash codes.";
        assert !p1.equals(Double.valueOf(0.75)) : "Objects of different types should not be considered equal.";

        assert !Probability.ONE.equals(Probability.EVENS) : "Numerically distinct probabilities should not be considered equal.";
        assert Probability.ONE.equals(Probability.ONE) : "Equality must be reflexive.";
        assert !Probability.ONE.equals(null) : "No object should be considered equal to null.";
    }


    @Test(dependsOnMethods = "testEquality")
    public void testComparisons()
    {
        Probability p1 = new Probability(0.75d);
        Probability p2 = new Probability(0.75d);
        Probability p3 = new Probability(0.9d);
        assert p1.compareTo(p1) == 0 : "Equality must be reflexive.";
        assert p1.compareTo(p2) == 0 : "equals() must be consitent with compareTo()";
        assert p1.compareTo(p3) < 0 : "First argument should be less than second.";
        assert p3.compareTo(p1) > 0 : "First argument should be greater than second.";
    }


    @Test(dependsOnMethods = "testEquality")
    public void testIntegerComplements()
    {
        Probability complement = Probability.ZERO.getComplement();
        assert complement.equals(Probability.ONE) : "Incorrect complement for zero: " + complement;
        assert complement.getComplement().equals(Probability.ZERO) : "Complement must be symmetrical.";
    }


    @Test(dependsOnMethods = "testEquality")
    public void testFractionalComplements()
    {
        Probability probability = new Probability(0.75d);
        Probability complement = probability.getComplement();
        assert complement.equals(new Probability(0.25d)) : "Incorrect complement: " + complement;
        assert complement.getComplement().equals(probability) : "Complement must be symmetrical.";
    }


    /**
     * If the probability is zero, the {@link Probability#nextEvent(java.util.Random)} method
     * should never return true.
     */
    @Test
    public void testImpossibleEvents()
    {
        for (int i = 0; i < 1000; i++)
        {
            assert !Probability.ZERO.nextEvent(rng) : "Impossible event occurred."; 
        }
    }


    /**
     * If the probability is one, the {@link Probability#nextEvent(java.util.Random)} method
     * should always return true.
     */
    @Test
    public void testCertainties()
    {
        for (int i = 0; i < 1000; i++)
        {
            assert Probability.ONE.nextEvent(rng) : "Certainty failed to happen.";
        }
    }


    /**
     * Check that the observed event outcomes are in line with what we would expect.
     */
    @Test
    public void testPossibleEvents()
    {
        final int iterations = 1000;
        int count = 0;
        for (int i = 0; i < iterations; i++)
        {
            if (Probability.EVENS.nextEvent(rng))
            {
                ++count;
            }
        }
        double observedProbability = (double) count / iterations;
        // If we get between 450 and 550 successful outcomes (the expected 500 +/- 10%),
        // we will assume that that the distribution is correct.
        Reporter.log("Observed probability: " + observedProbability);
        assert Maths.approxEquals(observedProbability, 0.5d, 0.1d) : "Observed probability outside tolerance.";
    }
}
