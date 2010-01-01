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

import java.awt.Dimension;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SpringLayout;
import org.uncommons.maths.number.ConstantGenerator;
import org.uncommons.maths.random.GaussianGenerator;
import org.uncommons.maths.random.Probability;
import org.uncommons.swing.SpringUtilities;
import org.uncommons.watchmaker.framework.EvolutionaryOperator;
import org.uncommons.watchmaker.framework.operators.EvolutionPipeline;
import org.uncommons.watchmaker.framework.operators.ListCrossover;
import org.uncommons.watchmaker.framework.operators.ListOperator;
import org.uncommons.watchmaker.swing.ProbabilityParameterControl;

/**
 * Panel that displays controls for the Mona Lisa example program.  These
 * controls allow the evolution parameters to be tweaked.
 * @author Daniel Dyer
 */
class ProbabilitiesPanel extends JPanel
{
    private static final Probability ONE_TENTH = new Probability(0.1d);
    
    private final ProbabilityParameterControl addPolygonControl;
    private final ProbabilityParameterControl removePolygonControl;
    private final ProbabilityParameterControl movePolygonControl;
    private final ProbabilityParameterControl crossOverControl;
    private final ProbabilityParameterControl addVertexControl;
    private final ProbabilityParameterControl removeVertexControl;
    private final ProbabilityParameterControl moveVertexControl;
    private final ProbabilityParameterControl changeColourControl;


    ProbabilitiesPanel()
    {
        super(new SpringLayout());
        addPolygonControl = new ProbabilityParameterControl(Probability.ZERO,
                                                            ONE_TENTH,
                                                            3,
                                                            new Probability(0.02));
        add(new JLabel("Add Polygon: "));
        add(addPolygonControl.getControl());
        addPolygonControl.setDescription("For each IMAGE, the probability that a new "
                                         + "randomly-generated polygon will be added.");

        addVertexControl = new ProbabilityParameterControl(Probability.ZERO,
                                                           ONE_TENTH,
                                                           3,
                                                           new Probability(0.01));
        add(new JLabel("Add Vertex: "));
        add(addVertexControl.getControl());
        addVertexControl.setDescription("For each POLYGON, the probability that a new "
                                        + "randomly-generated vertex will be added.");

        removePolygonControl = new ProbabilityParameterControl(Probability.ZERO,
                                                               ONE_TENTH,
                                                               3,
                                                               new Probability(0.02));
        add(new JLabel("Remove Polygon: "));
        add(removePolygonControl.getControl());
        removePolygonControl.setDescription("For each IMAGE, the probability that a "
                                            + "randomly-selected polygon will be discarded.");

        removeVertexControl = new ProbabilityParameterControl(Probability.ZERO,
                                                              ONE_TENTH,
                                                              3,
                                                              new Probability(0.01));
        add(new JLabel("Remove Vertex: "));
        add(removeVertexControl.getControl());
        removeVertexControl.setDescription("For each POLYGON, the probability that a "
                                           + "randomly-selected vertex will be discarded.");

        movePolygonControl = new ProbabilityParameterControl(Probability.ZERO,
                                                             ONE_TENTH,
                                                             3,
                                                             new Probability(0.01));
        add(new JLabel("Reorder Polygons: "));
        add(movePolygonControl.getControl());
        movePolygonControl.setDescription("For each IMAGE, the probability that the z-positions "
                                          + "of two randomly-selected polygons will be swapped.");

        moveVertexControl = new ProbabilityParameterControl(Probability.ZERO,
                                                            ONE_TENTH,
                                                            3,
                                                            new Probability(0.03));
        add(new JLabel("Move Vertex: "));
        add(moveVertexControl.getControl());
        moveVertexControl.setDescription("For each POLYGON, the probability that a randomly-selected "
                                         + "vertex will be displaced.");

        crossOverControl = new ProbabilityParameterControl(Probability.ZERO,
                                                           Probability.ONE,
                                                           2,
                                                           Probability.ONE);
        add(new JLabel("Cross-over: "));
        add(crossOverControl.getControl());
        crossOverControl.setDescription("For each PAIR of parent IMAGES, the probability that "
                                        + "2-point cross-over is applied.");

        changeColourControl = new ProbabilityParameterControl(Probability.ZERO,
                                                              ONE_TENTH,
                                                              3,
                                                              new Probability(0.01));
        add(new JLabel("Change Colour: "));
        add(changeColourControl.getControl());
        changeColourControl.setDescription("For each POLYGON, the probability that its colour will be mutated.");

        // Set component names for easy look-up from tests.
        addPolygonControl.getControl().setName("AddPolygon");
        removePolygonControl.getControl().setName("RemovePolygon");
        movePolygonControl.getControl().setName("MovePolygon");
        crossOverControl.getControl().setName("Cross-over");
        addVertexControl.getControl().setName("AddVertex");
        removeVertexControl.getControl().setName("RemoveVertex");
        moveVertexControl.getControl().setName("MoveVertex");
        changeColourControl.getControl().setName("ChangeColour");

        SpringUtilities.makeCompactGrid(this, 4, 4, 10, 0, 10, 0);
    }


    /**
     * Construct the combination of evolutionary operators that will be used to evolve the
     * polygon-based images.
     * @param factory A source of polygons.
     * @param canvasSize The size of the target image.
     * @param rng A source of randomness.
     * @return A complex evolutionary operator constructed from simpler operators.
     */
    public EvolutionaryOperator<List<ColouredPolygon>> createEvolutionPipeline(PolygonImageFactory factory,
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
