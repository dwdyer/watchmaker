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
import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.table.TableCellRenderer;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

/**
 * Unit test for the {@link SudokuCellRenderer} class.
 * @author Daniel Dyer
 */
public class SudokuCellRendererTest
{
    private JTable table;


    @Test
    public void testRenderFixedCells()
    {
        SudokuTableModel model = new SudokuTableModel();
        // Set the 2 lefthand cells in the top row.
        model.setValueAt('1', 0, 0);
        model.setValueAt('2', 0, 1);

        JTable table = new JTable(model);
        TableCellRenderer renderer = new SudokuCellRenderer();

        JLabel cell1 = (JLabel) renderer.getTableCellRendererComponent(table, '1', false, false, 0, 0);
        assert cell1.getText().equals("1") : "Wrong text at cell 1: " + cell1.getText();
        assert cell1.getFont().isBold() : "Fixed cells should be rendered in bold.";
        JLabel cell2 = (JLabel) renderer.getTableCellRendererComponent(table, '2', false, false, 0, 1);
        assert cell2.getText().equals("2") : "Wrong text at cell 2: " + cell2.getText();
        assert cell2.getFont().isBold() : "Fixed cells should be rendered in bold.";
        // Check an empty cell, it should have no text.
        JLabel cell3 = (JLabel) renderer.getTableCellRendererComponent(table, null, false, false, 0, 2);
        assert cell3.getText().length() == 0 : "Wrong text at cell 3: " + cell3.getText();
    }


    @BeforeClass
    public void createTable()
    {
        SudokuTableModel model = new SudokuTableModel();
        model.setSudoku(SudokuTestUtils.createSudoku(new int[][]{{3, 2, 4, 8, 9, 1, 7, 5, 6},
                                                                 {6, 9, 7, 1, 5, 2, 8, 4, 3},
                                                                 {8, 1, 5, 7, 3, 6, 4, 2, 9},
                                                                 {5, 2, 6, 9, 7, 4, 3, 1, 8},
                                                                 {4, 9, 8, 1, 2, 5, 6, 7, 3},
                                                                 {8, 7, 1, 3, 4, 2, 9, 6, 5},
                                                                 {2, 6, 3, 4, 8, 7, 5, 9, 1},
                                                                 {1, 3, 5, 8, 4, 9, 2, 6, 7},
                                                                 {7, 4, 2, 1, 5, 3, 9, 8, 6}}));
        table = new JTable(model);
    }


    @Test
    public void testRenderCellWithNoConflicts()
    {
        TableCellRenderer renderer = new SudokuCellRenderer();
        JLabel cell = (JLabel) renderer.getTableCellRendererComponent(table, 1, false, false, 2, 1);
        assert cell.getText().equals("1") : "Wrong value at cell (2, 1): " + cell.getText();
        assert cell.getBackground() == Color.WHITE : "Cell without conflicts should be white.";
    }


    @Test
    public void testRenderCellWithOneConflict()
    {
        TableCellRenderer renderer = new SudokuCellRenderer();
        JLabel cell = (JLabel) renderer.getTableCellRendererComponent(table, 3, false, false, 7, 1);
        assert cell.getText().equals("3") : "Wrong value at cell (7, 1): " + cell.getText();
        assert cell.getBackground() == Color.YELLOW : "Cell with one conflict should be yellow.";
    }


    @Test
    public void testRenderCellWithTwoConflicts()
    {
        TableCellRenderer renderer = new SudokuCellRenderer();
        JLabel cell = (JLabel) renderer.getTableCellRendererComponent(table, 6, false, false, 8, 8);
        assert cell.getText().equals("6") : "Wrong value at cell (8, 8): " + cell.getText();
        assert cell.getBackground() == Color.ORANGE : "Cell with two conflicts should be orange.";
    }


    @Test
    public void testRenderCellWithThreeConflicts()
    {
        TableCellRenderer renderer = new SudokuCellRenderer();
        JLabel cell = (JLabel) renderer.getTableCellRendererComponent(table, 1, false, false, 1, 3);
        assert cell.getText().equals("1") : "Wrong value at cell (1, 3): " + cell.getText();
        assert cell.getBackground() == Color.RED : "Cell with two conflicts should be red.";
    }
}
