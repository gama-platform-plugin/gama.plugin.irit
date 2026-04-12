/*******************************************************************************************************
 *
 * GamaQueue.java, in plugin gama.plugin.switchproject.gama.switchproject, is part of the source code of the GAMA modeling and simulation
 * platform (v. 1.8.1)
 *
 * (c) 2007-2020 UMI 209 UMMISCO IRD/SU & Partners
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 *
 ********************************************************************************************************/

package gama.plugin.switchproject.gama.util.deque;

import java.util.Collection;

import gama.plugin.switchproject.gaml.operators.IDequeOperator;
import gama.plugin.switchproject.gaml.types.TypesIrit;
import gama.api.gaml.types.IType;
import gama.api.runtime.scope.IScope;

/**
 * Queue type used by GAML type
 * 
 * @author Jean-François Erdelyi
 */
public class GamaQueue<T> extends GamaDeque<T> implements IDequeOperator<Object> {

	// ############################################
	// Attributes

	/**
	 * The serializable class does not declare a static final serialVersionUID field
	 * of type long
	 */
	private static final long serialVersionUID = 1L;

	// ############################################
	// Constructors

	/**
	 * Constructor
	 */
	public GamaQueue(IType<?> contentsType) {
		super(TypesIrit.QUEUE.of(contentsType));
	}

	/**
	 * Constructor with data
	 */
	public GamaQueue(IType<?> contentsType, T[] values) {
		super(TypesIrit.QUEUE.of(contentsType), values);
	}

	/**
	 * Constructor with data
	 */
	public GamaQueue(IType<?> contentsType, Collection<T> values) {
		super(TypesIrit.QUEUE.of(contentsType), values);
	}

	// ############################################
	// Override : IDequeOperator

	/**
	 * Pop data from queue (FIFO)
	 */
	@Override
	public T pop(IScope scope) {
		return pollFirst();
	}
}
