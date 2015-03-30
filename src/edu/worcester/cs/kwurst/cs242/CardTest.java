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

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

/** 
 * Tests for the Card class.
 * 
 * @author Karl R. Wurst
 * @version 29 March 2015
 */

public class CardTest {
    
    Card testCard; 

    @Before
    public void setUp() throws Exception {
        testCard = new Card(Card.Rank.THREE, Card.Suit.CLUBS);
        
    }

    @Test
    public void testRank() {
        assertEquals(testCard.rank(), Card.Rank.THREE);
    }

    @Test
    public void testSuit() {
        assertEquals(testCard.suit(), Card.Suit.CLUBS);
    }

    @Test
    public void testToString() {
        assertEquals(testCard.toString(), "THREE of CLUBS");
    }

    @Test
    public void testEquals() {
        assertTrue(testCard.equals(testCard));
        assertTrue(testCard.equals(new Card(Card.Rank.THREE, Card.Suit.CLUBS)));
        assertFalse(testCard.equals(null));
        assertFalse(testCard.equals(new String("Card")));
        assertFalse(testCard.equals(new Card(Card.Rank.FOUR, Card.Suit.CLUBS)));
        assertFalse(testCard.equals(new Card(Card.Rank.THREE, Card.Suit.HEARTS)));
    }
    
    @Test
    public void testNewDeck() {
        ArrayList<Card> deck = Card.newDeck();
        assertEquals(deck.size(), 52);
        assertEquals(deck.get(0), new Card(Card.Rank.ACE, Card.Suit.CLUBS));
        assertEquals(deck.get(51), new Card(Card.Rank.KING, Card.Suit.SPADES));        
    }

}
