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

/**
 * Unit test for termination condition that checks an abort flag set by the user.
 * @author Daniel Dyer
 */
public class UserAbortTest
{
    @Test
    public void testAbort()
    {
        UserAbort condition = new UserAbort();
        // This population data should be irrelevant.
        PopulationData<Object> data = new PopulationData<Object>(new Object(), 0, 0, 0, true, 2, 0, 0, 100);
        assert !condition.shouldTerminate(data) : "Should not terminate without user abort.";
        assert !condition.isAborted() : "Should not be aborted without user intervention.";
        condition.abort();
        assert condition.shouldTerminate(data) : "Should terminate after user abort.";
        assert condition.isAborted() : "Should be aborted after user intervention.";
    }
}
