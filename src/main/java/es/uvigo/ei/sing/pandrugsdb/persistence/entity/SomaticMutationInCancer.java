package es.uvigo.ei.sing.pandrugsdb.persistence.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Id;
import javax.persistence.IdClass;
import javax.persistence.Index;
import javax.persistence.Table;

@Entity(name = "somatic_mutation_in_cancer")
@IdClass(SomaticMutationInCancerId.class)
@Table(indexes = @Index(name = "geneSymbolMutationAAIndex", columnList = "gene_symbol,mutation_aa,", unique = false))
public class SomaticMutationInCancer {
	@Id
	@Column(name = "sample_id")
	private int sampleId;
	
	@Id
	@Column(name = "gene_symbol", length = 50, columnDefinition = "VARCHAR(50)")
	private String geneSymbol;
	
	@Id
	@Column(name = "mutation_id")
	private int mutationId;
	
	@Column(name = "mutation_aa", length = 50, columnDefinition = "VARCHAR(50)")
	private String mutationAA;
	
	@Enumerated(EnumType.STRING)
	@Column(name = "fathmm_prediction", length = 15)
	private FathmmPrediction fathmmPrediciton;
	
	@Id
	@Column(name = "status", length = 100, columnDefinition = "VARCHAR(100)")
	private String status;
	
	SomaticMutationInCancer() {}

	public SomaticMutationInCancer(
		int sampleId, int mutationId, String mutationAA, FathmmPrediction fathmmPrediciton, String status, String geneSymbol
	) {
		this.sampleId = sampleId;
		this.mutationId = mutationId;
		this.mutationAA = mutationAA;
		this.fathmmPrediciton = fathmmPrediciton;
		this.status = status;
		this.geneSymbol = geneSymbol;
	}

	public int getSampleId() {
		return sampleId;
	}

	public void setSampleId(int sampleId) {
		this.sampleId = sampleId;
	}

	public int getMutationId() {
		return mutationId;
	}

	public void setMutationId(int mutationId) {
		this.mutationId = mutationId;
	}

	public String getMutationAA() {
		return mutationAA;
	}

	public void setMutationAA(String mutationAA) {
		this.mutationAA = mutationAA;
	}

	public FathmmPrediction getFathmmPrediciton() {
		return fathmmPrediciton;
	}

	public void setFathmmPrediciton(FathmmPrediction fathmmPrediciton) {
		this.fathmmPrediciton = fathmmPrediciton;
	}
	
	public String getGeneSymbol() {
		return this.geneSymbol;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}
	
}
