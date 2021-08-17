/*
 *  Copyright Ivo Blöchliger, ivo.bloechliger@unifr.ch
 *  University of Fribourg.
 *  You may use and modify this code for teaching and learning 
 *  purposes. For any other use, please contact the author.
 */

package tm;

import java.io.IOException;
import java.io.PrintStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import javax.swing.JFrame;
import tm.machine.Machine;
import tm.machine.Tape;
import tm.parser.Parser;

/**
 *
 * @author ivo
 */
public class TM {
    
    public static boolean verbose = false;
    public static boolean stop = false;

    public static void main(String[] args) throws IOException {
        if (args.length==0) {
            CommandLineBuilderPanel panel = new CommandLineBuilderPanel();
            JFrame frame = new JFrame();
            frame.setTitle("Turing Machine Assembler");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setContentPane(panel);
            frame.pack();
            frame.setVisible(true);
        } else {
            Options o = new Options(args);
            String asm = o.getAsm();
            Tape tape = o.getTape();
            PrintStream out = o.getStream();
            Parser p;
            try {
                p = new Parser(asm,tape, o.keepTape);
            } catch (IllegalArgumentException e) {
                out.println(e.getMessage());
                Throwable t = e.getCause();
                while (t!=null) {
                    out.println(t.getMessage());
                    t = t.getCause();
                }
                return;
            }
            if (o.translateDest!=null && o.translateSource!=null) {
                p.translate(o.translateSource, o.translateDest);
            }
            if (o.showParsed) {
                out.println(p);
            }
            Machine m;
            try {
                m = p.instanciate();
            } catch (IllegalArgumentException e) {
                out.println(e.getMessage());
                Throwable t = e.getCause();
                while (t!=null) {
                    out.println(t.getMessage());
                    t = t.getCause();
                }
                return;
            }
            if (o.showCompiled) {
                out.println(m.states);
            }
            if (o.showUniversal) {
                out.println(m.universalEncode());
            }
            if (o.showUniversal2) {
                out.println(m.universalEncode2());
            }
            if (o.runMachine) {
                try {
                    m.execute(tape,out,o);
                } catch (Exception e) {
                    out.println("This is a bug in the Turing-Assembler System and should not happen.");
                    out.println("Please make sure, you use the latest version.\nIf so, please send the input file(s) and command line-options to");
                    out.println("ivo.bloechliger@unifr.ch so the bug can be verified and hopefully corrected. Please also include the following:");
                    out.println(e.getMessage());
                    e.printStackTrace(out);
                    return;
                }
            }
        }
    }
}
