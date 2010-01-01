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

import java.math.BigDecimal;
import javax.swing.JComponent;
import javax.swing.text.JTextComponent;
import org.testng.annotations.Test;
import org.uncommons.watchmaker.framework.interactive.Renderer;

/**
 * Unit test for the {@link ObjectSwingRenderer} class.
 * @author Daniel Dyer
 */
public class ObjectSwingRendererTest
{
    @Test
    public void testRendering()
    {
        Renderer<Object, JComponent> renderer = new ObjectSwingRenderer();
        JTextComponent textComponent = (JTextComponent) renderer.render(BigDecimal.TEN);
        assert textComponent.getText().equals("10") : "Wrong text rendered.";
        assert !textComponent.isEditable() : "Text component should not be editable.";
    }
}
