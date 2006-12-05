package org.uncommons.gui;

import javax.swing.SwingUtilities;
import org.testng.annotations.Test;

/**
 * Unit test for {@link SwingBackgroundTask}.  Ensures code is
 * executed on correct threads.
 * @author Daniel Dyer
 */
public class SwingBackgroundTaskTest
{
    private boolean taskOnEDT;
    private boolean postProcessingOnEDT;

    @Test
    public void testExecutionThreads() throws InterruptedException
    {
        SwingBackgroundTask<Object> testTask = new SwingBackgroundTask<Object>()
        {
            protected Object performTask()
            {
                taskOnEDT = SwingUtilities.isEventDispatchThread();
                return null;
            }

            protected void postProcessing(Object result)
            {
                postProcessingOnEDT = SwingUtilities.isEventDispatchThread();
            }
        };
        testTask.execute();
        testTask.waitForCompletion();
        assert !taskOnEDT : "Task should not be executed on EDT.";
        assert postProcessingOnEDT : "Post-processing should be executed on EDT.";
    }
}
