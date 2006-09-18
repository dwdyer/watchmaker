// ============================================================================
//   Copyright 2006 Daniel W. Dyer
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
package org.uncommons.watchmaker.examples.travellingsalesman;

import java.util.List;
import java.util.Map;
import org.uncommons.watchmaker.framework.FitnessEvaluator;

/**
 * The fitness score of a route is the total distance (in km).
 * @author Daniel Dyer
 */
public class RouteEvaluator implements FitnessEvaluator<List<String>>
{
    private final Map<String, Map<String, Integer>> distances;

    public RouteEvaluator(Map<String, Map<String, Integer>> distances)
    {
        this.distances = distances;
    }

    public double getFitness(List<String> candidate)
    {
        int totalDistance = 0;
        for (int i = 0; i < candidate.size(); i++)
        {
            int nextIndex = i < candidate.size() - 1 ? i + 1 : 0;
            totalDistance += distances.get(candidate.get(i)).get(candidate.get(nextIndex));
        }
        return totalDistance;
    }

    public boolean isFitnessNormalised()
    {
        return false;
    }
}
