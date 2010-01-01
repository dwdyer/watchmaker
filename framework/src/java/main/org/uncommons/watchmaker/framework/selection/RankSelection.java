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
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import org.uncommons.watchmaker.framework.EvaluatedCandidate;
import org.uncommons.watchmaker.framework.SelectionStrategy;

/**
 * <p>A selection strategy that is similar to fitness-proportionate selection
 * except that is uses relative fitness rather than absolute fitness in order to
 * determine the probability of selection for a given individual (i.e. the actual
 * numerical fitness values are ignored and only the ordering of the sorted
 * population is considered).</p>
 * <p>Rank selection is implemented in terms of a mapping function ({@link
 * #mapRankToScore(int, int)}) and delegation to a fitness-proportionate selector.  The
 * mapping function converts ranks into relative fitness scores that are used to
 * drive the delegate selector.</p>
 * @author Daniel Dyer
 */
public class RankSelection implements SelectionStrategy<Object>
{
    private final SelectionStrategy<Object> delegate;

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
    public RankSelection(SelectionStrategy<Object> delegate)
    {
        this.delegate = delegate;
    }


    /**
     * {@inheritDoc}
     */
    public <S> List<S> select(List<EvaluatedCandidate<S>> population,
                              boolean naturalFitnessScores,
                              int selectionSize,
                              Random rng)
    {
        List<EvaluatedCandidate<S>> rankedPopulation = new ArrayList<EvaluatedCandidate<S>>(population.size());
        Iterator<EvaluatedCandidate<S>> iterator = population.iterator();
        int index = -1;
        while (iterator.hasNext())
        {
            S candidate = iterator.next().getCandidate();
            rankedPopulation.add(new EvaluatedCandidate<S>(candidate,
                                                           mapRankToScore(++index,
                                                                          population.size())));
        }
        return delegate.select(rankedPopulation, true, selectionSize, rng);
    }


    /**
     * <p>Maps a population index to a relative pseudo-fitness score that can be used for
     * fitness-proportionate selection.  The general contract for the mapping function
     * {@code f} is: {@code f(rank) >= f(rank + 1)} for all legal values of
     * {@code rank}, assuming natural scores.</p>
     * <p>The default mapping function is a simple linear transformation, but this
     * can be over-ridden in sub-classes.  Alternative implementations can be linear or
     * non-linear and either natural or non-natural.</p>
     * @param rank A zero-based index into the population
     * {@code (0 <= rank < populationSize)}.
     * @param populationSize The number of individuals in the population.
     * @return {@code populationSize - rank}
     */
    protected double mapRankToScore(int rank, int populationSize)
    {
        return populationSize - rank;
    }


    /**
     * {@inheritDoc}
     */
    @Override
    public String toString()
    {
        return "Rank Selection";
    }
}
