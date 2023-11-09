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
import java.io.File;

import org.parboiled.Rule;
import org.parboiled.annotations.BuildParseTree;
import org.parboiled.common.FileUtils;
import org.parboiled.common.ImmutableList;

import ece351.util.BaseParser351;
import ece351.w.ast.WProgram;
import ece351.w.ast.Waveform;
import java.lang.invoke.MethodHandles;

@BuildParseTree
//Parboiled requires that this class not be final
public /*final*/ class WParboiledParser extends BaseParser351 {

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
	 * Run this parser, exit with error code 1 for malformed input.
	 * Called by wave/Makefile.
	 * @param args
	 */
	public static void main(final String[] args) {
    	process(WParboiledParser.class, FileUtils.readAllText(args[0]));
    }

	/**
	 * Construct an AST for a W program. Use this for debugging.
	 */
	public static WProgram parse(final String inputText) {
		return (WProgram) process(WParboiledParser.class, inputText).resultValue;
	}

	/**
	 * By convention we name the top production in the grammar "Program".
	 */
	@Override
    public Rule Program() {
        // Initialize a struct to hold the waveforms
        WProgram emptyListWaveforms = new WProgram();
        // In the return Rule, specify that we are expecting at least one waveform
        return Sequence(push(emptyListWaveforms), OneOrMore(Waveform()), EOI);
            // same as in the recognizer, but with the list of waveforms
        // // TODO: short code snippet
        // throw new ece351.util.Todo351Exception();
    }

	/**
	 * Each line of the input W file represents a "pin" in the circuit.
	 */
    public Rule Waveform() {
        // return Sequence(W0(), Name(), push(match()), push(new Waveform ((String)pop())), W0(), ":",
        // W0(), BitString(), W0(), ";", W0(), push(((WProgram)pop()).append((Waveform)pop())));
    
        return Sequence(W0(), Name(), push(match()), push(new Waveform ((String)pop())), W0(), ":",
        W0(), BitString(), W0(), ";", W0(), swap(), push(((WProgram)pop()).append((Waveform)pop())));
        // same as the recognizer with these added changes:
            // push match is for saving the matched pin name
            // push new waveform saves a new waveform object, the pop is to put the name we just saved onto the waveform
            // then save WProgram, and add to it the waveform we saved earlier
            // unsure about swap() -- fixed
        // // TODO: longer code snippet
        // throw new ece351.util.Todo351Exception();
        // push(((WProgram)pop()).append((Waveform)pop()));
    }

    /**
     * The first token on each line is the name of the pin that line represents.
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
        // same as recognizer
        // // TODO: short code snippet
        // throw new ece351.util.Todo351Exception();
    }

    /**
     * A BitString is the sequence of values for a pin.
     */
    public Rule BitString() {
        return Sequence(W0(), OneOrMore(Bit()), W0(), ZeroOrMore(Bit(), W0()));
        // same as recognizer
        // // TODO: short code snippet
        // throw new ece351.util.Todo351Exception();
    }
    
    /**
     * A BitString is composed of a sequence of Bits. 
     * Recall that PEGs incorporate lexing into the parser.
     */
    public Rule Bit() {
        return Sequence(FirstOf('0', '1'), push(match()), swap(), push(((Waveform)pop()).append((String)pop())));
        // // TODO: short code snippet
        // throw new ece351.util.Todo351Exception();
    }

}
