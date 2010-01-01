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
 * {@link TerminationCondition} implementation that allows for user-initiated
 * termination of an evolutionary algorithm.  This condition can be used, for
 * instance, to provide a button on a GUI that terminates execution.  The
 * application should retain a reference to the instance after passing it to
 * the evolution engine and should invoke the {@link #abort()} method to make
 * the evolution terminate at the end of the current generation.
 * @see org.uncommons.watchmaker.swing.AbortControl
 * @author Daniel Dyer
 */
public final class UserAbort implements TerminationCondition
{
    private volatile boolean aborted = false;

    /**
     * {@inheritDoc}
     */
    public boolean shouldTerminate(PopulationData<?> populationData)
    {
        return isAborted();
    }


    /**
     * Aborts any evolutionary algorithms that monitor this termination condition
     * instance.
     */
    public void abort()
    {
        aborted = true;
    }


    /**
     * @return true if the {@link #abort()} method has been invoked, false otherwise.
     */
    public boolean isAborted()
    {
        return aborted;
    }


    /**
     * Resets the abort condition to false so that it may be reused.
     */
    public void reset()
    {
        aborted = false;
    }
}
