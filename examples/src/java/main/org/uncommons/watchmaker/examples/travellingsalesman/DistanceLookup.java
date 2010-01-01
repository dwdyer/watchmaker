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

import java.util.List;

/**
 * Strategy interface for providing distances between cities in the
 * Travelling Salesman problem.
 * @author Daniel Dyer
 */
public interface DistanceLookup
{
    /**
     * @return The list of cities that this object knows about.
     */
    List<String> getKnownCities();

    /**
     * Looks-up the distance between two cities.
     * @param startingCity The city to start from.
     * @param destinationCity The city to end in.
     * @return The distance (in kilometres) between the two cities.
     */
    int getDistance(String startingCity, String destinationCity);
}
