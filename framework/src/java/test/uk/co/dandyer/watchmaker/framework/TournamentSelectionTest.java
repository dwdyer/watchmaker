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
import java.util.Random;
import org.testng.annotations.Test;

/**
 * Unit test for tournament selection strategy.  We cannot easily test
 * that the correct candidates are returned because of the random aspect
 * of the selection, but we can at least make sure the right number of
 * candidates are selected.
 * @author Daniel Dyer
 */
public class TournamentSelectionTest
{
    @Test
    public void testSelection()
    {
        SelectionStrategy selector = new TournamentSelection(0.7d);
        List<Pair<String, Double>> population = new ArrayList<Pair<String, Double>>(4);
        Pair<String, Double> steve = new Pair<String, Double>("Steve", 10.0);
        Pair<String, Double> john = new Pair<String, Double>("John", 8.4);
        Pair<String, Double> gary = new Pair<String, Double>("Gary", 6.2);
        Pair<String, Double> mary = new Pair<String, Double>("Mary", 9.1);
        population.add(steve);
        population.add(john);
        population.add(gary);
        population.add(mary);
        Collections.sort(population, new CandidateFitnessComparator());
        List<String> selection = selector.select(population, 2, new Random());
        assert selection.size() == 2 : "Selection size is " + selection.size() + ", should be 2.";
    }
}
