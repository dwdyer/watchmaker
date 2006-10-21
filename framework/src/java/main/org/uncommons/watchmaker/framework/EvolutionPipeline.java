package org.uncommons.watchmaker.framework;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * A compound evolutionary operator that applies multiple operators (of the
 * same type) in series.
 *
 * By combining EvolutionPipeline operators with {@link SplitEvolution} operators,
 * elaborate evolutionary schemes can be constructed.
 *
 * @author Daniel Dyer
 */
public class EvolutionPipeline<T> implements EvolutionaryOperator<T>
{
    private final List<EvolutionaryOperator<? super T>> pipeline;


    public EvolutionPipeline(List<EvolutionaryOperator<? super T>> pipeline)
    {
        this.pipeline = new ArrayList<EvolutionaryOperator<? super T>>(pipeline);
    }


    public EvolutionPipeline(EvolutionaryOperator<? super T>... pipeline)
    {
        this.pipeline = new ArrayList<EvolutionaryOperator<? super T>>(pipeline.length);
        for (EvolutionaryOperator<? super T> operator : pipeline)
        {
            this.pipeline.add(operator);
        }
    }


    public <S extends T> List<S> apply(List<S> selectedCandidates, Random rng)
    {
        List<S> population = selectedCandidates;
        for (EvolutionaryOperator<? super T> operator : pipeline)
        {
            population = operator.apply(population, rng);
        }
        return population;
    }
}
