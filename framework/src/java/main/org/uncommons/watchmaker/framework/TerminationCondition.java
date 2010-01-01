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
package org.uncommons.watchmaker.framework;

/**
 * Interface for implementing conditions used to terminate evolutionary algorithms.
 * @author Daniel Dyer
 */
public interface TerminationCondition
{
    /**
     * The condition is queried via this method to determine whether or not evolution
     * should finish at the current point.
     * @param populationData Information about the current state of evolution.  This may
     * be used to determine whether evolution should continue or not.
     * @return true if evolution should be terminated, false otherwise.
     */
    boolean shouldTerminate(PopulationData<?> populationData);
}
