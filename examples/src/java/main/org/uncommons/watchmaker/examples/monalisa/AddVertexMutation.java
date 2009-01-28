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
import java.awt.Point;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import org.uncommons.maths.number.ConstantGenerator;
import org.uncommons.maths.number.NumberGenerator;
import org.uncommons.watchmaker.framework.Probability;

/**
 * Evolutionary operator for mutating individual polygons.  Polygons are mutated
 * by adding a point, according to some probability.
 * @author Daniel Dyer
 */
public class AddVertexMutation extends AbstractVertexMutation
{
    private static final int MAX_VERTEX_COUNT = 10;

    /**
     * @param mutationProbability A {@link NumberGenerator} that controls the probability
     * that a point will be added.
     * @param canvasSize The size of the canvas.  Used to constrain the positions of the points.
     */
    public AddVertexMutation(Dimension canvasSize,
                             NumberGenerator<Probability> mutationProbability)
    {
        super(mutationProbability, canvasSize);
    }


    /**
     * @param mutationProbability The probability that a point will be added.
     * @param canvasSize The size of the canvas.  Used to constrain the positions of the points.
     */
    public AddVertexMutation(Dimension canvasSize,
                             Probability mutationProbability)
    {
        this(canvasSize, new ConstantGenerator<Probability>(mutationProbability));
    }


    protected List<Point> mutateVertices(List<Point> vertices, Random rng)
    {
        // A single point is added with the configured probability, unless
        // we already have the maximum permitted number of points.
        if (vertices.size() < MAX_VERTEX_COUNT && mutationProbability.nextValue().nextEvent(rng))
        {
            List<Point> newVertices = new ArrayList<Point>(vertices);
            newVertices.add(rng.nextInt(newVertices.size()),
                            new Point(rng.nextInt(canvasSize.width), rng.nextInt(canvasSize.height)));
            return newVertices;
        }
        else // Nothing changed.
        {
            return vertices;
        }
    }
}
