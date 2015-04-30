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
