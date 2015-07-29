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

import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import es.uvigo.ei.sing.pandrugsdb.core.variantsanalysis.vcf.VCFMetaData;

public class VEPMetaData extends VCFMetaData {

	private List<String> cSQAttributes = new LinkedList<>();
	
	public VEPMetaData(List<String> sampleIds,
			Map<String, String> simpleAttributes,
			Map<String, List<Map<String, String>>> complexAttributes) {

		super(sampleIds, simpleAttributes, complexAttributes);
		
		// parse CSQ attributes
		
		Map<String, String> csqValue = this.getComplexAttributesById("INFO", "CSQ");
		
		String description = csqValue.get("Description");
		
		String format = description.substring(description.indexOf("Format: ")+8);
		
		cSQAttributes.addAll(Arrays.asList(format.split("\\|")));
	}
	
	public List<String> getCSQAttributes() {
		return Collections.unmodifiableList(cSQAttributes);
	}
}
