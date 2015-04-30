package es.uvigo.ei.sing.pandrugsdb.core.variantsanalysis.vcf;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;

public interface VCFVariantDataBuilder<M extends VCFMetaData, V extends VCFVariant<M>> {

	public VCFVariantDataBuilder<M, V> setMetadata(M metadata);

	public VCFVariantDataBuilder<M, V> startVariant(String seqName, long position,
			String referenceAllele,
			Set<String> alternativeAlleles);

	public VCFVariantDataBuilder<M, V> setVariantQuality(double quality);

	//filters
	public VCFVariantDataBuilder<M, V> setVariantHasNoFilters();
	public VCFVariantDataBuilder<M, V> setVariantPassesFilters();
	public VCFVariantDataBuilder<M, V> setVariantFilters(List<String> filters);
	
	public VCFVariantDataBuilder<M, V> setVariantInfo(Map<String, List<String>> info);
	
	public VCFVariantDataBuilder<M, V> setVariantId(String variantId);
	
	public VCFVariantDataBuilder<M, V> addVariantSample(String sampleId,
			Map<String, List<String>> sampleData);
	
	public VCFVariantDataBuilder<M, V> endVariant();
	
	public Collection<V> build();


}
