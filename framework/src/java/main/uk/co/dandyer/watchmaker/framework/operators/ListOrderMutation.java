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
package uk.co.dandyer.watchmaker.framework.operators;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Collections;
import uk.co.dandyer.maths.ConstantSequence;
import uk.co.dandyer.maths.NumberSequence;
import uk.co.dandyer.watchmaker.framework.EvolutionaryOperator;

/**
 * A special mutation implementation that instead of changing the
 * genes of the candidate, re-orders them.  A single mutation involves
 * swapping a random element in the list with the element immediately
 * after it.  This operation can either apply a fixed number of
 * mutations to each candidate or it can draw values from a random
 * sequence, typically a poisson distribution (@see PoissonSequence),
 * to determine how many mutations to apply.
 * @author Daniel Dyer
 */
public class ListOrderMutation implements EvolutionaryOperator<List<?>>
{
    private final NumberSequence<Integer> mutationCountVariable;

    /**
     * Default is one mutation per candidate.
     */
    public ListOrderMutation()
    {
        this(1);
    }

    /**
     * @param mutationCount The constant number of mutations
     * to apply to each individual in the population.
     */
    public ListOrderMutation(int mutationCount)
    {
        this(new ConstantSequence<Integer>(mutationCount));
    }


    /**
     * @param mutationCountVariable A random variable that provides a number
     * of mutations that will be applied to each individual.
     */
    public ListOrderMutation(NumberSequence<Integer> mutationCountVariable)
    {
        this.mutationCountVariable = mutationCountVariable;
    }


    @SuppressWarnings("unchecked")
    public <S extends List<?>> List<S> apply(List<S> population, Random rng)
    {
        List<S> result = new ArrayList<S>(population.size());
        for (S candidate : population)
        {
            List<Object> newCandidate = new ArrayList<Object>(candidate);
            int mutationCount = Math.abs(mutationCountVariable.nextValue());
            for (int i = 0; i < mutationCount; i++)
            {
                // Swap a random element with the element after it in the list.
                int index = rng.nextInt(newCandidate.size());
                Collections.swap(newCandidate,
                                 index,
                                 index < newCandidate.size() - 1 ? index + 1 : 0);
            }
            result.add((S) newCandidate);
        }
        return result;
    }
}
