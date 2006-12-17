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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import org.testng.annotations.Test;

/**
 * Unit test for the route evaluator used by both Travelling Salesman
 * implementations.  Checks to make sure that route distances are
 * calculated correctly.
 * @author Daniel Dyer
 */
public class RouteEvaluatorTest
{
    /**
     * Test different routes to make sure the distances are calculated correctly.
     */
    @Test
    public void testDistanceCalculations()
    {
        RouteEvaluator evaluator = new RouteEvaluator(new TestDistances());

        List<String> route1 = Arrays.asList("City4", "City1", "City3", "City2");
        // Expected distance is sum of distances between adjacent cities on route
        // including returning to the start city.
        int expectedDistance1 = 8 + 5 + 13 + 21;
        int actualDistance1 = (int) evaluator.getFitness(route1);
        assert actualDistance1 == expectedDistance1 : "Distance should be " + expectedDistance1 + ", was " + actualDistance1;

        List<String> route2 = Arrays.asList("City3", "City4", "City2", "City1");
        // Expected distance is sum of distances between adjacent cities on route
        // including returning to the start city.
        int expectedDistance2 = 34 + 21 + 3 + 5;
        int actualDistance2 = (int) evaluator.getFitness(route2);
        assert actualDistance2 == expectedDistance2 : "Distance should be " + expectedDistance2 + ", was " + actualDistance2;
    }


    /**
     * Make sure the route evaluator works for end cases.
     */
    @Test
    public void testSingleCityRoute()
    {
        RouteEvaluator evaluator = new RouteEvaluator(new TestDistances());

        List<String> route = Arrays.asList("City1");
        // Expected distance is sum of distances between adjacent cities on route
        // including returning to the start city.
        int distance = (int) evaluator.getFitness(route);
        assert distance == 0 : "Distance should be 0, was " + distance;        
    }

    
    /**
     * Test data.
     */
    private static final class TestDistances implements DistanceLookup
    {
        private static final Map<String, Map<String, Integer>> distances = new TreeMap<String, Map<String, Integer>>();
        static
        {
            Map<String, Integer> city1 = new TreeMap<String, Integer>();
            city1.put("City1", 0);
            city1.put("City2", 3);
            city1.put("City3", 5);
            city1.put("City4", 8);
            distances.put("City1", city1);

            Map<String, Integer> city2 = new TreeMap<String, Integer>();
            city2.put("City1", 3);
            city2.put("City2", 0);
            city2.put("City3", 13);
            city2.put("City4", 21);
            distances.put("City2", city2);

            Map<String, Integer> city3 = new TreeMap<String, Integer>();
            city3.put("City1", 5);
            city3.put("City2", 13);
            city3.put("City3", 0);
            city3.put("City4", 34);
            distances.put("City3", city3);

            Map<String, Integer> city4 = new TreeMap<String, Integer>();
            city4.put("City1", 8);
            city4.put("City2", 21);
            city4.put("City3", 34);
            city4.put("City4", 0);
            distances.put("City4", city4);
        }

        public List<String> getKnownCities()
        {
            return new ArrayList<String>(distances.keySet());
        }

        public int getDistance(String startingCity, String destinationCity)
        {
            return distances.get(startingCity).get(destinationCity);
        }
    }
}
