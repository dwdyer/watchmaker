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
package org.uncommons.watchmaker.swing.evolutionmonitor;

import java.awt.BorderLayout;
import java.awt.Window;
import java.util.LinkedList;
import java.util.List;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JTabbedPane;
import javax.swing.SwingUtilities;
import org.uncommons.watchmaker.framework.EvolutionObserver;
import org.uncommons.watchmaker.framework.PopulationData;
import org.uncommons.watchmaker.framework.interactive.Renderer;

/**
 * The Evolution Monitor is a component that can be attached to an
 * {@link org.uncommons.watchmaker.framework.EvolutionEngine} to provide
 * real-time information (in a Swing GUI) about the current state of the
 * evolution.
 * @author Daniel Dyer
 */
public class EvolutionMonitor<T> implements EvolutionObserver<T>
{
    private final List<EvolutionObserver<? super T>> views = new LinkedList<EvolutionObserver<? super T>>();
    private final JComponent monitorComponent = new JTabbedPane();

    private Window window = null;


    /**
     * Creates an EvolutionMonitor with a single panel that graphs the fitness scores
     * of the population from generation to generation.
     */
    public EvolutionMonitor()
    {
        PopulationFitnessView fitnessView = new PopulationFitnessView();
        monitorComponent.add("Population Fitness", fitnessView);
        views.add(fitnessView);
    }


    /**
     * Creates an EvolutionMonitor with a second panel that displays a graphical
     * representation of the fittest candidate in the population.
     * @param renderer Renders a candidate solution as a JComponent.
     */
    public EvolutionMonitor(Renderer<T, JComponent> renderer)
    {
        this();
        FittestCandidateView<T> candidateView = new FittestCandidateView<T>(renderer);
        monitorComponent.add("Fittest Candidate So Far", candidateView);
        views.add(candidateView);
    }


    /**
     * {@inheritDoc}
     */
    public void populationUpdate(PopulationData<? extends T> populationData)
    {
        for (EvolutionObserver<? super T> view : views)
        {
            view.populationUpdate(populationData);
        }
    }

    
    public JComponent getGUIComponent()
    {
        return monitorComponent;
    }


    /**
     * Displays the evolution monitor component in a new {@link JFrame}.
     * @param title The title for the new frame.
     */
    public void showInFrame(final String title)
    {        
        SwingUtilities.invokeLater(new Runnable()
        {
            public void run()
            {
                JFrame frame = new JFrame(title);
                frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
                showWindow(frame);
            }
        });
    }


    /**
     * Displays the evolution monitor component in a new {@link JDialog}.
     * @param owner The owning frame for the new dialog.
     * @param title The title for the new dialog.
     * @param modal Whether the 
     */
    public void showInDialog(final JFrame owner,
                             final String title,
                             final boolean modal)
    {
        SwingUtilities.invokeLater(new Runnable()
        {
            public void run()
            {
                JDialog dialog = new JDialog(owner, title, modal);
                dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
                showWindow(dialog);
            }
        });
    }


    /**
     * Helper method for showing the evolution monitor in a frame or dialog.
     * @param newWindow The frame or dialog used to show the evolution monitor.
     */
    private void showWindow(Window newWindow)
    {
        if (window != null)
        {
            window.remove(getGUIComponent());
            window.setVisible(false);
            window.dispose();
            window = null;
        }
        newWindow.add(getGUIComponent(), BorderLayout.CENTER);
        newWindow.pack();
        newWindow.setVisible(true);
        this.window = newWindow;
    }
}
