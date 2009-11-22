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
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

/**
 * <p>Fitness evaluation strategy that spreads the workload across multiple
 * threads.  Fitness evaluations are performed in parallel on multi-processor, multi-core
 * and hyper-threaded machines.</p>
 *
 * <p>Evolutionary programs that execute in a restricted/managed
 * environment that does not permit applications to manage their own
 * threads should use {@link SequentialFitnessEvaluation} instead.</p>
 * 
 * @author Daniel Dyer
 */
class ConcurrentFitnessEvaluation implements FitnessEvaluationStrategy
{
    // A single multi-threaded worker is shared among multiple evolution engine instances.
    private static final FitnessEvaluationWorker WORKER = new FitnessEvaluationWorker();


    /**
     * {@inheritDoc}
     */
    public <T> List<EvaluatedCandidate<T>> evaluatePopulation(List<T> population,
                                                              FitnessEvaluator<? super T> fitnessEvaluator)
    {
        List<EvaluatedCandidate<T>> evaluatedPopulation = new ArrayList<EvaluatedCandidate<T>>(population.size());

        // Divide the required number of fitness evaluations equally among the
        // available processors and coordinate the threads so that we do not
        // proceed until all threads have finished processing.
        try
        {
            List<T> unmodifiablePopulation = Collections.unmodifiableList(population);
            List<Future<EvaluatedCandidate<T>>> results = new ArrayList<Future<EvaluatedCandidate<T>>>(population.size());
            // Submit tasks for execution and wait until all threads have finished fitness evaluations.
            for (T candidate : population)
            {
                results.add(WORKER.submit(new FitnessEvalutationTask<T>(fitnessEvaluator,
                                                                        candidate,
                                                                        unmodifiablePopulation)));
            }
            for (Future<EvaluatedCandidate<T>> result : results)
            {
                evaluatedPopulation.add(result.get());
            }
            assert evaluatedPopulation.size() == population.size() : "Wrong number of evaluated candidates.";
        }
        catch (ExecutionException ex)
        {
            throw new IllegalStateException("Fitness evaluation task execution failed.", ex);
        }
        catch (InterruptedException ex)
        {
            // Restore the interrupted status, allows methods further up the call-stack
            // to abort processing if appropriate.
            Thread.currentThread().interrupt();
        }

        return evaluatedPopulation;
    }
}
