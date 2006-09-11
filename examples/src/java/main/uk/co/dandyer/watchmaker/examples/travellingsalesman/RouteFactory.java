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
package uk.co.dandyer.watchmaker.examples.travellingsalesman;

import uk.co.dandyer.watchmaker.framework.AbstractCandidateFactory;
import java.util.List;
import java.util.Random;
import java.util.ArrayList;
import java.util.Collections;

/**
 * A route is a list of cities (each city is represented as a String containing
 * its name) in the order that they must be visited.
 * @author Daniel Dyer
 */
public class RouteFactory extends AbstractCandidateFactory<List<String>>
{
    private final List<String> cities;

    public RouteFactory(List<String> cities)
    {
        this.cities = cities;
    }

    protected List<String> generateRandomCandidate(Random rng)
    {
        List<String> candidate = new ArrayList<String>(cities);
        Collections.shuffle(candidate, rng);
        return candidate;
    }
}
