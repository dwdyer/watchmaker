package uk.co.dandyer.maths.random;

/**
 * Convenience implementation of {@link RandomSequence} that always
 * returns the same value.
 * @author Daniel Dyer
 */
public class Constant<T extends Number> implements RandomSequence<T>
{
    private final T constant;

    public Constant(T constant)
    {
        this.constant = constant;
    }

    public T nextValue()
    {
        return constant;
    }
}
