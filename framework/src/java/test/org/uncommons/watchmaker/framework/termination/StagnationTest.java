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
package org.uncommons.watchmaker.framework.termination;

import com.google.common.collect.ImmutableList;
import org.testng.annotations.Test;
import org.uncommons.watchmaker.framework.EvaluatedCandidate;
import org.uncommons.watchmaker.framework.PopulationData;
import org.uncommons.watchmaker.framework.TerminationCondition;

/**
 * Unit test for the {@link Stagnation} termination condition.
 * <p/>
 * @author Daniel Dyer
 */
public class StagnationTest
{
    @Test
    public void testFittestCandidateStagnation()
    {
        TerminationCondition stagnation = new Stagnation(2, true);
        ImmutableList<EvaluatedCandidate<Object>> evaluatedPopulation =
            ImmutableList.of(new EvaluatedCandidate<Object>(new Object(), 2));
        PopulationData<Object> data = new PopulationData<Object>(evaluatedPopulation, 1, 0.1,
            true, 10, 0, 0, 1);
        assert !stagnation.shouldTerminate(data):
            "Stagnation should not be triggered for at least 2 more generations.";
        // Best doesn't improve even though mean does.
        evaluatedPopulation =
            ImmutableList.of(new EvaluatedCandidate<Object>(new Object(), 1.8));
        data = new PopulationData<Object>(evaluatedPopulation, 1.2, 0.1, true, 10, 0, 1, 2);
        assert !stagnation.shouldTerminate(data):
            "Stagnation should not be triggered for at least 1 more generation.";
        // Best doesn't improve even though mean does.
        evaluatedPopulation =
            ImmutableList.of(new EvaluatedCandidate<Object>(new Object(), 2));
        data = new PopulationData<Object>(evaluatedPopulation, 1.5, 0.1, true, 10, 0, 2, 3);
        assert stagnation.shouldTerminate(data):
            "Stagnation should be triggered after 2 generations without improvement.";
    }


    @Test
    public void testFittestCandidateStagnationNonNatural()
    {
        TerminationCondition stagnation = new Stagnation(2, false);
        ImmutableList<EvaluatedCandidate<Object>> evaluatedPopulation =
            ImmutableList.of(new EvaluatedCandidate<Object>(new Object(), 2));
        PopulationData<Object> data = new PopulationData<Object>(evaluatedPopulation, 1.5,
            0.1, true, 10, 0, 0, 1);
        assert !stagnation.shouldTerminate(data):
            "Stagnation should not be triggered for at least 2 more generations.";
        // Best doesn't improve even though mean does.
        evaluatedPopulation =
            ImmutableList.of(new EvaluatedCandidate<Object>(new Object(), 2.2));
        data = new PopulationData<Object>(evaluatedPopulation, 1.2, 0.1, true, 10, 0, 1, 2);
        assert !stagnation.shouldTerminate(data):
            "Stagnation should not be triggered for at least 1 more generation.";
        // Best doesn't improve even though mean does.
        evaluatedPopulation =
            ImmutableList.of(new EvaluatedCandidate<Object>(new Object(), 2));
        data = new PopulationData<Object>(evaluatedPopulation, 1, 0.1, true, 10, 0, 2, 3);
        assert stagnation.shouldTerminate(data):
            "Stagnation should be triggered after 2 generations without improvement.";
    }


    @Test
    public void testPopulationMeanStagnation()
    {
        TerminationCondition stagnation = new Stagnation(2, true, true);
        ImmutableList<EvaluatedCandidate<Object>> evaluatedPopulation =
            ImmutableList.of(new EvaluatedCandidate<Object>(new Object(), 2));
        PopulationData<Object> data = new PopulationData<Object>(evaluatedPopulation, 1, 0.1,
            true, 10, 0, 0, 1);
        assert !stagnation.shouldTerminate(data):
            "Stagnation should not be triggered for at least 2 more generations.";
        // Best doesn't improve but mean does.
        data = new PopulationData<Object>(evaluatedPopulation, 1.2, 0.1, true, 10, 0, 1, 2);
        assert !stagnation.shouldTerminate(data):
            "Stagnation should not be triggered for at least 2 more generations.";
        // Best doesn't improve but mean does.
        data = new PopulationData<Object>(evaluatedPopulation, 1.5, 0.1, true, 10, 0, 2, 3);
        // Best has stagnated but mean hasn't so shouldn't terminate.
        assert !stagnation.shouldTerminate(data):
            "Stagnation should not be triggered for at least 2 more generations.";
        // Now we let the mean stagnate...and let the best candidate get fitter...
        evaluatedPopulation = ImmutableList.of(new EvaluatedCandidate<Object>(new Object(), 2.1));
        data = new PopulationData<Object>(evaluatedPopulation, 1.5, 0.1, true, 10, 0, 3, 4);
        assert !stagnation.shouldTerminate(data):
            "Stagnation should not be triggered for at least 1 more generation.";
        evaluatedPopulation = ImmutableList.of(new EvaluatedCandidate<Object>(new Object(), 2.2));
        data = new PopulationData<Object>(evaluatedPopulation, 1.5, 0.1, true, 10, 0, 4, 4);
        assert stagnation.shouldTerminate(data):
            "Stagnation should be triggered after 2 generations without improvement.";
    }


    @Test
    public void testPopulationMeanStagnationNonNatural()
    {
        TerminationCondition stagnation = new Stagnation(2, false, true);
        ImmutableList<EvaluatedCandidate<Object>> evaluatedPopulation =
            ImmutableList.of(new EvaluatedCandidate<Object>(new Object(), 2));
        PopulationData<Object> data = new PopulationData<Object>(evaluatedPopulation, 1.5,
            0.1, true, 10, 0, 0, 1);
        assert !stagnation.shouldTerminate(data):
            "Stagnation should not be triggered for at least 2 more generations.";
        // Best doesn't improve but mean does.
        data = new PopulationData<Object>(evaluatedPopulation, 1.2, 0.1, true, 10, 0, 1, 2);
        assert !stagnation.shouldTerminate(data):
            "Stagnation should not be triggered for at least 2 more generations.";
        // Best doesn't improve but mean does.
        data = new PopulationData<Object>(evaluatedPopulation, 1, 0.1, true, 10, 0, 2, 3);
        // Best has stagnated but mean hasn't so shouldn't terminate.
        assert !stagnation.shouldTerminate(data):
            "Stagnation should not be triggered for at least 2 more generations.";
        // Now we let the mean stagnate...and let the best candidate get fitter...
        evaluatedPopulation = ImmutableList.of(new EvaluatedCandidate<Object>(new Object(), 2.1));
        data = new PopulationData<Object>(evaluatedPopulation, 1.6, 0.1, true, 10, 0, 3, 4);
        assert !stagnation.shouldTerminate(data):
            "Stagnation should not be triggered for at least 1 more generation.";
        evaluatedPopulation = ImmutableList.of(new EvaluatedCandidate<Object>(new Object(), 2.2));
        data = new PopulationData<Object>(evaluatedPopulation, 1.5, 0.1, true, 10, 0, 4, 4);
        assert stagnation.shouldTerminate(data):
            "Stagnation should be triggered after 2 generations without improvement.";
    }
}
