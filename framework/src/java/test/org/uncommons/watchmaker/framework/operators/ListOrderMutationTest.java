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
package org.uncommons.watchmaker.framework.operators;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.testng.Reporter;
import org.testng.annotations.Test;
import org.uncommons.watchmaker.framework.FrameworkTestUtils;

/**
 * Unit test to validate the operation of the {@link ListOrderMutation} operator.
 * @author Daniel Dyer
 */
public class ListOrderMutationTest
{
    @Test
    public void testMutation()
    {
        ListOrderMutation<Character> operator = new ListOrderMutation<Character>();
        List<Character> candidate = new ArrayList<Character>(5);
        candidate.add('a');
        candidate.add('b');
        candidate.add('c');
        candidate.add('d');
        candidate.add('e');
        List<List<Character>> population = new ArrayList<List<Character>>(1);
        population.add(candidate);
        List<List<Character>> mutatedPopulation = operator.apply(population, FrameworkTestUtils.getRNG());
        assert mutatedPopulation.size() == population.size() : "Population size should be unchanged.";
        List<Character> mutatedCandidate = mutatedPopulation.get(0);
        Reporter.log("Original: " + Arrays.toString(candidate.toArray(new Character[candidate.size()])));
        Reporter.log("Mutation: " + Arrays.toString(mutatedCandidate.toArray(new Character[mutatedCandidate.size()])));
        assert mutatedCandidate.size() == candidate.size() : "Mutated candidate should be same length as original.";
        // Mutated candidate should have same elements but in a different order.
        int matchingPositions = 0;
        for (int i = 0; i < candidate.size(); i++)
        {
            if (candidate.get(i).equals(mutatedCandidate.get(i)))
            {
                ++matchingPositions;
            }
            else
            {
                // If positions don't match, an adjacent character should be a match.
                int nextPosition = (i + 1) % candidate.size();
                int previousPosition = ((i - 1) + candidate.size()) % candidate.size();
                boolean matchAdjacent = candidate.get(i).equals(mutatedCandidate.get(nextPosition))
                                        ^ candidate.get(i).equals(mutatedCandidate.get(previousPosition));
                assert matchAdjacent : "Mutated characters not in expected positions.";
            }
        }
        assert matchingPositions == candidate.size() - 2 : "All but 2 positions should be unchanged.";
    }
}
