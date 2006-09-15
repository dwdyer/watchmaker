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
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import uk.co.dandyer.watchmaker.framework.Pair;

/**
 * <p>Implements selection of <i>n</i> candidates from a population by selecting
 * <i>n</i> candidates at random where the probability of each candidate getting
 * selected is proportional to its fitness score.  This is analogous to each
 * candidate be assigned an area on a roulette wheel proportionate to its fitness
 * and the wheel being spun <n>i</n> times.  Candidates may be selected more than
 * once.</p>
 *
 * <p>In some instances, particularly with small population sizes, the randomness
 * of selection may result in excessively high occurances of particular candidates.
 * If this is a problem, {@link StochasticUniversalSampling} provides an alternative
 * fitness-proportionate strategy for selection.</p>
 *
 * @author Daniel Dyer
 */
public class RouletteWheelSelection extends FitnessProportionateSelection
{
    protected <T> List<T> fpSelect(List<Pair<T, Double>> normalisedPopulation,
                                   int selectionSize,
                                   Random rng)
    {
        // Record the cumulative fitness scores.  It doesn't matter whether the
        // population is sorted or not.  We will use these cumulative scores to work out
        // an index into the population.  The cumulative array itself is implicitly
        // sorted since each element must be greater than the previous one.  The
        // numerical difference between an element and the previous one is directly
        // proportional to the probability of the corresponding candidate in the population
        // being selected.
        double[] cumulativeFitnesses = new double[normalisedPopulation.size()];
        cumulativeFitnesses[0] = normalisedPopulation.get(0).getSecond();
        for (int i = 1; i < normalisedPopulation.size(); i++)
        {
            cumulativeFitnesses[i] = cumulativeFitnesses[i - 1] + normalisedPopulation.get(i).getSecond();
        }

        List<T> selection = new ArrayList<T>(selectionSize);
        for (int i = 0; i < selectionSize; i++)
        {
            double randomFitness = rng.nextDouble() * cumulativeFitnesses[cumulativeFitnesses.length - 1];
            int index = Arrays.binarySearch(cumulativeFitnesses, randomFitness);
            if (index < 0)
            {
                // Convert negative insertion point to array index.
                index = Math.abs(index + 1);
            }
            selection.add(normalisedPopulation.get(index).getFirst());
        }
        return selection;
    }
}