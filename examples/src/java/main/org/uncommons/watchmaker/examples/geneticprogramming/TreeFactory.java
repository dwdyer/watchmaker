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

import java.util.Random;
import org.uncommons.maths.random.Probability;
import org.uncommons.watchmaker.framework.factories.AbstractCandidateFactory;

/**
 * {@link org.uncommons.watchmaker.framework.CandidateFactory} for generating
 * trees of {@link Node}s for the genetic programming example application.
 * @author Daniel Dyer
 */
public class TreeFactory extends AbstractCandidateFactory<Node>
{
    // The number of program parameters that each program tree will be provided.
    private final int parameterCount;

    // The maximum depth of a program tree.  No function nodes will be created below
    // this depth (branches will be terminated with parameters or constants).
    private final int maximumDepth;

    // Probability that a created node is a function node rather
    // than a value node.
    private final Probability functionProbability;

    // Probability that a value (non-function) node is a parameter
    // node rather than a constant node.
    private final Probability parameterProbability;


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
                       Probability functionProbability,
                       Probability parameterProbability)
    {
        if (parameterCount < 0)
        {
            throw new IllegalArgumentException("Parameter count must be greater than or equal to 0.");
        }
        if (maxDepth < 1)
        {
            throw new IllegalArgumentException("Max depth must be at least 1.");
        }

        this.parameterCount = parameterCount;
        this.maximumDepth = maxDepth;
        this.functionProbability = functionProbability;
        this.parameterProbability = parameterProbability;
    }


    /**
     * {@inheritDoc}
     */
    public Node generateRandomCandidate(Random rng)
    {
        return makeNode(rng, maximumDepth);
    }


    /**
     * Recursively constructs a tree of Nodes, up to the specified maximum depth.
     * @param rng The RNG used to random create nodes.
     * @param maxDepth The maximum depth of the generated tree.
     * @return A tree of nodes.
     */
    private Node makeNode(Random rng, int maxDepth)
    {
        if (functionProbability.nextEvent(rng) && maxDepth > 1)
        {
            // Max depth for sub-trees is one less than max depth for this node.
            int depth = maxDepth - 1;
            switch (rng.nextInt(5))
            {
                case 0: return new Addition(makeNode(rng, depth), makeNode(rng, depth));
                case 1: return new Subtraction(makeNode(rng, depth), makeNode(rng, depth));
                case 2: return new Multiplication(makeNode(rng, depth), makeNode(rng, depth));
                case 3: return new IfThenElse(makeNode(rng, depth), makeNode(rng, depth), makeNode(rng, depth));
                default: return new IsGreater(makeNode(rng, depth), makeNode(rng, depth));
            }
        }
        else if (parameterProbability.nextEvent(rng))
        {
            return new Parameter(rng.nextInt(parameterCount));
        }
        else
        {
            return new Constant(rng.nextInt(11));
        }
    }
}
