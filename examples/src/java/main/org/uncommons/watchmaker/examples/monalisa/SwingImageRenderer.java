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
import java.awt.Graphics;
import java.awt.GridLayout;
import java.awt.image.BufferedImage;
import javax.swing.JComponent;
import javax.swing.JPanel;
import org.uncommons.watchmaker.framework.interactive.Renderer;

/**
 * Converts a {@link BufferedImage} to a {@link JComponent} that displays that image.
 * @author Daniel Dyer
 */
public class SwingImageRenderer implements Renderer<BufferedImage, JComponent>
{
    private final BufferedImage targetImage;


    public SwingImageRenderer(BufferedImage targetImage)
    {
        this.targetImage = targetImage;
    }

    
    public JComponent render(BufferedImage entity)
    {
        JPanel panel = new JPanel(new GridLayout(1, 2));
        panel.add(new ImageComponent(targetImage));
        panel.add(new ImageComponent(entity));
        return panel;
    }


    private static final class ImageComponent extends JComponent
    {
        private final BufferedImage image;

        public ImageComponent(BufferedImage image)
        {
            this.image = image;
        }

        @Override
        public Dimension getPreferredSize()
        {
            return new Dimension(image.getWidth(), image.getHeight());
        }

        @Override
        public Dimension getMinimumSize()
        {
            return new Dimension(image.getWidth(), image.getHeight());
        }


        @Override
        protected void paintComponent(Graphics g)
        {
            int x = Math.max(0, (getWidth() - image.getWidth()) / 2);
            int y = Math.max(0, (getHeight() - image.getHeight()) / 2);
            g.drawImage(image, x, y, image.getWidth(), image.getHeight(), null, this);
        }
    }
}
