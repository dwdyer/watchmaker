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
package org.uncommons.swing;

import java.awt.Component;
import java.awt.Container;
import javax.swing.Spring;
import javax.swing.SpringLayout;

/**
 * Utility methods for creating form-style or grid-style layouts with SpringLayout.
 * Modified version of the class presented in the Sun Swing tutorial
 * (http://java.sun.com/docs/books/tutorial/uiswing/layout/examples/SpringUtilities.java).
 */
public final class SpringUtilities
{
    private SpringUtilities()
    {
        // Private constructor prevents instantiation of utility class.
    }


    /**
     * Aligns the first {@code rows} * {@code cols} components of {@code parent}
     * in a grid.  Each component in a column is as wide as the maximum preferred
     * width of the components in that column; height is similarly determined for
     * each row.  The parent is made just big enough to fit them all.
     * @param parent The container to layout.
     * @param rows number of rows
     * @param columns number of columns
     * @param initialX x location to start the grid at
     * @param initialY y location to start the grid at
     * @param xPad x padding between cells
     * @param yPad y padding between cells
     */
    public static void makeCompactGrid(Container parent,
                                       int rows,
                                       int columns,
                                       int initialX,
                                       int initialY,
                                       int xPad,
                                       int yPad)
    {
        if (!(parent.getLayout() instanceof SpringLayout))
        {
            throw new IllegalArgumentException("The first argument to makeCompactGrid must use SpringLayout.");
        }
        SpringLayout layout = (SpringLayout) parent.getLayout();

        // Align all cells in each column and make them the same width.
        Spring x = Spring.constant(initialX);
        for (int c = 0; c < columns; c++)
        {
            Spring width = Spring.constant(0);
            for (int r = 0; r < rows; r++)
            {
                width = Spring.max(width,
                                   getConstraintsForCell(r, c, parent, columns).getWidth());
            }
            for (int r = 0; r < rows; r++)
            {
                SpringLayout.Constraints constraints = getConstraintsForCell(r, c, parent, columns);
                constraints.setX(x);
                constraints.setWidth(width);
            }
            x = Spring.sum(x, Spring.sum(width, Spring.constant(xPad)));
        }

        // Align all cells in each row and make them the same height.
        Spring y = Spring.constant(initialY);
        for (int r = 0; r < rows; r++)
        {
            Spring height = Spring.constant(0);
            for (int c = 0; c < columns; c++)
            {
                height = Spring.max(height,
                                    getConstraintsForCell(r, c, parent, columns).getHeight());
            }
            for (int c = 0; c < columns; c++)
            {
                SpringLayout.Constraints constraints = getConstraintsForCell(r, c, parent, columns);
                constraints.setY(y);
                constraints.setHeight(height);
            }
            y = Spring.sum(y, Spring.sum(height, Spring.constant(yPad)));
        }

        // Set the parent's size.
        SpringLayout.Constraints parentConstraints = layout.getConstraints(parent);
        parentConstraints.setConstraint(SpringLayout.SOUTH, y);
        parentConstraints.setConstraint(SpringLayout.EAST, x);
    }


    /**
     * Helper method for {@link #makeCompactGrid(Container, int, int, int, int, int, int)}.
     * @return The constraints for the specified cell.
     */
    private static SpringLayout.Constraints getConstraintsForCell(int row,
                                                                  int col,
                                                                  Container parent,
                                                                  int cols)
    {
        SpringLayout layout = (SpringLayout) parent.getLayout();
        Component c = parent.getComponent(row * cols + col);
        return layout.getConstraints(c);
    }
}
