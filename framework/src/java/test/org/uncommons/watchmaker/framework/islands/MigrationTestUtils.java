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
package org.uncommons.watchmaker.framework.islands;

import org.uncommons.watchmaker.framework.EvaluatedCandidate;
import java.util.List;
import java.util.ArrayList;

/**
 * Utility methods used by unit tests for migration strategies.
 * @author Daniel Dyer
 */
class MigrationTestUtils
{
    private MigrationTestUtils()
    {
        // Prevents instantiation.
    }


    public static <T> List<EvaluatedCandidate<T>> createTestPopulation(T... members)
    {
        List<EvaluatedCandidate<T>> population = new ArrayList<EvaluatedCandidate<T>>(members.length);
        for (T member : members)
        {
            population.add(new EvaluatedCandidate<T>(member, 0));
        }
        return population;
    }


    public static void testPopulationContents(List<EvaluatedCandidate<String>> actualPopulation,
                                              String... expectedPopulation)
    {
        assert actualPopulation.size() == expectedPopulation.length : "Wrong population size after migration.";
        for (int i = 0; i < actualPopulation.size(); i++)
        {
            assert actualPopulation.get(i).getCandidate().equals(expectedPopulation[i]) : "Wrong value at index " + i;
        }
    }

}
