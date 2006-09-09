package uk.co.dandyer.maths.random;

/**
 * Interface for providing random sequences of numbers from various
 * statistical distributions.
 * @author Daniel Dyer
 */
public interface RandomSequence<T extends Number>
{
    T nextValue();
}
