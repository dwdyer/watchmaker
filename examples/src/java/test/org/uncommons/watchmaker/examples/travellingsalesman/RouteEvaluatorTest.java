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
        int actualDistance1 = (int) evaluator.getFitness(route1, null);
        assert actualDistance1 == expectedDistance1 : "Distance should be " + expectedDistance1 + ", was " + actualDistance1;

        List<String> route2 = Arrays.asList("City3", "City4", "City2", "City1");
        // Expected distance is sum of distances between adjacent cities on route
        // including returning to the start city.
        int expectedDistance2 = 34 + 21 + 3 + 5;
        int actualDistance2 = (int) evaluator.getFitness(route2, null);
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
        int distance = (int) evaluator.getFitness(route, null);
        assert distance == 0 : "Distance should be 0, was " + distance;        
    }


}
