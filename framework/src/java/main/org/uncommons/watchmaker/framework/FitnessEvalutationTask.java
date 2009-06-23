// ============================================================================
//   Copyright 2006-2009 Daniel W. Dyer
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
package org.uncommons.watchmaker.framework;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;

/**
 * Callable task for performing parallel fitness evaluations.
 * @param <T> The type of entity for which fitness is calculated.
 * @author Daniel Dyer
 */
class FitnessEvalutationTask<T> implements Callable<List<EvaluatedCandidate<T>>>
{
    private final FitnessEvaluator<? super T> fitnessEvaluator;
    private final List<T> candidates;
    private final List<T> population;

    /**
     * Creates a task for performing fitness evaluations.
     * @param fitnessEvaluator The fitness function used to determine candidate fitness.
     * @param candidates The candidates to evaluate.  This is a subset of
     * {@code population}.
     * @param population The entire current population.  This will include all
     * of the candidates to evaluate along with any other individuals that are
     * not being evaluated by this task.
     */
    FitnessEvalutationTask(FitnessEvaluator<? super T> fitnessEvaluator,
                           List<T> candidates,
                           List<T> population)
    {
        this.fitnessEvaluator = fitnessEvaluator;
        this.candidates = candidates;
        this.population = population;
    }


    public List<EvaluatedCandidate<T>> call()
    {
        List<EvaluatedCandidate<T>> evaluatedCandidates = new ArrayList<EvaluatedCandidate<T>>(candidates.size());
        for (T candidate : candidates)
        {
            evaluatedCandidates.add(new EvaluatedCandidate<T>(candidate,
                                                              fitnessEvaluator.getFitness(candidate, population)));
        }
        return evaluatedCandidates;
    }
}
