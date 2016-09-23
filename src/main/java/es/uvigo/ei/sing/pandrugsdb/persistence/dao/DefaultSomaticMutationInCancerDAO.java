package es.uvigo.ei.sing.pandrugsdb.persistence.dao;

import static es.uvigo.ei.sing.pandrugsdb.util.Checks.requireNonEmpty;

import java.util.List;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Path;
import javax.persistence.criteria.Root;

import es.uvigo.ei.sing.pandrugsdb.persistence.entity.SomaticMutationInCancer;
import es.uvigo.ei.sing.pandrugsdb.persistence.entity.SomaticMutationInCancerId;

public class DefaultSomaticMutationInCancerDAO
extends DAO<SomaticMutationInCancerId, SomaticMutationInCancer>
implements SomaticMutationInCancerDAO {

	@Override
	public SomaticMutationInCancer get(SomaticMutationInCancerId id) {
		return super.get(id);
	}

	@Override
	public List<SomaticMutationInCancer> listByGeneAndMutationAA(String geneSymbol, String mutationAA) {
		requireNonEmpty(geneSymbol, "geneSymbol can't be empty or null");
		requireNonEmpty(mutationAA, "mutationAA can't be empty or null");

		final CriteriaQuery<SomaticMutationInCancer> query = createCBQuery();
		final Root<SomaticMutationInCancer> root = query.from(getEntityType());
		final CriteriaBuilder cb = cb();

		final Path<String> geneSymbolField = root.get("geneSymbol");
		final Path<Integer> mutationAAField = root.get("mutationAA");
		
		return em.createQuery(
			query.select(root)
				.where(cb.and(
					cb.equal(geneSymbolField, geneSymbol),
					cb.equal(mutationAAField, mutationAA)
				))
		).getResultList();
	}

}
