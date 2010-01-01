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
 * Randomly mutates the polygons that make up an image by removing a polygon
 * according to some probability.
 * @author Daniel Dyer
 */
public class RemovePolygonMutation implements EvolutionaryOperator<List<ColouredPolygon>>
{
    private final NumberGenerator<Probability> removePolygonProbability;

    /**
     * @param removePolygonProbability A {@link NumberGenerator} that controls the probability
     * that a polygon will be removed.
     */
    public RemovePolygonMutation(NumberGenerator<Probability> removePolygonProbability)
    {
        this.removePolygonProbability = removePolygonProbability;
    }


    /**
     * @param removePolygonProbability The probability that a polygon will be removed.
     */
    public RemovePolygonMutation(Probability removePolygonProbability)
    {
        this(new ConstantGenerator<Probability>(removePolygonProbability));
    }


    public List<List<ColouredPolygon>> apply(List<List<ColouredPolygon>> selectedCandidates, Random rng)
    {
        List<List<ColouredPolygon>> mutatedCandidates = new ArrayList<List<ColouredPolygon>>(selectedCandidates.size());
        for (List<ColouredPolygon> candidate : selectedCandidates)
        {
            // A single polygon is removed with the configured probability, unless
            // we already have the minimum permitted number of polygons.
            if (candidate.size() > PolygonImageFactory.MINIMUM_POLYGON_COUNT
                && removePolygonProbability.nextValue().nextEvent(rng))
            {
                List<ColouredPolygon> newPolygons = new ArrayList<ColouredPolygon>(candidate);
                newPolygons.remove(rng.nextInt(newPolygons.size()));
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
