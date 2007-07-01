// ============================================================================
//   Copyright 2006, 2007 Daniel W. Dyer
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
package org.uncommons.watchmaker.examples.sudoku;

import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;

/**
 * @author Daniel Dyer
 */
public class SudokuCellRenderer extends DefaultTableCellRenderer
{
    private static final Font VARIABLE_CELL_FONT = new Font("Monospaced", Font.PLAIN, 14);
    private static final Font FIXED_CELL_FONT = new Font("Monospaced", Font.BOLD, 14);

    private static final Color[] CONFLICT_COLOURS = new Color[]{Color.WHITE, Color.YELLOW, Color.ORANGE, Color.RED};

    public SudokuCellRenderer()
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
            component.setFont(sudoku.isFixed(row, column) ? FIXED_CELL_FONT : VARIABLE_CELL_FONT);

            int conflicts = 0;
            // Calculate conflicts in columns (there should be no conflicts in rows
            // because of the constraints enforced by the evolutionary operators).
            for (int i = 0; i < Sudoku.SIZE; i++)
            {
                if (i != row && sudoku.getValue(i, column) == (Integer) value)
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
                    if (i != row && j != column && sudoku.getValue(i, j) == (Integer) value)
                    {
                        ++conflicts;
                    }
                }
            }

            // Color the cell based on how "wrong" it is.
            component.setBackground(CONFLICT_COLOURS[Math.min(conflicts, CONFLICT_COLOURS.length - 1)]);
        }
        return component;
    }
}
