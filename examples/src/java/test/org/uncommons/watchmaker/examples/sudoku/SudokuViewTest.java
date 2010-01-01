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
import javax.swing.JFrame;
import org.fest.swing.core.BasicRobot;
import org.fest.swing.core.Robot;
import org.fest.swing.data.TableCell;
import org.fest.swing.fixture.FrameFixture;
import org.fest.swing.fixture.JTableCellFixture;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

/**
 * Unit test for the {@link SudokuView} class.
 * @author Daniel Dyer
 */
public class SudokuViewTest
{
    private static final String[] TEST_PUZZLE = {"4.5...9.7",
                                                 ".2..9..6.",
                                                 "39.6.7.28",
                                                 "9..3.2..6",
                                                 "7..9.6..3",
                                                 "5..4.8..1",
                                                 "28.1.5.49",
                                                 ".7..3..8.",
                                                 "6.4...3.2"};

    private Robot robot;

    @BeforeMethod(groups = "display-required")
    public void prepare()
    {
        robot = BasicRobot.robotWithNewAwtHierarchy();
    }


    @AfterMethod(groups = "display-required")
    public void cleanUp()
    {
        robot.cleanUp();
        robot = null;
    }

    
    @Test(groups = "display-required")
    public void testDisplayPuzzle()
    {
        SudokuView view = new SudokuView();
        JFrame frame = new JFrame();
        frame.add(view, BorderLayout.CENTER);
        FrameFixture frameFixture = new FrameFixture(robot, frame);
        frame.setSize(400, 400);
        frame.validate();

        frameFixture.show();
        view.setPuzzle(TEST_PUZZLE);

        // Check a non-empty cell.
        JTableCellFixture cell1 = frameFixture.table().cell(TableCell.row(0).column(0));
        cell1.requireEditable();
        cell1.requireValue("4");

        // And an empty cell.
        JTableCellFixture cell2 = frameFixture.table().cell(TableCell.row(0).column(1));
        cell2.requireEditable();
        cell2.requireValue("");
    }


    @Test(groups = "display-required")
    public void testDisplaySolution()
    {
        SudokuView view = new SudokuView();
        JFrame frame = new JFrame();
        frame.add(view, BorderLayout.CENTER);
        FrameFixture frameFixture = new FrameFixture(robot, frame);
        frame.setSize(400, 400);
        frame.validate();

        frameFixture.show();
        Sudoku sudoku = SudokuTestUtils.createSudoku(new int[][]
        {
            {1, 2, 8, 5, 4, 3, 9, 6, 7},
            {7, 6, 4, 9, 2, 8, 5, 1, 3},
            {3, 9, 5, 7, 6, 1, 2, 4, 8},
            {6, 1, 9, 4, 8, 5, 7, 3, 2},
            {5, 8, 3, 6, 7, 2, 1, 9, 4},
            {4, 7, 2, 3, 1, 9, 8, 5, 6},
            {8, 5, 1, 2, 3, 6, 4, 7, 9},
            {9, 4, 6, 8, 5, 7, 3, 2, 1},
            {2, 3, 7, 1, 9, 4, 6, 8, 5}
        });
        view.setSolution(sudoku);

        JTableCellFixture cell1 = frameFixture.table().cell(TableCell.row(0).column(0));
        cell1.requireNotEditable();
        cell1.requireValue("1");
    }

}
