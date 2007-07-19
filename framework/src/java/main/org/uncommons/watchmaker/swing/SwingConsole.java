// ============================================================================
//   Copyright 2006, 2007 Daniel W. Dyer
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
package org.uncommons.watchmaker.swing;

import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import org.uncommons.watchmaker.framework.interactive.Console;

/**
 * Swing-based console for interactive evolutionary algorithms.
 * @author Daniel Dyer
 */
public class SwingConsole extends JPanel implements Console<JComponent>
{
    private final Object lock = new Object();
    private int selectedIndex;

    /**
     * Creates a console that displays candidates arranged in three columns
     * (and as many rows as required).
     */
    public SwingConsole()
    {
        this(3);
    }


    /**
     * Creates a console with a configurable number of columns. 
     * @param columns The number of columns to use when displaying the
     * candidates for selection.
     */
    public SwingConsole(int columns)
    {
        super(new GridLayout(0, columns));
    }


    /**
     * {@inheritDoc}
     */
    public int select(final List<JComponent> renderedEntities)
    {
        selectedIndex = -1;
        SwingUtilities.invokeLater(new Runnable()
        {
            public void run()
            {
                removeAll();
                int index = -1;
                for (JComponent entity : renderedEntities)
                {
                    add(new EntityPanel(entity, ++index));
                }
                revalidate();
            }
        });
        return waitForSelection();
    }


    /**
     * Wait until the user has made a selection.
     * @return The index of the selected item.
     */
    private int waitForSelection()
    {
        synchronized (lock)
        {
            while (selectedIndex < 0)
            {
                try
                {
                    lock.wait();
                }
                catch (InterruptedException ex)
                {
                    // Ignore.
                }
            }
        }
        return selectedIndex;
    }


    /**
     * Swing panel that wraps a rendered entity and a button for selecting
     * that entity. 
     */
    private class EntityPanel extends JPanel
    {
        public EntityPanel(JComponent entityComponent,
                           final int index)
        {
            super(new BorderLayout());
            add(entityComponent, BorderLayout.CENTER);
            JButton selectButton = new JButton("Select");
            selectButton.addActionListener(new ActionListener()
            {
                public void actionPerformed(ActionEvent actionEvent)
                {
                    synchronized (lock)
                    {
                        selectedIndex = index;
                        lock.notifyAll();
                    }
                }
            });
            add(selectButton, BorderLayout.SOUTH);
            setBorder(BorderFactory.createEtchedBorder());
        }
    }
}
