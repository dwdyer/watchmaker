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

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import org.testng.annotations.Test;

/**
 * @author Daniel Dyer
 */
public class DistributedExecutorServiceTest
{
    @Test
    public void testInvokeAll() throws InterruptedException,
                                       ExecutionException
    {
        final int taskCount = 3;
        ExecutorService executor = new DistributedExecutorService();
        List<Callable<String>> tasks = new ArrayList<Callable<String>>(taskCount);
        for (int i = 0; i < taskCount; i++)
        {
            tasks.add(new SerializableCallable<String>()
            {
                public String call() throws Exception
                {
                    return "Hello";
                }
            });
        }
        List<Future<String>> futures = executor.invokeAll(tasks);
        assert futures.size() == taskCount : "Wrong number of futures returned.";
        for (Future<String> future : futures)
        {
            assert future.isDone() : "Future should have been completed in some way.";
            assert !future.isCancelled() : "Future should not have been cancelled.";
            String result = future.get();
            assert result.equals("Hello") : "Wrong result: " + result;
        }
    }


    @Test
    public void testInvokeAllWithTimeout() throws InterruptedException,
                                                  ExecutionException
    {
        final int taskCount = 3;
        ExecutorService executor = new DistributedExecutorService();
        List<Callable<String>> tasks = new ArrayList<Callable<String>>(taskCount);
        for (int i = 0; i < taskCount; i++)
        {
            tasks.add(new SerializableCallable<String>()
            {
                public String call() throws Exception
                {
                    Thread.sleep(10000);// Long enough delay so that it doesn't complete before the timeout.
                    return "Hello";
                }
            });
        }
        List<Future<String>> futures = executor.invokeAll(tasks, 100, TimeUnit.MILLISECONDS);
        assert futures.size() == taskCount : "Wrong number of futures returned.";
        for (Future<String> future : futures)
        {
            assert future.isDone() : "Future should have been completed in some way.";
            assert future.isCancelled() : "Future should have been cancelled.";
        }
    }
}
