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

import java.text.DecimalFormat;

/**
 * A program node that evaluates to a constant value. 
 * @author Daniel Dyer
 */
public class Constant extends LeafNode
{
    private static final DecimalFormat NUMBER_FORMAT = new DecimalFormat("######0.##");

    private final double constant;
    private final String label;


    /**
     * Creates a constant-valued node. 
     * @param constant The value that this node will always evaluate to.
     */
    public Constant(double constant)
    {
        this.constant = constant;
        this.label = NUMBER_FORMAT.format(constant);
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
     * {@inheritDoc}
     */
    public String getLabel()
    {
        return label;
    }


    /**
     * @return The String representation of this constant.
     */
    public String print()
    {
        return String.valueOf(constant);
    }


    /**
     * Two constants are equal if they have the same numeric value.
     * @param other The object that this object is compared to.
     * @return True if the constants are equivalent, false otherwise.
     */
    @Override
    public boolean equals(Object other)
    {
        if (this == other)
        {
            return true;
        }
        if (other == null || getClass() != other.getClass())
        {
            return false;
        }
        return Double.compare(((Constant) other).constant, constant) == 0;
    }


    /**
     * Over-ridden to be consistent with {@link #equals(Object)}.
     * @return This object's hash code.
     */
    @Override
    public int hashCode()
    {
        long temp = constant != +0.0d ? Double.doubleToLongBits(constant) : 0L;
        return (int) (temp ^ (temp >>> 32));
    }
}
