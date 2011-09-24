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
import java.awt.Font;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.SwingUtilities;
import org.uncommons.watchmaker.framework.PopulationData;
import org.uncommons.watchmaker.framework.interactive.Renderer;
import org.uncommons.watchmaker.framework.islands.IslandEvolutionObserver;

/**
 * {@link EvolutionMonitor} view for displaying a graphical representation
 * of the fittest candidate found so far.  This allows us to monitor the
 * progress of an evolutionary algorithm.
 * @param <T> The type of the evolved entity displayed by this component.
 * @author Daniel Dyer
 */
class FittestCandidateView<T> extends JPanel implements IslandEvolutionObserver<T>
{
    private static final Font BIG_FONT = new Font("Dialog", Font.BOLD, 16);

    private final Renderer<? super T, JComponent> renderer;
    private final JLabel fitnessLabel = new JLabel("N/A", JLabel.CENTER);
    private final JScrollPane scroller = new JScrollPane();

    private T fittestCandidate = null;

    /**
     * Creates a Swing view that uses the specified renderer to display
     * evolved entities.
     * @param renderer A renderer that convert evolved entities of the type
     * recognised by this view into Swing components.
     */
    FittestCandidateView(Renderer<? super T, JComponent> renderer)
    {
        super(new BorderLayout(0, 10));
        this.renderer = renderer;

        JPanel header = new JPanel(new BorderLayout());
        JLabel label = new JLabel("Fitness", JLabel.CENTER);
        header.add(label, BorderLayout.NORTH);
        fitnessLabel.setFont(BIG_FONT);
        header.add(fitnessLabel, BorderLayout.CENTER);
        add(header, BorderLayout.NORTH);

        scroller.setBackground(null);
        scroller.getViewport().setBackground(null);
        scroller.setBorder(null);
        add(scroller, BorderLayout.CENTER);

        // Set names for easier identification in unit tests.
        fitnessLabel.setName("FitnessLabel");
    }


    public void populationUpdate(final PopulationData<? extends T> populationData)
    {
        SwingUtilities.invokeLater(new Runnable()
        {
            public void run()
            {
                fitnessLabel.setText(String.valueOf(populationData.getBestCandidateFitness()));
                // If the fittest candidate is already displayed (because it was the fittest
                // candidate in the previous generation), don't incur the expense of rendering
                // it again.  Note that we still have to update the fitness score label above
                // because the fitness may be different even if the candidate isn't (in the
                // case where fitness is evaluated against other members of the population).
                if (populationData.getBestCandidate() != fittestCandidate)
                {
                    fittestCandidate = populationData.getBestCandidate();
                    JComponent renderedCandidate = renderer.render(fittestCandidate);
                    scroller.setViewportView(renderedCandidate);
                }
            }
        });
    }


    public void islandPopulationUpdate(int islandIndex, final PopulationData<? extends T> populationData)
    {
        // Do nothing.
    }
}
