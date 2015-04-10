package es.uvigo.ei.sing.pandrugsdb.controller;

import java.net.URL;
import java.util.List;

import es.uvigo.ei.sing.pandrugsdb.core.vcfanalysis.VariantCallingCandidateDrugComputation;
import es.uvigo.ei.sing.pandrugsdb.persistence.entity.User;

/**
 * @author Daniel Glez-Peña
 *
 */
public interface VariantCallingAnalysisController {
	
	public VariantCallingCandidateDrugComputation 
		startCandidateDrugsComputation(User user, URL vcfFile);
	
	public List<VariantCallingCandidateDrugComputation> 
		getComputations(User user);

}
