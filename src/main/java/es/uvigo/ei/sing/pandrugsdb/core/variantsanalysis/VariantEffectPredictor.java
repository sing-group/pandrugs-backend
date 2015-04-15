package es.uvigo.ei.sing.pandrugsdb.core.variantsanalysis;

import java.net.URL;

public interface VariantEffectPredictor {

	public VariantsEffectPredictionResults predictEffect(URL vcfFile);
}
