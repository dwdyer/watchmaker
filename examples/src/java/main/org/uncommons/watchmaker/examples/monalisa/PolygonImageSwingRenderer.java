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
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.util.List;
import javax.swing.JComponent;
import org.uncommons.watchmaker.framework.interactive.Renderer;

/**
 * Converts a {@link BufferedImage} to a {@link JComponent} that displays that image
 * alongside a pre-specified target image.
 * @author Daniel Dyer
 */
public class PolygonImageSwingRenderer implements Renderer<List<ColouredPolygon>, JComponent>
{
    private final BufferedImage targetImage;
    private final Renderer<List<ColouredPolygon>, BufferedImage> delegate;


    /**
     * @param targetImage This image is displayed on the left side of the
     * JComponent.
     */
    public PolygonImageSwingRenderer(BufferedImage targetImage)
    {
        // Convert the target image into the most efficient format for rendering.
        this.targetImage = new BufferedImage(targetImage.getWidth(),
                                             targetImage.getHeight(),
                                             BufferedImage.TYPE_INT_RGB);
        this.targetImage.getGraphics().drawImage(targetImage, 0, 0, null);
        this.targetImage.setAccelerationPriority(1);

        this.delegate = new PolygonImageRenderer(new Dimension(targetImage.getWidth(),
                                                               targetImage.getHeight()),
                                                 true, // Anti-alias.
                                                 null);
    }


    /**
     * Renders the specified image as a JComponent with the image on the
     * right and the pre-specified target image on the left.
     * @param entity The image to render on the right side of the component.
     * @return A Swing component that displays the rendered image.
     */
    public JComponent render(List<ColouredPolygon> entity)
    {
        return new ImageComponent(entity);
    }


    /**
     * Swing component for rendering a fixed size image.  If the image is smaller
     * than the component, it is centered.
     */
    private final class ImageComponent extends JComponent
    {
        private static final int GAP = 10;
        private static final int FOOTER = 20;

        private final List<ColouredPolygon> candidate;
        private final Dimension minimumSize;


        ImageComponent(List<ColouredPolygon> candidate)
        {
            this.candidate = candidate;
            this.minimumSize = new Dimension(targetImage.getWidth() * 2 + GAP,
                                             targetImage.getHeight() + FOOTER);
        }

        @Override
        public Dimension getPreferredSize()
        {
            return minimumSize;
        }

        @Override
        public Dimension getMinimumSize()
        {
            return minimumSize;
        }


        @Override
        protected void paintComponent(Graphics graphics)
        {
            int x = Math.max(0, (getWidth() - minimumSize.width) / 2);
            int y = Math.max(0, (getHeight() - minimumSize.height) / 2);
            graphics.drawImage(targetImage, x, y, this);
            BufferedImage candidateImage = delegate.render(candidate);
            candidateImage.setAccelerationPriority(1);
            Graphics clip = graphics.create(x + targetImage.getWidth() + GAP,
                                            y,
                                            candidateImage.getWidth(),
                                            candidateImage.getHeight() + FOOTER);
            clip.drawImage(candidateImage, 0, 0, this);

            clip.setColor(getForeground());
            String info = candidate.size() + " polygons, " + countVertices(candidate) + " vertices";
            FontMetrics fontMetrics = clip.getFontMetrics();
            int width = fontMetrics.stringWidth(info);
            int height = Math.round(fontMetrics.getLineMetrics(info, clip).getHeight());
            clip.drawString(info,
                            candidateImage.getWidth() - width,
                            candidateImage.getHeight() + height);
        }


        /**
         * Count the number of vertices in each polygon in the image and return
         * the total.
         * @param image The image to inspect.
         * @return The total number of vertices in all polygons in the image.
         */
        private int countVertices(List<ColouredPolygon> image)
        {
            int count = 0;
            for (ColouredPolygon polygon : image)
            {
                count += polygon.getVertices().size();
            }
            return count;
        }
    }
}
