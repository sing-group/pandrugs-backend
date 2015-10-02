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
import static java.util.Collections.unmodifiableList;
import static java.util.stream.Collectors.toList;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.function.ToDoubleFunction;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import es.uvigo.ei.sing.pandrugsdb.persistence.entity.CancerType;
import es.uvigo.ei.sing.pandrugsdb.persistence.entity.DrugSource;
import es.uvigo.ei.sing.pandrugsdb.persistence.entity.DrugStatus;
import es.uvigo.ei.sing.pandrugsdb.persistence.entity.Extra;
import es.uvigo.ei.sing.pandrugsdb.persistence.entity.GeneDrug;
import es.uvigo.ei.sing.pandrugsdb.persistence.entity.GeneInformation;
import es.uvigo.ei.sing.pandrugsdb.persistence.entity.Weighted;

public class GeneDrugGroup {
	private final String[] targetGenes;
	private final List<GeneDrug> geneDrugs;

	public GeneDrugGroup(
		String[] targetGenes,
		List<GeneDrug> geneDrugs
	) {
		requireNonEmpty(targetGenes);
		requireNonEmpty(geneDrugs);
		
		final Predicate<String> isInGenes =
			gd -> Stream.of(targetGenes).anyMatch(tg -> tg.equals(gd));
		final Predicate<GeneDrug> hasInIndirectGenes = 
			gd -> Stream.of(targetGenes).anyMatch(tg -> gd.getIndirectGenes().contains(tg));
		
		final boolean checkGenes = geneDrugs.stream()
			.allMatch(gd -> isInGenes.test(gd.getGeneSymbol())
				|| hasInIndirectGenes.test(gd));
		if (!checkGenes)
			throw new IllegalArgumentException("Invalid geneDrugs for targetGenes");
			
		checkSingleValue(
			geneDrugs, GeneDrug::getStandardDrugName,
			() -> new IllegalArgumentException("Different standard drug names in group")
		);
		
		checkSingleValue(
			geneDrugs, GeneDrug::getStatus,
			() -> new IllegalArgumentException("Different status in group")
		);
		
		checkSameIterableValues(
			geneDrugs, GeneDrug::getPathologies,
			() -> new IllegalArgumentException("Different pathologies in group")
		);
		
		checkSameIterableValues(
			geneDrugs, GeneDrug::getCancer,
			() -> new IllegalArgumentException("Different cancer in group")
		);

		checkSingleValue(
			geneDrugs, GeneDrug::getExtra,
			() -> new IllegalArgumentException("Different extra in group")
		);
		
		this.targetGenes = targetGenes;
		this.geneDrugs = geneDrugs;
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
			.map(GeneDrug::getGeneSymbol)
			.filter(this::isNotInTargetGenes)
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
		return !this.isDirect(geneDrug);
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

	public List<CancerType> getCancers() {
		return this.geneDrugs.get(0).getCancer();
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
	
	public String[] getTargetGeneNames(GeneDrug geneDrug) {
		return this.isDirect(geneDrug) ?
			new String[] { geneDrug.getGeneSymbol() } :
			geneDrug.getIndirectGenes().stream()
				.filter(this::isInTargetGenes)
			.toArray(String[]::new);
	}

	public String getIndirectGeneName(GeneDrug geneDrug) {
		return this.isDirect(geneDrug) ?
			null : geneDrug.getGeneSymbol();
	}
	
	//TODO: test d-score
	public double getDScore(GeneDrug geneDrug) {
		if (!this.geneDrugs.contains(geneDrug))
			throw new IllegalArgumentException("geneDrug doesn't belongs to this group");
		
		double score = Math.abs(geneDrug.getScore());
		
		switch (this.getStatus()) {
		case EXPERIMENTAL:
			score -= (this.isOnlyIndirect() ? 0.0002d : 0d);
			break;
		case APPROVED:
		case CLINICAL_TRIALS:
			score -= 0.1d;
			score += Math.min(9, this.targetGenes.length) * 0.01d;
			if (this.isOnlyIndirect())
				score -= 0.01d;
			
			score += geneDrug.getCuratedDrugSourceNames().size() * 0.001d + 0.001d;
			break;
		default:
			return Double.NaN;
		}
		
		return this.hasResistance() ? -score : score;
	}

	public double getGScore(GeneDrug geneDrug) {
		if (!this.geneDrugs.contains(geneDrug))
			throw new IllegalArgumentException("geneDrug doesn't belongs to this group");
		
		final ToDoubleFunction<GeneInformation> tpScore = gi -> Optional.ofNullable(gi)
			.map(GeneInformation::getTumorPortalMutationLevel)
			.map(Weighted::getWeightOf)
		.orElse(0d);
			
		final ToDoubleFunction<GeneInformation> cgcScore = gi -> Optional.ofNullable(gi)
			.map(GeneInformation::isCgc)
			.map(x -> x ? 0.2d : 0d)
		.orElse(0d);
			
		final ToDoubleFunction<GeneInformation> driverScore = gi -> Optional.ofNullable(gi)
			.map(GeneInformation::getDriverLevel)
			.map(Weighted::getWeightOf)
		.orElse(0d);
			
		final ToDoubleFunction<GeneInformation> geScore = gi -> Optional.ofNullable(gi)
			.map(GeneInformation::getGeneEssentialityScore)
			.map(score -> score * 0.4d)
		.orElse(0d);
		
		final GeneInformation geneInformation = geneDrug.getGeneInformation();
		return tpScore.applyAsDouble(geneInformation)
			+ cgcScore.applyAsDouble(geneInformation)
			+ driverScore.applyAsDouble(geneInformation)
			+ geScore.applyAsDouble(geneInformation);
	}
	
	//TODO: test d-score
	public double getDScore() {
		final double dScore = this.geneDrugs.stream()
			.mapToDouble(this::getDScore)
			.map(Math::abs)
		.max().orElse(Double.NaN);
		
		return this.hasResistance() ? -dScore : dScore;
	}

	public double getGScore() {
		return this.geneDrugs.stream()
			.mapToDouble(this::getGScore)
		.max().orElse(Double.NaN);
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
		List<GeneDrug> geneDrugs,
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
	
	private final static <T> void checkSameIterableValues(
		List<GeneDrug> geneDrugs,
		Function<GeneDrug, ? extends Iterable<T>> mapper,
		Supplier<RuntimeException> thrower
	) {
		final List<? extends Iterable<T>> iterables = geneDrugs.stream()
			.map(mapper)
		.collect(toList());
		
		final Iterable<T> base = iterables.get(0);
		
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
}
