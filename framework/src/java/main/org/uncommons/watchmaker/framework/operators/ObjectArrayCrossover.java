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

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import org.uncommons.maths.number.NumberGenerator;
import org.uncommons.maths.random.Probability;

/**
 * Cross-over with a configurable number of points (fixed or random) for arrays of reference types.
 * <p/>
 * @param <T> The component type of the arrays that are being evolved.
 * @author Daniel Dyer
 */
public class ObjectArrayCrossover<T> extends AbstractCrossover<T[]>
{
    /**
     * Default is single-point cross-over, applied to all parents.
     */
    public ObjectArrayCrossover()
    {
        this(1);
    }


    /**
     * Cross-over with a fixed number of cross-over points.
     * @param crossoverPoints The constant number of cross-over points
     * to use for all cross-over operations.
     */
    public ObjectArrayCrossover(int crossoverPoints)
    {
        super(crossoverPoints);
    }


    /**
     * Cross-over with a fixed number of cross-over points.  Cross-over
     * may or may not be applied to a given pair of parents depending on
     * the {@code crossoverProbability}.
     * @param crossoverPoints The constant number of cross-over points
     * to use for all cross-over operations.
     * @param crossoverProbability The probability that, once selected,
     * a pair of parents will be subjected to cross-over rather than
     * being copied, unchanged, into the output population.
     */
    public ObjectArrayCrossover(int crossoverPoints, Probability crossoverProbability)
    {
        super(crossoverPoints, crossoverProbability);
    }


    /**
     * Cross-over with a variable number of cross-over points.
     * @param crossoverPointsVariable A random variable that provides a number
     * of cross-over points for each cross-over operation.
     */
    public ObjectArrayCrossover(NumberGenerator<Integer> crossoverPointsVariable)
    {
        super(crossoverPointsVariable);
    }


    /**
     * Sets up a cross-over implementation that uses a variable number of cross-over
     * points.  Cross-over is applied to a proportion of selected parent pairs, with
     * the remainder copied unchanged into the output population.  The size of this
     * evolved proportion is controlled by the {@code crossoverProbabilityVariable}
     * parameter.
     * @param crossoverPointsVariable A variable that provides a (possibly constant,
     * possibly random) number of cross-over points for each cross-over operation.
     * @param crossoverProbabilityVariable A variable that controls the probability
     * that, once selected, a pair of parents will be subjected to cross-over rather
     * than being copied, unchanged, into the output population.
     */
    public ObjectArrayCrossover(NumberGenerator<Integer> crossoverPointsVariable,
        NumberGenerator<Probability> crossoverProbabilityVariable)
    {
        super(crossoverPointsVariable, crossoverProbabilityVariable);
    }


    @Override
    protected List<T[]> mate(T[] parent1,
        T[] parent2,
        int numberOfCrossoverPoints,
        Random rng)
    {
        if (parent1.length != parent2.length)
        {
            throw new IllegalArgumentException(
                "Cannot perform cross-over with different length parents.");
        }
        // Create the most specific-type arrays possible.
        @SuppressWarnings("unchecked")
        T[] offspring1 = (T[]) Array.newInstance(parent1.getClass().getComponentType(),
            parent1.length);
        System.arraycopy(parent1, 0, offspring1, 0, parent1.length);
        @SuppressWarnings("unchecked")
        T[] offspring2 = (T[]) Array.newInstance(parent2.getClass().getComponentType(),
            parent2.length);
        System.arraycopy(parent2, 0, offspring2, 0, parent2.length);
        // Apply as many cross-overs as required.
        Object[] temp = new Object[parent1.length];
        for (int i = 0; i < numberOfCrossoverPoints; i++)
        {
            // Cross-over index is always greater than zero and less than
            // the length of the parent so that we always pick a point that
            // will result in a meaningful cross-over.
            int crossoverIndex = (1 + rng.nextInt(parent1.length - 1));
            System.arraycopy(offspring1, 0, temp, 0, crossoverIndex);
            System.arraycopy(offspring2, 0, offspring1, 0, crossoverIndex);
            System.arraycopy(temp, 0, offspring2, 0, crossoverIndex);
        }
        List<T[]> result = new ArrayList<T[]>(2);
        result.add(offspring1);
        result.add(offspring2);
        return result;
    }
}
