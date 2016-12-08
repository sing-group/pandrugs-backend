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
package es.uvigo.ei.sing.pandrugsdb.controller.entity;

import static es.uvigo.ei.sing.pandrugsdb.util.Checks.requireNonEmpty;
import static es.uvigo.ei.sing.pandrugsdb.util.CompareCollections.equalsIgnoreOrder;
import static java.lang.Math.abs;
import static java.lang.Math.min;
import static java.util.Arrays.stream;
import static java.util.Collections.unmodifiableList;
import static java.util.Objects.requireNonNull;
import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toSet;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import es.uvigo.ei.sing.pandrugsdb.persistence.entity.CancerType;
import es.uvigo.ei.sing.pandrugsdb.persistence.entity.DrugSource;
import es.uvigo.ei.sing.pandrugsdb.persistence.entity.DrugStatus;
import es.uvigo.ei.sing.pandrugsdb.persistence.entity.Extra;
import es.uvigo.ei.sing.pandrugsdb.persistence.entity.GeneDrug;
import es.uvigo.ei.sing.pandrugsdb.service.genescore.DefaultGeneScoreCalculator;
import es.uvigo.ei.sing.pandrugsdb.service.genescore.GeneScoreCalculator;

public class GeneDrugGroup {
	private final String[] targetGenes;
	private final List<GeneDrug> geneDrugs;
	private final GeneScoreCalculator geneScoreCalculator;

	public GeneDrugGroup(
		String[] targetGenes,
		Collection<GeneDrug> geneDrugs
	) {
		this(targetGenes, geneDrugs, new DefaultGeneScoreCalculator());
	}

	public GeneDrugGroup(
		String[] targetGenes,
		Collection<GeneDrug> geneDrugs,
		GeneScoreCalculator geneScoreCalculator
	) {
		requireNonEmpty(targetGenes);
		requireNonEmpty(geneDrugs);
		
		this.geneScoreCalculator = requireNonNull(geneScoreCalculator);
		
		final Predicate<String> isInGenes =
			gd -> Stream.of(targetGenes).anyMatch(tg -> tg.equals(gd));
		final Predicate<GeneDrug> hasInIndirectGenes = 
			gd -> Stream.of(targetGenes).anyMatch(tg -> gd.getIndirectGeneSymbols().contains(tg));
		
		final boolean checkGenes = geneDrugs.stream()
			.allMatch(gd -> isInGenes.test(gd.getGeneSymbol())
				|| hasInIndirectGenes.test(gd));
		if (!checkGenes)
			throw new IllegalArgumentException("Invalid geneDrugs for targetGenes");
			
		checkSingleValue(
			geneDrugs, GeneDrug::getDrug,
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
		
		this.targetGenes = targetGenes;
		this.geneDrugs = new ArrayList<>(geneDrugs);
	}
	
	public List<GeneDrug> getGeneDrugs() {
		return unmodifiableList(geneDrugs);
	}

	public String[] getTargetGenes() {
		return this.targetGenes;
	}

	public String[] getDirectGenes() {
		return this.geneDrugs.stream()
			.map(GeneDrug::getGeneSymbol)
			.filter(this::isInTargetGenes)
			.distinct()
			.sorted()
		.toArray(String[]::new);
	}

	public String[] getIndirectGenes() {
		return this.geneDrugs.stream()
			.map(GeneDrug::getIndirectGeneSymbols)
			.flatMap(List::stream)
			.filter(this::isInTargetGenes)
			.distinct()
			.sorted()
		.toArray(String[]::new);
	}

	public boolean isDirect(GeneDrug geneDrug) {
		if (!this.geneDrugs.contains(geneDrug))
			throw new IllegalArgumentException("geneDrug doesn't belongs to this group");
		
		return this.isInTargetGenes(geneDrug.getGeneSymbol());
	}

	public boolean isIndirect(GeneDrug geneDrug) {
		if (!this.geneDrugs.contains(geneDrug))
			throw new IllegalArgumentException("geneDrug doesn't belongs to this group");
		
		final Set<String> indirect = stream(this.getIndirectGenes())
			.collect(toSet());
		indirect.retainAll(geneDrug.getIndirectGeneSymbols());
		
		return !indirect.isEmpty();
	}
	
	public boolean isDirectAndIndirect(GeneDrug geneDrug) {
		return this.isDirect(geneDrug) && this.isIndirect(geneDrug);
	}

	public int countTargetGenes() {
		return this.targetGenes.length;
	}
	
	public int countDirectGenes() {
		return (int) this.geneDrugs.stream()
			.map(GeneDrug::getGeneSymbol)
			.filter(this::isInTargetGenes)
		.count();
	}
	
	public int countIndirectGenes() {
		return (int) this.geneDrugs.stream()
			.map(GeneDrug::getGeneSymbol)
			.filter(this::isNotInTargetGenes)
		.count();
	}
	
	public boolean isOnlyIndirect() {
		return this.countDirectGenes() == 0;
	}
	
	public String getStandardDrugName() {
		return this.geneDrugs.get(0).getStandardDrugName();
	}
	
	public String getShowDrugName() {
		return this.geneDrugs.get(0).getShowDrugName();
	}
	
	public DrugStatus getStatus() {
		return this.geneDrugs.get(0).getStatus();
	}

	public int[] getPubchemId() {
		return this.geneDrugs.get(0).getDrug().getPubChemIds();
	}

	public CancerType[] getCancers() {
		return this.geneDrugs.get(0).getCancers();
	}

	public Extra getExtra() {
		return this.geneDrugs.get(0).getExtra();
	}

	public String[] getFamilies() {
		return this.geneDrugs.stream()
			.map(GeneDrug::getFamily)
			.distinct()
			.sorted()
		.toArray(String[]::new);
	}

	public DrugSource[] getSources() {
		return this.geneDrugs.stream()
			.map(GeneDrug::getDrugSources)
			.flatMap(List::stream)
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
			.collect(Collectors.toMap(
				DrugSource::getSource,
				ds -> ds.getDrugURL(this.targetGenes),
				(v1, v2) -> v1
			))
		);
	}
	
	public SortedMap<String, String> getSourceShortNames() {
		return new TreeMap<>(
			Stream.of(this.getSources())
			.collect(Collectors.toMap(
				DrugSource::getSource,
				ds -> ds.getSourceInformation().getShortName(),
				(v1, v2) -> v1
			))
		);
	}
	
	public DrugSource[] getCuratedSources() {
		return this.geneDrugs.stream()
			.map(GeneDrug::getDrugSources)
			.flatMap(List::stream)
			.filter(DrugSource::isCurated)
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
		return this.geneDrugs.stream()
			.anyMatch(GeneDrug::isResistance);
	}
	
	public String[] getTargetGeneNames(GeneDrug geneDrug, boolean forceIndirect) {
		return !forceIndirect && this.isDirect(geneDrug) ?
			new String[] { geneDrug.getGeneSymbol() } :
			geneDrug.getIndirectGeneSymbols().stream()
				.filter(this::isInTargetGenes)
			.toArray(String[]::new);
	}

	public String getIndirectGeneName(GeneDrug geneDrug, boolean forceIndirect) {
		return !forceIndirect && this.isDirect(geneDrug) ?
			null : geneDrug.getGeneSymbol();
	}
	
	//TODO: test d-score
	public double getDScore(GeneDrug geneDrug) {
		if (!this.geneDrugs.contains(geneDrug))
			throw new IllegalArgumentException("geneDrug doesn't belongs to this group");
		
		double score = abs(geneDrug.getScore());
		
		switch (this.getStatus()) {
		case EXPERIMENTAL:
			score -= (this.isOnlyIndirect() ? 0.0002d : 0d);
			break;
		case APPROVED:
		case CLINICAL_TRIALS:
			score -= 0.1d;
			score += min(9, this.targetGenes.length) * 0.01d;
			if (this.isOnlyIndirect())
				score -= 0.01d;
			
			score += min(9, geneDrug.getCuratedDrugSourceNames().size()) * 0.001d + 0.001d;
			break;
		default:
			return Double.NaN;
		}
		
		return this.hasResistance() ? -score : score;
	}
	
	//TODO: test d-score
	public double getDScore() {
		final double dScore = this.geneDrugs.stream()
			.mapToDouble(this::getDScore)
			.map(Math::abs)
		.max().orElse(Double.NaN);
		
		return this.hasResistance() ? -dScore : dScore;
	}
	
	public double getGScore(GeneDrug geneDrug) {
		if (!this.geneDrugs.contains(geneDrug))
			throw new IllegalArgumentException("geneDrug doesn't belongs to this group");
		
		return this.geneScoreCalculator.directGScore(geneDrug);
	}
	
	public Map<String, Double> getIndirectGScores(GeneDrug geneDrug) {
		if (!this.geneDrugs.contains(geneDrug))
			throw new IllegalArgumentException("geneDrug doesn't belongs to this group");
		
		return this.geneScoreCalculator.indirectGScores(geneDrug);
	}

	public double getGScore() {
		final double maxDirect = this.geneDrugs.stream()
			.filter(this::isDirect)
			.mapToDouble(this.geneScoreCalculator::directGScore)
		.max().orElse(0d);
		
		final double maxIndirect = this.geneDrugs.stream()
			.filter(this::isIndirect)
			.flatMapToDouble(gd -> stream(this.targetGenes)
				.mapToDouble(tg -> this.geneScoreCalculator.indirectGScore(gd, tg)))
		.max().orElse(0d);
		
		return Math.max(maxDirect, maxIndirect);
	}
	
	private boolean isInTargetGenes(String geneSymbol) {
		return Stream.of(this.targetGenes)
			.anyMatch(tg -> tg.equals(geneSymbol));
	}
	
	private boolean isNotInTargetGenes(String geneSymbol) {
		return Stream.of(this.targetGenes)
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
		result = prime * result + Arrays.hashCode(targetGenes);
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
		if (!equalsIgnoreOrder(targetGenes, other.targetGenes)) {
			return false;
		}
		return true;
	}

	@Override
	public String toString() {
		return "GeneDrugGroup [targetGenes=" + Arrays.toString(targetGenes) + ", geneDrugs=" + geneDrugs + "]";
	}
}
