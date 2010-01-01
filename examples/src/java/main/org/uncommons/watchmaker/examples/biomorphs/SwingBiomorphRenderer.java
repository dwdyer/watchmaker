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
package org.uncommons.watchmaker.examples.biomorphs;

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import javax.swing.JComponent;
import org.uncommons.watchmaker.framework.interactive.Renderer;

/**
 * Renders Biomorphs as Swing components.
 * @author Daniel Dyer
 */
public class SwingBiomorphRenderer implements Renderer<Biomorph, JComponent>
{
    /**
     * Renders an evolved biomorph as a component that can be displayed
     * in a Swing GUI.
     * @param biomorph The biomorph to render.
     * @return A component that displays a visual representation of the
     * biomorph.
     */
    public JComponent render(Biomorph biomorph)
    {
        return new BiomorphView(biomorph);
    }


    /**
     * A Swing component that can display a visual representation of a
     * biomorph.
     */
    private static final class BiomorphView extends JComponent
    {
        private final Biomorph biomorph;

        BiomorphView(Biomorph biomorph)
        {
            this.biomorph = biomorph;
            Dimension size = new Dimension(200, 200);
            setMinimumSize(size);
            setPreferredSize(size);
        }


        @Override
        protected void paintComponent(Graphics graphics)
        {
            super.paintComponent(graphics);
            if (graphics instanceof Graphics2D)
            {
                ((Graphics2D) graphics).setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                                                         RenderingHints.VALUE_ANTIALIAS_ON);
            }

            int[][] pattern = biomorph.getPatternPhenotype();
            int depth = biomorph.getLengthPhenotype();

            drawTree(graphics,
                     getSize().width / 2,
                     getSize().height / 2,
                     depth,
                     2, // Initial direction should be 2 or 6 to ensure horizontal symmetry.
                     pattern[0], // dx
                     pattern[1]); // dy
        }


        /**
         * Recursive method for drawing tree branches.
         */
        private void drawTree(Graphics graphics,
                              int x,
                              int y,
                              int length,
                              int direction,
                              int[] dx,
                              int[] dy)
        {
            // Make sure direction wraps round in the range 0 - 7.
            direction = (direction + 8) % 8;

            int x2 = x + length * dx[direction];
            int y2 = y + length * dy[direction];

            graphics.drawLine(x, y, x2, y2);

            if (length > 0)
            {
                // Recursively draw the left and right branches of the tree.
                drawTree(graphics, x2, y2, length - 1, direction - 1, dx, dy);
                drawTree(graphics, x2, y2, length - 1, direction + 1, dx, dy);
            }
        }
    }
}
