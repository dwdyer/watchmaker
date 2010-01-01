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
 * <p>Call-back interface so that programs can monitor the state of a
 * long-running evolutionary algorithm.</p>
 * <p>Depending on the parameters of the evolutionary program, an observer may
 * be invoked dozens or hundreds of times a second, especially when the population
 * size is small as this leads to shorter generations. The processing performed by an
 * evolution observer should be reasonably short-lived so as to avoid slowing down
 * the evolution.</p>
 * <p><strong>Using an EvolutionObserver to update a Swing GUI:</strong>
 * Evolution updates are dispatched on the request thread.  To adhere to
 * Swing threading rules you must use {@link javax.swing.SwingUtilities#invokeLater(Runnable)}
 * or {@link javax.swing.SwingUtilities#invokeAndWait(Runnable)} to perform any updates to Swing
 * components.</p>
 * <p>Be aware that if there are too many Swing updates queued for asynchronous
 * execution with {@link javax.swing.SwingUtilities#invokeLater(Runnable)}, due to a high
 * number of generations per second, then the GUI will become sluggish and
 * unresponsive.
 * This situation can be mitigated by minimising the amount of work done by
 * the evolution observer and/or by not updating the GUI every time the observer is
 * notified.</p>
 * <p>The unresponsive GUI problem does not occur when using
 * {@link javax.swing.SwingUtilities#invokeAndWait(Runnable)} because updates are
 * executed synchronously.  The downside is that evolution threads are stalled/idle until
 * Swing has finished performing the updates.  This won't make much difference on a single
 * core machine but will impact throughput on multi-core machines.</p>
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
