// ============================================================================
//   Copyright 2006, 2007, 2008 Daniel W. Dyer
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

import java.lang.reflect.Constructor;
import java.util.Random;
import org.uncommons.watchmaker.framework.Probability;

/**
 * Convenient base class for {@link Node}s that have two sub-trees.
 * @author Daniel Dyer
 */
abstract class BinaryNode implements Node
{
    private final Node left;
    private final Node right;
    private final char symbol;

    protected BinaryNode(Node left, Node right, char symbol)
    {
        this.left = left;
        this.right = right;
        this.symbol = symbol;
    }


    protected Node getLeft()
    {
        return left;
    }

    
    protected Node getRight()
    {
        return right;
    }


    /**
     * The depth of a binary node is the depth of its deepest sub-tree plus one.
     * @return The depth of the tree rooted at this node.
     */
    public int getDepth()
    {
        return 1 + Math.max(left.getDepth(), right.getDepth());
    }


    /**
     * {@inheritDoc} 
     */
    public String print()
    {
        StringBuilder buffer = new StringBuilder("(");
        buffer.append(left.print());
        buffer.append(' ');
        buffer.append(symbol);
        buffer.append(' ');
        buffer.append(right.print());
        buffer.append(')');
        return buffer.toString();
    }


    /**
     * {@inheritDoc}
     */
    public Node mutate(Random rng, Probability mutationProbability, TreeFactory treeFactory)
    {
        if (mutationProbability.nextEvent(rng))
        {
            return treeFactory.generateRandomCandidate(rng);
        }
        else
        {
            Node newLeft = left.mutate(rng, mutationProbability, treeFactory);
            Node newRight = right.mutate(rng, mutationProbability, treeFactory);
            if (newLeft != left && newRight != right)
            {
                Class<? extends BinaryNode> nodeClass = this.getClass();
                try
                {
                    Constructor<? extends BinaryNode> constructor = nodeClass.getConstructor(Node.class, Node.class);
                    return constructor.newInstance(newLeft, newRight);
                }
                catch (Exception ex)
                {
                    throw new IllegalStateException("Mutation failed.", ex);
                }
            }
            else
            {
                // Tree has not changed.
                return this;
            }
        }
    }
}
