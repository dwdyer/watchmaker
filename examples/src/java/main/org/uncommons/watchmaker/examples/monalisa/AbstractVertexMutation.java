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
import java.awt.Point;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import org.uncommons.maths.number.NumberGenerator;
import org.uncommons.maths.random.Probability;
import org.uncommons.watchmaker.framework.EvolutionaryOperator;

/**
 * Base class for mutation operators that modify the points of polygons in an
 * image.
 * @author Daniel Dyer
 */
abstract class AbstractVertexMutation implements EvolutionaryOperator<ColouredPolygon>
{
    private final Dimension canvasSize;
    private final NumberGenerator<Probability> mutationProbability;


    /**
     * @param mutationProbability A {@link NumberGenerator} that controls the probability
     * that a polygon's points will be mutated.
     * @param canvasSize The size of the canvas.  Used to constrain the positions of the points.
     */
    protected AbstractVertexMutation(NumberGenerator<Probability> mutationProbability,
                                     Dimension canvasSize)
    {
        this.mutationProbability = mutationProbability;
        this.canvasSize = canvasSize;
    }


    /**
     * @return The dimensions of the target image.
     */
    protected Dimension getCanvasSize()
    {
        return canvasSize;
    }


    /**
     * @return The {@link NumberGenerator} that provides the mutation probability.
     */
    protected NumberGenerator<Probability> getMutationProbability()
    {
        return mutationProbability;
    }


    /**
     * Applies the mutation to each polygon in the list provided according to the
     * pre-configured mutation probability.  If the probability is 0.1, approximately
     * 10% of the individuals will be mutated.  The actual mutation operation is
     * defined in the sub-class implementation of the
     * {@link #mutateVertices(java.util.List, java.util.Random)} method.
     * @param polygons The list of polygons to be mutated.
     * @param rng A source of randomness.
     * @return The polygons after mutation.  None, some or all will have been
     * modified. 
     */
    public List<ColouredPolygon> apply(List<ColouredPolygon> polygons, Random rng)
    {
        List<ColouredPolygon> newPolygons = new ArrayList<ColouredPolygon>(polygons.size());
        for (ColouredPolygon polygon : polygons)
        {
            List<Point> newVertices = mutateVertices(polygon.getVertices(), rng);
            newPolygons.add(newVertices == polygon.getVertices()
                            ? polygon
                            : new ColouredPolygon(polygon.getColour(), newVertices));
        }
        return newPolygons;
    }


    /**
     * Implemented in sub-classes to perform the mutation of the vertices.
     * @param vertices A list of the points that make up the polygon.
     * @param rng A source of randomness.
     * @return A mutated list of points.
     */
    protected abstract List<Point> mutateVertices(List<Point> vertices, Random rng);
}
