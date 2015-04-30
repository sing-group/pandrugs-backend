package es.uvigo.ei.sing.pandrugsdb.core.variantsanalysis.vcf;

import static java.util.Arrays.asList;
import static java.util.Arrays.stream;
import static java.util.stream.Collectors.toMap;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public class VCFReader<M extends VCFMetaData, V extends VCFVariant<M>> {

	private static final String SEP = "\t";
	private Collection<V> variants;
	private M metadata;
	
	private int lineCounter = 1;
	private URL vcf;
	private VCFMetaDataBuilder<M> metadataBuilder;
	private VCFVariantDataBuilder<M, V> variantDataBuilder;
	private boolean read = false;
	
	
	public VCFReader(
					URL vcf, 
					VCFMetaDataBuilder<M> metadataBuilder,
					VCFVariantDataBuilder<M, V> variantDataBuilder)	{

		this.vcf = vcf;
		this.metadataBuilder = metadataBuilder;
		this.variantDataBuilder = variantDataBuilder;
	}

	private void read() throws IOException, VCFParseException {
		final BufferedReader reader = new BufferedReader(
				new InputStreamReader(vcf.openStream())); 
		
		this.metadata = buildMetadata(reader, metadataBuilder);
		this.variants = buildVariants(reader, metadata, variantDataBuilder);
		this.read = true;
	}
	
	private M buildMetadata(
			BufferedReader reader, 
			VCFMetaDataBuilder<M> metadataBuilder) 
			throws IOException, VCFParseException {
		String line = null;
		// ## lines
		
		while ((line = reader.readLine())!=null && line.startsWith("##")) {
			String[] attValueTokens = line.split("=", 2);
			if (attValueTokens.length == 2) {
				String attName = attValueTokens[0].substring(2);
				String value = attValueTokens[1];
				
				if (value.matches("<.*>")) {
					value = value.substring(1, value.length() - 1);
					parseGenericMapAttribute(attName, value, metadataBuilder);
				} else {
					//simple value
					metadataBuilder.addAttribute(attName, value);
					
				}
			} else {
				//strange ## line 
				throw new VCFParseException("Expeted <key>=<value>"
						+ " format at line "+lineCounter);
			}
			lineCounter ++;
		}
		
		if (line != null && line.startsWith("#")) {
			//final column header line (where sample names appear)
			String[] columns = line.split(SEP);
			if (columns.length > 9) {
				//there are samples
				List<String> sampleNames = new LinkedList<>();
				for (int i = 9; i < columns.length; i++) {
					sampleNames.add(columns[i]);
				}
				metadataBuilder.setSamples(sampleNames);
			}
		} else if (!line.startsWith("#")) {
			// no columns header?
			throw new VCFParseException("It seems that there is no "
					+ "column headers line (which start with a single '#' char "
					+ "at line "+lineCounter);
		} else {
			// it seems that there is no header column and no data
			
		}
		lineCounter ++;
		
		return metadataBuilder.build();
	}
	

	
	private void parseGenericMapAttribute(String kind, String value,
			VCFMetaDataBuilder<M> metadataBuilder) {
		Map<String, String> attValues = parseAttributeValues(value);
		metadataBuilder.addAttribute(kind, attValues);
		
	}
	
	private Map<String, String> parseAttributeValues(String value) {

		
		// temporary replace [,=] inside description strings, those enclosed
		// with double quotes
		
		while (value.matches(".*\".*,.*\".*")) {	
			value = value.replaceAll("\"(.*),(.*)\"", "\"$1<comma>$2\"");
		}
		
		while (value.matches(".*\".*=.*\".*")) {	
			value = value.replaceAll("\"(.*)=(.*)\"", "\"$1<equals>$2\"");
		}
		
		Map<String, String> attValues = Arrays.stream(value.split(","))
				.map(attValue -> attValue.split("="))
		.collect(Collectors.toMap(
				attValueTokens -> attValueTokens[0], 
				attValueTokens -> {
					if (attValueTokens[1].startsWith("\"")) {
						attValueTokens[1] = attValueTokens[1].substring(1, attValueTokens[1].length()-1);
					}
					return attValueTokens[1].replaceAll("<comma>", ",").replaceAll("<equals>", "=");
				}));
		return attValues;
	}

	private Collection<V> buildVariants(
			BufferedReader reader, 
			M metadata, 
			VCFVariantDataBuilder<M, V> variantDataBuilder) 
			throws IOException, VCFParseException {
		
		String line = null;
		
		variantDataBuilder.setMetadata(metadata);
		
		while ((line = reader.readLine())!=null) {
			String[] tokens = line.split(SEP);

			parseVariantSeqPosRefAndAlleles(variantDataBuilder, tokens);
			
			parseVariantId(variantDataBuilder, tokens);
			
			parseVariantQuality(variantDataBuilder, tokens);
			
			parseVariantFilters(variantDataBuilder, tokens);
			
			parseVariantInfo(variantDataBuilder, tokens);
			
			parseSampleGenotypes(variantDataBuilder, tokens);

			this.lineCounter ++;			
			variantDataBuilder.endVariant();
		}
		
		return variantDataBuilder.build();
	}

	private void parseSampleGenotypes(
			VCFVariantDataBuilder<M, V> variantDataBuilder, String[] tokens) {
		final String[] genotypeFields =  tokens[8].split(":");
		for (int sampleCol = 9; sampleCol < tokens.length; sampleCol++) {
			final Map<String, List<String>> sampleGenotype = 
					new HashMap<>();				
			
			final String[] genotypeValues = tokens[sampleCol].split(":");
			int i = 0;
			for (String genotypeValue: genotypeValues) {
				final String genotypeField = genotypeFields[i];
				List<String> genotype = asList(genotypeValue.split(","));
				
				if (!allMissingValues(genotype)) {
					sampleGenotype.put(
						genotypeField, 
						genotype);
				}
				
				i++;
			}
			String sampleId = this.metadata.getSampleIds().get(sampleCol-9);
			variantDataBuilder.addVariantSample(
					sampleId, 
					sampleGenotype);
		}
	

	}

	private boolean allMissingValues(List<String> genotype) {
		boolean allAreDots = true;
		for (String genotypeFieldValue: genotype) {
			if (!genotypeFieldValue.equals(".")) {
				allAreDots = false;
				break;
			}
		}
		return allAreDots;
	}	
	private void parseVariantInfo(
			VCFVariantDataBuilder<M, V> variantDataBuilder, String[] tokens) 
			throws VCFParseException {
		
		String[] infoPairs = tokens[7].split(";");
		Map<String, List<String>> info =					
				stream(infoPairs)
				.map(pairString -> pairString.split("="))
		.collect(toMap(
				pairTokens -> pairTokens[0], 
				pairTokens -> Arrays.asList((pairTokens.length>1)?pairTokens[1].split(","):new String[]{})));
		
		/*if (!metadata.getInfoAttributesIds().containsAll(info.keySet())) {
			throw new VCFParseException("at least a INFO ID was "
					+ "not found in metadata at line "+this.lineCounter);
		}*/
		
		variantDataBuilder.setVariantInfo(info);
	}

	private void parseVariantSeqPosRefAndAlleles(
			VCFVariantDataBuilder<M, V> variantDataBuilder, String[] tokens) {
		String seqName = tokens[0];
		long position = Long.parseLong(tokens[1]);
		
		String referenceAllele = tokens[3];
		
		Set<String> alternativeAlleles = new LinkedHashSet<>();
			alternativeAlleles.addAll(Arrays.asList(tokens[4].split(",")));
		
		variantDataBuilder.startVariant(seqName, position,
				referenceAllele, alternativeAlleles);
	}

	private void parseVariantId(VCFVariantDataBuilder<M, V> variantDataBuilder,
			String[] tokens) {
		String variantId = tokens[2];
		if (!variantId.equals(".")) {
			variantDataBuilder.setVariantId(variantId);
		}
	}

	private void parseVariantQuality(
			VCFVariantDataBuilder<M, V> variantDataBuilder,
			String[] tokens) {
		double quality = Double.parseDouble(tokens[5]);
		variantDataBuilder.setVariantQuality(quality);
	}

	private void parseVariantFilters(
			VCFVariantDataBuilder<M, V> variantDataBuilder,
			String[] tokens) {
		String filterValue = tokens[6];
				
		if (filterValue.equals(".")) {
			variantDataBuilder.setVariantHasNoFilters();
		} else if (filterValue.equalsIgnoreCase("PASS")) {
			variantDataBuilder.setVariantPassesFilters();
		} else {
			String[] filterIds = filterValue.split(";");
			variantDataBuilder.setVariantFilters(Arrays.asList(filterIds));
		}
	}

	public Collection<V> getVariants() throws IOException, VCFParseException {
		if (!read)
			read();
		return variants;
	}
	
	public M getMetadata() throws IOException, VCFParseException {
		if (!read)
			read();
		return metadata;
	}
	
	
	
}
