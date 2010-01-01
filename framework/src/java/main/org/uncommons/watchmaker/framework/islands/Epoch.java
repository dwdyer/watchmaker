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
import java.util.concurrent.Callable;
import org.uncommons.watchmaker.framework.EvaluatedCandidate;
import org.uncommons.watchmaker.framework.EvolutionEngine;
import org.uncommons.watchmaker.framework.TerminationCondition;

/**
 * @author Daniel Dyer
 */
class Epoch<T> implements Callable<List<EvaluatedCandidate<T>>>
{
    private final EvolutionEngine<T> island;
    private final int populationSize;
    private final int eliteCount;
    private final List<T> seedCandidates;
    private final TerminationCondition[] terminationConditions;

    Epoch(EvolutionEngine<T> island,
          int populationSize,
          int eliteCount,
          List<T> seedCandidates,
          TerminationCondition... terminationConditions)
    {
        this.island = island;
        this.populationSize = populationSize;
        this.eliteCount = eliteCount;
        this.seedCandidates = seedCandidates;
        this.terminationConditions = terminationConditions;
    }


    public List<EvaluatedCandidate<T>> call() throws Exception
    {
        return island.evolvePopulation(populationSize, eliteCount, seedCandidates, terminationConditions);
    }
}
