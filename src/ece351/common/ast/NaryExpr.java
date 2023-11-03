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

package ece351.common.ast;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import org.parboiled.common.ImmutableList;

import ece351.util.Examinable;
import ece351.util.Examiner;

/**
 * An expression with multiple children. Must be commutative.
 */
public abstract class NaryExpr extends Expr {

	public final ImmutableList<Expr> children;

	public NaryExpr(final Expr... exprs) {
		Arrays.sort(exprs);
		ImmutableList<Expr> c = ImmutableList.of();
		for (final Expr e : exprs) {
			c = c.append(e);
		}
    	this.children = c;
	}
	
	public NaryExpr(final List<Expr> children) {
		final ArrayList<Expr> a = new ArrayList<Expr>(children);
		Collections.sort(a);
		this.children = ImmutableList.copyOf(a);
	}

	/**
	 * Each subclass must implement this factory method to return
	 * a new object of its own type. 
	 */
	public abstract NaryExpr newNaryExpr(final List<Expr> children);

	/**
	 * Construct a new NaryExpr (of the appropriate subtype) with 
	 * one extra child.
	 * @param e the child to append
	 * @return a new NaryExpr
	 */
	public NaryExpr append(final Expr e) {
		return newNaryExpr(children.append(e));
	}

	/**
	 * Construct a new NaryExpr (of the appropriate subtype) with 
	 * the extra children.
	 * @param list the children to append
	 * @return a new NaryExpr
	 */
	public NaryExpr appendAll(final List<Expr> list) {
		final List<Expr> a = new ArrayList<Expr>(children.size() + list.size());
		a.addAll(children);
		a.addAll(list);
		return newNaryExpr(a);
	}

	/**
	 * Check the representation invariants.
	 */
	public boolean repOk() {
		// programming sanity
		assert this.children != null;
		// should not have a single child: indicates a bug in simplification
		assert this.children.size() > 1 : "should have more than one child, probably a bug in simplification";
		// check that children is sorted
		int i = 0;
		for (int j = 1; j < this.children.size(); i++, j++) {
			final Expr x = this.children.get(i);
			assert x != null : "null children not allowed in NaryExpr";
			final Expr y = this.children.get(j);
			assert y != null : "null children not allowed in NaryExpr";
			assert x.compareTo(y) <= 0 : "NaryExpr.children must be sorted";
		}
        // Note: children might contain duplicates --- not checking for that
        // ... maybe should check for duplicate children ...
		// no problems found
		return true;
	}

	/**
	 * The name of the operator represented by the subclass.
	 * To be implemented by each subclass.
	 */
	public abstract String operator();
	
	/**
	 * The complementary operation: NaryAnd returns NaryOr, and vice versa.
	 */
	abstract protected Class<? extends NaryExpr> getThatClass();
	

	/**
     * e op x = e for absorbing element e and operator op.
     * @return
     */
	public abstract ConstantExpr getAbsorbingElement();

    /**
     * e op x = x for identity element e and operator op.
     * @return
     */
	public abstract ConstantExpr getIdentityElement();


	@Override 
    public final String toString() {
    	final StringBuilder b = new StringBuilder();
    	b.append("(");
    	int count = 0;
    	for (final Expr c : children) {
    		b.append(c);
    		if (++count  < children.size()) {
    			b.append(" ");
    			b.append(operator());
    			b.append(" ");
    		}
    		
    	}
    	b.append(")");
    	return b.toString();
    }


	@Override
	public final int hashCode() {
		return 17 + children.hashCode();
	}

	@Override
	public final boolean equals(final Object obj) {
		if (!(obj instanceof Examinable)) return false;
		return examine(Examiner.Equals, (Examinable)obj);
	}
	
	@Override
	public final boolean isomorphic(final Examinable obj) {
		return examine(Examiner.Isomorphic, obj);
	}
	
	private boolean examine(final Examiner e, final Examinable obj) {
		// basics - don't modify
		if (obj == null) return false;
		if (!this.getClass().equals(obj.getClass())) return false;
		final NaryExpr that = (NaryExpr) obj;
		
		// if the number of children are different, consider them not equivalent
		if(this.children.size() != that.children.size()){
			return false;
		}

		// since the n-ary expressions have the same number of children and they are sorted, just iterate and check
		// iterate through all children:
		for (int k = 0; k <= this.children.size() - 1; k++){
			// check if each child is the same, if they are not, return false
			if(!(this.children.get(k).equals(that.children.get(k)))){
				// supposed to be sorted, but might not be (because repOk might not pass)
				// if they are not the same elements in the same order return false
				return false;
			}
		}

		// no significant differences found, return true
		return true;
		// // TODO: longer code snippet
		// throw new ece351.util.Todo351Exception();
	}

	
	@Override
	protected final Expr simplifyOnce() {
		assert repOk();
		final Expr result = 
				simplifyChildren().
				mergeGrandchildren().
				foldIdentityElements().
				foldAbsorbingElements().
				foldComplements().
				removeDuplicates().
				simpleAbsorption().
				subsetAbsorption().
				singletonify();
		assert result.repOk();
		return result;
	}
	
	/**
	 * Call simplify() on each of the children.
	 */
	private NaryExpr simplifyChildren() {
		// note: we do not assert repOk() here because the rep might not be ok
		// the result might contain duplicate children, and the children
		// might be out of order

		// create array to store new Expr types with simplified children:
		ArrayList<Expr> simplifiedChildren = new ArrayList<>();

		// iterate through all children, simplify them and add them to simplifiedChildren:
		for (int k = 0; k <= this.children.size() - 1; k++){
			Expr simpleChild = this.children.get(k).simplify();
			simplifiedChildren.add(simpleChild);
		}

		// return the simplified children, use newNaryExpr to match return type of NaryExpr:
		return newNaryExpr(simplifiedChildren);

		// return this; // TODO: replace this stub
	}

	
	private NaryExpr mergeGrandchildren() { // -- DEBUG
		// extract children to merge using filter (because they are the same type as us)
		// if no children to merge, then return this (i.e., no change)
		if(this.children.isEmpty()){
			return this;
		}

		// use filter to get the other children, which will be kept in the result unchanged
		NaryExpr filteredChildren = filter(this.getClass(), true);
			// - this removes children that are of different type/class than the current expression
		ArrayList<Expr> copyChildren = new ArrayList<Expr>(this.children);

		// Before iterating, check if any children are left, if not, return as is
		if(filteredChildren.children.isEmpty()){
			return this;
		}

		// merge in the grandchildren
		// iterate through all children, remove them and add the grandchildren
		for (int k = 0; k <= filteredChildren.children.size() - 1; k++){
			// get the kth child's children (grandchildren):
			NaryExpr child_k = (NaryExpr) filteredChildren.children.get(k);

			// remove the children and add the grandchildren:
			copyChildren.remove(child_k);
			copyChildren.addAll(child_k.children);
		}

		NaryExpr grandchildren = newNaryExpr(copyChildren);
		assert grandchildren.repOk(); //this operation should always leave the AST in a legal state
		return grandchildren;
		// return this; // TODO: replace this stub
	}

    private NaryExpr foldIdentityElements() { // -- DEBUG
    	// if we have only one child stop now and return self
		if(this.children.size() == 1){
			return this; // TODO: replace this stub -- this part seems fine
			// do not assert repOk(): this fold might leave the AST in an illegal state (with only one child)
		}else{
			// we have multiple children, remove the identity elements
    		// all children were identity elements, so now our working list is empty
    		// return a new list with a single identity element

			// first, create the list to return:
			ArrayList<Expr> tempList = new ArrayList<>();
			tempList.add(this.getIdentityElement());

			// remove the identity elements:
			NaryExpr returnList = removeAll(tempList, Examiner.Equals);

			if(!returnList.children.isEmpty()){
				// normal return
				return returnList;
			}else{
				// if it is empty, add the removed identity element
				return returnList.append(this.getIdentityElement());
			}
		}
    }

    private NaryExpr foldAbsorbingElements() {
		// absorbing element: 0.x=0 and 1+x=1
			// absorbing element is present: return it
			// not so fast! what is the return type of this method? why does it have to be that way?

		// check if absorbing element is present:
		if(contains(this.getAbsorbingElement(), Examiner.Equals)){
			ArrayList<Expr> tempList = new ArrayList<>();
			tempList.add(this.getAbsorbingElement());
			NaryExpr returnList = newNaryExpr(tempList);
			return returnList;
		}else{
			// no absorbing element present, do nothing
			return this; // TODO: replace this stub
			// do not assert repOk(): this fold might leave the AST in an illegal state (with only one child)
		}
	}

	private NaryExpr foldComplements() {
		// collapse complements
		// !x . x . ... = 0 and !x + x + ... = 1
		// x op !x = absorbing element

		//Initialize:
		NotExpr notExprTerm = new NotExpr();
		// - for detecting ! terms
		NaryExpr filteredList = this.filter(notExprTerm.getClass(), true);

		// find all negations
		for(int k = 0; k <= filteredList.children.size() - 1; k++){
			// for each negation, see if we find its complement
			NotExpr currTerm = (NotExpr) filteredList.children.get(k);
			Expr tempVal = currTerm.expr;
			
			// found matching negation and its complement
			// - might have to separate the currTerm.expr into its own separate line
			if(this.contains(tempVal, Examiner.Equals)){
				ArrayList<Expr> tempList = new ArrayList<>();
				tempList.add(this.getAbsorbingElement());
				NaryExpr returnVal = newNaryExpr(tempList);
				return returnVal;
			}
			// return absorbing element
			// no complements to fold

		}
		return this; // TODO: replace this stub -- leave this for now
    	// do not assert repOk(): this fold might leave the AST in an illegal state (with only one child)
	}

	private NaryExpr removeDuplicates() {
		// remove duplicate children: x.x=x and x+x=x
		// since children are sorted this is fairly easy

		for(int k = 0; k<= this.children.size() - 2; k++){
			// store the initial child, assume it is unique and check to see if there are multiple instances of it
			Expr child_k = this.children.get(k);
			for(int j = k + 1; j <= this.children.size() - 1; j++){
				Expr child_j = this.children.get(j);
				if(child_k.equals(child_j)){
					ArrayList<Expr> tempList = new ArrayList<>();
					tempList.add(child_k);

					// Removing ALL instances of the duplicate found:
					NaryExpr returnList = removeAll(tempList, Examiner.Equals);

					// Before returning, add at least one instance of the duplicate:
					return returnList.append(child_k);
					// removed some duplicates
				}
			}
		}

		// no changes, no duplicates found, return as is:
		return this; // TODO: replace this stub
    	// do not assert repOk(): this fold might leave the AST in an illegal state (with only one child)
	}

	private NaryExpr simpleAbsorption() {
		// (x.y) + x ... = x ...
		// check if there are any conjunctions that can be removed

		// Initialize variables:
		NaryExpr filteredListTrue = this.filter(this.getThatClass(), true);
		NaryExpr filteredListFalse = this.filter(this.getThatClass(), false);
		ArrayList<NaryExpr> conjunctions = new ArrayList<>();
		// - to store the conjunctions we want to remove

		// First, iterate through filteredListTrue and populate conjunctions list
		for(Expr child : filteredListTrue.children){ // trying new notation for more readable code // debug later
			conjunctions.add((NaryExpr) child);
		}

		// Then, check for conjunctions that are removable
		for(NaryExpr conjunction: conjunctions){
			for(Expr conjunctionChild : conjunction.children){
				for(Expr filteredChild : filteredListFalse.children){
					if(filteredChild.equals(conjunctionChild)){
						// Confirmed that the conjunction should be removed
						ArrayList<Expr> returnVal = new ArrayList<>();
						returnVal.add(conjunction);
						return this.removeAll(returnVal, Examiner.Equals); // debug this
					}
				}
			}
		}

		return this; // TODO: replace this stub
    	// do not assert repOk(): this operation might leave the AST in an illegal state (with only one child)
	}

	private NaryExpr subsetAbsorption() {
		// check if there are any conjunctions that are supersets of others
		// e.g., ( a . b . c ) + ( a . b ) = a . b

		// Initialize variables:
		NaryExpr filteredList = this.filter(this.getThatClass(), true);
		ArrayList<NaryExpr> tempList = new ArrayList<>();
		ArrayList<Expr> subsetsToRemove = new ArrayList<>();
		// - to store the subsets we want to remove

		// Populate tempList 
		for(Expr child : filteredList.children){ // same for loop setup as in simpleAbsorption
			NaryExpr currExp = (NaryExpr) child;
			tempList.add(currExp);
		}

		// Iterate through the tempList to find subsets
		for(NaryExpr currExp : tempList){ //fixed
			// Keep toRemove true until we have confirmed otherwise using the below loops
			boolean toRemove = true;

			// Compare currExp to other items in tempList
			for(NaryExpr currExp2 : tempList){
				// We only care if currExp and currExp2 are not the same:
				if(currExp != currExp2){
					// Determine if toRemove should be changed to false:
					for(Expr child : (currExp.children)){
						if(!currExp2.contains(child, Examiner.Equals)){
							toRemove = false;
						}
					}

					// Remove if toRemove is still true:
					if(toRemove){
						subsetsToRemove.add(currExp2);
					}
				}
			}
		}

    	// do not assert repOk(): this operation might leave the AST in an illegal state (with only one child)
		// Remove all marked items (in subsetsToRemove), and return:
		return this.removeAll(subsetsToRemove, Examiner.Equals);
		// return this; // TODO: replace this stub
	}

	/**
	 * If there is only one child, return it (the containing NaryExpr is unnecessary).
	 */
	private Expr singletonify() {
		// if we have only one child, return it
		if(this.children.size() == 1){
			return this.children.get(0);
		}
		// having only one child is an illegal state for an NaryExpr
			// multiple children; nothing to do; return self
		return this; // TODO: replace this stub
	}

	/**
	 * Return a new NaryExpr with only the children of a certain type, 
	 * or excluding children of a certain type.
	 * @param filter
	 * @param shouldMatchFilter
	 * @return
	 */
	public final NaryExpr filter(final Class<? extends Expr> filter, final boolean shouldMatchFilter) {
		ImmutableList<Expr> l = ImmutableList.of();
		for (final Expr child : children) {
			if (child.getClass().equals(filter)) {
				if (shouldMatchFilter) {
					l = l.append(child);
				}
			} else {
				if (!shouldMatchFilter) {
					l = l.append(child);
				}
			}
		}
		return newNaryExpr(l);
	}

	public final NaryExpr filter(final Expr filter, final Examiner examiner, final boolean shouldMatchFilter) {
		ImmutableList<Expr> l = ImmutableList.of();
		for (final Expr child : children) {
			if (examiner.examine(child, filter)) {
				if (shouldMatchFilter) {
					l = l.append(child);
				}
			} else {
				if (!shouldMatchFilter) {
					l = l.append(child);
				}
			}
		}
		return newNaryExpr(l);
	}

	public final NaryExpr removeAll(final List<Expr> toRemove, final Examiner examiner) {
		NaryExpr result = this;
		for (final Expr e : toRemove) {
			result = result.filter(e, examiner, false);
		}
		return result;
	}

	public final boolean contains(final Expr expr, final Examiner examiner) {
		for (final Expr child : children) {
			if (examiner.examine(child, expr)) {
				return true;
			}
		}
		return false;
	}

}
