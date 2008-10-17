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

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import org.jfugue.Note;
import org.uncommons.watchmaker.framework.factories.AbstractCandidateFactory;

/**
 * Generates random melodies.  A melody is simply a list of JFugue {@link Note}
 * objects.
 * @author Daniel Dyer
 */
public class MelodyFactory extends AbstractCandidateFactory<List<Note>>
{
    private static final double ONE_EIGHTH = 0.125;

    private final int length;

    public MelodyFactory(int length)
    {
        this.length = length;
    }


    protected List<Note> generateRandomCandidate(Random rng)
    {
        List<Note> melody = new ArrayList<Note>(length);
        for (int i = 0; i < length; i++)
        {
            melody.add(createRandomNote(rng));
        }
        return melody;
    }


    private Note createRandomNote(Random rng)
    {
        // JFugue supports note values in the range 0-127.
        // We're ignoring the top 2 and bottom 2 octaves.
        byte noteValue = (byte) (rng.nextInt(83) + 24);
        double duration = ONE_EIGHTH * (rng.nextInt(7) + 1);
        return new Note(noteValue, duration);
    }
}
