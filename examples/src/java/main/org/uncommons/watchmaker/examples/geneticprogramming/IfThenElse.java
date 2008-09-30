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
import org.uncommons.watchmaker.framework.Probability;

/**
 * Simple conditional program {@link Node}.
 * @author Daniel Dyer
 */
public class IfThenElse implements Node
{
    private final Node condition;
    private final Node then;
    private final Node otherwise;

    public IfThenElse(Node condition, Node then, Node otherwise)
    {
        this.condition = condition;
        this.then = then;
        this.otherwise = otherwise;
    }


    public int getDepth()
    {
        return Math.max(condition.getDepth(), Math.max(then.getDepth(), otherwise.getDepth()));
    }


    /**
     * Operates on three other nodes.  The first is an expression to evaluate.
     * Which of the other two nodes is evaluated and returned depends on whether
     * this node evaluates to greater than zero or not.
     */
    public double evaluate(double[] programParameters)
    {
        return condition.evaluate(programParameters) > 0 // If...
               ? then.evaluate(programParameters)   // Then...
               : otherwise.evaluate(programParameters);  // Else...
    }

    
    public String print()
    {
        return "(" + condition.print() + " ? " + then.print() + " : " + otherwise.print() + ")";
    }


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
            if (newCondition != condition && newThen != then && newOtherwise != otherwise)
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
}
