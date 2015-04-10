package es.uvigo.ei.sing.pandrugsdb.core.vcfanalysis;

import java.util.List;

import es.uvigo.ei.sing.pandrugsdb.persistence.entity.User;

public interface ComputationsStore {

	void storeComputation(VariantCallingCandidateDrugComputation computation,
			User user);

	List<VariantCallingCandidateDrugComputation> retrieveComputations(User user);

}
