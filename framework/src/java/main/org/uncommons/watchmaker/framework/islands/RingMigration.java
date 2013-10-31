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

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import org.uncommons.watchmaker.framework.EvaluatedCandidate;

/**
 * Migrates a fixed number of individuals from each island to the adjacent island.
 * Operates as if the islands are arranged in a ring with migration occurring in a
 * clockwise direction.  The individuals to be migrated are chosen completely at random.
 * @author Daniel Dyer
 */
public class RingMigration implements Migration<Object>
{
    /**
     * Migrates a fixed number of individuals from each island to the adjacent island.
     * Operates as if the islands are arranged in a ring with migration occurring in a
     * clockwise direction.  The individuals to be migrated are chosen completely at random.
     * @param islandPopulations A list of the populations of each island.
     * @param migrantCount The number of (randomly selected) individuals to be moved on from
     * each island.
     * @param rng A source of randomness.
     * @param <S> The type of entity being evolved.
     */
    public <S extends Object> void migrate(List<List<EvaluatedCandidate<S>>> islandPopulations, int migrantCount, Random rng)
    {
        // The first batch of immigrants is from the last island to the first.
        List<EvaluatedCandidate<S>> lastIsland = islandPopulations.get(islandPopulations.size() - 1);
        Collections.shuffle(lastIsland, rng);
        List<EvaluatedCandidate<S>> migrants = lastIsland.subList(lastIsland.size() - migrantCount, lastIsland.size());

        for (List<EvaluatedCandidate<S>> island : islandPopulations)
        {
            // Migrants from the last island are immigrants for this island.
            List<EvaluatedCandidate<S>> immigrants = migrants;
            if (island != lastIsland) // We've already migrated individuals from the last island.
            {
                // Select the migrants that will move to the next island to make room for the immigrants here.
                // Randomise the population so that there is no bias concerning which individuals are migrated.
                Collections.shuffle(island, rng);
                migrants = new ArrayList<EvaluatedCandidate<S>>(island.subList(island.size() - migrantCount, island.size()));
            }
            // Copy the immigrants over the last members of the population (those that are themselves
            // migrating to the next island).
            for (int i = 0; i < immigrants.size(); i++)
            {
                island.set(island.size() - migrantCount + i, immigrants.get(i));
            }
        }
    }
}
