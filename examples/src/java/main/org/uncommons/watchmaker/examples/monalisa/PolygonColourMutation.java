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

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import org.uncommons.maths.Maths;
import org.uncommons.maths.number.ConstantGenerator;
import org.uncommons.maths.number.NumberGenerator;
import org.uncommons.maths.random.Probability;
import org.uncommons.watchmaker.framework.EvolutionaryOperator;

/**
 * Evolutionary operator for mutating individual polygons. Polygons are mutated by changing their
 * colour and/or either adding a point, removing a point or changing the position of a point.
 * <p/>
 * @author Daniel Dyer
 */
public class PolygonColourMutation implements EvolutionaryOperator<ColouredPolygon>
{
    private final NumberGenerator<Probability> mutationProbability;
    private final NumberGenerator<Double> mutationAmount;


    /**
     * @param mutationProbability A {@link NumberGenerator} that controls the probability that the
     * colour will be modified.
     * @param mutationAmount A {@link NumberGenerator} that controls the amount that the colour's
     * components are adjusted by.
     */
    public PolygonColourMutation(NumberGenerator<Probability> mutationProbability,
        NumberGenerator<Double> mutationAmount)
    {
        this.mutationProbability = mutationProbability;
        this.mutationAmount = mutationAmount;
    }


    /**
     * @param mutationProbability The probability that the colour will be modified.
     * @param mutationAmount A {@link NumberGenerator} that controls the amount that the colour's
     * components are adjusted by.
     */
    public PolygonColourMutation(Probability mutationProbability,
        NumberGenerator<Double> mutationAmount)
    {
        this(new ConstantGenerator<Probability>(mutationProbability), mutationAmount);
    }


    public List<ColouredPolygon> apply(List<ColouredPolygon> polygons, Random rng)
    {
        if (!mutationProbability.nextValue().nextEvent(rng))
            return polygons;
        int index = rng.nextInt(polygons.size());
        List<ColouredPolygon> newPolygons = new ArrayList<ColouredPolygon>(polygons);
        ColouredPolygon oldPolygon = newPolygons.get(index);
        newPolygons.set(index, new ColouredPolygon(mutateColour(oldPolygon.getColour(), rng),
            oldPolygon.getVertices()));
        return newPolygons;
    }


    /**
     * Mutate the specified colour.
     * <p/>
     * @param colour The colour to mutate.
     * @param rng A source of randomness.
     * @return The (possibly) mutated colour.
     */
    private Color mutateColour(Color colour, Random rng)
    {
        int red = colour.getRed();
        int green = colour.getGreen();
        int blue = colour.getBlue();
        int alpha = colour.getAlpha();
        switch (rng.nextInt(3))
        {
            case 0:
            {
                red = mutateColourComponent(red);
                break;
            }
            case 1:
            {
                green = mutateColourComponent(green);
                break;
            }
            case 2:
            {
                blue = mutateColourComponent(blue);
                break;
            }
            case 3:
            {
                // Alpha mutation is disabled by default, but you can enable it by changing the
                // above RNG limit from 3 to 4.
                alpha = mutateColourComponent(alpha);
                break;
            }
        }
        return new Color(red, green, blue, alpha);
    }


    /**
     * Adjust a single component (red, green, blue or alpha) of a colour.
     * <p/>
     * @param component The value to mutate.
     * @return The mutated component value.
     */
    private int mutateColourComponent(int component)
    {
        double amount = mutationAmount.nextValue();
        if (Double.compare(Math.abs(amount), 1) < 0)
            amount = Math.signum(amount);
        int mutatedComponent = (int) Math.round(component + amount);
        mutatedComponent = Maths.restrictRange(mutatedComponent, 0, 255);
        return mutatedComponent;
    }
}
