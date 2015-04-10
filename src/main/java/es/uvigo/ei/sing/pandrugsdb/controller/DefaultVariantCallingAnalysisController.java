package es.uvigo.ei.sing.pandrugsdb.controller;

import java.net.URL;
import java.util.List;

import javax.inject.Inject;

import es.uvigo.ei.sing.pandrugsdb.core.vcfanalysis.ComputationsStore;
import es.uvigo.ei.sing.pandrugsdb.core.vcfanalysis.VariantCallingCandidateDrugComputation;
import es.uvigo.ei.sing.pandrugsdb.core.vcfanalysis.VariantCallingCandidateDrugComputer;
import es.uvigo.ei.sing.pandrugsdb.persistence.entity.User;

public class DefaultVariantCallingAnalysisController implements
		VariantCallingAnalysisController {

	@Inject
	private ComputationsStore computationStore;
	
	@Inject
	private VariantCallingCandidateDrugComputer 
		variantCallingCandidateDrugComputer;
	
	@Override
	public VariantCallingCandidateDrugComputation 
		startCandidateDrugsComputation(User user, URL vcfFile) {
		
		final VariantCallingCandidateDrugComputation computation = 
				variantCallingCandidateDrugComputer.createComputation(vcfFile);

		computationStore.storeComputation(computation, user);
		
		return computation;
	}


	@Override
	public List<VariantCallingCandidateDrugComputation> getComputations(
			User user) {
		return computationStore.retrieveComputations(user);
	}
}
