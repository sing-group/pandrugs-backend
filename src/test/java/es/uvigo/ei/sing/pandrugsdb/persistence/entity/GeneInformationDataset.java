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

import static es.uvigo.ei.sing.pandrugsdb.persistence.entity.DriverLevel.HIGH_CONFIDENCE_DRIVER;
import static es.uvigo.ei.sing.pandrugsdb.persistence.entity.TumorPortalMutationLevel.NEAR_SIGNIFICANCE;
import static es.uvigo.ei.sing.pandrugsdb.persistence.entity.TumorPortalMutationLevel.SIGNIFICANTLY_MUTATED;
import static java.util.Arrays.asList;
import static java.util.Arrays.stream;
import static java.util.Collections.emptySet;
import static java.util.stream.StreamSupport.stream;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.function.Supplier;
import java.util.stream.IntStream;

public final class GeneInformationDataset {
	private GeneInformationDataset() {}

	public static GeneInformation[] geneInformations() {
		final Set<GeneInformation> gata2Interactions = new HashSet<>();
		final Set<GeneInformation> cdc42bpaInteractions = new HashSet<>();
		final Set<GeneInformation> ppp3r1Interactions = new HashSet<>();
		final Set<GeneInformation> prkag2Interactions = new HashSet<>();
		
		final GeneInformation gata2 = new GeneInformation("GATA2", NEAR_SIGNIFICANCE, true, null, 0d, emptySet(), emptySet(), gata2Interactions);
		final GeneInformation adamts19 = new GeneInformation("ADAMTS19", null, false, null, 0.3556896485d);
		final GeneInformation cdc42bpa = new GeneInformation("CDC42BPA", NEAR_SIGNIFICANCE, false, null, 0.8202555173d, emptySet(), emptySet(), cdc42bpaInteractions);
		final GeneInformation prkag2 = new GeneInformation("PRKAG2", null, false, null, 0d, emptySet(), emptySet(), prkag2Interactions);
		final GeneInformation ptgs1 = new GeneInformation("PTGS1", null, false, null, 0.4277512382d);
		final GeneInformation ppp3r1 = new GeneInformation("PPP3R1", null, false, null, 0d, emptySet(), emptySet(), ppp3r1Interactions);
		final GeneInformation max = new GeneInformation("MAX", null, true, HIGH_CONFIDENCE_DRIVER, 0.626823283d);
		final GeneInformation cx3cr1 = new GeneInformation("CX3CR1", null, false, null, 0d);
		final GeneInformation dmd = new GeneInformation("DMD", null, false, HIGH_CONFIDENCE_DRIVER, 0d);
		
		gata2Interactions.add(adamts19);
		gata2Interactions.add(cdc42bpa);
		gata2Interactions.add(prkag2);
		cdc42bpaInteractions.add(adamts19);
		cdc42bpaInteractions.add(ptgs1);
		cdc42bpaInteractions.add(ppp3r1);
		ppp3r1Interactions.add(dmd);
		prkag2Interactions.add(max);
		
		return new GeneInformation[] {
			gata2, adamts19, cdc42bpa, prkag2, ptgs1, ppp3r1, max, cx3cr1, dmd
		};
	}
	
	public static GeneInformation[] geneInformations(String ... geneSymbols) {
		final Set<String> geneSymbolsSet = new HashSet<>(asList(geneSymbols));
		
		return stream(geneInformations())
			.filter(gi -> geneSymbolsSet.contains(gi.getGeneSymbol()))
		.toArray(GeneInformation[]::new);
	}

	public static GeneInformation geneInformation(String geneSymbol) {
		return stream(geneInformations())
			.filter(gi -> gi.getGeneSymbol().equals(geneSymbol))
			.findFirst()
		.orElseThrow(IllegalArgumentException::new);
	}
	
	public static String[] genesWithMaxInteractionDegree(int degree) {
		final Map<Integer, Supplier<String[]>> interactionsByDegree = new HashMap<>();
		interactionsByDegree.put(0, GeneInformationDataset::genesWithMaxInteractionDegree0);
		interactionsByDegree.put(1, GeneInformationDataset::genesWithMaxInteractionDegree1);
		interactionsByDegree.put(2, GeneInformationDataset::genesWithMaxInteractionDegree2);
		interactionsByDegree.put(3, GeneInformationDataset::genesWithMaxInteractionDegree3);

		return interactionsByDegree.getOrDefault(degree, () -> new String[0]).get();
	}
	
	public static String[] genesWithInteractionDegreeUpTo(int maxDegree) {
		return IntStream.rangeClosed(0, maxDegree)
			.mapToObj(GeneInformationDataset::genesWithMaxInteractionDegree)
			.flatMap(Arrays::stream)
		.toArray(String[]::new);
	}
	
	public static String[] genesWithMaxInteractionDegree0() {
		return new String[] { "MAX", "DMD", "CX3CR1" };
	}
	
	public static String[] genesWithMaxInteractionDegree1() {
		return new String[] { "PPP3R1", "PRKAG2" };
	}
	
	public static String[] genesWithMaxInteractionDegree2() {
		return new String[] { "CDC42BPA" };
	}
	
	public static String[] genesWithMaxInteractionDegree3() {
		return new String[] { "GATA2" };
	}
	
	public static GeneInformation[] interactions(int degree, Iterable<String> geneSymbols) {
		return interactions(degree, stream(geneSymbols.spliterator(), false).toArray(String[]::new));
	}
	
	
	public static GeneInformation[] interactions(int degree, String ... geneSymbols) {
		final Set<GeneInformation> interactions = new HashSet<>(asList(geneInformations(geneSymbols)));
		
		for (int i = 0; i < degree; i++) {
			for (GeneInformation interaction : new HashSet<>(interactions)) {
				interactions.addAll(interaction.getInteractingGenes());
			}
		}
		
		return interactions.toArray(new GeneInformation[interactions.size()]);
	}
	
	public static String[] geneSymbolsForQuery(String query) {
		return stream(geneInformations())
			.map(GeneInformation::getGeneSymbol)
			.map(String::toUpperCase)
			.filter(gene -> gene.startsWith(query.toUpperCase()))
			.sorted()
		.toArray(String[]::new);
	}
	
	public static String[] geneSymbolsForQuery(String query, int maxResults) {
		return stream(geneInformations())
			.map(GeneInformation::getGeneSymbol)
			.map(String::toUpperCase)
			.filter(gene -> gene.startsWith(query.toUpperCase()))
			.sorted()
			.limit(maxResults)
		.toArray(String[]::new);
	}
	
	public static GeneInformation absentGeneInformation() {
		return new GeneInformation(absentGeneSymbol(), SIGNIFICANTLY_MUTATED, false, null, 0.5910312718d);
	}
	
	public static String absentGeneSymbol() {
		return "SIRT4";
	}
	
	public static String[] absentGeneSymbols() {
		return new String[] { "SIRT4", "BRCA1", "NF1" };
	}
	
	public static String presentGeneSymbol() {
		return "GATA2";
	}
	
	public static String[] presentGeneSymbols() {
		return stream(geneInformations())
			.map(GeneInformation::getGeneSymbol)
		.toArray(String[]::new);
	}
}
