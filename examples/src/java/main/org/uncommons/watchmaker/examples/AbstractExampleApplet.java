// ============================================================================
//   Copyright 2006-2009 Daniel W. Dyer
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
package org.uncommons.watchmaker.examples;

import java.awt.Container;
import javax.swing.JApplet;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;

/**
 * Base class for examples that run as applets.
 * @author Daniel Dyer
 */
public abstract class AbstractExampleApplet extends JApplet
{
    @Override
    public void init()
    {
        configure(this);
    }

    
    protected final void configure(final Container container)
    {
        try
        {
            // Use invokeAndWait so that we can be sure that initialisation is complete
            // before continuing.
            SwingUtilities.invokeAndWait(new Runnable()
            {
                public void run()
                {
                    prepareGUI(container);
                }
            });
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, ex, "Error Occurred", JOptionPane.ERROR_MESSAGE);
        }
    }


    /**
     * Implemented in sub-classes to initialise and layout the GUI.
     * @param container The container that this method should add components to.
     */
    protected abstract void prepareGUI(Container container);


    /**
     * Display this example program using a JFrame as the top-level GUI container (rather
     * than running the example as an applet).
     * @param title The text to use for the frame's title bar.
     */
    protected void displayInFrame(String title)
    {
        JFrame frame = new JFrame(title);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        configure(frame);
        frame.pack();
        frame.setVisible(true);
    }
}
