/*-
 * #%L
 * PanDrugs Backend
 * %%
 * Copyright (C) 2015 - 2023 Fátima Al-Shahrour, Elena Piñeiro, Daniel Glez-Peña
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
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Entity(name = "exac")
public class Exac implements Serializable {
	private static final long serialVersionUID = 1L;
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private int id;
	
	@Column(name = "chromosome", length = 2, columnDefinition = "VARCHAR(2)")
	private String chromosome;

	@Column(name = "location")
	private int location;
	
	@Column(name = "ref", length = 300, columnDefinition = "VARCHAR(300)")
	private String ref;
	
	@Column(name = "alt", length = 450, columnDefinition = "VARCHAR(430)")
	private String alt;
	
	@Column(name = "allele_frequency", precision = 6)
	private double alleleFrequency;
	
	@Column(name = "nfe_allele_frequency", precision = 6)
	private double nfeAlleleFrequency;

	public int getId() {
		return id;
	}
	
	public String getChromosome() {
		return chromosome;
	}

	public int getLocation() {
		return location;
	}

	public String getRef() {
		return ref;
	}

	public String getAlt() {
		return alt;
	}

	public double getAlleleFrequency() {
		return alleleFrequency;
	}

	public double getNfeAlleleFrequency() {
		return nfeAlleleFrequency;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		long temp;
		temp = Double.doubleToLongBits(alleleFrequency);
		result = prime * result + (int) (temp ^ (temp >>> 32));
		result = prime * result + ((alt == null) ? 0 : alt.hashCode());
		result = prime * result + ((chromosome == null) ? 0 : chromosome.hashCode());
		result = prime * result + location;
		temp = Double.doubleToLongBits(nfeAlleleFrequency);
		result = prime * result + (int) (temp ^ (temp >>> 32));
		result = prime * result + ((ref == null) ? 0 : ref.hashCode());
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
		Exac other = (Exac) obj;
		if (Double.doubleToLongBits(alleleFrequency) != Double.doubleToLongBits(other.alleleFrequency))
			return false;
		if (alt == null) {
			if (other.alt != null)
				return false;
		} else if (!alt.equals(other.alt))
			return false;
		if (chromosome == null) {
			if (other.chromosome != null)
				return false;
		} else if (!chromosome.equals(other.chromosome))
			return false;
		if (location != other.location)
			return false;
		if (Double.doubleToLongBits(nfeAlleleFrequency) != Double.doubleToLongBits(other.nfeAlleleFrequency))
			return false;
		if (ref == null) {
			if (other.ref != null)
				return false;
		} else if (!ref.equals(other.ref))
			return false;
		return true;
	}
	
}
