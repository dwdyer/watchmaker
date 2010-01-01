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

/**
 * Simple conditional program {@link Node}.
 * @author Daniel Dyer
 */
public class IfThenElse implements Node
{
    private final Node condition;
    private final Node then;
    private final Node otherwise;

    /**
     * @param condition If this node evaluates to a value greater than zero then
     * the value of the {@literal then} node is returned.  Otherwise the value of
     * the {@literal otherwise} node is returned.
     * @param then This node is evaluated if the {@literal condition} node has a
     * value greater than zero.
     * @param otherwise This node is evaluated if the {@literal condition} node has a
     * value less than or equal to zero.
     */
    public IfThenElse(Node condition, Node then, Node otherwise)
    {
        this.condition = condition;
        this.then = then;
        this.otherwise = otherwise;
    }


    /**
     * {@inheritDoc}
     */
    public String getLabel()
    {
        return "if";
    }


    /**
     * The arity of a ternary node is three.
     * @return 3
     */
    public int getArity()
    {
        return 3;
    }


    /**
     * {@inheritDoc}
     */
    public int getDepth()
    {
        return 1 + Math.max(condition.getDepth(), Math.max(then.getDepth(), otherwise.getDepth()));
    }


    /**
     * {@inheritDoc}
     */
    public int getWidth()
    {
        return condition.getWidth() + then.getWidth() + otherwise.getWidth();
    }


    /**
     * {@inheritDoc}
     */
    public int countNodes()
    {
        return 1 + condition.countNodes() + then.countNodes() + otherwise.countNodes();
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
        int conditionNodes = condition.countNodes();
        if (index <= conditionNodes)
        {
            return condition.getNode(index - 1);
        }
        else
        {
            int thenNodes = then.countNodes();
            if (index <= conditionNodes + thenNodes)
            {
                return then.getNode(index - conditionNodes - 1);
            }
            else
            {
                return otherwise.getNode(index - conditionNodes - thenNodes - 1);
            }
        }
    }


    /**
     * {@inheritDoc}
     */
    public Node getChild(int index)
    {
        switch (index)
        {
            case 0: return condition;
            case 1: return then;
            case 2 : return otherwise;
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

        int conditionNodes = condition.countNodes();
        if (index <= conditionNodes)
        {
            return new IfThenElse(condition.replaceNode(index - 1, newNode), then, otherwise);
        }
        else
        {
            int thenNodes = then.countNodes();
            if (index <= conditionNodes + thenNodes)
            {
                return new IfThenElse(condition, then.replaceNode(index - conditionNodes - 1, newNode), otherwise);
            }
            else
            {
                return new IfThenElse(condition,
                                      then,
                                      otherwise.replaceNode(index - conditionNodes - thenNodes - 1, newNode));
            }
        }
    }



    /**
     * {@inheritDoc}
     * Operates on three other nodes.  The first is an expression to evaluate.
     * Which of the other two nodes is evaluated and returned depends on whether
     * this node evaluates to greater than zero or not.
     * @param programParameters Program parameters (ignored by the conditional operator
     * but may be used in evaluating child nodes).
     */
    public double evaluate(double[] programParameters)
    {
        return condition.evaluate(programParameters) > 0 // If...
               ? then.evaluate(programParameters)   // Then...
               : otherwise.evaluate(programParameters);  // Else...
    }

    
    /**
     * {@inheritDoc}
     */
    public String print()
    {
        return "(" + condition.print() + " ? " + then.print() + " : " + otherwise.print() + ")";
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
            Node newCondition = condition.mutate(rng, mutationProbability, treeFactory);
            Node newThen = then.mutate(rng, mutationProbability, treeFactory);
            Node newOtherwise = otherwise.mutate(rng, mutationProbability, treeFactory);
            if (newCondition != condition || newThen != then || newOtherwise != otherwise)
            {
                return new IfThenElse(newCondition, newThen, newOtherwise);
            }
            else
            {
                // Tree has not changed.
                return this;
            }
        }

    }


    /**
     * {@inheritDoc}
     */
    @Override
    public String toString()
    {
        return print();
    }


    /**
     * {@inheritDoc}
     */
    public Node simplify()
    {
        Node simplifiedCondition = condition.simplify();

        // If the condition is constant then the expression can be replaced by the branch that
        // always gets evaluated.
        if (simplifiedCondition instanceof Constant)
        {
            return simplifiedCondition.evaluate(null) > 0 ? then.simplify() : otherwise.simplify();
        }
        else
        {
            Node simplifiedThen = then.simplify();
            Node simplifiedOtherwise = otherwise.simplify();
            // If both branches are identical, the condition is irrelevant.
            if (simplifiedThen.equals(simplifiedOtherwise))
            {
                return simplifiedThen;
            }
            // Only create a new node if something has actually changed, otherwise return the existing node.
            if (simplifiedCondition != condition || simplifiedThen != then || simplifiedOtherwise != otherwise)
            {
                return new IfThenElse(simplifiedCondition, simplifiedThen, simplifiedOtherwise);
            }
            else
            {
                return this;
            }
        }
    }
}
