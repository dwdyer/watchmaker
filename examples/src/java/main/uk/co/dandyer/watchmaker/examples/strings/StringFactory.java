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
package uk.co.dandyer.watchmaker.examples.strings;

import uk.co.dandyer.watchmaker.framework.AbstractCandidateFactory;
import java.util.Random;

/**
 * @author Daniel Dyer
 */
public class StringFactory extends AbstractCandidateFactory<String>
{
    private static final int ALPHABET_SIZE = 26;

    private final int stringLength;

    public StringFactory(int stringLength)
    {
        this.stringLength = stringLength;
    }


    protected String generateRandomCandidate(Random rng)
    {
        char[] chars = new char[stringLength];
        for (int i = 0; i < stringLength; i++)
        {
            chars[i] = (char) (rng.nextInt(ALPHABET_SIZE) + 'a');
        }
        return new String(chars);
    }
}
