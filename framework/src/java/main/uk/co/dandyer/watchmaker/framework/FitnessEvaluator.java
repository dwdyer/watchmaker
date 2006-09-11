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
package uk.co.dandyer.watchmaker.framework;

/**
 * Calculates the fitness score of a given candidate of the appropriate type.
 * @author Daniel Dyer
 */
public interface FitnessEvaluator<T>
{
    double getFitness(T candidate);

    /**
     * Returns true if a high fitness score means a fitter candidate
     * or false if a low fitness score means a fitter candidate.  An
     * example of a situation in which a low fitness score is good
     * is when the fitness corresponds to a cost and the algorithm
     * is attempting to minimise that cost.
     */
    boolean isHighFitnessBetter();
}