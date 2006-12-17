// ============================================================================
//   Copyright 2006 Daniel W. Dyer
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
    private boolean taskExecuted;
    private boolean taskOnEDT;
    private boolean postProcessingExecuted;
    private boolean postProcessingOnEDT;

    @Test
    public void testExecutionThreads() throws InterruptedException
    {
        SwingBackgroundTask<Object> testTask = new SwingBackgroundTask<Object>()
        {
            protected Object performTask()
            {
                taskExecuted = true;
                taskOnEDT = SwingUtilities.isEventDispatchThread();
                return null;
            }

            protected void postProcessing(Object result)
            {
                postProcessingExecuted = true;
                postProcessingOnEDT = SwingUtilities.isEventDispatchThread();
            }
        };
        testTask.execute();
        testTask.waitForCompletion();
        assert taskExecuted : "Task was not executed.";
        assert postProcessingExecuted : "Post-processing was not executed.";
        assert !taskOnEDT : "Task was executed on EDT.";
        assert postProcessingOnEDT : "Post-processing was not executed on EDT.";
    }
}
