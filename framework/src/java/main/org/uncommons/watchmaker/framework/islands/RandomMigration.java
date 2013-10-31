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
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import org.uncommons.watchmaker.framework.EvaluatedCandidate;

/**
 * Migrates a fixed number of candidates away from each island.  Which individuals are migrated is determined
 * randomly and which islands they move to is also random.  This contrasts with the more ordered migration offered
 * by {@link RingMigration}.  If the migration count is greater than one, it is possible (probable) that migrants
 * from the same island will be moved to different islands.  It is also possible that when a migrant's destination is
 * randomly chosen, it gets sent back to the island that it came from.
 * @author Daniel Dyer
 */
public class RandomMigration implements Migration<Object>
{
    /**
     * Migrates a fixed number of candidates away from each island.  Which individuals are migrated is determined
     * randomly and which islands they move to is also random.  If the migration count is greater than one, it is
     * possible (probable) that migrants from the same island will be moved to different islands.  It is also possible
     * that when a migrant's destination is randomly chosen, it gets sent back to the island that it came from.
     * @param islandPopulations A list of the populations of each island.
     * @param migrantCount The number of (randomly selected) individuals to be moved on from
     * each island.
     * @param rng A source of randomness.
     * @param <S> The type of entity being evolved.
     */
    public <S extends Object> void migrate(List<List<EvaluatedCandidate<S>>> islandPopulations, int migrantCount, Random rng)
    {
        List<EvaluatedCandidate<S>> migrants = new ArrayList<EvaluatedCandidate<S>>(migrantCount * islandPopulations.size());
        for (List<EvaluatedCandidate<S>> island : islandPopulations)
        {
            Collections.shuffle(island, rng);
            for (int i = 0; i < migrantCount; i++)
            {
                migrants.add(island.remove(island.size() - 1));
            }
        }
        Collections.shuffle(migrants);
        Iterator<EvaluatedCandidate<S>> iterator = migrants.iterator();
        for (List<EvaluatedCandidate<S>> island : islandPopulations)
        {
            for (int i = 0; i < migrantCount; i++)
            {
                island.add(iterator.next());
            }
        }
    }
}
