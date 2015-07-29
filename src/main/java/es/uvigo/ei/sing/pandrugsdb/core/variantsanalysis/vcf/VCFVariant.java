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
	
	private Map<String, Map<String, Object>> samplesData = new HashMap<>();
	
	public VCFVariant(String sequenceName, 
			long position, String referenceAllele,
			Set<String> alternativeAlleles,
			
			M metadata) {
		
		super(sequenceName, position, referenceAllele, alternativeAlleles);
		
		this.metadata = metadata;
	}
	
	public M getMetadata() {
		return metadata;
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
	
	public List<Object> getInfoValue(String id) {
		return this.infoValues.get(id);
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
