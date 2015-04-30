package es.uvigo.ei.sing.pandrugsdb.core.variantsanalysis.vcf;

import java.net.URL;

public interface VCFReaderFactory {

	public <M extends VCFMetaData, V extends VCFVariant<M>> VCFReader<M, V> 
	getReader(URL vcf, Class<V> variantClass, Class<M> metadataClass);
}
