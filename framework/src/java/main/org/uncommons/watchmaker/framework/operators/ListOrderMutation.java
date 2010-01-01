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
import java.util.Collections;
import java.util.List;
import java.util.Random;
import org.uncommons.maths.number.ConstantGenerator;
import org.uncommons.maths.number.NumberGenerator;
import org.uncommons.watchmaker.framework.EvolutionaryOperator;

/**
 * A special mutation implementation that instead of changing the
 * genes of the candidate, re-orders them.  A single mutation involves
 * swapping a random element in the list with the element immediately
 * after it.  This operation can either apply a fixed number of
 * mutations to each candidate or it can draw values from a random
 * sequence, typically a poisson distribution (see
 * {@link org.uncommons.maths.random.PoissonGenerator}), to determine how
 * many mutations to apply.
 * @param <T> The component type of the lists that are mutated.
 * @author Daniel Dyer
 */
public class ListOrderMutation<T> implements EvolutionaryOperator<List<T>>
{
    private final NumberGenerator<Integer> mutationCountVariable;
    private final NumberGenerator<Integer> mutationAmountVariable;

    /**
     * Default is one mutation per candidate.
     */
    public ListOrderMutation()
    {
        this(1, 1);
    }

    /**
     * @param mutationCount The constant number of mutations
     * to apply to each individual in the population.
     * @param mutationAmount The constant number of positions by
     * which a list element will be displaced as a result of mutation.
     */
    public ListOrderMutation(int mutationCount, int mutationAmount)
    {
        this(new ConstantGenerator<Integer>(mutationCount),
             new ConstantGenerator<Integer>(mutationAmount));
    }


    /**
     * Typically the mutation count will be from a Poisson distribution.
     * The mutation amount can be from any discrete probability distribution
     * and can include negative values.
     * @param mutationCount A random variable that provides a number
     * of mutations that will be applied to each individual.
     * @param mutationAmount A random variable that provides a number
     * of positions by which to displace an element when mutating.
     */
    public ListOrderMutation(NumberGenerator<Integer> mutationCount,
                             NumberGenerator<Integer> mutationAmount)
    {
        this.mutationCountVariable = mutationCount;
        this.mutationAmountVariable = mutationAmount;
    }


    public List<List<T>> apply(List<List<T>> selectedCandidates, Random rng)
    {
        List<List<T>> result = new ArrayList<List<T>>(selectedCandidates.size());
        for (List<T> candidate : selectedCandidates)
        {
            List<T> newCandidate = new ArrayList<T>(candidate);
            int mutationCount = Math.abs(mutationCountVariable.nextValue());
            for (int i = 0; i < mutationCount; i++)
            {
                int fromIndex = rng.nextInt(newCandidate.size());
                int mutationAmount = mutationAmountVariable.nextValue();
                int toIndex = (fromIndex + mutationAmount) % newCandidate.size();
                if (toIndex < 0)
                {
                    toIndex += newCandidate.size();
                }
                // Swap the randomly selected element with the one that is the
                // specified displacement distance away.
                Collections.swap(newCandidate, fromIndex, toIndex);
            }
            result.add(newCandidate);
        }
        return result;
    }
}
