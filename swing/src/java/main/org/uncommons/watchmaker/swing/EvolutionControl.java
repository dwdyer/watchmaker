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
package org.uncommons.watchmaker.swing;

import javax.swing.JComponent;

/**
 * Common interface for GUI controls for evolutionary programs.
 * @author Daniel Dyer
 */
public interface EvolutionControl
{
    /**
     * @return The GUI component used by this control.
     */
    JComponent getControl();

    /**
     * Resets the control to its initial configuration.
     */
    void reset();
}
