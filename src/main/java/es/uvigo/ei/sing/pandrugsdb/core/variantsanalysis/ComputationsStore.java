package es.uvigo.ei.sing.pandrugsdb.core.variantsanalysis;

import java.util.List;

import es.uvigo.ei.sing.pandrugsdb.persistence.entity.User;

public interface ComputationsStore {

	void storeComputation(VariantsCandidateTherapiesComputation computation,
			User user);

	List<VariantsCandidateTherapiesComputation> retrieveComputations(User user);

}
