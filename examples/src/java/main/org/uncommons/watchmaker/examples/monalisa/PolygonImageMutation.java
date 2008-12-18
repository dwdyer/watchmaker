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

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import org.uncommons.maths.number.ConstantGenerator;
import org.uncommons.maths.number.NumberGenerator;
import org.uncommons.watchmaker.framework.EvolutionaryOperator;
import org.uncommons.watchmaker.framework.Probability;

/**
 * Randomly mutates the set of polygons that make up an image.  Adds a polygon, removes a
 * polygon or moves a polygon.
 * @author Daniel Dyer
 */
public class PolygonImageMutation implements EvolutionaryOperator<List<ColouredPolygon>>
{
    private final NumberGenerator<Probability> changePolygonProbability;
    private final PolygonImageFactory factory;
    private final int minPolygons = 2;
    private final int maxPolygons;


    /**
     * @param changePolygonProbability A {@link NumberGenerator} that controls the probability
     * that any given polygon will be mutated.
     * @param factory Used to create new polygons.
     * @param maxPolygons The maximum number of polygons permitted in an image (minimum is 2).
     */
    public PolygonImageMutation(NumberGenerator<Probability> changePolygonProbability,
                                PolygonImageFactory factory,
                                int maxPolygons)
    {
        if (maxPolygons < minPolygons)
        {
            throw new IllegalArgumentException("Max polygons must be >= " + minPolygons);
        }
        this.changePolygonProbability = changePolygonProbability;
        this.factory = factory;
        this.maxPolygons = maxPolygons;
    }


    /**
     * @param changePolygonProbability The probability that any given polygon will be mutated.
     * @param factory Used to create new polygons.
     * @param maxPolygons The maximum number of polygons permitted in an image (minimum is 2).
     */
    public PolygonImageMutation(Probability changePolygonProbability,
                                PolygonImageFactory factory,
                                int maxPolygons)
    {
        this(new ConstantGenerator<Probability>(changePolygonProbability),
             factory,
             maxPolygons);
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
        List<ColouredPolygon> newPolygons = new ArrayList<ColouredPolygon>(candidate);

        if (changePolygonProbability.nextValue().nextEvent(rng))
        {
            switch (rng.nextInt(4))
            {
                case 0: // Remove a polygon.
                {
                    if (newPolygons.size() > minPolygons)
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
                    if (newPolygons.size() < maxPolygons)
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
}
