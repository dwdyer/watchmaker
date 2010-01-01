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
package org.uncommons.watchmaker.examples.geneticprogramming;

import java.awt.Dimension;
import javax.swing.JComponent;
import org.testng.annotations.Test;

/**
 * Basic unit test for the {@link SwingGPTreeRenderer} class.
 * @author Daniel Dyer
 */
public class SwingGPTreeRendererTest
{
    // There's not much we can effectively test for this class, but we can at least
    // make sure the generated component has the correct dimensions.
    @Test
    public void testSizes()
    {
        SwingGPTreeRenderer renderer = new SwingGPTreeRenderer();

        // Simple case.
        Node tree = new Constant(2);
        JComponent component = renderer.render(tree);
        Dimension minSize = component.getMinimumSize();
        assert minSize.width == 30 : "Wrong width: " + minSize.width;
        assert minSize.height == 50 : "Wrong height: " + minSize.height;

        // More complicated case.
        tree = new IfThenElse(new Parameter(0),
                              new Addition(new Constant(2), new Parameter(1)),
                              new Constant(5));
        component = renderer.render(tree);
        minSize = component.getMinimumSize();
        assert minSize.width == 120 : "Wrong width: " + minSize.width;
        assert minSize.height == 150 : "Wrong height: " + minSize.height;
    }
}
