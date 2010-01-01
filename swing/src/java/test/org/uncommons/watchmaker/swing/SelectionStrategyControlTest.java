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
package org.uncommons.watchmaker.swing;

import java.util.LinkedList;
import java.util.List;
import javax.swing.JComboBox;
import org.testng.annotations.Test;
import org.uncommons.watchmaker.framework.EvaluatedCandidate;
import org.uncommons.watchmaker.framework.SelectionStrategy;
import org.uncommons.watchmaker.framework.selection.RankSelection;
import org.uncommons.watchmaker.framework.selection.RouletteWheelSelection;
import org.uncommons.watchmaker.framework.selection.TruncationSelection;

/**
 * Unit test for the {@link SelectionStrategyControl}.
 * @author Daniel Dyer
 */
public class SelectionStrategyControlTest
{
    @Test
    public void testInitialisation()
    {
        SelectionStrategy<Object> rank = new RankSelection();
        SelectionStrategy<Object> roulette = new RouletteWheelSelection();
        List<SelectionStrategy<? super Object>> strategies = new LinkedList<SelectionStrategy<? super Object>>();
        strategies.add(rank);
        strategies.add(roulette);
        SelectionStrategyControl<?> control = new SelectionStrategyControl<Object>(strategies);
        JComboBox component = control.getControl();
        assert component.getItemCount() == 2 : "Combobox should contain 2 entries, is " + component.getItemCount();
        assert component.getItemAt(0) == rank : "First item should be rank selection.";
        assert component.getItemAt(1) == roulette : "Second item should be roulette wheel selection.";
    }


    @Test
    public void testChangeSelection()
    {
        SelectionStrategy<Object> quarter = new TruncationSelection(0.25);
        SelectionStrategy<Object> half = new TruncationSelection(0.5);
        List<SelectionStrategy<? super Object>> strategies = new LinkedList<SelectionStrategy<? super Object>>();
        strategies.add(quarter);
        strategies.add(half);
        SelectionStrategyControl<Object> control = new SelectionStrategyControl<Object>(strategies);

        List<EvaluatedCandidate<String>> population = new LinkedList<EvaluatedCandidate<String>>();
        population.add(new EvaluatedCandidate<String>("DDD", 4));
        population.add(new EvaluatedCandidate<String>("CCC", 3));
        population.add(new EvaluatedCandidate<String>("BBB", 2));
        population.add(new EvaluatedCandidate<String>("AAA", 1));

        // Using the first selection strategy, only the fittest 25% of candidates should be selected from.
        List<String> selection = control.getSelectionStrategy().select(population, true, 2, null);
        assert selection.get(0).equals("DDD") : "Wrong candidate selected: " + selection.get(0);
        assert selection.get(1).equals("DDD") : "Wrong candidate selected: " + selection.get(1);

        JComboBox component = control.getControl();
        component.setSelectedIndex(1); // Switch to 50% truncation.

        // Using the second selection strategy, only the fittest 50% of candidates should be selected from.
        selection = control.getSelectionStrategy().select(population, true, 2, null);
        assert selection.contains("CCC") : "Candidate CCC missing from selection.";
        assert selection.contains("DDD") : "Candidate DDD missing from selection.";
    }


    @Test(dependsOnMethods = "testChangeSelection")
    public void testReset()
    {
        SelectionStrategy<Object> quarter = new TruncationSelection(0.25);
        SelectionStrategy<Object> half = new TruncationSelection(0.5);
        List<SelectionStrategy<? super Object>> strategies = new LinkedList<SelectionStrategy<? super Object>>();
        strategies.add(quarter);
        strategies.add(half);
        SelectionStrategyControl<Object> control = new SelectionStrategyControl<Object>(strategies);

        control.getControl().setSelectedIndex(1); // Not the first strategy.
        control.reset(); // Reset to the first strategy.

        List<EvaluatedCandidate<String>> population = new LinkedList<EvaluatedCandidate<String>>();
        population.add(new EvaluatedCandidate<String>("DDD", 4));
        population.add(new EvaluatedCandidate<String>("CCC", 3));
        population.add(new EvaluatedCandidate<String>("BBB", 2));
        population.add(new EvaluatedCandidate<String>("AAA", 1));

        // Using the first selection strategy, only the fittest 25% of candidates should be selected from.
        List<String> selection = control.getSelectionStrategy().select(population, true, 2, null);
        assert selection.get(0).equals("DDD") : "Wrong candidate selected: " + selection.get(0);
        assert selection.get(1).equals("DDD") : "Wrong candidate selected: " + selection.get(1);
    }
}
