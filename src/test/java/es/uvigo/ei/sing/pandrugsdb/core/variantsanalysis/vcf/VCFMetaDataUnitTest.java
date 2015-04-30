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
