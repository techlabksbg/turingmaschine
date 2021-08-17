/*
 * Copyright Ivo Blöchliger, ivo.bloechliger@unifr.ch
 * University of Fribourg.
 * You may use and modify this code for teaching and learning 
 * purposes. For any other use, please contact the author.
 */

package tm.machine;

/**
 *
 * @author Ivo Blöchliger (ivo.bloechlige@unifr.ch)
 * 
 */
public class Tape {
    
    public static final char BLANK = '.';
    
    private StringBuilder tape = new StringBuilder(BLANK);
    private int position=0;
    private int width = 0;
    private int realPos = 0;
    
    public Tape() {}
    public Tape(String ini) {
        tape = new StringBuilder(ini);
    }
    
    public Tape(String tape, Alphabet a) {
        for (char c : tape.toCharArray()) {
            if (!a.contains(c)) {
                throw new IllegalArgumentException(String.format("Illegal character (not in alphabet) in tape: ",c));
            }
        }
        this.tape = new StringBuilder(tape);
    }
    
    public Tape(int w) {
        width = w;
    }
    public Tape(String ini, int w) {
        this(ini);
        width = w;
    }
    
    public void setContents(String tape) {
        this.tape = new StringBuilder(tape);
    }
    
    @Override
    public String toString() {
        return tape.toString();
    }
    
    public int getPosition() {
        return position;
    }
    
    public char read(int i) {
        return tape.charAt(i);
    }
    
    public char read() {
        return getCharAt(position);
    }
    
    public void write(char c) {
        tape.setCharAt(position, c);
    }
    
    public void left() {
        position--;
        realPos--;
        if (position<0) {
            tape.insert(0, BLANK);
            position=0;
        }
    }
    
    public void right() {
        position++;
        realPos++;
        if (tape.length()<=position) {
            tape.append(BLANK);
        }
    }
    
    public void move(int diff) {
        if (diff>0) {
            right();
        } else if (diff<0) {
            left();
        }
    }
    
    public char getCharAt(int p) {
        if (p<0 || p>=tape.length()) {
            return BLANK;
        } else {
            return tape.charAt(p);
        }
    }
    
    public void setWidth(int w) {
        this.width = w;
    }
    
    public void translate(String src, String dest) {
        for (int i=0; i<tape.length();i++) {
            int p = src.indexOf(""+tape.charAt(i));
            if (p>=0) {
                tape.setCharAt(i, dest.charAt(p));
            }
        }
    }
    
    public int getRealPos() {
        return realPos;
    }
    
    public String niceString() {
        if (width==0) { // Machine moves
            StringBuilder nice = new StringBuilder().append(BLANK);
            nice.append(' ').append(BLANK).append(' ').append(BLANK);
            for (int p=0; p<tape.length(); p++) {
                if (p==position) {
                    nice.append('[').append(tape.charAt(p)).append(']');
                } else if (p==position+1) {
                    nice.append(tape.charAt(p));
                } else {
                    nice.append(' ').append(tape.charAt(p));
                }
            }
            if (position<tape.length()-1) {
                nice.append(' ');
            }
            nice.append(BLANK).append(' ').append(BLANK);
            return nice.toString();
        } else { // Tape moves
            StringBuilder nice = new StringBuilder(width);
            for (int i=-width/4; i<width/4; i++) {
                nice.append(getCharAt(i+position));
                if (i==-1) {
                    nice.append('[');
                } else if (i==0) {
                    nice.append(']');
                } else {
                    nice.append(' ');
                }
            }
            return nice.toString();
        }
    }
}
