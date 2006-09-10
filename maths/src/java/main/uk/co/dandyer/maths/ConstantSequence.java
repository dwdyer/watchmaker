// ============================================================================
//   Copyright 2006 Daniel W. Dyer
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
package uk.co.dandyer.maths;

/**
 * Convenience implementation of {@link uk.co.dandyer.maths.NumberSequence} that always
 * returns the same value.
 * @author Daniel Dyer
 */
public class ConstantSequence<T extends Number> implements NumberSequence<T>
{
    private final T constant;

    public ConstantSequence(T constant)
    {
        this.constant = constant;
    }

    public T nextValue()
    {
        return constant;
    }
}
