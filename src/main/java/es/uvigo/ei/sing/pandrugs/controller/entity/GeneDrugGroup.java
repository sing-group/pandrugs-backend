/*
 * #%L
 * PanDrugs Backend
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
package es.uvigo.ei.sing.pandrugs.controller.entity;

import static es.uvigo.ei.sing.pandrugs.util.Checks.requireNonEmpty;
import static es.uvigo.ei.sing.pandrugs.util.CompareCollections.equalsIgnoreOrder;
import static java.util.Arrays.stream;
import static java.util.Collections.emptyMap;
import static java.util.Collections.emptySet;
import static java.util.Collections.unmodifiableList;
import static java.util.Collections.unmodifiableMap;
import static java.util.Collections.unmodifiableSet;
import static java.util.Objects.requireNonNull;
import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toMap;
import static java.util.stream.Collectors.toSet;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.stream.Stream;

import es.uvigo.ei.sing.pandrugs.persistence.entity.CancerType;
import es.uvigo.ei.sing.pandrugs.persistence.entity.Drug;
import es.uvigo.ei.sing.pandrugs.persistence.entity.DrugSource;
import es.uvigo.ei.sing.pandrugs.persistence.entity.DrugStatus;
import es.uvigo.ei.sing.pandrugs.persistence.entity.Extra;
import es.uvigo.ei.sing.pandrugs.persistence.entity.Gene;
import es.uvigo.ei.sing.pandrugs.persistence.entity.GeneDrug;
import es.uvigo.ei.sing.pandrugs.persistence.entity.GeneDrugWarning;
import es.uvigo.ei.sing.pandrugs.persistence.entity.IndirectGene;
import es.uvigo.ei.sing.pandrugs.service.drugscore.ByGroupDrugScoreCalculator;
import es.uvigo.ei.sing.pandrugs.service.drugscore.DrugScoreCalculator;
import es.uvigo.ei.sing.pandrugs.service.genescore.DefaultGeneScoreCalculator;
import es.uvigo.ei.sing.pandrugs.service.genescore.GeneScoreCalculator;

public class GeneDrugGroup {
	private final String[] queryGenes;
	private final Drug drug;
	private final List<GeneDrug> geneDrugs;
	private final GeneScoreCalculator geneScoreCalculator;
	private final DrugScoreCalculator drugScoreCalculator;
	
	private final Set<GeneDrugWarning> warnings;
	private final Map<String, Set<String>> resistanceCausedBy;

	public GeneDrugGroup(
		String[] queryGenes,
		Collection<GeneDrug> geneDrugs,
		Set<GeneDrugWarning> drugWarnings,
		Map<String, Set<String>> resistanceCausedBy
	) {
		this(queryGenes, geneDrugs, drugWarnings, resistanceCausedBy, new DefaultGeneScoreCalculator(), new ByGroupDrugScoreCalculator());
	}

	public GeneDrugGroup(
		String[] queryGenes,
		Collection<GeneDrug> geneDrugs,
		Set<GeneDrugWarning> drugWarnings,
		Map<String, Set<String>> resistanceCausedBy,
		GeneScoreCalculator geneScoreCalculator,
		DrugScoreCalculator drugScoreCalculator
	) {
		requireNonEmpty(queryGenes);
		requireNonEmpty(geneDrugs);
		
		this.geneScoreCalculator = requireNonNull(geneScoreCalculator);
		this.drugScoreCalculator = requireNonNull(drugScoreCalculator);
		
		if (drugWarnings.isEmpty()) {
			this.warnings = emptySet();
		} else {
			this.warnings = unmodifiableSet(new HashSet<>(drugWarnings));
		}
		
		if (resistanceCausedBy.isEmpty()) {
			this.resistanceCausedBy = emptyMap();
		} else {
			this.resistanceCausedBy = unmodifiableMap(resistanceCausedBy); // TODO: Make map' sets unmodifiable
		}
		
		final Predicate<String> isInGenes =
			gd -> Stream.of(queryGenes).anyMatch(gd::equals);
		final Predicate<GeneDrug> hasInIndirectGenes = 
			gd -> Stream.of(queryGenes).anyMatch(tg -> gd.getIndirectGeneSymbols().contains(tg));
		
		final boolean checkGenes = geneDrugs.stream()
			.allMatch(gd -> isInGenes.test(gd.getGeneSymbol())
				|| hasInIndirectGenes.test(gd));
		if (!checkGenes)
			throw new IllegalArgumentException("Invalid geneDrugs for queryGenes");
			
		checkSingleValue(
			geneDrugs, GeneDrug::getDrugId,
			() -> new IllegalArgumentException("Different drugs in group")
		);
		
		checkSingleValue(
			geneDrugs, GeneDrug::getStatus,
			() -> new IllegalArgumentException("Different status in group")
		);
		
		checkSameArrayValues(
			geneDrugs, GeneDrug::getPathologies,
			() -> new IllegalArgumentException("Different pathologies in group")
		);
		
		checkSameArrayValues(
			geneDrugs, GeneDrug::getCancers,
			() -> new IllegalArgumentException("Different cancer in group")
		);

		checkSingleValue(
			geneDrugs, GeneDrug::getExtra,
			() -> new IllegalArgumentException("Different extra in group")
		);
		
		this.queryGenes = queryGenes;
		this.geneDrugs = new ArrayList<>(geneDrugs);
		this.drug = this.geneDrugs.get(0).getDrug();
	}
	
	public List<GeneDrug> getGeneDrugs() {
		return unmodifiableList(geneDrugs);
	}
	
	public Set<GeneDrugWarning> getDrugWarnings() {
		return unmodifiableSet(this.warnings);
	}
	
	public Set<GeneDrugWarning> getDrugWarnings(Collection<String> geneSymbols) {
		return this.warnings.stream()
			.filter(dw -> geneSymbols.contains(dw.getGeneSymbol()))
		.collect(toSet());
	}
	
	public Set<String> getIndirectResistance(String geneSymbol) {
		return this.resistanceCausedBy.get(geneSymbol);
	}
	
	public boolean hasIndirectResistance(String geneSymbol) {
		return this.resistanceCausedBy.containsKey(geneSymbol);
	}
	
	public boolean hasIndirectResistances(GeneDrug geneDrug) {
		if (!this.geneDrugs.contains(geneDrug))
			throw new IllegalArgumentException("geneDrug doesn't belongs to this group");
		
		return this.resistanceCausedBy.containsKey(geneDrug.getGeneSymbol());
	}
	
	public String[] getQueryGeneSymbols() {
		return this.queryGenes;
	}
	
	public Gene[] getQueryGenes() {
		return stream(getQueryGeneSymbols())
			.map(this::getGeneFromGeneDrugs)
		.toArray(Gene[]::new);
	}
	
	private Gene getGeneFromGeneDrugs(final String geneSymbol) {
		return this.geneDrugs.stream()
			.map(GeneDrug::getDirectAndIndirectGenes)
			.flatMap(List::stream)
			.filter(gd -> gd.getGeneSymbol().equals(geneSymbol))	
			.findAny()
		.orElseThrow(() -> new IllegalArgumentException("Invalid gene symbol: " + geneSymbol));
	}

	public String[] getDirectGeneSymbols() {
		return this.geneDrugs.stream()
			.map(GeneDrug::getGeneSymbol)
			.filter(this::isInQueryGenes)
			.distinct()
			.sorted()
		.toArray(String[]::new);
	}

	public String[] getIndirectGeneSymbols() {
		return this.geneDrugs.stream()
			.map(GeneDrug::getIndirectGeneSymbols)
			.flatMap(List::stream)
			.filter(this::isInQueryGenes)
			.distinct()
			.sorted()
		.toArray(String[]::new);
	}
	
	public Gene[] getIndirectGenes() {
		return stream(getIndirectGeneSymbols())
			.map(this::getGeneFromGeneDrugs)
		.toArray(Gene[]::new);
	}

	public boolean isDirect(GeneDrug geneDrug) {
		if (!this.geneDrugs.contains(geneDrug))
			throw new IllegalArgumentException("geneDrug doesn't belongs to this group");
		
		return this.isInQueryGenes(geneDrug.getGeneSymbol());
	}

	public boolean isIndirect(GeneDrug geneDrug) {
		if (!this.geneDrugs.contains(geneDrug))
			throw new IllegalArgumentException("geneDrug doesn't belongs to this group");
		
		final List<String> gdIndirect = geneDrug.getIndirectGeneSymbols();
		
		return stream(this.getIndirectGeneSymbols())
			.anyMatch(gdIndirect::contains);
	}
	
	public boolean isDirectAndIndirect(GeneDrug geneDrug) {
		return this.isDirect(geneDrug) && this.isIndirect(geneDrug);
	}

	public int countQueryGenes() {
		return this.queryGenes.length;
	}
	
	public int countDirectGenes() {
		return (int) this.geneDrugs.stream()
			.map(GeneDrug::getGeneSymbol)
			.filter(this::isInQueryGenes)
		.count();
	}
	
	public int countIndirectGenes() {
		return (int) this.geneDrugs.stream()
			.map(GeneDrug::getGeneSymbol)
			.filter(this::isNotInQueryGenes)
		.count();
	}
	
	public boolean isOnlyIndirect() {
		return this.countDirectGenes() == 0;
	}
	
	public String getStandardDrugName() {
		return this.drug.getStandardName();
	}
	
	public String getShowDrugName() {
		return this.drug.getShowName();
	}
	
	public DrugStatus getStatus() {
		return this.drug.getStatus();
	}

	public int[] getPubchemId() {
		return this.drug.getPubChemIds();
	}

	public CancerType[] getCancers() {
		return this.drug.getCancers();
	}

	public Extra getExtra() {
		return this.drug.getExtra();
	}

	public String[] getFamilies() {
		return this.geneDrugs.stream()
			.map(GeneDrug::getDrug)
			.map(Drug::getFamilies)
			.flatMap(Arrays::stream)
			.distinct()
			.sorted()
		.toArray(String[]::new);
	}

	public DrugSource[] getSources() {
		return this.geneDrugs.stream()
			.map(GeneDrug::getDrugSources)
			.flatMap(Set::stream)
			.distinct()
		.toArray(DrugSource[]::new);
	}
	
	public String[] getSourceNames() {
		return Stream.of(this.getSources())
			.map(DrugSource::getSource)
			.sorted()
			.distinct()
		.toArray(String[]::new);
	}
	
	public SortedMap<String, String> getSourceLinks() {
		return new TreeMap<>(
			Stream.of(this.getSources())
			.collect(toMap(
				DrugSource::getSource,
				ds -> ds.getDrugURL(this.queryGenes),
				(v1, v2) -> v1
			))
		);
	}
	
	public SortedMap<String, String> getSourceShortNames() {
		return new TreeMap<>(
			Stream.of(this.getSources())
			.collect(toMap(
				DrugSource::getSource,
				ds -> ds.getSourceInformation().getShortName(),
				(v1, v2) -> v1
			))
		);
	}
	
	public DrugSource[] getCuratedSources() {
		return this.geneDrugs.stream()
			.map(GeneDrug::getCuratedDrugSources)
			.flatMap(List::stream)
			.distinct()
		.toArray(DrugSource[]::new);
	}
	
	public String[] getCuratedSourceNames() {
		return Stream.of(this.getCuratedSources())
			.map(DrugSource::getSource)
			.distinct()
			.sorted()
		.toArray(String[]::new);
	}
	
	public boolean isTarget() {
		return this.geneDrugs.stream()
			.anyMatch(GeneDrug::isTarget);
	}
	
	public boolean hasResistance() {
		for (GeneDrug geneDrug : this.getGeneDrugs()) {
			if (geneDrug.isResistance()) {
				return true;
			}
		}
		
		return false;
	}
	
	public Gene[] getQueryGenesForGeneDrug(GeneDrug geneDrug, boolean forceIndirect) {
		return !forceIndirect && this.isDirect(geneDrug) ?
			new Gene[] { geneDrug.getGene() } :
			geneDrug.getIndirectGenes().stream()
				.filter(this::isInQueryGenes)
				.map(IndirectGene::getGene)
			.toArray(Gene[]::new);
	}
	
	public String[] getQueryGeneSymbolsForGeneDrug(GeneDrug geneDrug, boolean forceIndirect) {
		return !forceIndirect && this.isDirect(geneDrug) ?
			new String[] { geneDrug.getGeneSymbol() } :
			geneDrug.getIndirectGeneSymbols().stream()
				.filter(this::isInQueryGenes)
			.toArray(String[]::new);
	}
	
	public Gene getIndirectGene(GeneDrug geneDrug, boolean forceIndirect) {
		return !forceIndirect && this.isDirect(geneDrug) ?
			null : geneDrug.getGene();
	}

	public String getIndirectGeneSymbol(GeneDrug geneDrug, boolean forceIndirect) {
		return !forceIndirect && this.isDirect(geneDrug) ?
			null : geneDrug.getGeneSymbol();
	}
	
	//TODO: test d-score
	public double getDScore(GeneDrug geneDrug) {
		if (!this.geneDrugs.contains(geneDrug))
			throw new IllegalArgumentException("geneDrug doesn't belongs to this group");
		
		return this.drugScoreCalculator.calculateGeneDrugScore(this, geneDrug);
	}
	
	//TODO: test d-score
	public double getDScore() {
		return this.drugScoreCalculator.calculateGeneDrugGroupScore(this);
	}
	
	public double getGScore(GeneDrug geneDrug) {
		if (!this.geneDrugs.contains(geneDrug))
			throw new IllegalArgumentException("geneDrug doesn't belongs to this group");
		
		return this.geneScoreCalculator.calculateDirectScore(geneDrug);
	}
	
	public Map<String, Double> getIndirectGScores(GeneDrug geneDrug) {
		if (!this.geneDrugs.contains(geneDrug))
			throw new IllegalArgumentException("geneDrug doesn't belongs to this group");
		
		return this.geneScoreCalculator.calculateIndirectScores(geneDrug);
	}

	public double getGScore() {
		final double maxDirect = this.geneDrugs.stream()
			.filter(this::isDirect)
			.mapToDouble(this.geneScoreCalculator::calculateDirectScore)
		.max().orElse(0d);
		
		final double maxIndirect = this.geneDrugs.stream()
			.filter(this::isIndirect)
			.flatMapToDouble(gd -> stream(this.queryGenes)
				.mapToDouble(tg -> this.geneScoreCalculator.calculateIndirectScore(gd, tg)))
		.max().orElse(0d);
		
		return Math.max(maxDirect, maxIndirect);
	}
	
	private boolean isInQueryGenes(IndirectGene indirectGene) {
		return isInQueryGenes(indirectGene.getGeneSymbol());
	}
	
	private boolean isInQueryGenes(String geneSymbol) {
		return Stream.of(this.queryGenes)
			.anyMatch(tg -> tg.equals(geneSymbol));
	}
	
	private boolean isNotInQueryGenes(String geneSymbol) {
		return Stream.of(this.queryGenes)
			.noneMatch(tg -> tg.equals(geneSymbol));
	}
	
	private final static <T> void checkSingleValue(
		Collection<GeneDrug> geneDrugs,
		Function<GeneDrug, T> mapper,
		Supplier<RuntimeException> thrower
	) {
		final long count = geneDrugs.stream()
			.map(mapper)
			.distinct()
		.count();
		
		if (count != 1) 
			throw thrower.get();
	}
	
	private final static <T> void checkSameArrayValues(
		Collection<GeneDrug> geneDrugs,
		Function<GeneDrug, T[]> mapper,
		Supplier<RuntimeException> thrower
	) {
		final List<T[]> iterables = geneDrugs.stream()
			.map(mapper)
		.collect(toList());
		
		final T[] base = iterables.get(0);
		
		for (int i = 1; i < iterables.size(); i++) {
			if (!equalsIgnoreOrder(base, iterables.get(i))) {
				throw thrower.get();
			}
		}
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((geneDrugs == null) ? 0 : geneDrugs.hashCode());
		result = prime * result + Arrays.hashCode(queryGenes);
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		GeneDrugGroup other = (GeneDrugGroup) obj;
		if (!equalsIgnoreOrder(geneDrugs, other.geneDrugs)) {
			return false;
		}
		if (!equalsIgnoreOrder(queryGenes, other.queryGenes)) {
			return false;
		}
		return true;
	}

	@Override
	public String toString() {
		return "GeneDrugGroup [queryGenes=" + Arrays.toString(queryGenes) + ", geneDrugs=" + geneDrugs + "]";
	}
}
