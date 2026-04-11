/*******************************************************************************************************
 *
 * SchedulingSkill.java, in plugin gama.plugin.switchproject.gama.switchproject,
 * is part of the source code of the GAMA modeling and simulation platform (v. 1.8.1)
 *
 * (c) 2007-2020 UMI 209 UMMISCO IRD/SU & Partners
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 * 
 ********************************************************************************************************/

package gama.plugin.switchproject.gaml.skills.scheduling;

import gama.plugin.switchproject.gama.common.interfaces.IKeywordIrit;
import gama.plugin.switchproject.gaml.architecure.event_manager.EventManagerArchitecture;
import gaml.compiler.descriptions.ActionDescription;
import gama.annotations.doc;
import gama.annotations.usage;
import gama.annotations.vars;
import gama.annotations.variable;
import gama.annotations.skill;
import gama.annotations.getter;
import gama.annotations.setter;
import gama.annotations.action;
import gama.annotations.arg;

import gama.annotations.support.IConcept;
import gama.annotations.support.ITypeProvider;
import gama.api.exceptions.GamaRuntimeException;
import gama.api.gaml.types.IType;
import gama.api.kernel.agent.IAgent;
import gama.api.kernel.skill.Skill;
import gama.api.runtime.scope.IScope;
import gama.api.types.date.IDate;
import gama.api.types.map.IMap;
import gama.annotations.example;
import gama.annotations.operator;
/**
 * Scheduling skill
 * 
 * @author Jean-François Erdelyi
 */
@vars({ @variable(name = IKeywordIrit.EVENT_MANAGER, type = IType.AGENT, doc = {
		@doc("The event manager, must be defined in another species with \"control: event_manager\"") }),
		@variable(name = IKeywordIrit.EVENT_DATE, type = IType.DATE, doc = { @doc("The date of the previous event") }),
		@variable(name = IKeywordIrit.REFER_TO, type = IType.AGENT, doc = {
				@doc("The agent to refer when the event is triggered") }) })
@skill(name = IKeywordIrit.SCHEDULING, concept = { IKeywordIrit.SCHEDULING, IConcept.SKILL }, internal = true)
public class SchedulingSkill extends Skill {

	// ############################################
	// Getter and setter of skill

	@setter(IKeywordIrit.EVENT_MANAGER)
	public void setEventManager(final IAgent agent, final IAgent manager) {
		if (agent == null || manager == null) {
			return;
		}
		agent.setAttribute(IKeywordIrit.EVENT_MANAGER, manager);
	}

	@getter(IKeywordIrit.EVENT_MANAGER)
	public IAgent getEventManager(final IAgent agent) {
		if (agent == null) {
			return null;
		}
		return (IAgent) agent.getAttribute(IKeywordIrit.EVENT_MANAGER);
	}

	@getter(IKeywordIrit.EVENT_DATE)
	public IDate getAt(final IAgent agent) {
		if (agent == null) {
			return null;
		}
		return (IDate) agent.getAttribute(IKeywordIrit.EVENT_DATE);
	}

	@getter(IKeywordIrit.REFER_TO)
	public IAgent getReferTo(final IAgent agent) {
		if (agent == null) {
			return null;
		}
		return (IAgent) agent.getAttribute(IKeywordIrit.REFER_TO);
	}

	// ############################################
	// Action of architecture

	@action(name = "later", args = {
			@arg(name = IKeywordIrit.THE_ACTION, type = IType.STRING, optional = false, doc = @doc("The name of an action or a primitive")),
			@arg(name = IKeywordIrit.WITH_ARGUMENTS, type = IType.MAP, optional = true, doc = @doc("A map expression containing the parameters of the action")),
			@arg(name = IKeywordIrit.AT, type = IType.DATE, optional = true, doc = @doc("Call date")),
			@arg(name = IKeywordIrit.REFER_TO, type = IType.AGENT, optional = true, doc = @doc("The agent to refer")) }, doc = @doc(examples = {
					@example("do later execute: my_action arguments: map((\"test\"::2)) date: starting_date") }, value = "Do action when the date is reached."))
	@SuppressWarnings("unchecked")
	public String register(final IScope scope) throws GamaRuntimeException {
		// Get date
		IDate date = (IDate) scope.getArg(IKeywordIrit.AT, IType.DATE);
		// Get caller
		IAgent caller = scope.getAgent();
		// Get arguments
		IMap<String, Object> args = (IMap<String, Object>) scope.getArg(IKeywordIrit.WITH_ARGUMENTS, IType.MAP);
		// Get refer to agent
		IAgent referredAgent = (IAgent) scope.getArg(IKeywordIrit.REFER_TO, IType.AGENT);
		
		// Get action
		String actionName = (String) scope.getArg(IKeywordIrit.THE_ACTION, IType.STRING);
		ActionDescription action;
		if(referredAgent == null ) {
			action = caller.getSpecies().getDescription().getAction(actionName);			
		} else {
			action = referredAgent.getSpecies().getDescription().getAction(actionName);
		}

		// Get manager
		IAgent manager = (IAgent) scope.getAgent().getAttribute(IKeywordIrit.EVENT_MANAGER);

		if (manager == null) {
			throw GamaRuntimeException.error("The manager must be defined if you have to use the action \"later\"",
					scope);
		}

		// Get skill and do register on it
		EventManagerArchitecture eventSkill = (EventManagerArchitecture) manager.getSpecies().getArchitecture();
		if (eventSkill == null) {
			throw GamaRuntimeException
					.error("The manager must use the control \"event_manager\" to execute the action \"later\"", scope);
		}
		return eventSkill.register(scope, caller, action, args, date, referredAgent);
	}


	@action(name = "kill_event", args = {
			@arg(name = IKeywordIrit.ID, type = IType.STRING, optional = false, doc = @doc("The event ID")) }, doc = @doc(examples = {
					@example("do kill_event(\"123e4567-e89b-12d3-a456-426655440000\")") }, value = "Kill event."))
	public Object kill(final IScope scope) throws GamaRuntimeException {
		// Get manager
		IAgent manager = (IAgent) scope.getAgent().getAttribute(IKeywordIrit.EVENT_MANAGER);

		// Get arg
		String id = (String) scope.getArg(IKeywordIrit.ID, IType.STRING);

		if (manager == null) {
			throw GamaRuntimeException.error("The manager must be defined if you have to use the action \"later\"",
					scope);
		}

		// Get skill and do register on it
		EventManagerArchitecture eventSkill = (EventManagerArchitecture) manager.getSpecies().getArchitecture();
		if (eventSkill == null) {
			throw GamaRuntimeException
					.error("The manager must use the control \"event_manager\" to execute the action \"later\"", scope);
		}
		return eventSkill.kill(scope, id);
	}

}
