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

import uk.co.dandyer.watchmaker.framework.SelectionStrategy;
import uk.co.dandyer.watchmaker.framework.Pair;
import java.util.List;
import java.util.Random;
import java.util.ArrayList;
import java.util.Iterator;

/**
 * <p>A selection strategy that is similar to fitness-proportionate selection
 * except that is uses relative fitness rather than absolute fitness in order to
 * determine the probability of selection for a given individual (i.e. the actual
 * numerical fitness values are ignored and only the ordering of the sorted
 * population is considered).</p>
 * <p>Rank selection is implemented in terms of a mapping function (@see
 * #mapRankToScore()) and delegation to a fitness-proportionate selector.  The
 * mapping function converts ranks into relative fitness scores that are used to
 * drive the delegate selector.</p>
 * @author Daniel Dyer
 */
public class RankSelection implements SelectionStrategy
{
    private final FitnessProportionateSelection delegate;

    /**
     * Creates a default rank-based selector with a linear
     * mapping function and selection frequencies that correspond
     * to expected values.
     */
    public RankSelection()
    {
        this(new StochasticUniversalSampling());
    }

    /**
     * Creates a rank-based selector with a linear mapping function and
     * configurable delegate for performing the proportionate selection.
     * @param delegate The proportionate selector that will be delegated
     * to after converting rankings into relative fitness scores.
     */
    public RankSelection(FitnessProportionateSelection delegate)
    {
        this.delegate = delegate;
    }


    public <T> List<T> select(List<Pair<T, Double>> population,
                              int selectionSize,
                              Random rng)
    {
        List<Pair<T, Double>> rankedPopulation = new ArrayList<Pair<T, Double>>(population.size());
        Iterator<Pair<T, Double>> iterator = population.iterator();
        int index = -1;
        while (iterator.hasNext())
        {
            T candidate = iterator.next().getFirst();
            rankedPopulation.add(new Pair<T, Double>(candidate, mapRankToScore(++index, population.size())));
        }
        return delegate.select(rankedPopulation, selectionSize, rng);
    }


    /**
     * <p>Maps a population index to a relative pseudo-fitness score that can be used for
     * fitness-proportionate selection.  The general contract for the mapping function
     * <code>f</code> is <code>f(rank) <= f(rank + 1)</code> for all legal values of
     * <code>rank</code>.</p>
     * <p>The default mapping function is a simple linear transformation, but this
     * can be over-ridden in sub-classes.  Alternative implementations can be linear or
     * non-linear and either normalised or de-normalised.  However, for performance reasons
     * it is preferable to return normalised scores.</p>
     * @param rank A zero-based index into the population (0 <= rank < populationSize).
     * @param populationSize The number of individuals in the population.
     */
    protected double mapRankToScore(int rank, int populationSize)
    {
        return populationSize - rank;
    }
}
