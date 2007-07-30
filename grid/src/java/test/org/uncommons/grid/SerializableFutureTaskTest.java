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

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.concurrent.Callable;
import java.util.concurrent.CancellationException;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import org.testng.Reporter;
import org.testng.annotations.Test;

/**
 * Unit test for the serializable task class used by the
 * {@link DistributedExecutorService}.  Ensures that serialization and execution
 * work correctly.
 * @author Daniel Dyer
 */
public class SerializableFutureTaskTest
{
    @SuppressWarnings({"unchecked"})
    @Test
    public void testSerialization() throws IOException,
                                           ClassNotFoundException,
                                           ExecutionException,
                                           InterruptedException
    {
        SerializableFutureTask<String> task
            = new SerializableFutureTask<String>(new SerializableRunnable(), "Watchmaker");
        long taskID = task.getID();

        // Serialize the task to ensure that it is actually serializable (an exception
        // will be thrown if it isn't).
        ByteArrayOutputStream byteOutputStream = new ByteArrayOutputStream();
        ObjectOutputStream outputStream = new ObjectOutputStream(byteOutputStream);
        outputStream.writeObject(task);
        outputStream.flush();
        outputStream.close();
        byte[] serializedData = byteOutputStream.toByteArray();
        Reporter.log("Serialized data is " + serializedData.length + " bytes.");

        // Deserialize the task.
        ByteArrayInputStream byteInputStream = new ByteArrayInputStream(serializedData);
        ObjectInputStream inputStream = new ObjectInputStream(byteInputStream);
        SerializableFutureTask<String> task2 = (SerializableFutureTask<String>) inputStream.readObject();

        // Check that the deserialized task as the same ID as the original.
        assert task2.getID() == taskID : "Deserialized task has wrong ID: " + task2.getID();

        // Execute the deserialized task to ensure that we get the expected result.
        task2.run();
        assert task2.get().equals("Watchmaker") : "Deserialized task is not correct.";
    }


    /**
     * Checks that the timeout version of the get method works as expected.  Because the task
     * is not executed, the get method should throw a TimeoutException after the specified
     * delay. 
     * @throws TimeoutException Should be thrown if the test succeeds.
     * @throws ExecutionException Should not be thrown if the test succeeds.
     * @throws InterruptedException Should not be thrown if the test succeeds.
     */
    @Test(expectedExceptions = TimeoutException.class)
    public void testTimeout() throws TimeoutException, ExecutionException, InterruptedException
    {
        SerializableFutureTask<String> task
            = new SerializableFutureTask<String>(new SerializableRunnable(), "Watchmaker");
        task.get(50, TimeUnit.MILLISECONDS);
    }


    @Test(expectedExceptions = CancellationException.class)
    public void testCancellationOfUnstartedTask() throws ExecutionException, InterruptedException
    {
        SerializableFutureTask<String> task
            = new SerializableFutureTask<String>(new SerializableRunnable(), "Watchmaker");
        assert !task.isCancelled() : "Task should not be cancelled initially.";
        assert !task.isDone() : "Task should not be completed initially.";
        boolean cancelled = task.cancel(true);
        assert cancelled : "Cancellation should succeed.";
        assert task.isCancelled() : "Task should have been cancelled.";
        assert task.isDone() : "Task should be marked as done after cancellation.";
        boolean cancelledAgain = task.cancel(true);
        assert !cancelledAgain : "Task should not be cancelled twice.";
        task.get(); // This should throw a CancellationException.
    }


    @Test(expectedExceptions = CancellationException.class)
    public void testCancellationOfRunningTask() throws ExecutionException,
                                                       InterruptedException,
                                                       TimeoutException
    {
        final CountDownLatch latch = new CountDownLatch(1);
        SerializableFutureTask<String> task
            = new SerializableFutureTask<String>(new SerializableRunnable()
        {

            @Override
            public void run()
            {
                try
                {
                    latch.countDown(); // Notifies the main test thread that the task has started.
                    Thread.sleep(10000); // Long enough delay so that it doesn't complete before we can cancel it.
                }
                catch (InterruptedException ex)
                {
                    // Ignore we expect this to happen.
                }
            }
        }, "Watchmaker");
        new Thread(task, "CancellationTest").start();
        latch.await(); // Wait for the task thread to start. 
        boolean cancelled = task.cancel(true);
        assert cancelled : "Cancellation should succeed.";
        assert task.isCancelled() : "Task should have been cancelled.";
        assert task.isDone() : "Task should be marked as done after cancellation.";
        boolean cancelledAgain = task.cancel(true);
        assert !cancelledAgain : "Task should not be cancelled twice.";
        task.get(0, TimeUnit.MILLISECONDS); // This should throw a CancellationException.
    }


    @Test(expectedExceptions = ExecutionException.class)
    public void testExceptionInTask() throws ExecutionException,
                                             InterruptedException
    {
        SerializableFutureTask<String> task
            = new SerializableFutureTask<String>(new SerializableCallable<String>()
        {

            public String call() throws Exception
            {
                // Any kind of checked exception will do for this test.
                throw new IOException();
            }
        });
        task.run();
        try
        {
            task.get();
        }
        catch (ExecutionException ex)
        {
            assert ex.getCause() instanceof IOException : "Wrong cause.";
            throw ex; // Re-throw so TestNG can validate that the exception was thrown.
        }
    }


    /**
     * Attempting to create a SerializableFutureTask with a non-Serializable
     * {@link Callable} should result in an appropriate exception.  It is an
     * error for the constructor to accept the argument without failing since
     * this will just lead to problems later on.
     */
    @Test(expectedExceptions = IllegalArgumentException.class)
    public void testNonSerializableCallable()
    {
        new SerializableFutureTask<String>(new Callable<String>()
        {
            public String call()
            {
                return "I am not Serializable";
            }
        });
    }


    /**
     * Attempting to create a SerializableFutureTask with a non-Serializable
     * {@link Runnable} should result in an appropriate exception.  It is an
     * error for the constructor to accept the argument without failing since
     * this will just lead to problems later on.
     */
    @Test(expectedExceptions = IllegalArgumentException.class)
    public void testNonSerializableRunnable()
    {
        new SerializableFutureTask<String>(new Runnable()
        {
            public void run()
            {
                // Do nothing.
            }
        }, "Watchmaker");
    }


    /**
     * Base class for {@link Runnable}s that are also {@link Serializable}.
     */
    private static class SerializableRunnable implements Runnable,
                                                         Serializable
    {
        public void run()
        {
            // Do nothing.
        }
    }
}
