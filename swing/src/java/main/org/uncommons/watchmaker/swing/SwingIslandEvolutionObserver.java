package org.uncommons.watchmaker.swing;

import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicReference;
import org.uncommons.watchmaker.framework.PopulationData;
import org.uncommons.watchmaker.framework.islands.IslandEvolutionObserver;

/**
 * Limits the update rate of a Swing-based IslandEvolutionObserver.
 * <p/>
 * @param <T> the population type
 * @author Gili Tzabari
 */
public class SwingIslandEvolutionObserver<T> implements IslandEvolutionObserver<T>
{
    private final IslandEvolutionObserver<T> delegate;
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
    private final ConcurrentHashMap<Integer, PopulationData<? extends T>> latestIslandPopulation =
        new ConcurrentHashMap<Integer, PopulationData<? extends T>>();


    /**
     * Creates a new SwingEvolutionObserver.
     * 
     * @param delegate the underlying IslandEvolutionObserver to update
     * @param delay the amount of time to wait before updating the underlying
     *   EvolutionObserver
     * @param unit the time unit of delay
     * @throws NullPointerException if delegate or unit are null
     * @throws IllegalArgumentException if delay is negative
     */
    public SwingIslandEvolutionObserver(IslandEvolutionObserver<T> delegate, long delay,
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


    public void islandPopulationUpdate(final int i, PopulationData<? extends T> populationData)
    {
        if (latestIslandPopulation.put(i, populationData) != null)
        {
            // An update is already scheduled
            return;
        }

        // Schedule an update
        timer.schedule(new Runnable()
        {
            public void run()
            {
                delegate.islandPopulationUpdate(i, latestIslandPopulation.remove(i));
            }
        }, delay, unit);
    }
}
