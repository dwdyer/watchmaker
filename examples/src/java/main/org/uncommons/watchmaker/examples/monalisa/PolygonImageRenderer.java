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
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.util.List;
import org.uncommons.watchmaker.framework.interactive.Renderer;

/**
 * Renders a polygon-based image to a {@link BufferedImage}.  For efficiency reasons, this
 * renderer returns the same image object (with different data) for subsequent invocations.
 * This means that invoking code should not expect returned images to be unaltered following
 * subsequent invocations.  It also means that a renderer is not thread-safe.
 * @author Daniel Dyer
 */
public class PolygonImageRenderer implements Renderer<List<ColouredPolygon>, BufferedImage>
{
    private static final AffineTransform IDENTITY_TRANSFORM = new AffineTransform();
    
    private final Dimension targetSize;
    private final AffineTransform transform;
    private final BufferedImage image;
    private final Graphics2D graphics;


    /**
     * @param targetSize The size of the canvas on which the polygons will be rendered.
     * @param antialias Whether or not to enable anti-aliasing for the rendered image.
     * @param transform A transformation applied to the vertices of an image's polygons
     * before drawing to the destination image.  This transformation adjusts the image
     * so that it fits on a canvas of the specified {@code targetSize}. 
     */
    public PolygonImageRenderer(Dimension targetSize,
                                boolean antialias,
                                AffineTransform transform)
    {
        this.targetSize = targetSize;
        this.transform = transform;        
        this.image = new BufferedImage(targetSize.width,
                                       targetSize.height,
                                       BufferedImage.TYPE_INT_RGB);
        this.graphics = image.createGraphics();
        if (antialias)
        {
            graphics.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                                      RenderingHints.VALUE_ANTIALIAS_ON);
        }
    }


    /**
     * Renders the specified polygons as an image.
     * @param entity A collection of coloured polygons.
     * @return An image object displaying the polygons.
     */
    public BufferedImage render(List<ColouredPolygon> entity)
    {
        // Need to set the background before applying the transform.
        graphics.setTransform(IDENTITY_TRANSFORM);
        graphics.setColor(Color.GRAY);
        graphics.fillRect(0, 0, targetSize.width, targetSize.height);
        if (transform != null)
        {
            graphics.setTransform(transform);
        }
        for (ColouredPolygon polygon : entity)
        {
            graphics.setColor(polygon.getColour());
            graphics.fillPolygon(polygon.getPolygon());
        }
        return image;
    }
}
