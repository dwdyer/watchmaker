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
import org.uncommons.maths.ConstantGenerator;
import org.uncommons.maths.NumberGenerator;
import org.uncommons.watchmaker.framework.EvolutionaryOperator;
import org.uncommons.watchmaker.framework.Probability;

/**
 * Randomly mutates the colours and vertices of {@link ColouredPolygon} objects. 
 * @author Daniel Dyer
 */
public class PolygonImageMutation implements EvolutionaryOperator<List<ColouredPolygon>>
{
    private final Dimension canvasSize;
    private final NumberGenerator<Probability> colourMutationProbability;
    private final NumberGenerator<Double> colourChangeAmount;
    private final NumberGenerator<Probability> changePolygonProbability;
    private final NumberGenerator<Probability> changePointProbability;
    private final PolygonImageFactory factory;


    public PolygonImageMutation(Dimension canvasSize,
                                NumberGenerator<Probability> colourMutationProbability,
                                NumberGenerator<Double> colourChangeAmount,
                                NumberGenerator<Probability> changePolygonProbability,
                                NumberGenerator<Probability> changePointProbability,
                                PolygonImageFactory factory)
    {
        this.canvasSize = canvasSize;
        this.colourMutationProbability = colourMutationProbability;
        this.colourChangeAmount = colourChangeAmount;
        this.changePolygonProbability = changePolygonProbability;
        this.changePointProbability = changePointProbability;
        this.factory = factory;
    }

    public PolygonImageMutation(Dimension canvasSize,
                                Probability colourMutationProbability,
                                NumberGenerator<Double> colourChangeAmount,
                                Probability changePolygonProbability,
                                Probability changePointProbability,
                                PolygonImageFactory factory)
    {
        this(canvasSize,
             new ConstantGenerator<Probability>(colourMutationProbability),
             colourChangeAmount,
             new ConstantGenerator<Probability>(changePolygonProbability),
             new ConstantGenerator<Probability>(changePointProbability),
             factory);
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


    /**
     * Muate a single image.
     * @param candidate The image to mutate.
     * @param rng A source of randomness.
     * @return The (possibly) mutated version of the canidate image.
     */
    private List<ColouredPolygon> mutateImage(List<ColouredPolygon> candidate, Random rng)
    {
        List<ColouredPolygon> newPolygons = new ArrayList<ColouredPolygon>(candidate.size());
        for (ColouredPolygon polygon : candidate)
        {
            newPolygons.add(mutatePolygon(polygon, rng));
        }

        if (changePolygonProbability.nextValue().nextEvent(rng))
        {
            switch (rng.nextInt(4))
            {
                case 0: // Remove a polygon.
                {
                    if (newPolygons.size() > 3)
                    {
                        newPolygons.remove(rng.nextInt(newPolygons.size()));
                    }
                    break;
                }
                case 1: // Replace a polygon.
                {
                    newPolygons.set(rng.nextInt(newPolygons.size()),
                                    factory.createRandomPolygon(rng));
                    break;
                }
                case 2: // Add a polygon.
                {
                    if (newPolygons.size() < 50)
                    {
                        newPolygons.add(rng.nextInt(newPolygons.size() + 1),
                                        factory.createRandomPolygon(rng));
                    }
                    break;
                }
                default: // Move a polygon.
                {
                    ColouredPolygon polygon = newPolygons.remove(rng.nextInt(newPolygons.size()));
                    newPolygons.add(rng.nextInt(newPolygons.size()) + 1, polygon);
                }
            }
        }
        return newPolygons;
    }


    /**
     * Muate a single polygon.
     * @param polygon The polygon to mutate.
     * @param rng A source of randomness.
     * @return The (possibly) mutated version of the original polygon (the colour and/or
     * vertices may have been changed).
     */
    private ColouredPolygon mutatePolygon(ColouredPolygon polygon, Random rng)
    {
        return new ColouredPolygon(mutateColour(polygon.getColour(), rng),
                                   mutateVertices(polygon.getVertices(), rng));
    }


    /**
     * Mutate the specified colour.
     * @param colour The colour to mutate.
     * @param rng A source of randomness.
     * @return The (possibly) mutated colour.
     */
    private Color mutateColour(Color colour, Random rng)
    {
        if (colourMutationProbability.nextValue().nextEvent(rng))
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


    /**
     * Adjust a single component (red, green, blue or alpha) of a colour.
     * @param component The value to mutate.
     * @return The mutated component value.
     */
    private int mutateColourComponent(int component)
    {
        int mutatedComponent = (int) Math.round(component + colourChangeAmount.nextValue());
        mutatedComponent = Math.max(0, Math.min(255, mutatedComponent)); // Make sure value is in range 0-255.
        return mutatedComponent;
    }


    private List<Point> mutateVertices(List<Point> vertices, Random rng)
    {
        if (changePointProbability.nextValue().nextEvent(rng))
        {
            List<Point> newVertices = new ArrayList<Point>(vertices);
            switch (rng.nextInt(3))
            {
                case 0: // Remove a point.
                {
                    if (newVertices.size() > 3)
                    {
                        newVertices.remove(rng.nextInt(newVertices.size()));
                    }
                    break;
                }
                case 1: // Replace a point.
                {
                    newVertices.set(rng.nextInt(newVertices.size()),
                                    new Point(rng.nextInt(canvasSize.width), rng.nextInt(canvasSize.height)));
                    break;
                }
                default: // Add a point.
                {
                    if (newVertices.size() < 10)
                    {
                        newVertices.add(rng.nextInt(newVertices.size()),
                                        new Point(rng.nextInt(canvasSize.width), rng.nextInt(canvasSize.height)));
                    }
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
