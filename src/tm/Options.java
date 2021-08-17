/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package tm;

import java.io.BufferedOutputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import tm.machine.LineFilter;
import tm.machine.Tape;


/**
 *
 * @author ivo
 */
public class Options {
    public String asmFile;
    public String tapeFile;
    public String tape;
    public boolean showParsed;
    public boolean showCompiled;
    public boolean showUniversal;
    public boolean showUniversal2;
    public boolean showCompact;
    public boolean runMachine;
    public int width = 0;
    public int limit = 0;
    public String outputFile;
    private PrintStream out;
    public boolean keepTape = false;
    public String translateSource;
    public String translateDest;
    public LineFilter lineFilter = new LineFilter();
    
    public Options(String[] args) {
        int p=0;
        while (p<args.length) {
            switch(args[p]) {
                case "-T":
                    tapeFile = args[++p];
                    keepTape = true;
                    break;
                case "-t":
                    tape = args[++p];
                    keepTape = true;
                    break;
                case "-p":
                    showParsed = true;
                    break;
                case "-s":
                    showCompiled = true;
                    break;
                case "-u":
                    showUniversal = true;
                    break;
                case "-u2":
                    showUniversal2 = true;
                    break;
                case "-r":
                    runMachine = true;
                    break;
                case "-m":
                    limit = Integer.parseInt(args[++p]);
                    break;
                case "-c":
                case "-fc":
                    lineFilter.add((LineFilter lf) -> lf.stateName!=lf.oldState);
                    break;
                case "-ft":
                    int minPos = Integer.parseInt(args[++p]);
                    int maxPos = Integer.parseInt(args[++p]);
                    lineFilter.add((LineFilter lf) -> lf.pos>=minPos && lf.pos<=maxPos);
                    break;
                case "-fi":
                    int minIter = Integer.parseInt(args[++p]);
                    lineFilter.add((LineFilter lf) -> lf.iter>=minIter);
                    break;
                case "-fs":
                    String state = args[++p];
                    lineFilter.add((LineFilter lf) -> lf.stateName.equals(state));
                    break;
                case "-fr":
                    String symbols = args[++p];
                    lineFilter.add((LineFilter lf) -> symbols.contains(String.valueOf(lf.read)));
                    break;
                case "-fw":
                    symbols = args[++p];
                    lineFilter.add((LineFilter lf) -> symbols.contains(String.valueOf(lf.write)));
                    break;
                case "-w":
                    width = Integer.parseInt(args[++p]);
                    break;
                case "-o":
                    outputFile = args[++p];
                    break;
                case "-a":
                    translateSource = args[++p];
                    translateDest = args[++p];
                    break;
                default:
                    asmFile=args[p];
            }
            p++;
        }
    } 
    
    public String getAsm() throws IOException {
        return new String(Files.readAllBytes(Paths.get(asmFile)));
    }
    
    public Tape getTape() throws IOException {
        if (tape!=null) {
            return new Tape(tape);
        }
        if (tapeFile!=null) {
            String tapeString = (new String(Files.readAllBytes(Paths.get(tapeFile)))).replaceAll("\\s", "");
            return new Tape(tapeString);
        }
        return new Tape(".");
    }
    
    public PrintStream getStream() throws FileNotFoundException {
        if (outputFile==null) {
            return System.out;
        } else {
            out = new PrintStream(new FileOutputStream(outputFile));
            return out;
        }
    }
    
    public void closeStream() {
        if (out!=null) {
            out.close();
        }
    }
    
}
