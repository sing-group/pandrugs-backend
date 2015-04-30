package es.uvigo.ei.sing.pandrugsdb.core.variantsanalysis.vcf;

import java.util.List;
import java.util.Map;

public interface VCFMetaDataBuilder<M extends VCFMetaData> {

	public VCFMetaDataBuilder<M> setSamples(List<String> sampleNames);
	
	public VCFMetaDataBuilder<M> addAttribute(String attName, String value);
	
	public VCFMetaDataBuilder<M> addAttribute(String kind, Map<String, String> value);
	
	public M build();
	
}
