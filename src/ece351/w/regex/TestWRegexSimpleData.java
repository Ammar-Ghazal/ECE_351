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

package ece351.w.regex;

import java.util.ArrayList;
import java.util.List;

class TestWRegexSimpleData {

	static List<String> regexs = new ArrayList<String>();
	
	static {
		// r1.wave: This regex is exactly the same as the input, so it accepts.
		regexs.add("A: 0;\\s*");
		// regexs.add("A: 0[?]*1;\\s*");

		// r2.wave
		// Copy the regex from above and paste it here.
		// Generalize the regex to accept multiple input values, either 0 or 1.
		// regexs.add("A:[ 0]?[ 1]?;\\s*");
		regexs.add("A:[ ]?[0]?[ ]?[1]?;\\s*"); //spaces optional?
		// TODO: short code snippet
		// throw new ece351.util.Todo351Exception();

		// r3.wave
		// Copy the regex from above and paste it here.
		// Generalize the regex to allow whitespace between the last signal and the semi-colon.
		// regexs.add("A:[ 0]?[ 1]?[ ]?;\\s*");
		regexs.add("A:[ ]?[0]?[ ]?[1]?[ ]?;\\s*"); //spaces optional?
		// TODO: short code snippet
		// throw new ece351.util.Todo351Exception();

		// r4.wave
		// Copy the regex from above and paste it here.
		// Generalize the regex to allow multi-character pin names.
		regexs.add("[AB][AB]?:[ ]?[0]?[ ]?[1]?[ ]?;\\s*"); //spaces optional?
		// TODO: short code snippet
		// throw new ece351.util.Todo351Exception();
		
		// r5.wave
		// Copy the regex from above and paste it here.
		// Generalize the regex to allow lower case in pin names.
		regexs.add("[A-Za-z][A-Za-z]?:[ ]?[0]?[ ]?[1]?[ ]?;\\s*"); //spaces optional?
		// TODO: short code snippet
		// throw new ece351.util.Todo351Exception();
		
		// r6.wave
		// Copy the regex from above and paste it here.
		// Generalize the regex to allow multiple spaces between values.
		regexs.add("[ ]*[A-Za-z]*[ ]*:[ ]*[0]?[ ]*[1]?[ ]*;\\s*"); //spaces optional?
		// TODO: short code snippet
		// throw new ece351.util.Todo351Exception();

		// r7.wave
		// Copy the regex from above and paste it here.
		// Generalize the regex for whitespace again.
		// No modification made
		regexs.add("[ ]*[A-Za-z]*[ ]*:[ ]*[0]?[ ]*[1]?[ ]*;\\s*");
		// TODO: short code snippet
		// throw new ece351.util.Todo351Exception();

		// r8.wave
		// Copy the regex from above and paste it here.
		// Generalize the regex to allow multiple pins
		regexs.add("[[\\s]*[ ]*[A-Za-z]+[ ]*:[ |1|0]*;[\\s]*]*"); // spaces optional?
		regexs.add("[[\\s]*[ ]*[A-Za-z]+[ ]*:[[1|0]*]*;[\\s]*]*"); // latest REGEX
		regexs.add("[[\\s]*[ ]*[A|B|a|b][A|B|a|b]*[ ]*:[ |1|0]+;[\\s]*]+"); // latest REGEX
		regexs.add("((\\s)*( )*[A-Za-z]+( )*:( |1|0)+;(\\s)*)+"); // latest REGEX
		// X: A B C ;
			// also changed the * to a + for fist bit to ensure at least 1 is present
			// just allow for multiple lines, add []* over everything
		// TODO: short code snippet
		// throw new ece351.util.Todo351Exception();
		
	};
	
}
