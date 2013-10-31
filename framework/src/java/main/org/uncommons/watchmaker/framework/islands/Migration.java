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

import java.util.List;
import java.util.Random;
import org.uncommons.watchmaker.framework.EvaluatedCandidate;

/**
 * Strategy interface for different ways of migrating individuals between islands
 * in {@link IslandEvolution}.
 * @param <T> The type of the individual members of the island populations.
 * @author Daniel Dyer
 */
public interface Migration<T>
{
    /**
     * @param islandPopulations The populations of each island in the system.
     * @param migrantCount The number of individuals to move from each island.
     * @param rng A source of randomness.
     */
    <S extends T> void migrate(List<List<EvaluatedCandidate<S>>> islandPopulations, int migrantCount, Random rng);
}
