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

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import org.uncommons.util.reflection.ReflectionUtils;
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
    private final int groupSize;
    private final int maxSelectionsPerGeneration;


    /**
     * @param <R> The type of object that can be displayed by the specified
     * console.  The specified renderer must be able to map evolved entities
     * into objects of this type.
     * @param console The user interface (graphical, textual or other) used
     * to present a selection choice to the user.
     * @param renderer A renderer used to map the evolved entities to objects
     * that can be processed by the supplied console.
     * @param groupSize The number of candidates to present to the user at
     * once (the user selects one from this number).
     * @param maxSelectionsPerGeneration The maximum number of selections that
     * the user will be asked to make for each generation of the evolutionary
     * algorithm.  If this number is lower than the required selection size,
     * the user's selections will be repeated to make up the shortfall.  The
     * purpose of this setting is two-fold.  Firstly it minimises user fatigue.
     * Secondly, it can be used to increase selection pressure.  In the extreme
     * case, a setting of 1 will ensure that members of the subsequent generation
     * are all descended from a single parent.
     */
    public <R> InteractiveSelection(Console<R> console,
                                    Renderer<T, R> renderer,
                                    int groupSize,
                                    int maxSelectionsPerGeneration)
    {
        if (groupSize < 2)
        {
            throw new IllegalArgumentException("Group size must be at least 2.");
        }
        if (maxSelectionsPerGeneration < 1)
        {
            throw new IllegalArgumentException("Maximum selections must be 1 or more.");
        }
        this.console = console;
        this.renderer = renderer;
        this.groupSize = groupSize;
        this.maxSelectionsPerGeneration = maxSelectionsPerGeneration;
    }

    
    /**
     * @param console The user interface (graphical, textual or other) used
     * to present a selection choice to the user.
     * @param groupSize The number of candidates to present to the user at
     * once (the user selects one from this number).
     * @param maxSelectionsPerGeneration The maximum number of selections that
     * the user will be asked to make for each generation of the evolutionary
     * algorithm.  If this number is lower than the required selection size,
     * the user's selections will be repeated to make up the shortfall.  The
     * purpose of this setting is two-fold.  Firstly it minimises user fatigue.
     * Secondly, it can be used to increase selection pressure.  In the extreme
     * case, a setting of 1 will ensure that members of the subsequent generation
     * are all descended from a single parent. 
     */
    public InteractiveSelection(Console<T> console,
                                int groupSize,
                                int maxSelectionsPerGeneration)
    {
        this(console, new NoOpRenderer<T>(), groupSize, maxSelectionsPerGeneration);
    }


    /**
     * {@inheritDoc}
     */
    public <S extends T> List<S> select(List<EvaluatedCandidate<S>> population,
                                        boolean naturalFitnessScores,
                                        int selectionSize,
                                        Random rng)
    {
        if (population.size() < groupSize)
        {
            throw new IllegalArgumentException("Population is too small for selection group size of " + groupSize);
        }

        int selectionCount = Math.min(selectionSize, maxSelectionsPerGeneration);
        List<S> selection = new ArrayList<S>(selectionCount);
        for (int i = 0; i < selectionCount; i++)
        {
            // Pick candidates at random (without replacement).
            List<S> group = new ArrayList<S>(groupSize);
            List<EvaluatedCandidate<S>> candidates = new ArrayList<EvaluatedCandidate<S>>(population);
            Collections.shuffle(candidates);
            for (int j = 0; j < groupSize; j++)
            {
                group.add(candidates.get(j).getCandidate());
            }
            // Get the user to pick which one should survive to reproduce.
            selection.add(select(group));
        }

        // If the selection is not big enough, extend it by randomly duplicating some
        // of the selections.
        if (selectionCount < selectionSize)
        {
            List<S> extendedSelection = new ArrayList<S>(selectionSize);
            extendedSelection.addAll(selection);
            for (int i = 0; i < selectionSize - selectionCount; i++)
            {
                extendedSelection.add(selection.get(selectionCount == 1 ? 0 : rng.nextInt(selectionCount)));
            }
            return extendedSelection;
        }
        else
        {
            return selection;
        }
    }


    private <S extends T> S select(List<S> candidates)
    {
        List<Object> renderedCandidates = new ArrayList<Object>(candidates.size());
        for (S candidate : candidates)
        {
            renderedCandidates.add(renderer.render(candidate));
        }
        Method consoleSelectMethod = ReflectionUtils.findKnownMethod(Console.class,
                                                                     "select",
                                                                     List.class);
        Integer selection = ReflectionUtils.invokeUnchecked(consoleSelectMethod,
                                                            console,
                                                            renderedCandidates);
        return candidates.get(selection);
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
