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
package org.uncommons.watchmaker.examples.strings;

import org.testng.annotations.Test;
import org.uncommons.watchmaker.framework.FitnessEvaluator;

/**
 * Unit test for fitness function used by the strings example.
 * @author Daniel Dyer
 */
public class StringEvaluatorTest
{
    @Test
    public void testIdentical()
    {
        String target = "abcdefgh";
        String candidate = "abcdefgh";
        FitnessEvaluator<String> evaluator = new StringEvaluator(target);
        int score = (int) evaluator.getFitness(candidate, null);
        assert score == 0 : "Fitness should be zero for identical strings, is " + score;
    }


    @Test
    public void testCompletelyDifferent()
    {
        String target = "abcdefgh";
        String candidate = "ijklmnop";
        FitnessEvaluator<String> evaluator = new StringEvaluator(target);
        int score = (int) evaluator.getFitness(candidate, null);
        assert score == target.length() : "Fitness should be " + target.length() + ", is " + score;
    }


    @Test
    public void testPartialSolution()
    {
        String target = "abcdefgh";
        String candidate = "abcdxxxx";
        FitnessEvaluator<String> evaluator = new StringEvaluator(target);
        int score = (int) evaluator.getFitness(candidate, null);
        assert score == 4 : "Fitness score should be 4, is " + score;
    }
}
