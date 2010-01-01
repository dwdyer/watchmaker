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
package org.uncommons.util.id;

import org.testng.annotations.Test;

/**
 * Unit test for prefixed ID source.
 * @author Daniel Dyer
 */
public class StringPrefixIDSourceTest
{
    @Test
    public void testPrefix()
    {
        IDSource<String> idSource = new StringPrefixIDSource("Watchmaker", new IntSequenceIDSource());
        String id1 = idSource.nextID();
        assert id1.equals("Watchmaker0") : "Wrong ID: " + id1;
        String id2 = idSource.nextID();
        assert id2.equals("Watchmaker1") : "Wrong ID: " + id2;
    }
}
