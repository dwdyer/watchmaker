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
import org.uncommons.watchmaker.framework.FitnessEvaluator;

/**
 * Fitness evalator that measures the total distance of a route in the travelling salesman
 * problem.  The fitness score of a route is the total distance (in km).  A route
 * is represented as a list of cities in the order that they will be visited.
 * The last leg of the journey is from the last city in the list back to the
 * first.
 * @author Daniel Dyer
 */
public class RouteEvaluator implements FitnessEvaluator<List<String>>
{
    public double getFitness(List<String> candidate)
    {
        int totalDistance = 0;
        for (int i = 0; i < candidate.size(); i++)
        {
            int nextIndex = i < candidate.size() - 1 ? i + 1 : 0;
            totalDistance += Europe.getInstance().getDistance(candidate.get(i),
                                                              candidate.get(nextIndex));
        }
        return totalDistance;
    }


    /**
     * {@inheritDoc}
     * Returns false since shorter distances represent fitter candidates.
     * @return false
     */
    public boolean isNatural()
    {
        return false;
    }
}
