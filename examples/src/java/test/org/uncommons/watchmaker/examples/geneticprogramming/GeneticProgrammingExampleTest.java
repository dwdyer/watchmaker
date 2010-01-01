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
package org.uncommons.watchmaker.examples.geneticprogramming;

import java.util.HashMap;
import java.util.Map;
import org.testng.annotations.Test;

/**
 * Test for the {@link GeneticProgrammingExample} example application.
 * @author Daniel Dyer
 */
public class GeneticProgrammingExampleTest
{
    @Test
    public void testApplication()
    {
        Map<double[], Double> testData = new HashMap<double[], Double>();
        testData.put(new double[]{26, 35}, 165.0d);
        testData.put(new double[]{8, 24}, 64.0d);
        testData.put(new double[]{20, 1}, 101.0d);
        testData.put(new double[]{33, 11}, 176.0d);
        testData.put(new double[]{37, 16}, 201.0d);

        Node evolvedProgram = GeneticProgrammingExample.evolveProgram(testData);

        // Check that evolved program works for test data.
        double result1 = evolvedProgram.evaluate(new double[]{8, 24});
        assert result1 == 64.0d : "Incorrect result: " + result1;        

        // Check that the evolved program generalises.
        double result2 = evolvedProgram.evaluate(new double[]{10, 7});
        assert result2 == 57.0d : "Incorrect result: " + result2;
        double result3 = evolvedProgram.evaluate(new double[]{13, 22});
        assert result3 == 87.0d : "Incorrect result: " + result3;
    }
}
