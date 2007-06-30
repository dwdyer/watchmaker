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

import javax.swing.table.AbstractTableModel;

/**
 * {@link javax.swing.table.TableModel} for displaying a Sudoku grid in a
 * {@link javax.swing.JTable}.
 * @author Daniel Dyer
 */
public class SudokuTableModel extends AbstractTableModel
{
    private Sudoku sudoku;

    public void setSudoku(Sudoku sudoku)
    {
        this.sudoku = sudoku;
        fireTableRowsUpdated(0, getRowCount() - 1);
    }


    public Sudoku getSudoku()
    {
        return sudoku;
    }

    
    public int getRowCount()
    {
        return Sudoku.SIZE;
    }


    public int getColumnCount()
    {
        return Sudoku.SIZE;
    }


    public Integer getValueAt(int row, int column)
    {
        return sudoku == null ? null : sudoku.getValue(row, column);
    }
}
