/*
 *  Copyright Ivo Blöchliger, ivo.bloechliger@unifr.ch
 *  University of Fribourg.
 *  You may use and modify this code for teaching and learning 
 *  purposes. For any other use, please contact the author.
 */

package tm.parser;

import tm.machine.Alphabet;

/**
 *
 * @author ivo
 */
public class StateLine {
    Symbol read;
    boolean isSimple = true;
    Symbol write;
    Command[] commands;
    Direction dir;
    StateName nextState;
    int line = 0;
    
    StateLine(String line, int l) {
        this.line = l;
        if (line.contains("{")) {
            int p1 = line.indexOf("{");
            int p2 = line.indexOf("}");
            if (p2==-1) {
                throw new IllegalArgumentException(String.format("missing } in %s at line %d", line,this.line));
            }
            read = new Symbol(line.substring(0,p1).trim());
            isSimple = false;
            String[] parts = line.substring(p1+1, p2).trim().split("\\s+");
            commands = new Command[parts.length];
            boolean lastWrite = false;
            for (int i=0; i<parts.length; i++) {
                commands[i] = new Command(parts[i]);
                if (lastWrite && commands[i].write!=null) {
                    throw new IllegalArgumentException(String.format("It is futile to have two consecutive print-commands in %s at line %d", line,this.line));
                }
                lastWrite = commands[i].write!=null;
            }
            nextState = new StateName(line.substring(p2+1).trim(), this.line);
        } else {
            String[] parts = line.split("\\s+");
            read = new Symbol(parts[0]);
            write = new Symbol(parts[1]);
            dir = Direction.fromString(parts[2]);
            StringBuilder rest = new StringBuilder();
            for (int i=3; i<parts.length; i++) {
                rest.append(parts[i]);
                if (i<parts.length-1) {
                    rest.append(" ");
                }
            }
            nextState = new StateName(rest.toString(), this.line);
        }
    }
    
    public void translate(String src, String dest) {
        read.translate(src, dest);
        if (write!=null) {
            write.translate(src, dest);
        } else if (commands!=null) {
            for (Command c : commands) {
                c.translate(src,dest);
            }
        }
        nextState.translate(src, dest);
    }
    
    @Override
    public String toString() {
        if (isSimple) {
            return read.toString()+" "+write.toString()+" "+dir.toString()+" "+nextState.toString();
        } else {
            StringBuilder sb = new StringBuilder();
            sb.append(read.toString()).append(" {");
            for (int i=0; i<commands.length; i++) {
                sb.append(commands[i].toString());
                if (i<commands.length-1) sb.append(" ");
            }
            sb.append("} ").append(nextState.toString());
            return sb.toString();
        }
    }

    void addTo(Alphabet a) {
        read.addTo(a);
        if (write!=null) {
            write.addTo(a);
        }
        if (commands!=null) {
            for (Command c: commands) {
                c.addTo(a);
            }
        }
     }
}
