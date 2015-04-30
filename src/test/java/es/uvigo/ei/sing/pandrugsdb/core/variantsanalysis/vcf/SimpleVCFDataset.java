package es.uvigo.ei.sing.pandrugsdb.core.variantsanalysis.vcf;

import static es.uvigo.ei.sing.pandrugsdb.TestUtils.asList;

import java.util.List;
import java.util.Map;

import com.google.common.collect.ImmutableMap;

public class SimpleVCFDataset {
	
	private final List<String> samplesIds = asList("Sample1", "Sample2", "Sample3");
	
	private final Map<String, String> simpleAttributes = 
			new ImmutableMap.Builder<String, String>()
			.put("fileformat", "VCFv4.0")
			.put("fileData", "20110705")
		.build();
	
	private final Map<String, List<Map<String, String>>> complexAttributes =
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
					.put("Description", "Total Depth")
					.build(),
					new ImmutableMap.Builder<String, String>()
					.put("ID", "AF")
					.put("Number", ".")
					.put("Type", "Float")
					.put("Description", "Allele Frequency")
					.build(),
					new ImmutableMap.Builder<String, String>()
					.put("ID", "AA")
					.put("Number", "1")
					.put("Type", "String")
					.put("Description", "Ancestral Allele")
					.build(),
					new ImmutableMap.Builder<String, String>()
					.put("ID", "DB")
					.put("Number", "0")
					.put("Type", "Flag")
					.put("Description", "dbSNP membership, build 129")
					.build(),
					new ImmutableMap.Builder<String, String>()
					.put("ID", "H2")
					.put("Number", "0")
					.put("Type", "Flag")
					.put("Description", "HapMap2 membership")
					.build()
					))
			.put("FILTER", asList( 
					new ImmutableMap.Builder<String, String>()
					.put("ID", "q10")
					.put("Description", "Quality below 10")
					.build(),
					new ImmutableMap.Builder<String, String>()
					.put("ID", "s50")
					.put("Description", "Less than 50% of samples have data")
					.build()
					))
			.put("FORMAT", asList( 
					new ImmutableMap.Builder<String, String>()
					.put("ID", "GQ")
					.put("Number", "1")
					.put("Type", "Integer")
					.put("Description", "Genotype Quality")
					.build(),
					new ImmutableMap.Builder<String, String>()
					.put("ID", "GT")
					.put("Number", "1")
					.put("Type", "String")
					.put("Description", "Genotype")
					.build(),
					new ImmutableMap.Builder<String, String>()
					.put("ID", "DP")
					.put("Number", "1")
					.put("Type", "Integer")
					.put("Description", "Read Depth")
					.build(),
					new ImmutableMap.Builder<String, String>()
					.put("ID", "HQ")
					.put("Number", "2")
					.put("Type", "Integer")
					.put("Description", "Haplotype Quality")
					.build()
					))
			.build();
	
	private VCFMetaData metadata;
	
	public SimpleVCFDataset() {
		this.metadata = new VCFMetaData(
				samplesIds, 
				simpleAttributes,
				complexAttributes);
	}
	
	public List<String> getSamplesIds() {
		return samplesIds;
	}
	
	public Map<String, String> getSimpleAttributes() {
		return simpleAttributes;
	}
	
	public Map<String, List<Map<String, String>>> getComplexAttributes() {
		return complexAttributes;
	}
	
	public VCFMetaData getMetadata() {
		return metadata;
	}
}
