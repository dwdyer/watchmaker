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
import java.util.Collections;
import java.util.List;
import org.testng.annotations.Test;
import org.uncommons.watchmaker.framework.EvaluatedCandidate;
import org.uncommons.watchmaker.framework.FrameworkTestUtils;
import org.uncommons.watchmaker.framework.SelectionStrategy;

/**
 * Unit test for fitness proportionate selection where observed selection
 * frequencies correspond to expected frequencies. 
 * @author Daniel Dyer
 */
public class StochasticUniversalSamplingTest
{
    /**
     * Test selection when fitness scoring is natural (higher is better).
     */
    @Test
    public void testNaturalFitnessSelection()
    {
        SelectionStrategy<Object> selector = new StochasticUniversalSampling();
        List<EvaluatedCandidate<String>> population = new ArrayList<EvaluatedCandidate<String>>(4);
        // Higher score is better.
        EvaluatedCandidate<String> steve = new EvaluatedCandidate<String>("Steve", 10.0);
        EvaluatedCandidate<String> john = new EvaluatedCandidate<String>("John", 4.5);
        EvaluatedCandidate<String> mary = new EvaluatedCandidate<String>("Mary", 1.0);
        EvaluatedCandidate<String> gary = new EvaluatedCandidate<String>("Gary", 0.5);
        population.add(steve);
        population.add(john);
        population.add(mary);
        population.add(gary);
        List<String> selection = selector.select(population, true, 4, FrameworkTestUtils.getRNG());
        assert selection.size() == 4 : "Selection size is " + selection.size() + ", should be 4.";
        int steveCount = Collections.frequency(selection, steve.getCandidate());
        int johnCount = Collections.frequency(selection, john.getCandidate());
        int garyCount = Collections.frequency(selection, gary.getCandidate());
        int maryCount = Collections.frequency(selection, mary.getCandidate());
        assert steveCount >= 2 && steveCount <= 3 : "Candidate selected wrong number of times (should be 2 or 3, was " + steveCount + ")";
        assert johnCount >= 1 && johnCount <= 2 : "Candidate selected wrong number of times (should be 1 or 2, was " + johnCount + ")";
        assert garyCount <= 1 : "Candidate selected wrong number of times (should be 0 or 1, was " + garyCount + ")";
        assert maryCount <= 1 : "Candidate selected wrong number of times (should be 0 or 1, was " + maryCount + ")";
    }


    /**
     * Test selection when fitness scoring is non-natural (lower is better).
     */
    @Test
    public void testNonNaturalFitnessSelection()
    {
        SelectionStrategy<Object> selector = new StochasticUniversalSampling();
        List<EvaluatedCandidate<String>> population = new ArrayList<EvaluatedCandidate<String>>(4);
        // Lower score is better.
        EvaluatedCandidate<String> gary = new EvaluatedCandidate<String>("Gary", 0.5);
        EvaluatedCandidate<String> mary = new EvaluatedCandidate<String>("Mary", 1.0);
        EvaluatedCandidate<String> john = new EvaluatedCandidate<String>("John", 4.5);
        EvaluatedCandidate<String> steve = new EvaluatedCandidate<String>("Steve", 10.0);
        population.add(gary);
        population.add(mary);
        population.add(john);
        population.add(steve);
        List<String> selection = selector.select(population, false, 4, FrameworkTestUtils.getRNG());
        assert selection.size() == 4 : "Selection size is " + selection.size() + ", should be 4.";
        int garyCount = Collections.frequency(selection, gary.getCandidate());
        int maryCount = Collections.frequency(selection, mary.getCandidate());
        int johnCount = Collections.frequency(selection, john.getCandidate());
        int steveCount = Collections.frequency(selection, steve.getCandidate());
        assert garyCount >= 2 && garyCount <= 3 : "Candidate selected wrong number of times (should be 2 or 3, was " + garyCount + ")";
        assert maryCount >= 1 && maryCount <= 2 : "Candidate selected wrong number of times (should be 1 or 2, was " + maryCount + ")";
        assert johnCount <= 1 : "Candidate selected wrong number of times (should be 0 or 1, was " + johnCount + ")";
        assert steveCount <= 1 : "Candidate selected wrong number of times (should be 0 or 1, was " + steveCount + ")";
    }
}
