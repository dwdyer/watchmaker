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

import org.uncommons.watchmaker.framework.PopulationData;
import org.uncommons.watchmaker.framework.TerminationCondition;

/**
 * Terminates evolution after a pre-determined period of time has elapsed.
 * @author Daniel Dyer
 */
public class ElapsedTime implements TerminationCondition
{
    private final long maxDuration;

    /**
     * @param maxDuration The maximum period of time (in milliseconds) before
     * evolution will be terminated.
     */
    public ElapsedTime(long maxDuration)
    {
        if (maxDuration <= 0)
        {
            throw new IllegalArgumentException("Duration must be positive.");
        }
        this.maxDuration = maxDuration;
    }


    /**
     * {@inheritDoc}
     * This implementation terminates evolution if the pre-configured maximum
     * permitted time has elapsed.
     */
    public boolean shouldTerminate(PopulationData<?> populationData)
    {
        return populationData.getElapsedTime() >= maxDuration;
    }
}
