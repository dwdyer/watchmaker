package org.uncommons.watchmaker.swing;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;
import org.uncommons.util.concurrent.ConfigurableThreadFactory;
import org.uncommons.watchmaker.framework.EvolutionObserver;
import org.uncommons.watchmaker.framework.PopulationData;

/**
 * Limits the update rate of a Swing-based {@link EvolutionObserver}.
 * 
 * @param <T> The population type.
 * @author Gili Tzabari
 */
public class SwingEvolutionObserver<T> implements EvolutionObserver<T>
{
    private final EvolutionObserver<T> delegate;
    private final long delay;
    private final TimeUnit unit;
    private final ConfigurableThreadFactory threadFactory = new ConfigurableThreadFactory("SwingEvolutionObserver",
                                                                                          Thread.NORM_PRIORITY,
                                                                                          true);
    private final ScheduledExecutorService timer =  Executors.newScheduledThreadPool(1, threadFactory);

    private final AtomicReference<PopulationData<? extends T>> latestPopulation
        = new AtomicReference<PopulationData<? extends T>>();


    /**
     * Creates a new SwingEvolutionObserver.
     * 
     * @param delegate The underlying EvolutionObserver to update.
     * @param delay The amount of time to wait before updating the underlying {@link EvolutionObserver}.
     * @param unit The time unit of delay.
     * @throws NullPointerException If delegate or unit are null.
     * @throws IllegalArgumentException If delay is negative.
     */
    public SwingEvolutionObserver(EvolutionObserver<T> delegate, long delay, TimeUnit unit)
    {
        if (delegate == null)
        {
            throw new NullPointerException("delegate may not be null");
        }
        if (unit == null)
        {
            throw new NullPointerException("unit may not be null");
        }
        if (delay < 0)
        {
            throw new IllegalArgumentException("delay may not be negative: " + delay);
        }
        
        this.delegate = delegate;
        this.delay = delay;
        this.unit = unit;
    }


    public void populationUpdate(PopulationData<? extends T> populationData)
    {
        if (latestPopulation.getAndSet(populationData) != null)
        {
            // An update is already scheduled.
            return;
        }
        
        // Schedule an update in 300ms.
        timer.schedule(new Runnable()
        {
            public void run()
            {
                delegate.populationUpdate(latestPopulation.getAndSet(null));
            }
        }, delay, unit);
    }
}
