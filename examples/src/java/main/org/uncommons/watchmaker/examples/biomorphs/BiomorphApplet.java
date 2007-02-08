// ============================================================================
//   Copyright 2007 Daniel W. Dyer
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
package org.uncommons.watchmaker.examples.biomorphs;

import java.awt.BorderLayout;
import javax.swing.JApplet;
import javax.swing.JComponent;
import javax.swing.SwingUtilities;
import org.uncommons.gui.SwingBackgroundTask;
import org.uncommons.maths.random.MersenneTwisterRNG;
import org.uncommons.watchmaker.framework.EvolutionEngine;
import org.uncommons.watchmaker.framework.SelectionStrategy;
import org.uncommons.watchmaker.framework.StandaloneEvolutionEngine;
import org.uncommons.watchmaker.framework.interactive.InteractiveSelection;
import org.uncommons.watchmaker.framework.interactive.NullFitnessEvaluator;
import org.uncommons.watchmaker.framework.interactive.Renderer;
import org.uncommons.watchmaker.framework.interactive.SwingConsole;

/**
 * @author Daniel Dyer
 */
public class BiomorphApplet extends JApplet
{
    private final Renderer<Biomorph, JComponent> renderer = new SwingBiomorphRenderer();
    private final SwingConsole console = new SwingConsole();

    @Override
    public void init()
    {
        SwingUtilities.invokeLater(new Runnable()
        {
            public void run()
            {
                add(console, BorderLayout.CENTER);
                validate();
            }
        });
    }


    @Override
    public void start()
    {
        SwingBackgroundTask<Biomorph> task = new SwingBackgroundTask<Biomorph>()
        {
            protected Biomorph performTask()
            {
                SelectionStrategy<Biomorph> selection = new InteractiveSelection<Biomorph>(console, renderer, 6);
                EvolutionEngine<Biomorph> engine = new StandaloneEvolutionEngine<Biomorph>(new BiomorphFactory(),
                                                                                           new BiomorphMutation(0.1d),
                                                                                           new NullFitnessEvaluator(),
                                                                                           selection,
                                                                                           new MersenneTwisterRNG());
                return engine.evolve(10, 0, 20);
            }

            protected void postProcessing(Biomorph result)
            {
                // TO DO:
            }
        };
        task.execute();
    }
}
