package es.uvigo.ei.sing.pandrugsdb.controller;

import java.net.URL;
import java.util.List;

import javax.inject.Inject;

import es.uvigo.ei.sing.pandrugsdb.core.variantsanalysis.ComputationsStore;
import es.uvigo.ei.sing.pandrugsdb.core.variantsanalysis.VariantsCandidateTherapiesComputation;
import es.uvigo.ei.sing.pandrugsdb.core.variantsanalysis.VariantsCandidateTherapiesComputer;
import es.uvigo.ei.sing.pandrugsdb.persistence.entity.User;

public class DefaultVariantCallingAnalysisController implements
		VariantsAnalysisController {

	@Inject
	private ComputationsStore computationStore;
	
	@Inject
	private VariantsCandidateTherapiesComputer 
		variantCallingCandidateTherapiesComputer;
	
	@Override
	public VariantsCandidateTherapiesComputation 
		startCandidateTherapiesComputation(User user, URL vcfFile) {
		
		final VariantsCandidateTherapiesComputation computation = 
				variantCallingCandidateTherapiesComputer.createComputation(vcfFile);

		computationStore.storeComputation(computation, user);
		
		return computation;
	}


	@Override
	public List<VariantsCandidateTherapiesComputation> getComputations(
			User user) {
		return computationStore.retrieveComputations(user);
	}
}
