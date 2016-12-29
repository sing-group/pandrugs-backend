/*
 * #%L
 * PanDrugs Backend
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
package es.uvigo.ei.sing.pandrugs.persistence.entity;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.IdClass;
import javax.persistence.ManyToOne;
import javax.persistence.PrimaryKeyJoinColumn;

@Entity(name = "protein_pfam")
@IdClass(ProteinPfamId.class)
public class ProteinPfam implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@Column(name = "start")
	private int start;
	
	@Id
	@Column(name = "end")
	private int end;

	@Id
	@Column(name = "protein_uniprot_id", insertable = false, updatable = false)
	private String uniprotId;
	
	@Id
	@Column(name = "pfam_accession", length = 7, columnDefinition = "CHAR(7)", insertable = false, updatable = false)
	private String accession;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@PrimaryKeyJoinColumn(
		name = "protein_uniprot_id",
		referencedColumnName = "uniprot_id"
	)
	private Protein protein;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@PrimaryKeyJoinColumn(
		name = "pfam_accession",
		referencedColumnName = "accession",
		columnDefinition = "CHAR(7)"
	)
	private Pfam pfam;

	ProteinPfam() {}

	public Protein getProtein() {
		return protein;
	}
	
	public Pfam getPfam() {
		return pfam;
	}
	
	public int getStart() {
		return start;
	}
	
	public int getEnd() {
		return end;
	}
}
