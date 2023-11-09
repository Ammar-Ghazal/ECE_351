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

package ece351.f.parboiled;

import java.lang.invoke.MethodHandles;

import org.parboiled.Rule;

import ece351.common.ast.AndExpr;
import ece351.common.ast.AssignmentStatement;
import ece351.common.ast.ConstantExpr;
import ece351.common.ast.Constants;
import ece351.common.ast.Expr;
import ece351.common.ast.NotExpr;
import ece351.common.ast.OrExpr;
import ece351.common.ast.VarExpr;
import ece351.f.ast.FProgram;
import ece351.util.CommandLine;
// import ece351.w.ast.WProgram;
// import jas.Var;

// Parboiled requires that this class not be final
public /*final*/ class FParboiledParser extends FBase implements Constants {

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
	public static void main(final String[] args) {
    	final CommandLine c = new CommandLine(args);
    	final String input = c.readInputSpec();
    	final FProgram fprogram = parse(input);
    	assert fprogram.repOk();
    	final String output = fprogram.toString();
    	
    	// if we strip spaces and parens input and output should be the same
    	if (strip(input).equals(strip(output))) {
    		// success: return quietly
    		return;
    	} else {
    		// failure: make a noise
    		System.err.println("parsed value not equal to input:");
    		System.err.println("    " + strip(input));
    		System.err.println("    " + strip(output));
    		System.exit(1);
    	}
    }
	
	private static String strip(final String s) {
		return s.replaceAll("\\s", "").replaceAll("\\(", "").replaceAll("\\)", "");
	}
	
	public static FProgram parse(final String inputText) {
		final FProgram result = (FProgram) process(FParboiledParser.class, inputText).resultValue;
		assert result.repOk();
		return result;
	}

	@Override
	public Rule Program() {
		// Initialize a struct to hold F
        FProgram allFormulas = new FProgram();
        // In the return Rule, specify that we are expecting at least one waveform
        return Sequence(push(allFormulas), OneOrMore(Sequence(Formula(),swap(), push(((FProgram)pop()).append((AssignmentStatement)pop())))), EOI);
		// Follow grammar from manual:
            // Program → Formula+ $$
            // Fomula → Var ‘<=’ Expr ‘;’
            // Expr → Term (‘or’ Term)*
            // Term → Factor (‘and’ Factor)*
            // Factor → ‘not’ Factor | ‘(’ Expr ‘)’ | Var | Constant
            // Constant → ‘‘0’’ | ‘‘1’’
            // Var → id
		// // TODO: longer code snippet
		// throw new ece351.util.Todo351Exception();
	}

    public Rule Formula() {
		// For the grammar production Id, ensure that the Id does not match any of the keywords specified
		// in the rule, 'Keyword'
        // Follow grammar from manual:
            // Program → Formula+ $$
            // Fomula → Var ‘<=’ Expr ‘;’
            // Expr → Term (‘or’ Term)*
            // Term → Factor (‘and’ Factor)*
            // Factor → ‘not’ Factor | ‘(’ Expr ‘)’ | Var | Constant
            // Constant → ‘‘0’’ | ‘‘1’’
            // Var → id
        return Sequence(W0(), Variable(), push(new VarExpr((String)pop())), W0(), "<=", W0(), Expression(), W0(), ";", W0(), swap(), push(new AssignmentStatement((VarExpr)pop(), (Expr)pop())));
        // // TODO: longer code snippet
        // throw new ece351.util.Todo351Exception();
	}
    
    public Rule Expression() {
		// For the grammar production Id, ensure that the Id does not match any of the keywords specified
		// in the rule, 'Keyword'
        // Follow grammar from manual:
            // Program → Formula+ $$
            // Fomula → Var ‘<=’ Expr ‘;’
            // Expr → Term (‘or’ Term)*
            // Term → Factor (‘and’ Factor)*
            // Factor → ‘not’ Factor | ‘(’ Expr ‘)’ | Var | Constant
            // Constant → ‘‘0’’ | ‘‘1’’
            // Var → id
        return Sequence(W0(), Term(), ZeroOrMore(Sequence(W0(), "or", W0(), Term(), W0(), swap(), push(new OrExpr((Expr)pop(), (Expr)pop())))));
            // the two terms in the or expression are (Expr)pop() and (Expr)pop()
				// swap was used to keep correct order, probably unnecessary
        // // TODO: longer code snippet
        // throw new ece351.util.Todo351Exception();
	}

    public Rule Term() {
		// For the grammar production Id, ensure that the Id does not match any of the keywords specified
		// in the rule, 'Keyword'
        // Follow grammar from manual:
            // Program → Formula+ $$
            // Fomula → Var ‘<=’ Expr ‘;’
            // Expr → Term (‘or’ Term)*
            // Term → Factor (‘and’ Factor)*
            // Factor → ‘not’ Factor | ‘(’ Expr ‘)’ | Var | Constant
            // Constant → ‘‘0’’ | ‘‘1’’
            // Var → id
        // return Sequence(W0(), Factor(), ZeroOrMore(Sequence(W0(), "and", W0(), Factor(), W0())));
        return Sequence(W0(), Factor(), ZeroOrMore(Sequence(W0(), "and", W0(), Factor(), W0(), swap(), push(new AndExpr((Expr)pop(), (Expr)pop())))));
			// very similar to the previously defined Expression()
            // there must be another Sequence() thing inside ZeroOrMore, since we have all of (‘and’ Term) under the *
        // // TODO: longer code snippet
        // throw new ece351.util.Todo351Exception();
	}

    public Rule Factor() {
		// For the grammar production Id, ensure that the Id does not match any of the keywords specified
		// in the rule, 'Keyword'
        // Follow grammar from manual:
            // Program → Formula+ $$
            // Fomula → Var ‘<=’ Expr ‘;’
            // Expr → Term (‘or’ Term)*
            // Term → Factor (‘and’ Factor)*
            // Factor → ‘not’ Factor | ‘(’ Expr ‘)’ | Var | Constant
            // Constant → ‘‘0’’ | ‘‘1’’
            // Var → id
        return Sequence(W0(), FirstOf(Sequence(W0(), "not", W0(), Factor(), W0(), push(new NotExpr((Expr)pop()))),
                                      Sequence(W0(), "(", W0(), Expression(), W0(),")", W0()),
                                      Sequence(Variable(), push(new VarExpr((String)pop()))), // need to change this to Sequence
                                      Constant()),
                                      W0());
            // for factor, we have one of the following:
                // ‘not’ Factor
                // ‘(’ Expr ‘)’
                // Var
                // Constant
        // // TODO: longer code snippet
        // throw new ece351.util.Todo351Exception();
	}

    public Rule Constant() {
		// For the grammar production Id, ensure that the Id does not match any of the keywords specified
		// in the rule, 'Keyword'
        // Follow grammar from manual:
            // Program → Formula+ $$
            // Fomula → Var ‘<=’ Expr ‘;’
            // Expr → Term (‘or’ Term)*
            // Term → Factor (‘and’ Factor)*
            // Factor → ‘not’ Factor | ‘(’ Expr ‘)’ | Var | Constant
            // Constant → ‘‘0’’ | ‘‘1’’
            // Var → id
        // return Sequence(W0(), FirstOf("'0'", "'1'"), W0());
        return FirstOf(Sequence("'0'", push(ConstantExpr.FalseExpr)), Sequence("'1'", push(ConstantExpr.TrueExpr)));
			// fixed, typo
        // // TODO: longer code snippet
        // throw new ece351.util.Todo351Exception();
	}

    public Rule Variable() {
		// For the grammar production Id, ensure that the Id does not match any of the keywords specified
		// in the rule, 'Keyword'
        // Follow grammar from manual:
            // Program → Formula+ $$
            // Fomula → Var ‘<=’ Expr ‘;’
            // Expr → Term (‘or’ Term)*
            // Term → Factor (‘and’ Factor)*
            // Factor → ‘not’ Factor | ‘(’ Expr ‘)’ | Var | Constant
            // Constant → ‘‘0’’ | ‘‘1’’
            // Var → id
        return Sequence(W0(), ID(), push(match()), W0());
        // // TODO: longer code snippet
        // throw new ece351.util.Todo351Exception();
	}

    public Rule ID() {
		// For the grammar production Id, ensure that the Id does not match any of the keywords specified
		// in the rule, 'Keyword'
        // Follow grammar from manual:
            // Program → Formula+ $$
            // Fomula → Var ‘<=’ Expr ‘;’
            // Expr → Term (‘or’ Term)*
            // Term → Factor (‘and’ Factor)*
            // Factor → ‘not’ Factor | ‘(’ Expr ‘)’ | Var | Constant
            // Constant → ‘‘0’’ | ‘‘1’’
            // Var → id
        return FirstOf(CharRange('A', 'Z'), CharRange('a', 'z')); // no Sequence needed, since these are values
        // // TODO: longer code snippet
        // throw new ece351.util.Todo351Exception();
	}
}
