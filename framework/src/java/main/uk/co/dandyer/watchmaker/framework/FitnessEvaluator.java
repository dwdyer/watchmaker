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
    /**
     * Calculates a fitness score for the given candidate.  Whether
     * a higher score indicates a fitter candidate or not depends on
     * whether the fitness scores are normalised (@see #isFitnessNormalised).
     * @param candidate
     */
    double getFitness(T candidate);

    /**
     * <p>Specifies whether this evaluator generates normalised
     * (descending) fitness scores or not.</p>
     * <p>Normalised fitness scores are those in which the fittest
     * individual in a population has the highest fitness value.  In this
     * case the algorithm is attempting to maximise fitness scores.
     * In contrast, de-normalised fitness evaluation results in fitter
     * individuals being assigned lower scores than weaker individuals.
     * In this case the algorithm is attempting to minimise fitness scores.</p>
     * <p>An example of a situation in which a low fitness score is good
     * is when the fitness corresponds to a cost and the algorithm
     * is attempting to minimise that cost.</p>
     * @return True if a high fitness score means a fitter candidate
     * or false if a low fitness score means a fitter candidate.
     */
    boolean isFitnessNormalised();
}