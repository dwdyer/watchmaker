// ============================================================================
//   Copyright 2006, 2007 Daniel W. Dyer
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
package org.uncommons.watchmaker.examples.biomorphs;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.BorderFactory;
import javax.swing.JApplet;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.SpinnerNumberModel;
import javax.swing.SpringLayout;
import javax.swing.SwingUtilities;
import org.uncommons.gui.SpringUtilities;
import org.uncommons.gui.SwingBackgroundTask;
import org.uncommons.maths.random.MersenneTwisterRNG;
import org.uncommons.watchmaker.framework.EvolutionEngine;
import org.uncommons.watchmaker.framework.EvolutionObserver;
import org.uncommons.watchmaker.framework.PopulationData;
import org.uncommons.watchmaker.framework.SelectionStrategy;
import org.uncommons.watchmaker.framework.StandaloneEvolutionEngine;
import org.uncommons.watchmaker.framework.interactive.InteractiveSelection;
import org.uncommons.watchmaker.framework.interactive.NullFitnessEvaluator;
import org.uncommons.watchmaker.framework.interactive.Renderer;
import org.uncommons.watchmaker.framework.interactive.SwingConsole;

/**
 * Watchmaker Framework implementation of Dawkin's biomorph program. 
 * @author Daniel Dyer
 */
public class BiomorphApplet extends JApplet
{
    private final Renderer<Biomorph, JComponent> renderer = new SwingBiomorphRenderer();
    private final SwingConsole console = new SwingConsole(4);
    private final JDialog selectionDialog = new JDialog((JFrame) null, "Biomorph Selection", true);
    private final JPanel biomorphHolder = new JPanel(new GridLayout(1, 1));

    public BiomorphApplet()
    {
        setLayout(new GridLayout(1, 2));
        add(new ControlPanel());
        add(biomorphHolder);
        biomorphHolder.setBorder(BorderFactory.createTitledBorder("Last Evolved Biomorph"));
        biomorphHolder.add(new JLabel("Nothing generated yet.", JLabel.CENTER));
        selectionDialog.add(console, BorderLayout.CENTER);
        selectionDialog.setSize(800, 600);
        selectionDialog.validate();
    }


    /**
     * Helper method to create a background task for running the interactive evolutionary
     * algorithm.
     * @return A Swing task that will execute on a background thread and update
     * the GUI when it is done.
     */
    private SwingBackgroundTask<Biomorph> createTask(final int populationSize,
                                                     final int generationCount)
    {
        return new SwingBackgroundTask<Biomorph>()
        {
            protected Biomorph performTask()
            {
                SelectionStrategy<Biomorph> selection = new InteractiveSelection<Biomorph>(console, renderer, populationSize, 1);
                EvolutionEngine<Biomorph> engine = new StandaloneEvolutionEngine<Biomorph>(new BiomorphFactory(),
                                                                                           new DawkinsBiomorphMutation(),
                                                                                           // new RandomBiomorphMutation(0.1d),
                                                                                           new NullFitnessEvaluator(),
                                                                                           selection,
                                                                                           new MersenneTwisterRNG());
                engine.addEvolutionObserver(new GenerationTracker());
                return engine.evolve(populationSize, 0, generationCount);
            }

            protected void postProcessing(Biomorph result)
            {
                selectionDialog.setVisible(false);
                biomorphHolder.removeAll();
                biomorphHolder.add(renderer.render(result));
                biomorphHolder.revalidate();
            }
        };
    }


    private final class GenerationTracker implements EvolutionObserver<Biomorph>
    {
        public void populationUpdate(final PopulationData<Biomorph> populationData)
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

        public ControlPanel()
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
            populationSpinner = new JSpinner(new SpinnerNumberModel(12, 2, 24, 1));
            populationLabel.setLabelFor(populationSpinner);
            inputPanel.add(populationLabel);
            inputPanel.add(populationSpinner);
            JLabel generationsLabel = new JLabel("Number of Generations: ");
            generationsSpinner = new JSpinner(new SpinnerNumberModel(20, 1, 100, 1));
            generationsLabel.setLabelFor(generationsSpinner);
            inputPanel.add(generationsLabel);
            inputPanel.add(generationsSpinner);

            SpringUtilities.makeCompactGrid(inputPanel, 2, 2, 30, 6, 6, 6);
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
                               (Integer) generationsSpinner.getValue()).execute();
                    selectionDialog.setVisible(true);
                }
            });
            buttonPanel.add(startButton);
            return buttonPanel;
        }
    }
}
