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
package org.uncommons.watchmaker.swing;

import org.testng.annotations.Test;

/**
 * Unit test for evolution abort control.
 * @author Daniel Dyer
 */
public class AbortControlTest
{
    /**
     * Make sure that clicking the button causes the termination condition
     * to be satisfied.
     */
    @Test
    public void testAbort()
    {
        AbortControl control = new AbortControl();
        assert !control.getTerminationCondition().shouldTerminate(null) : "Abort condition should be false.";
        control.getControl().doClick(); // Should fire an event that changes the condition.
        assert control.getTerminationCondition().shouldTerminate(null) : "Abort condition should be true.";
        // Finally, ensure that reset works as expected.
        control.reset();
        assert !control.getTerminationCondition().shouldTerminate(null) : "Abort condition should be false.";
    }
}
