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
import org.uncommons.watchmaker.framework.selection.TruncationSelection;

/**
 * Unit test for the evolutionary approach to the travelling salesman problem.
 * This test does not check that the correct result is returned (we would need
 * to run the full evolutionary algorithm for that, and even then optimality
 * is not guaranteed).  This just ensures that nothing invalid happens and
 * provides coverage for the classes involved.
 * @author Daniel Dyer
 */
public class EvolutionaryTravellingSalesmanTest
{
    private final DistanceLookup data = new TestDistances();

    /**
     * Ensure that the algorithm behaves when configured to use mutation.
     */
    @Test
    public void testWithMutation()
    {
        TravellingSalesmanStrategy strategy = new EvolutionaryTravellingSalesman(data,
                                                                                 new TruncationSelection(0.5),
                                                                                 10, // Small population.
                                                                                 0, // No elitism.
                                                                                 3, // Only a few generations.
                                                                                 false, // Cross-over.
                                                                                 true); // Mutation.
        List<String> cities = Arrays.asList("City1", "City2", "City3", "City4");
        List<String> route = strategy.calculateShortestRoute(cities, null);
        assert route.size() == 4 : "Route is wrong length: " + route.size();
        assert route.contains("City1") : "Route does not contain City1.";
        assert route.contains("City2") : "Route does not contain City2.";
        assert route.contains("City3") : "Route does not contain City3.";
        assert route.contains("City4") : "Route does not contain City4.";
    }


    /**
     * Ensure that the algorithm behaves when configured to use cross-over.
     */
    @Test
    public void testWithCrossover()
    {
        TravellingSalesmanStrategy strategy = new EvolutionaryTravellingSalesman(data,
                                                                                 new TruncationSelection(0.5),
                                                                                 10, // Small population.
                                                                                 0, // No elitism.
                                                                                 3, // Only a few generations.
                                                                                 true, // Cross-over
                                                                                 false); // Mutation.
        List<String> cities = Arrays.asList("City1", "City2", "City3", "City4");
        List<String> route = strategy.calculateShortestRoute(cities, null);
        assert route.size() == 4 : "Route is wrong length: " + route.size();
        assert route.contains("City1") : "Route does not contain City1.";
        assert route.contains("City2") : "Route does not contain City2.";
        assert route.contains("City3") : "Route does not contain City3.";
        assert route.contains("City4") : "Route does not contain City4.";
    }


    /**
     * The strategy must ensure that at least one of cross-over or mutation is chosen.
     */
    @Test(expectedExceptions = IllegalArgumentException.class)
    public void testNoEvolution()
    {
        new EvolutionaryTravellingSalesman(data,
                                           new TruncationSelection(0.5),
                                           10,
                                           0,
                                           3,
                                           false,
                                           false); // Should throw an IllegalArgumentException as no operators are enabled.
    }
}
