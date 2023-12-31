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

package ece351.w.parboiled;
// import org.apache.tools.ant.types.resources.First; // causes illegal import error?
import org.parboiled.Rule;
import org.parboiled.annotations.BuildParseTree;
import org.parboiled.common.FileUtils;

import ece351.util.BaseParser351;
import ece351.w.ast.WProgram;
import java.lang.invoke.MethodHandles;

// import javax.sound.midi.Sequence; // causes illegal import error?


@BuildParseTree
//Parboiled requires that this class not be final
public /*final*/ class WParboiledRecognizer extends BaseParser351 {

	public static Class<?> findLoadedClass(String className) throws IllegalAccessException {
        try {
            return MethodHandles.lookup().findClass(className);
        } catch (ClassNotFoundException e) {
            return null;
        }
    }

    public static Class<?> loadClass(byte[] code) throws IllegalAccessException {
        return MethodHandles.lookup().defineClass(code);
    }
	/**
	 * Run this recognizer, exit with error code 1 to reject.
	 * This method is called by wave/Makefile.
	 * @param args args[0] is the name of the input file to read
	 */
	public static void main(final String[] args) {
    	process(WParboiledRecognizer.class, FileUtils.readAllText(args[0]));
    }
	
	public static void recognize(final String inputText) {
		process(WParboiledRecognizer.class, inputText);
	}

	
	/** 
	 * Use this method to print the parse tree for debugging.
	 * @param w the text of the W program to recognize
	 */
	public static void printParseTree(final String w) {
		printParseTree(WParboiledRecognizer.class, w);
	}

	/**
	 * By convention we name the top production in the grammar "Program".
	 */
	@Override
	public Rule Program() { // Diff than Program() in WRecursiveDescentRecognizer, must return type Rule
        return Sequence(OneOrMore(Waveform()), EOI);
        // // TODO: short code snippet
        // throw new ece351.util.Todo351Exception();
	}
    
	/**
	 * Each line of the input W file represents a "pin" in the circuit.
	 */
    public Rule Waveform() {
        // Here, we need to have the equivalent of inspecting & consuming of ":" and ";" inside of a while loop
        return Sequence(W0(), Name(), W0(), ":", W0(), BitString(), W0(), ";", W0());
        // - pass the W0() for whitespace, name for the "pin name", and BitString for the bit values
        // // TODO: short code snippet
        // throw new ece351.util.Todo351Exception();
    }

    /**
     * The first token in each statement is the name of the waveform 
     * that statement represents.
     */
    public Rule Name() {
        return Sequence(W0(), Letter(), ZeroOrMore(Letter()));
        // // TODO: short code snippet
        // throw new ece351.util.Todo351Exception();
    }

    /**
     * A Name is composed of a sequence of Letters. 
     * Recall that PEGs incorporate lexing into the parser.
     */
    public Rule Letter() {
        return FirstOf(CharRange('A', 'Z'), CharRange('a', 'z'));
            // -- fixed
        // // TODO: short code snippet
        // throw new ece351.util.Todo351Exception();
    }

    /**
     * A BitString is the sequence of values for a pin.
     */
    public Rule BitString() {
        return Sequence(W0(), OneOrMore(Bit()), W0(), ZeroOrMore(Bit(), W0()));
            // return at least one bit, with any whitespace, and optionally, more bits/whitespace
        // // TODO: short code snippet
        // throw new ece351.util.Todo351Exception();
    }
    
    /**
     * A BitString is composed of a sequence of Bits. 
     * Recall that PEGs incorporate lexing into the parser.
     */
    public Rule Bit() {
        return FirstOf('0', '1');
            // the bit is either 0 or 1
        // // TODO: short code snippet
        // throw new ece351.util.Todo351Exception();
    }

}

