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
import org.uncommons.maths.Maths;
import org.uncommons.maths.number.ConstantGenerator;
import org.uncommons.maths.number.NumberGenerator;
import org.uncommons.maths.random.Probability;

/**
 * Evolutionary operator for mutating individual polygons.  Polygons are mutated
 * by moving a point, according to some probability.
 * @author Daniel Dyer
 */
public class AdjustVertexMutation extends AbstractVertexMutation
{
    private final NumberGenerator<? extends Number> changeAmount;

    /**
     * @param mutationProbability A {@link NumberGenerator} that controls the
     * probability that a point will be moved.
     * @param canvasSize The size of the canvas.  Used to constrain the positions
     * of the points.
     * @param changeAmount A {@link NumberGenerator} that controls the distance
     * that points are moved (in pixels).  Should generate both positive and
     * negative values.
     */
    public AdjustVertexMutation(Dimension canvasSize,
                                NumberGenerator<Probability> mutationProbability,
                                NumberGenerator<? extends Number> changeAmount)
    {
        super(mutationProbability, canvasSize);
        this.changeAmount = changeAmount;
    }


    /**
     * @param mutationProbability The probability that a point will be moved.
     * @param canvasSize The size of the canvas.  Used to constrain the positions
     * of the points.
     * @param changeAmount A {@link NumberGenerator} that controls the distance
     * that points are moved (in pixels).  Should generate both positive and
     * negative values.
     */
    public AdjustVertexMutation(Dimension canvasSize,
                                Probability mutationProbability,
                                NumberGenerator<? extends Number> changeAmount)
    {
        this(canvasSize, new ConstantGenerator<Probability>(mutationProbability), changeAmount);
    }


    @Override
    protected List<Point> mutateVertices(List<Point> vertices, Random rng)
    {
        // A single point is modified with the configured probability.
        if (getMutationProbability().nextValue().nextEvent(rng))
        {
            List<Point> newVertices = new ArrayList<Point>(vertices);
            int xDelta = (int) Math.round(changeAmount.nextValue().doubleValue());
            int yDelta = (int) Math.round(changeAmount.nextValue().doubleValue());
            int index = rng.nextInt(newVertices.size());
            Point oldPoint = newVertices.get(index);
            int newX = oldPoint.x + xDelta;
            int newY = oldPoint.y + yDelta;
            newX = Maths.restrictRange(newX, 0, getCanvasSize().width - 1);
            newY = Maths.restrictRange(newY, 0, getCanvasSize().height - 1);
            newVertices.set(index, new Point(newX, newY));
            return newVertices;
        }
        else // Nothing changed.
        {
            return vertices;
        }
    }
}
