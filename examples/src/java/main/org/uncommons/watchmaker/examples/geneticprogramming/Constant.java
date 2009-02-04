// ============================================================================
//   Copyright 2006-2009 Daniel W. Dyer
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
 * A program node that evaluates to a constant value. 
 * @author Daniel Dyer
 */
public class Constant extends LeafNode
{
    private final double constant;

    public Constant(double constant)
    {
        this.constant = constant;
    }


    /**
     * @param programParameters The parameters passed to the program (ignored by this node).
     * @return The numeric value of this constant.
     */
    public double evaluate(double[] programParameters)
    {
        return constant;
    }


    /**
     * @return The String representation of this constant.
     */
    public String print()
    {
        return String.valueOf(constant);
    }
}
