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
package org.uncommons.watchmaker.examples.travellingsalesman;

import java.util.Arrays;
import java.util.List;
import org.testng.annotations.Test;

/**
 * Test for the brute force solution to the travelling salesman
 * problem.
 * @author Daniel Dyer
 */
public class BruteForceTravellingSalesmanTest
{
    /**
     * Make sure that the brute force implementation returns the correct result.
     */
    @Test
    public void testEvaluation()
    {
        DistanceLookup data = new TestDistances();
        TravellingSalesmanStrategy strategy = new BruteForceTravellingSalesman(data);
        List<String> cities = Arrays.asList("City1", "City2", "City3", "City4");        
        List<String> route = strategy.calculateShortestRoute(cities, null);
        assert route.size() == 4 : "Route is wrong length: " + route.size();
        assert route.contains("City1") : "Route does not contain City1.";
        assert route.contains("City2") : "Route does not contain City2.";
        assert route.contains("City3") : "Route does not contain City3.";
        assert route.contains("City4") : "Route does not contain City4.";
        double distance = 0;
        distance += data.getDistance(route.get(0), route.get(1));
        distance += data.getDistance(route.get(1), route.get(2));
        distance += data.getDistance(route.get(2), route.get(3));
        distance += data.getDistance(route.get(3), route.get(0));
        assert (long) distance == 47 : "Incorrect shortest route: " + distance;
    }
}
