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
 * Strategy interface for "natural" selection.
 * @param <T> The type of evolved entity that we are selecting.
 * @author Daniel Dyer
 */
public interface SelectionStrategy<T>
{
    /**
     * <p>Select the specified number of candidates from the population.
     * Implementations may assume that the population is sorted in descending
     * order according to fitness (so the fittest individual is the first item
     * in the list).</p>
     * <p>It is an error to call this method with an empty or null population.</p>
     * @param <S> The type of evolved entity that we are selecting, a sub-type of T.
     * @param population The population from which to select.
     * @param naturalFitnessScores Whether higher fitness values represent fitter
     * individuals or not.
     * @param selectionSize The number of individual selections to make (not necessarily
     * the number of distinct candidates to select, since the same individual may
     * potentially be selected more than once).
     * @param rng Source of randomness for stochastic selection strategies.
     * @return A list containing the selected candidates.  Some individual candidates may
     * potentially have been selected multiple times.
     */
    <S extends T> List<S> select(List<EvaluatedCandidate<S>> population,
                                 boolean naturalFitnessScores,
                                 int selectionSize,
                                 Random rng);
}
