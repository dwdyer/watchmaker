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

import org.testng.annotations.Test;

/**
 * Unit test for the table model used by the Sudoku example application.
 * @author Daniel Dyer
 */
public class SudokuTableModelTest
{
    private static final String[] TEST_PUZZLE = new String[]{"4.5...9.7",
                                                             ".2..9..6.",
                                                             "39.6.7.28",
                                                             "9..3.2..6",
                                                             "7..9.6..3",
                                                             "5..4.8..1",
                                                             "28.1.5.49",
                                                             ".7..3..8.",
                                                             "6.4...3.2"};

    /**
     * Makes sure that the table model class correctly converts to and from the
     * String patterns used by {@link SudokuFactory}.
     */
    @Test
    public void testPatternConversions()
    {
        SudokuTableModel model = new SudokuTableModel();
        model.setPattern(TEST_PUZZLE);
        // Change a cell, to ensure we don't get exactly the same pattern back.
        model.setValueAt('1', 8, 7);
        String[] newPattern = model.getPattern();
        // Make sure the pattern is correct.
        for (int i = 0; i < 8; i++) // Check the first 8 rows, which haven't changed.
        {
            assert newPattern[i].equals(TEST_PUZZLE[i]) : "Row " + i + " incorrect: " + newPattern[i];
        }
        // Check modified row.
        assert newPattern[8].equals("6.4...312") : "Row 8 incorrect: " + newPattern[8]; 
    }
}
