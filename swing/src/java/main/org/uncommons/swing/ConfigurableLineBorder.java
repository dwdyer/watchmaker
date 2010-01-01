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
package org.uncommons.swing;

import java.awt.Component;
import java.awt.Graphics;
import java.awt.Insets;
import javax.swing.border.Border;

/**
 * Border class that allows each of the four sides of the border to be enabled
 * or disabled independently.
 * @author Daniel Dyer
 */
public class ConfigurableLineBorder implements Border
{
    private final boolean top;
    private final boolean left;
    private final boolean bottom;
    private final boolean right;
    private final int thickness;
    private final Insets insets;


    /**
     * @param top Whether or not to draw the border on the top edge.
     * @param left Whether or not to draw the border on the left edge.
     * @param bottom Whether or not to draw the border on the bottom edge.
     * @param right Whether or not to draw the border on the right edge.
     * @param thickness The width (in pixels) of each side of the border.
     */
    public ConfigurableLineBorder(boolean top,
                                  boolean left,
                                  boolean bottom,
                                  boolean right,
                                  int thickness)
    {
        this.top = top;
        this.left = left;
        this.bottom = bottom;
        this.right = right;
        this.thickness = thickness;
        this.insets = new Insets(top ? thickness : 0,
                                 left ? thickness : 0,
                                 bottom ? thickness : 0,
                                 right ? thickness : 0);
    }


    /**
     * Renders borders for the specified component based on the configuration
     * of this border object.
     * @param component The component for which the border is painted.
     * @param graphics A {@link Graphics} object to use for painting.
     * @param x The X-coordinate of the top left point of the border rectangle.
     * @param y The Y-coordinate of the top left point of the border rectangle.
     * @param width The width of the border rectangle.
     * @param height The height of the border rectangle.
     */
    public void paintBorder(Component component,
                            Graphics graphics,
                            int x,
                            int y,
                            int width,
                            int height)
    {
        if (top)
        {
            graphics.fillRect(x, y, width, thickness);
        }
        if (bottom)
        {
            graphics.fillRect(x, y + height - thickness, width, thickness);
        }
        if (left)
        {
            graphics.fillRect(x, y, thickness, height);
        }
        if (right)
        {
            graphics.fillRect(x + width - thickness, y, thickness, height);
        }
    }


    /**
     * @param component The component for which the border is painted.
     * @return Insets for the current border configuration.
     */
    public Insets getBorderInsets(Component component)
    {
        return insets;
    }


    /**
     * @return false
     */
    public boolean isBorderOpaque()
    {
        return false;
    }
}
