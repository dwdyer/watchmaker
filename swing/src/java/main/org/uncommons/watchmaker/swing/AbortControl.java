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

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JButton;
import org.uncommons.watchmaker.framework.TerminationCondition;
import org.uncommons.watchmaker.framework.termination.UserAbort;

/**
 * A GUI control that allows the user to abort an evolutionary program.
 * @author Daniel Dyer
 */
public class AbortControl implements EvolutionControl
{
    private final JButton control = new JButton("Abort");
    private final UserAbort abortCondition = new UserAbort();
    
    public AbortControl()
    {
        control.addActionListener(new ActionListener()
        {
            public void actionPerformed(ActionEvent actionEvent)
            {
                control.setEnabled(false);
                abortCondition.abort();
            }
        });
        setDescription("Terminate the evolution on completion of the current generation.");
    }


    /**
     * @return A button that, when pressed, will trigger the abort condition
     * associated with this control.
     */
    public JButton getControl()
    {
        return control;
    }


    /**
     * Enables the GUI control and resets the abort condition ready
     * for use.
     */
    public void reset()
    {
        control.setEnabled(true);
        abortCondition.reset();
    }


    /**
     * @return A {@link TerminationCondition} that is tied to this control.  It can
     * be passed to an {@link org.uncommons.watchmaker.framework.EvolutionEngine}
     * so that the evolution is aborted when this control is invoked.
     */
    public TerminationCondition getTerminationCondition()
    {
        return abortCondition;
    }


    /**
     * {@inheritDoc}
     */
    public final void setDescription(String description)
    {
        control.setToolTipText(description);
    }    
}
