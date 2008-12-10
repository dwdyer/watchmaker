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
import java.awt.Graphics;
import java.awt.GraphicsConfiguration;
import java.awt.GraphicsEnvironment;
import java.awt.image.BufferedImage;
import java.util.List;
import org.uncommons.watchmaker.framework.interactive.Renderer;

/**
 * Renders a polygon-based image to a {@link BufferedImage}.
 * @author Daniel Dyer
 */
public class PolygonRenderer implements Renderer<List<ColouredPolygon>, BufferedImage>
{
    private static final GraphicsConfiguration GRAPHICS_CONFIG
        = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice().getDefaultConfiguration();

    private final Dimension targetSize;

    public PolygonRenderer(Dimension targetSize)
    {
        this.targetSize = targetSize;
    }


    public BufferedImage render(List<ColouredPolygon> entity)
    {
        BufferedImage image = GRAPHICS_CONFIG.createCompatibleImage(targetSize.width, targetSize.height);
        Graphics graphics = image.getGraphics();
        graphics.setColor(Color.WHITE);
        graphics.fillRect(0, 0, targetSize.width, targetSize.height);
        for (ColouredPolygon polygon : entity)
        {
            graphics.setColor(polygon.getColour());
            graphics.fillPolygon(polygon.getPolygon());
        }
        return image;
    }
}
