package org.uncommons.watchmaker.framework.operators;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import org.uncommons.watchmaker.framework.EvolutionaryOperator;

/**
 * Mutation of individual characters in a string according to some
 * probability.
 * @author Daniel Dyer
 */
public class StringMutation implements EvolutionaryOperator<String>
{
    private final char[] alphabet;
    private final double mutationProbability;

    /**
     * @param alphabet The permitted values for each character in a string.
     * @param mutationProbability The probability that a given character
     * is changed.
     */
    public StringMutation(char[] alphabet, double mutationProbability)
    {
        if (mutationProbability < 0 || mutationProbability > 1)
        {
            throw new IllegalArgumentException("Mutation probability must be between 0 and 1.");
        }
        this.alphabet = alphabet.clone();
        this.mutationProbability = mutationProbability;
    }


    @SuppressWarnings("unchecked")
    public <S extends String> List<S> apply(List<S> selectedCandidates, Random rng)
    {
        List<S> mutatedPopulation = new ArrayList<S>(selectedCandidates.size());
        for (String s : selectedCandidates)
        {
            mutatedPopulation.add((S) mutateString(s, rng));
        }
        return mutatedPopulation;
    }


    private String mutateString(String s, Random rng)
    {
        StringBuilder buffer = new StringBuilder(s);
        for (int i = 0; i < buffer.length(); i++)
        {
            if (rng.nextDouble() <= mutationProbability)
            {
                buffer.setCharAt(i, alphabet[rng.nextInt(alphabet.length)]);
            }
        }
        return buffer.toString();
    }
}
