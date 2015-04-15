package es.uvigo.ei.sing.pandrugsdb.controller;

import java.net.URL;
import java.util.List;

import es.uvigo.ei.sing.pandrugsdb.core.variantsanalysis.VariantsCandidateTherapiesComputation;
import es.uvigo.ei.sing.pandrugsdb.persistence.entity.User;

/**
 * @author Daniel Glez-Peña
 *
 */
public interface VariantsAnalysisController {
	
	public VariantsCandidateTherapiesComputation 
		startCandidateTherapiesComputation(User user, URL vcfFile);
	
	public List<VariantsCandidateTherapiesComputation>
		getComputations(User user);

}
