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
package es.uvigo.ei.sing.pandrugsdb.persistence.entity;

import static java.util.Arrays.asList;
import static java.util.Collections.emptyList;
import static java.util.function.Function.identity;
import static java.util.stream.Collectors.toMap;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

public final class GeneDrugDataset {
	private GeneDrugDataset() {}
	
	public final static String[] geneDrugsIds() {
		return geneDrugs().stream()
			.map(GeneDrug::getGeneSymbol)
		.toArray(String[]::new);
	}
	
	public final static List<SourceInformation> sourceInfos() {
		return asList(
			new SourceInformation("CancerCommons", "http://dgidb.genome.wustl.edu/sources/CancerCommons", true),
			new SourceInformation("ClearityFoundationBiomarkers", "http://dgidb.genome.wustl.edu/sources/ClearityFoundationBiomarkers", true),
			new SourceInformation("ClearityFoundationClinicalTrial", "http://www.broadinstitute.org/ctrp/", true),
			new SourceInformation("CTRP", "http://www.broadinstitute.org/ctrp/", false),
			new SourceInformation("DrugBank", "http://dgidb.genome.wustl.edu/sources/DrugBank", false),
			new SourceInformation("GDSC", "http://www.cancerrxgene.org/translation/Gene/GDSC", false),
			new SourceInformation("moAb", "http://en.wikipedia.org/wiki/List_of_therapeutic_monoclonal_antibodies", true),
			new SourceInformation("MyCancerGenome", "http://dgidb.genome.wustl.edu/drug_claims/[GENES]/[DRUG]", true),
			new SourceInformation("MyCancerGenomeClinicalTrial", "http://dgidb.genome.wustl.edu/sources/MyCancerGenomeClinicalTrial", true),
			new SourceInformation("PharmGKB", "http://dgidb.genome.wustl.edu/sources/PharmGKB", false),
			new SourceInformation("TALC", "http://dgidb.genome.wustl.edu/sources/TALC", true),
			new SourceInformation("TARGET-CGA", "http://www.broadinstitute.org/cancer/cga/target", true),
			new SourceInformation("TEND", "http://dgidb.genome.wustl.edu/sources/TEND", true),
			new SourceInformation("TTD", "http://dgidb.genome.wustl.edu/sources/TTD", false)
		);
	}
	
	public final static List<GeneDrug> geneDrugs() {
		final Map<String, SourceInformation> sourceInfos = sourceInfos().stream()
			.collect(toMap(SourceInformation::getSource, identity()));

		return Arrays.asList(
			new GeneDrug(3, "ABI1", "INDISULAM", "Other",
				DrugStatus.CLINICAL, null, "cancer", null,
				false, "resistance", null, 0d,
				asList(),
				asList(new DrugSource(2, "CTRP", "indisulam", "INDISULAM", "INDISULAM", sourceInfos.get("CTRP"))),
				emptyList()),
			new GeneDrug(20, "ANGPTL4", "PRIMA-1", "Other",
				DrugStatus.EXPERIMENTAL, null, null, null,
				false, null, null, 0d, 
				asList(),
				asList(new DrugSource(7, "CTRP", "PRIMA-1", "PRIMA-1", "PRIMA-1", sourceInfos.get("CTRP"))),
				emptyList()),
			new GeneDrug(13, "BCL2L1", "BMS-536924", "Other",
				DrugStatus.EXPERIMENTAL, null, null, null,
				false, "resistance", null, 0d, 
				asList(),
				asList(new DrugSource(1, "CTRP", "BMS-536924", "BMS-536924", "BMS-536924", sourceInfos.get("CTRP"))),
				emptyList()),
			new GeneDrug(5, "CACNB2", "LOVASTATIN", "Other",
				DrugStatus.APPROVED,
				"Cardiology/Vascular disease",
				"clinical cancer", null, false, null, null,
				0.4d, 
				asList(),
				asList(new DrugSource(3, "CTRP", "lovastatin acid", "LOVASTATIN", "LOVASTATIN", sourceInfos.get("CTRP"))),
				emptyList()),
			new GeneDrug(8, "CASC5", "LOVASTATIN", "Other",
				DrugStatus.APPROVED,
				"Cardiology/Vascular disease",
				"clinical cancer", null, false, "resistance",
				null, 0d, 
				asList(),
				asList(new DrugSource(3, "CTRP", "lovastatin acid", "LOVASTATIN", "LOVASTATIN", sourceInfos.get("CTRP"))),
				emptyList()),
			new GeneDrug(14, 
				"CCNA2",
				"(2R)-2-{[4-(BENZYLAMINO)-8-(1-METHYLETHYL)PYRAZOLO[1,5-A][1,3,5]TRIAZIN-2-YL]AMINO}BUTAN-1-OL",
				"Cell cycle", DrugStatus.EXPERIMENTAL, null,
				null, null, true, null, null, 0.5d, 
				asList(),
				asList(
					new DrugSource(10, "DrugBank", "DB08285", "(2R)-2-{[4-(BENZYLAMINO)-8-(1-METHYLETHYL)PYRAZOLO[1,5-A][1,3,5]TRIAZIN-2-YL]AMINO}BUTAN-1-OL", "(2R)-2-{[4-(BENZYLAMINO)-8-(1-METHYLETHYL)PYRAZOLO[1,5-A][1,3,5]TRIAZIN-2-YL]AMINO}BUTAN-1-OL", sourceInfos.get("DrugBank"))),
				emptyList()),
			new GeneDrug(16, "CLTC", "YK 4-279", "Other",
				DrugStatus.EXPERIMENTAL, null, null, null,
				false, null, null, 0d, 
				asList(),
				asList(new DrugSource(9, "CTRP", "YK 4-279", "YK 4-279", "YK 4-279", sourceInfos.get("CTRP"))),
				emptyList()),
			new GeneDrug(12, "CYP11A1", "AMINOGLUTETHIMIDE",
				"Metabolism", DrugStatus.WITHDRAWN, null, null,
				null, true, null, null, 0.5d, 
				asList(),
				asList(new DrugSource(17, "TTD", "DAP000842", "AMINOGLUTETHIMIDE", "AMINOGLUTETHIMIDE", sourceInfos.get("TTD"))),
				emptyList()),
			new GeneDrug(18, "DBN1", "NEOPELTOLIDE", "Other",
				DrugStatus.EXPERIMENTAL, null, null, null,
				false, null, null, 0d, 
				asList(),
				asList(new DrugSource(5, "CTRP", "neopeltolide", "NEOPELTOLIDE", "NEOPELTOLIDE", sourceInfos.get("CTRP"))),
				emptyList()),
			new GeneDrug(10, "EFTUD1", "S-(METHYLMERCURY)-L-CYSTEINE",
				"Other", DrugStatus.EXPERIMENTAL, null, null,
				null, true, null, null, 0.5d, 
				asList(),
				asList(new DrugSource(11, "DrugBank", "DB02750", "S-(METHYLMERCURY)-L-CYSTEINE", "S-(METHYLMERCURY)-L-CYSTEINE", sourceInfos.get("DrugBank"))),
				emptyList()),
			new GeneDrug(9, "EPHB4", "XL647",
				"Receptor Tyrosine Kinase",
				DrugStatus.CLINICAL, null, "cancer", null,
				true, null, null, 0.8d, 
				asList(),
				asList(new DrugSource(15, "TALC", "XL647", "XL647", "XL647", sourceInfos.get("TALC"))),
				emptyList()),
			new GeneDrug(2, "FIGF", "SALERMIDE", "Other",
				DrugStatus.EXPERIMENTAL, null, null, null,
				false, null, null, 0d, 
				asList(),
				asList(new DrugSource(8, "CTRP", "salermide", "SALERMIDE", "SALERMIDE", sourceInfos.get("CTRP"))),
				emptyList()),
			new GeneDrug(1, "HDAC3", "SODIUM PHENYLBUTYRATE",
				"Epigenetics", DrugStatus.APPROVED,
				"Hematology", "clinical cancer", null, true,
				null, null, 0.9d, 
				asList(),
				asList(new DrugSource(16, "TALC", "SODIUM PHENYLBUTYRATE", "SODIUM PHENYLBUTYRATE", "SODIUM PHENYLBUTYRATE", sourceInfos.get("TALC"))),
				emptyList()),
			new GeneDrug(15, "HSP90AA1", "IPI-504",
				"Chaperone inhibitor", DrugStatus.CLINICAL,
				null, "cancer", null, true, null, null, 0.8d,
				asList(),
				asList(new DrugSource(18, "TTD", "DCL000137", "IPI-504", "IPI-504", sourceInfos.get("TDD"))),
				emptyList()),
			new GeneDrug(4, "KDM6A", "CHEMBL254381", "Other",
				DrugStatus.EXPERIMENTAL, null, null, null,
				false, null, null, 0d, 
				asList(),
				asList(new DrugSource(6, "CTRP", "PNU-74654", "CHEMBL254381", "CHEMBL254381", sourceInfos.get("CTRP"))),
				emptyList()),
			new GeneDrug(6, "MSH2", "BMS-536924", "Other",
				DrugStatus.EXPERIMENTAL, null, null, null,
				false, "resistance", null, 0d, 
				asList(),
				asList(new DrugSource(1, "CTRP", "BMS-536924", "BMS-536924", "BMS-536924", sourceInfos.get("CTRP"))),
				emptyList()),
			new GeneDrug(17, "PDE4A", "PICLAMILAST", "Other",
				DrugStatus.EXPERIMENTAL, null, null, null,
				true, null, null, 0.5d, 
				asList(),
				asList(new DrugSource(12, "DrugBank", "DB01791", "PICLAMILAST", "PICLAMILAST", sourceInfos.get("CTRP"))),
				emptyList()),
			new GeneDrug(19, "PIK3CB", "BYL719", "PI3K/Akt/mTOR",
				DrugStatus.CLINICAL, null, "cancer", null,
				true, null, null, 0.8d, 
				asList(),
				asList(new DrugSource(14, "MyCancerGenomeClinicalTrial", "BYL719", "BYL719", "BYL719", sourceInfos.get("MyCancerGenomeClinicalTrial"))),
				emptyList()),
			new GeneDrug(11, "PSMD10P2", "CARFILZOMIB", "Proteases",
				DrugStatus.APPROVED, "Oncology",
				"multiple myeloma", "TARGETED THERAPY", true,
				null, null, 1d, 
				asList(),
				asList(new DrugSource(13, "MyCancerGenome", "CARFILZOMIB", "CARFILZOMIB", "CARFILZOMIB", sourceInfos.get("MyCancerGenome"))),
				emptyList()),
			new GeneDrug(7, "TNFRSF1B", "NAVITOCLAX", "Other",
				DrugStatus.CLINICAL, null, "cancer", null,
				false, "resistance", null, 0d, 
				asList(),
				asList(new DrugSource(4, "CTRP", "navitoclax", "NAVITOCLAX", "NAVITOCLAX", sourceInfos.get("CTRP"))),
				emptyList())
		);
	}
	
	public final static List<DrugSource> drugSources() {
		final Map<String, SourceInformation> sourceInfos = sourceInfos().stream()
			.collect(toMap(SourceInformation::getSource, identity()));
		
		return asList(
			new DrugSource(1, "CTRP", "BMS-536924", "BMS-536924", "BMS-536924", sourceInfos.get("CTRP")),
			new DrugSource(2, "CTRP", "indisulam", "INDISULAM", "INDISULAM", sourceInfos.get("CTRP")),
			new DrugSource(3, "CTRP", "lovastatin acid", "LOVASTATIN", "LOVASTATIN", sourceInfos.get("CTRP")),
			new DrugSource(4, "CTRP", "navitoclax", "NAVITOCLAX", "NAVITOCLAX", sourceInfos.get("CTRP")),
			new DrugSource(5, "CTRP", "neopeltolide", "NEOPELTOLIDE", "NEOPELTOLIDE", sourceInfos.get("CTRP")),
			new DrugSource(6, "CTRP", "PNU-74654", "CHEMBL254381", "CHEMBL254381", sourceInfos.get("CTRP")),
			new DrugSource(7, "CTRP", "PRIMA-1", "PRIMA-1", "PRIMA-1", sourceInfos.get("CTRP")),
			new DrugSource(8, "CTRP", "salermide", "SALERMIDE", "SALERMIDE", sourceInfos.get("CTRP")),
			new DrugSource(9, "CTRP", "YK 4-279", "YK 4-279", "YK 4-279", sourceInfos.get("CTRP")),
			new DrugSource(10, "DrugBank", "DB08285", "(2R)-2-{[4-(BENZYLAMINO)-8-(1-METHYLETHYL)PYRAZOLO[1,5-A][1,3,5]TRIAZIN-2-YL]AMINO}BUTAN-1-OL", "(2R)-2-{[4-(BENZYLAMINO)-8-(1-METHYLETHYL)PYRAZOLO[1,5-A][1,3,5]TRIAZIN-2-YL]AMINO}BUTAN-1-OL", sourceInfos.get("DrugBank")),
			new DrugSource(11, "DrugBank", "DB02750", "S-(METHYLMERCURY)-L-CYSTEINE", "S-(METHYLMERCURY)-L-CYSTEINE", sourceInfos.get("DrugBank")),
			new DrugSource(12, "DrugBank", "DB01791", "PICLAMILAST", "PICLAMILAST", sourceInfos.get("DrugBank")),
			new DrugSource(13, "MyCancerGenome", "CARFILZOMIB", "CARFILZOMIB", "CARFILZOMIB", sourceInfos.get("MyCancerGenome")),
			new DrugSource(14, "MyCancerGenomeClinicalTrial", "BYL719", "BYL719", "BYL719", sourceInfos.get("MyCancerGenomeClinicalTrial")),
			new DrugSource(15, "TALC", "XL647", "XL647", "XL647", sourceInfos.get("TALC")),
			new DrugSource(16, "TALC", "SODIUM PHENYLBUTYRATE", "SODIUM PHENYLBUTYRATE", "SODIUM PHENYLBUTYRATE", sourceInfos.get("TALC")),
			new DrugSource(17, "TTD", "DAP000842", "AMINOGLUTETHIMIDE", "AMINOGLUTETHIMIDE", sourceInfos.get("TTD")),
			new DrugSource(18, "TTD", "DCL000137", "IPI-504", "IPI-504", sourceInfos.get("TTD"))
		);
	}
	
	public final static GeneDrug presentGeneDrug() {
		return geneDrugs().get(0);
	}
	
	public final static GeneDrug absentGeneDrug() {
		return new GeneDrug(1000, "GENE", "DRUG", "Other",
			DrugStatus.CLINICAL, null, "cancer", null,
			false, "resistance", null, 0d,
			emptyList(),
			emptyList(),
			emptyList()
		);
	}
}
