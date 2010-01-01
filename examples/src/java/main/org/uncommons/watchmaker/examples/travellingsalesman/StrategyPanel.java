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
package org.uncommons.watchmaker.examples.travellingsalesman;

import java.awt.BorderLayout;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import javax.swing.BorderFactory;
import javax.swing.ButtonGroup;
import javax.swing.JPanel;
import javax.swing.JRadioButton;

/**
 * Panel for configuring a route-finding strategy for the travelling
 * salesman problem.
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
    StrategyPanel(DistanceLookup distances)
    {
        super(new BorderLayout());
        this.distances = distances;
        evolutionOption = new JRadioButton("Evolution", true);
        evolutionOption.setName("EvolutionOption"); // Helps to find the radio button from a unit test.
        bruteForceOption = new JRadioButton("Brute Force", false);
        bruteForceOption.setName("BruteForceOption"); // Helps to find the radio button from a unit test.
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
        evolutionPanel = new EvolutionPanel(distances);
        evolutionPanel.setName("EvolutionPanel"); // Helps to find the panel from a unit test.
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
}
