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
package org.uncommons.watchmaker.examples.monalisa;

import java.awt.Dimension;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.URL;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;
import javax.imageio.ImageIO;
import javax.swing.JComponent;
import org.uncommons.maths.random.GaussianGenerator;
import org.uncommons.maths.random.MersenneTwisterRNG;
import org.uncommons.watchmaker.framework.ConcurrentEvolutionEngine;
import org.uncommons.watchmaker.framework.EvolutionEngine;
import org.uncommons.watchmaker.framework.EvolutionaryOperator;
import org.uncommons.watchmaker.framework.Probability;
import org.uncommons.watchmaker.framework.interactive.Renderer;
import org.uncommons.watchmaker.framework.operators.EvolutionPipeline;
import org.uncommons.watchmaker.framework.operators.ListCrossover;
import org.uncommons.watchmaker.framework.operators.ListOperator;
import org.uncommons.watchmaker.framework.selection.TournamentSelection;
import org.uncommons.watchmaker.framework.termination.Stagnation;
import org.uncommons.watchmaker.swing.evolutionmonitor.EvolutionMonitor;

/**
 * This program is inspired by Roger Alsing's evolution of the Mona Lisa
 * (http://rogeralsing.com/2008/12/07/genetic-programming-evolution-of-mona-lisa/).
 * It attempts to find the combination of 50 translucent polygons that most closely
 * resembles Leonardo da Vinci's Mona Lisa.
 * @author Daniel Dyer
 */
public class MonaLisaExample
{
    private static final String IMAGE_PATH = "org/uncommons/watchmaker/examples/monalisa/monalisa.jpg";


    public static void main(String[] args) throws IOException
    {
        URL imageURL = MonaLisaExample.class.getClassLoader().getResource(IMAGE_PATH);
        BufferedImage targetImage = ImageIO.read(imageURL);

        Dimension canvasSize = new Dimension(targetImage.getWidth(), targetImage.getHeight());

        Random rng = new MersenneTwisterRNG();
        PolygonImageEvaluator evaluator = new PolygonImageEvaluator(targetImage);
        PolygonImageFactory factory = new PolygonImageFactory(canvasSize);
        EvolutionaryOperator<List<ColouredPolygon>> pipeline = createEvolutionPipeline(factory, canvasSize, rng);

        EvolutionEngine<List<ColouredPolygon>> engine
            = new ConcurrentEvolutionEngine<List<ColouredPolygon>>(factory,
                                                                   pipeline,
                                                                   evaluator,
                                                                   new TournamentSelection(new Probability(0.8)),
                                                                   rng);
        
        Renderer<List<ColouredPolygon>, JComponent> renderer = new PolygonImageSwingRenderer(targetImage);

        EvolutionMonitor<List<ColouredPolygon>> monitor = new EvolutionMonitor<List<ColouredPolygon>>(renderer);
        engine.addEvolutionObserver(monitor);
        monitor.showInFrame("Mona Lisa");

        engine.evolve(20, 1, new Stagnation(500, evaluator.isNatural()));
    }


    /**
     * Construct the combination of evolutionary operators that will be used to evolve the
     * polygon-based images.
     * @param factory A source of polygons.
     * @param canvasSize The size of the target image.
     * @param rng A source of randomness.
     * @return A complex evolutionary operator constructed from simpler operators.
     */
    private static EvolutionaryOperator<List<ColouredPolygon>> createEvolutionPipeline(PolygonImageFactory factory,
                                                                                       Dimension canvasSize,
                                                                                       Random rng)
    {
        List<EvolutionaryOperator<List<ColouredPolygon>>> operators
            = new LinkedList<EvolutionaryOperator<List<ColouredPolygon>>>();
        operators.add(new ListCrossover<ColouredPolygon>());
        operators.add(new RemovePolygonMutation(new Probability(0.02)));
        operators.add(new MovePolygonMutation(new Probability(0.02)));
        operators.add(new AddPolygonMutation(new Probability(0.02), factory, 50));
        operators.add(new ListOperator<ColouredPolygon>(new PolygonColourMutation(new Probability(0.01),
                                                                                  new GaussianGenerator(0, 10, rng))));
        operators.add(new ListOperator<ColouredPolygon>(new PolygonVertexMutation(canvasSize, new Probability(0.02))));

        return new EvolutionPipeline<List<ColouredPolygon>>(operators);
    }
}
