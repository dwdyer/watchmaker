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

/**
 * Simple multiplication operator {@link Node}.
 * @author Daniel Dyer
 */
public class Multiplication extends BinaryNode
{
    /**
     * Creates a node that evaluates to the sum of the values of its two
     * child nodes ({@literal left} and {@literal right}).
     * @param left The first operand.
     * @param right The second operand.
     */
    public Multiplication(Node left, Node right)
    {
        super(left, right, '*');
    }


    /**
     * Evaluates the two sub-trees and returns the product of these two values.
     * @param programParameters Program parameters (ignored by the multiplication operator
     * but may be used in evaluating the sub-trees).
     * @return The sum of the values of both child nodes.
     */
    public double evaluate(double[] programParameters)
    {
        return getLeft().evaluate(programParameters) * getRight().evaluate(programParameters);
    }
}
