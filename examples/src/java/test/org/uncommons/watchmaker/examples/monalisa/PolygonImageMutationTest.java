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
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import org.testng.annotations.Test;
import org.uncommons.maths.ConstantGenerator;
import org.uncommons.maths.random.MersenneTwisterRNG;
import org.uncommons.watchmaker.framework.Probability;

/**
 * Unit test for the {@link PolygonImageMutation} evolutionary operator.
 * @author Daniel Dyer
 */
public class PolygonImageMutationTest
{
    private final Random rng = new MersenneTwisterRNG();

    @Test
    public void testColourMutation()
    {
        PolygonImageMutation mutation = new PolygonImageMutation(new Dimension(100, 100),
                                                                 Probability.ONE, // Guaranteed mutation.
                                                                 new ConstantGenerator<Double>(1d), // Fixed colour mutation.
                                                                 Probability.ZERO,
                                                                 Probability.ZERO,
                                                                 null);
        // A grey triangle.
        final ColouredPolygon polygon = new ColouredPolygon(new Color(128, 128, 128, 128),
                                                            Arrays.asList(new Point(0, 0),
                                                                          new Point(50, 50),
                                                                          new Point(0, 75)));
        List<ColouredPolygon> image = Arrays.asList(polygon);
        List<List<ColouredPolygon>> images = new ArrayList<List<ColouredPolygon>>(1);
        images.add(image);
        List<List<ColouredPolygon>> mutatedImages = mutation.apply(images, rng);
        Color mutatedColor = mutatedImages.get(0).get(0).getColour();
        assert mutatedColor.getRed() == 129 : "Red component should have been incremented, is " + mutatedColor.getRed();
        assert mutatedColor.getGreen() == 129 : "Green component should have been incremented, is " + mutatedColor.getGreen();
        assert mutatedColor.getBlue() == 129 : "Blue component should have been incremented, is " + mutatedColor.getBlue();
        assert mutatedColor.getAlpha() == 129 : "Alpha component should have been incremented, is " + mutatedColor.getAlpha();
    }
}
