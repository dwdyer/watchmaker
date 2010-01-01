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
package org.uncommons.watchmaker.framework.selection;

import java.util.ArrayList;
import java.util.List;
import org.testng.annotations.Test;
import org.uncommons.watchmaker.framework.EvaluatedCandidate;
import org.uncommons.watchmaker.framework.SelectionStrategy;

/**
 * Unit test for truncation selection strategy.  Ensures the
 * correct candidates are selected.
 * @author Daniel Dyer
 */
public class TruncationSelectionTest
{
    @Test
    public void testNaturalFitnessSelection()
    {
        SelectionStrategy<Object> selector = new TruncationSelection(0.5d);
        List<EvaluatedCandidate<String>> population = new ArrayList<EvaluatedCandidate<String>>(4);
        // Higher score is better.
        EvaluatedCandidate<String> steve = new EvaluatedCandidate<String>("Steve", 10.0);
        EvaluatedCandidate<String> mary = new EvaluatedCandidate<String>("Mary", 9.1);
        EvaluatedCandidate<String> john = new EvaluatedCandidate<String>("John", 8.4);
        EvaluatedCandidate<String> gary = new EvaluatedCandidate<String>("Gary", 6.2);
        population.add(steve);
        population.add(mary);
        population.add(john);
        population.add(gary);
        List<String> selection = selector.select(population, true, 2, null);
        assert selection.size() == 2 : "Selection size is " + selection.size() + ", should be 2.";
        assert selection.contains(steve.getCandidate()) : "Best candidate not selected.";
        assert selection.contains(mary.getCandidate()) : "Second best candidate not selected.";
    }


    @Test
    public void testNonNaturalFitnessSelection()
    {
        SelectionStrategy<Object> selector = new TruncationSelection(0.5d);
        List<EvaluatedCandidate<String>> population = new ArrayList<EvaluatedCandidate<String>>(4);
        // Lower score is better.
        EvaluatedCandidate<String> gary = new EvaluatedCandidate<String>("Gary", 6.2);
        EvaluatedCandidate<String> john = new EvaluatedCandidate<String>("John", 8.4);
        EvaluatedCandidate<String> mary = new EvaluatedCandidate<String>("Mary", 9.1);
        EvaluatedCandidate<String> steve = new EvaluatedCandidate<String>("Steve", 10.0);
        population.add(gary);
        population.add(john);
        population.add(mary);
        population.add(steve);
        List<String> selection = selector.select(population, false, 2, null);
        assert selection.size() == 2 : "Selection size is " + selection.size() + ", should be 2.";
        assert selection.contains(gary.getCandidate()) : "Best candidate not selected.";
        assert selection.contains(john.getCandidate()) : "Second best candidate not selected.";
    }


    /**
     * The selection ratio must be greater than zero to be useful.  This test
     * ensures that an appropriate exception is thrown if the ratio is not positive.
     * Not throwing an exception is an error because it permits undetected bugs in
     * evolutionary programs.
     */
    @Test(expectedExceptions = IllegalArgumentException.class)
    public void testZeroRatio()
    {
        new TruncationSelection(0d);
    }


    /**
     * The selection ratio must be less than 1 to be useful.  This test
     * ensures that an appropriate exception is thrown if the ratio is too high.
     * Not throwing an exception is an error because it permits undetected bugs in
     * evolutionary programs.
     */
    @Test(expectedExceptions = IllegalArgumentException.class)
    public void testRatioTooHigh()
    {
        new TruncationSelection(1d);
    }
}
