/*******************************************************************************************************
 *
 * EventManagerArchitecture.java, in plugin gama.plugin.switchproject.gaml.switchproject, is part of the source code
 * of the GAMA modeling and simulation platform (v. 1.8.1)
 *
 * (c) 2007-2020 UMI 209 UMMISCO IRD/SU & Partners
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 *
 ********************************************************************************************************/

package gama.plugin.switchproject.gaml.architecure.event_manager;

import gama.annotations.doc;
import gama.annotations.vars;
import gama.annotations.constants.IKeyword;
import gama.annotations.variable;
import gama.annotations.skill;
import gama.annotations.getter;
import gama.annotations.support.IConcept;
import gama.api.gaml.types.IType;
import gama.api.kernel.agent.IAgent;
import gama.api.runtime.scope.IScope;
import gama.api.types.date.IDate;
import gama.api.types.map.IMap;
import gama.plugin.switchproject.gama.common.interfaces.IKeywordIrit;
import gama.plugin.switchproject.gama.util.event_manager.Event;
import gama.plugin.switchproject.gama.util.event_manager.EventManager;
import gaml.compiler.descriptions.ActionDescription;
import gama.api.compilation.descriptions.IDescription;
import gama.api.exceptions.GamaRuntimeException;
import gama.gaml.architecture.reflex.ReflexArchitecture;


/**
 * Event manager architecture
 * 
 * @author Jean-François Erdelyi
 */
@vars({ @variable(name = IKeyword.SIZE, type = IType.INT, doc = @doc("Return the size of the all queues"))
	})
@skill(name = IKeywordIrit.EVENT_MANAGER, concept = { IConcept.BEHAVIOR,
		IConcept.ARCHITECTURE }, doc = @doc("Event manager behavior"))
public class EventManagerArchitecture extends ReflexArchitecture {

	// ############################################
	// Getter and setter of architecture

	/**
	 * Get size
	 */
	@getter(IKeyword.SIZE)
	public int getQueueSize(final IAgent agent) {
		return getCurrentManagerIfExists(agent).getQueueSize();
	}

	// ############################################
	// Methods

	/**
	 * Execution (each step)
	 */
	@Override
	public Object executeOn(final IScope scope) throws GamaRuntimeException {
		super.executeOn(scope);		
		return executeCurrentManager(scope);
	}

	/**
	 * Initialization
	 */
	@Override
	public boolean init(final IScope scope) throws GamaRuntimeException {
		final IAgent agent = getCurrentAgent(scope);
		final EventManager manager = new EventManager(scope);

		agent.setAttribute(IKeywordIrit.EVENT_MANAGER, manager);
		return true;
	}

	/**
	 * Execute current manager
	 */
	protected Object executeCurrentManager(final IScope scope) throws GamaRuntimeException {
		final IAgent agent = getCurrentAgent(scope);
		if (scope.interrupted() || agent == null) {
			return null;
		}

		return getCurrentManagerIfExists(agent).execute(scope);
	}

	/**
	 * Get current manager by agent
	 */
	protected EventManager getCurrentManager(final IAgent agent) throws GamaRuntimeException {
		return (EventManager) agent.getAttribute(IKeywordIrit.EVENT_MANAGER);
	}

	/**
	 * Get current manager by agent. throw exception if does not exists
	 */
	protected EventManager getCurrentManagerIfExists(final IAgent agent) throws GamaRuntimeException {
		EventManager manager = getCurrentManager(agent);
		if (manager == null) {
			throw GamaRuntimeException.error("No event manager agent was detected", agent.getScope());
		}
		return manager;
	}

	/**
	 * Internal register (used by "scheduling" skill)
	 */
	public String register(final IScope scope, final IAgent caller, final IDescription action,
			final IMap<String, Object> args, final IDate date, final IAgent referredAgent)
			throws GamaRuntimeException {

		IAgent agent = (IAgent) getCurrentAgent(scope).getAttribute(IKeywordIrit.EVENT_MANAGER);
		if (scope.interrupted() || agent == null) {
			return null;
		}

		return getCurrentManagerIfExists(agent).register(scope,
				new Event(scope, caller, (ActionDescription) action, args, date, referredAgent));
	}
	
	/**
	 * Internal kill (used by "scheduling" skill)
	 */
	public Object kill(final IScope scope, final String id) throws GamaRuntimeException {

		IAgent agent = (IAgent) getCurrentAgent(scope).getAttribute(IKeywordIrit.EVENT_MANAGER);
		if (scope.interrupted() || agent == null) {
			return false;
		}

		getCurrentManagerIfExists(agent).kill(scope, id);
		return true;
	}
}
