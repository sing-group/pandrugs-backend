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

import static es.uvigo.ei.sing.pandrugsdb.TestUtils.asList;
import static es.uvigo.ei.sing.pandrugsdb.TestUtils.asSet;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;

import org.junit.Test;

public class VCFMetaDataUnitTest {
	
	private VCFMetaData metadata;

	public VCFMetaDataUnitTest() {
		this.metadata = new SimpleVCFDataset().getMetadata();
	}
	
	@Test
	public void testGetSampleIds() {
		assertThat(metadata.getSampleIds(), is(asList("Sample1", "Sample2", "Sample3")));
	}
	
	@Test
	public void testGetInfoAttributesIds() {
		assertThat(metadata.getAttributesIds("INFO"),
				is(asSet("NS", "DP", "AA", "AF", "H2", "DB")));
	}
	
	@Test
	public void testGetInfoAttributeNumber() {
		assertThat(metadata.getInfoAttributeNumber("NS"), is("1"));
		assertThat(metadata.getInfoAttributeNumber("DP"), is("1"));
		assertThat(metadata.getInfoAttributeNumber("AF"), is("."));
		assertThat(metadata.getInfoAttributeNumber("AA"), is("1"));
		assertThat(metadata.getInfoAttributeNumber("DB"), is("0"));
		assertThat(metadata.getInfoAttributeNumber("H2"), is("0"));
	}
	
	@Test
	public void testGetFormatAttributeNumber() {
		assertThat(metadata.getFormatAttributeNumber("GQ"), is("1"));
		assertThat(metadata.getFormatAttributeNumber("GT"), is("1"));
		assertThat(metadata.getFormatAttributeNumber("DP"), is("1"));
		assertThat(metadata.getFormatAttributeNumber("HQ"), is("2"));
	}
	
	@Test
	public void testGetInfoAttributeType() {
		assertEquals(Integer.class, metadata.getInfoAttributeType("NS"));
		assertEquals(Integer.class, metadata.getInfoAttributeType("DP"));
		assertEquals(Float.class, metadata.getInfoAttributeType("AF"));
		assertEquals(String.class, metadata.getInfoAttributeType("AA"));
		assertEquals(Boolean.class, metadata.getInfoAttributeType("DB"));
		assertEquals(Boolean.class, metadata.getInfoAttributeType("H2"));
	}
	
	@Test
	public void testGetFormatAttributeType() {
		assertEquals(Integer.class, metadata.getFormatAttributeType("GQ"));
		assertEquals(String.class, metadata.getFormatAttributeType("GT"));
		assertEquals(Integer.class, metadata.getFormatAttributeType("DP"));
		assertEquals(Integer.class, metadata.getFormatAttributeType("HQ"));
	}
	
}
