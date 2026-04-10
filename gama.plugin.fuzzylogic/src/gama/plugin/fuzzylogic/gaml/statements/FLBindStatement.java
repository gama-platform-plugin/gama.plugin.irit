package gama.plugin.fuzzylogic.gaml.statements;

import java.util.Map;

import gama.annotations.doc;
import gama.annotations.facet;
import gama.annotations.facets;
import gama.annotations.inside;
import gama.annotations.symbol;
import gama.annotations.support.ISymbolKind;
import gama.api.annotations.serializer;
import gama.api.annotations.validator;
import gama.api.compilation.descriptions.IDescription;
import gama.api.constants.IGamlIssue;
import gama.api.exceptions.GamaRuntimeException;
import gama.api.gaml.expressions.IExpression;
import gama.api.gaml.statements.AbstractStatement;
import gama.api.gaml.types.IType;
import gama.api.kernel.agent.IAgent;
import gama.api.runtime.scope.IScope;
import gaml.compiler.descriptions.SpeciesDescription;
import gaml.compiler.descriptions.StatementDescription;
import gama.plugin.fuzzylogic.gaml.statements.FLBindStatement.FuzzyLogicBindStatementValidator;
import gama.plugin.fuzzylogic.utils.IFLKeyword;
import gama.plugin.fuzzylogic.utils.validator.FuzzyLogicStatementValidator;

/**
 * The class SetVariableStatement.
 *
 * @author gaudou
 * @since 11 march 20
 *
 */
@symbol (
	name = IFLKeyword.FL_BIND,
	kind = ISymbolKind.SINGLE_STATEMENT,
	with_sequence = false,
	concept = { IFLKeyword.FL_CONCEPT })
@doc (value = "`" + IFLKeyword.FL_BIND + "` allows to bind an agent attribute to a FIS variable or output.")
@inside (
	kinds = { ISymbolKind.BEHAVIOR, ISymbolKind.SEQUENCE_STATEMENT})
@facets (
	value = { 
		@facet (
			name = IFLKeyword.FL_ATTRIBUTE,
			type = { IType.NONE },
			optional = false,
			doc = { @doc ("an attribute of the current agent") }),
		@facet (
			name = IFLKeyword.FL_VARIABLE,
			type = IType.STRING,
			optional = true,
			doc = { @doc ("the name of a FIS variable") }),
		@facet (
			name = IFLKeyword.FL_OUTPUT,
			type = IType.STRING,
			optional = true,
			doc = { @doc ("the name of a FIS output") })
	}, 
	omissible = IFLKeyword.FL_ATTRIBUTE)
@validator (FuzzyLogicBindStatementValidator.class)
public class FLBindStatement extends AbstractStatement {

	public static class FuzzyLogicBindStatementValidator extends FuzzyLogicStatementValidator {
		@Override
		public void validate(final StatementDescription description) {
			super.validate(description);
			final IExpression att = description.getFacetExpr(IFLKeyword.FL_ATTRIBUTE);
			final IExpression var = description.getFacetExpr(IFLKeyword.FL_VARIABLE);
			final IExpression out = description.getFacetExpr(IFLKeyword.FL_OUTPUT);
			
			if( (var == null) && (out == null) ) {
				description.error("" + IFLKeyword.FL_BIND + " requiers at least either " + IFLKeyword.FL_VARIABLE + " or " + IFLKeyword.FL_OUTPUT + "` facet.",
						IGamlIssue.MISSING_ARGUMENT);		
			}

			if( (var != null) && (out != null) ) {
				description.error("" + IFLKeyword.FL_BIND + " requiers no more than one facet among " + IFLKeyword.FL_VARIABLE + " and " + IFLKeyword.FL_OUTPUT + " facets.",
						IGamlIssue.CONFLICTING_FACETS);		
			}		
		
			// Check that attributes exist
			IDescription superDesc = description.getEnclosingDescription();
			while (! (superDesc instanceof SpeciesDescription) ) {
				superDesc = superDesc.getEnclosingDescription();
			}
			SpeciesDescription superSpeciesDescr = (SpeciesDescription) superDesc;
			
			if( (att != null) && (superSpeciesDescr.getAttribute(att.getName()) == null)) {
				description.error("The attribute " + att.getName() + " does not exist.",
						IGamlIssue.UNKNOWN_FIELD);		
			}
		}
	}


	protected IExpression attribute;
	protected IExpression fis_variable, fis_output;	
	
	public FLBindStatement(IDescription desc) {
		super(desc);
		
		attribute = getFacet(IFLKeyword.FL_ATTRIBUTE);
		fis_variable = getFacet(IFLKeyword.FL_VARIABLE);
		fis_output = getFacet(IFLKeyword.FL_OUTPUT);
	}
	
	/**
	 * @see msi.gaml.commands.AbstractCommand#privateExecuteIn(msi.gama.runtime.IScope)
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Override	
	protected Object privateExecuteIn(final IScope scope) throws GamaRuntimeException {
		final IAgent agt = scope.getAgent();
		Object attributeName = attribute.value(scope);

		if(fis_variable != null) {
			String variableName = (String) fis_variable.value(scope);
			
			Map<String,String> vars = (Map) agt.getAttribute(IFLKeyword.FL_ATT_VARIABLES);
			if(vars == null) {
				throw GamaRuntimeException.error("The FIS has not been initialized.", scope);
			}
			vars.put(variableName, attributeName.toString());
			agt.setAttribute(IFLKeyword.FL_ATT_VARIABLES, vars);			
		}
		
		if(fis_output != null) {
			String outputName = (String) fis_output.value(scope);

			Map<String,String> outs = (Map) agt.getAttribute(IFLKeyword.FL_ATT_OUTPUTS);
			if(outs == null) {
				throw GamaRuntimeException.error("The FIS has not been initialized.", scope);
			}
			outs.put(outputName, attributeName.toString());
			agt.setAttribute(IFLKeyword.FL_ATT_OUTPUTS, outs);			
		}
		
		return null; 
	}
}
