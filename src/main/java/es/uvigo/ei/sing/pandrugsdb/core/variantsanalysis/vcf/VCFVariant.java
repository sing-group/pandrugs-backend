package es.uvigo.ei.sing.pandrugsdb.core.variantsanalysis.vcf;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import es.uvigo.ei.sing.pandrugsdb.core.variantsanalysis.Variant;


public class VCFVariant<M extends VCFMetaData> extends Variant {

	private M metadata;
	private String id;
	private double quality;
	
	private List<String> filters;
	private Map<String, List<Object>> infoValues = new HashMap<>();
	
	private Map<String, Map<String, Object>> samplesData;
	
	public VCFVariant(String sequenceName, 
			long position, String referenceAllele,
			Set<String> alternativeAlleles,
			
			M metadata) {
		
		super(sequenceName, position, referenceAllele, alternativeAlleles);
		
		this.metadata = metadata;
	}
	
	public void setId(String id) {
		this.id = id;
	}
	
	public String getId() {
		return id;
	}
	
	public double getQuality() {
		return quality;
	}
	
	void setQuality(double quality) {
		this.quality = quality;
	}
	
	public void setNotPassedFilters(List<String> filters) {
		this.filters = filters;
	}
	
	public boolean hasPassedFilters() {
		return this.filters != null && this.filters.isEmpty();
	}
	
	public boolean hasBeenFiltered() {
		return this.filters != null;
	}
	
	public void putInfoValue(String id, List<Object> values) {
		final String numberS = this.metadata.getInfoAttributeNumber(id);
		
		try {
			int number = Integer.parseInt(numberS);
			
			if (values.size() != number) {
				throw new IllegalArgumentException("Info attribute "+id+" "
						+ "must have "+number+" values");
			}
					
		} catch (NumberFormatException e) {
			if (numberS.equals("A")) {
				if (values.size() != this.getAlternativeAlleles().size()) {
					throw new IllegalArgumentException("Info attribute "+id+" "
							+ "must have "+this.getAlternativeAlleles().size()
							+ " values, the same as its alternative alleles");
				}
			}
		}
		this.infoValues.put(id, values);
	}
	
	public Object getSampleValue(String sampleId, String valueId) {
		return samplesData.get(sampleId).get(valueId);
	}
	
	public void putSampleValue(String sampleId, String valueId, Object value) {
		if (!metadata.getSampleIds().contains(sampleId)) {
			throw new IllegalArgumentException(
					"metadata does not contain this sample id: "+sampleId);
		}
		_putSampleValue(sampleId, valueId, value);
	}
	
	private void _putSampleValue(String sampleId, String valueId, Object value) {
		Map<String, Object> sampleData = samplesData.get(sampleId);
		
		if (sampleData == null) {
			sampleData =  new HashMap<>();
			samplesData.put(sampleId, sampleData);
		}
		
		sampleData.put(valueId, value);
	}
}
