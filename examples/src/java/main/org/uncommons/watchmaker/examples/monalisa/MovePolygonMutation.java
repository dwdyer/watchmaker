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
 * Randomly mutates an image by swapping the z-order of two of its polygons
 * according to some probability.
 * @author Daniel Dyer
 */
public class MovePolygonMutation implements EvolutionaryOperator<List<ColouredPolygon>>
{
    private final NumberGenerator<Probability> movePolygonProbability;


    /**
     * @param movePolygonProbability A {@link NumberGenerator} that controls the probability
     * that a polygon will be replaced.
     */
    public MovePolygonMutation(NumberGenerator<Probability> movePolygonProbability)
    {
        this.movePolygonProbability = movePolygonProbability;
    }


    /**
     * @param replacePolygonProbability The probability that a polygon will be replaced.
     */
    public MovePolygonMutation(Probability replacePolygonProbability)
    {
        this(new ConstantGenerator<Probability>(replacePolygonProbability));
    }


    public List<List<ColouredPolygon>> apply(List<List<ColouredPolygon>> selectedCandidates, Random rng)
    {
        List<List<ColouredPolygon>> mutatedCandidates = new ArrayList<List<ColouredPolygon>>(selectedCandidates.size());
        for (List<ColouredPolygon> candidate : selectedCandidates)
        {
            if (movePolygonProbability.nextValue().nextEvent(rng))
            {
                List<ColouredPolygon> newPolygons = new ArrayList<ColouredPolygon>(candidate);
                ColouredPolygon polygon = newPolygons.remove(rng.nextInt(newPolygons.size()));
                newPolygons.add(rng.nextInt(newPolygons.size()) + 1, polygon);
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
