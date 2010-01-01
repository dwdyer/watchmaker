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

import java.awt.BorderLayout;
import java.awt.Color;
import javax.swing.BorderFactory;
import javax.swing.DefaultCellEditor;
import javax.swing.JComboBox;
import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;

/**
 * A component for displaying Sudoku puzzles and solutions.
 * @author Daniel Dyer
 */
class SudokuView extends JPanel
{
    private final SudokuTableModel sudokuTableModel = new SudokuTableModel();

    SudokuView()
    {
        super(new BorderLayout());
        JTable sudokuTable = new JTable(sudokuTableModel);
        sudokuTable.setRowHeight(40);
        sudokuTable.setGridColor(Color.GRAY);
        sudokuTable.setShowGrid(true);
        TableColumnModel columnModel = sudokuTable.getColumnModel();
        TableCellRenderer renderer = new SudokuCellRenderer();
        JComboBox valueCombo = new JComboBox(new Object[]{null, '1', '2', '3', '4', '5', '6', '7', '8', '9'});
        TableCellEditor editor = new DefaultCellEditor(valueCombo);
        for (int i = 0; i < columnModel.getColumnCount(); i++)
        {
            TableColumn column = columnModel.getColumn(i);
            column.setCellRenderer(renderer);
            column.setCellEditor(editor);
        }
        add(sudokuTable, BorderLayout.CENTER);
        setBorder(BorderFactory.createTitledBorder("Puzzle/Solution"));
    }


    public void setSolution(Sudoku sudoku)
    {
        sudokuTableModel.setSudoku(sudoku);
    }


    public void setPuzzle(String[] pattern)
    {
        sudokuTableModel.setPattern(pattern);
    }


    public String[] getPuzzle()
    {
        return sudokuTableModel.getPattern();
    }
}
