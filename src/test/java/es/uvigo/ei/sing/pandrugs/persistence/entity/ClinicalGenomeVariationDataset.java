/*-
 * #%L
 * PanDrugs Backend
 * %%
 * Copyright (C) 2015 - 2021 Fátima Al-Shahrour, Elena Piñeiro, Daniel Glez-Peña
 * and Miguel Reboiro-Jato
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

package es.uvigo.ei.sing.pandrugs.persistence.entity;

import static java.util.Arrays.stream;

import java.util.Arrays;

public final class ClinicalGenomeVariationDataset {
	private ClinicalGenomeVariationDataset() {}
	
	public static ClinicalGenomeVariation[] clinicalGenomeVariations() {
		return new ClinicalGenomeVariation[] {
			new ClinicalGenomeVariation("1", 123456789, 123456789, "c.321G>A", "Disease 1", "ACC00001", "Pathogenic", "rs1000000"),
			new ClinicalGenomeVariation("1", 123456789, 123456789, "c.321G>A", "Disease 2", "ACC00001", "Pathogenic", "rs2000000"),
			new ClinicalGenomeVariation("1", 123456789, 123456789, "c.321G>A", "Disease 3", "ACC00001", "Pathogenic", "rs3000000"),
			new ClinicalGenomeVariation("2", 222222222, 222222223, "c.4727_4753del2", "Disease 1", "ACC00002", "Pathogenic", "rs2000000"),
			new ClinicalGenomeVariation("1", 123456789, 123456790, "c.321del2", "Disease 1", "ACC00001", "Pathogenic", "rs3000000")
		};
	}
	
	public static ClinicalGenomeVariation[] withChromosomeLocation(String chromosome, int start, int end) {
		return stream(clinicalGenomeVariations())
			.filter(cgv -> cgv.getChromosome().equals(chromosome))
			.filter(cgv -> cgv.getStart() == start)
			.filter(cgv -> cgv.getEnd() == end)
		.toArray(ClinicalGenomeVariation[]::new);
	}
	
	public static ClinicalGenomeVariation[] withDbSnp(String dbSnp) {
		return Arrays.stream(clinicalGenomeVariations())
			.filter(cgv -> cgv.getDbSnp().equals(dbSnp))
		.toArray(ClinicalGenomeVariation[]::new);
	}
	
	public static ClinicalGenomeVariation withId(String chromosome, int start, int end, String hgvs, String disease, String accession) {
		return stream(withChromosomeLocation(chromosome, start, end))
			.filter(cgv -> cgv.getHgvs().equals(hgvs))
			.filter(cgv -> cgv.getDisease().equals(disease))
			.filter(cgv -> cgv.getAccession().equals(accession))
		.findAny().orElse(null);
		
	}
}
