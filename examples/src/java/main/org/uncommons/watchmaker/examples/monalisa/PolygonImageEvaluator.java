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
import java.awt.GraphicsConfiguration;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.Transparency;
import java.awt.geom.AffineTransform;
import java.awt.image.AffineTransformOp;
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
public class PolygonImageEvaluator implements FitnessEvaluator<List<ColouredPolygon>>
{
    private final BufferedImage targetImage;
    private final Renderer<List<ColouredPolygon>, BufferedImage> renderer;
    private final AffineTransformOp transform;

    public PolygonImageEvaluator(BufferedImage targetImage)
    {
        this.renderer = new PolygonImageRenderer(new Dimension(targetImage.getWidth(),
                                                               targetImage.getHeight()),
                                                 false);
        int width = targetImage.getWidth();
        int height = targetImage.getWidth();
        // Scale the image down so that its smallest dimension is 100 pixels.  For large images this drastically
        // reduces the number of pixels that we need to check for fitness evaluation.
        double ratio = 1;
        if (width > 100 && height > 100)
        {
            ratio = 100d / (width > height ? width : height);
        }
        this.transform = new AffineTransformOp(AffineTransform.getScaleInstance(ratio, ratio),
                                               AffineTransformOp.TYPE_NEAREST_NEIGHBOR);
        this.targetImage = convertImage(transform.filter(targetImage, null));
        assert this.targetImage.getType() == BufferedImage.TYPE_INT_RGB : "Inefficient image type.";
    }


    /**
     * Make sure that the image is in the most efficient format for reading from.
     * This avoids having to convert pixels every time we access them.  This method also reduces
     * the image to thumbnail size so that we don't have so many pixels to check when evaluating
     * fitness.
     * @param image The image to convert.
     * @return The image converted to INT_RGB format.
     */
    private BufferedImage convertImage(BufferedImage image)
    {
        if (image.getType() != BufferedImage.TYPE_INT_RGB)
        {
            GraphicsDevice graphicsDevice = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice();
            GraphicsConfiguration config = graphicsDevice.getDefaultConfiguration();
            BufferedImage newImage = config.createCompatibleImage(image.getWidth(),
                                                                  image.getHeight(),
                                                                  Transparency.OPAQUE);
            newImage.getGraphics().drawImage(image, 0, 0, null);
            return newImage;
        }
        else
        {
            return image;
        }
    }


    /**
     * Render the polygons as an image and then do a pixel-by-pixel comparison
     * against the target image.  The fitness score is the total error.  A lower
     * score means a closer match.
     * @param candidate The image to evaluate.
     * @param population Not used.
     * @return A number indicating how close the candidate image is to the target image
     * (lower is better).
     */
    public double getFitness(List<ColouredPolygon> candidate,
                             List<? extends List<ColouredPolygon>> population)
    {
        Raster targetImageData = targetImage.getData();
        BufferedImage candidateImage = renderer.render(candidate);
        candidateImage = transform.filter(candidateImage, null);
        Raster candidateImageData = candidateImage.getData();
        assert candidateImageData.getWidth() == targetImage.getWidth() : "Image width mismatch.";
        assert candidateImageData.getHeight() == targetImage.getHeight() : "Image height mismatch.";

        double fitness = 0;
        int[] targetPixelValues = new int[3];
        int[] candidatePixelValues = new int[3];
        for (int row = 0; row < targetImage.getHeight(); row++)
        {
            for (int column = 0; column < targetImage.getWidth(); column++)
            {
                targetPixelValues = targetImageData.getPixel(column, row, targetPixelValues);
                candidatePixelValues = candidateImageData.getPixel(column, row, candidatePixelValues);
                long error = 0;
                for (int i = 0; i < targetPixelValues.length; i++)
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
