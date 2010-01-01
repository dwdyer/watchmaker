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

import org.testng.annotations.Test;

/**
 * Simple unit test for the {@link Subtraction} node type.
 * @author Daniel Dyer
 */
public class SubtractionTest
{
    @Test
    public void testEvaluation()
    {
        Node node = new Subtraction(new Constant(7), new Constant(3));
        double value = node.evaluate(new double[0]);
        assert value == 4 : "Wrong result: " + value;
    }

    
    @Test
    public void testStringRepresentation()
    {
        Node node = new Subtraction(new Constant(7), new Constant(3));
        assert node.print().equals("(7.0 - 3.0)") : "Wrong string representation: " + node.print();
    }


    /**
     * If the arguments to the subtract function are both constants then the subtract node
     * should be replaced by a constant node containing the evaluation of this sum.
     */
    @Test
    public void testSimplifyConstants()
    {
        Node node = new Subtraction(new Constant(7), new Constant(5));
        Node simplified = node.simplify();
        assert simplified instanceof Constant
            : "Simplified node should be Constant, is " + simplified.getClass().getSimpleName();
        assert simplified.evaluate(BinaryNode.NO_ARGS) == node.evaluate(BinaryNode.NO_ARGS) : "Simplified answer differs.";
        assert simplified.evaluate(BinaryNode.NO_ARGS) == 2;
    }


    /**
     * If the arguments to the subtract function are identical, even if they are not constant,
     * then the answer will always be zero, so this node should be replaced by the constant zero.
     */
    @Test
    public void testSimplifyIdenticalArguments()
    {
        Node node = new Subtraction(new Parameter(0), new Parameter(0));
        Node simplified = node.simplify();
        assert simplified instanceof Constant
            : "Simplified node should be Constant, is " + simplified.getClass().getSimpleName();
        double[] args = new double[]{5}; // Provides a value for the parameter nodes.
        assert simplified.evaluate(args) == node.evaluate(args) : "Simplified answer differs.";
        assert simplified.evaluate(args) == 0;
    }


    /**
     * Test that simplification doesn't cause any problems when the expression is already as simple
     * as possible.
     */
    @Test
    public void testSimplifySimplest()
    {
        Node node = new Subtraction(new Parameter(0), new Constant(1));
        Node simplified = node.simplify();
        assert simplified == node : "Expression should not have been changed.";
    }


    /**
     * If the second argument is zero, the experession can be replaced by its lefthand side.
     */
    @Test
    public void testSimplifySubtractZero()
    {
        Node node = new Subtraction(new Parameter(0), new Constant(0));
        Node simplified = node.simplify();
        assert simplified instanceof Parameter
            : "Simplified node should be Parameter, is " + simplified.getClass().getSimpleName();
        double[] args = new double[]{5}; // Provides a value for the parameter nodes.
        assert simplified.evaluate(args) == node.evaluate(args) : "Simplified answer differs.";
        assert simplified.evaluate(args) == 5;
    }


    /**
     * But if the first argument is zero, the experession should not be simplified as it has
     * the effect of negating the second argument.
     */
    @Test
    public void testSimplifySubtractFromZero()
    {
        Node node = new Subtraction(new Constant(0), new Parameter(0));
        Node simplified = node.simplify();
        assert simplified == node : "Expression should not have been changed.";
    }


    /**
     * Make sure that sub-nodes are simplified.
     */
    @Test
    public void testSimplifySubNode()
    {
        Node node = new Subtraction(new Parameter(0),
                                    new Subtraction(new Constant(3), new Constant(2)));
        Node simplified = node.simplify();
        assert simplified instanceof Subtraction
            : "Simplified node should be Subtraction, is " + simplified.getClass().getSimpleName();
        double[] args = new double[]{5}; // Provides a value for the parameter nodes.
        assert simplified.evaluate(args) == node.evaluate(args) : "Simplified answer differs.";
        assert simplified.countNodes() < node.countNodes() : "Should be fewer nodes after simplification.";
    }
}
