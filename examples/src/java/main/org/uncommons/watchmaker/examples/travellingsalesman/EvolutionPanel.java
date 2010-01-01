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

import java.awt.FlowLayout;
import java.util.List;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.SpinnerNumberModel;
import javax.swing.SpringLayout;
import org.uncommons.maths.random.Probability;
import org.uncommons.swing.SpringUtilities;
import org.uncommons.watchmaker.framework.SelectionStrategy;
import org.uncommons.watchmaker.swing.SelectionStrategyControl;

/**
 * Controls for configuring an {@link EvolutionaryTravellingSalesman} object.
 * @author Daniel Dyer
 */
final class EvolutionPanel extends JPanel
{
    private final JLabel populationLabel;
    private final JSpinner populationSpinner;
    private final JLabel elitismLabel;
    private final JSpinner elitismSpinner;
    private final JLabel generationsLabel;
    private final JSpinner generationsSpinner;
    private final JLabel selectionLabel;
    private final SelectionStrategyControl<List<String>> selectionControl;
    private final JCheckBox crossoverCheckbox;
    private final JCheckBox mutationCheckbox;
    private final DistanceLookup distances;

    EvolutionPanel(DistanceLookup distances)
    {
        super(new FlowLayout(FlowLayout.LEFT, 0, 0));
        this.distances = distances;
        JPanel innerPanel = new JPanel(new SpringLayout());

        populationLabel = new JLabel("Population Size: ");
        populationSpinner = new JSpinner(new SpinnerNumberModel(300, 2, 10000, 1));
        populationLabel.setLabelFor(populationSpinner);
        innerPanel.add(populationLabel);
        innerPanel.add(populationSpinner);

        elitismLabel = new JLabel("Elitism: ");
        elitismSpinner = new JSpinner(new SpinnerNumberModel(3, 0, 10000, 1));
        elitismLabel.setLabelFor(elitismSpinner);
        innerPanel.add(elitismLabel);
        innerPanel.add(elitismSpinner);

        generationsLabel = new JLabel("Number of Generations: ");
        generationsSpinner = new JSpinner(new SpinnerNumberModel(100, 1, 10000, 1));
        generationsLabel.setLabelFor(generationsSpinner);
        innerPanel.add(generationsLabel);
        innerPanel.add(generationsSpinner);

        selectionLabel = new JLabel("Selection Strategy: ");
        List<SelectionStrategy<? super List<String>>> strategies
            = SelectionStrategyControl.createDefaultOptions(new Probability(0.95d), 0.5d);
        this.selectionControl = new SelectionStrategyControl<List<String>>(strategies);
        innerPanel.add(selectionLabel);
        selectionControl.getControl().setSelectedIndex(selectionControl.getControl().getItemCount() - 1);
        innerPanel.add(selectionControl.getControl());

        crossoverCheckbox = new JCheckBox("Cross-over", true);
        mutationCheckbox = new JCheckBox("Mutation", true);

        innerPanel.add(crossoverCheckbox);
        innerPanel.add(mutationCheckbox);

        SpringUtilities.makeCompactGrid(innerPanel, 5, 2, 30, 6, 6, 6);
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
        selectionLabel.setEnabled(b);
        selectionControl.getControl().setEnabled(b);
        crossoverCheckbox.setEnabled(b);
        mutationCheckbox.setEnabled(b);
        super.setEnabled(b);
    }


    public TravellingSalesmanStrategy getStrategy()
    {
        return new EvolutionaryTravellingSalesman(distances,
                                                  selectionControl.getSelectionStrategy(),
                                                  (Integer) populationSpinner.getValue(),
                                                  (Integer) elitismSpinner.getValue(),
                                                  (Integer) generationsSpinner.getValue(),
                                                  crossoverCheckbox.isSelected(),
                                                  mutationCheckbox.isSelected());
    }
}
