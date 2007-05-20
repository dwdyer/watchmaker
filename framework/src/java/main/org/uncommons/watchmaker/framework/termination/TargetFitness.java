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
package org.uncommons.watchmaker.framework.termination;

import org.uncommons.watchmaker.framework.PopulationData;
import org.uncommons.watchmaker.framework.TerminationCondition;

/**
 * Terminates evolution once at least one candidate in the population has equalled
 * or bettered a pre-determined fitness score. 
 * @author Daniel Dyer
 */
public class TargetFitness implements TerminationCondition
{
    private final double targetFitness;
    private final boolean natural;

    public TargetFitness(double targetFitness, boolean natural)
    {
        this.targetFitness = targetFitness;
        this.natural = natural;
    }

    public boolean shouldTerminate(PopulationData<?> populationData)
    {
        if (natural)
        {
            // If we're using "natural" fitness scores, higher values are better.
            return populationData.getBestCandidateFitness() >= targetFitness;
        }
        else
        {
            // If we're using "non-natural" fitness scores, lower values are better.
            return populationData.getBestCandidateFitness() <= targetFitness;
        }
    }
}
