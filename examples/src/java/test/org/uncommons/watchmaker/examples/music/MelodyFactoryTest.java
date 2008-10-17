// ============================================================================
//   Copyright 2006, 2007, 2008 Daniel W. Dyer
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
package org.uncommons.watchmaker.examples.music;

import java.util.List;
import org.jfugue.Note;
import org.testng.annotations.Test;
import org.uncommons.maths.random.MersenneTwisterRNG;
import org.uncommons.watchmaker.framework.CandidateFactory;

/**
 * Simple unit test for {@link MelodyFactory}.
 * @author Daniel Dyer
 */
public class MelodyFactoryTest
{
    /**
     * Generated melodies should have the expected number of notes.
     */
    @Test
    public void testLengths()
    {
        final int length = 8;

        CandidateFactory<List<Note>> factory = new MelodyFactory(length);
        List<List<Note>> population = factory.generateInitialPopulation(5, new MersenneTwisterRNG());
        assert population.size() == 5 : "Wrong population size: " + population.size();
        for (List<Note> melody : population)
        {
            assert melody.size() == length : "Wrong length: " + melody.size();
        }
    }
}
