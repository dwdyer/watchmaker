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
import java.awt.Point;
import java.util.Arrays;
import java.util.List;
import org.testng.annotations.Test;
import org.uncommons.maths.number.ConstantGenerator;
import org.uncommons.maths.random.Probability;
import org.uncommons.watchmaker.examples.ExamplesTestUtils;

/**
 * Unit test for the {@link PolygonColourMutation} evolutionary operator.
 * @author Daniel Dyer
 */
public class PolygonColourMutationTest
{
    @Test
    public void testColourMutation()
    {
        PolygonColourMutation mutation = new PolygonColourMutation(Probability.ONE, // Guaranteed mutation.
                                                                   new ConstantGenerator<Double>(1d));
        // A grey triangle.
        final ColouredPolygon polygon = new ColouredPolygon(new Color(128, 128, 128, 128),
                                                            Arrays.asList(new Point(0, 0),
                                                                          new Point(50, 50),
                                                                          new Point(0, 75)));
        List<ColouredPolygon> image = Arrays.asList(polygon);
        List<ColouredPolygon> mutatedImage = mutation.apply(image, ExamplesTestUtils.getRNG());
        Color mutatedColour = mutatedImage.get(0).getColour();
        assert mutatedColour.getRed() == 129 : "Red component should have been incremented, is " + mutatedColour.getRed();
        assert mutatedColour.getGreen() == 129 : "Green component should have been incremented, is " + mutatedColour.getGreen();
        assert mutatedColour.getBlue() == 129 : "Blue component should have been incremented, is " + mutatedColour.getBlue();
        assert mutatedColour.getAlpha() == 129 : "Alpha component should have been incremented, is " + mutatedColour.getAlpha();
    }


    @Test
    public void testZeroProbability()
    {
        PolygonColourMutation mutation = new PolygonColourMutation(Probability.ZERO,
                                                                   new ConstantGenerator<Double>(1d));
        // A grey triangle.
        Color originalColour = new Color(128, 128, 128, 128);
        final ColouredPolygon polygon = new ColouredPolygon(originalColour,
                                                            Arrays.asList(new Point(0, 0),
                                                                          new Point(50, 50),
                                                                          new Point(0, 75)));
        List<ColouredPolygon> image = Arrays.asList(polygon);
        List<ColouredPolygon> mutatedImage = mutation.apply(image, ExamplesTestUtils.getRNG());
        assert mutatedImage.get(0) == polygon : "Polygon should not have changed at all.";
        assert mutatedImage.get(0).getColour() == originalColour : "Colour should not have changed at all.";
    }
}
