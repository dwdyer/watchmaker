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
import org.uncommons.util.reflection.ReflectionUtils;
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


    public int countNodes()
    {
        return 1 + left.countNodes() + right.countNodes();
    }


    public Node getNode(int index)
    {
        if (index == 0)
        {
            return this;
        }
        int leftNodes = left.countNodes();
        if (index <= leftNodes)
        {
            return left.getNode(index - 1);
        }
        else
        {
            return right.getNode(index - leftNodes - 1);
        }
    }


    public Node replaceNode(int index, Node newNode)
    {
        if (index == 0)
        {
            return newNode;
        }

        int leftNodes = left.countNodes();
        Constructor<? extends BinaryNode> constructor = ReflectionUtils.findKnownConstructor(this.getClass(),
                                                                                             Node.class,
                                                                                             Node.class);
        if (index <= leftNodes)
        {
            return ReflectionUtils.invokeUnchecked(constructor,
                                                   left.replaceNode(index - 1, newNode),
                                                   right);
        }
        else
        {
            return ReflectionUtils.invokeUnchecked(constructor,
                                                   left,
                                                   right.replaceNode(index - leftNodes - 1, newNode));
        }
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
                Constructor<? extends BinaryNode> constructor = ReflectionUtils.findKnownConstructor(this.getClass(),
                                                                                                     Node.class,
                                                                                                     Node.class);
                return ReflectionUtils.invokeUnchecked(constructor, newLeft, newRight);
            }
            else
            {
                // Tree has not changed.
                return this;
            }
        }
    }


    @Override
    public String toString()
    {
        return print();
    }
}
