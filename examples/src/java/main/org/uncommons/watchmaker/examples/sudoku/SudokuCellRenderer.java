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
package org.uncommons.watchmaker.examples.sudoku;

import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.border.Border;
import javax.swing.table.DefaultTableCellRenderer;
import org.uncommons.swing.ConfigurableLineBorder;

/**
 * Renders Sudoku cells in a JTable.  As well as displaying the cell value,
 * grid lines are drawn to conform to normal Sudoku conventions and incorrect
 * solutions have their invalid cells colour-coded.
 * @author Daniel Dyer
 */
class SudokuCellRenderer extends DefaultTableCellRenderer
{
    private static final Font VARIABLE_CELL_FONT = new Font("Monospaced", Font.PLAIN, 32);
    private static final Font FIXED_CELL_FONT = new Font("Monospaced", Font.BOLD, 34);
    private static final Color VARIABLE_TEXT_COLOUR = Color.DARK_GRAY;
    private static final Color FIXED_TEXT_COLOUR = Color.BLACK;

    private static final Color[] CONFLICT_COLOURS = {Color.WHITE, Color.YELLOW, Color.ORANGE, Color.RED};

    private static final Border TOP_BORDER = new ConfigurableLineBorder(true, false, false, false, 1);
    private static final Border BOTTOM_BORDER = new ConfigurableLineBorder(false, false, true, false, 1);
    private static final Border LEFT_BORDER = new ConfigurableLineBorder(false, true, false, false, 1);
    private static final Border RIGHT_BORDER = new ConfigurableLineBorder(false, false, false, true, 1);
    private static final Border TOP_LEFT_BORDER = new ConfigurableLineBorder(true, true, false, false, 1);
    private static final Border TOP_RIGHT_BORDER = new ConfigurableLineBorder(true, false, false, true, 1);
    private static final Border BOTTOM_LEFT_BORDER = new ConfigurableLineBorder(false, true, true, false, 1);
    private static final Border BOTTOM_RIGHT_BORDER = new ConfigurableLineBorder(false, false, true, true, 1);


    SudokuCellRenderer()
    {
        setHorizontalAlignment(JLabel.CENTER);
    }


    @Override
    public Component getTableCellRendererComponent(JTable table,
                                                   Object value,
                                                   boolean isSelected,
                                                   boolean hasFocus,
                                                   int row,
                                                   int column)
    {
        Component component = super.getTableCellRendererComponent(table,
                                                                  value,
                                                                  false,
                                                                  false,
                                                                  row,
                                                                  column);
        SudokuTableModel model = (SudokuTableModel) table.getModel();
        Sudoku sudoku = model.getSudoku();
        if (sudoku != null)
        {
            if (sudoku.isFixed(row, column))
            {
                component.setFont(FIXED_CELL_FONT);
                component.setForeground(FIXED_TEXT_COLOUR);
            }
            else
            {
                component.setFont(VARIABLE_CELL_FONT);
                component.setForeground(VARIABLE_TEXT_COLOUR);
            }

            int conflicts = 0;
            // Calculate conflicts in columns (there should be no conflicts in rows
            // because of the constraints enforced by the evolutionary operators).
            for (int i = 0; i < Sudoku.SIZE; i++)
            {
                if (i != row && model.getValueAt(i, column).equals(value))
                {
                    ++conflicts;
                }
            }
            // Calculate conflicts in sub-grid.
            int band = row / 3;
            int bandStart = band * 3;
            int stack = column / 3;
            int stackStart = stack * 3;
            for (int i = bandStart; i < bandStart + 3; i++)
            {
                for (int j = stackStart; j < stackStart + 3; j++)
                {
                    if (i != row && j != column && model.getValueAt(i, j).equals(value))
                    {
                        ++conflicts;
                    }
                }
            }

            // Color the cell based on how "wrong" it is.
            component.setBackground(CONFLICT_COLOURS[Math.min(conflicts, CONFLICT_COLOURS.length - 1)]);
        }
        else
        {
            // If the model is in puzzle mode, then all non-null cells are 'givens'.
            component.setFont(FIXED_CELL_FONT);
            component.setForeground(FIXED_TEXT_COLOUR);
        }

        ((JComponent) component).setBorder(getBorder(row, column));


        return component;
    }


    /**
     * Get appropriate border for cell based on its position in the grid.
     */
    private Border getBorder(int row, int column)
    {
        if (row % 3 == 2)
        {
            switch (column % 3)
            {
                case 2: return BOTTOM_RIGHT_BORDER;
                case 0: return BOTTOM_LEFT_BORDER;
                default: return BOTTOM_BORDER;
            }
        }
        else if (row % 3 == 0)
        {
            switch (column % 3)
            {
                case 2: return TOP_RIGHT_BORDER;
                case 0: return TOP_LEFT_BORDER;
                default: return TOP_BORDER;
            }
        }

        switch (column % 3)
        {
            case 2: return RIGHT_BORDER;
            case 0: return LEFT_BORDER;
            default: return null;
        }
    }
}
