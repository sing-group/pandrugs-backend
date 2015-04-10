package es.uvigo.ei.sing.pandrugsdb.core.vcfanalysis;

import java.net.URL;

public interface VariantCallingCandidateDrugComputer {
	public VariantCallingCandidateDrugComputation createComputation(URL vcfFile); 
}
