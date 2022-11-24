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

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;

@Entity(name = "source_info")
public class SourceInformation implements Serializable {
	private static final long serialVersionUID = 1L;
	
	@Id
	@Column(name = "source", length = 50, columnDefinition = "VARCHAR(50)")
	private String source;
	
	@Column(name = "short_name", length = 10, columnDefinition = "VARCHAR(10)", unique = true, nullable = false)
	private String shortName;
	
	@Column(name = "url_template", length = 1000, columnDefinition = "VARCHAR(1000)")
	private String urlTemplate;
	
	@Column(name = "curated")
	private boolean curated;
	
	SourceInformation() {}

	public SourceInformation(
		String source, String shortName, String urlTemplate, boolean curated
	) {
		this.source = source;
		this.shortName = shortName;
		this.urlTemplate = urlTemplate;
		this.curated = curated;
	}

	public String getSource() {
		return source;
	}
	
	public String getShortName() {
		return shortName;
	}

	public String getUrlTemplate() {
		return urlTemplate;
	}
	
	public boolean isCurated() {
		return curated;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + (curated ? 1231 : 1237);
		result = prime * result + ((shortName == null) ? 0 : shortName.hashCode());
		result = prime * result + ((source == null) ? 0 : source.hashCode());
		result = prime * result + ((urlTemplate == null) ? 0 : urlTemplate.hashCode());
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
		SourceInformation other = (SourceInformation) obj;
		if (curated != other.curated)
			return false;
		if (shortName == null) {
			if (other.shortName != null)
				return false;
		} else if (!shortName.equals(other.shortName))
			return false;
		if (source == null) {
			if (other.source != null)
				return false;
		} else if (!source.equals(other.source))
			return false;
		if (urlTemplate == null) {
			if (other.urlTemplate != null)
				return false;
		} else if (!urlTemplate.equals(other.urlTemplate))
			return false;
		return true;
	}
}
