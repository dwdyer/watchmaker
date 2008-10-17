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
package org.uncommons.watchmaker.examples.music;

import java.awt.BorderLayout;
import java.util.List;
import java.util.Random;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.SwingUtilities;
import org.jfugue.Note;
import org.uncommons.maths.random.MersenneTwisterRNG;
import org.uncommons.watchmaker.framework.EvolutionEngine;
import org.uncommons.watchmaker.framework.EvolutionaryOperator;
import org.uncommons.watchmaker.framework.StandaloneEvolutionEngine;
import org.uncommons.watchmaker.framework.interactive.InteractiveSelection;
import org.uncommons.watchmaker.framework.interactive.Renderer;
import org.uncommons.watchmaker.framework.operators.ListCrossover;
import org.uncommons.watchmaker.framework.termination.GenerationCount;
import org.uncommons.watchmaker.swing.SwingConsole;

/**
 * @author Daniel Dyer
 */
public class MusicExample
{
    public static void main(String[] args) throws InterruptedException
    {
        MelodyFactory factory = new MelodyFactory(8);
        EvolutionaryOperator<List<?>> operator = new ListCrossover(2);
        final SwingConsole console = new SwingConsole();
        Renderer<List<Note>, JComponent> renderer = new SwingMelodyRenderer();
        InteractiveSelection<List<Note>> selection = new InteractiveSelection<List<Note>>(console,
                                                                                          renderer,
                                                                                          3,
                                                                                          5);
        Random rng = new MersenneTwisterRNG();

        EvolutionEngine<List<Note>> engine = new StandaloneEvolutionEngine<List<Note>>(factory,
                                                                                       operator,
                                                                                       selection,
                                                                                       rng);
        SwingUtilities.invokeLater(new Runnable()
        {
            public void run()
            {
                JFrame frame = new JFrame("Melody Evolver");
                frame.setDefaultCloseOperation(JDialog.EXIT_ON_CLOSE);
                frame.add(console, BorderLayout.CENTER);
                frame.setSize(640, 240);
                frame.setVisible(true);
            }
        });
        engine.evolve(10, 0, new GenerationCount(20));
    }
}
