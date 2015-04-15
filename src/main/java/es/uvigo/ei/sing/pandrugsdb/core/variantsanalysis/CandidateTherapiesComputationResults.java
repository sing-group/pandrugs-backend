package es.uvigo.ei.sing.pandrugsdb.core.variantsanalysis;

import java.util.List;

public class CandidateTherapiesComputationResults {
	private List<CandidateTherapy> candidateTherapies;
	private VariantsEffectPredictionResults variantEffectPredictionResults;
	
	public CandidateTherapiesComputationResults(
			List<CandidateTherapy> candidateTherapies,
			VariantsEffectPredictionResults variantEffectPredictionResults) {
		super();
		this.candidateTherapies = candidateTherapies;
		this.variantEffectPredictionResults = variantEffectPredictionResults;
	}

	public List<CandidateTherapy> getCandidateTherapies() {
		return candidateTherapies;
	}

	public VariantsEffectPredictionResults getVariantEffectPredictionResults() {
		return variantEffectPredictionResults;
	}
	
	
	
	
	
}
