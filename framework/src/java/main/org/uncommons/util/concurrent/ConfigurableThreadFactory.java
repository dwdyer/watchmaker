//=============================================================================
// Copyright 2006-2010 Daniel W. Dyer
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
//     http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.
//=============================================================================
package org.uncommons.util.concurrent;

import java.util.concurrent.ThreadFactory;
import org.uncommons.util.id.IDSource;
import org.uncommons.util.id.IntSequenceIDSource;
import org.uncommons.util.id.StringPrefixIDSource;

/**
 * Thread factory that creates threads for use by a
 * {@link java.util.concurrent.ThreadPoolExecutor}.  The factory can be
 * configured to customise the names, priority and daemon status of created
 * threads.
 * @author Daniel Dyer
 */
public class ConfigurableThreadFactory implements ThreadFactory
{
    /**
     * A default exception handler that simply logs the stack trace of the exception.
     */
    private static final Thread.UncaughtExceptionHandler DEFAULT_EXCEPTION_HANDLER
        = new Thread.UncaughtExceptionHandler()
    {
        public void uncaughtException(Thread thread, Throwable throwable)
        {
            // Log any exceptions thrown.
            throwable.printStackTrace();
        }
    };

    
    private final IDSource<String> nameGenerator;
    private final int priority;
    private final boolean daemon;
    private final Thread.UncaughtExceptionHandler uncaughtExceptionHandler;

    /**
     * @param namePrefix The String prefix used to assign identifiers to created threads.
     * @param priority The initial priority for created threads.
     * @param daemon Whether or not created threads should be daemon threads or user threads.
     * The JVM exits when the only threads running are all daemon threads.
     */
    public ConfigurableThreadFactory(String namePrefix,
                                     int priority,
                                     boolean daemon)
    {
        this(namePrefix, priority, daemon, DEFAULT_EXCEPTION_HANDLER);
    }


    /**
     * @param namePrefix The String prefix used to assign identifiers to created threads.
     * @param priority The initial priority for created threads.
     * @param daemon Whether or not created threads should be daemon threads or user threads.
     * The JVM exits when the only threads running are all daemon threads.
     * @param uncaughtExceptionHandler A strategy for dealing with uncaught exceptions.
     */
    public ConfigurableThreadFactory(String namePrefix,
                                     int priority,
                                     boolean daemon,
                                     Thread.UncaughtExceptionHandler uncaughtExceptionHandler)
    {
        this.nameGenerator = new StringPrefixIDSource(namePrefix + '-', new IntSequenceIDSource());
        this.priority = priority;
        this.daemon = daemon;
        this.uncaughtExceptionHandler = uncaughtExceptionHandler;
    }


    /**
     * Creates a new thread configured according to this factory's parameters.
     * @param runnable The runnable to be executed by the new thread.
     * @return The created thread.
     */
    public Thread newThread(Runnable runnable)
    {
        Thread thread = new Thread(runnable, nameGenerator.nextID());
        thread.setPriority(priority);
        thread.setDaemon(daemon);
        thread.setUncaughtExceptionHandler(uncaughtExceptionHandler);
        return thread;
    }
}
