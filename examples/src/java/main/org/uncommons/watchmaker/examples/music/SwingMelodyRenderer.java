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
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import org.jfugue.Note;
import org.jfugue.Pattern;
import org.jfugue.Player;
import org.uncommons.swing.SwingBackgroundTask;
import org.uncommons.watchmaker.framework.interactive.Renderer;

/**
 * Renders melodies as JComponents so that they can be presented and played
 * in the console of an interactive evolutionary algorithm.
 * @author Daniel Dyer
 */
public class SwingMelodyRenderer implements Renderer<List<Note>, JComponent>
{
    public JComponent render(List<Note> melody)
    {
        final Pattern pattern = convertNotesToPattern(melody);

        JPanel panel = new JPanel(new BorderLayout());

        JTextArea textArea = new JTextArea(formatMusicString(pattern.getMusicString()));
        textArea.setEditable(false);
        textArea.setFont(new Font("Monospaced", Font.PLAIN, textArea.getFont().getSize()));
        JScrollPane scroller = new JScrollPane(textArea);
        panel.add(scroller, BorderLayout.CENTER);

        final JButton playButton = new JButton("Play");
        playButton.addActionListener(new ActionListener()
        {
            public void actionPerformed(ActionEvent actionEvent)
            {
                playButton.setEnabled(false);
                new SwingBackgroundTask<Object>()
                {
                    protected Object performTask() throws Exception
                    {
                        Player player = new Player();
                        player.play(pattern);
                        player.close();
                        return null;
                    }


                    @Override
                    protected void postProcessing(Object result)
                    {
                        playButton.setEnabled(true);
                    }
                }.execute();
            }
        });
        panel.add(playButton, BorderLayout.SOUTH);

        return panel;
    }


    private String formatMusicString(String musicString)
    {
        String[] tokens = musicString.trim().split("\\s");
        StringBuilder buffer = new StringBuilder();
        for (String token : tokens)
        {
            buffer.append(token);
            buffer.append("\n");
        }
        return buffer.toString();
    }


    /**
     * Converts a list of {@link Note}s into a {@link Pattern} that
     * can be played by JFugue.
     * @param notes A sequence of individual notes.
     * @return A pattern of music created from the specified notes.
     */
    private Pattern convertNotesToPattern(List<Note> notes)
    {
        Pattern pattern = new Pattern();
        for (Note note : notes)
        {
            pattern.addElement(note);
        }
        return pattern;
    }
}
