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

import static java.util.Collections.unmodifiableSet;

import java.io.Serializable;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;

@Entity(name = "protein_change_publication")
public class ProteinChangesPublication implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;

	@Column(name = "title", length = 400, columnDefinition = "VARCHAR(400)")
	private String title;

	@Column(name = "authors", length = 3500, columnDefinition = "VARCHAR(3500)")
	private String authors;

	@Column(name = "publication", length = 200, columnDefinition = "VARCHAR(200)")
	private String publication;
	
	@ManyToMany(fetch = FetchType.LAZY)
	@JoinTable(
		name = "protein_change_publication_protein_change",
		joinColumns = @JoinColumn(
			name = "publication_id",
			referencedColumnName = "id"
		),
		inverseJoinColumns = {
			@JoinColumn(
				name = "uniprot_id",
				referencedColumnName = "uniprot_id"
			),
			@JoinColumn(
				name = "protein_change",
				referencedColumnName = "protein_change"
			)
		}
	)
	private Set<ProteinChange> changes;

	ProteinChangesPublication() {}

	public Integer getId() {
		return id;
	}

	public String getTitle() {
		return title;
	}

	public String getAuthors() {
		return authors;
	}

	public String getPublication() {
		return publication;
	}
	
	public Set<ProteinChange> getChanges() {
		return unmodifiableSet(changes);
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((authors == null) ? 0 : authors.hashCode());
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		result = prime * result + ((publication == null) ? 0 : publication.hashCode());
		result = prime * result + ((title == null) ? 0 : title.hashCode());
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
		ProteinChangesPublication other = (ProteinChangesPublication) obj;
		if (authors == null) {
			if (other.authors != null)
				return false;
		} else if (!authors.equals(other.authors))
			return false;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		if (publication == null) {
			if (other.publication != null)
				return false;
		} else if (!publication.equals(other.publication))
			return false;
		if (title == null) {
			if (other.title != null)
				return false;
		} else if (!title.equals(other.title))
			return false;
		return true;
	}
}
