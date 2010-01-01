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

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;
import org.testng.annotations.Test;

/**
 * Unit test for the general-purpose thread factory implementation.
 * @author Daniel Dyer
 */
public class ConfigurableThreadFactoryTest
{
    @Test
    public void testDaemonThreads()
    {
        ThreadFactory threadFactory = new ConfigurableThreadFactory("Test",
                                                                    Thread.MIN_PRIORITY,
                                                                    true);
        Runnable doNothing = new Runnable()
        {
            public void run()
            {
                // Do nothing.
            }
        };
        Thread thread1 = threadFactory.newThread(doNothing);
        assert thread1.getName().startsWith("Test") : "Wrong thread name: " + thread1.getName();
        assert thread1.getPriority() == Thread.MIN_PRIORITY : "Wrong priority: " + thread1.getPriority();
        assert thread1.isDaemon() : "Thread should be a daemon.";

        // Second thread should have a different name.
        Thread thread2 = threadFactory.newThread(doNothing);
        assert thread2.getName().startsWith("Test") : "Wrong thread name: " + thread2.getName();
        assert !thread1.getName().equals(thread2.getName()) : "Thread names should be different.";
    }


    @Test
    public void testNonDaemonThreads()
    {
        ThreadFactory threadFactory = new ConfigurableThreadFactory("Test",
                                                                    Thread.MAX_PRIORITY,
                                                                    false);
        Runnable doNothing = new Runnable()
        {
            public void run()
            {
                // Do nothing.
            }
        };
        Thread thread = threadFactory.newThread(doNothing);
        assert thread.getName().startsWith("Test") : "Wrong thread name: " + thread.getName();
        assert thread.getPriority() == Thread.MAX_PRIORITY : "Wrong priority: " + thread.getPriority();
        assert !thread.isDaemon() : "Thread should not be a daemon.";
    }


    @Test
    public void testDefaultExceptionHandler() throws InterruptedException
    {
        // Intercept std. err.
        ByteArrayOutputStream byteStream = new ByteArrayOutputStream();
        System.setErr(new PrintStream(byteStream));

        ThreadFactory threadFactory = new ConfigurableThreadFactory("Test",
                                                                    Thread.MAX_PRIORITY,
                                                                    false);
        Runnable doNothing = new Runnable()
        {
            public void run()
            {
                throw new IllegalStateException("This is a test.");
            }
        };
        Thread thread = threadFactory.newThread(doNothing);
        thread.start();
        thread.join();

        String output = byteStream.toString();
        assert output.startsWith("java.lang.IllegalStateException") : "Exception handler failed to log exception.";
    }


    @Test
    public void testCustomExceptionHandler() throws InterruptedException
    {
        ExceptionHandler exceptionHandler = new ExceptionHandler();
        ThreadFactory threadFactory = new ConfigurableThreadFactory("Test",
                                                                    Thread.MAX_PRIORITY,
                                                                    false,
                                                                    exceptionHandler);
        Runnable doNothing = new Runnable()
        {
            public void run()
            {
                throw new IllegalStateException("This is a test.");
            }
        };
        Thread thread = threadFactory.newThread(doNothing);
        thread.start();
        thread.join();
        assert exceptionHandler.getExceptionCount() == 1 : "Exception not thrown.";
    }


    private static final class ExceptionHandler implements Thread.UncaughtExceptionHandler
    {
        private final AtomicInteger count = new AtomicInteger();

        public void uncaughtException(Thread thread, Throwable throwable)
        {
            count.incrementAndGet();
        }

        public int getExceptionCount()
        {
            return count.get();
        }
    }
}
