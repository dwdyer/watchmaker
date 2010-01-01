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

import javax.swing.JComponent;
import javax.swing.JTextArea;
import org.uncommons.watchmaker.framework.interactive.Renderer;

/**
 * A default {@link Renderer} implementation that can display any object
 * as a Swing component.  It simply converts the object into its String
 * representation (via the {@link Object#toString()} method) and shows
 * that in a text area.
 * @author Daniel Dyer
 */
public class ObjectSwingRenderer implements Renderer<Object, JComponent>
{
    /**
     * Calls {@link Object#toString()} on the specified entity and creates
     * a {@link JTextArea} containing that text.
     * @param entity The evolved entity to render.
     * @return A text area containing the string representation of the entity.
     */
    public JComponent render(Object entity)
    {
        JTextArea text = new JTextArea(entity.toString());
        text.setEditable(false);
        text.setBackground(null);
        text.setLineWrap(true);
        text.setWrapStyleWord(true);
        return text;
    }
}
