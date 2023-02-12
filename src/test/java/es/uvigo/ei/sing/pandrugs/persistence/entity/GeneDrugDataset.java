/*-
 * #%L
 * PanDrugs Backend
 * %%
 * Copyright (C) 2015 - 2023 Fátima Al-Shahrour, Elena Piñeiro, Daniel Glez-Peña
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

import static es.uvigo.ei.sing.pandrugs.util.StringFormatter.toUpperCase;
import static java.util.Arrays.asList;
import static java.util.Arrays.stream;
import static java.util.Collections.emptyList;
import static java.util.Collections.emptyMap;
import static java.util.Collections.singletonMap;
import static java.util.function.Function.identity;
import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toMap;
import static java.util.stream.Collectors.toSet;
import static java.util.stream.Stream.concat;

import java.util.Collection;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Function;
import java.util.stream.Stream;

import es.uvigo.ei.sing.pandrugs.controller.entity.GeneDrugGroup;
import es.uvigo.ei.sing.pandrugs.service.entity.GeneDrugGroupInfos;
import es.uvigo.ei.sing.pandrugs.service.entity.GeneRanking;
import es.uvigo.ei.sing.pandrugs.util.Compare;

public final class GeneDrugDataset {
	private static final String ABSENT_DRUG_NAME = "ABSENT DRUG";
	private static final String DRUG_NAME_1 = "DRUG 1";
	private static final String DRUG_NAME_2 = "DRUG 2";
	private static final String DRUG_NAME_3 = "DRUG 3";
	private static final String DRUG_NAME_10 = "DRUG 10";
	private static final String DRUG_NAME_11 = "DRUG 11";
	private static final String DRUG_NAME_12 = "DRUG 12";
	private static final String DRUG_NAME_20 = "DRUG 20";
	private static final String DRUG_NAME_21 = "DRUG 21";
	private static final String DRUG_NAME_30 = "DRUG 30";
	private static final String DRUG_NAME_31 = "DRUG 31";
	private static final String DRUG_NAME_32 = "DRUG 32";
	private static final String DRUG_NAME_40 = "DRUG 40";
	private static final String DRUG_NAME_41 = "DRUG 41";
	private static final String DRUG_NAME_42 = "DRUG 42";
	private static final String ABSENT_GENE_SYMBOL_1 = "ABSENT GENE 1";
	private static final String ABSENT_GENE_SYMBOL_2 = "ABSENT GENE 2";
	private static final String ABSENT_GENE_SYMBOL_3 = "ABSENT GENE 3";
	private static final String PATHWAY_MEMBER_GENE_SYMBOL_1 = "PM1";
	private static final String PATHWAY_MEMBER_GENE_SYMBOL_2 = "PM2";
	private static final String PATHWAY_MEMBER_GENE_SYMBOL_3 = "PM3";
	private static final String GENE_DEPENDENCY_GENE_SYMBOL_1 = "GD1";
	private static final String GENE_DEPENDENCY_GENE_SYMBOL_2 = "GD2";
	private static final String GENE_DEPENDENCY_GENE_SYMBOL_3 = "GD3";
	private static final String DIRECT_GENE_SYMBOL_1 = "DIRECT GENE 1";
	private static final String DIRECT_GENE_SYMBOL_2 = "DIRECT GENE 2";
	private static final String WITH_PATHWAY_MEMBER_SYMBOL_1 = "GENE WITH INDIRECT 1";
	private static final String WITH_PATHWAY_MEMBER_SYMBOL_2 = "GENE WITH INDIRECT 2";
	private static final String WITH_GENE_DEPENDENCY_GENE_SYMBOL_1 = "GENE WITH GENE DEPENDENCY 1";
	private static final String WITH_GENE_DEPENDENCY_GENE_SYMBOL_2 = "GENE WITH GENE DEPENDENCY 2";
	private static final String WITH_PM_AND_GD_GENE_SYMBOL_2 = "GENE WITH PM AND GD 2";
	private static final String WITH_PM_AND_GD_GENE_SYMBOL_3 = "GENE WITH PM AND GD 3";

	private GeneDrugDataset() {}
	
	public static Map<String, SourceInformation> sourceInfos() {
		return Stream.of(
			new SourceInformation("Source 1", "S1", "http://source1.org", false),
			new SourceInformation("Source 2", "S2", "http://source2.org", false),
			new SourceInformation("Source 3", "S3", "http://source3.org", false),
			new SourceInformation("Source 4", "S4", "http://source4.org", false)
		).collect(toMap(
			SourceInformation::getSource,
			Function.identity()
		));
	}
	
	public static Map<String, Gene> genes() {
		return Stream.of(
			new Gene(singleGeneSymbolDirect(), null, false, null, null, 0d, false, 0d, OncodriveRole.NONE),
			new Gene(DIRECT_GENE_SYMBOL_2, null, false, null, null, 0d, false, 0d, OncodriveRole.NONE),
			new Gene(WITH_PATHWAY_MEMBER_SYMBOL_1, null, false, null, null, 0d, false, 0d, OncodriveRole.NONE),
			new Gene(WITH_PATHWAY_MEMBER_SYMBOL_2, null, false, null, null, 0d, false, 0d, OncodriveRole.NONE),
			new Gene(WITH_GENE_DEPENDENCY_GENE_SYMBOL_1, null, false, null, null, 0d, false, 0d, OncodriveRole.NONE),
			new Gene(WITH_GENE_DEPENDENCY_GENE_SYMBOL_2, null, false, null, null, 0d, false, 0d, OncodriveRole.NONE),
			new Gene(WITH_PM_AND_GD_GENE_SYMBOL_2, null, false, null, null, 0d, false, 0d, OncodriveRole.NONE),
			new Gene(WITH_PM_AND_GD_GENE_SYMBOL_3, null, false, null, null, 0d, false, 0d, OncodriveRole.NONE)
		).collect(toMap(
			Gene::getGeneSymbol,
			identity()
		));
	}
	
	public static Drug[] drugs() {
		@SuppressWarnings("unchecked")
		final Set<DrugSource>[] sourcesOfDrugs = new Set[14];
		for (int i = 0; i < sourcesOfDrugs.length; i++) {
			sourcesOfDrugs[i] = new HashSet<>();
		}
		
		final Drug[] drugs = new Drug[] {
			new Drug(0, DRUG_NAME_1, "Show Drug 1", DrugStatus.CLINICAL_TRIALS, null, null, null, null, null, null, sourcesOfDrugs[0]),
			new Drug(1, DRUG_NAME_2, "Show Drug 2", DrugStatus.APPROVED, null, null, null, null, null, null, sourcesOfDrugs[1]),
			new Drug(2, DRUG_NAME_3, "Show Drug 3", DrugStatus.EXPERIMENTAL, null, null, null, null, null, null, sourcesOfDrugs[2]),
			new Drug(10, DRUG_NAME_10, "Show Drug 10", DrugStatus.CLINICAL_TRIALS, null, null, null, null, null, null, sourcesOfDrugs[3]),
			new Drug(11, DRUG_NAME_11, "Show Drug 11", DrugStatus.APPROVED, null, null, null, null, null, null, sourcesOfDrugs[4]),
			new Drug(12, DRUG_NAME_12, "Show Drug 12", DrugStatus.EXPERIMENTAL, null, null, null, null, null, null, sourcesOfDrugs[5]),
			new Drug(20, DRUG_NAME_20, "Show Drug 20", DrugStatus.WITHDRAWN, null, null, null, null, null, null, sourcesOfDrugs[6]),
			new Drug(21, DRUG_NAME_21, "Show Drug 21", DrugStatus.UNDEFINED, null, null, null, null, null, null, sourcesOfDrugs[7]),
			new Drug(30, DRUG_NAME_30, "Show Drug 30", DrugStatus.CLINICAL_TRIALS, null, null, null, null, null, null, sourcesOfDrugs[8]),
			new Drug(31, DRUG_NAME_31, "Show Drug 31", DrugStatus.APPROVED, null, null, null, null, null, null, sourcesOfDrugs[9]),
			new Drug(32, DRUG_NAME_32, "Show Drug 32", DrugStatus.EXPERIMENTAL, null, null, null, null, null, null, sourcesOfDrugs[10]),
			new Drug(40, DRUG_NAME_40, "Show Drug 40", DrugStatus.CLINICAL_TRIALS, null, null, null, null, null, null, sourcesOfDrugs[11]),
			new Drug(41, DRUG_NAME_41, "Show Drug 41", DrugStatus.APPROVED, null, null, null, null, null, null, sourcesOfDrugs[12]),
			new Drug(42, DRUG_NAME_42, "Show Drug 42", DrugStatus.EXPERIMENTAL, null, null, null, null, null, null, sourcesOfDrugs[13])
		};
		
		final DrugSource[] drugSources = drugSources(drugs);
		
		for (int i = 0; i < sourcesOfDrugs.length; i++) {
			sourcesOfDrugs[i].add(drugSources[i]);
		}
		
		return drugs;
	}
	
	public static Drug drug(String standardName) {
		return findDrug(standardName, drugs());
	}
	
	private static Drug findDrug(String standardName, Drug[] drugs) {
		return stream(drugs)
			.filter(drug -> drug.getStandardName().equals(standardName))
			.findFirst()
		.orElseThrow(() -> new IllegalArgumentException("Unknown drug with standard name: " + standardName));
	}
	
	public static GeneDrug[] geneDrugs() {
		final Comparator<GeneDrug> comparator =
			(gd1, gd2) -> Compare.objects(gd1, gd2)
				.by(GeneDrug::getGeneSymbol)
				.thenBy(GeneDrug::getDrugId)
			.andGet();
		
		return concat(
			stream(geneDrugsWithInactiveDrugs()),
			stream(geneDrugsWithActiveDrugStatus())
		)
			.sorted(comparator)
		.toArray(GeneDrug[]::new);
	}
	
	public static GeneDrug[] geneDrugsWithDrug(String ... drugNames) {
		final Set<String> upperDrugNames = stream(toUpperCase(drugNames)).collect(toSet());
		
		return stream(geneDrugsWithActiveDrugStatus())
			.filter(gd -> upperDrugNames.contains(gd.getStandardDrugName()))
		.toArray(GeneDrug[]::new);
	}
	
	public static String[] listGeneSymbols(String queryFilter, int maxResults) {
		final String query = queryFilter.toUpperCase();
		final int limit = maxResults <= 0 ? Integer.MAX_VALUE : maxResults;
		
		return stream(geneDrugsWithActiveDrugStatus())
			.map(GeneDrug::getGeneSymbol)
			.filter(gene -> gene.startsWith(query))
			.distinct()
			.sorted()
			.limit(limit)
		.toArray(String[]::new);
	}
	
	public static Drug[] listDrugs(String queryFilter, int maxResults) {
		final String query = queryFilter.toUpperCase();
		final int limit = maxResults <= 0 ? Integer.MAX_VALUE : maxResults;
		
		return stream(geneDrugsWithActiveDrugStatus())
			.map(GeneDrug::getDrug)
			.filter(gd -> 
				gd.getStandardName().startsWith(query) ||
				gd.getShowName().startsWith(query) ||
				gd.getCuratedDrugSourceNames().stream().anyMatch(dsn -> dsn.startsWith(query))
			)
			.distinct()
			.sorted((d1, d2) -> d1.getShowName().compareTo(d2.getShowName()))
			.limit(limit)
		.toArray(Drug[]::new);
	}
	
	private static DrugSource[] drugSources(Drug[] drugs) {
		final Map<String, SourceInformation> sourceInfos = sourceInfos();
		
		return new DrugSource[] {
			new DrugSource("Source 1", "Source Drug 1", findDrug(DRUG_NAME_1, drugs),
				sourceInfos.get("Source 1")
			),
			new DrugSource("Source 1", "Source Drug 2", findDrug(DRUG_NAME_2, drugs),
				sourceInfos.get("Source 1")
			),
			new DrugSource("Source 2", "Source Drug 3", findDrug(DRUG_NAME_3, drugs),
				sourceInfos.get("Source 2")
			),
			new DrugSource("Source 1", "Source Drug 10", findDrug(DRUG_NAME_10, drugs),
				sourceInfos.get("Source 1")
			),
			new DrugSource("Source 1", "Source Drug 11", findDrug(DRUG_NAME_11, drugs),
				sourceInfos.get("Source 1")
			),
			new DrugSource("Source 2", "Source Drug 12", findDrug(DRUG_NAME_12, drugs),
				sourceInfos.get("Source 2")
			),
			new DrugSource("Source 2", "Source Drug 20", findDrug(DRUG_NAME_20, drugs),
				sourceInfos.get("Source 2")
			),
			new DrugSource("Source 2", "Source Drug 21", findDrug(DRUG_NAME_21, drugs),
				sourceInfos.get("Source 2")
			),
			new DrugSource("Source 3", "Source Drug 30", findDrug(DRUG_NAME_30, drugs),
				sourceInfos.get("Source 3")
			),
			new DrugSource("Source 3", "Source Drug 31", findDrug(DRUG_NAME_31, drugs),
				sourceInfos.get("Source 3")
			),
			new DrugSource("Source 3", "Source Drug 32", findDrug(DRUG_NAME_32, drugs),
				sourceInfos.get("Source 3")
			),
			new DrugSource("Source 4", "Source Drug 40", findDrug(DRUG_NAME_40, drugs),
				sourceInfos.get("Source 4")
			),
			new DrugSource("Source 4", "Source Drug 41", findDrug(DRUG_NAME_41, drugs),
				sourceInfos.get("Source 4")
			),
			new DrugSource("Source 4", "Source Drug 42", findDrug(DRUG_NAME_42, drugs),
				sourceInfos.get("Source 4")
			)
		};
	}
	
	public static GeneDrug[] geneDrugsWithActiveDrugStatus() {
		return Stream.of(
			multipleGeneDrugDirect(),
			multipleGeneDrugOnlyPathwayMember(),
			multipleGeneDrugOnlyGeneDependency(),
			multipleGeneDrugWithBothIndirect()
		)
			.flatMap(Stream::of)
		.toArray(GeneDrug[]::new);
	}
	
	public static GeneDrug[] geneDrugsWithInactiveDrugs() {
		final Map<String, Gene> genes = genes();
		final Drug[] drugs = drugs();
		
		return new GeneDrug[] {
			new GeneDrug(
				genes.get(DIRECT_GENE_SYMBOL_2),
				findDrug(DRUG_NAME_20, drugs),
				true,
				ResistanceType.SENSITIVITY,
				0.5,
				emptyList(),
				emptyMap()
			),
			new GeneDrug(
				genes.get(WITH_PATHWAY_MEMBER_SYMBOL_2),
				findDrug(DRUG_NAME_21, drugs),
				true,
				ResistanceType.SENSITIVITY,
				0.5,
				emptyList(),
				emptyMap()
			)
		};
	}
	
	public static GeneDrug singleGeneDrugDirect() {
		return new GeneDrug(
			genes().get(singleGeneSymbolDirect()),
			drug(DRUG_NAME_1),
			true,
			ResistanceType.SENSITIVITY,
			0.1,
			emptyList(),
			emptyMap()
		);
	}
	
	public static GeneDrug[] multipleGeneDrugDirect() {
		final Map<String, Gene> genes = genes();
		final Drug[] drugs = drugs();
		
		return new GeneDrug[] {
			singleGeneDrugDirect(),
			new GeneDrug(
				genes.get(DIRECT_GENE_SYMBOL_2),
				findDrug(DRUG_NAME_1, drugs),
				true,
				ResistanceType.SENSITIVITY,
				0.2,
				emptyList(),
				emptyMap()
			),
			new GeneDrug(
				genes.get(DIRECT_GENE_SYMBOL_2),
				findDrug(DRUG_NAME_2, drugs),
				true,
				ResistanceType.SENSITIVITY,
				0.3,
				emptyList(),
				emptyMap()
			),
			new GeneDrug(
				genes.get(DIRECT_GENE_SYMBOL_2),
				findDrug(DRUG_NAME_3, drugs),
				true,
				ResistanceType.SENSITIVITY,
				0.4,
				emptyList(),
				emptyMap()
			)
		};
	}
	
	public static GeneDrug singleGeneDrugPathwayMember() {
		return new GeneDrug(
			genes().get(WITH_PATHWAY_MEMBER_SYMBOL_1),
			drug(DRUG_NAME_10),
			true,
			ResistanceType.SENSITIVITY,
			0.1,
			asList(new Gene(singleGeneSymbolPathwayMember())),
			emptyMap()
		);
	}
	
	private static GeneDrug[] multipleGeneDrugOnlyPathwayMember() {
		final Map<String, Gene> genes = genes();
		final Drug[] drugs = drugs();
		
		return new GeneDrug[] {
			singleGeneDrugPathwayMember(),
			new GeneDrug(
				genes.get(WITH_PATHWAY_MEMBER_SYMBOL_2),
				findDrug(DRUG_NAME_10, drugs),
				true,
				ResistanceType.SENSITIVITY,
				0.2,
				asList(new Gene(PATHWAY_MEMBER_GENE_SYMBOL_2)),
				emptyMap()
			),
			new GeneDrug(
				genes.get(WITH_PATHWAY_MEMBER_SYMBOL_2),
				findDrug(DRUG_NAME_11, drugs),
				true,
				ResistanceType.SENSITIVITY,
				0.3,
				asList(new Gene(PATHWAY_MEMBER_GENE_SYMBOL_2)),
				emptyMap()
			),
			new GeneDrug(
				genes.get(WITH_PATHWAY_MEMBER_SYMBOL_2),
				findDrug(DRUG_NAME_12, drugs),
				true,
				ResistanceType.SENSITIVITY,
				0.4,
				asList(new Gene(PATHWAY_MEMBER_GENE_SYMBOL_2)),
				emptyMap()
			)
		};
	}
	
	public static GeneDrug[] multipleGeneDrugPathwayMember() {
		return Stream.concat(stream(multipleGeneDrugOnlyPathwayMember()), stream(multipleGeneDrugWithBothIndirect()))
			.toArray(GeneDrug[]::new);
	}
	
	public static GeneDrug singleGeneDrugGeneDependency() {
		return new GeneDrug(
			genes().get(WITH_GENE_DEPENDENCY_GENE_SYMBOL_1),
			drug(DRUG_NAME_30),
			true,
			ResistanceType.SENSITIVITY,
			0.1,
			emptyList(),
			singletonMap(new Gene(singleGeneSymbolGeneDependency()), "GoF")
		);
	}
	
	private static GeneDrug[] multipleGeneDrugOnlyGeneDependency() {
		final Map<String, Gene> genes = genes();
		final Drug[] drugs = drugs();
		
		return new GeneDrug[] {
			singleGeneDrugGeneDependency(),
			new GeneDrug(
				genes.get(WITH_GENE_DEPENDENCY_GENE_SYMBOL_2),
				findDrug(DRUG_NAME_30, drugs),
				true,
				ResistanceType.SENSITIVITY,
				0.2,
				emptyList(),
				singletonMap(new Gene(GENE_DEPENDENCY_GENE_SYMBOL_2), "LoF")
			),
			new GeneDrug(
				genes.get(WITH_GENE_DEPENDENCY_GENE_SYMBOL_2),
				findDrug(DRUG_NAME_31, drugs),
				true,
				ResistanceType.SENSITIVITY,
				0.3,
				emptyList(),
				singletonMap(new Gene(GENE_DEPENDENCY_GENE_SYMBOL_2), "GoF")
			),
			new GeneDrug(
				genes.get(WITH_GENE_DEPENDENCY_GENE_SYMBOL_2),
				findDrug(DRUG_NAME_32, drugs),
				true,
				ResistanceType.SENSITIVITY,
				0.4,
				emptyList(),
				singletonMap(new Gene(GENE_DEPENDENCY_GENE_SYMBOL_2), "LoF")
			)
		};
	}
	
	public static GeneDrug[] multipleGeneDrugGeneDependency() {
		return Stream.concat(stream(multipleGeneDrugOnlyGeneDependency()), stream(multipleGeneDrugWithBothIndirect()))
			.toArray(GeneDrug[]::new);
	}
	
	public static GeneDrug singleGeneDrugWithBothIndirect() {
		return new GeneDrug(
			genes().get(WITH_PM_AND_GD_GENE_SYMBOL_3),
			drug(DRUG_NAME_42),
			true,
			ResistanceType.SENSITIVITY,
			0.4,
			asList(new Gene(PATHWAY_MEMBER_GENE_SYMBOL_3)),
			singletonMap(new Gene(GENE_DEPENDENCY_GENE_SYMBOL_3), "GoF")
		);
	}
	
	public static GeneDrug[] multipleGeneDrugWithBothIndirect() {
		final Map<String, Gene> genes = genes();
		final Drug[] drugs = drugs();
		
		return new GeneDrug[] {
			singleGeneDrugWithBothIndirect(),
			new GeneDrug(
				genes.get(WITH_PM_AND_GD_GENE_SYMBOL_2),
				findDrug(DRUG_NAME_40, drugs),
				true,
				ResistanceType.SENSITIVITY,
				0.1,
				asList(new Gene(PATHWAY_MEMBER_GENE_SYMBOL_2)),
				singletonMap(new Gene(GENE_DEPENDENCY_GENE_SYMBOL_2), "LoF")
			),
			new GeneDrug(
				genes.get(WITH_PM_AND_GD_GENE_SYMBOL_2),
				findDrug(DRUG_NAME_41, drugs),
				true,
				ResistanceType.SENSITIVITY,
				0.2,
				asList(new Gene(PATHWAY_MEMBER_GENE_SYMBOL_2)),
				singletonMap(new Gene(GENE_DEPENDENCY_GENE_SYMBOL_2), "GoF")
			),
			new GeneDrug(
				genes.get(WITH_PM_AND_GD_GENE_SYMBOL_2),
				findDrug(DRUG_NAME_42, drugs),
				true,
				ResistanceType.SENSITIVITY,
				0.3,
				asList(new Gene(PATHWAY_MEMBER_GENE_SYMBOL_2)),
				singletonMap(new Gene(GENE_DEPENDENCY_GENE_SYMBOL_2), "LoF")
			)
		};
	}
	
	public static GeneDrug[] multipleGeneDrugMixed() {
		return Stream.of(
			multipleGeneDrugDirect(),
			multipleGeneDrugOnlyPathwayMember(),
			multipleGeneDrugOnlyGeneDependency(),
			multipleGeneDrugWithBothIndirect()
		)
			.flatMap(Stream::of)
			.distinct()
		.toArray(GeneDrug[]::new);
	}
	
	public static GeneDrugGroup[] singleGeneDrugGroupDirect() {
		return new GeneDrugGroup[] {
			new GeneDrugGroup(
				new String[] { singleGeneSymbolDirect() },
				asList(singleGeneDrugDirect()),
				emptyMap()
			)
		};
	}
	
	public static GeneDrugGroup[] multipleGeneDrugGroupMixedOnlyDirect() {
		final GeneDrug[] geneDrugs = multipleGeneDrugDirect();
		
		return new GeneDrugGroup[] {
			new GeneDrugGroup(
				new String[] { singleGeneSymbolDirect(), DIRECT_GENE_SYMBOL_2 },
				asList(
					geneDrugs[0],
					geneDrugs[1]
				),
				emptyMap()
			),
			new GeneDrugGroup(
				new String[] { DIRECT_GENE_SYMBOL_2 },
				asList(geneDrugs[2]),
				emptyMap()
			),
			new GeneDrugGroup(
				new String[] { DIRECT_GENE_SYMBOL_2 },
				asList(geneDrugs[3]),
				emptyMap()
			)
		};
	}
	
	public static GeneDrugGroup[] singleGeneDrugGroupPathwayMember() {
		return new GeneDrugGroup[] {
			new GeneDrugGroup(
				new String[] { PATHWAY_MEMBER_GENE_SYMBOL_1 },
				asList(singleGeneDrugPathwayMember()),
				emptyMap()
			)
		};
	}
	
	public static GeneDrugGroup[] multipleGeneDrugGroupPathwayMember() {
		final GeneDrug[] geneDrugs = multipleGeneDrugPathwayMember();
		
		return new GeneDrugGroup[] {
			new GeneDrugGroup(
				new String[] { singleGeneSymbolPathwayMember(), PATHWAY_MEMBER_GENE_SYMBOL_2 },
				asList(
					geneDrugs[0],
					geneDrugs[1]
				),
				emptyMap()
			),
			new GeneDrugGroup(
				new String[] { PATHWAY_MEMBER_GENE_SYMBOL_2 },
				asList(geneDrugs[2]),
				emptyMap()
			),
			new GeneDrugGroup(
				new String[] { PATHWAY_MEMBER_GENE_SYMBOL_2 },
				asList(geneDrugs[3]),
				emptyMap()
			),
			
			new GeneDrugGroup(
				new String[] {
					PATHWAY_MEMBER_GENE_SYMBOL_2,
					PATHWAY_MEMBER_GENE_SYMBOL_3
				},
				asList(
					geneDrugs[4],
					geneDrugs[7]
				),
				emptyMap()
			),
			new GeneDrugGroup(
				new String[] { PATHWAY_MEMBER_GENE_SYMBOL_2 },
				asList(geneDrugs[5]),
				emptyMap()
			),
			new GeneDrugGroup(
				new String[] { PATHWAY_MEMBER_GENE_SYMBOL_2 },
				asList(geneDrugs[6]),
				emptyMap()
			)
		};
	}
	
	public static GeneDrugGroup[] singleGeneDrugGroupGeneDependency() {
		return new GeneDrugGroup[] {
			new GeneDrugGroup(
				new String[] { GENE_DEPENDENCY_GENE_SYMBOL_1 },
				asList(singleGeneDrugGeneDependency()),
				emptyMap()
			)
		};
	}
	
	public static GeneDrugGroup[] multipleGeneDrugGroupGeneDependency() {
		final GeneDrug[] geneDrugs = multipleGeneDrugGeneDependency();
		
		return new GeneDrugGroup[] {
			new GeneDrugGroup(
				new String[] { singleGeneSymbolGeneDependency(), GENE_DEPENDENCY_GENE_SYMBOL_2 },
				asList(
					geneDrugs[0],
					geneDrugs[1]
				),
				emptyMap()
			),
			new GeneDrugGroup(
				new String[] { GENE_DEPENDENCY_GENE_SYMBOL_2 },
				asList(geneDrugs[2]),
				emptyMap()
			),
			new GeneDrugGroup(
				new String[] { GENE_DEPENDENCY_GENE_SYMBOL_2 },
				asList(geneDrugs[3]),
				emptyMap()
			),
			
			new GeneDrugGroup(
				new String[] {
					GENE_DEPENDENCY_GENE_SYMBOL_2,
					GENE_DEPENDENCY_GENE_SYMBOL_3
				},
				asList(
					geneDrugs[4],
					geneDrugs[7]
				),
				emptyMap()
			),
			new GeneDrugGroup(
				new String[] { GENE_DEPENDENCY_GENE_SYMBOL_2 },
				asList(geneDrugs[5]),
				emptyMap()
			),
			new GeneDrugGroup(
				new String[] { GENE_DEPENDENCY_GENE_SYMBOL_2 },
				asList(geneDrugs[6]),
				emptyMap()
			)
		};
	}
	
	public static GeneDrugGroup[] multipleGeneDrugGroupMixedOnlyPathwayMember() {
		final GeneDrug[] geneDrugs = multipleGeneDrugMixed();
		
		return new GeneDrugGroup[] {
			new GeneDrugGroup(
				new String[] { singleGeneSymbolPathwayMember(), PATHWAY_MEMBER_GENE_SYMBOL_2 },
				asList(
					geneDrugs[4],
					geneDrugs[5]
				),
				emptyMap()
			),
			new GeneDrugGroup(
				new String[] { PATHWAY_MEMBER_GENE_SYMBOL_2 },
				asList(geneDrugs[6]),
				emptyMap()
			),
			new GeneDrugGroup(
				new String[] { PATHWAY_MEMBER_GENE_SYMBOL_2 },
				asList(geneDrugs[7]),
				emptyMap()
			),
			
			new GeneDrugGroup(
				new String[] {
					PATHWAY_MEMBER_GENE_SYMBOL_2,
					PATHWAY_MEMBER_GENE_SYMBOL_3
				},
				asList(
					geneDrugs[12],
					geneDrugs[15]
				),
				emptyMap()
			),
			new GeneDrugGroup(
				new String[] { PATHWAY_MEMBER_GENE_SYMBOL_2 },
				asList(geneDrugs[13]),
				emptyMap()
			),
			new GeneDrugGroup(
				new String[] { PATHWAY_MEMBER_GENE_SYMBOL_2 },
				asList(geneDrugs[14]),
				emptyMap()
			)
		};
	}
	
	public static GeneDrugGroup[] multipleGeneDrugGroupMixedOnlyGeneDependency() {
		final GeneDrug[] geneDrugs = multipleGeneDrugMixed();
		
		return new GeneDrugGroup[] {
			new GeneDrugGroup(
				new String[] { singleGeneSymbolGeneDependency(), GENE_DEPENDENCY_GENE_SYMBOL_2 },
				asList(
					geneDrugs[8],
					geneDrugs[9]
				),
				emptyMap()
			),
			new GeneDrugGroup(
				new String[] { GENE_DEPENDENCY_GENE_SYMBOL_2 },
				asList(geneDrugs[10]),
				emptyMap()
			),
			new GeneDrugGroup(
				new String[] { GENE_DEPENDENCY_GENE_SYMBOL_2 },
				asList(geneDrugs[11]),
				emptyMap()
			),
			
			new GeneDrugGroup(
				new String[] {
					GENE_DEPENDENCY_GENE_SYMBOL_2,
					GENE_DEPENDENCY_GENE_SYMBOL_3
				},
				asList(
					geneDrugs[12],
					geneDrugs[15]
				),
				emptyMap()
			),
			new GeneDrugGroup(
				new String[] { GENE_DEPENDENCY_GENE_SYMBOL_2 },
				asList(geneDrugs[13]),
				emptyMap()
			),
			new GeneDrugGroup(
				new String[] { GENE_DEPENDENCY_GENE_SYMBOL_2 },
				asList(geneDrugs[14]),
				emptyMap()
			)
		};
	}
	
	public static GeneDrugGroup[] multipleGeneDrugGroupMixedOnlyIndirect() {
		final GeneDrug[] geneDrugs = multipleGeneDrugMixed();
		
		return new GeneDrugGroup[] {
			new GeneDrugGroup(
				new String[] { singleGeneSymbolPathwayMember(), PATHWAY_MEMBER_GENE_SYMBOL_2 },
				asList(
					geneDrugs[4],
					geneDrugs[5]
				),
				emptyMap()
			),
			new GeneDrugGroup(
				new String[] { PATHWAY_MEMBER_GENE_SYMBOL_2 },
				asList(geneDrugs[6]),
				emptyMap()
			),
			new GeneDrugGroup(
				new String[] { PATHWAY_MEMBER_GENE_SYMBOL_2 },
				asList(geneDrugs[7]),
				emptyMap()
			),
			
			new GeneDrugGroup(
				new String[] { singleGeneSymbolGeneDependency(), GENE_DEPENDENCY_GENE_SYMBOL_2 },
				asList(
					geneDrugs[8],
					geneDrugs[9]
				),
				emptyMap()
			),
			new GeneDrugGroup(
				new String[] { GENE_DEPENDENCY_GENE_SYMBOL_2 },
				asList(geneDrugs[10]),
				emptyMap()
			),
			new GeneDrugGroup(
				new String[] { GENE_DEPENDENCY_GENE_SYMBOL_2 },
				asList(geneDrugs[11]),
				emptyMap()
			),
			
			new GeneDrugGroup(
				new String[] {
					GENE_DEPENDENCY_GENE_SYMBOL_2,
					GENE_DEPENDENCY_GENE_SYMBOL_3,
					PATHWAY_MEMBER_GENE_SYMBOL_2,
					PATHWAY_MEMBER_GENE_SYMBOL_3
				},
				asList(
					geneDrugs[12],
					geneDrugs[15]
				),
				emptyMap()
			),
			new GeneDrugGroup(
				new String[] { GENE_DEPENDENCY_GENE_SYMBOL_2, PATHWAY_MEMBER_GENE_SYMBOL_2 },
				asList(geneDrugs[13]),
				emptyMap()
			),
			new GeneDrugGroup(
				new String[] { GENE_DEPENDENCY_GENE_SYMBOL_2, PATHWAY_MEMBER_GENE_SYMBOL_2 },
				asList(geneDrugs[14]),
				emptyMap()
			)
		};
	}
	
	public static GeneDrugGroup[] multipleGeneDrugGroupMixed() {
		final GeneDrug[] geneDrugs = multipleGeneDrugMixed();
		
		return new GeneDrugGroup[] {
			new GeneDrugGroup(
				new String[] { singleGeneSymbolDirect(), DIRECT_GENE_SYMBOL_2 },
				asList(
					geneDrugs[0],
					geneDrugs[1]
				),
				emptyMap()
			),
			new GeneDrugGroup(
				new String[] { DIRECT_GENE_SYMBOL_2 },
				asList(geneDrugs[2]),
				emptyMap()
			),
			new GeneDrugGroup(
				new String[] { DIRECT_GENE_SYMBOL_2 },
				asList(geneDrugs[3]),
				emptyMap()
			),
			
			new GeneDrugGroup(
				new String[] { singleGeneSymbolPathwayMember(), PATHWAY_MEMBER_GENE_SYMBOL_2 },
				asList(
					geneDrugs[4],
					geneDrugs[5]
				),
				emptyMap()
			),
			new GeneDrugGroup(
				new String[] { PATHWAY_MEMBER_GENE_SYMBOL_2 },
				asList(geneDrugs[6]),
				emptyMap()
			),
			new GeneDrugGroup(
				new String[] { PATHWAY_MEMBER_GENE_SYMBOL_2 },
				asList(geneDrugs[7]),
				emptyMap()
			),
			
			new GeneDrugGroup(
				new String[] { singleGeneSymbolGeneDependency(), GENE_DEPENDENCY_GENE_SYMBOL_2 },
				asList(
					geneDrugs[8],
					geneDrugs[9]
				),
				emptyMap()
			),
			new GeneDrugGroup(
				new String[] { GENE_DEPENDENCY_GENE_SYMBOL_2 },
				asList(geneDrugs[10]),
				emptyMap()
			),
			new GeneDrugGroup(
				new String[] { GENE_DEPENDENCY_GENE_SYMBOL_2 },
				asList(geneDrugs[11]),
				emptyMap()
			),
			
			new GeneDrugGroup(
				new String[] {
					GENE_DEPENDENCY_GENE_SYMBOL_2,
					GENE_DEPENDENCY_GENE_SYMBOL_3,
					PATHWAY_MEMBER_GENE_SYMBOL_2,
					PATHWAY_MEMBER_GENE_SYMBOL_3
				},
				asList(
					geneDrugs[12],
					geneDrugs[15]
				),
				emptyMap()
			),
			new GeneDrugGroup(
				new String[] { GENE_DEPENDENCY_GENE_SYMBOL_2, PATHWAY_MEMBER_GENE_SYMBOL_2 },
				asList(geneDrugs[13]),
				emptyMap()
			),
			new GeneDrugGroup(
				new String[] { GENE_DEPENDENCY_GENE_SYMBOL_2, PATHWAY_MEMBER_GENE_SYMBOL_2 },
				asList(geneDrugs[14]),
				emptyMap()
			)
		};
	}
	
	public static GeneDrugGroupInfos singleGeneDrugGroupInfosDirect() {
		return new GeneDrugGroupInfos(asList(singleGeneDrugGroupDirect()));
	}
	
	public static GeneDrugGroupInfos multipleGeneDrugGroupInfosDirect() {
		return new GeneDrugGroupInfos(asList(multipleGeneDrugGroupMixedOnlyDirect()));
	}
	
	public static GeneDrugGroupInfos singleGeneDrugGroupInfosPathwayMember() {
		return new GeneDrugGroupInfos(asList(singleGeneDrugGroupPathwayMember()));
	}
	
	public static GeneDrugGroupInfos multipleGeneDrugGroupInfosPathwayMember() {
		return new GeneDrugGroupInfos(asList(multipleGeneDrugGroupPathwayMember()));
	}
	
	public static GeneDrugGroupInfos singleGeneDrugGroupInfosGeneDependency() {
		return new GeneDrugGroupInfos(asList(singleGeneDrugGroupGeneDependency()));
	}
	
	public static GeneDrugGroupInfos multipleGeneDrugGroupInfosGeneDependency() {
		return new GeneDrugGroupInfos(asList(multipleGeneDrugGroupGeneDependency()));
	}
	
	public static GeneDrugGroupInfos multipleGeneDrugGroupInfosIndirect() {
		return new GeneDrugGroupInfos(asList(multipleGeneDrugGroupMixedOnlyIndirect()));
	}
	
	public static GeneDrugGroupInfos multipleGeneDrugGroupInfosMixed() {
		return new GeneDrugGroupInfos(asList(multipleGeneDrugGroupMixed()));
	}
	
	public static GeneDrugGroupInfos multipleGeneDrugGroupInfosMixedOnlyDirect() {
		return new GeneDrugGroupInfos(asList(multipleGeneDrugGroupMixedOnlyDirect()));
	}
	
	public static GeneDrugGroupInfos multipleGeneDrugGroupInfosMixedOnlyIndirect() {
		return new GeneDrugGroupInfos(asList(multipleGeneDrugGroupMixedOnlyIndirect()));
	}
	
	public static String absentGeneSymbol() {
		return ABSENT_GENE_SYMBOL_1;
	}
	
	public static String[] absentGeneSymbols() {
		return new String[] {
			ABSENT_GENE_SYMBOL_1,
			ABSENT_GENE_SYMBOL_2,
			ABSENT_GENE_SYMBOL_3,
			PATHWAY_MEMBER_GENE_SYMBOL_2
		};
	}
	
	public static String[] presentGeneSymbols() {
		return new String[] {
			DIRECT_GENE_SYMBOL_1,
			WITH_PATHWAY_MEMBER_SYMBOL_1,
			WITH_PATHWAY_MEMBER_SYMBOL_2
		};
	}
	
	public static Map<String, Boolean> geneSymbolsWithPresence() {
		final Map<String, Boolean> genePresence = new HashMap<>();
		
		for (String geneSymbol : absentGeneSymbols()) {
			genePresence.put(geneSymbol, false);
		}
		
		for (String geneSymbol : presentGeneSymbols()) {
			genePresence.put(geneSymbol, true);
		}
		
		return genePresence;
	}
	
	public static String singleGeneSymbolDirect() {
		return DIRECT_GENE_SYMBOL_1;
	}

	public static String[] multipleGeneSymbolsDirect() {
		return new String[] { singleGeneSymbolDirect(), DIRECT_GENE_SYMBOL_2 };
	}

	public static String singleGeneSymbolPathwayMember() {
		return PATHWAY_MEMBER_GENE_SYMBOL_1;
	}

	public static String[] multipleGeneSymbolsPathwayMember() {
		return new String[] { singleGeneSymbolPathwayMember(), PATHWAY_MEMBER_GENE_SYMBOL_2, PATHWAY_MEMBER_GENE_SYMBOL_3 };
	}
	
	public static String singleGeneSymbolGeneDependency() {
		return GENE_DEPENDENCY_GENE_SYMBOL_1;
	}
	
	public static String[] multipleGeneSymbolsGeneDependency() {
		return new String[] { singleGeneSymbolGeneDependency(), GENE_DEPENDENCY_GENE_SYMBOL_2, GENE_DEPENDENCY_GENE_SYMBOL_3 };
	}
	
	public static String[] multipleGeneSymbolsWithBothIndirect() {
		return new String[] {
			GENE_DEPENDENCY_GENE_SYMBOL_2,
			GENE_DEPENDENCY_GENE_SYMBOL_3,
			PATHWAY_MEMBER_GENE_SYMBOL_2,
			PATHWAY_MEMBER_GENE_SYMBOL_3
		};
	}
	
	public static String[] multipleGeneSymbolsIndirect() {
		return Stream.of(
			multipleGeneSymbolsPathwayMember(),
			multipleGeneSymbolsGeneDependency(),
			multipleGeneSymbolsWithBothIndirect()
		)
			.flatMap(Stream::of)
			.distinct()
		.toArray(String[]::new);
	}
	
	public static String[] multipleGeneSymbolsMixed() {
		return Stream.of(
			multipleGeneSymbolsDirect(),
			multipleGeneSymbolsPathwayMember(),
			multipleGeneSymbolsGeneDependency(),
			multipleGeneSymbolsWithBothIndirect()
		)
			.flatMap(Stream::of)
			.distinct()
		.toArray(String[]::new);
	}

	public static String absentDrugName() {
		return ABSENT_DRUG_NAME;
	}

	public static String[] inactiveDrugNames() {
		return new String[] { DRUG_NAME_20, DRUG_NAME_21 };
	}
	
	public static String singleDrugName() {
		return DRUG_NAME_1;
	}
	
	public static String[] multipleDrugNames() {
		return new String[] {
			DRUG_NAME_1,
			DRUG_NAME_10,
			DRUG_NAME_11,
			DRUG_NAME_12,
			DRUG_NAME_2,
			DRUG_NAME_20,
			DRUG_NAME_21,
			DRUG_NAME_3,
			DRUG_NAME_30,
			DRUG_NAME_31,
			DRUG_NAME_32,
			DRUG_NAME_40,
			DRUG_NAME_41,
			DRUG_NAME_42
		};
	}
	
	public static GeneDrugGroup[] singleGeneDrugGroupByDrug() {
		final GeneDrug[] geneDrugs = geneDrugs();
		
		return new GeneDrugGroup[] {
			new GeneDrugGroup(
				new String[] { DIRECT_GENE_SYMBOL_1, DIRECT_GENE_SYMBOL_2 },
				asList(geneDrugs[0], geneDrugs[1]),
				emptyMap()
			)
		};
	}
	
	public static GeneDrugGroup[] multipleGeneDrugGroupsByDrugs() {
		final GeneDrug[] geneDrugs = geneDrugs();
		
		final Function<Drug, String[]> listGeneSymbols = drug -> stream(geneDrugs)
			.filter(gd -> gd.getDrug().equals(drug))
			.map(GeneDrug::getGeneSymbol)
			.distinct()
		.toArray(String[]::new);
		final Function<Drug, Collection<GeneDrug>> listGeneDrugs = drug -> stream(geneDrugs)
			.filter(gd -> gd.getDrug().equals(drug))
			.distinct()
		.collect(toList());
		
		return stream(geneDrugs)
			.map(GeneDrug::getDrug)
			.filter(Drug::hasActiveStatus)
			.distinct()
			.map(drug -> new GeneDrugGroup(
				listGeneSymbols.apply(drug),
				listGeneDrugs.apply(drug),
				emptyMap()
			))
		.toArray(GeneDrugGroup[]::new);
	}
	
	public static GeneDrugGroupInfos singleDrugGeneDrugGroupsInfos() {
		return new GeneDrugGroupInfos(asList(singleGeneDrugGroupByDrug()));
	}
	
	public static GeneDrugGroupInfos multipleDrugGeneDrugGroupsInfosMixed() {
		return new GeneDrugGroupInfos(asList(multipleGeneDrugGroupsByDrugs()));
	}
	
	public static GeneDrugGroupInfos emptyGeneDrugGroupInfo() {
		return new GeneDrugGroupInfos(emptyList());
	}
	
	public static GeneRanking rankingFor(String ... geneSymbols) {
		final AtomicInteger rank = new AtomicInteger(1);
		
		return new GeneRanking(stream(geneSymbols)
			.sequential()
			.collect(toMap(
				Function.identity(),
				gs -> (double) rank.getAndIncrement()
			))
		);
	}
}
