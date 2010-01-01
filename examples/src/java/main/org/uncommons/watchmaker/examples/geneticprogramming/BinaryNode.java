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
package org.uncommons.watchmaker.examples.geneticprogramming;

import java.lang.reflect.Constructor;
import java.util.Random;
import org.uncommons.maths.random.Probability;
import org.uncommons.util.reflection.ReflectionUtils;

/**
 * Convenient base class for {@link Node}s that have two sub-trees.
 * @author Daniel Dyer
 */
abstract class BinaryNode implements Node
{
    protected static final double[] NO_ARGS = new double[0];

    /** The first argument to the binary function. */
    protected final Node left;
    /** The second argument to the binary function. */
    protected final Node right;
    
    private final char symbol;


    /**
     * @param left The first argument to the binary function.
     * @param right The second argument to the binary function.
     * @param symbol A single character that indicates the type of function.
     */
    protected BinaryNode(Node left, Node right, char symbol)
    {
        this.left = left;
        this.right = right;
        this.symbol = symbol;
    }


    /**
     * {@inheritDoc}
     */
    public String getLabel()
    {
        return String.valueOf(symbol);
    }


    /**
     * The arity of a binary node is two.
     * @return 2
     */
    public int getArity()
    {
        return 2;
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
     * The width of a binary node is the sum of the widths of its two sub-trees.
     * @return The width of the tree rooted at this node.
     */
    public int getWidth()
    {
        return left.getWidth() + right.getWidth();
    }


    /**
     * {@inheritDoc}
     */
    public int countNodes()
    {
        return 1 + left.countNodes() + right.countNodes();
    }


    /**
     * {@inheritDoc}
     */
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


    /**
     * {@inheritDoc}
     */
    public Node getChild(int index)
    {
        switch (index)
        {
            case 0: return left;
            case 1: return right;
            default: throw new IndexOutOfBoundsException("Invalid child index: " + index);
        }
    }


    /**
     * {@inheritDoc}
     */
    public Node replaceNode(int index, Node newNode)
    {
        if (index == 0)
        {
            return newNode;
        }

        int leftNodes = left.countNodes();
        if (index <= leftNodes)
        {
            return newInstance(left.replaceNode(index - 1, newNode), right);
        }
        else
        {
            return newInstance(left, right.replaceNode(index - leftNodes - 1, newNode));
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
                return newInstance(newLeft, newRight);
            }
            else
            {
                // Tree has not changed.
                return this;
            }
        }
    }


    private Node newInstance(Node newLeft, Node newRight)
    {
        Constructor<? extends BinaryNode> constructor = ReflectionUtils.findKnownConstructor(this.getClass(),
                                                                                             Node.class,
                                                                                             Node.class);
        return ReflectionUtils.invokeUnchecked(constructor, newLeft, newRight);
    }


    @Override
    public String toString()
    {
        return print();
    }
}
