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
import java.util.List;
import java.util.Random;
import org.uncommons.watchmaker.framework.EvolutionaryOperator;

/**
 * <p>A higher-order evolutionary operator that is applied to populations made
 * up of lists.  In such populations, each candidate solution is itself a list
 * and this operator is applied to the list contents rather than the candidate.
 * It is analogous to the map function in functional programming languages.</p>
 *
 * <p>For example, if the evolved population consists of candidates that are
 * lists of strings, we could use a ListOperator to wrap an operator of type
 * String and convert it to an operator that works with lists of Strings.</p>
 *
 * @param <T> The element type of the lists to be mutated.
 * @author Daniel Dyer
 */
public class ListOperator <T> implements EvolutionaryOperator<List<T>>
{
    private final EvolutionaryOperator<T> delegate;

    /**
     * @param delegate The evolutionary operator that will be applied to each
     * list candidate.
     */
    public ListOperator(EvolutionaryOperator<T> delegate)
    {
        this.delegate = delegate;
    }


    /**
     * Applies the configured operator to each list candidate, operating on the elements
     * that make up a candidate rather than on the list of candidates.
     * candidates and returns the results.
     * @param selectedCandidates A list of list candidates.
     * @param rng A source of randomness.
     * @return The result of applying the configured operator to each element
     * in each list candidates.
     */
    public List<List<T>> apply(List<List<T>> selectedCandidates, Random rng)
    {
        List<List<T>> output = new ArrayList<List<T>>(selectedCandidates.size());
        for (List<T> item : selectedCandidates)
        {
            output.add(delegate.apply(item, rng));
        }
        return output;
    }
}
