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
import java.awt.Dimension;
import java.awt.Point;
import java.awt.image.BufferedImage;
import java.util.Arrays;
import java.util.List;
import org.testng.annotations.Test;
import org.uncommons.maths.random.MersenneTwisterRNG;

/**
 * Unit test for {@link PolygonImageEvaluator}.
 * @author Daniel Dyer
 */
public class PolygonImageEvaluatorTest
{
    /**
     * An image that is identical to the target image should have a fitness
     * of zero. 
     */
    @Test(groups = "display-required")
    public void testPerfectMatch()
    {
        Dimension canvasSize = new Dimension(100, 100);
        PolygonImageFactory factory = new PolygonImageFactory(canvasSize);
        List<ColouredPolygon> image = factory.generateRandomCandidate(new MersenneTwisterRNG());

        BufferedImage targetImage = new PolygonImageRenderer(canvasSize, false, null).render(image);
        PolygonImageEvaluator evaluator = new PolygonImageEvaluator(targetImage);

        double fitness = evaluator.getFitness(image, null);
        assert fitness == 0 : "Fitness should be zero when image is an exact match.";
    }


    /**
     * An image that is different to the target image should have a non-zero fitness.
     */
    @Test(groups = "display-required")
    public void testDifferentImages()
    {
        Dimension canvasSize = new Dimension(100, 100);
        List<ColouredPolygon> targetImage = Arrays.asList(new ColouredPolygon(Color.BLACK,
                                                                              Arrays.asList(new Point(0, 0),
                                                                                            new Point(99, 0),
                                                                                            new Point(99, 99),
                                                                                            new Point(0, 99))));
        List<ColouredPolygon> candidateImage = Arrays.asList(new ColouredPolygon(Color.WHITE,
                                                                                 Arrays.asList(new Point(0, 0),
                                                                                               new Point(99, 0),
                                                                                               new Point(99, 99),
                                                                                               new Point(0, 99))));

        BufferedImage renderedTarget = new PolygonImageRenderer(canvasSize, false, null).render(targetImage);
        PolygonImageEvaluator evaluator = new PolygonImageEvaluator(renderedTarget);

        double fitness = evaluator.getFitness(candidateImage, null);
        assert fitness > 0 : "Fitness should be non-zero when image does not match target.";
    }


    /**
     * If the image is not INT_RGB, it will be converted.  This should not affect the results.
     */
    @Test(groups = "display-required")
    public void testImageConversion()
    {
        Dimension canvasSize = new Dimension(100, 100);
        PolygonImageFactory factory = new PolygonImageFactory(canvasSize);
        List<ColouredPolygon> image = factory.generateRandomCandidate(new MersenneTwisterRNG());

        BufferedImage targetImage = new PolygonImageRenderer(canvasSize, false, null).render(image);
        // Convert target image to some format that will have to be converted to INT_RGB.
        BufferedImage newImage = new BufferedImage(targetImage.getWidth(),
                                                   targetImage.getHeight(),
                                                   BufferedImage.TYPE_3BYTE_BGR); // Sub-optimal image type.
        newImage.getGraphics().drawImage(targetImage, 0, 0, null);

        PolygonImageEvaluator evaluator = new PolygonImageEvaluator(newImage);

        double fitness = evaluator.getFitness(image, null);
        assert fitness == 0 : "Fitness should be zero when image is an exact match.";
    }

}
