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
package org.uncommons.watchmaker.examples.monalisa;

import org.uncommons.maths.number.NumberGenerator;

/**
 * NumberGenerator helper functions.
 * <p/>
 * @author Gili Tzabari
 */
public class NumberGenerators
{
    /**
     * Returns the multiplication of two NumberGenerators.
     * <p/>
     * @param first the first value generator
     * @param second the second value generator
     * @return the multiplication of first and second NumberGenerators
     */
    public static NumberGenerator<Double> multiplyDouble(final NumberGenerator<Double> first,
        final NumberGenerator<Double> second)
    {
        return new NumberGenerator<Double>()
        {
            @Override
            public Double nextValue()
            {
                return first.nextValue() * second.nextValue();
            }
        };
    }
}
