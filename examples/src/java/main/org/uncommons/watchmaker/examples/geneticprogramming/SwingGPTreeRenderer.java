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
package org.uncommons.watchmaker.examples.geneticprogramming;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import javax.swing.JComponent;
import org.uncommons.watchmaker.framework.interactive.Renderer;

/**
 * A Swing renderer for genetic programming trees.
 * @author Daniel Dyer
 */
public class SwingGPTreeRenderer implements Renderer<Node, JComponent>
{
    /**
     * Renders a GP tree as a Swing component.
     * @param tree The root node of the GP tree to render.
     * @return A {@link JComponent} that displays a graphical representation of the tree.
     */
    public JComponent render(Node tree)
    {
        return new GPTreeView(tree);
    }


    private static final class GPTreeView extends JComponent
    {
         // Allow 30 pixels for each node horizontally.
        private static final int NODE_WIDTH = 30;
         // Allow 50 pixels for each node vertically.
        private static final int NODE_HEIGHT = 50;
        private static final int CIRCLE_RADIUS = 9;
        private static final int CIRCLE_DIAMETER = CIRCLE_RADIUS * 2;

        private final Node rootNode;


        GPTreeView(Node rootNode)
        {
            this.rootNode = rootNode;
            int minHeight = rootNode.getDepth() * NODE_HEIGHT;
            int minWidth = rootNode.getWidth() * NODE_WIDTH;
            Dimension size = new Dimension(minWidth, minHeight);
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
            // Center the tree if the component is bigger than required.
            int offset = (getSize().width - getMinimumSize().width) / 2;
            drawNode(rootNode, offset, 0, graphics);
        }


        /**
         * Recursively draw the specified node and its children.
         * @param node The sub-tree to draw.
         * @param x The left edge of the area in which this tree is drawn.
         * @param y The top edge of the area in which this tree is drawn.
         * @param graphics The target for drawing.
         */
        private void drawNode(Node node, int x, int y, Graphics graphics)
        {
            int start = x + node.getWidth() * NODE_WIDTH / 2;
            if (node instanceof Constant)
            {
                graphics.setColor(Color.YELLOW);
                graphics.fillRoundRect(start - (NODE_WIDTH / 2 - 2),
                                       y,
                                       NODE_WIDTH - 4,
                                       CIRCLE_DIAMETER,
                                       CIRCLE_RADIUS,
                                       CIRCLE_RADIUS);
                graphics.setColor(Color.BLACK);
                graphics.drawRoundRect(start - (NODE_WIDTH / 2 - 2),
                                       y,
                                       NODE_WIDTH - 4,
                                       CIRCLE_DIAMETER,
                                       CIRCLE_RADIUS,
                                       CIRCLE_RADIUS);
            }
            else if (node instanceof Parameter)
            {
                graphics.setColor(Color.GREEN);
                graphics.fillRect(start - CIRCLE_RADIUS, y, CIRCLE_DIAMETER, CIRCLE_DIAMETER);
                graphics.setColor(Color.BLACK);
                graphics.drawRect(start - CIRCLE_RADIUS, y, CIRCLE_DIAMETER, CIRCLE_DIAMETER);
            }
            else
            {
                graphics.setColor(Color.WHITE);
                graphics.fillOval(start - CIRCLE_RADIUS, y, CIRCLE_DIAMETER, CIRCLE_DIAMETER);
                graphics.setColor(Color.BLACK);
                graphics.drawOval(start - CIRCLE_RADIUS, y, CIRCLE_DIAMETER, CIRCLE_DIAMETER);
            }
            FontMetrics metrics = graphics.getFontMetrics();
            int stringWidth = metrics.stringWidth(node.getLabel());
            graphics.drawString(node.getLabel(),
                                (int) Math.round(start - (double) stringWidth / 2),
                                (int) Math.round(y + CIRCLE_RADIUS + (double) metrics.getHeight() / 2 - metrics.getDescent()));

            int xOffset = x;
            for (int i = 0; i < node.getArity(); i++)
            {
                Node child = node.getChild(i);
                drawNode(child, xOffset, y + NODE_HEIGHT, graphics);
                graphics.drawLine(start,
                                  y + CIRCLE_DIAMETER, xOffset + (child.getWidth() * NODE_WIDTH / 2),
                                  y + NODE_HEIGHT);
                xOffset += child.getWidth() * NODE_WIDTH;
            }
        }
    }
}
