// ============================================================================
//   Copyright 2006, 2007, 2008 Daniel W. Dyer
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
package org.uncommons.swing;

import java.awt.BorderLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;

/**
 * A Swing component that arranges components and associated labels in two columns.
 * All of the labelled components are placed in the {@link BorderLayout#NORTH NORTH}
 * section of a {@link BorderLayout}.  The other areas of the BorderLayout remain
 * unoccupied and can be added to.
 * @author Daniel Dyer
 */
public class LabelledComponentsPanel extends JPanel
{
    private static final GridBagConstraints LABEL_CONSTRAINTS = new GridBagConstraints(GridBagConstraints.RELATIVE,
                                                                                       GridBagConstraints.RELATIVE,
                                                                                       GridBagConstraints.RELATIVE,
                                                                                       1,
                                                                                       1,
                                                                                       0,
                                                                                       GridBagConstraints.WEST,
                                                                                       GridBagConstraints.HORIZONTAL,
                                                                                       new Insets(0, 0, 0, 0),
                                                                                       0,
                                                                                       0);

    private static final GridBagConstraints VALUE_CONSTRAINTS = new GridBagConstraints(GridBagConstraints.RELATIVE,
                                                                                       GridBagConstraints.RELATIVE,
                                                                                       GridBagConstraints.REMAINDER,
                                                                                       1,
                                                                                       0,
                                                                                       0,
                                                                                       GridBagConstraints.EAST,
                                                                                       GridBagConstraints.HORIZONTAL,
                                                                                       new Insets(0, 0, 0, 0),
                                                                                       0,
                                                                                       0);

    private final JPanel contentPanel = new JPanel(new GridBagLayout());

    
    public LabelledComponentsPanel()
    {
        super(new BorderLayout());
        add(contentPanel, BorderLayout.NORTH);
    }


    /**
     * Add another row to the form.
     * @param labelText The text of this component's label.
     * @param component The component to display in the righthand column.
     */
    public void addLabelledComponent(String labelText, JComponent component)
    {
        contentPanel.add(new JLabel(labelText), LABEL_CONSTRAINTS);
        contentPanel.add(component, VALUE_CONSTRAINTS);
    }
}
