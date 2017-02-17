/*
 * #%L
 * PanDrugs Backend
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
package es.uvigo.ei.sing.pandrugs.persistence.entity;

import static es.uvigo.ei.sing.pandrugs.persistence.entity.DriverLevel.HIGH_CONFIDENCE_DRIVER;
import static es.uvigo.ei.sing.pandrugs.persistence.entity.TumorPortalMutationLevel.NEAR_SIGNIFICANCE;
import static es.uvigo.ei.sing.pandrugs.persistence.entity.TumorPortalMutationLevel.SIGNIFICANTLY_MUTATED;
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

public final class GeneDataset {
	private GeneDataset() {}

	public static Gene newGene(
		String geneSymbol,
		TumorPortalMutationLevel tumorPortalMutationLevel,
		boolean cgc,
		DriverLevel driverLevel,
		Double geneEssentialityScore,
		boolean ccle,
		OncodriveRole oncodriveRole
	) {
		return new Gene(geneSymbol, tumorPortalMutationLevel, cgc, driverLevel, geneEssentialityScore, ccle, oncodriveRole);
	}
	
	public static Gene[] genes() {
		final Set<Gene> gata2Interactions = new HashSet<>();
		final Set<Gene> cdc42bpaInteractions = new HashSet<>();
		final Set<Gene> ppp3r1Interactions = new HashSet<>();
		final Set<Gene> prkag2Interactions = new HashSet<>();
		
		final Set<Gene> pathwayGenes000002 = new HashSet<>();
		final Set<Gene> pathwayGenes000003 = new HashSet<>();
		final Set<Gene> pathwayGenes000004 = new HashSet<>();
		
		final Set<Pathway> gata2Pathways = new HashSet<>();
		final Set<Pathway> adamts19Pathways = new HashSet<>();
		final Set<Pathway> cdc42bpaPathways = new HashSet<>();
		final Set<Pathway> prkag2Pathways = new HashSet<>();
		final Set<Pathway> ptgs1Pathways = new HashSet<>();
		
		final Gene gata2 = new Gene("GATA2", NEAR_SIGNIFICANCE, true, null, 0d, true, OncodriveRole.NONE, emptySet(), emptySet(), emptySet(), emptySet(), gata2Interactions, gata2Pathways);
		final Gene adamts19 = new Gene("ADAMTS19", null, false, null, 0.3556896485d, true, OncodriveRole.NONE, emptySet(), emptySet(), emptySet(), emptySet(), emptySet(), adamts19Pathways);
		final Gene cdc42bpa = new Gene("CDC42BPA", NEAR_SIGNIFICANCE, false, null, 0.8202555173d, true, OncodriveRole.NONE, emptySet(), emptySet(), emptySet(), emptySet(), cdc42bpaInteractions, cdc42bpaPathways);
		final Gene prkag2 = new Gene("PRKAG2", null, false, null, 0d, false, OncodriveRole.NONE, emptySet(), emptySet(), emptySet(), emptySet(), prkag2Interactions, prkag2Pathways);
		final Gene ptgs1 = new Gene("PTGS1", null, false, null, 0.4277512382d, false, OncodriveRole.NONE, emptySet(), emptySet(), emptySet(), emptySet(), emptySet(), ptgs1Pathways);
		final Gene ppp3r1 = new Gene("PPP3R1", null, false, null, 0d, true, OncodriveRole.NONE, emptySet(), emptySet(), emptySet(), emptySet(), ppp3r1Interactions, emptySet());
		final Gene max = new Gene("MAX", null, true, HIGH_CONFIDENCE_DRIVER, 0.626823283d, true, OncodriveRole.ACTIVATING);
		final Gene cx3cr1 = new Gene("CX3CR1", null, false, null, 0d, false, OncodriveRole.NONE);
		final Gene dmd = new Gene("DMD", null, false, HIGH_CONFIDENCE_DRIVER, 0d, false, OncodriveRole.NONE);
		
		final Pathway pathway00002 = new Pathway("hsa00002", "Single Gene Pathway", pathwayGenes000002);
		final Pathway pathway00003 = new Pathway("hsa00003", "Multiple Gene Pathway 1", pathwayGenes000003);
		final Pathway pathway00004 = new Pathway("hsa00004", "Multiple Gene Pathway 2", pathwayGenes000004);
		
		gata2Interactions.add(adamts19);
		gata2Interactions.add(cdc42bpa);
		gata2Interactions.add(prkag2);
		cdc42bpaInteractions.add(adamts19);
		cdc42bpaInteractions.add(ptgs1);
		cdc42bpaInteractions.add(ppp3r1);
		ppp3r1Interactions.add(dmd);
		prkag2Interactions.add(max);
		
		pathwayGenes000002.add(gata2);
		pathwayGenes000003.add(gata2);
		pathwayGenes000003.add(adamts19);
		pathwayGenes000003.add(cdc42bpa);
		pathwayGenes000004.add(cdc42bpa);
		pathwayGenes000004.add(prkag2);
		pathwayGenes000004.add(ptgs1);
		
		gata2Pathways.add(pathway00002);
		gata2Pathways.add(pathway00003);
		adamts19Pathways.add(pathway00003);
		cdc42bpaPathways.add(pathway00003);
		cdc42bpaPathways.add(pathway00004);
		prkag2Pathways.add(pathway00004);
		ptgs1Pathways.add(pathway00004);
		
		return new Gene[] {
			gata2, adamts19, cdc42bpa, prkag2, ptgs1, ppp3r1, max, cx3cr1, dmd
		};
	}
	
	public static Gene[] genes(String ... geneSymbols) {
		final Set<String> geneSymbolsSet = new HashSet<>(asList(geneSymbols));
		
		return stream(genes())
			.filter(gene -> geneSymbolsSet.contains(gene.getGeneSymbol()))
		.toArray(Gene[]::new);
	}

	public static Gene gene(String geneSymbol) {
		return stream(genes())
			.filter(gene -> gene.getGeneSymbol().equals(geneSymbol))
			.findFirst()
		.orElseThrow(IllegalArgumentException::new);
	}
	
	public static String[] geneSymbolsWithMaxInteractionDegree(int degree) {
		final Map<Integer, Supplier<String[]>> interactionsByDegree = new HashMap<>();
		interactionsByDegree.put(0, GeneDataset::geneSymbolsWithMaxInteractionDegree0);
		interactionsByDegree.put(1, GeneDataset::geneSymbolsWithMaxInteractionDegree1);
		interactionsByDegree.put(2, GeneDataset::geneSymbolsWithMaxInteractionDegree2);
		interactionsByDegree.put(3, GeneDataset::geneSymbolsWithMaxInteractionDegree3);

		return interactionsByDegree.getOrDefault(degree, () -> new String[0]).get();
	}
	
	public static String[] geneSymbolsWithInteractionDegreeUpTo(int maxDegree) {
		return IntStream.rangeClosed(0, maxDegree)
			.mapToObj(GeneDataset::geneSymbolsWithMaxInteractionDegree)
			.flatMap(Arrays::stream)
		.toArray(String[]::new);
	}
	
	public static String[] geneSymbolsWithMaxInteractionDegree0() {
		return new String[] { "MAX", "DMD", "CX3CR1" };
	}
	
	public static String[] geneSymbolsWithMaxInteractionDegree1() {
		return new String[] { "PPP3R1", "PRKAG2" };
	}
	
	public static String[] geneSymbolsWithMaxInteractionDegree2() {
		return new String[] { "CDC42BPA" };
	}
	
	public static String[] geneSymbolsWithMaxInteractionDegree3() {
		return new String[] { "GATA2" };
	}
	
	public static Gene[] interactions(int degree, Iterable<String> geneSymbols) {
		return interactions(degree, stream(geneSymbols.spliterator(), false).toArray(String[]::new));
	}
	
	
	public static Gene[] interactions(int degree, String ... geneSymbols) {
		final Set<Gene> interactions = new HashSet<>(asList(genes(geneSymbols)));
		
		for (int i = 0; i < degree; i++) {
			for (Gene interaction : new HashSet<>(interactions)) {
				interactions.addAll(interaction.getInteractingGenes());
			}
		}
		
		return interactions.toArray(new Gene[interactions.size()]);
	}
	
	public static String[] geneSymbolsForQuery(String query) {
		return stream(genes())
			.map(Gene::getGeneSymbol)
			.map(String::toUpperCase)
			.filter(gene -> gene.startsWith(query.toUpperCase()))
			.sorted()
		.toArray(String[]::new);
	}
	
	public static String[] geneSymbolsForQuery(String query, int maxResults) {
		return stream(genes())
			.map(Gene::getGeneSymbol)
			.map(String::toUpperCase)
			.filter(gene -> gene.startsWith(query.toUpperCase()))
			.sorted()
			.limit(maxResults)
		.toArray(String[]::new);
	}
	
	public static Gene absentGene() {
		return new Gene(absentGeneSymbol(), SIGNIFICANTLY_MUTATED, false, null, 0.5910312718d, false, OncodriveRole.NONE);
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
		return stream(genes())
			.map(Gene::getGeneSymbol)
		.toArray(String[]::new);
	}
	
	public static Gene[] genesWithPathway() {
		return stream(genes())
			.filter(gene -> !gene.getPathways().isEmpty())
		.toArray(Gene[]::new);
	}
	
	public static Gene[] genesWithoutPathway() {
		return stream(genes())
			.filter(gene -> gene.getPathways().isEmpty())
		.toArray(Gene[]::new);
	}
}
