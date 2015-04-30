package es.uvigo.ei.sing.pandrugsdb.core.variantsanalysis.vcf;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

public abstract class AbstractVCFVariantDataBuilder
				<M extends VCFMetaData, V extends VCFVariant<M>> 
				implements VCFVariantDataBuilder<M, V> {

	protected M metadata;
	private V currentVariant;

	private Collection<V> variants = new LinkedList<>();
	
	@Override
	public VCFVariantDataBuilder<M, V> setMetadata(M metadata) {
		this.metadata = metadata;
		return this;
	}

	@Override
	public VCFVariantDataBuilder<M, V> startVariant(String seqName,
			long position, String referenceAllele,
			Set<String> alternativeAlleles) {
		this.currentVariant = createVariant(seqName, position, referenceAllele, alternativeAlleles);
		return this;
	}

	@Override
	public VCFVariantDataBuilder<M, V> setVariantId(String variantId) {		
		this.currentVariant.setId(variantId);
		return this;
	}
	
	@Override
	public VCFVariantDataBuilder<M, V> setVariantQuality(double quality) {
		this.currentVariant.setQuality(quality);
		return this;
	}

	@Override
	public VCFVariantDataBuilder<M, V> setVariantHasNoFilters() {
		this.currentVariant.setNotPassedFilters(null);
		return this;
	}

	@Override
	public VCFVariantDataBuilder<M, V> setVariantPassesFilters() {
		this.currentVariant.setNotPassedFilters(new ArrayList<>());
		return this;
	}

	@Override
	public VCFVariantDataBuilder<M, V> setVariantFilters(List<String> filters) {
		this.currentVariant.setNotPassedFilters(filters);
		return this;
	}

	@Override
	public VCFVariantDataBuilder<M, V> setVariantInfo(
			Map<String, List<String>> info) {
		
		
		for (String infoAttribute : info.keySet()) {
			Class<?> type = metadata.getInfoAttributeType(infoAttribute);
			List<Object> values = new ArrayList<>(info.get(infoAttribute).size());
			for (String infoValue : info.get(infoAttribute)) {
				values.add(parseValue(type, infoValue));
			}
			this.currentVariant.putInfoValue(infoAttribute, values);
		}

		return this;
	}

	@Override
	public VCFVariantDataBuilder<M, V> addVariantSample(String sampleId,
			Map<String, List<String>> sampleData) {
		// parse datatypes
		for (String formatAttribute : sampleData.keySet()) {
			Class<?> type = metadata.getFormatAttributeType(formatAttribute);
			List<Object> values = new ArrayList<>(sampleData.get(formatAttribute).size());
			for (String infoValue : sampleData.get(formatAttribute)) {
				values.add(parseValue(type, infoValue));
			}
			this.currentVariant.putSampleValue(sampleId, formatAttribute, values);
		}
		
		return this;
	}

	private Object parseValue(Class<?> type, String infoValue) {
		if (type.equals(String.class)) {
			return infoValue;
		} else if (type.equals(Integer.class)) {
			return Integer.parseInt(infoValue);
		} else if (type.equals(Float.class)) {
			return Float.parseFloat(infoValue);
		} else if (type.equals(Boolean.class)) {
			return new Boolean(true);
		}
		throw new IllegalArgumentException("Class not valid " + type
				+ ". It must be String, Integer, Float or Boolean");
	}

	

	@Override
	public VCFVariantDataBuilder<M, V> endVariant() {
		this.variants.add(this.currentVariant);
		this.currentVariant = null;
		return this;
	}

	@Override
	public Collection<V> build() {
		return this.variants;
	}

	protected abstract V createVariant(String seqName, long position,
			String referenceAllele, Set<String> alternativeAlleles);

}
