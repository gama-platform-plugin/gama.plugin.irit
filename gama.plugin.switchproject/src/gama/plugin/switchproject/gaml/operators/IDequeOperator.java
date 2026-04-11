/*******************************************************************************************************
 *
 * IDequeOperator.java, in plugin gama.plugin.switchproject.gama.switchproject, is part of the source code of the GAMA modeling and simulation
 * platform (v. 1.8.1)
 *
 * (c) 2007-2020 UMI 209 UMMISCO IRD/SU & Partners
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 *
 ********************************************************************************************************/

package gama.plugin.switchproject.gaml.operators;

import gama.plugin.switchproject.gama.common.interfaces.IKeywordIrit;

import gama.annotations.doc;
import gama.annotations.usage;
import gama.annotations.support.ITypeProvider;
import gama.api.exceptions.GamaRuntimeException;
import gama.api.runtime.scope.IScope;
import gama.annotations.example;
import gama.annotations.operator;

/**
 * Deque interface used for Queue and Stack types
 * 
 * @author Jean-François Erdelyi
 */
public interface IDequeOperator<T> {
	/**
	 * Pop operator must be redefined in queue and stack classes
	 */
	@operator(value = "pop", can_be_const = true, category = { IKeywordIrit.QUEUE_OPERATOR,
			IKeywordIrit.STACK_OPERATOR }, type = ITypeProvider.CONTENT_TYPE_AT_INDEX
					+ 1, concept = { IKeywordIrit.QUEUE, IKeywordIrit.STACK })
	@doc(value = "retrieves and removes the first available element of this container, or returns null if this container is empty", masterDoc = true, comment = "the pop operator behavior depends on the nature of the operand", usages = {
			@usage(value = "pop return and remove the first object of the container", examples = {
					@example(value = "pop(stack([1, 2]))", equals = "2"),
					@example(value = "pop(queue([1, 2]))", equals = "1") }) })
	T pop(IScope scope) throws GamaRuntimeException;
}
