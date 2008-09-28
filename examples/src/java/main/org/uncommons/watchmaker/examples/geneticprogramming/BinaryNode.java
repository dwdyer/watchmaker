package org.uncommons.watchmaker.examples.geneticprogramming;

import java.lang.reflect.Constructor;
import java.util.Random;

/**
 * Convenient base class for {@link Node}s that have two sub-trees.
 * @author Daniel Dyer
 */
abstract class BinaryNode implements Node
{
    protected final Node left;
    protected final Node right;
    private final char symbol;

    protected BinaryNode(Node left, Node right, char symbol)
    {
        this.left = left;
        this.right = right;
        this.symbol = symbol;
    }


    /**
     * The depth of a binary node is the depth of its deepest sub-tree plus one.
     * @return The depth of the tree rooted at this node.
     */
    public int getDepth()
    {
        return 1 + Math.max(left.getDepth(), right.getDepth());
    }


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


    public Node mutate(Random rng, double mutationProbability, TreeFactory treeFactory)
    {
        if (rng.nextDouble() < mutationProbability)
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
