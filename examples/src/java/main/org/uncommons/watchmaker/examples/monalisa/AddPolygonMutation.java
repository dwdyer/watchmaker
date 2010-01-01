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

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import org.uncommons.maths.number.ConstantGenerator;
import org.uncommons.maths.number.NumberGenerator;
import org.uncommons.maths.random.Probability;
import org.uncommons.watchmaker.framework.EvolutionaryOperator;

/**
 * Randomly mutates the polygons that make up an image by adding a polygon
 * according to some probability.
 * @author Daniel Dyer
 */
public class AddPolygonMutation implements EvolutionaryOperator<List<ColouredPolygon>>
{
    private final NumberGenerator<Probability> addPolygonProbability;
    private final PolygonImageFactory factory;
    private final int maxPolygons;



    /**
     * @param addPolygonProbability A {@link NumberGenerator} that controls the probability
     * that a polygon will be added.
     * @param factory Used to create new polygons.
     * @param maxPolygons The maximum number of polygons permitted in an image (must be at least 2).
     */
    public AddPolygonMutation(NumberGenerator<Probability> addPolygonProbability,
                              PolygonImageFactory factory,
                              int maxPolygons)
    {
        if (maxPolygons < 2)
        {
            throw new IllegalArgumentException("Max polygons must be > 1.");
        }
        this.addPolygonProbability = addPolygonProbability;
        this.factory = factory;
        this.maxPolygons = maxPolygons;
    }


    /**
     * @param addPolygonProbability The probability that a polygon will be removed.
     * @param factory Used to create new polygons.
     * @param maxPolygons The maximum number of polygons permitted in an image (must be at least 2).
     */
    public AddPolygonMutation(Probability addPolygonProbability,
                              PolygonImageFactory factory,
                              int maxPolygons)
    {
        this(new ConstantGenerator<Probability>(addPolygonProbability),
             factory,
             maxPolygons);
    }


    public List<List<ColouredPolygon>> apply(List<List<ColouredPolygon>> selectedCandidates, Random rng)
    {
        List<List<ColouredPolygon>> mutatedCandidates = new ArrayList<List<ColouredPolygon>>(selectedCandidates.size());
        for (List<ColouredPolygon> candidate : selectedCandidates)
        {
            // A single polygon is added with the configured probability, unless
            // we already have the maximum permitted number of polygons.
            if (candidate.size() < maxPolygons && addPolygonProbability.nextValue().nextEvent(rng))
            {
                List<ColouredPolygon> newPolygons = new ArrayList<ColouredPolygon>(candidate);
                newPolygons.add(rng.nextInt(newPolygons.size() + 1),
                                factory.createRandomPolygon(rng));
                mutatedCandidates.add(newPolygons);
            }
            else // Nothing changed.
            {
                mutatedCandidates.add(candidate);
            }
        }
        return mutatedCandidates;
    }


}
