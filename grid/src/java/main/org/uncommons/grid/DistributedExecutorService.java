// ============================================================================
//   Copyright 2006, 2007 Daniel W. Dyer
//
//   Licensed under the Apache License, Version 2.0 (the "License");
//   you may not use this file except in compliance with the License.
//   You may obtain a copy of the License at
//
//       http://www.apache.org/licenses/LICENSE-2.0
//
//   Unless required by applicable law or agreed to in writing, software
//   distributed under the License is distributed on an "AS IS" BASIS,
//   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
//   See the License for the specific language governing permissions and
//   limitations under the License.
// ============================================================================
package org.uncommons.grid;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.CancellationException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

/**
 * Executor service that distributes tasks among remote nodes.  This implementation
 * has the restriction that all submitted {@link Runnable}s and {@link Callable}s
 * must also be {@link java.io.Serializable}.
 * @author Daniel Dyer
 */
public class DistributedExecutorService implements ExecutorService
{
    private volatile boolean shutdown;

    public void shutdown()
    {
        shutdown = true;
    }


    public List<Runnable> shutdownNow()
    {
        return null;  // TO DO:
    }


    public boolean isShutdown()
    {
        return shutdown;
    }


    public boolean isTerminated()
    {
        return false;  // TO DO:
    }


    public boolean awaitTermination(long l, TimeUnit timeUnit) throws InterruptedException
    {
        return false;  // TO DO:
    }


    /**
     * @throws IllegalArgumentException If the callable parameter is not {@link Serializable}.
     * @throws RejectedExecutionException If this ExecutorService has been shutdown.
     */
    public <T> Future<T> submit(Callable<T> callable)
    {
        if (!(callable instanceof Serializable))
        {
            throw new IllegalArgumentException("Callable parameter must be serializable.");
        }
        SerializableFutureTask<T> task = new SerializableFutureTask<T>(callable);
        execute(task);
        return task;
    }


    /**
     * @throws IllegalArgumentException If the runnable parameter is not {@link Serializable}.
     * @throws RejectedExecutionException If this ExecutorService has been shutdown.
     */
    public <T> Future<T> submit(Runnable runnable, T t)
    {
        if (!(runnable instanceof Serializable))
        {
            throw new IllegalArgumentException("Runnable parameter must be serializable.");
        }
        SerializableFutureTask<T> task = new SerializableFutureTask<T>(runnable, t);
        execute(task);
        return task;

    }


    /**
     * @throws IllegalArgumentException If the runnable parameter is not {@link Serializable}.
     */
    public Future<?> submit(Runnable runnable)
    {
        if (!(runnable instanceof Serializable))
        {
            throw new IllegalArgumentException("Runnable parameter must be serializable.");
        }
        SerializableFutureTask<?> task = new SerializableFutureTask<Object>(runnable, null);
        execute(task);
        return task;
    }


    /**
     * @throws IllegalArgumentException If any of the callables are not {@link Serializable}.
     * @throws RejectedExecutionException If this ExecutorService has been shutdown.
     */
    public <T> List<Future<T>> invokeAll(Collection<Callable<T>> collection) throws InterruptedException
    {
        List<Future<T>> futures = new ArrayList<Future<T>>(collection.size());
        boolean done = false;
        try
        {
            for (Callable<T> callable : collection)
            {
                futures.add(submit(callable));
            }
            // Block until each task is done.
            for (Future<T> future : futures)
            {
                if (!future.isDone())
                {
                    try
                    {
                        future.get();
                    }
                    catch (CancellationException ex)
                    {
                        // Ignore and move on to the next task.
                    }
                    catch (ExecutionException ex)
                    {
                        // Ignore and move on to the next task.
                    }
                }
            }
            // If we get to here without anything more serious than a CancellationException
            // or ExecutionException, then all of the tasks have completed or been cancelled.
            done = true;
        }
        finally
        {
            // If there was an exception while processing, cancel any incomplete tasks.
            if (!done)
            {
                for (Future<T> future : futures)
                {
                    future.cancel(true);
                }
            }
        }
        return futures;
    }


    /**
     * @throws IllegalArgumentException If any of the callables are not {@link Serializable}.
     * @throws RejectedExecutionException If this ExecutorService has been shutdown.
     */
    public <T> List<Future<T>> invokeAll(Collection<Callable<T>> collection,
                                         long timeout,
                                         TimeUnit timeUnit) throws InterruptedException
    {
        long startTime = System.nanoTime();
        long remainingTime = timeUnit.toNanos(timeout);
        List<Future<T>> futures = new ArrayList<Future<T>>(collection.size());
        boolean done = false;
        try
        {
            for (Callable<T> callable : collection)
            {
                futures.add(submit(callable));
            }
            // Block until each task is done.
            for (Future<T> future : futures)
            {
                if (!future.isDone())
                {
                    try
                    {
                        future.get(remainingTime, TimeUnit.NANOSECONDS);
                    }
                    catch (CancellationException ex)
                    {
                        // Ignore and move on to the next task.
                    }
                    catch (ExecutionException ex)
                    {
                        // Ignore and move on to the next task.
                    }
                    long now = System.nanoTime();
                    remainingTime -= (now - startTime);
                    startTime = now;
                }
            }
            // If we get to here without anything more serious than a CancellationException
            // or ExecutionException, then all of the tasks have completed or been cancelled.
            done = true;
        }
        catch (TimeoutException ex)
        {
            // Our work here is done, no time to wait for any more results.
            // (any unprocessed tasks will be cancelled in the finally block).
            return futures;
        }
        finally
        {
            // If there was an exception (including a timeout) while processing,
            // cancel any incomplete tasks.
            if (!done)
            {
                for (Future<T> future : futures)
                {
                    future.cancel(true);
                }
            }
        }
        return futures;        
    }


    /**
     * @throws RejectedExecutionException If this ExecutorService has been shutdown.
     */
    public <T> T invokeAny(Collection<Callable<T>> collection) throws InterruptedException,
                                                                      ExecutionException
    {
        return null;  // TO DO:
    }


    /**
     * @throws RejectedExecutionException If this ExecutorService has been shutdown.
     */
    public <T> T invokeAny(Collection<Callable<T>> collection,
                           long l,
                           TimeUnit timeUnit) throws InterruptedException,
                                                     ExecutionException,
                                                     TimeoutException
    {
        return null;  // TO DO:
    }


    /**
     * @throws RejectedExecutionException If this ExecutorService has been shutdown.
     */
    public void execute(Runnable runnable)
    {
        if (shutdown)
        {
            throw new RejectedExecutionException("ExecutorService has been shutdown.");
        }
        // TO DO: Actually distribute the tasks.
        new Thread(runnable).start();
    }
}
