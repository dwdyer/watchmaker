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
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import org.uncommons.watchmaker.framework.EvolutionObserver;
import org.uncommons.watchmaker.framework.PopulationData;
import org.uncommons.watchmaker.framework.interactive.Renderer;

/**
 * {@link EvolutionMonitor} view for displaying a graphical representation
 * of the fittest candidate found so far.  This allows us to monitor the
 * progress of an evolutionary algorithm.
 * @param <T> The type of the evolved entity displayed by this component.
 * @author Daniel Dyer
 */
class FittestCandidateView<T> extends JPanel implements EvolutionObserver<T>
{
    private final Renderer<T, JComponent> renderer;

    public FittestCandidateView(Renderer<T, JComponent> renderer)
    {
        super(new BorderLayout());
        this.renderer = renderer;
    }

    
    public void populationUpdate(final PopulationData<? extends T> populationData)
    {
        SwingUtilities.invokeLater(new Runnable()
        {
            public void run()
            {
                removeAll();
                add(new JLabel("Fitness: " + populationData.getBestCandidateFitness(), JLabel.CENTER),
                    BorderLayout.NORTH);
                add(renderer.render(populationData.getBestCandidate()), BorderLayout.CENTER);
                revalidate();
            }
        });
    }
}
