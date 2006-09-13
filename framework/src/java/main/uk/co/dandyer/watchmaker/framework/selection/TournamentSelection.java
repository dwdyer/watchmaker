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
 * @author Daniel Dyer
 */
public class TournamentSelection implements SelectionStrategy
{
    private final double selectionProbability;

    /**
     * @param selectionProbability The probability that the fitter of two randomly
     * chosen candidates will be selected.  Since this is a probability it must be
     * between 0.0 and 1.0.  This implementation adds the further restriction that
     * the probability must be greater than 0.5 since any lower value would favour
     * weaker candidates over strong ones, negating the "survival of the fittest"
     * aspect of the evolutionary algorithm.
     */
    public TournamentSelection(double selectionProbability)
    {
        if (selectionProbability <= 0.5 || selectionProbability >= 1.0)
        {
            throw new IllegalArgumentException("Selection threshold must be greater than 0.5 and less than 1.0.");
        }
        this.selectionProbability = selectionProbability;
    }


    public <T> List<T> select(List<Pair<T, Double>> population,
                              int selectionSize,
                              Random rng)
    {
        List<T> selection = new ArrayList<T>(selectionSize);
        for (int i = 0; i < selectionSize; i++)
        {
            // Pick two candidates at random.
            Pair<T, Double> candidate1 = population.get(rng.nextInt(population.size()));
            Pair<T, Double> candidate2 = population.get(rng.nextInt(population.size()));

            // Use a random value to decide wether to select the fitter individual or the weaker one.
            double value = rng.nextDouble();
            if (value < selectionProbability)
            {
                // Select the fitter candidate.
                selection.add(candidate2.getSecond() > candidate1.getSecond() ? candidate2.getFirst() : candidate1.getFirst());
            }
            else
            {
                // Select the less fit candidate.
                selection.add(candidate2.getSecond() > candidate1.getSecond() ? candidate1.getFirst() : candidate2.getFirst());
            }
        }
        return selection;
    }
}
