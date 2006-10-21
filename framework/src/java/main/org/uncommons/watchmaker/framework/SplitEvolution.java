package org.uncommons.watchmaker.framework;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import org.uncommons.maths.ConstantSequence;
import org.uncommons.maths.NumberSequence;

/**
 * Compound evolutionary operator that allows the evolution of a population
 * to be split into two separate streams.  A percentage of the population
 * will be evolved according to one specified operator and the remainder
 * according to another operator.  When both streams have been executed, the
 * resulting offspring will be returned as a single combined population.
 *
 * This kind of separation is common in a genetic programming context where,
 * for example, 10% of the population is mutated and the remaining 90%
 * undergoes cross-over independently.
 *
 * To split evolution into more than two streams, multiple SplitEvolution operators
 * can be combined.
 * By combining SplitEvolution operators with {@link EvolutionPipeline} operators,
 * elaborate evolutionary schemes can be constructed.
 *
 * @author Daniel Dyer
 */
public class SplitEvolution<T> implements EvolutionaryOperator<T>
{
    private final EvolutionaryOperator<? super T> operator1;
    private final EvolutionaryOperator<? super T> operator2;
    private final NumberSequence<Double> weightVariable;

    /**
     * @param weight The proportion (as a real number between zero and 1 exclusive)
     * of the population that will be evolved by <code>operator1</code>.  The
     * remainder will be evolved by <code>operator2</code>.
     */
    public SplitEvolution(EvolutionaryOperator<? super T> operator1,
                          EvolutionaryOperator<? super T> operator2,
                          double weight)
    {
        this(operator1, operator2, new ConstantSequence<Double>(weight));
        if (weight <= 0 || weight >= 1)
        {
            throw new IllegalArgumentException("Split ratio must be greater than 0 and less than 1.");
        }
    }


    /**
     * @param weightVariable A random variable that provides the ratio for
     * dividing the population between the two evolutionary streams.  Must
     * only generate values in the range 0 &lt; ratio &lt; 1.
     */
    public SplitEvolution(EvolutionaryOperator<? super T> operator1,
                          EvolutionaryOperator<? super T> operator2,
                          NumberSequence<Double> weightVariable)
    {
        this.operator1 = operator1;
        this.operator2 = operator2;
        this.weightVariable = weightVariable;
    }


    /**
     * Applies one evolutionary operator to part of the population and another
     * to the remainder.  Returns a list combining the output of both.
     */
    public <S extends T> List<S> apply(List<S> selectedCandidates, Random rng)
    {
        double ratio = weightVariable.nextValue();
        int size = (int) Math.round(ratio * selectedCandidates.size());
        List<S> list1 = selectedCandidates.subList(0, size);
        List<S> list2 = selectedCandidates.subList(size, selectedCandidates.size());
        List<S> result = new ArrayList<S>(selectedCandidates.size());
        result.addAll(operator1.apply(list1, rng));
        result.addAll(operator2.apply(list2, rng));
        return result;
    }
}
