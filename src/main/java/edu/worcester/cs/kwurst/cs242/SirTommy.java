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

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Scanner;

import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.SwingWorker;

import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.io.IOException;
import java.io.InputStream;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;
import java.io.PrintStream;
import java.io.PrintWriter;

import javax.swing.JTextArea;

/**
 * Sir Tommy solitaire game.
 * @author Karl R. Wurst
 * @version 29 March 2015
 */

public class SirTommy {
    private LinkedList<Card> deck;
    private LinkedList<Card>[] foundation;
    private LinkedList<Card>[] wastePile;
    private Scanner in;

    /**
     * Sets up the empty piles and shuffles the deck.
     */
    public SirTommy() throws IOException {
        /* Console code from https://stackoverflow.com/a/9680496 starts... */
        
        PipedInputStream inPipe = new PipedInputStream();
        PipedInputStream outPipe = new PipedInputStream();

        System.setIn(inPipe);
        System.setOut(new PrintStream(new PipedOutputStream(outPipe), true));

        PrintWriter inWriter = new PrintWriter(new PipedOutputStream(inPipe), true);

        JFrame frame = new JFrame("Sir Tommy Solitaire");

        //frame.add(console(outPipe, inWriter));
        //frame.setSize(1000, 1000);
        //frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        //frame.setVisible(true);
        
        /* Console code from https://stackoverflow.com/a/9680496 ends... */
        
        JScrollPane pane = new JScrollPane(console(outPipe, inWriter),JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);

        frame.setSize(1000, 1000);
        frame.getContentPane().add(pane);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(true);
        
        System.out.println(" Sir Tommy Solitaire. For rules see: https://en.wikipedia.org/wiki/Sir_Tommy\n");
        
        deck = new LinkedList<Card>(Card.newDeck());
        foundation = new LinkedList[4];
        wastePile = new LinkedList[4];

        for (int i=0; i < 4; i++) {
            foundation[i] = new LinkedList<Card>();
            wastePile[i] = new LinkedList<Card>();
        }

        Collections.shuffle(deck); // shuffle the deck

        in = new Scanner(System.in);
    }

    /* Console code from https://stackoverflow.com/a/9680496 */
    public static JTextArea console(final InputStream out, final PrintWriter in) {
        final JTextArea area = new JTextArea();
        
        // handle "System.out"
        new SwingWorker<Void, String>() {
            @Override protected Void doInBackground() throws Exception {
                Scanner s = new Scanner(out);
                while (s.hasNextLine()) publish(s.nextLine() + "\n");
                return null;
            }
            @Override protected void process(List<String> chunks) {
                for (String line : chunks) area.append(line);
            }
        }.execute();

        // handle "System.in"
        area.addKeyListener(new KeyAdapter() {
            private StringBuffer line = new StringBuffer();
            @Override public void keyTyped(KeyEvent e) {
                char c = e.getKeyChar();
                if (c == KeyEvent.VK_ENTER) {
                    in.println(line);
                    line.setLength(0); 
                } else if (c == KeyEvent.VK_BACK_SPACE) { 
                    line.setLength(line.length() - 1); 
                } else if (!Character.isISOControl(c)) {
                    line.append(e.getKeyChar());
                }
            }
        });

        return area;
    }
    
    /**
     * Displays the foundations, deck, and waste piles. 
     */
    public void displayTableau() {
        System.out.print("\t\t");
        for (int i = 0; i < 4; i++ ) {
            System.out.print("F" + i + ":[" + foundation[i].peek() + "]\t");
        }
        System.out.print("\n D:[" + deck.peek() + "]\t");
        for (int i = 0; i < 4; i++ ) {
            System.out.print("W" + i + ":[" + wastePile[i].peek() + "]\t");
        }
        System.out.println();
    }

    /**
     * Plays the game. Asks the player for move information, checks if the move is legal,
     * moves the card, and checks to see if the game is over.
     */
    public void play() {
        String src = null;
        String dest = null;
        LinkedList<Card> srcPile = null;
        boolean gameOver = false;

        while (!gameOver) {
            displayTableau();

            // Get the source of the card to move.
            do {
                System.out.print("\n Move top card from (D, W0, W1, W2, W3, or Q to quit): \n");
                src = in.nextLine();
                if (src.toUpperCase().charAt(0) == 'Q') {
                    gameOver = true;
                }
            } while (!gameOver && (srcPile = getSrcPile(src)) == null);

            // Get the destination to move the card to.
            if (!gameOver) {
                System.out.print("\n to (F0, F1, F2, F3, W0, W1, W2, W3): \n");
                dest = in.nextLine();
                addToDestPile(dest, srcPile);
            }

            // Check to see if the game is over (either by winning, or quitting.)
            gameOver = checkForGameOver(gameOver);			
        }
    }

    /**
     * Check to see if the game is over. If the cards are all on the foundations, the player wins,
     * otherwise, if they quit, they lose.
     * @param gameOver set to true if the player has quit.
     * @return whether the game is over.
     */
    public boolean checkForGameOver(boolean gameOver) {
        int cardsLeft = 0;

        // Count the cards on the waste piles
        for (LinkedList<Card> stack: wastePile) {
            cardsLeft += stack.size();
        }

        // If the deck and waste piles are empty, the player won.
        if ((cardsLeft + deck.size()) == 0) {
            System.out.print(" You win!\n");
            return true;
        } else if (gameOver) { // The player quit, and loses.
            System.out.print(" You lost - try again.\n");
            return true;
        }
        return false; // Game is not over.
    }

    /**
     * Get the pile that the card is being moved from.
     * @param src the choice that the player entered.
     * @return the pile to move the card from, or null if the choice was not valid.
     */
    public LinkedList<Card> getSrcPile(String src) {
        LinkedList<Card> srcPile;
        src = src.toUpperCase().trim(); // trim off spaces and convert to uppercase to make comparison easier
        if (src.equals("D")) {
            srcPile = deck;
        } else if (src.charAt(0) == 'W') { // starts with W, so it is a waste pile
            int pileNum = Integer.parseInt(src.substring(1,2));
            // check that the pile number is valid
            if (pileNum < 0 || pileNum > 4) {
                System.out.print(" " + src + " is not a valid waste pile.\n");
                return null;
            } else {
                srcPile = wastePile[pileNum]; 
            }
        } else {
            System.out.print(" " + src + " is not a valid pile to move from. Please choose another.\n");
            return null;	
        }
        // check that the pile is not empty
        if (srcPile.isEmpty()) {
            System.out.print(" " + src + " is empty. Please choose another.\n");
            return null;
        }
        return srcPile;
    }

    public boolean addToDestPile(String dest, LinkedList<Card> srcPile) {
        dest = dest.toUpperCase().trim();
        if (dest.charAt(0) == 'W') {
            int pileNum = Integer.parseInt(dest.substring(1,2));
            // check that the pile number is valid
            if (pileNum < 0 || pileNum > 4) {
                System.out.print(" " + dest + " is not a valid waste pile.\n");
                return false;
            } else {
                // move the card
                wastePile[Integer.parseInt(dest.substring(1,2))].push(srcPile.pop());
                return true;
            }
        } else if (dest.charAt(0) == 'F') {
            int pileNum = Integer.parseInt(dest.substring(1,2));
            // check that the pile number is valid
            if (pileNum < 0 || pileNum > 4) {
                System.out.print(" " + dest + " is not a valid foundation pile.\n");
                return false;
            } else {
                // try to move the card
                return putCardOnFoundation(srcPile, foundation[Integer.parseInt(dest.substring(1,2))]);
            }	
        } else {
            System.out.print(" " + dest + " is not a valid pile to move to. Please choose another.\n");
            return false;	
        }
    }

    /**
     * Try to move the card from the source pile to a foundation.
     * @param srcPile the pile to move the card from
     * @param destPile the pile to move the card to
     * @return whether the move was successful
     */
    public boolean putCardOnFoundation(LinkedList<Card> srcPile, LinkedList<Card> destPile) {
        // Can only put ACEs on an empty foundation
        if (destPile.isEmpty()) {
            if (srcPile.peek().rank().equals(Card.Rank.ACE)) {
                destPile.push(srcPile.pop());
                return true;
            } else {
                System.out.print(" " + srcPile.peek() + " cannot be put on empty foundation. Please choose another.\n");
                return false;
            }
        } else {
            // Can only put cards that are one rank higher on a non-empty foundation
            if (destPile.peek().rank().compareTo(srcPile.peek().rank()) == -1) {
                destPile.push(srcPile.pop());
                return true;
            } else {
                System.out.print(" " + srcPile.peek().rank() + " cannot be put on " + destPile.peek().rank() + ". Please choose another.\n");
                return false;
            }
        }
    }

    /**
     * Start the game
     * @param args command-line arguments (not used)
     * @throws IOException 
     */
    public static void main(String[] args) throws IOException {
        SirTommy game = new SirTommy();
        game.play();
    }

}
