// ============================================================================
//   Copyright 2007 Daniel W. Dyer
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
package org.uncommons.watchmaker.framework.interactive;

import org.uncommons.watchmaker.framework.FitnessEvaluator;

/**
 * Fitness evaluation is not required for interactive selection.
 * @author Daniel Dyer
 */
public class NullFitnessEvaluator implements FitnessEvaluator<Object>
{
    public double getFitness(Object candidate)
    {
        return 0;
    }

    public boolean isNatural()
    {
        return true;
    }
}
