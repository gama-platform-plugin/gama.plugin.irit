/*******************************************************************************************************
 *
 * PushStatement.java, in plugin gama.plugin.switchproject.gama.switchproject, is part of the source code of the GAMA modeling and
 * simulation platform (v. 1.8.1)
 *
 * (c) 2007-2020 UMI 209 UMMISCO IRD/SU & Partners
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 *
 ********************************************************************************************************/

package gama.plugin.switchproject.gaml.statements;

import gama.plugin.switchproject.gama.common.interfaces.IKeywordIrit;
import gama.plugin.switchproject.gama.util.deque.GamaDeque;
import gama.annotations.symbol;
import gama.annotations.inside;
import gama.annotations.doc;
import gama.annotations.facet;
import gama.annotations.usage;
import gama.annotations.example;
import gama.annotations.facets;

import gama.annotations.support.ISymbolKind;

import gama.annotations.constants.IKeyword;
import gama.api.compilation.descriptions.IDescription;
import gama.api.exceptions.GamaRuntimeException;
import gama.api.gaml.expressions.IExpression;
import gama.api.gaml.statements.AbstractStatement;
import gama.api.gaml.types.IType;
import gama.api.runtime.scope.IScope;

/**
 * Push statement used by Queue and Stack types
 * 
 * @author Jean-François Erdelyi
 */
@symbol(name = IKeywordIrit.PUSH, kind = ISymbolKind.SINGLE_STATEMENT, with_sequence = false, concept = {
		IKeywordIrit.STACK, IKeywordIrit.QUEUE })
@inside(kinds = { ISymbolKind.BEHAVIOR, ISymbolKind.SEQUENCE_STATEMENT, ISymbolKind.LAYER }, symbols = IKeyword.CHART)
@doc(value = "Allows to add, i.e. to insert, a new element in a deque", usages = {
		@usage(value = "The new element can be added either at the end of the deque", examples = {
				@example(value = "push expr to: stack;		// Add at the end", isExecutable = false),
				@example(value = "push expr to: queue;		// Add at the end", isExecutable = false) }) })
@facets(value = {
		@facet(name = IKeyword.ITEM, type = IType.NONE, optional = false, doc = {
				@doc("any expression to add in the deque") }),
		@facet(name = IKeyword.TO, type = { IKeywordIrit.STACK_TYPE,
				IKeywordIrit.QUEUE_TYPE }, optional = false, doc = {
						@doc("the stack or queue") }), }, omissible = IKeyword.ITEM)
@SuppressWarnings({ "rawtypes", "unchecked" })
public class PushStatement extends AbstractStatement {

	// ############################################
	// Attributs

	/**
	 * Expressions Item
	 */
	final IExpression itemExp;

	/**
	 * Expressions To
	 */
	final IExpression toExp;

	// ############################################
	// Constructor

	/**
	 * Push constructor
	 */
	public PushStatement(IDescription desc) {
		super(desc);

		// Get facets
		itemExp = getFacet(IKeyword.ITEM);
		toExp = getFacet(IKeyword.TO);

		// Save data from facets
		String toName = (toExp != null) ? toExp.literalValue() : null;

		// Set name
		setName("push to " + toName);
	}

	// ############################################
	// Methods

	/**
	 * Return object casted into GamaDeque if possible, null otherwise
	 */
	private GamaDeque identifyContainer(final IScope scope, final IExpression toExp) throws GamaRuntimeException {
		final Object cont = toExp.value(scope);
		if (cont instanceof GamaDeque) {
			return (GamaDeque) cont;
		}
		return null;
	}

	/**
	 * Main fonction
	 */
	@Override
	protected Object privateExecuteIn(IScope scope) throws GamaRuntimeException {
		// Cast dequeu if possible
		GamaDeque to = identifyContainer(scope, toExp);
		// Get value form facet
		Object data = itemExp.value(scope);

		// Check if deque and data are not null and insert data
		if (to != null && data != null) {
			to.addLast(data);
		}
		return to;
	}

}
