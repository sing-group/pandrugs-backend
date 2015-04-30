package es.uvigo.ei.sing.pandrugsdb.core.variantsanalysis.vcf;

import java.util.Set;

public class DefaultVCFVariantDataBuilder extends
		AbstractVCFVariantDataBuilder<VCFMetaData, VCFVariant<VCFMetaData>> {

	@Override
	protected VCFVariant<VCFMetaData> createVariant(String seqName, long position,
			String referenceAllele, Set<String> alternativeAlleles) {
		
		return new VCFVariant<>(seqName, position, referenceAllele,
				alternativeAlleles, metadata);
	}

}
