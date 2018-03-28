/*-
 * #%L
 * PanDrugs Backend
 * %%
 * Copyright (C) 2015 - 2018 Fátima Al-Shahrour, Elena Piñeiro, Daniel Glez-Peña and Miguel Reboiro-Jato
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

package es.uvigo.ei.sing.pandrugs.persistence.entity;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Index;
import javax.persistence.PostLoad;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

@Entity(name = "gene_drug_warning")
@Table(
	indexes = @Index(name = "IDX_gene_drug_warning_affected_gene", columnList = "affected_gene", unique = false),
	uniqueConstraints = @UniqueConstraint(columnNames = { "affected_gene", "standard_drug_name", "interaction_type", "indirect_gene" })
	
)
public class GeneDrugWarning implements Serializable {
	private static final long serialVersionUID = 1L;
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	
	@Column(name = "affected_gene", length = 100, columnDefinition = "VARCHAR(100)", nullable = false)
	private String affectedGene;

	@Column(name = "standard_drug_name", length = 2000, columnDefinition = "VARCHAR(2000)", nullable = false)
	private String standardDrugName;
	
	@Column(name = "warning", length = 1000, columnDefinition = "VARCHAR(1000)", nullable = false)
	private String warning;
	
	@Column(name = "interaction_type", nullable = false)
	@Enumerated(EnumType.STRING)
	private InteractionType interactionType;
	
	@Column(name = "indirect_gene", length = 100, columnDefinition = "VARCHAR(100)", nullable = true)
	private String indirectGene;
	
	GeneDrugWarning() {}

	@PostLoad
	public void validate() {
		if (this.interactionType == InteractionType.PATHWAY_MEMBER && this.indirectGene == null) {
			throw new IllegalStateException("Missing indirect gene for PATHWAY_MEMBER relation");
		} else if (this.interactionType != InteractionType.PATHWAY_MEMBER && this.indirectGene != null) {
			throw new IllegalStateException("Indirect gene is not supported in interaction types different from PATHWAY_MEMBER");
		}
	}
	
	public Long getId() {
		return id;
	}

	public String getAffectedGene() {
		return affectedGene;
	}

	public String getStandardDrugName() {
		return standardDrugName;
	}

	public String getWarning() {
		return warning;
	}

	public InteractionType getInteractionType() {
		return interactionType;
	}

	public String getIndirectGene() {
		return indirectGene;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		GeneDrugWarning other = (GeneDrugWarning) obj;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		return true;
	}
}
