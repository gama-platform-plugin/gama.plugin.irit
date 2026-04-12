/*******************************************************************************************************
 *
 * GamaQueueType.java, in plugin gama.plugin.switchproject.gama.switchproject, is part of the source code of the GAMA modeling and
 * simulation platform (v. 1.8.1)
 *
 * (c) 2007-2020 UMI 209 UMMISCO IRD/SU & Partners
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 *
 ********************************************************************************************************/

package gama.plugin.switchproject.gaml.types;

import java.awt.Color;
import java.util.Arrays;
import java.util.Collection;
import java.util.regex.Pattern;

import gama.plugin.switchproject.gama.common.interfaces.IKeywordIrit;
import gama.plugin.switchproject.gama.util.deque.GamaQueue;

import gama.annotations.doc;
import gama.annotations.type;
import gama.api.exceptions.GamaRuntimeException;
import gama.api.gaml.expressions.IExpression;
import gama.api.gaml.types.GamaContainerType;
import gama.api.gaml.types.IType;
import gama.api.gaml.types.ITypesManager;
import gama.annotations.support.IConcept;
import gama.api.gaml.types.Types;
import gama.api.runtime.scope.IScope;
import gama.api.types.date.IDate;
import gama.api.types.geometry.IPoint;
import gama.api.types.misc.IContainer;
import gama.annotations.support.ISymbolKind;


/**
 * Queue type in GAML
 * 
 * @author Jean-François Erdelyi
 */
@SuppressWarnings("rawtypes")
@type(name = IKeywordIrit.QUEUE, id = IKeywordIrit.QUEUE_TYPE, wraps = {
		GamaQueue.class }, kind = ISymbolKind.CONTAINER, doc = {
				@doc("Queue") }, concept = { IConcept.TYPE, IConcept.CONTAINER, IKeywordIrit.QUEUE })
public class GamaQueueType extends GamaContainerType<GamaQueue> {

	// ############################################
	// Constructor

	/**
	 * TEMPORY CONSTRUCTOR TODO REMOVE THIS
	 */
	public GamaQueueType(final ITypesManager typesManager) {
		super(typesManager);
		id = IKeywordIrit.QUEUE_TYPE;
		name = IKeywordIrit.QUEUE;
		parent = null;
		plugin = "gama.plugin.switchproject.gama.switchproject";
		support = GamaQueue.class;
		varKind = ISymbolKind.CONTAINER;
	}
	
	public GamaQueueType() {
		super(null);	
	}


	// ############################################
	// Methods

	/**
	 * Cast data into GamaQueue
	 */
	@Override
	public GamaQueue cast(final IScope scope, final Object obj, final Object param, final IType keyType,
			final IType contentsType, final boolean copy) throws GamaRuntimeException {
		return staticCast(scope, obj, contentsType, copy);
	}

	/**
	 * Static cast definition
	 */
	@SuppressWarnings("unchecked")
	public static GamaQueue staticCast(final IScope scope, final Object obj, final IType<?> ct, final boolean copy)
			throws GamaRuntimeException {
		final IType<?> contentsType = ct == null ? Types.NO_TYPE : ct;

		if (obj == null) {
			return new GamaQueue(contentsType);
		}

		if (obj instanceof IDate) {
			return new GamaQueue(contentsType, ((IDate) obj).listValue(scope, contentsType));
		}

		if (obj instanceof IContainer) {
			return new GamaQueue(contentsType, ((IContainer) obj).listValue(scope, contentsType, true));
		}

		if (obj instanceof Collection) {
			return new GamaQueue(contentsType, (Collection) obj);
		}

		if (obj instanceof Color) {
			final Color c = (Color) obj;
			return new GamaQueue(contentsType, new Integer[] { c.getRed(), c.getGreen(), c.getBlue() });
		}

		if (obj instanceof IPoint) {
			final IPoint point = (IPoint) obj;
			return new GamaQueue(contentsType, new Double[] { point.getX(), point.getY() });
		}

		if (obj instanceof String) {
			final String ponctuation = "\\p{Punct}";
			Pattern TOKENIZER_PATTERN = Pattern.compile(ponctuation);
			return new GamaQueue(contentsType, Arrays.asList(TOKENIZER_PATTERN.split((String)obj)));
		}

		return new GamaQueue(contentsType, new Object[] { obj });
	}

	/**
	 * Get default value
	 */
	@Override
	public GamaQueue getDefault() {
		return null;
	}

	/**
	 * Get key type (integer -> index)
	 */
	@Override
	public IType<?> getKeyType() {
		return Types.get(INT);
	}

	/**
	 * Get content type
	 */
	@Override
	public IType<?> contentsTypeIfCasting(final IExpression expr) {
		switch (expr.getGamlType().id()) {
		case COLOR:
		case DATE:
			return Types.get(INT);
		case POINT:
			return Types.get(FLOAT);
		}
		return super.contentsTypeIfCasting(expr);
	}

	/**
	 * True (the type can const cast)
	 */
	@Override
	public boolean canCastToConst() {
		return true;
	}
}
