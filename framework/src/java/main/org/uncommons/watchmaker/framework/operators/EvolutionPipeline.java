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
 * <p>A compound evolutionary operator that applies multiple operators (of the
 * same type) in series.</p>
 *
 * <p>By combining EvolutionPipeline operators with {@link SplitEvolution} operators,
 * elaborate evolutionary schemes can be constructed.</p>
 *
 * @param <T> The type of evolved candidate that this pipeline operates on.
 * @author Daniel Dyer
 */
public class EvolutionPipeline<T> implements EvolutionaryOperator<T>
{
    private final List<EvolutionaryOperator<T>> pipeline;


    /**
     * Creates a pipeline consisting of the specified operators in
     * the order that they are supplied.
     * @param pipeline An ordered list of operators that make up the
     * pipeline.
     */
    public EvolutionPipeline(List<EvolutionaryOperator<T>> pipeline)
    {
        if (pipeline.isEmpty())
        {
            throw new IllegalArgumentException("Pipeline must contain at least one operator.");
        }
        this.pipeline = new ArrayList<EvolutionaryOperator<T>>(pipeline);
    }


    /**
     * Applies each operation in the pipeline in turn to the selection.
     * @param selectedCandidates The candidates to subjected to evolution.
     * @param rng A source of randomness used by all stochastic processes in
     * the pipeline.
     * @return A list of evolved candidates.
     */
    public List<T> apply(List<T> selectedCandidates, Random rng)
    {
        List<T> population = selectedCandidates;
        for (EvolutionaryOperator<T> operator : pipeline)
        {
            population = operator.apply(population, rng);
        }
        return population;
    }
}
