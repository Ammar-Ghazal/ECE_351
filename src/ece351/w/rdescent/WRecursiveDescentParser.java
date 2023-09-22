/* *********************************************************************
 * ECE351 
 * Department of Electrical and Computer Engineering 
 * University of Waterloo 
 * Term: Fall 2021 (1219)
 *
 * The base version of this file is the intellectual property of the
 * University of Waterloo. Redistribution is prohibited.
 *
 * By pushing changes to this file I affirm that I am the author of
 * all changes. I affirm that I have complied with the course
 * collaboration policy and have not plagiarized my work. 
 *
 * I understand that redistributing this file might expose me to
 * disciplinary action under UW Policy 71. I understand that Policy 71
 * allows for retroactive modification of my final grade in a course.
 * For example, if I post my solutions to these labs on GitHub after I
 * finish ECE351, and a future student plagiarizes them, then I too
 * could be found guilty of plagiarism. Consequently, my final grade
 * in ECE351 could be retroactively lowered. This might require that I
 * repeat ECE351, which in turn might delay my graduation.
 *
 * https://uwaterloo.ca/secretariat-general-counsel/policies-procedures-guidelines/policy-71
 * 
 * ********************************************************************/

package ece351.w.rdescent;

import org.parboiled.common.ImmutableList;

import ece351.util.Lexer;
import ece351.w.ast.WProgram;
import ece351.w.ast.Waveform;

public final class WRecursiveDescentParser {
    private final Lexer lexer;

    public WRecursiveDescentParser(final Lexer lexer) {
        this.lexer = lexer;
    }

    public static WProgram parse(final String input) {
    	final WRecursiveDescentParser p = new WRecursiveDescentParser(new Lexer(input));
        return p.parse();
    }

    public WProgram parse() {
    	// STUB: return null;
        // TODO: longer code snippet
        // throw new ece351.util.Todo351Exception();
        // Similar to recognizer, except now build the tree, instead of returning void

        // First, create instances of WProgram and Waveform
        WProgram wprogram = new WProgram();
        Waveform waveform = new Waveform();

        // // keep reading waveform data until lexer reaches EOF
        while(!lexer.inspectEOF()){
            String waveformStr = "";

            // Store the pin name of the waveform:
            while (lexer.inspectID()){
                // ******* --------- might be able to change the = to a += below
                // waveformStr = lexer.consumeID();

                // re store waveform in itself since strings are immutable type
                waveform = waveform.rename((lexer.consumeID()).toString());
            }

            // Inspect, consume, and append : to wavefor
            if(lexer.inspect(":")){
                // waveform = waveform.append(lexer.consume(":"));
                lexer.consume(":");
            }

            // Inspect and consume the bits 0 & 1, and the ; terminating the waveform
            while(!lexer.inspect(";")){
                if(lexer.inspect("1", "0")){
                    waveform = waveform.append(lexer.consume("0", "1"));
                    // lexer.consume("0", "1");
                }else{
                     waveform = waveform.append(lexer.consume("0", "1"));
                    // lexer.consume("0", "1");
                }
            }
            // Consume and append ";"
            // waveform = waveform.append(lexer.consume(";"));
            lexer.consume(";");

            // Save waveform in WProgram: remember wprogram is also of type immutable list
            wprogram = wprogram.append(waveform);

            // Clear waveform variable for next waveform:
            waveform = new Waveform();

            // throw new ece351.util.Todo351Exception();
        }
        return wprogram;
    }
}
