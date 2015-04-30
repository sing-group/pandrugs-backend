package es.uvigo.ei.sing.pandrugsdb.core.variantsanalysis.vcf;

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
	DefaultVCFMetaDataBuilder.class
})
public class DefaultVCFMetaDataBuilderUnitTest {
	@Rule	
	public EasyMockRule rule = new EasyMockRule(this);
	
	@Mock
	private VCFMetaData vCFMetaDataMock;
	
	@TestSubject
	private DefaultVCFMetaDataBuilder builder = new DefaultVCFMetaDataBuilder();
	
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
		
		expectNew(VCFMetaData.class, eq(someSampleIds),
				eq(expectedSimpleAttributes), eq(expectedComplexAttributes)).andReturn(
				vCFMetaDataMock);
		
		replay(VCFMetaData.class, vCFMetaDataMock);
		
		builder.setSamples(someSampleIds);
		
		expectedSimpleAttributes.entrySet().stream().forEach(
			entry -> builder.addAttribute(entry.getKey(), entry.getValue()));
		
		expectedComplexAttributes.entrySet().stream().forEach(
			entry -> entry.getValue().stream().forEach(
					value -> builder.addAttribute(entry.getKey(), value)));
		
		builder.build();
		
		verify(VCFMetaData.class, vCFMetaDataMock);
		
		
	}
}



