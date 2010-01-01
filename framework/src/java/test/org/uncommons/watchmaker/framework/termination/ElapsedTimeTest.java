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
package org.uncommons.watchmaker.framework.termination;

import org.testng.annotations.Test;
import org.uncommons.watchmaker.framework.PopulationData;
import org.uncommons.watchmaker.framework.TerminationCondition;

/**
 * Unit test for termination condition that checks the time taken so far by the
 * evolutionary algorithm.
 * @author Daniel Dyer
 */
public class ElapsedTimeTest
{
    @Test
    public void testElapsedTimes()
    {
        TerminationCondition condition = new ElapsedTime(1000);
        PopulationData<Object> data = new PopulationData<Object>(new Object(), 0, 0, 0, true, 2, 0, 0, 100);
        assert !condition.shouldTerminate(data) : "Should not terminate before timeout.";
        data = new PopulationData<Object>(new Object(), 0, 0, 0, true, 2, 0, 0, 1000);
        assert condition.shouldTerminate(data) : "Should terminate after timeout.";
    }


    /**
     * The duration must be greater than zero to be useful.  This test
     * ensures that an appropriate exception is thrown if the duration is not positive.
     * Not throwing an exception is an error because it permits undetected bugs in
     * evolutionary programs.
     */
    @Test(expectedExceptions = IllegalArgumentException.class)
    public void testZeroRatio()
    {
        new ElapsedTime(0L);
    }

}
