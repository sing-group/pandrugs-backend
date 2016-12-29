/*
 * #%L
 * PanDrugsDB Backend
 * %%
 * Copyright (C) 2015 - 2016 Fátima Al-Shahrour, Elena Piñeiro, Daniel Glez-Peña and Miguel Reboiro-Jato
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

import static java.util.Arrays.stream;
import static java.util.stream.Collectors.toList;

import java.util.Arrays;
import java.util.List;

public final class SomaticMutationInCancerDataset {
	private SomaticMutationInCancerDataset() {}
	
	public static SomaticMutationInCancer[] somaticMutationInCancer() {
		return new SomaticMutationInCancer[] {
			new SomaticMutationInCancer(1106715, 520, "p.G12V", FathmmPrediction.CANCER, "Reported in another cancer sample as somatic", "KRAS"),
			new SomaticMutationInCancer(1171692, 12600, "p.V617F", FathmmPrediction.CANCER, "Reported in another cancer sample as somatic", "JAK2"),
			new SomaticMutationInCancer(1191791, 12600, "p.V617F", FathmmPrediction.CANCER, "Reported in another cancer sample as somatic", "JAK2"),
			new SomaticMutationInCancer(1253988, 12600, "p.V617F", FathmmPrediction.CANCER, "Reported in another cancer sample as somatic", "JAK2"),
			new SomaticMutationInCancer(1243570, 520, "p.G12V", FathmmPrediction.CANCER, "Reported in another cancer sample as somatic", "KRAS"),
			new SomaticMutationInCancer(1278874, 29325, "p.H1047R", FathmmPrediction.CANCER, "Reported in another cancer sample as somatic", "PIK3CA"),
			new SomaticMutationInCancer(1297208, 1131, "p.V600E", FathmmPrediction.CANCER, "Reported in another cancer sample as somatic", "BRAF"),
			new SomaticMutationInCancer(2062380, 1723202, "p.H51delH", FathmmPrediction.NONE, "Confirmed somatic variant", "NUFIP2"),
			new SomaticMutationInCancer(2062380, 911094, "p.D169N", FathmmPrediction.PASSENGER_OTHER, "Confirmed somatic variant", "C8A")
		};
	}
	
	public static List<String[]> validGeneAndMutationAA() {
		return stream(somaticMutationInCancer())
			.map(smic -> new String[] { smic.getGeneSymbol(), smic.getMutationAA() })
			.distinct()
		.collect(toList());
	}
	
	public static List<String[]> invalidGeneAndMutationAA() {
		return Arrays.asList(
			new String[] { "FAKE", "p.FAKE" },
			new String[] { "KRAS", "p.FAKE" },
			new String[] { "FAKE", "p.D169N" }
		);
	}
	
	public static SomaticMutationInCancer[] withGeneAndMutationAA(String geneSymbol, String mutationAA) {
		return stream(somaticMutationInCancer())
			.filter(cgv -> cgv.getGeneSymbol().equals(geneSymbol))
			.filter(cgv -> cgv.getMutationAA().equals(mutationAA))
		.toArray(SomaticMutationInCancer[]::new);
	}
	
	public static SomaticMutationInCancer withId(SomaticMutationInCancerId sampleId) {
		return stream(somaticMutationInCancer())
			.filter(smic -> smic.getSampleId() == sampleId.getSampleId())
			.filter(smic -> smic.getGeneSymbol() == sampleId.getGeneSymbol())
		.findAny().orElse(null);
	}
	
	public static SomaticMutationInCancerId[] validIds() {
		return stream(somaticMutationInCancer())
			.map(SomaticMutationInCancerId::of)
		.toArray(SomaticMutationInCancerId[]::new);
	}
	
	public static SomaticMutationInCancerId[] invalidIds() {
		return new SomaticMutationInCancerId[] { 
			new SomaticMutationInCancerId(0, "FAKE", 0, "Fake"),
			new SomaticMutationInCancerId(0, "KRAS", 0, "Fake"),
			new SomaticMutationInCancerId(1106715, "FAKE", 0, "Fake")
		};
	}
}
