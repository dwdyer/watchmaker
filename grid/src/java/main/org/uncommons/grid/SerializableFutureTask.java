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
import java.util.concurrent.Callable;
import java.util.concurrent.CancellationException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

/**
 * This class is a {@link Serializable} replacement for {@link java.util.concurrent.FutureTask}.
 * It is used internally by {@link DistributedExecutorService}.
 * @author Daniel Dyer
 */
class SerializableFutureTask<V> implements Future<V>,
                                           Runnable,
                                           Serializable
{
    private final Callable<V> callable;
    private final ReentrantLock lock = new ReentrantLock();
    private final Condition accessible = lock.newCondition();

    private transient boolean cancelled = false;
    private transient V result = null;
    private transient Throwable throwable = null;
    private transient Thread thread = null;

    /**
     * Creates a task that executes the given {@link Callable} and returns its
     * result.
     * @param callable The task logic.
     */
    public SerializableFutureTask(Callable<V> callable)
    {
        if (!(callable instanceof Serializable))
        {
            throw new IllegalArgumentException("Callable parameter must be Serializable");
        }
        this.callable = callable;
    }


    /**
     * Creates a task that executes the given {@link Runnable} before returning the
     * specified result.
     * @param runnable The task logic.
     * @param value The result returned when the task completes.
     */
    public SerializableFutureTask(final Runnable runnable,
                                  final V value)
    {
        this(new SerializableCallable<V>(value, runnable));
    }


    public boolean cancel(boolean mayInterrupt)
    {
        try
        {
            lock.lock();
            if (isDone())
            {
                return false;
            }
            else if (mayInterrupt && thread != null && thread != Thread.currentThread())
            {
                thread.interrupt();
            }
            cancelled = true;
            return cancelled;
        }
        finally
        {
            lock.unlock();
        }
    }


    /**
     * @return True if the task has been cancelled, false otherwise.
     */
    public boolean isCancelled()
    {
        try
        {
            lock.lock();
            return cancelled;
        }
        finally
        {
            lock.unlock();
        }
    }


    /**
     * @return True if the task completed succesfully, threw an exception or was
     * cancelled.  Returns false if the task has not yet been executed or cancelled.
     */
    public boolean isDone()
    {
        try
        {
            lock.lock();
            return cancelled || result != null || throwable != null;
        }
        finally
        {
            lock.unlock();
        }
    }


    public V get() throws InterruptedException, ExecutionException
    {
        try
        {
            lock.lock();
            while (!isDone())
            {
                accessible.await();
            }

            if (cancelled)
            {
                throw new CancellationException();
            }
            else if (throwable != null)
            {
                throw new ExecutionException(throwable);
            }
            
            return result;
        }
        finally
        {
            lock.unlock();
        }
    }


    public V get(long timeout, TimeUnit timeUnit) throws InterruptedException,
                                                         ExecutionException,
                                                         TimeoutException
    {
        try
        {
            lock.lock();
            long nanoSeconds = timeUnit.toNanos(timeout);
            while (!isDone())
            {
                if (nanoSeconds <= 0)
                {
                    throw new TimeoutException();
                }
                else
                {
                    nanoSeconds = accessible.awaitNanos(nanoSeconds);
                }
            }

            if (cancelled)
            {
                throw new CancellationException();
            }
            else if (throwable != null)
            {
                throw new ExecutionException(throwable);
            }

            return result;
        }
        finally
        {
            lock.unlock();
        }
    }


    public void run()
    {
        try
        {
            lock.lock();
            if (!isDone())
            {
                thread = Thread.currentThread();
            }
        }
        finally
        {
            lock.unlock();

        }
        
        try
        {
            // The call is made without holding the lock (the lock is re-acquired
            // later to set the result or the throwable depending on the outcome
            // of the call).
            setResult(callable.call());
        }
        catch (Throwable t)
        {
            setThrowable(t);
        }
    }


    /**
     * Acquires the lock and updates the state of this task with the result
     * passed in.
     * @param result The result of invoking the {@link Callable}.
     */
    private void setResult(V result)
    {
        try
        {
            lock.lock();
            this.result = result;
            thread = null;
            accessible.signalAll();
        }
        finally
        {
            lock.unlock();
        }
    }


    /**
     * Acquires the lock and updates the state of this task with the throwable
     * passed in.
     * @param t An exception thrown when attempting to invoke the {@link Callable}.
     */
    private void setThrowable(Throwable t)
    {
        try
        {
            lock.lock();
            throwable = t;
        }
        finally
        {
            lock.unlock();
        }
    }


    /**
     * A {@link Serializable} implementation of {@link Callable} that wraps a serializable
     * {@link Runnable} and returns a serializable result. 
     */
    private static final class SerializableCallable<V> implements Callable<V>,
                                                                  Serializable
    {
        private final V value;
        private final Runnable runnable;

        public SerializableCallable(V value,
                                    Runnable runnable)
        {
            if (!(runnable instanceof Serializable))
            {
                throw new IllegalArgumentException("Runnable parameter must be serializable.");
            }
            this.value = value;
            this.runnable = runnable;
        }


        public V call() throws Exception
        {
            runnable.run();
            return value;
        }
    }

}
