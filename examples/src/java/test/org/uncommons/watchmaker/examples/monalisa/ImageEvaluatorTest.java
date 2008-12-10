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

import java.awt.Dimension;
import java.awt.image.BufferedImage;
import java.util.List;
import org.testng.annotations.Test;
import org.uncommons.maths.random.MersenneTwisterRNG;

/**
 * Unit test for {@link ImageEvaluator}.
 * @author Daniel Dyer
 */
public class ImageEvaluatorTest
{
    /**
     * An image that is identical to the target image should have a fitness
     * of zero. 
     */
    @Test
    public void testPerfectMatch()
    {
        Dimension canvasSize = new Dimension(100, 100);
        PolygonImageFactory factory = new PolygonImageFactory(canvasSize, 5, 3);
        List<ColouredPolygon> image = factory.generateRandomCandidate(new MersenneTwisterRNG());

        BufferedImage targetImage = new PolygonRenderer(canvasSize).render(image);
        ImageEvaluator evaluator = new ImageEvaluator(targetImage);

        double fitness = evaluator.getFitness(image, null);
        assert fitness == 0 : "Fitness should be zero when image is an exact match.";
    }
}
