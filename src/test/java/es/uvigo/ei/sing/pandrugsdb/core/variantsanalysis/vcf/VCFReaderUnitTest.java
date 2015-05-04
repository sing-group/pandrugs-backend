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
import static org.easymock.EasyMock.capture;
import static org.easymock.EasyMock.createNiceMock;
import static org.easymock.EasyMock.createStrictMock;
import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.newCapture;
import static org.easymock.EasyMock.replay;
import static org.easymock.EasyMock.verify;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.easymock.Capture;
import org.easymock.EasyMockRunner;
import org.junit.Test;
import org.junit.runner.RunWith;

import com.google.common.collect.ImmutableMap;


@RunWith(EasyMockRunner.class)
public class VCFReaderUnitTest {

/*
##fileformat=VCFv4.0
##fileDate=20110705
##reference=1000GenomesPilot-NCBI37
##phasing=partial
##INFO=<ID=NS,Number=1,Type=Integer,Description="Number of Samples With Data">
##INFO=<ID=DP,Number=1,Type=Integer,Description="Total Depth">
##INFO=<ID=AF,Number=.,Type=Float,Description="Allele Frequency">
##INFO=<ID=AA,Number=1,Type=String,Description="Ancestral Allele">
##INFO=<ID=DB,Number=0,Type=Flag,Description="dbSNP membership, build 129">
##INFO=<ID=H2,Number=0,Type=Flag,Description="HapMap2 membership">
##FILTER=<ID=q10,Description="Quality below 10">
##FILTER=<ID=s50,Description="Less than 50% of samples have data">
##FORMAT=<ID=GQ,Number=1,Type=Integer,Description="Genotype Quality">
##FORMAT=<ID=GT,Number=1,Type=String,Description="Genotype">
##FORMAT=<ID=DP,Number=1,Type=Integer,Description="Read Depth">
##FORMAT=<ID=HQ,Number=2,Type=Integer,Description="Haplotype Quality">
#CHROM POS    ID        REF  ALT     QUAL FILTER INFO                              FORMAT      Sample1        Sample2        Sample3
2      4370   rs6057    G    A       29   .      NS=2;DP=13;AF=0.5;DB;H2           GT:GQ:DP:HQ 0|0:48:1:52,51 1|0:48:8:51,51 1/1:43:5:.,.
2      7330   .         T    A       3    q10    NS=5;DP=12;AF=0.017               GT:GQ:DP:HQ 0|0:46:3:58,50 0|1:3:5:65,3   0/0:41:3
2      110696 rs6055    A    G,T     67   PASS   NS=2;DP=10;AF=0.333,0.667;AA=T;DB GT:GQ:DP:HQ 1|2:21:6:23,27 2|1:2:0:18,2   2/2:35:4
*/
	
	private URL aBasicVCF = getClass().getResource("basic.vcf");
	
	@SuppressWarnings({"unchecked"})
	@Test
	public void testReadBuildsMetadataWithBasicVCF() throws IOException, VCFParseException {
		
		final VCFMetaDataBuilder<VCFMetaData> metadataBuilder = 
				createNiceMock(VCFMetaDataBuilder.class);	
		final VCFMetaData metadata = createNiceMock(VCFMetaData.class);
		expect(metadata.getSampleIds())
			.andReturn(asList("Sample1", "Sample2", "Sample3")).anyTimes();
		expect(metadataBuilder.build())
			.andReturn(metadata);
		
		expect(metadataBuilder.setSamples(
				asList("Sample1", "Sample2", "Sample3")))
		.andReturn(metadataBuilder);
		
		expect(metadataBuilder.addAttribute("fileformat", "VCFv4.0"))
			.andReturn(metadataBuilder);
		//ID=DB,Number=0,Type=Flag,Description="dbSNP membership, build 129"
		final Map<String, String> anExpectedInfoAttribute = new HashMap<>();
		anExpectedInfoAttribute.put("ID", "DB");
		anExpectedInfoAttribute.put("Type", "Flag");
		anExpectedInfoAttribute.put("Number", "0");
		anExpectedInfoAttribute.put("Description", "dbSNP membership, build 129");

		expect(metadataBuilder.addAttribute("INFO", anExpectedInfoAttribute))
			.andReturn(metadataBuilder);
		
		final VCFVariantDataBuilder<VCFMetaData, VCFVariant<VCFMetaData>> variantDataBuilder = 
				createNiceMock(VCFVariantDataBuilder.class);

		Object[] mocks = {metadata, metadataBuilder, variantDataBuilder};

		replay(mocks);
		
		final VCFReader<VCFMetaData, VCFVariant<VCFMetaData> > reader = 
				new VCFReader<>(this.aBasicVCF, 
						metadataBuilder, variantDataBuilder);
		
		reader.getMetadata();

		verify(mocks);
	}
	
	@SuppressWarnings("unchecked")
	@Test
	public void testReadBuildsVariantsDataWithBasicVCF() 
			throws IOException, VCFParseException {
		
		final VCFMetaDataBuilder<VCFMetaData> metadataBuilder = 
				createNiceMock(VCFMetaDataBuilder.class);	
		final VCFMetaData metadata = createNiceMock(VCFMetaData.class);
		expect(metadata.getSampleIds()).andReturn(
				asList("Sample1", "Sample2", "Sample3")).anyTimes();
		
		expect(metadataBuilder.build()).andReturn(metadata);
		
		final VCFVariantDataBuilder<VCFMetaData, VCFVariant<VCFMetaData>> variantDataBuilder = 
				createStrictMock(VCFVariantDataBuilder.class);

		Capture<VCFMetaData> captureMetadata = newCapture();
		expect(variantDataBuilder.setMetadata(capture(captureMetadata)))
			.andReturn(variantDataBuilder);
		
		// ROW 1
		expect(variantDataBuilder.startVariant(
				"2", 4370, "G",	asSet("A")))
				.andReturn(variantDataBuilder);		
		expect(variantDataBuilder.setVariantId("rs6057"))
				.andReturn(variantDataBuilder);
		expect(variantDataBuilder.setVariantQuality(29f))
				.andReturn(variantDataBuilder);
		expect(variantDataBuilder.setVariantHasNoFilters())
				.andReturn(variantDataBuilder);
		Map<String, List<String>> expectedVariant1Info = 
				new ImmutableMap.Builder<String, List<String>>()
			.put("NS", asList("2"))
			.put("DP", asList("13"))
			.put("AF", asList("0.5"))
			.put("DB", asList())
			.put("H2", asList())
		.build();
		expect(variantDataBuilder.setVariantInfo(expectedVariant1Info))
				.andReturn(variantDataBuilder);

		Map<String, List<String>> expectedVariant1Sample1Genotype = 
				new ImmutableMap.Builder<String, List<String>>()
				.put("GT", asList("0|0"))
				.put("GQ", asList("48"))
				.put("DP", asList("1"))
				.put("HQ", asList("52", "51"))
			.build();
		expect(variantDataBuilder.addVariantSample(
				"Sample1", expectedVariant1Sample1Genotype))
				.andReturn(variantDataBuilder);
		
		Map<String, List<String>> expectedVariant1Sample2Genotype = 
				new ImmutableMap.Builder<String, List<String>>()
				.put("GT", asList("1|0"))
				.put("GQ", asList("48"))
				.put("DP", asList("8"))
				.put("HQ", asList("51", "51"))
			.build();
		expect(variantDataBuilder.addVariantSample(
				"Sample2",expectedVariant1Sample2Genotype))
				.andReturn(variantDataBuilder);
		
		Map<String, List<String>> expectedVariant1Sample3Genotype = 
				new ImmutableMap.Builder<String, List<String>>()
				.put("GT", asList("1/1"))
				.put("GQ", asList("43"))
				.put("DP", asList("5"))
				.build();
		expect(variantDataBuilder.addVariantSample(
				"Sample3",expectedVariant1Sample3Genotype))
				.andReturn(variantDataBuilder);
		
		expect(variantDataBuilder.endVariant())
			.andReturn(variantDataBuilder);
		
		
		// ROW 2
		expect(variantDataBuilder.startVariant(
				"2", 7330, "T",	asSet("A")))
			.andReturn(variantDataBuilder);		
		expect(variantDataBuilder.setVariantQuality(3f))
			.andReturn(variantDataBuilder);
		expect(variantDataBuilder.setVariantFilters(asList("q10")))
			.andReturn(variantDataBuilder);
		Map<String, List<String>> expectedVariant2Info = 
				new ImmutableMap.Builder<String, List<String>>()
				.put("NS", asList("5"))
				.put("DP", asList("12"))
				.put("AF", asList("0.017"))				
				.build();
		expect(variantDataBuilder.setVariantInfo(expectedVariant2Info))
		.andReturn(variantDataBuilder);
		
		Map<String, List<String>> expectedVariant2Sample1Genotype = 
				new ImmutableMap.Builder<String, List<String>>()
				.put("GT", asList("0|0"))
				.put("GQ", asList("46"))
				.put("DP", asList("3"))
				.put("HQ", asList("58", "50"))
				.build();
		expect(variantDataBuilder.addVariantSample(
				"Sample1", expectedVariant2Sample1Genotype))
				.andReturn(variantDataBuilder);
		
		Map<String, List<String>> expectedVariant2Sample2Genotype = 
				new ImmutableMap.Builder<String, List<String>>()
				.put("GT", asList("0|1"))
				.put("GQ", asList("3"))
				.put("DP", asList("5"))
				.put("HQ", asList("65", "3"))
				.build();
		expect(variantDataBuilder.addVariantSample(
				"Sample2",expectedVariant2Sample2Genotype))
				.andReturn(variantDataBuilder);
		
		Map<String, List<String>> expectedVariant2Sample3Genotype = 
				new ImmutableMap.Builder<String, List<String>>()
				.put("GT", asList("0/0"))
				.put("GQ", asList("41"))
				.put("DP", asList("3"))
				.build();
		expect(variantDataBuilder.addVariantSample(
				"Sample3",expectedVariant2Sample3Genotype))
				.andReturn(variantDataBuilder);
		
		expect(variantDataBuilder.endVariant())
		.andReturn(variantDataBuilder);
		
		
		// ROW 3
		expect(variantDataBuilder.startVariant(
				"2", 110696, "A",	asSet("G", "T")))
			.andReturn(variantDataBuilder);
		expect(variantDataBuilder.setVariantId("rs6055"))
			.andReturn(variantDataBuilder);
		expect(variantDataBuilder.setVariantQuality(67f))
			.andReturn(variantDataBuilder);
		expect(variantDataBuilder.setVariantPassesFilters())
			.andReturn(variantDataBuilder);
		Map<String, List<String>> expectedVariant3Info = 
				new ImmutableMap.Builder<String, List<String>>()
				.put("NS", asList("2"))
				.put("DP", asList("10"))
				.put("AF", asList("0.333", "0.667"))				
				.put("AA", asList("T"))				
				.put("DB", asList())				
				.build();
		expect(variantDataBuilder.setVariantInfo(expectedVariant3Info))
		.andReturn(variantDataBuilder);
		
		Map<String, List<String>> expectedVariant3Sample1Genotype = 
				new ImmutableMap.Builder<String, List<String>>()
				.put("GT", asList("1|2"))
				.put("GQ", asList("21"))
				.put("DP", asList("6"))
				.put("HQ", asList("23", "27"))
				.build();
		expect(variantDataBuilder.addVariantSample(
				"Sample1", expectedVariant3Sample1Genotype))
				.andReturn(variantDataBuilder);
		
		Map<String, List<String>> expectedVariant3Sample2Genotype = 
				new ImmutableMap.Builder<String, List<String>>()
				.put("GT", asList("2|1"))
				.put("GQ", asList("2"))
				.put("DP", asList("0"))
				.put("HQ", asList("18", "2"))
				.build();
		expect(variantDataBuilder.addVariantSample(
				"Sample2",expectedVariant3Sample2Genotype))
				.andReturn(variantDataBuilder);
		
		Map<String, List<String>> expectedVariant3Sample3Genotype = 
				new ImmutableMap.Builder<String, List<String>>()
				.put("GT", asList("2/2"))
				.put("GQ", asList("35"))
				.put("DP", asList("4"))
				.build();
		expect(variantDataBuilder.addVariantSample(
				"Sample3",expectedVariant3Sample3Genotype))
				.andReturn(variantDataBuilder);
		
		expect(variantDataBuilder.endVariant())
			.andReturn(variantDataBuilder);
				
		
		expect(variantDataBuilder.build())
			.andReturn(new ArrayList<>());
		
		Object[] mocks = {metadata, metadataBuilder, variantDataBuilder};

		replay(mocks);
		
		// do work
		final VCFReader<VCFMetaData, VCFVariant<VCFMetaData>> reader = 
				new VCFReader<>(this.aBasicVCF, 
						metadataBuilder, variantDataBuilder);		
		
		reader.getVariants();

		// verify
		assertThat(reader.getMetadata(), is(captureMetadata.getValue()));
		verify(mocks);
		
	}

	
}
