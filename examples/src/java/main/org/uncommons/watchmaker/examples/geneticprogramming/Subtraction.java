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

/**
 * Simple subtraction operator {@link Node}.
 * <p/>
 * @author Daniel Dyer
 */
public class Subtraction extends BinaryNode
{
    /**
     * Creates a node that evaluates the the value of {@literal left} minus the value of {@literal right}.
     * <p/>
     * @param left The first operand.
     * @param right The second operand.
     */
    public Subtraction(Node left, Node right)
    {
        super(left, right, '-');
    }


    /**
     * Evaluates the two sub-trees and returns the difference between these two values.
     * @param programParameters Program parameters (ignored by the subtraction operator
     * but may be used in evaluating the sub-trees).
     * @return The difference between the values of the two child nodes.
     */
    public double evaluate(double[] programParameters)
    {
        return left.evaluate(programParameters) - right.evaluate(programParameters);
    }


    public Node simplify()
    {
        Node simplifiedLeft = left.simplify();
        Node simplifiedRight = right.simplify();
        // If the two arguments are identical then the result will always be zero.
        if (simplifiedLeft.equals(simplifiedRight))
        {
            return new Constant(0);
        }
        // Subtracting zero is pointless, the expression can be reduced to its lefthand side.
        else if (simplifiedRight instanceof Constant && simplifiedRight.evaluate(NO_ARGS) == 0)
        {
            return simplifiedLeft;
        }
        // If the two arguments are constants, we can simplify by calculating the result, it won't
        // ever change.
        else if (simplifiedLeft instanceof Constant && simplifiedRight instanceof Constant)
        {
            return new Constant(simplifiedLeft.evaluate(NO_ARGS) - simplifiedRight.evaluate(NO_ARGS));
        }
        else if (simplifiedLeft != left || simplifiedRight != right)
        {
            return new Subtraction(simplifiedLeft, simplifiedRight);
        }
        else
        {
            return this;
        }
    }
}
