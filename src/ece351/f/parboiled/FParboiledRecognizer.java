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

import ece351.common.ast.Constants;
import ece351.util.CommandLine;

//Parboiled requires that this class not be final
public /*final*/ class FParboiledRecognizer extends FBase implements Constants {

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
	
	public static void main(final String... args) {
		final CommandLine c = new CommandLine(args);
    	process(FParboiledRecognizer.class, c.readInputSpec());
    }

	@Override
	public Rule Program() {
		// STUB: return NOTHING; // TODO: replace this stub ?
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
        return Sequence(OneOrMore(Formula()), EOI);
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
        return Sequence(W0(), Variable(), W0(), "<=", W0(), Expression(), W0(), ";", W0());
            // this has the exact same structure as Waveform() in WParboiledRecognizer.java
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
        return Sequence(W0(), Term(), ZeroOrMore(Sequence(W0(), "or", W0(), Term(), W0())));
            // there must be another Sequence() thing inside ZeroOrMore, since we have all of (‘or’ Term) under the *
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
        return Sequence(W0(), Factor(), ZeroOrMore(Sequence(W0(), "and", W0(), Factor(), W0())));
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
        return Sequence(W0(), FirstOf(Sequence(W0(), "not", W0(), Factor(), W0()),
                                      Sequence(W0(), "(", W0(), Expression(), W0(),")", W0()),
                                      Variable(),
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
        return FirstOf("'0'", "'1'");
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
        return Sequence(W0(), ID(), W0());
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
        return FirstOf(CharRange('A', 'Z'), CharRange('a', 'z'));
        // // TODO: longer code snippet
        // throw new ece351.util.Todo351Exception();
	}
}
