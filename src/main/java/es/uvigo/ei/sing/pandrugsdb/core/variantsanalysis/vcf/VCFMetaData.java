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

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public class VCFMetaData {

	private List<String> sampleIds;
	private Map<String, List<Map<String, String>>> complexAttributes;
	private Map<String, String> simpleAttributes;

	public VCFMetaData(
			List<String> sampleIds,
			Map<String, String> simpleAttributes,
			Map<String, List<Map<String, String>>> complexAttributes
			) {
	
		this.sampleIds = sampleIds;
		this.complexAttributes = complexAttributes;
		this.simpleAttributes = simpleAttributes;
	}
	
	public List<String> getSampleIds() {
		return sampleIds;
	}

	public Set<String> getAttributesIds(String kind) {
		return new 
			HashSet<>(complexAttributes.get(kind).stream()
					.map(map -> map.get("ID"))
			.collect(Collectors.toList()));
	}
	
	public List<Map<String, String>> getComplexAttributes(String kind) {
		return this.complexAttributes.get(kind);
	}
	
	public Map<String, String> getComplexAttributesById(String kind, String id) {
		if (!this.complexAttributes.containsKey(kind)) {
			throw new IllegalArgumentException("complex kind not found ##"+kind);
		}
		return this.complexAttributes.get(kind).stream()
					.filter(v -> v.get("ID").equalsIgnoreCase(id))
				.findFirst()
					.orElseThrow(
					()-> new IllegalArgumentException("id "+id+" not found"));
				
	}
	
	public Map<String, String> getSimpleAttributes() {
		return this.simpleAttributes;
	}
	
	public String getSimpleAttribute(String attributeName) {
		if (!this.simpleAttributes.containsKey(attributeName)) {
			throw new IllegalArgumentException(
					"simple attribute "+attributeName+" not found");
		}
		return this.simpleAttributes.get(attributeName);
	}
	
	public String getInfoAttributeNumber(String id) {
		return this.getComplexAttributesById("INFO", id).get("Number");
	}
	
	public Class<?> getInfoAttributeType(String id) {
		return getAttributeType("INFO", id);
	}
	
	public Class<?> getFormatAttributeType(String id) {
		return getAttributeType("FORMAT", id);
	}
	
	public Class<?> getAttributeType(String kind, String id) {
		String type = this.getComplexAttributesById(kind, id).get("Type");
		switch (type.toUpperCase()) {
			case "INTEGER": return Integer.class;
			case "STRING": return String.class;
			case "FLOAT": return Float.class;
			case "FLAG": return Boolean.class;
		}
		throw new IllegalArgumentException("unkown type ("+type+") for attribute ID "+id);
	}
	
	public String getFormatAttributeNumber(String id) {
		return this.getComplexAttributesById("FORMAT", id).get("Number");
	}
}
