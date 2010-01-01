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

import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import org.testng.annotations.Test;

/**
 * Unit test for the distance look-ups for European cities that is used by
 * the Travelling Salesman applet.
 * @author Daniel Dyer
 */
public class EuropeanDistanceLookupTest
{
    /**
     * Check to make sure that each city's distance to itself is zero.
     */
    @Test
    public void testSelfDistances()
    {
        DistanceLookup europe = new EuropeanDistanceLookup();
        List<String> cities = europe.getKnownCities();
        for (String city : cities)
        {
            assert europe.getDistance(city, city) == 0 : city + " distance to self is non-zero.";
        }
    }


    /**
     * Check to make sure that for every pair of cities, the distance in one
     * direction is equal to the distance in the other direction.
     */
    @Test
    public void testReturnDistances()
    {
        DistanceLookup europe = new EuropeanDistanceLookup();
        Queue<String> cities = new LinkedList<String>(europe.getKnownCities());
        while (!cities.isEmpty())
        {
            String startCity = cities.remove();
            for(String city : cities)
            {
                int outDistance = europe.getDistance(startCity, city);
                int returnDistance = europe.getDistance(city, startCity);
                assert outDistance == returnDistance : "Return distance mismatch for " + startCity + " and " + city;
            }
        }
    }
}
