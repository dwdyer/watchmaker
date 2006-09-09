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
package uk.co.dandyer.watchmaker.framework;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.testng.annotations.Test;
import uk.co.dandyer.maths.random.MersenneTwisterRNG;

/**
 * @author Daniel Dyer
 */
public class StochasticUniversalSamplingTest
{
    @Test
    public void testSelection()
    {
        SelectionStrategy selector = new StochasticUniversalSampling();
        List<Pair<String, Double>> population = new ArrayList<Pair<String, Double>>(4);
        Pair<String, Double> steve = new Pair<String, Double>("Steve", 10.0);
        Pair<String, Double> john = new Pair<String, Double>("John", 4.5);
        Pair<String, Double> gary = new Pair<String, Double>("Gary", 0.5);
        Pair<String, Double> mary = new Pair<String, Double>("Mary", 1.0);
        population.add(steve);
        population.add(john);
        population.add(gary);
        population.add(mary);
        List<String> selection = selector.select(population, 4, new MersenneTwisterRNG());
        assert selection.size() == 4 : "Selection size is " + selection.size() + ", should be 4.";
        int steveCount = Collections.frequency(selection, steve.getFirst());
        int johnCount = Collections.frequency(selection, john.getFirst());
        int garyCount = Collections.frequency(selection, gary.getFirst());
        int maryCount = Collections.frequency(selection, mary.getFirst());
        assert steveCount >= 2 && steveCount <= 3 : "Candidate selected wrong number of times (should be 2 or 3, was " + steveCount + ")";
        assert johnCount >= 1 && johnCount <= 2 : "Candidate selected wrong number of times (should be 1 or 2, was " + johnCount + ")";
        assert garyCount <= 1 : "Candidate selected wrong number of times (should be 0 or 1, was " + garyCount + ")";
        assert maryCount <= 1 : "Candidate selected wrong number of times (should be 0 or 1, was " + maryCount + ")";
    }

}
