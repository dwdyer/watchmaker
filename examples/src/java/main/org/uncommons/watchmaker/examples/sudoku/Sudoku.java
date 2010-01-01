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

/**
 * A potential solution for a Sudoku puzzle.
 * @author Daniel Dyer
 */
public final class Sudoku
{
    /** The dimensions (in cells) of the puzzle square. */
    public static final int SIZE = 9;
    private static final int MIN_VALUE = 1;
    private static final int MAX_VALUE = SIZE;

    private final Cell[][] cells;


    /**
     * Creates a sudoku solution from a 2-dimensional array of cells.
     * @param cells The cells that make-up this Sudoku grid.
     */
    public Sudoku(Cell[][] cells)
    {
        if (cells.length != SIZE)
        {
            throw new IllegalArgumentException("Sudoku must have 9 rows.");
        }
        if (cells[0].length != SIZE) // Should really check all rows because the array may not be square.
        {
            throw new IllegalArgumentException("Sudoku must have 9 columns.");
        }
        this.cells = cells;
    }


    /**
     * Queries the value of a particular cell.
     * @param row The row index of the cell.
     * @param column The column index of the cell.
     * @return The value (1 - 9) of the specified cell.
     */
    public int getValue(int row, int column)
    {
        return cells[row][column].getValue();
    }


    /**
     * Checks whether a particular cell is a 'given' or not. 
     * @param row The row index of the cell.
     * @param column The column index of the cell.
     * @return True if the value in the identified cell is fixed (a 'given' cell),
     * false otherwise.
     */
    public boolean isFixed(int row, int column)
    {
        return cells[row][column].isFixed();
    }


    /**
     * Returns an array of cells that make up a row.  The array
     * returned is a clone of the underlying data structure and
     * therefore can be modified without affecting this object.
     * @param row The index of the row to return.
     * @return A row of cells from this Sudoku grid.
     */
    public Cell[] getRow(int row)
    {
        return cells[row].clone();
    }


    /**
     * Renders the Sudoku grid as a multi-line String.
     * @return The String representation of this grid.
     */
    @Override
    public String toString()
    {
        StringBuilder buffer = new StringBuilder();
        for (Cell[] row : cells)
        {
            for (Cell cell : row)
            {
                buffer.append(' ');
                buffer.append(cell.getValue());
            }
            buffer.append('\n');
        }
        return buffer.toString();
    }


    /**
     * A single cell in a sudoku grid.  Contains a value and may be fixed (i.e. it is
     * one of the given cells at the start).
     */
    public static final class Cell
    {
        private final int value;
        private final boolean fixed;

        /**
         * @param value The value (1 - 9) contained in this cell.
         * @param fixed Whether or not this cell's value is fixed (a 'given').
         */
        public Cell(int value, boolean fixed)
        {
            if (value < MIN_VALUE || value > MAX_VALUE)
            {
                throw new IllegalArgumentException("Value must be between 1 and 9.");
            }
            this.value = value;
            this.fixed = fixed;
        }


        /**
         * @return The value contained in this cell.
         */
        public int getValue()
        {
            return value;
        }


        /**
         * @return True if this cell is a 'given', false otherwise.
         */
        public boolean isFixed()
        {
            return fixed;
        }
    }
}
