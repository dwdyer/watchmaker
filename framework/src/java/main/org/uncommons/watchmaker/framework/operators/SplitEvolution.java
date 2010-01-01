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
import java.util.Collections;
import java.util.List;
import java.util.Random;
import org.uncommons.maths.number.ConstantGenerator;
import org.uncommons.maths.number.NumberGenerator;
import org.uncommons.watchmaker.framework.EvolutionaryOperator;

/**
 * <p>Compound evolutionary operator that allows the evolution of a population
 * to be split into two separate streams.  A percentage of the population
 * will be evolved according to one specified operator and the remainder
 * according to another operator.  When both streams have been executed, the
 * resulting offspring will be returned as a single combined population.</p>
 *
 * <p>This kind of separation is common in a genetic programming context where,
 * for example, 10% of the population is mutated and the remaining 90%
 * undergoes cross-over independently.</p>
 *
 * <p>To split evolution into more than two streams, multiple SplitEvolution operators
 * can be combined.  By combining SplitEvolution operators with
 * {@link EvolutionPipeline} operators, elaborate evolutionary schemes can be
 * constructed.</p>
 *
 * @param <T> The type of evolved entity dealt with by this operator.
 * @author Daniel Dyer
 */
public class SplitEvolution<T> implements EvolutionaryOperator<T>
{
    private final EvolutionaryOperator<T> operator1;
    private final EvolutionaryOperator<T> operator2;
    private final NumberGenerator<Double> weightVariable;

    /**
     * @param operator1 The operator that will apply to the first part of the
     * population (as determined by the {@code weight} parameter).
     * @param operator2 The operator that will apply to the second part of the
     * population (as determined by the {@code weight} parameter).
     * @param weight The proportion (as a real number between zero and 1 exclusive)
     * of the population that will be evolved by {@code operator1}.  The
     * remainder will be evolved by {@code operator2}.
     */
    public SplitEvolution(EvolutionaryOperator<T> operator1,
                          EvolutionaryOperator<T> operator2,
                          double weight)
    {
        this(operator1, operator2, new ConstantGenerator<Double>(weight));
        if (weight <= 0 || weight >= 1)
        {
            throw new IllegalArgumentException("Split ratio must be greater than 0 and less than 1.");
        }
    }


    /**
     * @param operator1 The operator that will apply to the first part of the
     * population (as determined by the {@code weightVariable} parameter).
     * @param operator2 The operator that will apply to the second part of the
     * population (as determined by the {@code weightVariable} parameter).
     * @param weightVariable A random variable that provides the ratio for
     * dividing the population between the two evolutionary streams.  Must
     * only generate values in the range {@literal 0 < ratio < 1}.
     */
    public SplitEvolution(EvolutionaryOperator<T> operator1,
                          EvolutionaryOperator<T> operator2,
                          NumberGenerator<Double> weightVariable)
    {
        this.operator1 = operator1;
        this.operator2 = operator2;
        this.weightVariable = weightVariable;
    }


    /**
     * Applies one evolutionary operator to part of the population and another
     * to the remainder.  Returns a list combining the output of both.  Which
     * candidates are submitted to which stream is determined randomly.
     * @param selectedCandidates A list of the candidates that survived to be
     * eligible for evolution.
     * @param rng A source of randomness passed to each of the two delegate
     * evolutionary operators.
     * @return The combined results from the two streams of evolution.
     */
    public List<T> apply(List<T> selectedCandidates, Random rng)
    {
        double ratio = weightVariable.nextValue();
        int size = (int) Math.round(ratio * selectedCandidates.size());

        // Shuffle the collection before applying each operation so that the
        // split is not influenced by any ordering artifacts from previous
        // operations.
        List<T> selectionClone = new ArrayList<T>(selectedCandidates);
        Collections.shuffle(selectionClone, rng);

        List<T> list1 = selectionClone.subList(0, size);
        List<T> list2 = selectionClone.subList(size, selectedCandidates.size());
        List<T> result = new ArrayList<T>(selectedCandidates.size());
        result.addAll(operator1.apply(list1, rng));
        result.addAll(operator2.apply(list2, rng));
        return result;
    }
}
