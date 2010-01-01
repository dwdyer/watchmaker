//=============================================================================
// Copyright 2006-2010 Daniel W. Dyer
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
//     http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.
//=============================================================================
package org.uncommons.watchmaker.framework;

import org.testng.annotations.Test;

/**
 * Unit test for the simple {@link EvaluatedCandidate} class.  Ensures that
 * the equals and hashCode methods function correctly.
 * @author Daniel Dyer
 */
public class EvaluatedCandidateTest
{
    @Test
    public void testEquality()
    {
        // Equality is determined only by fitness score, the actual candidate
        // representation is irrelevant.  These two candidates should be considered
        // equal.
        EvaluatedCandidate<String> candidate1 = new EvaluatedCandidate<String>("AAAA", 5);
        EvaluatedCandidate<String> candidate2 = new EvaluatedCandidate<String>("BBBB", 5);

        assert candidate1.equals(candidate1) : "Equality must be reflexive.";
        assert candidate2.equals(candidate2) : "Equality must be reflexive.";

        assert candidate1.hashCode() == candidate2.hashCode() : "Hash codes must be identical for equal objects.";
        assert candidate1.compareTo(candidate2) == 0 : "compareTo() must be consistent with equals()";

        assert candidate1.equals(candidate2) : "Candidates with equal fitness should be equal.";
        assert candidate2.equals(candidate1) : "Equality must be symmetric.";
    }


    @Test
    public void testNotEqual()
    {
        // Equality is determined only by fitness score, the actual candidate
        // representation is irrelevant.  These two candidates should be considered
        // unequal.
        EvaluatedCandidate<String> candidate1 = new EvaluatedCandidate<String>("AAAA", 5);
        EvaluatedCandidate<String> candidate2 = new EvaluatedCandidate<String>("AAAA", 7);

        assert !candidate1.equals(candidate2) : "Candidates with equal fitness should be equal.";
        assert !candidate2.equals(candidate1) : "Equality must be symmetric.";

        assert candidate1.compareTo(candidate2) != 0 : "compareTo() must be consistent with equals()";
    }


    @Test
    public void testNullEquality()
    {
        EvaluatedCandidate<String> candidate = new EvaluatedCandidate<String>("AAAA", 5);
        assert !candidate.equals(null) : "Object must not be considered equal to null reference.";
    }


    @Test
    public void testDifferentClassEquality()
    {
        EvaluatedCandidate<String> candidate = new EvaluatedCandidate<String>("AAAA", 5);
        assert !candidate.equals(new Object()) : "Object must not be equal to instances of other classes.";
    }


    /**
     * Comparisons are based only on fitness score.
     */
    @Test(dependsOnMethods = "testEquality")
    public void testComparisons()
    {
        // Only test greater than and less than comparisons here since we've already
        // done equality.
        EvaluatedCandidate<String> candidate1 = new EvaluatedCandidate<String>("AAAA", 5);
        EvaluatedCandidate<String> candidate2 = new EvaluatedCandidate<String>("AAAA", 7);
        assert candidate1.compareTo(candidate2) < 0 : "Incorrect ordering.";
        assert candidate2.compareTo(candidate1) > 0 : "Incorrect ordering.";
    }


    /**
     * Negative fitness scores are not supported.  An informative exception should be thrown.
     */
    @Test(expectedExceptions = IllegalArgumentException.class)
    public void testNegativeFitness()
    {
        new EvaluatedCandidate<String>("ABC", -1); // Should throw an exception.
    }
}
