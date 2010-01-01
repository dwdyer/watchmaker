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

import javax.swing.table.AbstractTableModel;

/**
 * {@link javax.swing.table.TableModel} for displaying a Sudoku
 * grid in a {@link javax.swing.JTable}.
 * @author Daniel Dyer
 */
class SudokuTableModel extends AbstractTableModel
{
    // In puzzle mode, the user can edit the given cells and all other
    // cells are blank.  In solution mode, all cells have values and are
    // uneditable.
    private boolean puzzleMode = true;

    // Solution mode data.
    private Sudoku sudoku;

    // Puzzle mode data.
    private final Character[][] cells = new Character[Sudoku.SIZE][Sudoku.SIZE];


    /**
     * Sets the Sudoku grid represented by this table model.
     */
    public void setSudoku(Sudoku sudoku)
    {
        this.sudoku = sudoku;
        puzzleMode = false;
        fireTableRowsUpdated(0, getRowCount() - 1);
    }


    /**
     * @return The Sudoku grid represented by this table model.
     */
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


    public Object getValueAt(int row, int column)
    {
        if (puzzleMode)
        {
            return cells[row][column];
        }
        else
        {
            return sudoku == null ? null : sudoku.getValue(row, column);
        }
    }


    @Override
    public boolean isCellEditable(int row, int column)
    {
        return puzzleMode;
    }


    @Override
    public void setValueAt(Object object, int row, int column)
    {
        Character value = (Character) object;
        if (!(value == null || (value >= '1' && value <= '9')))
        {
            throw new IllegalArgumentException("Invalid character: " + value);
        }
        cells[row][column] = value;
        fireTableCellUpdated(row, column);
    }


    /**
     * Sets all cells at once using the same patterns as supported by
     * {@link SudokuFactory}.
     * @param pattern A String representation of a Sudoku puzzle.  Each element
     * in the array represents a single row.  There are 9 elements and each has 9
     * characters, one per cell.  Number cells are represented by the characters
     * '0' to '9' and blank cells are represented by dots.
     */
    public void setPattern(String[] pattern)
    {
        if (pattern.length != Sudoku.SIZE)
        {
            throw new IllegalArgumentException("Pattern must have " + Sudoku.SIZE + " rows.");
        }
        for (int row = 0; row < pattern.length; row++)
        {
            String patternRow = pattern[row];
            if (patternRow.length() != Sudoku.SIZE)
            {
                throw new IllegalArgumentException("Row must have " + Sudoku.SIZE + " columns.");
            }
            for (int column = 0; column < patternRow.toCharArray().length; column++)
            {
                char c = patternRow.toCharArray()[column];
                cells[row][column] = c == '.' ? null : c;
            }
        }
        sudoku = null;
        puzzleMode = true;
        fireTableRowsUpdated(0, getRowCount() - 1);
    }


    /**
     * @return A Sudoku puzzle in the pattern format used by {@link SudokuFactory}.
     */
    public String[] getPattern()
    {
        String[] pattern = new String[Sudoku.SIZE];
        for (int i = 0; i < cells.length; i++)
        {
            Character[] row = cells[i];
            StringBuilder rowString = new StringBuilder();
            for (Character c : row)
            {
                rowString.append(c == null ? '.' : c);
            }
            pattern[i] = rowString.toString();
        }
        return pattern;
    }

}
