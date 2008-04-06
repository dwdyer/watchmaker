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

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import org.uncommons.watchmaker.framework.factories.AbstractCandidateFactory;

/**
 * {@link org.uncommons.watchmaker.framework.CandidateFactory} for generating
 * trees of {@link Node}s for the genetic programming example application.
 * @author Daniel Dyer
 */
public class TreeFactory extends AbstractCandidateFactory<Node>
{
    private static final List<Class<? extends Node>> NODE_TYPES = new ArrayList<Class<? extends Node>>(5);
    static
    {
        NODE_TYPES.add(Addition.class);
        NODE_TYPES.add(Subtraction.class);
        NODE_TYPES.add(Multiplication.class);
        NODE_TYPES.add(IfThenElse.class);
        NODE_TYPES.add(IsGreater.class);
    }

    // The number of program parameters that each program tree will be provided.
    private final int parameterCount;

    // The maximum depth of a program tree.  No function nodes will be created below
    // this depth (branches will be terminated with parameters or constants).
    private final int maxDepth;

    // Probability that a created node is a function node rather
    // than a value node.
    private final double functionProbability;

    // Probability that a value (non-function) node is a parameter
    // node rather than a constant node.
    private final double parameterProbability;


    /**
     * @param parameterCount The number of program parameters that each
     * generated program tree can will be provided when executed.
     * @param maxDepth The maximum depth of generated trees.
     * @param functionProbability The probability (between 0 and 1) that a
     * randomly-generated node will be a function node rather than a value
     * (parameter or constant) node.
     * @param parameterProbability The probability that a randomly-generated
     * non-function node will be a parameter node rather than a constant node.
     */
    public TreeFactory(int parameterCount,
                       int maxDepth,
                       double functionProbability,
                       double parameterProbability)
    {
        if (parameterCount < 0)
        {
            throw new IllegalArgumentException("Parameter count must be greater than or equal to 0.");
        }
        if (maxDepth < 1)
        {
            throw new IllegalArgumentException("Max depth must be at least 1.");
        }
        assertProbabilityInRange(functionProbability);
        assertProbabilityInRange(parameterProbability);
        
        this.parameterCount = parameterCount;
        this.maxDepth = maxDepth;
        this.functionProbability = functionProbability;
        this.parameterProbability = parameterProbability;
    }


    /**
     * Check that a probability is in the range 0..1 and throw an exception
     * if it is not.
     * @param p The probability to check.
     */
    private void assertProbabilityInRange(double p)
    {
        if (p < 0 || p > 1)
        {
            throw new IllegalArgumentException("Probability must be between 0 and 1.");
        }
    }


    /**
     * {@inheritDoc}
     */
    protected Node generateRandomCandidate(Random rng)
    {
        return makeNode(rng, maxDepth);
    }


    /**
     * Recursively constructs a tree of Nodes, up to the specified maximum depth.
     * @param rng The RNG used to random create nodes.
     * @param maxDepth The maximum depth of the generated tree.
     * @return A tree of nodes.
     */
    private Node makeNode(Random rng, int maxDepth)
    {
        if (rng.nextDouble() <= functionProbability && maxDepth > 1)
        {
            try
            {
                Class<? extends Node> type = NODE_TYPES.get(rng.nextInt(NODE_TYPES.size()));
                Node node = type.newInstance();
                List<Node> children = new ArrayList<Node>(node.getChildCount());
                for (int i = 0; i < node.getChildCount(); i++)
                {
                    // Recursively construct the nodes that this node will operate on.
                    children.add(makeNode(rng, maxDepth - 1));
                }
                node.setChildren(children);
                return node;
            }
            catch (IllegalAccessException ex)
            {
                throw new IllegalStateException(ex);
            }
            catch (InstantiationException ex)
            {
                throw new IllegalStateException(ex);
            }
        }
        else if (rng.nextDouble() <= parameterProbability)
        {
            return new Parameter(rng.nextInt(parameterCount));
        }
        else
        {
            return new Constant(rng.nextInt(11));
        }
    }
}
