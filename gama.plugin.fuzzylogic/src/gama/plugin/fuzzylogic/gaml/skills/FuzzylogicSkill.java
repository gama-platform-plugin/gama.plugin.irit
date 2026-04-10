package gama.plugin.fuzzylogic.gaml.skills;

import java.util.Map;

import gama.annotations.action;
import gama.annotations.arg;
import gama.annotations.doc;
import gama.annotations.skill;
import gama.annotations.constants.IKeyword;
import gama.annotations.support.IConcept;
import gama.api.exceptions.GamaRuntimeException;
import gama.api.gaml.types.IType;
import gama.api.kernel.agent.IAgent;
import gama.api.kernel.skill.Skill;
import gama.api.runtime.scope.IScope;
import gama.plugin.fuzzylogic.utils.IFLKeyword;
import net.sourceforge.jFuzzyLogic.FIS;

@doc ("The fuzzy_logic skill is intended to define the actions of an agent reasoning using a Fuzzy logic Inference System (FIS).")
@skill ( name = "fuzzy_logic",
		 concept = { IConcept.SKILL, IFLKeyword.FL_CONCEPT})
@SuppressWarnings("unchecked")
public class FuzzylogicSkill extends Skill {

//	@action(name = "fl_init_fis", 
//			args = {
//				@arg(name = IKeyword.FROM, type = IType.FILE, optional = false, 
//						doc = @doc("The file containing the Fuzzy Logic Inference System."))			
//			}, doc = @doc("Action that initializes the FIS from a FCL file."))
//	public void initFIS(final IScope scope) throws GamaRuntimeException {
//		final IAgent agt = scope.getAgent();
//		
//		// Initialise the FIS 
//		GamaFile<?, ?> fclFile = (GamaFile<?, ?>) scope.getArg(IKeyword.FROM, IType.FILE);
//		FIS f = FIS.load(fclFile.getPath(scope),true) ;
//		agt.setAttribute(IFLKeyword.FL_ATT_FIS, f);
//		
//		// Initialise the variables and outputs binding maps
//		Map<String,String> vars = GamaMapFactory.create(Types.STRING, Types.STRING);
//		agt.setAttribute(IFLKeyword.FL_ATT_VARIABLES, vars);			
//
//		Map<String,String> outputs = GamaMapFactory.create(Types.STRING, Types.STRING);
//		agt.setAttribute(IFLKeyword.FL_ATT_OUTPUTS, outputs);	
//	}	
//	
	
	@action(name = "fl_evaluate", 
			doc = @doc("Action that evaluates the FIS."))
	@SuppressWarnings("rawtypes")	
	public void evaluate(final IScope scope) throws GamaRuntimeException {
		final IAgent agt = scope.getAgent();
		FIS fis = (FIS) agt.getAttribute(IFLKeyword.FL_ATT_FIS);	
		Map<String,String> vars = (Map) agt.getAttribute(IFLKeyword.FL_ATT_VARIABLES);

		if(vars == null) {
			throw GamaRuntimeException.error("Variables of the FIS have not been associated with agent attributes", scope);
		}
		
        // Set inputs from the agent attributes
		for(String nameVariables : vars.keySet()) {
	        fis.setVariable(nameVariables, (Double) agt.getAttribute(vars.get(nameVariables)));			
		}

        // Evaluate the FIS
        fis.evaluate();	
        
        // Set values to bound attributes
		Map<String,String> outs = (Map) agt.getAttribute(IFLKeyword.FL_ATT_OUTPUTS);

		if(outs == null) {
			throw GamaRuntimeException.error("Outputs of the FIS have not been associated with agent attributes", scope);
		}
        // Set outputs variables from the agent outputs
		for(String nameOuputs : outs.keySet()) {
			agt.setAttribute(outs.get(nameOuputs), fis.getVariable(nameOuputs).getValue());
		}
	}
	
	
	@action(name = "fl_get_output", args = {
			@arg(name = IKeyword.VAR, type = IType.STRING, optional = false, 
					doc = @doc("The name of the output we want to take the value."))}, 
			doc = @doc("Action that returns the value of the given output FIS."))
	public Double getOutput(final IScope scope) throws GamaRuntimeException {
		final IAgent agt = scope.getAgent();
		FIS fis = (FIS) agt.getAttribute(IFLKeyword.FL_ATT_FIS);
		String varFISOutput = (String) scope.getArg(IKeyword.VAR,IType.STRING);		
		
		if(fis == null) {
			throw GamaRuntimeException.error("The FIS has not been initialized.", scope);
		}
				    
	    return fis.getVariable(varFISOutput).getValue();
	}		
}
