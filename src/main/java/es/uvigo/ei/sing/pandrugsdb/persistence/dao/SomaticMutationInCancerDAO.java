package es.uvigo.ei.sing.pandrugsdb.persistence.dao;

import java.util.List;

import es.uvigo.ei.sing.pandrugsdb.persistence.entity.SomaticMutationInCancer;
import es.uvigo.ei.sing.pandrugsdb.persistence.entity.SomaticMutationInCancerId;

public interface SomaticMutationInCancerDAO {
	public SomaticMutationInCancer get(SomaticMutationInCancerId id);
	
	public default SomaticMutationInCancer get(
		int sampleId, String geneSymbol, int mutationId, String status
	) {
		return get(new SomaticMutationInCancerId(sampleId, geneSymbol, mutationId, status));
	}
	
	public List<SomaticMutationInCancer> listByGeneAndMutationAA(String gene_symbol, String mutationAA);
}
