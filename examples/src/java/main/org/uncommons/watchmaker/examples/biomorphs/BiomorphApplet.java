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
package org.uncommons.watchmaker.examples.biomorphs;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.SpinnerNumberModel;
import javax.swing.SpringLayout;
import javax.swing.SwingUtilities;
import org.uncommons.maths.random.MersenneTwisterRNG;
import org.uncommons.maths.random.Probability;
import org.uncommons.swing.SpringUtilities;
import org.uncommons.swing.SwingBackgroundTask;
import org.uncommons.watchmaker.examples.AbstractExampleApplet;
import org.uncommons.watchmaker.framework.EvolutionEngine;
import org.uncommons.watchmaker.framework.EvolutionObserver;
import org.uncommons.watchmaker.framework.EvolutionaryOperator;
import org.uncommons.watchmaker.framework.GenerationalEvolutionEngine;
import org.uncommons.watchmaker.framework.PopulationData;
import org.uncommons.watchmaker.framework.interactive.InteractiveSelection;
import org.uncommons.watchmaker.framework.interactive.Renderer;
import org.uncommons.watchmaker.framework.termination.GenerationCount;
import org.uncommons.watchmaker.swing.SwingConsole;

/**
 * Watchmaker Framework implementation of Dawkin's biomorph program.
 * @author Daniel Dyer
 */
public class BiomorphApplet extends AbstractExampleApplet
{
    private Renderer<Biomorph, JComponent> renderer;
    private SwingConsole console;
    private JDialog selectionDialog;
    private JPanel biomorphHolder;


    /**
     * Initialise and layout the GUI.
     * @param container The Swing component that will contain the GUI controls.
     */
    @Override
    protected void prepareGUI(Container container)
    {
        renderer = new SwingBiomorphRenderer();
        console = new SwingConsole(5);
        selectionDialog = new JDialog((JFrame) null, "Biomorph Selection", true);
        biomorphHolder = new JPanel(new GridLayout(1, 1));

        container.add(new ControlPanel(), BorderLayout.WEST);
        container.add(biomorphHolder, BorderLayout.CENTER);
        biomorphHolder.setBorder(BorderFactory.createTitledBorder("Last Evolved Biomorph"));
        biomorphHolder.add(new JLabel("Nothing generated yet.", JLabel.CENTER));
        selectionDialog.add(console, BorderLayout.CENTER);
        selectionDialog.setSize(800, 600);
        selectionDialog.validate();
    }


    /**
     * Helper method to create a background task for running the interactive evolutionary
     * algorithm.
     * @param populationSize How big the population used by the created evolution engine
     * should be.
     * @param generationCount How many generations to use when the evolution engine is
     * invoked.
     * @param random If true use random mutation, otherwise use Dawkins mutation.
     * @return A Swing task that will execute on a background thread and update
     * the GUI when it is done.
     */
    private SwingBackgroundTask<Biomorph> createTask(final int populationSize,
                                                     final int generationCount,
                                                     final boolean random)
    {
        return new SwingBackgroundTask<Biomorph>()
        {
            @Override
            protected Biomorph performTask()
            {
                EvolutionaryOperator<Biomorph> mutation = random ? new RandomBiomorphMutation(new Probability(0.12d))
                                                                 : new DawkinsBiomorphMutation();
                InteractiveSelection<Biomorph> selection = new InteractiveSelection<Biomorph>(console,
                                                                                              renderer,
                                                                                              populationSize,
                                                                                              1);
                EvolutionEngine<Biomorph> engine = new GenerationalEvolutionEngine<Biomorph>(new BiomorphFactory(),
                                                                                             mutation,
                                                                                             selection,
                                                                                             new MersenneTwisterRNG());
                engine.addEvolutionObserver(new GenerationTracker());
                return engine.evolve(populationSize,
                                     0,
                                     new GenerationCount(generationCount));
            }

            @Override
            protected void postProcessing(Biomorph result)
            {
                selectionDialog.setVisible(false);
                biomorphHolder.removeAll();
                biomorphHolder.add(renderer.render(result));
                biomorphHolder.revalidate();
            }
        };
    }


    /**
     * Entry point for running this example as an application rather than an applet.
     * @param args Program arguments (ignored).
     */
    public static void main(String[] args)
    {
        new BiomorphApplet().displayInFrame("Watchmaker Framework - Biomporphs Example");
    }


    /**
     * Simple observer to update the dialog title every time the evolution advances
     * to a new generation.
     */
    private final class GenerationTracker implements EvolutionObserver<Biomorph>
    {
        public void populationUpdate(final PopulationData<? extends Biomorph> populationData)
        {
            SwingUtilities.invokeLater(new Runnable()
            {
                public void run()
                {
                    selectionDialog.setTitle("Biomorph Selection - Generation "
                                             + (populationData.getGenerationNumber() + 1));
                }
            });
        }
    }


    /**
     * Panel for controlling the evolutionary algorithm parameters.
     */
    private final class ControlPanel extends JPanel
    {
        private JSpinner populationSpinner;
        private JSpinner generationsSpinner;
        private JComboBox mutationCombo;

        ControlPanel()
        {
            super(new BorderLayout());
            add(createInputPanel(), BorderLayout.NORTH);
            add(createButtonPanel(), BorderLayout.SOUTH);
            setBorder(BorderFactory.createTitledBorder("Evolution Controls"));
        }


        private JComponent createInputPanel()
        {
            JPanel inputPanel = new JPanel(new SpringLayout());
            JLabel populationLabel = new JLabel("Population Size: ");
            populationSpinner = new JSpinner(new SpinnerNumberModel(18, 2, 25, 1));
            populationSpinner.setEnabled(false);
            populationLabel.setLabelFor(populationSpinner);
            inputPanel.add(populationLabel);
            inputPanel.add(populationSpinner);
            JLabel generationsLabel = new JLabel("Number of Generations: ");
            generationsSpinner = new JSpinner(new SpinnerNumberModel(20, 1, 100, 1));
            generationsLabel.setLabelFor(generationsSpinner);
            inputPanel.add(generationsLabel);
            inputPanel.add(generationsSpinner);
            JLabel mutationLabel = new JLabel("Mutation Type: ");
            mutationCombo = new JComboBox(new String[]{"Dawkins (Non-random)", "Random"});
            mutationCombo.addItemListener(new ItemListener()
            {
                public void itemStateChanged(ItemEvent itemEvent)
                {
                    if (mutationCombo.getSelectedIndex() == 0)
                    {
                        populationSpinner.setValue(18);
                        populationSpinner.setEnabled(false);
                    }
                    else
                    {
                        populationSpinner.setEnabled(true);
                    }
                }
            });
            inputPanel.add(mutationLabel);
            inputPanel.add(mutationCombo);

            SpringUtilities.makeCompactGrid(inputPanel, 3, 2, 30, 6, 6, 6);

            return inputPanel;
        }


        private JComponent createButtonPanel()
        {
            JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
            JButton startButton = new JButton("Start");
            startButton.addActionListener(new ActionListener()
            {
                public void actionPerformed(ActionEvent actionEvent)
                {
                    createTask((Integer) populationSpinner.getValue(),
                               (Integer) generationsSpinner.getValue(),
                               mutationCombo.getSelectedIndex() == 1).execute();
                    selectionDialog.setVisible(true);
                }
            });
            buttonPanel.add(startButton);
            return buttonPanel;
        }
    }
}
