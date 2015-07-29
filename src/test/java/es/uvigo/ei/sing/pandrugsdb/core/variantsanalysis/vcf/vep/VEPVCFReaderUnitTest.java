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
import static org.easymock.EasyMock.createNiceMock;
import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.replay;
import static org.easymock.EasyMock.verify;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.junit.Assert.assertThat;

import java.io.IOException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import org.easymock.EasyMockRunner;
import org.junit.Test;
import org.junit.runner.RunWith;

import es.uvigo.ei.sing.pandrugsdb.core.variantsanalysis.vcf.VCFMetaData;
import es.uvigo.ei.sing.pandrugsdb.core.variantsanalysis.vcf.VCFMetaDataBuilder;
import es.uvigo.ei.sing.pandrugsdb.core.variantsanalysis.vcf.VCFParseException;
import es.uvigo.ei.sing.pandrugsdb.core.variantsanalysis.vcf.VCFReader;
import es.uvigo.ei.sing.pandrugsdb.core.variantsanalysis.vcf.VCFVariant;
import es.uvigo.ei.sing.pandrugsdb.core.variantsanalysis.vcf.VCFVariantDataBuilder;

@RunWith(EasyMockRunner.class)
public class VEPVCFReaderUnitTest {

	private URL aBasicVEPVCF = getClass().getResource("basic.vep.vcf");

	@SuppressWarnings({ "unchecked" })
	@Test
	public void testReadBuildsMetadataWithBasicVEPVCF() throws IOException,
			VCFParseException {

		final VCFMetaDataBuilder<VCFMetaData> metadataBuilder = createNiceMock(VCFMetaDataBuilder.class);
		final VCFMetaData metadata = createNiceMock(VCFMetaData.class);

		expect(metadata.getSampleIds()).andReturn(asList("PGDX539T"))
				.anyTimes();
		expect(metadataBuilder.build()).andReturn(metadata);

		final Map<String, String> anExpectedInfoAttribute = new HashMap<>();
		anExpectedInfoAttribute.put("ID", "CSQ");
		anExpectedInfoAttribute.put("Type", "String");
		anExpectedInfoAttribute.put("Number", ".");
		anExpectedInfoAttribute
				.put("Description",
						"Consequence type as predicted by VEP. Format: Allele|Gene|Feature|Feature_type|Consequence|cDNA_position|CDS_position|Protein_position|Amino_acids|Codons|Existing_variation|CCDS|BIOTYPE|GMAF|SIFT|CANONICAL|DOMAINS|SYMBOL|DISTANCE|PolyPhen|EXON|INTRON|PUBMED|CLIN_SIG|MOTIF_NAME|MOTIF_POS|HIGH_INF_POS|MOTIF_SCORE_CHANGE|ENSP|AA_MAF|EA_MAF|AFR_MAF|AMR_MAF|ASN_MAF|EUR_MAF|Condel");

		expect(metadataBuilder.addAttribute("INFO", anExpectedInfoAttribute))
				.andReturn(metadataBuilder);

		final VCFVariantDataBuilder<VCFMetaData, VCFVariant<VCFMetaData>> variantDataBuilder = createNiceMock(VCFVariantDataBuilder.class);

		Object[] mocks = { metadata, metadataBuilder, variantDataBuilder };

		replay(mocks);

		final VCFReader<VCFMetaData, VCFVariant<VCFMetaData>> reader = new VCFReader<>(
				this.aBasicVEPVCF, metadataBuilder, variantDataBuilder);

		reader.getMetadata();

		verify(mocks);

	}

	@Test
	public void testCSQParsing() throws IOException, VCFParseException {
		VCFReader<VEPMetaData, VEPVariant> reader = new VCFReader<>(
				this.aBasicVEPVCF, new VEPMetaDataBuilder(),
				new VEPVariantDataBuilder());

		VEPMetaData metadata = reader.getMetadata();
		assertThat(
				metadata.getCSQAttributes(),
				is(asList("Allele", "Gene", "Feature", "Feature_type",
						"Consequence", "cDNA_position", "CDS_position",
						"Protein_position", "Amino_acids", "Codons",
						"Existing_variation", "CCDS", "BIOTYPE", "GMAF",
						"SIFT", "CANONICAL", "DOMAINS", "SYMBOL", "DISTANCE",
						"PolyPhen", "EXON", "INTRON", "PUBMED", "CLIN_SIG",
						"MOTIF_NAME", "MOTIF_POS", "HIGH_INF_POS",
						"MOTIF_SCORE_CHANGE", "ENSP", "AA_MAF", "EA_MAF",
						"AFR_MAF", "AMR_MAF", "ASN_MAF", "EUR_MAF", "Condel")));
		
		
		VEPVariant[] variants = (VEPVariant[]) reader.getVariants().toArray(
				new VEPVariant[0]);

		assertThat(variants.length, is(2));
		
		assertThat(variants[0].getCSQCount(), is(13));
		assertThat(variants[0].getCSQAttribute(0, "Allele"), is("T"));
		assertThat(variants[0].getCSQAttribute(0, "Gene"), is(nullValue()));
		assertThat(variants[0].getCSQAttribute(10, "Gene"), is("ENSG00000187634"));
		assertThat(variants[0].getCSQAttribute(10, "Condel"), is(nullValue()));
		
	}
}
