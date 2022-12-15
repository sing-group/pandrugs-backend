/*-
 * #%L
 * PanDrugs Backend
 * %%
 * Copyright (C) 2015 - 2022 Fátima Al-Shahrour, Elena Piñeiro, Daniel Glez-Peña
 * and Miguel Reboiro-Jato
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
import java.util.Objects;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.IdClass;
import javax.persistence.JoinColumn;
import javax.persistence.JoinColumns;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

@Entity
@Table(name = "gene_drug_to_drug_source")
@IdClass(GeneDrugToDrugSourceId.class)
public class GeneDrugToDrugSource implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@Column(name = "gene_symbol", length = 50, columnDefinition = "VARCHAR(50)")
	private String geneSymbol;

	@Id
	@Column(name = "drug_id")
	private int drugId;

	@Id
	@Column(name = "target")
	private boolean target;

	@Id
	@Column(name = "source", length = 50, columnDefinition = "VARCHAR(50)")
	private String source;

	@Id
	@Column(name = "source_drug_name", length = 200, columnDefinition = "VARCHAR(200)")
	private String sourceDrugName;

	@Column(name = "alteration", length = 1200, columnDefinition = "VARCHAR(1200)", nullable = true)
	private String alteration;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumns({
		@JoinColumn(name = "gene_symbol", referencedColumnName = "gene_symbol", insertable = false, updatable = false),
		@JoinColumn(name = "drug_id", referencedColumnName = "drug_id", insertable = false, updatable = false),
		@JoinColumn(name = "target", referencedColumnName = "target", insertable = false, updatable = false)
	})
	private GeneDrug geneDrug;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumns({
		@JoinColumn(name = "source", referencedColumnName = "source", insertable = false, updatable = false),
		@JoinColumn(name = "source_drug_name", referencedColumnName = "source_drug_name", insertable = false, updatable = false)
	})
	private DrugSource drugSource;

	GeneDrugToDrugSource() {}

	public GeneDrugToDrugSource(GeneDrug geneDrug, DrugSource drugSource, String alteration) {
		this.alteration = alteration;
		this.geneDrug = geneDrug;
		this.drugSource = drugSource;
		this.geneSymbol = geneDrug.getGeneSymbol();
		this.drugId = geneDrug.getDrugId();
		this.target = geneDrug.isTarget();
		this.source = drugSource.getSource();
		this.sourceDrugName = drugSource.getSourceDrugName();
	}

	public GeneDrug getGeneDrug() {
		return geneDrug;
	}

	public DrugSource getDrugSource() {
		return drugSource;
	}

	public String getAlteration() {
		return alteration;
	}

	public boolean isCurated() {
		return drugSource.isCurated();
	}

	@Override
	public int hashCode() {
		return Objects.hash(drugId, geneSymbol, source, sourceDrugName, target);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		GeneDrugToDrugSource other = (GeneDrugToDrugSource) obj;
		return drugId == other.drugId && Objects.equals(geneSymbol, other.geneSymbol) && Objects.equals(source, other.source)
			&& Objects.equals(sourceDrugName, other.sourceDrugName) && target == other.target;
	}

	@Override
	public String toString() {
		return "GeneDrugToDrugSource [geneSymbol=" + geneSymbol + ", drugId=" + drugId + ", target=" + target + ", source=" + source + ", sourceDrugName="
			+ sourceDrugName + ", alteration=" + alteration + "]";
	}
}
