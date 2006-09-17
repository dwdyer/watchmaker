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
package uk.co.dandyer.watchmaker.framework;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Random;

/**
 * Generic evolutionary algorithm engine for evolution that runs on a single host.
 * @author Daniel Dyer
 * @param <T> The type of entity that is to be evolved.
 */
public class StandaloneEvolutionEngine<T> extends AbstractEvolutionEngine<T>
{
    private final FitnessEvaluator<? super T> fitnessEvaluator;
    private final Comparator<EvaluatedCandidate<?>> fitnessComparator;

    public StandaloneEvolutionEngine(CandidateFactory<? extends T> candidateFactory,
                                     List<EvolutionaryOperator<? super T>> evolutionPipeline,
                                     FitnessEvaluator<? super T> fitnessEvaluator,
                                     SelectionStrategy selectionStrategy,
                                     Random rng)
    {
        super(candidateFactory, evolutionPipeline, selectionStrategy, rng);
        this.fitnessEvaluator = fitnessEvaluator;
        this.fitnessComparator = new CandidateFitnessComparator(fitnessEvaluator.isFitnessNormalised());
    }

    /**
     * Takes a population, assigns a fitness score to each member and returns
     * the members with their scores attached, sorted in descending order of
     * fitness (descending order of fitness score for normalised scores, ascending
     * order of scores for de-normalised scores).
     */
    protected List<EvaluatedCandidate<T>> evaluatePopulation(List<T> population)
    {
        List<EvaluatedCandidate<T>> evaluatedPopulation = new ArrayList<EvaluatedCandidate<T>>(population.size());
        for (T candidate : population)
        {
            evaluatedPopulation.add(new EvaluatedCandidate<T>(candidate,
                                                              fitnessEvaluator.getFitness(candidate)));
        }
        // Sort candidates in descending order according to fitness.
        Collections.sort(evaluatedPopulation, fitnessComparator);
        return evaluatedPopulation;
    }
}
