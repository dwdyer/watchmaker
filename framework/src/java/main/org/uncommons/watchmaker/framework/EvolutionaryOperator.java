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
package org.uncommons.watchmaker.framework;

import java.util.List;
import java.util.Random;

/**
 * <p>An evolutionary operator is a function that takes a population of
 * candidates as an argument and returns a new population that is the
 * result of applying a transformation to the original population.</p>
 * <p><strong>An implementation of this class must not modify any of
 * the selected candidate objects passed in.</strong>  Doing so will
 * affect the correct operation of the {@link EvolutionEngine}.  Instead
 * the operator should create and return new candidate objects.  The
 * operator is not required to create copies of unmodified individuals
 * (for efficiency these may be returned directly).</p>
 * @param <T> The type of evolvable entity that this operator accepts.
 * @author Daniel Dyer
 */
public interface EvolutionaryOperator<T>
{
    /**
     * <p>Apply the operation to each entry in the list of selected
     * candidates.  It is important to note that this method operates on
     * the list of candidates returned by the selection strategy and not
     * on the current population.  Each entry in the list (not each
     * individual - the list may contain the same individual more than
     * once) must be operated on exactly once.</p>
     *
     * <p>Implementing classes should not assume any particular ordering
     * (or lack of ordering) for the selection.  If ordering or
     * shuffling is required, it should be performed by the implementing
     * class.  The implementation should not re-order the list provided
     * but instead should make a copy of the list and re-order that.
     * The ordering of the selection should be totally irrelevant for
     * operators that process each candidate in isolation, such as mutation.
     * It should only be an issue for operators, such as cross-over, that
     * deal with multiple candidates in a single operation.</p>
     * <p><strong>The operator must not modify any of the candidates passed
     * in</strong>.  Instead it should return a list that contains evolved
     * copies of those candidates (umodified candidates can be included in
     * the results without having to be copied).</p>
     * @param selectedCandidates The individuals to evolve.
     * @param rng A source of randomness for stochastic operators (most
     * operators will be stochastic).
     * @return The evolved individuals.
     */
    List<T> apply(List<T> selectedCandidates, Random rng);
}
