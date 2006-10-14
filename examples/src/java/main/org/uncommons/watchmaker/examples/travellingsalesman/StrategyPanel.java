// ============================================================================
//   Copyright 2006 Daniel W. Dyer
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
package org.uncommons.watchmaker.examples.travellingsalesman;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import javax.swing.BorderFactory;
import javax.swing.ButtonGroup;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JSpinner;
import javax.swing.SpinnerNumberModel;
import javax.swing.SpringLayout;
import org.uncommons.gui.SpringUtilities;

/**
 * @author Daniel Dyer
 */
class StrategyPanel extends JPanel
{
    private final JRadioButton evolutionOption;
    private final JRadioButton bruteForceOption;
    private final EvolutionPanel evolutionPanel;

    public StrategyPanel()
    {
        super(new BorderLayout());
        evolutionOption = new JRadioButton("Evolution", true);
        bruteForceOption = new JRadioButton("Brute Force", false);
        evolutionOption.addItemListener(new ItemListener()
        {
            public void itemStateChanged(ItemEvent itemEvent)
            {
                evolutionPanel.setEnabled(evolutionOption.isSelected());
            }
        });
        ButtonGroup strategyGroup = new ButtonGroup();
        strategyGroup.add(evolutionOption);
        strategyGroup.add(bruteForceOption);
        evolutionPanel = new EvolutionPanel();
        add(evolutionOption, BorderLayout.NORTH);
        add(evolutionPanel, BorderLayout.CENTER);
        add(bruteForceOption, BorderLayout.SOUTH);
        setBorder(BorderFactory.createTitledBorder("Route-Finding Strategy"));
    }


    public TravellingSalesmanStrategy getStrategy()
    {
        if (bruteForceOption.isSelected())
        {
            return new BruteForceTravellingSalesman();
        }
        else
        {
            return evolutionPanel.getStrategy();
        }
    }


    private static final class EvolutionPanel extends JPanel
    {
        private JLabel populationLabel;
        private JSpinner populationSpinner;
        private JLabel generationsLabel;
        private JSpinner generationsSpinner;

        public EvolutionPanel()
        {
            super(new FlowLayout(FlowLayout.LEFT, 0, 0));
            JPanel innerPanel = new JPanel(new SpringLayout());

            populationLabel = new JLabel("Population Size: ");
            populationSpinner = new JSpinner(new SpinnerNumberModel(300, 2, 10000, 1));
            populationLabel.setLabelFor(populationSpinner);
            innerPanel.add(populationLabel);
            innerPanel.add(populationSpinner);

            generationsLabel = new JLabel("Number of Generations: ");
            generationsSpinner = new JSpinner(new SpinnerNumberModel(100, 1, 10000, 1));
            generationsLabel.setLabelFor(generationsSpinner);
            innerPanel.add(generationsLabel);
            innerPanel.add(generationsSpinner);

            SpringUtilities.makeCompactGrid(innerPanel, 2, 2, 30, 6, 6, 6);
            add(innerPanel);
        }

        @Override
        public void setEnabled(boolean b)
        {
            populationLabel.setEnabled(b);
            populationSpinner.setEnabled(b);
            generationsLabel.setEnabled(b);
            generationsSpinner.setEnabled(b);
            super.setEnabled(b);
        }

        public TravellingSalesmanStrategy getStrategy()
        {
            return new EvolutionaryTravellingSalesman((Integer) populationSpinner.getValue(),
                                                      (Integer) generationsSpinner.getValue());
        }
    }
}
