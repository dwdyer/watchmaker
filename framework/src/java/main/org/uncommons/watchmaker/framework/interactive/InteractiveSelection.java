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

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import org.uncommons.watchmaker.framework.EvaluatedCandidate;
import org.uncommons.watchmaker.framework.SelectionStrategy;

/**
 * Special selection strategy used for interactive evolutionary algorithms.
 * @param <T> The type of evolved entity that can be selected by this class.
 * @author Daniel Dyer
 */
public class InteractiveSelection<T> implements SelectionStrategy<T>
{
    private final Console<?> console;
    private final Renderer<T, ?> renderer;


    /**
     * @param <R> The type of object that can be displayed by the specified
     * console.  The specified renderer must be able to map evolved entities
     * into objects of this type.
     * @param console The user interface (graphical, textual or other) used
     * to present a selection choice to the user.
     * @param renderer A renderer used to map the evolved entities to objects
     * that can be processed by the supplied console. 
     */
    public <R> InteractiveSelection(Console<R> console,
                                    Renderer<T, R> renderer)
    {
        this.console = console;
        this.renderer = renderer;
    }

    
    /**
     * @param console The user interface (graphical, textual or other) used
     * to present a selection choice to the user.
     */
    public InteractiveSelection(Console<T> console)
    {
        this(console, new NoOpRenderer<T>());
    }
    

    public <S extends T> List<S> select(List<EvaluatedCandidate<S>> population,
                                        boolean naturalFitnessScores,
                                        int selectionSize,
                                        Random rng)
    {
        List<S> selection = new ArrayList<S>(selectionSize);
        for (int i = 0; i < selectionSize; i++)
        {
            // Pick two candidates at random.
            EvaluatedCandidate<S> candidate1 = population.get(rng.nextInt(population.size()));
            EvaluatedCandidate<S> candidate2 = population.get(rng.nextInt(population.size()));

            // Get the user to pick which one should survive to reproduce.
            selection.add(select(candidate1.getCandidate(), candidate2.getCandidate()));
        }
        return selection;
    }


    @SuppressWarnings({"unchecked"})
    private <S extends T> S select(S candidate1, S candidate2)
    {
        Object rendereredCandidate1 = renderer.render(candidate1);
        Object rendereredCandidate2 = renderer.render(candidate2);
        try
        {
            Method consoleSelectMethod = console.getClass().getMethod("select", List.class);
            int selection = (Integer) consoleSelectMethod.invoke(console,
                                                                 Arrays.asList(rendereredCandidate1,
                                                                               rendereredCandidate2));
            return selection == 0 ? candidate1 : candidate2;
        }
        catch (IllegalAccessException ex)
        {
            // This cannot happen - the select method is public.
            throw new IllegalStateException(ex);
        }
        catch (NoSuchMethodException ex)
        {
            // This cannot happen - the select method is explicitly identified.
            throw new IllegalStateException(ex);
        }
        catch (InvocationTargetException ex)
        {
            // The select method is not declared to throw any exceptions so the
            // worst that can happen is a RuntimeException - we can re-throw that.
            throw (RuntimeException) ex.getCause();
        }
    }


    /**
     * Renderer that does nothing.  Used when the console already supports the
     * evolved type.
     */
    private static final class NoOpRenderer<T> implements Renderer<T, T>
    {
        public T render(T entity)
        {
            return entity;
        }
    }
}
