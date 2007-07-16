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
package org.uncommons.watchmaker.framework.selection;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import org.testng.annotations.Test;
import org.uncommons.watchmaker.framework.EvaluatedCandidate;
import org.uncommons.watchmaker.framework.SelectionStrategy;

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
    public void testNaturalFitnessSelection()
    {
        SelectionStrategy<Object> selector = new TournamentSelection(0.7d);
        List<EvaluatedCandidate<String>> population = new ArrayList<EvaluatedCandidate<String>>(4);
        EvaluatedCandidate<String> steve = new EvaluatedCandidate<String>("Steve", 10.0);
        EvaluatedCandidate<String> mary = new EvaluatedCandidate<String>("Mary", 9.1);
        EvaluatedCandidate<String> john = new EvaluatedCandidate<String>("John", 8.4);
        EvaluatedCandidate<String> gary = new EvaluatedCandidate<String>("Gary", 6.2);
        population.add(steve);
        population.add(mary);
        population.add(john);
        population.add(gary);
        List<String> selection = selector.select(population, true, 2, new Random());
        assert selection.size() == 2 : "Selection size is " + selection.size() + ", should be 2.";
    }


    @Test
    public void testNonNaturalFitnessSelection()
    {
        SelectionStrategy<Object> selector = new TournamentSelection(0.7d);
        List<EvaluatedCandidate<String>> population = new ArrayList<EvaluatedCandidate<String>>(4);
        EvaluatedCandidate<String> gary = new EvaluatedCandidate<String>("Gary", 6.2);
        EvaluatedCandidate<String> john = new EvaluatedCandidate<String>("John", 8.4);
        EvaluatedCandidate<String> mary = new EvaluatedCandidate<String>("Mary", 9.1);
        EvaluatedCandidate<String> steve = new EvaluatedCandidate<String>("Steve", 10.0);
        population.add(gary);
        population.add(john);
        population.add(mary);
        population.add(steve);
        List<String> selection = selector.select(population, false, 2, new Random());
        assert selection.size() == 2 : "Selection size is " + selection.size() + ", should be 2.";
    }



    /**
     * The probability of selecting the fitter of two candidates must be greater than 0.5 to be
     * useful (if it is not, there is no selection pressure, or the pressure is in favour of weaker
     * candidates, which is counter-productive) .  This test ensures that an appropriate exception
     * is thrown if the probability is 0.5 or less.  Not throwing an exception is an error because
     * it permits undetected bugs in evolutionary programs.
     */
    @Test(expectedExceptions = IllegalArgumentException.class)
    public void testProbabilityTooLow()
    {
        new TournamentSelection(0.5d);
    }


    /**
     * The probability of selecting the fitter of two candidates must be less than 1.  A probability
     * greater than 1 is invalid and a probability equal to 1 removes any chance of weaker candidates
     * surviving.  This test ensures that an appropriate exception is thrown if the probability is
     * 1 or more.  Not throwing an exception is an error because it permits undetected bugs in
     * evolutionary programs.
     */
    @Test(expectedExceptions = IllegalArgumentException.class)
    public void testProbabilityTooHigh()
    {
        new TournamentSelection(1d);
    }
}
