package uk.co.dandyer.maths.random;

/**
 * Exception thrown by {@link uk.co.dandyer.maths.random.SeedGenerator} implementations when
 * they are unable to generate a new seed for an RNG.
 * @author Daniel Dyer
 */
public class SeedException extends Exception
{
    public SeedException(String message)
    {
        super(message);
    }

    public SeedException(String message, Throwable cause)
    {
        super(message, cause);
    }
}
