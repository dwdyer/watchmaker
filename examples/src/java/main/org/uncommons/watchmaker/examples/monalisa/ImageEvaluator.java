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
import java.awt.image.Raster;
import java.util.List;
import org.uncommons.watchmaker.framework.FitnessEvaluator;
import org.uncommons.watchmaker.framework.interactive.Renderer;

/**
 * Compares the generated polygon-based images to the target bitmap.  The polygon images
 * are rendered the same size as the target image and then each pixel is compared.  The
 * fitness value is a combination of the differences for each pixel.  Lower fitness is better. 
 * @author Daniel Dyer
 */
public class ImageEvaluator implements FitnessEvaluator<List<ColouredPolygon>>
{
    private final BufferedImage targetImage;
    private final Renderer<List<ColouredPolygon>, BufferedImage> renderer;

    public ImageEvaluator(BufferedImage targetImage)
    {
        this.targetImage = targetImage;
        this.renderer = new PolygonRenderer(new Dimension(targetImage.getWidth(), targetImage.getHeight()));
    }

    
    public double getFitness(List<ColouredPolygon> candidate,
                             List<? extends List<ColouredPolygon>> population)
    {
        Raster targetImageData = targetImage.getData();
        Raster candidateImageData = renderer.render(candidate).getData();
        assert candidateImageData.getWidth() == targetImage.getWidth() : "Image width mismatch.";
        assert candidateImageData.getHeight() == targetImage.getHeight() : "Image height mismatch.";

        double fitness = 0;
        int[] targetPixelValues = new int[4];
        int[] candidatePixelValues = new int[4];
        for (int row = 0; row < targetImage.getHeight(); row++)
        {
            for (int column = 0; column < targetImage.getWidth(); column++)
            {
                targetPixelValues = targetImageData.getPixel(column, row, targetPixelValues);
                candidatePixelValues = candidateImageData.getPixel(column, row, candidatePixelValues);
                long error = 0;
                for (int i = 0; i < 3; i++)
                {
                    int delta = targetPixelValues[i] - candidatePixelValues[i];
                    error += (delta * delta);
                }
                fitness += Math.sqrt(error);
            }
        }

        return fitness;
    }

    
    public boolean isNatural()
    {
        return false;
    }
}
