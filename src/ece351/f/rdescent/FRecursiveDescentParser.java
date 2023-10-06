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

package ece351.f.rdescent;

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
import ece351.util.Lexer;



public final class FRecursiveDescentParser implements Constants {
   
	// instance variables
	private final Lexer lexer;

    public FRecursiveDescentParser(String... args) {
    	final CommandLine c = new CommandLine(args);
        lexer = new Lexer(c.readInputSpec());
    }
    
    public FRecursiveDescentParser(final Lexer lexer) {
        this.lexer = lexer;
    }

    public static void main(final String arg) {
    	main(new String[]{arg});
    }
    
    public static void main(final String[] args) {
    	parse(args);
    }

    public static FProgram parse(final String... args) {
        final FRecursiveDescentParser p = new FRecursiveDescentParser(args);
        return p.parse();
    }
    
    public FProgram parse() {
        return program();
    }

    FProgram program() {
    	FProgram fp = new FProgram();
    	do {
        	fp = fp.append(formula());
        } while (!lexer.inspectEOF());
        lexer.consumeEOF();
        assert fp.repOk();
        return fp;
    }

    AssignmentStatement formula() {
        final VarExpr var = var();
        lexer.consume("<=");
        final Expr expr = expr();
        lexer.consume(";");
        return new AssignmentStatement(var, expr);
    }
    
    // Implement the below 5 functions based on grammar in figure 3.1:
    Expr expr() { 
        // Expr → Term (‘or’ Term)*
        Expr rtnVal = term();
        while (lexer.inspect(OR)){ // while loop since there is a *
            // after finding OR, consume it
            lexer.consume(OR);
            Expr parsedTerm = term();
            rtnVal = new OrExpr(rtnVal, parsedTerm);
        }
        // throw new ece351.util.Todo351Exception();
        return rtnVal;
    } // TODO // TODO: replace this stub

    Expr term() { 
        //Term → Factor (‘and’ Factor)*
        Expr rtnVal = factor();
        while (lexer.inspect(AND)){ // while loop since there is a *
            // after finding AND, consume it
            lexer.consume(AND);
            Expr parsedTerm = factor();
            rtnVal = new AndExpr(rtnVal, parsedTerm);
        }
        // throw new ece351.util.Todo351Exception();
        return rtnVal;
    } // TODO // TODO: replace this stub

    Expr factor() { 
        // Factor → ‘not’ Factor | ‘(’ Expr ‘)’ | Var | Constant
        Expr rtnVal;
        if (lexer.inspect(NOT)){
            // after finding NOT, consume it
            lexer.consume(NOT);
            Expr parsedVal = factor();
            rtnVal = new NotExpr(parsedVal);
            return rtnVal;
        } else if (lexer.inspect("(")){
            lexer.consume("(");
            rtnVal = expr();
            if(lexer.inspect(")")){lexer.consume(")");}; // check again for peace of mind
            return rtnVal;
        } else { // not sure about this since there is | btw Var and Constant
            // Distinguish Var from Constant by using checked for "'" string
            if(lexer.inspect("'")){
                rtnVal = constant();
                return rtnVal;
            }else{
                rtnVal = var();
                return rtnVal;
            }
        }
    // throw new ece351.util.Todo351Exception();
    } // TODO // TODO: replace this stub
    
    ConstantExpr constant() { // remember to check for '' single quotes!
        // Constant → ‘‘0’’ | ‘‘1’’
        if (lexer.inspect("'")){
            lexer.consume("'");
            if (lexer.inspect("0")){
                lexer.consume("0");
                lexer.consume("'");
                return ConstantExpr.FalseExpr;
            } else{
                lexer.consume("1");
                lexer.consume("'");
                return ConstantExpr.TrueExpr;
            }
        }else{
            return ConstantExpr.FalseExpr;
        }
    // throw new ece351.util.Todo351Exception();
    } // TODO // TODO: replace this stub


    // ConstantExpr constant() { 
    // 	String input = "";
    // 	if(lexer.inspect("'")){
    //         lexer.consume("'");
    //     }
	// 	if(lexer.inspect("0","1")){
    // 		input = lexer.consume("1","0");
    // 		lexer.consume("'");
    		
    // 	}
	// 	if (input == "1"){
	// 		return ConstantExpr.TrueExpr;
	// 	}
	// 	else{
	// 		return ConstantExpr.FalseExpr;
	// 	}
    // } // TODO // TODO: replace this stub

    VarExpr var() { 
        // Var → id
        String rtnString = "";
        while(lexer.inspectID()){ // must check for ID first
            rtnString += lexer.consumeID(); // Build the returned string
        }
        return new VarExpr(rtnString);
    // throw new ece351.util.Todo351Exception();
    } // TODO // TODO: replace this stub

    // helper functions
    private boolean peekConstant() {
        return lexer.inspect("'");
    }

}

