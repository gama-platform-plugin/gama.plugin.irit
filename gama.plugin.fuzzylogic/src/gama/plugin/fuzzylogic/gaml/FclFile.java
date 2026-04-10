package gama.plugin.fuzzylogic.gaml;

// documentation is returned as a simple String

import gama.annotations.doc;
import gama.annotations.file;
import gama.annotations.support.IConcept;
import gama.api.exceptions.GamaRuntimeException;
import gama.api.gaml.types.IType;
import gama.api.runtime.scope.IScope;
import gama.api.types.file.GamaFile;
import gama.api.types.list.IList;
import gama.api.utils.geometry.IEnvelope;
// GamaFileMetaData and ConstantDoc moved in the rework; keep a lightweight local metadata class instead
// Strings utility no longer used; use System.lineSeparator() instead
import net.sourceforge.jFuzzyLogic.FIS;
import net.sourceforge.jFuzzyLogic.FunctionBlock;
import net.sourceforge.jFuzzyLogic.rule.RuleBlock;

@file (
		name = "fcl",
		extensions = { "fcl" },
		buffer_type = IType.LIST,
		buffer_content = IType.STRING,
		buffer_index = IType.INT,
		concept = { IConcept.FILE, IConcept.SAVE_FILE },
		doc = @doc ("Represents a fuzzy logic system in fic format. The internal contents is a string at index 0"))
@SuppressWarnings("unchecked")
public class FclFile extends GamaFile<IList<String>, String> {

	public static class FclInfo {

		private static final String DELIMITER = ";";
		private static final String SUFFIX_DEL = " | ";

		public int nbRules;
		public int nbVariables;

		public FclInfo(final String fileName, final long modificationStamp) {
			// lightweight metadata: compute numbers of variables and rules
			FIS fis = FIS.load(fileName, true);
			for (final FunctionBlock fb : fis) {
				nbVariables += fb.getVariables().size();
				for (final RuleBlock rb : fb.getRuleBlocks().values()) {
					nbRules += rb.getRules().size();
				}
			}
		}

//		public FclInfo(final String propertyString) {
//			super(propertyString);
//
//			final String[] segments = split(propertyString);
//			savedModel = segments[1];
//			savedExperiment = segments[2];
//			savedCycle = Integer.valueOf(segments[3]);
//		}

		public String getDocumentation() {
			final StringBuilder sb = new StringBuilder();
			sb.append("Number of variables: ").append(nbVariables).append(System.lineSeparator());
			sb.append("Number of rules: ").append(nbRules).append(System.lineSeparator());
			return sb.toString();
		}

		public String getSuffix() {
			return "Variables: " + nbVariables + " | Rules: " + nbRules;
		}

		public void appendSuffix(final StringBuilder sb) {
			sb.append("Variables: ").append(nbVariables).append(SUFFIX_DEL);
			sb.append("Rules: ").append(nbRules);
		}

		/**
		 * @return
		 */
		public String toPropertyString() {
			// simple serialization for properties
			return nbVariables + DELIMITER + nbRules;
		}
	}
	
	
	@doc ("Constructor for FCL (Fuzzy Control Language, specification IEC 61131 part 7) files: read the content.")
	public FclFile(IScope scope, String pn) throws GamaRuntimeException {
		super(scope, pn);
	}

	@Override
	public IEnvelope computeEnvelope(IScope scope) {
		// FCL files are not geometric; return null for envelope
		return null;
	}


}
