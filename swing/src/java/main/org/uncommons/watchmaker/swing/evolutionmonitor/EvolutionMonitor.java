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
package org.uncommons.watchmaker.swing.evolutionmonitor;

import java.awt.BorderLayout;
import java.awt.Window;
import java.lang.reflect.InvocationTargetException;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.TimeUnit;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.SwingUtilities;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.StandardChartTheme;
import org.uncommons.watchmaker.framework.PopulationData;
import org.uncommons.watchmaker.framework.interactive.Renderer;
import org.uncommons.watchmaker.framework.islands.IslandEvolutionObserver;
import org.uncommons.watchmaker.swing.ObjectSwingRenderer;
import org.uncommons.watchmaker.swing.SwingIslandEvolutionObserver;

/**
 * The Evolution Monitor is a component that can be attached to an
 * {@link org.uncommons.watchmaker.framework.EvolutionEngine} to provide
 * real-time information (in a Swing GUI) about the current state of the
 * evolution.
 * @param <T> The type of the evolved entities monitored by this component.
 * @author Daniel Dyer
 */
public class EvolutionMonitor<T> implements IslandEvolutionObserver<T>
{
    private final List<IslandEvolutionObserver<? super T>> views = new LinkedList<IslandEvolutionObserver<? super T>>();

    private JComponent monitorComponent;
    private Window window = null;

    private final boolean islands;

    /**
     * <p>Creates an EvolutionMonitor with a single panel that graphs the fitness scores
     * of the population from generation to generation.</p>
     * <p>If you are using {@link org.uncommons.watchmaker.framework.islands.IslandEvolution},
     * use the {@link #EvolutionMonitor(boolean)} constructor instead, to enable island support.</p>
     */
    public EvolutionMonitor()
    {
        this(false);
    }

    
    /**
     * Creates an EvolutionMonitor with a single panel that graphs the fitness scores
     * of the population from generation to generation.
     * @param islands Whether the monitor should be configured for displaying data from
     * {@link org.uncommons.watchmaker.framework.islands.IslandEvolution}.  Set this
     * parameter to false when using a standard {@link org.uncommons.watchmaker.framework.EvolutionEngine}
     * or if you don't want to display island-specific data for island evolution.
     */
    public EvolutionMonitor(boolean islands)
    {
        this(new ObjectSwingRenderer(), islands);
    }


    /**
     * Creates an EvolutionMonitor with a second panel that displays a graphical
     * representation of the fittest candidate in the population.
     * @param renderer Renders a candidate solution as a JComponent.
     * @param islands Whether the monitor should be configured for displaying data from
     * {@link org.uncommons.watchmaker.framework.islands.IslandEvolution}.  Set this
     * parameter to false when using a standard {@link org.uncommons.watchmaker.framework.EvolutionEngine}
     */
    public EvolutionMonitor(final Renderer<? super T, JComponent> renderer, boolean islands)
    {
        this.islands = islands;
        if (SwingUtilities.isEventDispatchThread())
        {
            init(renderer);
        }
        else
        {
            try
            {
                SwingUtilities.invokeAndWait(new Runnable()
                {
                    public void run()
                    {
                        init(renderer);
                    }
                });
            }
            catch (InterruptedException ex)
            {
                Thread.currentThread().interrupt();
                throw new IllegalStateException(ex);
            }
            catch (InvocationTargetException ex)
            {
                throw new IllegalStateException(ex);
            }
        }
    }

    
    private void init(Renderer<? super T, JComponent> renderer)
    {
        // Make sure all JFreeChart charts are created with the legacy theme
        // (grey surround and white data area).
        ChartFactory.setChartTheme(StandardChartTheme.createLegacyTheme());

        JTabbedPane tabs = new JTabbedPane();
        monitorComponent = new JPanel(new BorderLayout());
        monitorComponent.add(tabs, BorderLayout.CENTER);

        FittestCandidateView<T> candidateView = new FittestCandidateView<T>(renderer);
        tabs.add("Fittest Individual", candidateView);
        views.add(new SwingIslandEvolutionObserver<T>(candidateView, 300, TimeUnit.MILLISECONDS));

        PopulationFitnessView fitnessView = new PopulationFitnessView(islands);
        tabs.add(islands ? "Global Population" : "Population Fitness", fitnessView);
        views.add(fitnessView);

        if (islands)
        {
            IslandsView islandsView = new IslandsView();
            tabs.add("Island Populations", islandsView);
            views.add(new SwingIslandEvolutionObserver<Object>(islandsView, 300, TimeUnit.MILLISECONDS));
        }

        JVMView jvmView = new JVMView();
        tabs.add("JVM Memory", jvmView);

        StatusBar statusBar = new StatusBar(islands);
        monitorComponent.add(statusBar, BorderLayout.SOUTH);
        views.add(new SwingIslandEvolutionObserver<Object>(statusBar, 300, TimeUnit.MILLISECONDS));
    }


    /**
     * {@inheritDoc}
     */
    public void populationUpdate(PopulationData<? extends T> populationData)
    {
        for (IslandEvolutionObserver<? super T> view : views)
        {
            view.populationUpdate(populationData);
        }
    }


    /**
     * {@inheritDoc}
     */
    public void islandPopulationUpdate(int islandIndex, PopulationData<? extends T> populationData)
    {
        for (IslandEvolutionObserver<? super T> view : views)
        {
            view.islandPopulationUpdate(islandIndex, populationData);
        }
    }


    public JComponent getGUIComponent()
    {
        return monitorComponent;
    }


    /**
     * Displays the evolution monitor component in a new {@link JFrame}.  There is no
     * need to make sure this method is invoked from the Event Dispatch Thread, the
     * method itself ensures that the window is created and displayed from the EDT.
     * @param title The title for the new frame.
     * @param exitOnClose Whether the JVM should exit when the frame is closed.  Useful
     * if this is the only application window.
     */
    public void showInFrame(final String title,
                            final boolean exitOnClose)
    {        
        SwingUtilities.invokeLater(new Runnable()
        {
            public void run()
            {
                JFrame frame = new JFrame(title);
                frame.setDefaultCloseOperation(exitOnClose ? JFrame.EXIT_ON_CLOSE : JFrame.DISPOSE_ON_CLOSE);
                showWindow(frame);
            }
        });
    }


    /**
     * Displays the evolution monitor component in a new {@link JDialog}.  There is no
     * need to make sure this method is invoked from the Event Dispatch Thread, the
     * method itself ensures that the window is created and displayed from the EDT.
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
