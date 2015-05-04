/*
 * #%L
 * PanDrugsDB Backend
 * %%
 * Copyright (C) 2015 Fátima Al-Shahrour, Elena Piñeiro, Daniel Glez-Peña and Miguel Reboiro-Jato
 * %%
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public
 * License along with this program.  If not, see
 * <http://www.gnu.org/licenses/gpl-3.0.html>.
 * #L%
 */
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
