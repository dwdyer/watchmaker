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

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Point;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import org.uncommons.maths.random.GaussianGenerator;
import org.uncommons.watchmaker.framework.EvolutionaryOperator;
import org.uncommons.watchmaker.framework.Probability;

/**
 * Randomly mutates the colours and vertices of {@link ColouredPolygon} objects. 
 * @author Daniel Dyer
 */
public class PolygonImageMutation implements EvolutionaryOperator<List<ColouredPolygon>>
{
    private final Dimension canvasSize;
    private final Probability mutationProbability;
    private final GaussianGenerator colourChangeAmount;
    private final GaussianGenerator vertexChangeAmount;

    public PolygonImageMutation(Dimension canvasSize,
                                Probability mutationProbability,
                                GaussianGenerator colourChangeAmount,
                                GaussianGenerator vertexChangeAmount)
    {
        this.canvasSize = canvasSize;
        this.mutationProbability = mutationProbability;
        this.colourChangeAmount = colourChangeAmount;
        this.vertexChangeAmount = vertexChangeAmount;
    }

    
    public List<List<ColouredPolygon>> apply(List<List<ColouredPolygon>> selectedCandidates, Random rng)
    {
        List<List<ColouredPolygon>> mutatedCandidates = new ArrayList<List<ColouredPolygon>>(selectedCandidates.size());
        for (List<ColouredPolygon> candidate : selectedCandidates)
        {
            mutatedCandidates.add(mutateImage(candidate, rng));
        }
        return mutatedCandidates;
    }


    private List<ColouredPolygon> mutateImage(List<ColouredPolygon> candidate, Random rng)
    {
        List<ColouredPolygon> mutatedPolygons = new ArrayList<ColouredPolygon>(candidate.size());
        for (ColouredPolygon polygon : candidate)
        {
            mutatedPolygons.add(mutatePolygon(polygon, rng));
        }
        return mutatedPolygons;
    }


    private ColouredPolygon mutatePolygon(ColouredPolygon polygon, Random rng)
    {
        return new ColouredPolygon(mutateColour(polygon.getColour(), rng),
                                   mutateVertices(polygon.getVertices(), rng));
    }


    private Color mutateColour(Color colour, Random rng)
    {
        if (mutationProbability.nextEvent(rng))
        {
            return new Color(mutateColourComponent(colour.getRed()),
                             mutateColourComponent(colour.getGreen()),
                             mutateColourComponent(colour.getBlue()),
                             mutateColourComponent(colour.getAlpha()));
        }
        else
        {
            return colour;
        }
    }


    private int mutateColourComponent(int component)
    {
        int mutatedComponent = (int) Math.round(component + colourChangeAmount.nextValue());
        mutatedComponent = Math.max(0, Math.min(255, mutatedComponent)); // Make sure value is in range 0-255.
        return mutatedComponent;
    }


    private List<Point> mutateVertices(List<Point> vertices, Random rng)
    {
        List<Point> newVertices = new ArrayList<Point>(vertices.size());
        for (Point point : vertices)
        {
            if (mutationProbability.nextEvent(rng))
            {
                int x = (int) Math.round(point.x + vertexChangeAmount.nextValue());
                x = Math.max(0, Math.min(canvasSize.width - 1, x));
                int y = (int) Math.round(point.y + vertexChangeAmount.nextValue());
                y = Math.max(0, Math.min(canvasSize.width - 1, y));
                newVertices.add(new Point(x, y));
            }
            else
            {
                newVertices.add(point);
            }
        }
        return newVertices;
    }
}
