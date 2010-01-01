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
 * A program {@link Node} that simply returns the value of one of the
 * program's parameters.
 * @author Daniel Dyer
 */
public class Parameter extends LeafNode
{
    private final int parameterIndex;

    /**
     * @param parameterIndex Which of the program's (zero-indexed) parameter
     * values should be returned upon evaluation of this node.
     */
    public Parameter(int parameterIndex)
    {
        this.parameterIndex = parameterIndex;
    }


    /**
     * Returns the value of one of the program parameters.
     * @param programParameters The parameters to this program.
     * @return The program parameter at the index condigured for this node.
     */
    public double evaluate(double[] programParameters)
    {
        if (parameterIndex >= programParameters.length)
        {
            throw new IllegalArgumentException("Invalid parameter index: " + parameterIndex);
        }
        return programParameters[parameterIndex];
    }


    /**
     * {@inheritDoc}
     */
    public String getLabel()
    {
        return "P" + parameterIndex;
    }


    /**
     * {@inheritDoc}
     * For a parameter node the String representation is simply "arg0", "arg1", etc.
     * depending on which program parameter it refers to.
     */
    public String print()
    {
        return "arg" + parameterIndex;
    }


    /**
     * Two parameters are equal if they evaluate to the same program argument
     * (i.e. they have the same parameter index).
     * @param other The object that this object is compared to.
     * @return True if the parameters are equivalent, false otherwise.
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
        return parameterIndex == ((Parameter) other).parameterIndex;
    }


    /**
     * Over-ridden to be consistent with {@link #equals(Object)}.
     * @return This object's hash code.
     */
    @Override
    public int hashCode()
    {
        return parameterIndex;
    }
}
