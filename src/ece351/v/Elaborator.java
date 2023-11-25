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

package ece351.v;

import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.stream.Collectors;

import org.parboiled.common.ImmutableList;

import ece351.common.ast.AndExpr;
import ece351.common.ast.AssignmentStatement;
import ece351.common.ast.ConstantExpr;
import ece351.common.ast.EqualExpr;
import ece351.common.ast.Expr;
import ece351.common.ast.NAndExpr;
import ece351.common.ast.NOrExpr;
import ece351.common.ast.NaryAndExpr;
import ece351.common.ast.NaryOrExpr;
import ece351.common.ast.NotExpr;
import ece351.common.ast.OrExpr;
import ece351.common.ast.Statement;
import ece351.common.ast.VarExpr;
import ece351.common.ast.XNOrExpr;
import ece351.common.ast.XOrExpr;
import ece351.common.visitor.PostOrderExprVisitor;
import ece351.util.CommandLine;
import ece351.v.ast.Architecture;
import ece351.v.ast.Component;
import ece351.v.ast.DesignUnit;
import ece351.v.ast.IfElseStatement;
import ece351.v.ast.Process;
import ece351.v.ast.VProgram;

/**
 * Inlines logic in components to architecture body.
 */
public final class Elaborator extends PostOrderExprVisitor {

	private final Map<String, String> current_map = new LinkedHashMap<String, String>();
	
	public static void main(String[] args) {
		System.out.println(elaborate(args));
	}
	
	public static VProgram elaborate(final String[] args) {
		return elaborate(new CommandLine(args));
	}
	
	public static VProgram elaborate(final CommandLine c) {
        final VProgram program = DeSugarer.desugar(c);
        return elaborate(program);
	}
	
	public static VProgram elaborate(final VProgram program) {
		final Elaborator e = new Elaborator();
		return e.elaborateit(program);
	}

	private VProgram elaborateit(final VProgram root) {

		// our ASTs are immutable. so we cannot mutate root.
		// we need to construct a new AST that will be the return value.
		// it will be like the input (root), but different.
		VProgram result = new VProgram();
		
		int compCount = 1; // -- failed tests 12 & 14 when set to 0?
		// iterate over all of the designUnits in root.
		for(DesignUnit du : root.designUnits){
			// for each one, construct a new architecture. -- given code
			Architecture a = du.arch.varyComponents(ImmutableList.<Component>of());
			// this gives us a copy of the architecture with an empty list of components.

			// now we can build up this Architecture with new components.
			// In the elaborator, an architectures list of signals, and set of statements may change (grow)
			// iterate through all components first:
			for(Component c : du.arch.components){
				int id = 0; // for mapping
				DesignUnit preexistingDU = result.designUnits.stream().filter(duVar -> duVar.entity.identifier.equals(c.entityName)).collect(Collectors.toList()).get(0);
				//populate dictionary/map?
				//add input signals, map to ports
				for(String in : preexistingDU.entity.input){
					current_map.put(in, c.signalList.get(id));
					id++;
				}

				//add output signals, map to ports
				for(String out : preexistingDU.entity.output){
					current_map.put(out, c.signalList.get(id));
					id++;
				}

				//add local signals, add to signal list of current designUnit --> map and add to new architecture
				for(String loc_sig : preexistingDU.arch.signals){
					// follow this format: comp3_e
					current_map.put(loc_sig, "comp" + compCount + "_" + loc_sig);
					a = a.appendSignal("comp" + compCount + "_" + loc_sig);
					// a.appendSignal("comp" + compCount + "_" + loc_sig); //-- debugged
				}

				//loop through the statements in the architecture body
				for(Statement s : preexistingDU.arch.statements){
					// make the appropriate variable substitutions for signal assignment statements
					if(s.getClass() == AssignmentStatement.class){ // if it is an assignment statment
						// i.e., call changeStatementVars
						s = changeStatementVars((AssignmentStatement)s);
					} // make the appropriate variable substitutions for processes (sensitivity list, if/else body statements)
					else if(s.getClass() == Process.class){
						// i.e., call expandProcessComponent
						s = expandProcessComponent((Process)s);
					}
					// Remember to update architecture
					a = a.appendStatement(s);
					// a.appendStatement(s); //-- debugged
				}
				// update compCount here
				compCount++;
			}
			// append this new architecture to result
			result = result.append(new DesignUnit(a, du.entity));
		}
		
		// // TODO: longer code snippet
		// throw new ece351.util.Todo351Exception();

		assert result.repOk();
		return result;
	}
	
	// you do not have to use these helper methods; we found them useful though
	private Process expandProcessComponent(final Process process){
		// create sensitivityList because process requires a sensitivityList:
		ImmutableList<String> sensitivityList = ImmutableList.of();
		for (String i : process.sensitivityList){
			// must reassign b/c of immutable list behaviour -- debugged
			sensitivityList = sensitivityList.append(current_map.get(i));
		}
		
		// create statements because process requires a statements:
		ImmutableList<Statement> statements = ImmutableList.of();
		for (Statement statement : process.sequentialStatements){
			// depending on the statement type/class, append using one of: 
				// changeStatementVars, expandProcessComponent, or changeIfVars
			if (statement.getClass() == AssignmentStatement.class){
				statements = statements.append(changeStatementVars((AssignmentStatement)statement));
			}else if(statement.getClass() == Process.class){
				statements = statements.append(expandProcessComponent((Process)statement));
			}else if(statement.getClass() == IfElseStatement.class){
				statements = statements.append(changeIfVars((IfElseStatement)statement));
			}
		}
		return new Process(statements, sensitivityList);
		// // TODO: longer code snippet
		// throw new ece351.util.Todo351Exception();
	}
	
	// you do not have to use these helper methods; we found them useful though
	private  IfElseStatement changeIfVars(final IfElseStatement s){
		// in order to build IfElseStatement, we need:
			// 1. elseBody --> ImmutableList<AssignmentStatement>
			// 2. ifBody --> ImmutableList<AssignmentStatement>
			// 3. cond --> Expr
		// Build elseBody --> ImmutableList<AssignmentStatement>
		ImmutableList<AssignmentStatement> elseBody = ImmutableList.of();
		for(AssignmentStatement elseStatement : s.elseBody){
			elseBody = elseBody.append(changeStatementVars(elseStatement));
		}

		// Build ifBody --> ImmutableList<AssignmentStatement>
		ImmutableList<AssignmentStatement> ifBody = ImmutableList.of();
		for(AssignmentStatement ifStatement : s.ifBody){
			ifBody = ifBody.append(changeStatementVars(ifStatement));
		}

		// Build cond --> Expr
		Expr cond = traverseExpr(s.condition);

		return new IfElseStatement(elseBody, ifBody, cond);
		// // TODO: longer code snippet
		// throw new ece351.util.Todo351Exception();
	}

	// you do not have to use these helper methods; we found them useful though
	private AssignmentStatement changeStatementVars(final AssignmentStatement s){
		// must use traverse, since it will figure out if it is Nary, Binary or Unary
		return new AssignmentStatement(current_map.get(s.outputVar.identifier), traverseExpr(s.expr));
		// // TODO: short code snippet
		// throw new ece351.util.Todo351Exception();
	}
	
	
	@Override
	public Expr visitVar(VarExpr e){
		// TODO replace/substitute the variable found in the map
		return new VarExpr(current_map.get(e.identifier));
		// // TODO: short code snippet
		// throw new ece351.util.Todo351Exception();
	}
	
	// do not rewrite these parts of the AST
	@Override public Expr visitConstant(ConstantExpr e) { return e; }
	@Override public Expr visitNot(NotExpr e) { return e; }
	@Override public Expr visitAnd(AndExpr e) { return e; }
	@Override public Expr visitOr(OrExpr e) { return e; }
	@Override public Expr visitXOr(XOrExpr e) { return e; }
	@Override public Expr visitEqual(EqualExpr e) { return e; }
	@Override public Expr visitNAnd(NAndExpr e) { return e; }
	@Override public Expr visitNOr(NOrExpr e) { return e; }
	@Override public Expr visitXNOr(XNOrExpr e) { return e; }
	@Override public Expr visitNaryAnd(NaryAndExpr e) { return e; }
	@Override public Expr visitNaryOr(NaryOrExpr e) { return e; }
}