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
package uk.co.dandyer.watchmaker.framework.selection;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import uk.co.dandyer.watchmaker.framework.SelectionStrategy;
import uk.co.dandyer.watchmaker.framework.Pair;

/**
 * An alternative to {@link uk.co.dandyer.watchmaker.framework.selection.RouletteWheelSelection} as a fitness-proportionate selection
 * strategy.  Ensures that the frequency of selection for each candidate is consistent
 * with its expected frequency of selection.
 * @author Daniel Dyer
 */
public class StochasticUniversalSampling implements SelectionStrategy
{
    public <T> List<T> select(List<Pair<T, Double>> population,
                              int selectionSize,
                              Random rng)
    {
        assert !population.isEmpty() : "Cannot select from an empty population.";

        // Calculate the sum of all fitness values.
        double aggregateFitness = 0;
        for (Pair<T, Double> candidate : population)
        {
            aggregateFitness += candidate.getSecond();
        }

        List<T> selection = new ArrayList<T>(selectionSize);
        // Pick a random offset between 0 and 1 as the starting point for selection.
        double startOffset = rng.nextDouble();
        double cumulativeExpectation = 0;
        int index = 0;
        for (Pair<T, Double> candidate : population)
        {
            // Calculate the number of times this candidate is expected to
            // be selected on average and add it to the cumulative total
            // of expected frequencies.
            cumulativeExpectation += candidate.getSecond() / aggregateFitness * selectionSize;

            // If f is the expected frequency, the candidate will be selected at
            // least as often as floor(f) and at most as often as ceil(f). The
            // actual count depends on the random starting offset.
            while (cumulativeExpectation > startOffset + index)
            {
                selection.add(candidate.getFirst());
                index++;
            }
        }
        assert selection.size() == selectionSize : "Wrong number of candidates selected.";
        return selection;
    }
}
