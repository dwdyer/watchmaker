// ============================================================================
//   Copyright 2006 Daniel W. Dyer
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
package org.uncommons.gui;

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
     * in a grid. Each component is as big as the maximum preferred width and
     * height of the components.  The parent is made just big enough to fit them
     * all.
     * @param parent The container to layout.
     * @param rows Number of rows
     * @param cols Number of columns
     * @param initialX x location to start the grid at
     * @param initialY y location to start the grid at
     * @param xPad x padding between cells
     * @param yPad y padding between cells
     */
    public static void makeGrid(Container parent,
                                int rows,
                                int cols,
                                int initialX,
                                int initialY,
                                int xPad, int yPad)
    {
        if (!(parent.getLayout() instanceof SpringLayout))
        {
            throw new IllegalArgumentException("The first argument to makeGrid must use SpringLayout.");
        }
        SpringLayout layout = (SpringLayout) parent.getLayout();

        Spring xPadSpring = Spring.constant(xPad);
        Spring yPadSpring = Spring.constant(yPad);
        Spring initialXSpring = Spring.constant(initialX);
        Spring initialYSpring = Spring.constant(initialY);
        int max = rows * cols;

        // Calculate Springs that are the max of the width/height so that all
        // cells have the same size.
        Spring maxWidthSpring = layout.getConstraints(parent.getComponent(0)).getWidth();
        Spring maxHeightSpring = layout.getConstraints(parent.getComponent(0)).getWidth();
        for (int i = 1; i < max; i++)
        {
            SpringLayout.Constraints cons = layout.getConstraints(parent.getComponent(i));
            maxWidthSpring = Spring.max(maxWidthSpring, cons.getWidth());
            maxHeightSpring = Spring.max(maxHeightSpring, cons.getHeight());
        }

        // Apply the new width/height Spring. This forces all the
        // components to have the same size.
        for (int i = 0; i < max; i++)
        {
            SpringLayout.Constraints cons = layout.getConstraints(parent.getComponent(i));
            cons.setWidth(maxWidthSpring);
            cons.setHeight(maxHeightSpring);
        }

        // Then adjust the x/y constraints of all the cells so that they
        // are aligned in a grid.
        SpringLayout.Constraints lastConstraints = null;
        SpringLayout.Constraints lastRowConstraints = null;
        for (int i = 0; i < max; i++)
        {
            SpringLayout.Constraints constraints = layout.getConstraints(parent.getComponent(i));
            if (i % cols == 0) // Start of new row.
            {
                lastRowConstraints = lastConstraints;
                constraints.setX(initialXSpring);
            }
            else // X position depends on previous component.
            {
                constraints.setX(Spring.sum(lastConstraints.getConstraint(SpringLayout.EAST),
                                            xPadSpring));
            }

            if (i / cols == 0) // First row.
            {
                constraints.setY(initialYSpring);
            }
            else // Y position depends on previous row.
            {
                constraints.setY(Spring.sum(lastRowConstraints.getConstraint(SpringLayout.SOUTH),
                                            yPadSpring));
            }
            lastConstraints = constraints;
        }

        // Set the parent's size.
        SpringLayout.Constraints pCons = layout.getConstraints(parent);
        pCons.setConstraint(SpringLayout.SOUTH,
                            Spring.sum(Spring.constant(yPad),
                                       lastConstraints.getConstraint(SpringLayout.SOUTH)));
        pCons.setConstraint(SpringLayout.EAST,
                            Spring.sum(Spring.constant(xPad),
                                       lastConstraints.getConstraint(SpringLayout.EAST)));
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
