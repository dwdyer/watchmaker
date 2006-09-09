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
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import uk.co.dandyer.maths.random.RandomSequence;
import uk.co.dandyer.maths.random.Constant;

/**
 * Generic base class for cross-over implementations.  Supports all
 * cross-over processes that operate on a pair of parent candidates.
 * @author Daniel Dyer
 */
public abstract class AbstractCrossover<T> implements EvolutionaryProcess<T>
{
    private final RandomSequence<Integer> crossoverPointsVariable;

    /**
     * Default is single-point cross-over.
     */
    protected AbstractCrossover()
    {
        this(1);
    }


    /**
     * @param crossoverPoints The constant number of cross-over points
     * to use for all cross-over operations.
     */
    protected AbstractCrossover(int crossoverPoints)
    {
        this(new Constant<Integer>(crossoverPoints));
    }


    /**
     * @param crossoverPointsVariable A random variable that provides a number
     * of cross-over points for each cross-over operation.
     */
    protected AbstractCrossover(RandomSequence<Integer> crossoverPointsVariable)
    {
        this.crossoverPointsVariable = crossoverPointsVariable;
    }

    /**
     * @param <S> A more specific type restriction than that associated
     * with this class (T).  Ensures that the returned list is of the appropriate
     * type even when dealing with sub-classes of T.
     */
    public <S extends T> List<S> apply(List<S> population, Random rng)
    {
        List<S> result = new ArrayList<S>(population.size());
        Iterator<S> iterator = population.iterator();
        while (iterator.hasNext())
        {
            S parent1 = iterator.next();
            S parent2 = iterator.next();
            int crossoverPoints = crossoverPointsVariable.nextValue();
            result.addAll(reproduce(parent1, parent2, crossoverPoints, rng));
        }
        return result;
    }

    protected abstract <S extends T> List<S> reproduce(S parent1,
                                                       S parent2,
                                                       int numberOfCrossoverPoints,
                                                       Random rng);
}
