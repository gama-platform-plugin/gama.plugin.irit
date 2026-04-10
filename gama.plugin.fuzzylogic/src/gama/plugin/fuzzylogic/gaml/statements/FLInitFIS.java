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
import gama.api.exceptions.GamaRuntimeException;
import gama.api.gaml.expressions.IExpression;
import gama.api.gaml.statements.AbstractStatement;
import gama.api.gaml.types.IType;
import gama.api.gaml.types.Types;
import gama.api.kernel.agent.IAgent;
import gama.api.runtime.scope.IScope;
import gama.api.types.file.GamaFile;
import gama.api.types.map.GamaMapFactory;
import gaml.compiler.descriptions.StatementDescription;
// serializer APIs reworked — plugin provides no custom serializer here
import gama.plugin.fuzzylogic.gaml.statements.FLInitFIS.FLInitFISValidator;
import gama.plugin.fuzzylogic.utils.IFLKeyword;
import gama.plugin.fuzzylogic.utils.validator.FuzzyLogicStatementValidator;
import net.sourceforge.jFuzzyLogic.FIS;

@symbol (
		name = IFLKeyword.FL_INIT_FIS,
		kind = ISymbolKind.SINGLE_STATEMENT,
		with_sequence = false,
		concept = { IFLKeyword.FL_CONCEPT })
@doc (value = "`" + IFLKeyword.FL_INIT_FIS + "` allows to init a FIS from a file.")
@inside (
		kinds = { ISymbolKind.BEHAVIOR, ISymbolKind.SEQUENCE_STATEMENT})
@facets (
		value = { 
			@facet (
				name = IFLKeyword.FL_FROM,
				type = { IType.FILE },
				optional = false,
				doc = { @doc ("the file containing the FIS description") })
		}, 
		omissible = IFLKeyword.FL_FROM)
@validator (FLInitFISValidator.class)
public class FLInitFIS extends AbstractStatement {

	public static class FLInitFISValidator extends FuzzyLogicStatementValidator {
		@Override
		public void validate(StatementDescription description) {
			super.validate(description);
			// TODO Auto-generated method stub	
		}
		
	}

	// Serializer support removed: plugin relies on default serialization for now.
	
	protected IExpression file;	
	
	public FLInitFIS(IDescription desc) {
		super(desc);
		file = getFacet(IFLKeyword.FL_FROM);
	}

	@SuppressWarnings("unchecked")
	@Override
	protected Object privateExecuteIn(IScope scope) throws GamaRuntimeException {
		final IAgent agt = scope.getAgent();
		
		// Initialise the FIS 
		GamaFile<?, ?> fclFile = (GamaFile<?, ?>) file.value(scope);
		FIS f = FIS.load(fclFile.getPath(scope),true) ;
		agt.setAttribute(IFLKeyword.FL_ATT_FIS, f);
		
		// Initialise the variables and outputs binding maps
		Map<String,String> vars = GamaMapFactory.create(Types.STRING, Types.STRING);
		agt.setAttribute(IFLKeyword.FL_ATT_VARIABLES, vars);			

		Map<String,String> outputs = GamaMapFactory.create(Types.STRING, Types.STRING);
		agt.setAttribute(IFLKeyword.FL_ATT_OUTPUTS, outputs);		
		return null;
	}

}
