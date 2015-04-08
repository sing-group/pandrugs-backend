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

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public final class GeneDrugDataset {
	private GeneDrugDataset() {}
	
	public final static String[] geneDrugsIds() {
		return geneDrugs().stream()
			.map(GeneDrug::getGeneSymbol)
		.toArray(String[]::new);
	}
	
	public final static List<GeneDrug> geneDrugs() {
		return Arrays.asList(
			new GeneDrug("ABI1", "INDISULAM", "Other",
				DrugStatus.CLINICAL, null, "cancer", null, false,
				"resistance", null, 0d, Arrays.asList(new DrugSource(2, "CTRP", "indisulam", "INDISULAM")),
				Collections.emptyList()),
			new GeneDrug("ANGPTL4", "PRIMA-1", "Other",
				DrugStatus.EXPERIMENTAL, null, null, null,
				false, null, null, 0d, Arrays.asList(new DrugSource(7, "CTRP", "PRIMA-1", "PRIMA-1")),
				Collections.emptyList()),
			new GeneDrug("BCL2L1", "BMS-536924", "Other",
				DrugStatus.EXPERIMENTAL, null, null, null,
				false, "resistance", null, 0d, Arrays.asList(new DrugSource(1, "CTRP", "BMS-536924", "BMS-536924")),
				Collections.emptyList()),
			new GeneDrug("CACNB2", "LOVASTATIN", "Other",
				DrugStatus.APPROVED,
				"Cardiology/Vascular disease",
				"clinical cancer", null, false, null, null,
				0.4d, Arrays.asList(new DrugSource(3, "CTRP", "lovastatin acid", "LOVASTATIN")),
				Collections.emptyList()),
			new GeneDrug("CASC5", "LOVASTATIN", "Other",
				DrugStatus.APPROVED,
				"Cardiology/Vascular disease",
				"clinical cancer", null, false, "resistance",
				null, 0d, Arrays.asList(new DrugSource(3, "CTRP", "lovastatin acid", "LOVASTATIN")),
				Collections.emptyList()),
			new GeneDrug(
				"CCNA2",
				"(2R)-2-{[4-(BENZYLAMINO)-8-(1-METHYLETHYL)PYRAZOLO[1,5-A][1,3,5]TRIAZIN-2-YL]AMINO}BUTAN-1-OL",
				"Cell cycle", DrugStatus.EXPERIMENTAL, null,
				null, null, true, null, null, 0.5d, Arrays.asList(new DrugSource(10, "DrugBank", "DB08285", "(2R)-2-{[4-(BENZYLAMINO)-8-(1-METHYLETHYL)PYRAZOLO[1,5-A][1,3,5]TRIAZIN-2-YL]AMINO}BUTAN-1-OL")),
				Collections.emptyList()),
			new GeneDrug("CLTC", "YK 4-279", "Other",
				DrugStatus.EXPERIMENTAL, null, null, null,
				false, null, null, 0d, Arrays.asList(new DrugSource(9, "CTRP", "YK 4-279", "YK 4-279")),
				Collections.emptyList()),
			new GeneDrug("CYP11A1", "AMINOGLUTETHIMIDE",
				"Metabolism", DrugStatus.WITHDRAWN, null, null,
				null, true, null, null, 0.5d, Arrays.asList(new DrugSource(17, "TTD", "DAP000842", "AMINOGLUTETHIMIDE")),
				Collections.emptyList()),
			new GeneDrug("DBN1", "NEOPELTOLIDE", "Other",
				DrugStatus.EXPERIMENTAL, null, null, null,
				false, null, null, 0d, Arrays.asList(new DrugSource(5, "CTRP", "neopeltolide", "NEOPELTOLIDE")),
				Collections.emptyList()),
			new GeneDrug("EFTUD1", "S-(METHYLMERCURY)-L-CYSTEINE",
				"Other", DrugStatus.EXPERIMENTAL, null, null,
				null, true, null, null, 0.5d, Arrays.asList(new DrugSource(11, "DrugBank", "DB02750", "S-(METHYLMERCURY)-L-CYSTEINE")),
				Collections.emptyList()),
			new GeneDrug("EPHB4", "XL647",
				"Receptor Tyrosine Kinase",
				DrugStatus.CLINICAL, null, "cancer", null,
				true, null, null, 0.8d, Arrays.asList(new DrugSource(15, "TALC", "XL647", "XL647")),
				Collections.emptyList()),
			new GeneDrug("FIGF", "SALERMIDE", "Other",
				DrugStatus.EXPERIMENTAL, null, null, null,
				false, null, null, 0d, Arrays.asList(new DrugSource(8, "CTRP", "salermide", "SALERMIDE")),
				Collections.emptyList()),
			new GeneDrug("HDAC3", "SODIUM PHENYLBUTYRATE",
				"Epigenetics", DrugStatus.APPROVED,
				"Hematology", "clinical cancer", null, true,
				null, null, 0.9d, Arrays.asList(new DrugSource(16, "TALC", "SODIUM PHENYLBUTYRATE", "SODIUM PHENYLBUTYRATE")),
				Collections.emptyList()),
			new GeneDrug("HSP90AA1", "IPI-504",
				"Chaperone inhibitor", DrugStatus.CLINICAL,
				null, "cancer", null, true, null, null, 0.8d,
				Arrays.asList(new DrugSource(18, "TTD", "DCL000137", "IPI-504")),
				Collections.emptyList()),
			new GeneDrug("KDM6A", "CHEMBL254381", "Other",
				DrugStatus.EXPERIMENTAL, null, null, null,
				false, null, null, 0d, Arrays.asList(new DrugSource(6, "CTRP", "PNU-74654", "CHEMBL254381")),
				Collections.emptyList()),
			new GeneDrug("MSH2", "BMS-536924", "Other",
				DrugStatus.EXPERIMENTAL, null, null, null,
				false, "resistance", null, 0d, Arrays.asList(new DrugSource(1, "CTRP", "BMS-536924", "BMS-536924")),
				Collections.emptyList()),
			new GeneDrug("PDE4A", "PICLAMILAST", "Other",
				DrugStatus.EXPERIMENTAL, null, null, null,
				true, null, null, 0.5d, Arrays.asList(new DrugSource(12, "DrugBank", "DB01791", "PICLAMILAST")),
				Collections.emptyList()),
			new GeneDrug("PIK3CB", "BYL719", "PI3K/Akt/mTOR",
				DrugStatus.CLINICAL, null, "cancer", null,
				true, null, null, 0.8d, Arrays.asList(new DrugSource(14, "MyCancerGenomeClinicalTrial", "BYL719", "BYL719")),
				Collections.emptyList()),
			new GeneDrug("PSMD10P2", "CARFILZOMIB", "Proteases",
				DrugStatus.APPROVED, "Oncology",
				"multiple myeloma", "TARGETED THERAPY", true,
				null, null, 1d, Arrays.asList(new DrugSource(13, "MyCancerGenome", "CARFILZOMIB", "CARFILZOMIB")),
				Collections.emptyList()),
			new GeneDrug("TNFRSF1B", "NAVITOCLAX", "Other",
				DrugStatus.CLINICAL, null, "cancer", null,
				false, "resistance", null, 0d, Arrays.asList(new DrugSource(4, "CTRP", "navitoclax", "NAVITOCLAX")),
				Collections.emptyList())
		);
	}
	
	public final static List<DrugSource> drugSources() {
		return Arrays.asList(
			new DrugSource(1, "CTRP", "BMS-536924", "BMS-536924"),
			new DrugSource(2, "CTRP", "indisulam", "INDISULAM"),
			new DrugSource(3, "CTRP", "lovastatin acid", "LOVASTATIN"),
			new DrugSource(4, "CTRP", "navitoclax", "NAVITOCLAX"),
			new DrugSource(5, "CTRP", "neopeltolide", "NEOPELTOLIDE"),
			new DrugSource(6, "CTRP", "PNU-74654", "CHEMBL254381"),
			new DrugSource(7, "CTRP", "PRIMA-1", "PRIMA-1"),
			new DrugSource(8, "CTRP", "salermide", "SALERMIDE"),
			new DrugSource(9, "CTRP", "YK 4-279", "YK 4-279"),
			new DrugSource(10, "DrugBank", "DB08285", "(2R)-2-{[4-(BENZYLAMINO)-8-(1-METHYLETHYL)PYRAZOLO[1,5-A][1,3,5]TRIAZIN-2-YL]AMINO}BUTAN-1-OL"),
			new DrugSource(11, "DrugBank", "DB02750", "S-(METHYLMERCURY)-L-CYSTEINE"),
			new DrugSource(12, "DrugBank", "DB01791", "PICLAMILAST"),
			new DrugSource(13, "MyCancerGenome", "CARFILZOMIB", "CARFILZOMIB"),
			new DrugSource(14, "MyCancerGenomeClinicalTrial", "BYL719", "BYL719"),
			new DrugSource(15, "TALC", "XL647", "XL647"),
			new DrugSource(16, "TALC", "SODIUM PHENYLBUTYRATE", "SODIUM PHENYLBUTYRATE"),
			new DrugSource(17, "TTD", "DAP000842", "AMINOGLUTETHIMIDE"),
			new DrugSource(18, "TTD", "DCL000137", "IPI-504")
		);
	}
	
	public final static GeneDrug presentGeneDrug() {
		return geneDrugs().get(0);
	}
	
	public final static GeneDrug absentGeneDrug() {
		return new GeneDrug("GENE", "DRUG", "Other",
			DrugStatus.CLINICAL, null, "cancer", null,
			false, "resistance", null, 0d, 
			Collections.emptyList(),
			Collections.emptyList()
		);
	}
}
