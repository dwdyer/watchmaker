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
package org.uncommons.watchmaker.examples;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.Map;
import org.uncommons.util.reflection.ReflectionUtils;
import org.uncommons.watchmaker.examples.biomorphs.BiomorphApplet;
import org.uncommons.watchmaker.examples.bits.BitsExample;
import org.uncommons.watchmaker.examples.geneticprogramming.GeneticProgrammingExample;
import org.uncommons.watchmaker.examples.monalisa.MonaLisaApplet;
import org.uncommons.watchmaker.examples.strings.StringsExample;
import org.uncommons.watchmaker.examples.sudoku.SudokuApplet;
import org.uncommons.watchmaker.examples.travellingsalesman.TravellingSalesmanApplet;

/**
 * Launcher for Watchmaker example applications.
 * @author Daniel Dyer
 */
public class Launcher
{
    private static final Map<String, Class<?>> EXAMPLES = new LinkedHashMap<String, Class<?>>();
    static
    {
        EXAMPLES.put("biomorphs", BiomorphApplet.class);
        EXAMPLES.put("bits", BitsExample.class);
        EXAMPLES.put("gp", GeneticProgrammingExample.class);
        EXAMPLES.put("monalisa", MonaLisaApplet.class);
        EXAMPLES.put("salesman", TravellingSalesmanApplet.class);
        EXAMPLES.put("strings", StringsExample.class);
        EXAMPLES.put("sudoku", SudokuApplet.class);
    }


    private Launcher()
    {
        // Prevents instantiation of launcher class.
    }

    
    /**
     * Launch the specified example application from the command-line.
     * @param args First item is the name of the example to run.  Any subsequent arguments are passed
     * on to the specific example.
     */
    public static void main(String[] args)
    {
        Class<?> exampleClass = args.length > 0 ? EXAMPLES.get(args[0]) : null;
        if (exampleClass == null)
        {
            System.err.println("First argument must be the name of an example, i.e. one of "
                               + Arrays.toString(EXAMPLES.keySet().toArray()));
            System.exit(1);
        }

        // All args except the first one should be passed to the example application.
        String[] appArgs = new String[args.length - 1];
        System.arraycopy(args, 1, appArgs, 0, appArgs.length);

        // Invoke the main method for the selected example application.
        Method main = ReflectionUtils.findKnownMethod(exampleClass, "main", String[].class);
        ReflectionUtils.invokeUnchecked(main, exampleClass, new Object[]{appArgs});
    }
}
