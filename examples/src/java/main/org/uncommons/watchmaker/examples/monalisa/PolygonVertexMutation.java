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
import org.uncommons.maths.ConstantGenerator;
import org.uncommons.maths.NumberGenerator;
import org.uncommons.watchmaker.framework.EvolutionaryOperator;
import org.uncommons.watchmaker.framework.Probability;

/**
 * Evolutionary operator for mutating individual polygons.  Polygons are mutated
 * by changing their colour and/or either adding a point, removing a point or
 * changing the position of a point.
 * @author Daniel Dyer
 */
public class PolygonVertexMutation implements EvolutionaryOperator<ColouredPolygon>
{
    private final Dimension canvasSize;
    private final NumberGenerator<Probability> mutationProbability;


    public PolygonVertexMutation(Dimension canvasSize,
                                 NumberGenerator<Probability> mutationProbability)
    {
        this.canvasSize = canvasSize;
        this.mutationProbability = mutationProbability;
    }


    public PolygonVertexMutation(Dimension canvasSize,
                                 Probability mutationProbability)
    {
        this(canvasSize, new ConstantGenerator<Probability>(mutationProbability));
    }


    public List<ColouredPolygon> apply(List<ColouredPolygon> polygons, Random rng)
    {
        List<ColouredPolygon> newPolygons = new ArrayList<ColouredPolygon>(polygons.size());
        for (ColouredPolygon polygon : polygons)
        {
            List<Point> newVertices = mutateVertices(polygon.getVertices(), rng);
            newPolygons.add(newVertices != polygon.getVertices()
                            ? new ColouredPolygon(polygon.getColour(), newVertices)
                            : polygon);
        }
        return newPolygons;
    }


    private List<Point> mutateVertices(List<Point> vertices, Random rng)
    {
        if (mutationProbability.nextValue().nextEvent(rng))
        {
            List<Point> newVertices = new ArrayList<Point>(vertices);
            switch (rng.nextInt(3))
            {
                case 0: // Add a point.
                {
                    if (newVertices.size() < 10)
                    {
                        newVertices.add(rng.nextInt(newVertices.size()),
                                        new Point(rng.nextInt(canvasSize.width), rng.nextInt(canvasSize.height)));
                    }
                    break;
                }
                case 1: // Remove a point.
                {
                    if (newVertices.size() > 3)
                    {
                        newVertices.remove(rng.nextInt(newVertices.size()));
                    }
                    break;
                }
                default: // Replace a point.
                {
                    newVertices.set(rng.nextInt(newVertices.size()),
                                    new Point(rng.nextInt(canvasSize.width), rng.nextInt(canvasSize.height)));
                }
            }
            return newVertices;
        }
        else
        {
            return vertices;
        }
    }
}
