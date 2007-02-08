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
import javax.swing.JComponent;
import javax.swing.JFrame;
import org.uncommons.maths.random.MersenneTwisterRNG;
import org.uncommons.watchmaker.framework.EvolutionEngine;
import org.uncommons.watchmaker.framework.StandaloneEvolutionEngine;
import org.uncommons.watchmaker.framework.interactive.InteractiveSelection;
import org.uncommons.watchmaker.framework.interactive.NullFitnessEvaluator;
import org.uncommons.watchmaker.framework.interactive.Renderer;
import org.uncommons.watchmaker.framework.interactive.SwingConsole;

/**
 * @author Daniel Dyer
 */
public class BiomorphExample
{
    public static void main(String[] args)
    {
        Renderer<Biomorph, JComponent> renderer = new SwingBiomorphRenderer();
        SwingConsole console = new SwingConsole();

        EvolutionEngine<Biomorph> engine = new StandaloneEvolutionEngine<Biomorph>(new BiomorphFactory(),
                                                                                   new BiomorphMutation(0.1d),
                                                                                   new NullFitnessEvaluator(),
                                                                                   new InteractiveSelection<Biomorph>(console, renderer),
                                                                                   new MersenneTwisterRNG());

        JFrame frame = new JFrame("Biomorphs");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.getContentPane().add(console, BorderLayout.CENTER);
        frame.setSize(800, 600);                
        frame.validate();
        frame.setVisible(true);

        engine.evolve(6, 0, 100);
    }
}
