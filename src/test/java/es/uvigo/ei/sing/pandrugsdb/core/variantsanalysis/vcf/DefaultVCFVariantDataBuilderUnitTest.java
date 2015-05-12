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
import static org.easymock.EasyMock.expectLastCall;
import static org.hamcrest.collection.IsIterableContainingInAnyOrder.containsInAnyOrder;
import static org.junit.Assert.assertThat;
import static org.powermock.api.easymock.PowerMock.expectNew;
import static org.powermock.api.easymock.PowerMock.replay;
import static org.powermock.api.easymock.PowerMock.verify;

import java.util.List;

import org.easymock.EasyMock;
import org.easymock.EasyMockRule;
import org.easymock.TestSubject;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import com.google.common.collect.ImmutableMap;

@RunWith(PowerMockRunner.class)
@PrepareForTest({
	DefaultVCFVariantDataBuilder.class
})
public class DefaultVCFVariantDataBuilderUnitTest {
	@Rule	
	public EasyMockRule rule = new EasyMockRule(this);
	
	
	@TestSubject
	private DefaultVCFVariantDataBuilder builder = 
							new DefaultVCFVariantDataBuilder();
	
	@SuppressWarnings("unchecked")
	@Test
	public void testBuild() throws Exception {
		
		VCFMetaData metadata = new SimpleVCFDataset().getMetadata();
		
		VCFVariant<VCFMetaData> variant1 = EasyMock.createMock(VCFVariant.class);
		
		expectNew(VCFVariant.class, "2", 4370l, "G", asSet("A"), metadata)
			.andReturn(variant1);
		variant1.setId("rs6057"); expectLastCall().once();
		variant1.setQuality(29d); expectLastCall().once();
		variant1.setNotPassedFilters(null); expectLastCall().once();
		variant1.putInfoValue("NS", asList(2)); expectLastCall().once();
		variant1.putInfoValue("DP", asList(13)); expectLastCall().once();
		variant1.putInfoValue("AF", asList(0.5f)); expectLastCall().once();
		variant1.putInfoValue("DB", asList()); expectLastCall().once();
		variant1.putInfoValue("H2", asList()); expectLastCall().once();
		
		variant1.putSampleValue("Sample1", "GT", asList("0|0")); expectLastCall().once();
		variant1.putSampleValue("Sample1", "GQ", asList(48)); expectLastCall().once();
		variant1.putSampleValue("Sample1", "DP", asList(1)); expectLastCall().once();
		variant1.putSampleValue("Sample1", "HQ", asList(52, 51)); expectLastCall().once();
		
		variant1.putSampleValue("Sample2", "GT", asList("1|0")); expectLastCall().once();
		variant1.putSampleValue("Sample2", "GQ", asList(48)); expectLastCall().once();
		variant1.putSampleValue("Sample2", "DP", asList(8)); expectLastCall().once();
		variant1.putSampleValue("Sample2", "HQ", asList(51, 51)); expectLastCall().once();
		
		variant1.putSampleValue("Sample3", "GT", asList("1/1")); expectLastCall().once();
		variant1.putSampleValue("Sample3", "GQ", asList(43)); expectLastCall().once();
		variant1.putSampleValue("Sample3", "DP", asList(5)); expectLastCall().once();
		
		VCFVariant<VCFMetaData> variant2 = EasyMock.createMock(VCFVariant.class);
		expectNew(VCFVariant.class, "2", 7330l, "T", asSet("A"), metadata)
		.andReturn(variant2);
		variant2.setQuality(3d); expectLastCall().once();
		variant2.setNotPassedFilters(asList("q10")); expectLastCall().once();
		variant2.putInfoValue("NS", asList(5)); expectLastCall().once();
		variant2.putInfoValue("DP", asList(12)); expectLastCall().once();
		variant2.putInfoValue("AF", asList(0.017f)); expectLastCall().once();
		
		variant2.putSampleValue("Sample1", "GT", asList("0|0")); expectLastCall().once();
		variant2.putSampleValue("Sample1", "GQ", asList(46)); expectLastCall().once();
		variant2.putSampleValue("Sample1", "DP", asList(3)); expectLastCall().once();
		variant2.putSampleValue("Sample1", "HQ", asList(58, 50)); expectLastCall().once();
		
		variant2.putSampleValue("Sample2", "GT", asList("0|1")); expectLastCall().once();
		variant2.putSampleValue("Sample2", "GQ", asList(3)); expectLastCall().once();
		variant2.putSampleValue("Sample2", "DP", asList(5)); expectLastCall().once();
		variant2.putSampleValue("Sample2", "HQ", asList(65, 3)); expectLastCall().once();
		
		variant2.putSampleValue("Sample3", "GT", asList("0/0")); expectLastCall().once();
		variant2.putSampleValue("Sample3", "GQ", asList(41)); expectLastCall().once();
		variant2.putSampleValue("Sample3", "DP", asList(3)); expectLastCall().once();
		
		VCFVariant<VCFMetaData> variant3 = EasyMock.createMock(VCFVariant.class);
		expectNew(VCFVariant.class, "2", 110696l, "A", asSet("G", "T"), metadata)
		.andReturn(variant3);
		variant3.setQuality(67d); expectLastCall().once();
		variant3.setNotPassedFilters(asList()); expectLastCall().once();
		variant3.putInfoValue("NS", asList(2)); expectLastCall().once();
		variant3.putInfoValue("DP", asList(10)); expectLastCall().once();
		variant3.putInfoValue("AF", asList(0.333f, 0.667f)); expectLastCall().once();
		variant3.putInfoValue("AA", asList("T")); expectLastCall().once();
		variant3.putInfoValue("DB", asList()); expectLastCall().once();
		
		variant3.putSampleValue("Sample1", "GT", asList("1|2")); expectLastCall().once();
		variant3.putSampleValue("Sample1", "GQ", asList(21)); expectLastCall().once();
		variant3.putSampleValue("Sample1", "DP", asList(6)); expectLastCall().once();
		variant3.putSampleValue("Sample1", "HQ", asList(23, 27)); expectLastCall().once();
		
		variant3.putSampleValue("Sample2", "GT", asList("2|1")); expectLastCall().once();
		variant3.putSampleValue("Sample2", "GQ", asList(2)); expectLastCall().once();
		variant3.putSampleValue("Sample2", "DP", asList(0)); expectLastCall().once();
		variant3.putSampleValue("Sample2", "HQ", asList(18, 2)); expectLastCall().once();
		
		variant3.putSampleValue("Sample3", "GT", asList("2/2")); expectLastCall().once();
		variant3.putSampleValue("Sample3", "GQ", asList(35)); expectLastCall().once();
		variant3.putSampleValue("Sample3", "DP", asList(4)); expectLastCall().once();
		
		replay(VCFVariant.class, variant1, variant2, variant3);
		
		builder.setMetadata(metadata);
		
		builder.startVariant("2", 4370, "G", asSet("A"));
		builder.setVariantId("rs6057");
		builder.setVariantQuality(29);
		builder.setVariantHasNoFilters();
		builder.setVariantInfo(new ImmutableMap.Builder<String, List<String>>()
				.put("NS", asList("2"))
				.put("DP", asList("13"))
				.put("AF", asList("0.5"))
				.put("DB", asList())
				.put("H2", asList())
				.build());
		
		builder.addVariantSample("Sample1", new ImmutableMap.Builder<String, List<String>>()
				.put("GT", asList("0|0"))
				.put("GQ", asList("48"))
				.put("DP", asList("1"))
				.put("HQ", asList("52", "51"))
				.build());
		
		builder.addVariantSample("Sample2", new ImmutableMap.Builder<String, List<String>>()
				.put("GT", asList("1|0"))
				.put("GQ", asList("48"))
				.put("DP", asList("8"))
				.put("HQ", asList("51", "51"))
				.build());
		
		builder.addVariantSample("Sample3", new ImmutableMap.Builder<String, List<String>>()
				.put("GT", asList("1/1"))
				.put("GQ", asList("43"))
				.put("DP", asList("5"))
				.build());
		builder.endVariant();
		
		builder.startVariant("2", 7330, "T", asSet("A"));
		builder.setVariantQuality(3);
		builder.setVariantFilters(asList("q10"));
		builder.setVariantInfo(new ImmutableMap.Builder<String, List<String>>()
				.put("NS", asList("5"))
				.put("DP", asList("12"))
				.put("AF", asList("0.017"))
				.build());
		
		builder.addVariantSample("Sample1", new ImmutableMap.Builder<String, List<String>>()
				.put("GT", asList("0|0"))
				.put("GQ", asList("46"))
				.put("DP", asList("3"))
				.put("HQ", asList("58", "50"))
				.build());
		
		builder.addVariantSample("Sample2", new ImmutableMap.Builder<String, List<String>>()
				.put("GT", asList("0|1"))
				.put("GQ", asList("3"))
				.put("DP", asList("5"))
				.put("HQ", asList("65", "3"))
				.build());
		
		builder.addVariantSample("Sample3", new ImmutableMap.Builder<String, List<String>>()
				.put("GT", asList("0/0"))
				.put("GQ", asList("41"))
				.put("DP", asList("3"))
				.build());
		builder.endVariant();
		
		builder.startVariant("2", 110696l, "A", asSet("G", "T"));
		builder.setVariantQuality(67);
		builder.setVariantPassesFilters();
		builder.setVariantInfo(new ImmutableMap.Builder<String, List<String>>()
				.put("NS", asList("2"))
				.put("DP", asList("10"))
				.put("AF", asList("0.333", "0.667"))
				.put("AA", asList("T"))
				.put("DB", asList())
				.build());
		
		builder.addVariantSample("Sample1", new ImmutableMap.Builder<String, List<String>>()
				.put("GT", asList("1|2"))
				.put("GQ", asList("21"))
				.put("DP", asList("6"))
				.put("HQ", asList("23", "27"))
				.build());
		
		builder.addVariantSample("Sample2", new ImmutableMap.Builder<String, List<String>>()
				.put("GT", asList("2|1"))
				.put("GQ", asList("2"))
				.put("DP", asList("0"))
				.put("HQ", asList("18", "2"))
				.build());
		
		builder.addVariantSample("Sample3", new ImmutableMap.Builder<String, List<String>>()
				.put("GT", asList("2/2"))
				.put("GQ", asList("35"))
				.put("DP", asList("4"))
				.build());
		builder.endVariant();
		
		assertThat(builder.build(), 
			containsInAnyOrder(variant1, variant2, variant3));
		
		verify(VCFVariant.class, variant1, variant2, variant3);
	}
}
