// ============================================================================
//   Copyright 2006-2009 Daniel W. Dyer
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
package org.uncommons.watchmaker.framework.islands;

import java.util.Arrays;
import java.util.List;
import org.testng.Reporter;
import org.testng.annotations.Test;
import org.uncommons.watchmaker.framework.FrameworkTestUtils;

/**
 * Unit test for the custom operator used by island model evolution.
 * @author Daniel Dyer
 */
public class RingMigrationTest
{
    /**
     * Make sure that nothing strange happens when there is no migration.
     */
    @Test
    public void testZeroMigration()
    {
        Migration migration = new RingMigration();
        List<List<String>> islandPopulations = Arrays.asList(Arrays.asList("A", "A", "A"),
                                                             Arrays.asList("B", "B", "B"),
                                                             Arrays.asList("C", "C", "C"));
        migration.migrate(islandPopulations, 0, FrameworkTestUtils.getRNG());
        assert islandPopulations.size() == 3 : "Wrong number of populations after migration.";
        testPopulationContents(islandPopulations.get(0), Arrays.asList("A", "A", "A"));
        testPopulationContents(islandPopulations.get(1), Arrays.asList("B", "B", "B"));
        testPopulationContents(islandPopulations.get(2), Arrays.asList("C", "C", "C"));
    }


    /**
     * Make sure that nothing strange happens when the entire island is migrated.
     */
    @Test
    public void testFullMigration()
    {
        Migration migration = new RingMigration();
        List<List<String>> islandPopulations = Arrays.asList(Arrays.asList("A", "A", "A"),
                                                             Arrays.asList("B", "B", "B"),
                                                             Arrays.asList("C", "C", "C"));
        migration.migrate(islandPopulations, 3, FrameworkTestUtils.getRNG());
        assert islandPopulations.size() == 3: "Wrong number of populations after migration.";
        Reporter.log(islandPopulations.toString());
        testPopulationContents(islandPopulations.get(0), Arrays.asList("C", "C", "C"));
        testPopulationContents(islandPopulations.get(1), Arrays.asList("A", "A", "A"));
        testPopulationContents(islandPopulations.get(2), Arrays.asList("B", "B", "B"));
    }



    private void testPopulationContents(List<String> actualPopulation, List<String> expectedPopulation)
    {
        assert actualPopulation.size() == expectedPopulation.size() : "Wrong population size after migration.";
        for (int i = 0; i < actualPopulation.size(); i++)
        {
            assert actualPopulation.get(i).equals(expectedPopulation.get(i)) : "Wrong value at index " + i;
        }
    }
}
