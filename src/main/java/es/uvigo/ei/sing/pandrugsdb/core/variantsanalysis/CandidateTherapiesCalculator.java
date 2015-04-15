package es.uvigo.ei.sing.pandrugsdb.core.variantsanalysis;

import java.util.List;

public interface CandidateTherapiesCalculator {

	public List<CandidateTherapy> calculateTherapies(
			VariantsEffectPredictionResults vep);
}
