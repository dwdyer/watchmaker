// ============================================================================
//   Copyright 2006, 2007 Daniel W. Dyer
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
package org.uncommons.watchmaker.examples.geneticprogramming;

import java.util.Random;

/**
 * Convenient base class for {@link Node}s that have no sub-trees.
 * @author Daniel Dyer
 */
abstract class LeafNode implements Node
{
    public int getDepth()
    {
        return 1;
    }


    public Node mutate(Random rng, double mutationProbability, TreeFactory treeFactory)
    {
        if (rng.nextDouble() < mutationProbability)
        {
            return treeFactory.generateRandomCandidate(rng);
        }
        else
        {
            // Node is unchanged.
            return this;
        }
    }
}
