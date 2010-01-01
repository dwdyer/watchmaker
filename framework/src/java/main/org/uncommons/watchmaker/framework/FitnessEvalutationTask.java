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
import java.util.concurrent.Callable;

/**
 * Callable task for performing parallel fitness evaluations.
 * @param <T> The type of entity for which fitness is calculated.
 * @author Daniel Dyer
 */
class FitnessEvalutationTask<T> implements Callable<EvaluatedCandidate<T>>
{
    private final FitnessEvaluator<? super T> fitnessEvaluator;
    private final T candidate;
    private final List<T> population;

    /**
     * Creates a task for performing fitness evaluations.
     * @param fitnessEvaluator The fitness function used to determine candidate fitness.
     * @param candidate The candidate to evaluate.
     * @param population The entire current population.  This will include all
     * of the candidates to evaluate along with any other individuals that are
     * not being evaluated by this task.
     */
    FitnessEvalutationTask(FitnessEvaluator<? super T> fitnessEvaluator,
                           T candidate,
                           List<T> population)
    {
        this.fitnessEvaluator = fitnessEvaluator;
        this.candidate = candidate;
        this.population = population;
    }


    public EvaluatedCandidate<T> call()
    {
        return new EvaluatedCandidate<T>(candidate,
                                         fitnessEvaluator.getFitness(candidate, population));
    }
}
