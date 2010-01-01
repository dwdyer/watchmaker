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

import java.awt.Dimension;
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
    // This field is marked as transient, even though the class is not Serializable, because
    // Terracotta will respect the fact it is transient and not try to share it.
    private final transient ThreadLocal<Renderer<List<ColouredPolygon>, BufferedImage>> threadLocalRenderer
        = new ThreadLocal<Renderer<List<ColouredPolygon>, BufferedImage>>();

    private final int width;
    private final int height;
    private final AffineTransform transform;
    private final int[] targetPixels;


    /**
     * Creates an evaluator that assigns fitness scores to images based on how
     * close they are to the specified target image. 
     * @param targetImage The image that all others are compared to.
     */
    public PolygonImageEvaluator(BufferedImage targetImage)
    {
        // Scale the image down so that its smallest dimension is 100 pixels.  For large images this drastically
        // reduces the number of pixels that we need to check for fitness evaluation.
        Raster targetImageData;
        if (targetImage.getWidth() > 100 && targetImage.getHeight() > 100)
        {
            double ratio = 100.0d / (targetImage.getWidth() > targetImage.getHeight() ? targetImage.getHeight() : targetImage.getWidth());
            transform = AffineTransform.getScaleInstance(ratio, ratio);
            AffineTransformOp transformOp = new AffineTransformOp(transform,
                                                                  AffineTransformOp.TYPE_NEAREST_NEIGHBOR);
            targetImageData = convertImage(transformOp.filter(targetImage, null)).getData();
        }
        else
        {
            targetImageData = convertImage(targetImage).getData();
            transform = null;
        }

        this.width = targetImageData.getWidth();
        this.height = targetImageData.getHeight();
        int[] pixelArray = new int[targetImageData.getWidth() * targetImageData.getHeight()];
        targetPixels = (int[]) targetImageData.getDataElements(0,
                                                               0,
                                                               targetImageData.getWidth(),
                                                               targetImageData.getHeight(),
                                                               pixelArray);
    }


    /**
     * Make sure that the image is in the most efficient format for reading from.
     * This avoids having to convert pixels every time we access them.
     * @param image The image to convert.
     * @return The image converted to INT_RGB format.
     */
    private BufferedImage convertImage(BufferedImage image)
    {
        if (image.getType() == BufferedImage.TYPE_INT_RGB)
        {
            return image;
        }
        else
        {
            BufferedImage newImage = new BufferedImage(image.getWidth(),
                                                       image.getHeight(),
                                                       BufferedImage.TYPE_INT_RGB);
            newImage.getGraphics().drawImage(image, 0, 0, null);
            return newImage;
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
        // Use one renderer per thread because they are not thread safe.
        Renderer<List<ColouredPolygon>, BufferedImage> renderer = threadLocalRenderer.get();
        if (renderer == null)
        {
            renderer = new PolygonImageRenderer(new Dimension(width, height),
                                                false,
                                                transform);
            threadLocalRenderer.set(renderer);
        }
        
        BufferedImage candidateImage = renderer.render(candidate);
        Raster candidateImageData = candidateImage.getData();

        int[] candidatePixelValues = new int[targetPixels.length];
        candidatePixelValues = (int[]) candidateImageData.getDataElements(0,
                                                                          0,
                                                                          candidateImageData.getWidth(),
                                                                          candidateImageData.getHeight(),
                                                                          candidatePixelValues);
        double fitness = 0;
        for (int i = 0; i < targetPixels.length; i++)
        {
            fitness += comparePixels(targetPixels[i], candidatePixelValues[i]);
        }

        return fitness;
    }


    private double comparePixels(int p1, int p2)
    {
        int deltaR = ((p1 >>> 16) & 0xFF) - ((p2 >>> 16) & 0xFF);
        int deltaG = ((p1 >>> 8) & 0xFF) - ((p2 >>> 8) & 0xFF);
        int deltaB = (p1 & 0xFF) - (p2 & 0xFF);
        return Math.sqrt(deltaR * deltaR + deltaG * deltaG + deltaB * deltaB);
    }


    public boolean isNatural()
    {
        return false;
    }
}
