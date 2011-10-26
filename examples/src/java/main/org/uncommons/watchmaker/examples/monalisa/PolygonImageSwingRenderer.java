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
import java.awt.geom.AffineTransform;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;
import java.util.List;
import javax.swing.JComponent;
import org.uncommons.watchmaker.framework.interactive.Renderer;

/**
 * Converts a {@link BufferedImage} to a {@link JComponent} that displays that image alongside a
 * pre-specified target image.
 * <p/>
 * @author Daniel Dyer
 */
public class PolygonImageSwingRenderer implements Renderer<List<ColouredPolygon>, JComponent>
{
    private final Renderer<List<ColouredPolygon>, BufferedImage> delegate;
    private final int width;
    private final int height;
    private final double zoomFactor;
    private final AffineTransformOp zoomOp;


    /**
     * @param width The width of the candidate.
     * @param height The height of the candidate.
     * @param antialias Whether or not to enable anti-aliasing for the rendered image.
     * @param zoomFactor indicates the scale factor to apply to the image being rendered
     */
    public PolygonImageSwingRenderer(int width, int height, boolean antialias, double zoomFactor)
    {
        this.zoomFactor = zoomFactor;
        this.width = (int) (width * zoomFactor);
        this.height = (int) (height * zoomFactor);
        this.delegate = new PolygonImageRenderer(new Dimension(width, height),
            antialias, null);
        this.zoomOp = new AffineTransformOp(AffineTransform.getScaleInstance(zoomFactor, zoomFactor),
            AffineTransformOp.TYPE_NEAREST_NEIGHBOR);
    }


    /**
     * Renders the specified image as a JComponent with the image on the right and the pre-specified
     * target image on the left.
     * <p/>
     * @param entity The image to render on the right side of the component.
     * @return A Swing component that displays the rendered image.
     */
    public JComponent render(List<ColouredPolygon> entity)
    {
        return new ImageComponent(entity);
    }

    /**
     * Swing component for rendering a fixed size image. If the image is smaller than the component,
     * it is centered.
     */
    private final class ImageComponent extends JComponent
    {
        private final List<ColouredPolygon> candidate;
        private final Dimension minimumSize;


        ImageComponent(List<ColouredPolygon> candidate)
        {
            this.candidate = candidate;
            this.minimumSize = new Dimension(width, height);
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
            BufferedImage candidateImage = delegate.render(candidate);
            candidateImage.setAccelerationPriority(1);
            Graphics clip = graphics.create(x, y,
                (int) (candidateImage.getWidth() * zoomFactor),
                (int) (candidateImage.getHeight() * zoomFactor));
            clip.drawImage(zoomOp.filter(candidateImage, null), 0, 0, this);

            clip.setColor(getForeground());
            String info = candidate.size() + " polygons, " + countVertices(candidate) + " vertices";
            FontMetrics fontMetrics = clip.getFontMetrics();
            int width = fontMetrics.stringWidth(info);
            int height = Math.round(fontMetrics.getLineMetrics(info, clip).getHeight());
            clip.drawString(info,
                (int) (candidateImage.getWidth() * zoomFactor - width),
                (int) (candidateImage.getHeight() * zoomFactor + height));
            clip.dispose();
        }


        /**
         * Count the number of vertices in each polygon in the image and return the total.
         * <p/>
         * @param image The image to inspect.
         * @return The total number of vertices in all polygons in the image.
         */
        private int countVertices(List<ColouredPolygon> image)
        {
            int count = 0;
            for (ColouredPolygon polygon: image)
            {
                count += polygon.getVertices().size();
            }
            return count;
        }
    }
}
