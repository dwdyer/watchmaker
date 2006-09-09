package uk.co.dandyer.watchmaker.framework;

import java.util.List;
import java.util.Random;

/**
 * An evolutionary process is a function that takes a population of
 * candidates as an argument and returns a new population that is the
 * result of applying a transformation to the original population.
 * @author Daniel Dyer
 */
public interface EvolutionaryProcess<T>
{
    /**
     * @param <S> A more spefic type restriction than the one specified
     * for this class.  Allows the operation to be applied to sub-classes
     * of T and still return a list of the appropriate type.
     */
    <S extends T> List<S> apply(List<S> population, Random rng);
}
