package org.uncommons.watchmaker.swing;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;
import org.uncommons.watchmaker.framework.EvolutionObserver;
import org.uncommons.watchmaker.framework.PopulationData;

/**
 * Limits the update rate of a Swing-based EvolutionObserver.
 * <p/>
 * @param <T> the population type
 * @author Gili Tzabari
 */
public class SwingEvolutionObserver<T> implements EvolutionObserver<T>
{
    private final EvolutionObserver<T> delegate;
    private final long delay;
    private final TimeUnit unit;
    private final ScheduledExecutorService timer =
        Executors.newScheduledThreadPool(1, new ThreadFactory()
    {
        private final ThreadFactory delegate = Executors.defaultThreadFactory();


        public Thread newThread(Runnable r)
        {
            Thread result = delegate.newThread(r);
            result.setDaemon(true);
            return result;
        }
    });
    private final AtomicReference<PopulationData<? extends T>> latestPopulation =
        new AtomicReference<PopulationData<? extends T>>();


    /**
     * Creates a new SwingEvolutionObserver.
     * 
     * @param delegate the underlying EvolutionObserver to update
     * @param delay the amount of time to wait before updating the underlying
     *   EvolutionObserver
     * @param unit the time unit of delay
     * @throws NullPointerException if delegate or unit are null
     * @throws IllegalArgumentException if delay is negative
     */
    public SwingEvolutionObserver(EvolutionObserver<T> delegate, long delay,
                                  TimeUnit unit)
    {
        if (delegate == null)
            throw new NullPointerException("delegate may not be null");
        if (unit == null)
            throw new NullPointerException("unit may not be null");
        if (delay < 0)
            throw new IllegalArgumentException("delay may not be negative: " + delay);

        this.delegate = delegate;
        this.delay = delay;
        this.unit = unit;
    }


    public <S extends T> void populationUpdate(PopulationData<S> populationData)
    {
        if (latestPopulation.getAndSet(populationData) != null)
        {
            // An update is already scheduled
            return;
        }

        // Schedule an update
        timer.schedule(new Runnable()
        {
            public void run()
            {
                delegate.populationUpdate(latestPopulation.getAndSet(null));
            }
        }, delay, unit);
    }
}
