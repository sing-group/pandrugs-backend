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

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.IdClass;
import javax.persistence.Index;
import javax.persistence.Table;

@Entity(name = "clinical_genome_variation")
@IdClass(ClinicalGenomeVariationId.class)
@Table(indexes = {
	@Index(name = "dbSnpIndex", columnList = "db_snp", unique = false),
	@Index(name = "chromosomePositionsIndex", columnList = "chromosome,start,hgvs", unique = false)
})
public class ClinicalGenomeVariation implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@Column(name = "chromosome", length = 2, columnDefinition = "VARCHAR(2)")
	private String chromosome;
	
	@Id
	@Column(name = "start")
	private int start;
	
	@Id
	@Column(name = "end")
	private int end;

	@Id
	@Column(name = "hgvs", length = 240, columnDefinition = "VARCHAR(240)")
	private String hgvs;
	
	@Id
	@Column(name = "disease", length = 150, columnDefinition = "VARCHAR(150)")
	private String disease;
	
	@Id
	@Column(name = "accession", length = 12, columnDefinition = "VARCHAR(12)")
	private String accession;
	
	@Column(name = "clinical_significance", length = 60, columnDefinition = "VARCHAR(60)", nullable = false)
	private String clinicalSignificance;
	
	@Column(name = "db_snp", length = 12, columnDefinition = "VARCHAR(12)", nullable = true)
	private String dbSnp;
	
	ClinicalGenomeVariation() {
	}
	
	public ClinicalGenomeVariation(
		String chromosome, int start, int end,
		String hgvs, String disease, String accession,
		String clinicalSignificance, String dbSnp
	) {
		this.chromosome = chromosome;
		this.start = start;
		this.end = end;
		this.hgvs = hgvs;
		this.disease = disease;
		this.accession = accession;
		this.clinicalSignificance = clinicalSignificance;
		this.dbSnp = dbSnp;
	}

	public String getChromosome() {
		return chromosome;
	}

	public int getStart() {
		return start;
	}

	public int getEnd() {
		return end;
	}

	public String getHgvs() {
		return hgvs;
	}

	public String getDisease() {
		return disease;
	}

	public String getAccession() {
		return accession;
	}

	public String getClinicalSignificance() {
		return clinicalSignificance;
	}

	public String getDbSnp() {
		return dbSnp;
	}
	
}
