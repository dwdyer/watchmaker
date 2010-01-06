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
package org.uncommons.watchmaker.examples.monalisa;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.Random;
import javax.imageio.ImageIO;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.SpinnerNumberModel;
import org.uncommons.maths.random.Probability;
import org.uncommons.maths.random.XORShiftRNG;
import org.uncommons.swing.SwingBackgroundTask;
import org.uncommons.watchmaker.examples.AbstractExampleApplet;
import org.uncommons.watchmaker.framework.CachingFitnessEvaluator;
import org.uncommons.watchmaker.framework.EvolutionEngine;
import org.uncommons.watchmaker.framework.EvolutionaryOperator;
import org.uncommons.watchmaker.framework.FitnessEvaluator;
import org.uncommons.watchmaker.framework.GenerationalEvolutionEngine;
import org.uncommons.watchmaker.framework.SelectionStrategy;
import org.uncommons.watchmaker.framework.TerminationCondition;
import org.uncommons.watchmaker.framework.interactive.Renderer;
import org.uncommons.watchmaker.framework.selection.TournamentSelection;
import org.uncommons.watchmaker.framework.termination.Stagnation;
import org.uncommons.watchmaker.swing.AbortControl;
import org.uncommons.watchmaker.swing.ProbabilityParameterControl;
import org.uncommons.watchmaker.swing.evolutionmonitor.EvolutionMonitor;

/**
 * This program is inspired by Roger Alsing's evolution of the Mona Lisa
 * (http://rogeralsing.com/2008/12/07/genetic-programming-evolution-of-mona-lisa/).
 * It attempts to find the combination of 50 translucent polygons that most closely
 * resembles Leonardo da Vinci's Mona Lisa.
 * @author Daniel Dyer
 */
public class MonaLisaApplet extends AbstractExampleApplet
{
    private static final String IMAGE_PATH = "org/uncommons/watchmaker/examples/monalisa/monalisa.jpg";

    private ProbabilitiesPanel probabilitiesPanel;
    private EvolutionMonitor<List<ColouredPolygon>> monitor;
    private JButton startButton;
    private AbortControl abort;
    private JSpinner populationSpinner;
    private JSpinner elitismSpinner;
    private ProbabilityParameterControl selectionPressureControl;
    private BufferedImage targetImage;


    @Override
    public void init()
    {
        try
        {
            URL imageURL = MonaLisaApplet.class.getClassLoader().getResource(IMAGE_PATH);
            targetImage = ImageIO.read(imageURL);
            super.init();
        }
        catch (IOException ex)
        {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, ex, "Failed to Load Image", JOptionPane.ERROR_MESSAGE);

        }
    }


    /**
     * Initialise and layout the GUI.
     * @param container The Swing component that will contain the GUI controls.
     */
    @Override
    protected void prepareGUI(Container container)
    {
        probabilitiesPanel = new ProbabilitiesPanel();
        probabilitiesPanel.setBorder(BorderFactory.createTitledBorder("Evolution Probabilities"));        
        JPanel controls = new JPanel(new BorderLayout());
        controls.add(createParametersPanel(), BorderLayout.NORTH);
        controls.add(probabilitiesPanel, BorderLayout.SOUTH);
        container.add(controls, BorderLayout.NORTH);

        Renderer<List<ColouredPolygon>, JComponent> renderer = new PolygonImageSwingRenderer(targetImage);
        monitor = new EvolutionMonitor<List<ColouredPolygon>>(renderer, false);
        container.add(monitor.getGUIComponent(), BorderLayout.CENTER);
    }


    private JComponent createParametersPanel()
    {
        Box parameters = Box.createHorizontalBox();
        parameters.add(Box.createHorizontalStrut(10));
        final JLabel populationLabel = new JLabel("Population Size: ");
        parameters.add(populationLabel);
        parameters.add(Box.createHorizontalStrut(10));
        populationSpinner = new JSpinner(new SpinnerNumberModel(10, 2, 1000, 1));
        populationSpinner.setMaximumSize(populationSpinner.getMinimumSize());
        parameters.add(populationSpinner);
        parameters.add(Box.createHorizontalStrut(10));
        final JLabel elitismLabel = new JLabel("Elitism: ");
        parameters.add(elitismLabel);
        parameters.add(Box.createHorizontalStrut(10));
        elitismSpinner = new JSpinner(new SpinnerNumberModel(2, 1, 1000, 1));
        elitismSpinner.setMaximumSize(elitismSpinner.getMinimumSize());
        parameters.add(elitismSpinner);
        parameters.add(Box.createHorizontalStrut(10));

        parameters.add(new JLabel("Selection Pressure: "));
        parameters.add(Box.createHorizontalStrut(10));
        selectionPressureControl = new ProbabilityParameterControl(Probability.EVENS,
                                                                   Probability.ONE,
                                                                   2,
                                                                   new Probability(0.7));
        parameters.add(selectionPressureControl.getControl());
        parameters.add(Box.createHorizontalStrut(10));

        startButton = new JButton("Start");
        abort = new AbortControl();        
        startButton.addActionListener(new ActionListener()
        {
            public void actionPerformed(ActionEvent ev)
            {
                abort.getControl().setEnabled(true);
                populationLabel.setEnabled(false);
                populationSpinner.setEnabled(false);
                elitismLabel.setEnabled(false);
                elitismSpinner.setEnabled(false);
                startButton.setEnabled(false);
                new EvolutionTask((Integer) populationSpinner.getValue(),
                                  (Integer) elitismSpinner.getValue(),
                                  abort.getTerminationCondition(),
                                  new Stagnation(1000, false)).execute();
            }
        });
        abort.getControl().setEnabled(false);
        parameters.add(startButton);
        parameters.add(abort.getControl());
        parameters.add(Box.createHorizontalStrut(10));

        parameters.setBorder(BorderFactory.createTitledBorder("Parameters"));
        return parameters;
    }


    /**
     * Entry point for running this example as an application rather than an applet.
     * @param args Program arguments (ignored).
     * @throws IOException If there is a problem loading the target image. 
     */
    public static void main(String[] args) throws IOException
    {
        MonaLisaApplet gui = new MonaLisaApplet();
        // If a URL is specified as an argument, use that image.  Otherwise use the default Mona Lisa picture.
        URL imageURL = args.length > 0
                       ? new URL(args[0])
                       : MonaLisaApplet.class.getClassLoader().getResource(IMAGE_PATH);
        gui.targetImage = ImageIO.read(imageURL);
        gui.displayInFrame("Watchmaker Framework - Mona Lisa Example");
    }


    /**
     * The task that acutally performs the evolution.
     */
    private class EvolutionTask extends SwingBackgroundTask<List<ColouredPolygon>>
    {
        private final int populationSize;
        private final int eliteCount;
        private final TerminationCondition[] terminationConditions;


        EvolutionTask(int populationSize, int eliteCount, TerminationCondition... terminationConditions)
        {
            this.populationSize = populationSize;
            this.eliteCount = eliteCount;
            this.terminationConditions = terminationConditions;
        }


        @Override
        protected List<ColouredPolygon> performTask() throws Exception
        {
            Dimension canvasSize = new Dimension(targetImage.getWidth(), targetImage.getHeight());

            Random rng = new XORShiftRNG();
            FitnessEvaluator<List<ColouredPolygon>> evaluator
                = new CachingFitnessEvaluator<List<ColouredPolygon>>(new PolygonImageEvaluator(targetImage));
            PolygonImageFactory factory = new PolygonImageFactory(canvasSize);
            EvolutionaryOperator<List<ColouredPolygon>> pipeline
                = probabilitiesPanel.createEvolutionPipeline(factory, canvasSize, rng);

            SelectionStrategy<Object> selection = new TournamentSelection(selectionPressureControl.getNumberGenerator());
            EvolutionEngine<List<ColouredPolygon>> engine
                = new GenerationalEvolutionEngine<List<ColouredPolygon>>(factory,
                                                                         pipeline,
                                                                         evaluator,
                                                                         selection,
                                                                         rng);
            engine.addEvolutionObserver(monitor);

            return engine.evolve(populationSize, eliteCount, terminationConditions);
        }


        @Override
        protected void postProcessing(List<ColouredPolygon> result)
        {
            abort.reset();
            abort.getControl().setEnabled(false);
            populationSpinner.setEnabled(true);
            elitismSpinner.setEnabled(true);
            startButton.setEnabled(true);
        }


        @Override
        protected void onError(Throwable throwable)
        {
            super.onError(throwable);
            postProcessing(null);
        }
    }
}
