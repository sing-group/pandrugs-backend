/*
 * #%L
 * PanDrugsDB Backend
 * %%
 * Copyright (C) 2015 Fátima Al-Shahrour, Elena Piñeiro, Daniel Glez-Peña and Miguel Reboiro-Jato
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
package es.uvigo.ei.sing.pandrugsdb.core.variantsanalysis.vcf.vep;

import java.util.Set;

import es.uvigo.ei.sing.pandrugsdb.core.variantsanalysis.vcf.VCFVariant;

public class VEPVariant extends VCFVariant<VEPMetaData> {

	public VEPVariant(String sequenceName, long position,
			String referenceAllele, Set<String> alternativeAlleles,
			VEPMetaData metadata) {
		super(sequenceName, position, referenceAllele, alternativeAlleles,
				metadata);
	}

	public Object getCSQCount() {
		return this.getInfoValue("CSQ").size();
	}

	public Object getCSQAttribute(int number, String attributeName) {
		// provisional implementation

		String[] csqValues = this.getInfoValue("CSQ").get(number).toString()
				.split("\\|", this.getMetadata().getCSQAttributes().size());
		String result = csqValues[this.getMetadata().getCSQAttributes()
				.indexOf(attributeName)].trim();

		if (result.length() == 0) {
			return null;
		} else {
			return result;
		}
	}

}
