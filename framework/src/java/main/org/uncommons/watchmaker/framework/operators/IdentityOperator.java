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
 * Evolutionary operator that simply returns the selected candidates unaltered.
 * This can be useful when combined with {@link SplitEvolution} so that a
 * proportion of the selected candidates can be copied unaltered into the next
 * generation while the remainder are evolved.
 * @param <T> The type of evolvable entity that this operator is used with.
 * @author Daniel Dyer
 */
public class IdentityOperator<T> implements EvolutionaryOperator<T>
{
    /**
     * Returns the selected candidates unaltered.
     * @param selectedCandidates The candidates to "evolve" (or do
     * nothing to in this case). 
     * @param rng A source of randomness (not used).
     * @return The unaltered candidates.
     */
    public List<T> apply(List<T> selectedCandidates, Random rng)
    {
        return new ArrayList<T>(selectedCandidates);
    }
}
