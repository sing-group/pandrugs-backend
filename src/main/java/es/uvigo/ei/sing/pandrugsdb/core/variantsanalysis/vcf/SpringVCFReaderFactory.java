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

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.net.URL;
import java.util.List;

public class SpringVCFReaderFactory implements VCFReaderFactory {

	public List<VCFMetaDataBuilder<?>> getAvailableMetaDataImplementations() {
		return availableMetaDataImplementations;
	}

	public void setAvailableMetaDataImplementations(
			List<VCFMetaDataBuilder<?>> availableMetaDataImplementations) {
		this.availableMetaDataImplementations = availableMetaDataImplementations;
	}

	public List<VCFVariantDataBuilder<?, ?>> getAvailableDataImplementations() {
		return availableDataImplementations;
	}

	public void setAvailableDataImplementations(
			List<VCFVariantDataBuilder<?, ?>> availableDataImplementations) {
		this.availableDataImplementations = availableDataImplementations;
	}

	private List<VCFMetaDataBuilder<?>> availableMetaDataImplementations;
	private List<VCFVariantDataBuilder<?, ?>> availableDataImplementations;
	
	
	
	@SuppressWarnings("unchecked")
	@Override
	public <M extends VCFMetaData, V extends VCFVariant<M>> VCFReader<M, V> getReader(
			URL vcf,
			Class<V> variantClass, Class<M> metadataClass) {
		VCFMetaDataBuilder<M> foundMetadataBuilder = null;
		for (VCFMetaDataBuilder<?> concreteMetadataBuilder: getAvailableMetaDataImplementations()) {
			if (getGenericTypeParameterValue(concreteMetadataBuilder.getClass(), 0).equals(variantClass)) {
				foundMetadataBuilder = (VCFMetaDataBuilder<M>) concreteMetadataBuilder;
			}
		}
		
		if (foundMetadataBuilder == null) { 
			throw new IllegalArgumentException("Not found implementation for "
				+ "metadata class "+metadataClass);
		}
		
		VCFVariantDataBuilder<M, V> foundDataBuilder = null;
		for (VCFVariantDataBuilder<?, ?> concreteDataBuilder: getAvailableDataImplementations()) {
			if (getGenericTypeParameterValue(concreteDataBuilder.getClass(), 0).equals(variantClass) &&
					getGenericTypeParameterValue(concreteDataBuilder.getClass(), 1).equals(metadataClass)) {
				foundDataBuilder = (VCFVariantDataBuilder<M, V>) concreteDataBuilder;
			}
		}
		if (foundDataBuilder == null) {
			throw new IllegalArgumentException("Not found implementation for "
				+ "variant data class "+variantClass);
		}
		return new VCFReader<M, V>(vcf, foundMetadataBuilder, foundDataBuilder);
	}
	
	private Class<?> getGenericTypeParameterValue(Class<?> clazz, int genericParameter) {
		Type type = clazz.getGenericSuperclass();

        while (!(type instanceof ParameterizedType) || ((ParameterizedType) type).getRawType() != VCFReader.class) {
            if (type instanceof ParameterizedType) {
                type = ((Class<?>) ((ParameterizedType) type).getRawType()).getGenericSuperclass();
            } else {
                type = ((Class<?>) type).getGenericSuperclass();
            }
        }

        return (Class<?>) ((ParameterizedType) type).getActualTypeArguments()[genericParameter];
	}

}
