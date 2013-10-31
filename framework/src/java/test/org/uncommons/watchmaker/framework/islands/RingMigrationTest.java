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

import java.util.Arrays;
import java.util.List;
import org.testng.Reporter;
import org.testng.annotations.Test;
import org.uncommons.watchmaker.framework.EvaluatedCandidate;
import org.uncommons.watchmaker.framework.FrameworkTestUtils;

/**
 * Unit test for the {@link RingMigration} class.
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
        Migration<Object> migration = new RingMigration();
        @SuppressWarnings("unchecked")
        List<List<EvaluatedCandidate<String>>> islandPopulations = Arrays.asList(MigrationTestUtils.createTestPopulation("A", "A", "A"),
                                                                                 MigrationTestUtils.createTestPopulation("B", "B", "B"),
                                                                                 MigrationTestUtils.createTestPopulation("C", "C", "C"));
        migration.migrate(islandPopulations, 0, FrameworkTestUtils.getRNG());
        assert islandPopulations.size() == 3 : "Wrong number of populations after migration.";
        MigrationTestUtils.testPopulationContents(islandPopulations.get(0), "A", "A", "A");
        MigrationTestUtils.testPopulationContents(islandPopulations.get(1), "B", "B", "B");
        MigrationTestUtils.testPopulationContents(islandPopulations.get(2), "C", "C", "C");
    }


    /**
     * Make sure that nothing strange happens when the entire island is migrated.
     */
    @Test
    public void testFullMigration()
    {
        Migration<Object> migration = new RingMigration();
        @SuppressWarnings("unchecked")
        List<List<EvaluatedCandidate<String>>> islandPopulations = Arrays.asList(MigrationTestUtils.createTestPopulation("A", "A", "A"),
                                                                                 MigrationTestUtils.createTestPopulation("B", "B", "B"),
                                                                                 MigrationTestUtils.createTestPopulation("C", "C", "C"));
        migration.migrate(islandPopulations, 3, FrameworkTestUtils.getRNG());
        assert islandPopulations.size() == 3: "Wrong number of populations after migration.";
        MigrationTestUtils.testPopulationContents(islandPopulations.get(0), "C", "C", "C");
        MigrationTestUtils.testPopulationContents(islandPopulations.get(1), "A", "A", "A");
        MigrationTestUtils.testPopulationContents(islandPopulations.get(2), "B", "B", "B");
    }
}
