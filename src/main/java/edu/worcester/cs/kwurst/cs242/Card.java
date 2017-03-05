/*
 *  Copyright (C) 2010-2015 Karl R. Wurst
 * 
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110-1301, USA
 */

package edu.worcester.cs.kwurst.cs242;

import java.util.*;

/** 
 * Represents a deck of cards. Modified from Sun's example on their Enums explanation page.
 * I have made only one change to the Card class - changing the ordering of the Ranks 
 * so that ACE is the lowest rank.
 * 
 * @author Karl R. Wurst
 * @version 29 March 2015
 * @see <a href="http://java.sun.com/j2se/1.5.0/docs/guide/language/enums.html">Enums explanation on Sun's website</a>
 */

public class Card {
   
    public enum Rank { ACE, DEUCE, THREE, FOUR, FIVE, SIX,
        SEVEN, EIGHT, NINE, TEN, JACK, QUEEN, KING }

    public enum Suit { CLUBS, DIAMONDS, HEARTS, SPADES }

    private final Rank rank;
    private final Suit suit;
    
    /**
     * Create a new card
     * @param rank the rank of the card
     * @param suit the suit of the card
     */
    protected Card(Rank rank, Suit suit) {
        this.rank = rank;
        this.suit = suit;
    }

    /**
     * Get the rank of the card.
     * @return
     */
    public Rank rank() { 
    	return rank; 
    }
    
    /**
     * Get the suit of the card.
     * @return
     */
    public Suit suit() {
    	return suit; 
    }
    
    /**
	 * Provide a human-readable representation of the card.
     * @see java.lang.Object#toString()
     */
    public String toString() { 
    	return rank + " of " + suit; 
    }

    // This is single deck of cards, already filled, that can be copied to get a new deck whenever needed.
    private static final List<Card> protoDeck = new ArrayList<Card>();

    // Initialize prototype deck
    static {
        for (Suit suit : Suit.values())
            for (Rank rank : Rank.values())
                protoDeck.add(new Card(rank, suit));
    }

    /* (non-Javadoc)
     * @see java.lang.Object#equals(java.lang.Object)
     */
    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        Card other = (Card) obj;
        if (rank != other.rank)
            return false;
        if (suit != other.suit)
            return false;
        return true;
    }

    /**
     * Create a new deck of cards. 
     * This deck is not shuffled - it is in order by ranks within suits.
     * @return an ArrayList of Cards
     */
    public static ArrayList<Card> newDeck() {
        return new ArrayList<Card>(protoDeck); // Return copy of prototype deck
    }
}