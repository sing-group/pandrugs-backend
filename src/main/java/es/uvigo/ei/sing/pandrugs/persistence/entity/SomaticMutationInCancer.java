/*
 * #%L
 * PanDrugsDB Backend
 * %%
 * Copyright (C) 2015 - 2016 Fátima Al-Shahrour, Elena Piñeiro, Daniel Glez-Peña and Miguel Reboiro-Jato
 * %%
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public
 * License along with this program.  If not, see
 * <http://www.gnu.org/licenses/gpl-3.0.html>.
 * #L%
 */
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
