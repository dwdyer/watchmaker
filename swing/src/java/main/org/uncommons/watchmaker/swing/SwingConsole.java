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
package org.uncommons.watchmaker.swing;

import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
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
    private final Lock lock = new ReentrantLock();
    private final Condition selected = lock.newCondition();
    private final AtomicInteger selectedIndex = new AtomicInteger(-1);

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
     * This method blocks and therefore must not be invoked from the Event
     * Dispatch Thread.
     * {@inheritDoc}
     */
    public int select(final List<? extends JComponent> renderedEntities)
    {
        selectedIndex.set(-1);
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
        waitForSelection();
        return selectedIndex.get();
    }


    /**
     * Wait until the user has made a selection.
     */
    private void waitForSelection()
    {
        lock.lock();
        try
        {
            while (selectedIndex.get() < 0)
            {
                selected.awaitUninterruptibly();
            }
        }
        finally
        {
            lock.unlock();
        }
    }


    /**
     * Swing panel that wraps a rendered entity and a button for selecting
     * that entity. 
     */
    private class EntityPanel extends JPanel
    {
        EntityPanel(JComponent entityComponent, final int index)
        {
            super(new BorderLayout());
            add(entityComponent, BorderLayout.CENTER);
            JButton selectButton = new JButton("Select");
            selectButton.setName("Selection-" + index); // This helps to find the button from a unit test.
            selectButton.addActionListener(new ActionListener()
            {
                public void actionPerformed(ActionEvent actionEvent)
                {
                    lock.lock();
                    try
                    {
                        selectedIndex.set(index);
                        selected.signalAll();
                    }
                    finally
                    {
                        lock.unlock();
                    }
                }
            });
            add(selectButton, BorderLayout.SOUTH);
            setBorder(BorderFactory.createEtchedBorder());
        }
    }
}
