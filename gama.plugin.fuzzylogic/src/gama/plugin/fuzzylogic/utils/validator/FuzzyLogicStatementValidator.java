package gama.plugin.fuzzylogic.utils.validator;

import gama.api.compilation.descriptions.IDescription;
import gama.api.compilation.descriptions.IDescriptionValidator;
import gama.api.compilation.descriptions.ISkillDescription;
import gama.api.constants.IGamlIssue;
import gaml.compiler.descriptions.SkillDescription;
import gaml.compiler.descriptions.SpeciesDescription;
import gaml.compiler.descriptions.StatementDescription;
import gama.plugin.fuzzylogic.gaml.skills.FuzzylogicSkill;

public class FuzzyLogicStatementValidator implements IDescriptionValidator<StatementDescription> {

	/**
	 * Method validate()
	 *
	 * @see msi.gaml.compilation.IDescriptionValidator#validate(msi.gaml.descriptions.IDescription)
	 */
	@Override
	public void validate(final StatementDescription description) {
		String statementName = description.getKeyword();
		IDescription superDesc = description.getEnclosingDescription();
		while (! (superDesc instanceof SpeciesDescription) ) {
			superDesc = superDesc.getEnclosingDescription();
		}
		
		
					
		for(ISkillDescription skillDesc : ((SpeciesDescription)superDesc).getSkills()) {
			if(skillDesc.getInstance() instanceof FuzzylogicSkill) {return;}
		}
		
		description.error("`" + statementName + "` must be used in the context of an agent with the Fuzzy Logic control architecture",
				IGamlIssue.WRONG_CONTEXT);		
	}
}