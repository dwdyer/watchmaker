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

/**
 * <p>Fitness evaluation strategy that performs all work synchronously on the request thread.
 * This implementation does not take advantage of the parallelism offered by multi-processor,
 * multi-core or hyper-threaded machines.</p>
 *
 * <p>This strategy is suitable for restricted/managed environments where it is not permitted
 * for applications to manage their own threads. Most applications should use the
 * {@link ConcurrentFitnessEvaluation} instead.</p>
 *
 * @author Daniel Dyer
 */
class SequentialFitnessEvaluation implements FitnessEvaluationStrategy
{
    /**
     * {@inheritDoc}
     */
    public <T> List<EvaluatedCandidate<T>> evaluatePopulation(List<T> population,
                                                              FitnessEvaluator<? super T> fitnessEvaluator)
    {
        List<EvaluatedCandidate<T>> evaluatedPopulation = new ArrayList<EvaluatedCandidate<T>>(population.size());
        for (T candidate : population)
        {
            evaluatedPopulation.add(new EvaluatedCandidate<T>(candidate,
                                                              fitnessEvaluator.getFitness(candidate,
                                                                                          population)));
        }
        return evaluatedPopulation;
    }
}
