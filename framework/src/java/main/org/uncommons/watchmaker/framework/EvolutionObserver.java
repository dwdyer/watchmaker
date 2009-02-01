// ============================================================================
//   Copyright 2006-2009 Daniel W. Dyer
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
package org.uncommons.watchmaker.framework;

/**
 * Call-back interface so that programs can monitor the state of a
 * long-running evolutionary algorithm.
 * @param <T> The type of entity that exists in the evolving population
 * that is being observed.  This type can be bound to a super-type of the
 * actual population type so as to allow a non-specific observer that can
 * be re-used for different population types.
 * @author Daniel Dyer
 */
public interface EvolutionObserver<T>
{
    /**
     * Invoked when the state of the population has changed (typically
     * at the end of a generation).
     * @param data Statistics about the state of the current generation.
     */
    void populationUpdate(PopulationData<? extends T> data);
}
