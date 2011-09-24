package org.uncommons.watchmaker.swing;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;
import org.uncommons.util.concurrent.ConfigurableThreadFactory;
import org.uncommons.watchmaker.framework.PopulationData;
import org.uncommons.watchmaker.framework.islands.IslandEvolutionObserver;

/**
 * Limits the update rate of a Swing-based {@link IslandEvolutionObserver}.
 * 
 * @param <T> The population type.
 * @author Gili Tzabari
 */
public class SwingIslandEvolutionObserver<T> implements IslandEvolutionObserver<T>
{
    private final IslandEvolutionObserver<T> delegate;
    private final long delay;
    private final TimeUnit unit;
    private final ConfigurableThreadFactory threadFactory = new ConfigurableThreadFactory("SwingIslandEvolutionObserver",
                                                                                          Thread.NORM_PRIORITY,
                                                                                          true);
    private final ScheduledExecutorService timer = Executors.newScheduledThreadPool(1, threadFactory);
    private final AtomicReference<PopulationData<? extends T>> latestPopulation
        = new AtomicReference<PopulationData<? extends T>>();
    private final ConcurrentHashMap<Integer, PopulationData<? extends T>> latestIslandPopulation
        = new ConcurrentHashMap<Integer, PopulationData<? extends T>>();

    /**
     * Creates a new SwingIslandEvolutionObserver.
     * 
     * @param delegate The underlying {@link IslandEvolutionObserver} to update.
     * @param delay The amount of time to wait before updating the underlying {@link IslandEvolutionObserver}.
     * @param unit The time unit of delay.
     * @throws NullPointerException If delegate or unit are null.
     * @throws IllegalArgumentException If delay is negative.
     */
    public SwingIslandEvolutionObserver(IslandEvolutionObserver<T> delegate, long delay, TimeUnit unit)
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
        
        // Schedule an update.
        timer.schedule(new Runnable()
        {
            public void run()
            {
                delegate.populationUpdate(latestPopulation.getAndSet(null));
            }
        }, delay, unit);
    }


    public void islandPopulationUpdate(final int islandIndex, PopulationData<? extends T> populationData)
    {
        if (latestIslandPopulation.put(islandIndex, populationData) != null)
        {
            // An update is already scheduled.
            return;
        }
        
        // Schedule an update.
        timer.schedule(new Runnable()
        {
            public void run()
            {
                delegate.islandPopulationUpdate(islandIndex, latestIslandPopulation.remove(islandIndex));
            }
        }, delay, unit);
    }
}
