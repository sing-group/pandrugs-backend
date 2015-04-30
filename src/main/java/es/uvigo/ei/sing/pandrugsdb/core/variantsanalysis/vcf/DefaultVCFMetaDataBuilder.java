package es.uvigo.ei.sing.pandrugsdb.core.variantsanalysis.vcf;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class DefaultVCFMetaDataBuilder implements VCFMetaDataBuilder<VCFMetaData> {

	private List<String> sampleNames = new LinkedList<>();
	private Map<String, String> simpleAttributes = new HashMap<>();
	private Map<String, List<Map<String, String>>> complexAttributes =
			new HashMap<>();
	
	@Override
	public VCFMetaDataBuilder<VCFMetaData> setSamples(List<String> sampleNames) {
		this.sampleNames = sampleNames;
		return this;
	}

	@Override
	public VCFMetaDataBuilder<VCFMetaData> addAttribute(String attName,
			String value) {
		this.simpleAttributes.put(attName, value);
		return this;
	}

	@Override
	public VCFMetaDataBuilder<VCFMetaData> addAttribute(String kind,
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
	public VCFMetaData build() {
		return new VCFMetaData(sampleNames, simpleAttributes, complexAttributes);
	}
}
