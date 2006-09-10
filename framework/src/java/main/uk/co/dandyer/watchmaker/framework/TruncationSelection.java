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
import java.util.Comparator;
import java.util.List;
import java.util.Random;

/**
 * Implements selection of <i>n</i> candidates from a population by simply
 * selecting the <i>n</i> candidates with the highest fitness scores (the
 * rest are discarded).  A candidate is never selected more than once.
 * @author Daniel Dyer
 */
public class TruncationSelection implements SelectionStrategy
{
    private final double selectionRatio;

    /**
     * @param selectionRatio The proportion of the highest ranked candidates to
     * select from the population.  The value must be positive and less than 1.
     */
    public TruncationSelection(double selectionRatio)
    {
        this.selectionRatio = selectionRatio;
    }


    /**
     * Selects the fittest candidates.  If the selectionRatio results in
     * fewer selected candidates than required, then these candidates are
     * selected multiple times to make up the shortfall.
     */
    public <T> List<T> select(List<Pair<T, Double>> population,
                              int selectionSize,
                              Random rng)
    {
        List<T> selection = new ArrayList<T>(selectionSize);

        int eligibleCount = (int) Math.round(selectionRatio * population.size());
        eligibleCount = eligibleCount > selectionSize ? selectionSize : eligibleCount;

        do
        {
            int count = Math.min(eligibleCount, selectionSize - selection.size());
            for (int i = 0; i < count; i++)
            {
                selection.add(population.get(i).getFirst());
            }
        } while (selection.size() < selectionSize);
        return selection;
    }
}