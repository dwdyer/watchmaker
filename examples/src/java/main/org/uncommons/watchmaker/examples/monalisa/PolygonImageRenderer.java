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
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.util.List;
import org.uncommons.watchmaker.framework.interactive.Renderer;

/**
 * Renders a polygon-based image to a {@link BufferedImage}.
 * @author Daniel Dyer
 */
public class PolygonImageRenderer implements Renderer<List<ColouredPolygon>, BufferedImage>
{
    private final Dimension targetSize;
    private final boolean antialias;

    /**
     * @param targetSize The size of the canvas on which the polygons will be rendered.
     * @param antialias Whether or not to enable anti-aliasing for the rendered image.
     */
    public PolygonImageRenderer(Dimension targetSize,
                                boolean antialias)
    {
        this.targetSize = targetSize;
        this.antialias = antialias;
    }


    /**
     * Renders the specified polygons as an image.
     * @param entity A collection of coloured polygons.
     * @return An image object displaying the polygons.
     */
    public BufferedImage render(List<ColouredPolygon> entity)
    {
        BufferedImage image = new BufferedImage(targetSize.width,
                                                targetSize.height,
                                                BufferedImage.TYPE_INT_RGB);

        Graphics2D graphics = image.createGraphics();
        if (antialias)
        {
            graphics.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                                      RenderingHints.VALUE_ANTIALIAS_ON);
        }

        graphics.setColor(Color.GRAY);
        graphics.fillRect(0, 0, targetSize.width, targetSize.height);
        for (ColouredPolygon polygon : entity)
        {
            graphics.setColor(polygon.getColour());
            graphics.fillPolygon(polygon.getPolygon());
        }
        graphics.dispose();
        return image;
    }
}
