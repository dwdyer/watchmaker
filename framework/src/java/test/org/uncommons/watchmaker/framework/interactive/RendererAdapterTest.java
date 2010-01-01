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
package org.uncommons.watchmaker.framework.interactive;

import java.util.Date;
import org.testng.annotations.Test;

/**
 * Unit test to ensure that renderer chaining works correctly.
 * @author Daniel Dyer
 */
public class RendererAdapterTest
{
    @Test
    public void testChaining()
    {
        Renderer<Long, Date> longToDate = new TimestampToDateRenderer();
        Renderer<Date, String> dateToString = new DateToStringRenderer();

        long currentTime = System.currentTimeMillis();
        Date date = longToDate.render(currentTime);
        String expectedOutput = dateToString.render(date);

        Renderer<Long, String> longToString = new RendererAdapter<Long, String>(longToDate,
                                                                                dateToString);
        String actualOutput = longToString.render(currentTime);
        assert actualOutput.equals(expectedOutput) : "Actual/expected output mismatch: " + actualOutput;
    }


    /**
     * Example renderer for converting a number of milliseconds since 00:00 on 1st
     * January 1970 into a Java {@link Date} object.
     */
    private static final class TimestampToDateRenderer implements Renderer<Long, Date>
    {
        public Date render(Long timestamp)
        {
            return new Date(timestamp);
        }
    }


    /**
     * Example renderer for converting a Java {@link Date} object into a date String.
     */
    private static final class DateToStringRenderer implements Renderer<Date, String>
    {
        public String render(Date date)
        {
            return date.toString();
        }
    }
}
