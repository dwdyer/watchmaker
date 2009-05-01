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
package org.uncommons.watchmaker.examples.monalisa;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.net.URL;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;
import javax.imageio.ImageIO;
import javax.swing.BorderFactory;
import javax.swing.JApplet;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.SpinnerNumberModel;
import javax.swing.SpringLayout;
import javax.swing.SwingUtilities;
import org.uncommons.maths.number.ConstantGenerator;
import org.uncommons.maths.random.GaussianGenerator;
import org.uncommons.maths.random.Probability;
import org.uncommons.maths.random.XORShiftRNG;
import org.uncommons.swing.SpringUtilities;
import org.uncommons.swing.SwingBackgroundTask;
import org.uncommons.watchmaker.framework.CachingFitnessEvaluator;
import org.uncommons.watchmaker.framework.ConcurrentEvolutionEngine;
import org.uncommons.watchmaker.framework.EvolutionEngine;
import org.uncommons.watchmaker.framework.EvolutionaryOperator;
import org.uncommons.watchmaker.framework.FitnessEvaluator;
import org.uncommons.watchmaker.framework.TerminationCondition;
import org.uncommons.watchmaker.framework.interactive.Renderer;
import org.uncommons.watchmaker.framework.operators.EvolutionPipeline;
import org.uncommons.watchmaker.framework.operators.ListCrossover;
import org.uncommons.watchmaker.framework.operators.ListOperator;
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
public class MonaLisaApplet extends JApplet
{
    private static final String IMAGE_PATH = "org/uncommons/watchmaker/examples/monalisa/monalisa.jpg";
    private static final Probability ONE_TENTH = new Probability(0.1d);

    private EvolutionMonitor<List<ColouredPolygon>> monitor;
    private JButton startButton;
    private AbortControl abort;
    private ProbabilityParameterControl addPolygonControl;
    private ProbabilityParameterControl removePolygonControl;
    private ProbabilityParameterControl movePolygonControl;
    private ProbabilityParameterControl crossOverControl;
    private ProbabilityParameterControl addVertexControl;
    private ProbabilityParameterControl removeVertexControl;
    private ProbabilityParameterControl moveVertexControl;
    private ProbabilityParameterControl changeColourControl;
    private JSpinner populationSpinner;
    private JSpinner elitismSpinner;

    @Override
    public void init()
    {
        try
        {
            URL imageURL = MonaLisaApplet.class.getClassLoader().getResource(IMAGE_PATH);
            final BufferedImage targetImage = ImageIO.read(imageURL);
            SwingUtilities.invokeAndWait(new Runnable()
            {
                public void run()
                {
                    Renderer<List<ColouredPolygon>, JComponent> renderer = new PolygonImageSwingRenderer(targetImage);
                    monitor = new EvolutionMonitor<List<ColouredPolygon>>(renderer);
                    
                    add(createControls(targetImage), BorderLayout.NORTH);
                    add(monitor.getGUIComponent(), BorderLayout.CENTER);
                }
            });
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, ex, "Error Occurred", JOptionPane.ERROR_MESSAGE);
        }
    }


    private JComponent createControls(BufferedImage targetImage)
    {
        JPanel controls = new JPanel(new BorderLayout());

        JPanel probabilities = new JPanel(new SpringLayout());

        addPolygonControl = new ProbabilityParameterControl(Probability.ZERO,
                                                            ONE_TENTH,
                                                            3,
                                                            new Probability(0.02));
        probabilities.add(new JLabel("Add Polygon: "));
        probabilities.add(addPolygonControl.getControl());
        addPolygonControl.setDescription("For each IMAGE, the probability that a new randomly-generated polygon will be added.");

        addVertexControl = new ProbabilityParameterControl(Probability.ZERO,
                                                           ONE_TENTH,
                                                           3,
                                                           new Probability(0.01));
        probabilities.add(new JLabel("Add Vertex: "));
        probabilities.add(addVertexControl.getControl());
        addVertexControl.setDescription("For each POLYGON, the probability that a new randomly-generated vertex will be added.");

        removePolygonControl = new ProbabilityParameterControl(Probability.ZERO,
                                                               ONE_TENTH,
                                                               3,
                                                               new Probability(0.02));
        probabilities.add(new JLabel("Remove Polygon: "));
        probabilities.add(removePolygonControl.getControl());
        removePolygonControl.setDescription("For each IMAGE, the probability that a randomly-selected polygon will be discarded.");

        removeVertexControl = new ProbabilityParameterControl(Probability.ZERO,
                                                              ONE_TENTH,
                                                              3,
                                                              new Probability(0.01));
        probabilities.add(new JLabel("Remove Vertex: "));
        probabilities.add(removeVertexControl.getControl());
        removeVertexControl.setDescription("For each POLYGON, the probability that a randomly-selected vertex will be discarded.");

        movePolygonControl = new ProbabilityParameterControl(Probability.ZERO,
                                                             ONE_TENTH,
                                                             3,
                                                             new Probability(0.02));
        probabilities.add(new JLabel("Reorder Polygons: "));
        probabilities.add(movePolygonControl.getControl());
        movePolygonControl.setDescription("For each IMAGE, the probability that the z-positions of two randomly-selected polygons will be swapped.");

        moveVertexControl = new ProbabilityParameterControl(Probability.ZERO,
                                                            ONE_TENTH,
                                                            3,
                                                            new Probability(0.03));
        probabilities.add(new JLabel("Move Vertex: "));
        probabilities.add(moveVertexControl.getControl());
        moveVertexControl.setDescription("For each POLYGON, the probability that a randomly-selected vertex will be displaced.");

        crossOverControl = new ProbabilityParameterControl(Probability.ZERO,
                                                           Probability.ONE,
                                                           2,
                                                           Probability.ONE);
        probabilities.add(new JLabel("Cross-over: "));
        probabilities.add(crossOverControl.getControl());
        crossOverControl.setDescription("For each PAIR of parent IMAGES, the probability that 2-point cross-over is applied.");

        changeColourControl = new ProbabilityParameterControl(Probability.ZERO,
                                                              ONE_TENTH,
                                                              3,
                                                              new Probability(0.01));
        probabilities.add(new JLabel("Change Colour: "));
        probabilities.add(changeColourControl.getControl());
        changeColourControl.setDescription("For each POLYGON, the probability that its colour will be mutated.");

        SpringUtilities.makeCompactGrid(probabilities, 4, 4, 0, 0, 10, 0);

        controls.add(probabilities, BorderLayout.NORTH);
        controls.add(createParametersPanel(targetImage), BorderLayout.SOUTH);
        controls.setBorder(BorderFactory.createTitledBorder("Evolution Parameters"));
        return controls;
    }


    private JComponent createParametersPanel(final BufferedImage targetImage)
    {
        JPanel parameters = new JPanel(new FlowLayout(FlowLayout.LEFT));
        parameters.add(new JLabel("Population Size: "));
        populationSpinner = new JSpinner(new SpinnerNumberModel(15, 2, 1000, 1));
        parameters.add(populationSpinner);
        parameters.add(new JLabel("Elitism: "));
        elitismSpinner = new JSpinner(new SpinnerNumberModel(3, 1, 1000, 1));
        parameters.add(elitismSpinner);

        JPanel buttons = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        startButton = new JButton("Start");
        abort = new AbortControl();        
        startButton.addActionListener(new ActionListener()
        {
            public void actionPerformed(ActionEvent ev)
            {
                abort.getControl().setEnabled(true);
                populationSpinner.setEnabled(false);
                elitismSpinner.setEnabled(false);
                startButton.setEnabled(false);
                createTask(targetImage,
                           (Integer) populationSpinner.getValue(),
                           (Integer) elitismSpinner.getValue(),
                           abort.getTerminationCondition(),
                           new Stagnation(1000, false)).execute();
            }
        });
        abort.getControl().setEnabled(false);
        buttons.add(startButton);
        buttons.add(abort.getControl());

        JPanel wrapper = new JPanel(new BorderLayout());
        wrapper.add(parameters, BorderLayout.CENTER);
        wrapper.add(buttons, BorderLayout.EAST);
        return wrapper;
    }


    private SwingBackgroundTask<List<ColouredPolygon>> createTask(final BufferedImage targetImage,
                                                                  final int populationSize,
                                                                  final int eliteCount,
                                                                  final TerminationCondition... terminationConditions)
    {
        return new SwingBackgroundTask<List<ColouredPolygon>>()
        {
            @Override
            protected List<ColouredPolygon> performTask() throws Exception
            {
                Dimension canvasSize = new Dimension(targetImage.getWidth(), targetImage.getHeight());

                Random rng = new XORShiftRNG();
                FitnessEvaluator<List<ColouredPolygon>> evaluator
                    = new CachingFitnessEvaluator<List<ColouredPolygon>>(new PolygonImageEvaluator(targetImage));
                PolygonImageFactory factory = new PolygonImageFactory(canvasSize);
                EvolutionaryOperator<List<ColouredPolygon>> pipeline = createEvolutionPipeline(factory, canvasSize, rng);

                EvolutionEngine<List<ColouredPolygon>> engine
                    = new ConcurrentEvolutionEngine<List<ColouredPolygon>>(factory,
                                                                           pipeline,
                                                                           evaluator,
                                                                           new TournamentSelection(new Probability(0.8)),
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
        };
    }


    /**
     * Construct the combination of evolutionary operators that will be used to evolve the
     * polygon-based images.
     * @param factory A source of polygons.
     * @param canvasSize The size of the target image.
     * @param rng A source of randomness.
     * @return A complex evolutionary operator constructed from simpler operators.
     */
    private EvolutionaryOperator<List<ColouredPolygon>> createEvolutionPipeline(PolygonImageFactory factory,
                                                                                Dimension canvasSize,
                                                                                Random rng)
    {
        List<EvolutionaryOperator<List<ColouredPolygon>>> operators
            = new LinkedList<EvolutionaryOperator<List<ColouredPolygon>>>();
        operators.add(new ListCrossover<ColouredPolygon>(new ConstantGenerator<Integer>(2),
                                                         crossOverControl.getNumberGenerator()));
        operators.add(new RemovePolygonMutation(removePolygonControl.getNumberGenerator()));
        operators.add(new MovePolygonMutation(movePolygonControl.getNumberGenerator()));
        operators.add(new ListOperator<ColouredPolygon>(new RemoveVertexMutation(canvasSize,
                                                                                 removeVertexControl.getNumberGenerator())));
        operators.add(new ListOperator<ColouredPolygon>(new AdjustVertexMutation(canvasSize,
                                                                                 moveVertexControl.getNumberGenerator(),
                                                                                 new GaussianGenerator(0, 3, rng))));
        operators.add(new ListOperator<ColouredPolygon>(new AddVertexMutation(canvasSize,
                                                                              addVertexControl.getNumberGenerator())));
        operators.add(new ListOperator<ColouredPolygon>(new PolygonColourMutation(changeColourControl.getNumberGenerator(),
                                                                                  new GaussianGenerator(0, 20, rng))));
        operators.add(new AddPolygonMutation(addPolygonControl.getNumberGenerator(), factory, 50));
        return new EvolutionPipeline<List<ColouredPolygon>>(operators);
    }
}
