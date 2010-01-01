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
 * Unit test for composite ID source.
 * @author Daniel Dyer
 */
public class CompositeIDSourceTest
{
    @Test
    public void testCombination()
    {
        int topPart = 15;
        IDSource<Long> idSource = new CompositeIDSource(topPart);
        long firstID = idSource.nextID();
        long secondID = idSource.nextID();
        assert 64424509440L == firstID : "First ID should be 2^36 - 2^32 (or 15 shifted left 32 places).";
        assert secondID == firstID + 1 : "Second ID should be first ID plus 1.";
    }
}
