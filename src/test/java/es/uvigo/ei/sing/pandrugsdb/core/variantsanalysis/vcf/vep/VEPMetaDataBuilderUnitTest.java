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
package es.uvigo.ei.sing.pandrugsdb.core.variantsanalysis.vcf.vep;

import static es.uvigo.ei.sing.pandrugsdb.TestUtils.asList;
import static org.easymock.EasyMock.eq;
import static org.powermock.api.easymock.PowerMock.expectNew;
import static org.powermock.api.easymock.PowerMock.replay;
import static org.powermock.api.easymock.PowerMock.verify;

import java.util.List;
import java.util.Map;

import org.easymock.EasyMockRule;
import org.easymock.Mock;
import org.easymock.TestSubject;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import com.google.common.collect.ImmutableMap;

@RunWith(PowerMockRunner.class)
@PrepareForTest({
	VEPMetaDataBuilder.class
})
public class VEPMetaDataBuilderUnitTest {
	@Rule	
	public EasyMockRule rule = new EasyMockRule(this);
	
	@Mock
	private VEPMetaData vEPMetaDataMock;
	
	@TestSubject
	private VEPMetaDataBuilder builder = new VEPMetaDataBuilder();
	
	@Test
	public void testBuild() throws Exception {
		final List<String> someSampleIds = asList("Sample1", "Sample2");
		
		final Map<String, String> expectedSimpleAttributes = 
				new ImmutableMap.Builder<String, String>()
				.put("fileformat", "VCFv4.0")
				.put("reference", "1000GenomesPilot-NCBI37")
				.build();
		final Map<String, List<Map<String, String>>> expectedComplexAttributes = 
				new ImmutableMap.Builder<String, List<Map<String, String>>>()
				.put("INFO", asList( 
						new ImmutableMap.Builder<String, String>()
						.put("ID", "NS")
						.put("Number", "1")
						.put("Type", "Integer")
						.put("Description", "Number of Samples With Data")
						.build(),
						new ImmutableMap.Builder<String, String>()
						.put("ID", "DP")
						.put("Number", "1")
						.put("Type", "Integer")
						.put("Description", "Allele Frequency")
						.build()))
				.put("FORMAT", asList( 
						new ImmutableMap.Builder<String, String>()
						.put("ID", "GT")
						.put("Number", "1")
						.put("Type", "String")
						.put("Description", "Genotype")
						.build()))
				.build();
		
		expectNew(VEPMetaData.class, eq(someSampleIds),
				eq(expectedSimpleAttributes), eq(expectedComplexAttributes)).andReturn(
				vEPMetaDataMock);
		
		replay(VEPMetaData.class, vEPMetaDataMock);
		
		builder.setSamples(someSampleIds);
		
		expectedSimpleAttributes.entrySet().stream().forEach(
			entry -> builder.addAttribute(entry.getKey(), entry.getValue()));
		
		expectedComplexAttributes.entrySet().stream().forEach(
			entry -> entry.getValue().stream().forEach(
					value -> builder.addAttribute(entry.getKey(), value)));
		
		builder.build();
		
		verify(VEPMetaData.class, vEPMetaDataMock);
		
		
	}
}



