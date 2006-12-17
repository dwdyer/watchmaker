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
final class StrategyPanel extends JPanel
{
    private final DistanceLookup distances;
    private final JRadioButton evolutionOption;
    private final JRadioButton bruteForceOption;
    private final EvolutionPanel evolutionPanel;

    /**
     * Creates a panel with components for controlling the route-finding
     * strategy.
     * @param distances Data used by the strategy in order to calculate
     * shortest routes.
     */
    public StrategyPanel(DistanceLookup distances)
    {
        super(new BorderLayout());
        this.distances = distances;
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
            return new BruteForceTravellingSalesman(distances);
        }
        else
        {
            return evolutionPanel.getStrategy();
        }
    }


    @Override
    public void setEnabled(boolean b)
    {
        evolutionOption.setEnabled(b);
        bruteForceOption.setEnabled(b);
        evolutionPanel.setEnabled(b && evolutionOption.isSelected());
        super.setEnabled(b);
    }


    /**
     * Panel of evolution controls.
     */
    private final class EvolutionPanel extends JPanel
    {
        private JLabel populationLabel;
        private JSpinner populationSpinner;
        private JLabel elitismLabel;
        private JSpinner elitismSpinner;
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

            elitismLabel = new JLabel("Elitism (no. candidates preserved unchanged): ");
            elitismSpinner = new JSpinner(new SpinnerNumberModel(3, 0, 10000, 1));
            elitismLabel.setLabelFor(elitismSpinner);
            innerPanel.add(elitismLabel);
            innerPanel.add(elitismSpinner);

            generationsLabel = new JLabel("Number of Generations: ");
            generationsSpinner = new JSpinner(new SpinnerNumberModel(100, 1, 10000, 1));
            generationsLabel.setLabelFor(generationsSpinner);
            innerPanel.add(generationsLabel);
            innerPanel.add(generationsSpinner);

            SpringUtilities.makeCompactGrid(innerPanel, 3, 2, 30, 6, 6, 6);
            add(innerPanel);
        }

        @Override
        public void setEnabled(boolean b)
        {
            populationLabel.setEnabled(b);
            populationSpinner.setEnabled(b);
            elitismLabel.setEnabled(b);
            elitismSpinner.setEnabled(b);
            generationsLabel.setEnabled(b);
            generationsSpinner.setEnabled(b);
            super.setEnabled(b);
        }

        public TravellingSalesmanStrategy getStrategy()
        {
            return new EvolutionaryTravellingSalesman(distances,
                                                      (Integer) populationSpinner.getValue(),
                                                      (Integer) elitismSpinner.getValue(),
                                                      (Integer) generationsSpinner.getValue());
        }
    }
}
