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
package es.uvigo.ei.sing.pandrugsdb.core.variantsanalysis.vcf;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public abstract class AbstractVCFMetaDataBuilder<M extends VCFMetaData> implements VCFMetaDataBuilder<M> {

	protected List<String> sampleNames = new LinkedList<>();
	protected Map<String, String> simpleAttributes = new HashMap<>();
	protected Map<String, List<Map<String, String>>> complexAttributes =
			new HashMap<>();
	
	@Override
	public VCFMetaDataBuilder<M> setSamples(List<String> sampleNames) {
		this.sampleNames = sampleNames;
		return this;
	}

	@Override
	public VCFMetaDataBuilder<M> addAttribute(String attName,
			String value) {
		this.simpleAttributes.put(attName, value);
		return this;
	}

	@Override
	public VCFMetaDataBuilder<M> addAttribute(String kind,
			Map<String, String> value) {
		
		List<Map<String, String>> currentValues = complexAttributes.get(kind);
		
		if (currentValues == null) {
			currentValues = new LinkedList<>();
			complexAttributes.put(kind, currentValues);
		}
		
		currentValues.add(value);
		
		return this;
	}

	@Override
	public abstract M build();
}
