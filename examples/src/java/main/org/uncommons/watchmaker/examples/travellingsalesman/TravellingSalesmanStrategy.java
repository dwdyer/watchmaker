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

import java.util.Collection;
import java.util.List;

/**
 * Defines methods that must be implemented by classes that provide
 * solutions to the Travelling Salesman problem.
 * @author Daniel Dyer
 */
public interface TravellingSalesmanStrategy
{
    /**
     * @return A description of the strategy.
     */
    String getDescription();

    /**
     * Calculates the shortest round trip distance that visits each
     * of the specified cities once and returns to the starting point.
     * @param cities The destination that must each be visited for the route
     * to be valid.
     * @param progressListener A call-back for keeping track of the route-finding
     * algorithm's progress.
     * @return The shortest route found for the given list of destinations.
     */
    List<String> calculateShortestRoute(Collection<String> cities,
                                        ProgressListener progressListener);
}
