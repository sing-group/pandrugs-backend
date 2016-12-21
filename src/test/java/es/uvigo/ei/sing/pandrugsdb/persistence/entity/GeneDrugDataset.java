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

import static es.uvigo.ei.sing.pandrugsdb.util.StringFormatter.toUpperCase;
import static java.util.Arrays.asList;
import static java.util.Arrays.stream;
import static java.util.Collections.emptyList;
import static java.util.stream.Collectors.toMap;
import static java.util.stream.Collectors.toSet;
import static java.util.stream.Stream.concat;

import java.util.Comparator;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Function;
import java.util.stream.Stream;

import es.uvigo.ei.sing.pandrugsdb.controller.entity.GeneDrugGroup;
import es.uvigo.ei.sing.pandrugsdb.service.entity.GeneDrugGroupInfos;
import es.uvigo.ei.sing.pandrugsdb.service.entity.GeneRanking;
import es.uvigo.ei.sing.pandrugsdb.util.Compare;

public final class GeneDrugDataset {
	private static final String ABSENT_DRUG_NAME = "ABSENT DRUG";
	private static final String DRUG_NAME_1 = "DRUG 1";
	private static final String DRUG_NAME_2 = "DRUG 2";
	private static final String DRUG_NAME_3 = "DRUG 3";
	private static final String DRUG_NAME_10 = "DRUG 10";
	private static final String DRUG_NAME_11 = "DRUG 11";
	private static final String DRUG_NAME_12 = "DRUG 12";
	private static final String DRUG_NAME_21 = "DRUG 21";
	private static final String DRUG_NAME_22 = "DRUG 22";
	private static final String ABSENT_GENE_SYMBOL = "ABSENT GENE";
	private static final String INDIRECT_GENE_SYMBOL_1 = "IG1";
	private static final String INDIRECT_GENE_SYMBOL_2 = "IG2";
	private static final String DIRECT_GENE_SYMBOL_1 = "DIRECT GENE 1";
	private static final String DIRECT_GENE_SYMBOL_2 = "DIRECT GENE 2";
	private static final String WITH_INDIRECT_GENE_SYMBOL_1 = "GENE WITH INDIRECT 1";
	private static final String WITH_INDIRECT_GENE_SYMBOL_2 = "GENE WITH INDIRECT 2";

	private GeneDrugDataset() {}
	
	public static Map<String, SourceInformation> sourceInfos() {
		return Stream.of(
			new SourceInformation("Source 1", "S1", "http://source1.org", false),
			new SourceInformation("Source 2", "S2", "http://source2.org", false)
		).collect(toMap(
			SourceInformation::getSource,
			Function.identity()
		));
	}
	
	public static Map<String, Gene> genes() {
		return Stream.of(
			new Gene(singleGeneSymbolDirect(), null, false, null, 0d, false, OncodriveRole.NONE),
			new Gene(DIRECT_GENE_SYMBOL_2, null, false, null, 0d, false, OncodriveRole.NONE),
			new Gene(WITH_INDIRECT_GENE_SYMBOL_1, null, false, null, 0d, false, OncodriveRole.NONE),
			new Gene(WITH_INDIRECT_GENE_SYMBOL_2, null, false, null, 0d, false, OncodriveRole.NONE)
		).collect(toMap(
			Gene::getGeneSymbol,
			Function.identity()
		));
	}
	
	public static Drug[] drugs() {
		return new Drug[] {
			new Drug(0, DRUG_NAME_1, "Show Drug 1", DrugStatus.CLINICAL_TRIALS, null, null, null, null, null),
			new Drug(1, DRUG_NAME_2, "Show Drug 2", DrugStatus.APPROVED, null, null, null, null, null),
			new Drug(2, DRUG_NAME_3, "Show Drug 3", DrugStatus.EXPERIMENTAL, null, null, null, null, null),
			new Drug(10, DRUG_NAME_10, "Show Drug 10", DrugStatus.CLINICAL_TRIALS, null, null, null, null, null),
			new Drug(11, DRUG_NAME_11, "Show Drug 11", DrugStatus.APPROVED, null, null, null, null, null),
			new Drug(12, DRUG_NAME_12, "Show Drug 12", DrugStatus.EXPERIMENTAL, null, null, null, null, null),
			new Drug(21, DRUG_NAME_21, "Show Drug 21", DrugStatus.WITHDRAWN, null, null, null, null, null),
			new Drug(22, DRUG_NAME_22, "Show Drug 22", DrugStatus.UNDEFINED, null, null, null, null, null)
		};
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
	
	public static GeneDrug[] geneDrugsWithActiveDrugStatus() {
		return concat(
			stream(multipleGeneDirect()),
			stream(multipleGeneIndirect()))
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
	
	public static String[] listStandardDrugNames(String queryFilter, int maxResults) {
		final String query = queryFilter.toUpperCase();
		final int limit = maxResults <= 0 ? Integer.MAX_VALUE : maxResults;
		
		return stream(geneDrugsWithActiveDrugStatus())
			.map(GeneDrug::getStandardDrugName)
			.filter(gene -> gene.startsWith(query))
			.distinct()
			.sorted()
			.limit(limit)
		.toArray(String[]::new);
	}
	
	public static DrugSource[] drugSources() {
		final Map<String, SourceInformation> sourceInfos = sourceInfos();
		final Drug[] drugs = drugs();
		
		return new DrugSource[] {
			new DrugSource("Source 1", "Source Drug 1", drugs[0],
				sourceInfos.get("Source 1")
			),
			new DrugSource("Source 1", "Source Drug 2", drugs[1],
				sourceInfos.get("Source 1")
			),
			new DrugSource("Source 2", "Source Drug 3", drugs[2],
				sourceInfos.get("Source 2")
			),
			new DrugSource("Source 1", "Source Drug 10", drugs[3],
				sourceInfos.get("Source 1")
			),
			new DrugSource("Source 1", "Source Drug 11", drugs[4],
				sourceInfos.get("Source 1")
			),
			new DrugSource("Source 2", "Source Drug 12", drugs[5],
				sourceInfos.get("Source 2")
			),
			new DrugSource("Source 2", "Source Drug 20", drugs[6],
				sourceInfos.get("Source 2")
			),
			new DrugSource("Source 2", "Source Drug 21", drugs[7],
				sourceInfos.get("Source 2")
			)
		};
	}
	
	public static GeneDrug[] geneDrugsWithInactiveDrugs() {
		final Map<String, Gene> genes = genes();
		final Drug[] drugs = drugs();
		
		return new GeneDrug[] {
			new GeneDrug(
				genes.get(DIRECT_GENE_SYMBOL_2),
				drugs[6],
				true,
				null,
				null,
				ResistanceType.SENSITIVITY,
				0.5,
				emptyList(),
				asList(drugSources()[0])
			),
			new GeneDrug(
				genes.get(WITH_INDIRECT_GENE_SYMBOL_2),
				drugs[7],
				true,
				null,
				null,
				ResistanceType.SENSITIVITY,
				0.5,
				emptyList(),
				asList(drugSources()[1])
			)
		};
	}
	
	public static GeneDrug singleGeneDrugDirect() {
		return new GeneDrug(
			genes().get(singleGeneSymbolDirect()),
			drugs()[0],
			true,
			null,
			null,
			ResistanceType.SENSITIVITY,
			0.1,
			emptyList(),
			asList(drugSources()[0])
		);
	}
	
	public static GeneDrug[] multipleGeneDirect() {
		final Map<String, Gene> genes = genes();
		final Drug[] drugs = drugs();
		
		return new GeneDrug[] {
			singleGeneDrugDirect(),
			new GeneDrug(
				genes.get(DIRECT_GENE_SYMBOL_2),
				drugs[0],
				true,
				null,
				null,
				ResistanceType.SENSITIVITY,
				0.2,
				emptyList(),
				asList(drugSources()[0])
			),
			new GeneDrug(
				genes.get(DIRECT_GENE_SYMBOL_2),
				drugs[1],
				true,
				null,
				null,
				ResistanceType.SENSITIVITY,
				0.3,
				emptyList(),
				asList(drugSources()[1])
			),
			new GeneDrug(
				genes.get(DIRECT_GENE_SYMBOL_2),
				drugs[2],
				true,
				null,
				null,
				ResistanceType.SENSITIVITY,
				0.4,
				emptyList(),
				asList(drugSources()[2])
			)
		};
	}
	
	public static GeneDrug singleGeneIndirect() {
		return new GeneDrug(
			genes().get(WITH_INDIRECT_GENE_SYMBOL_1),
			drugs()[3],
			true,
			null,
			null,
			ResistanceType.SENSITIVITY,
			0.1,
			asList(singleGeneSymbolIndirect()),
			asList(drugSources()[3])
		);
	}
	
	public static GeneDrug[] multipleGeneIndirect() {
		final Map<String, Gene> genes = genes();
		final Drug[] drugs = drugs();
		
		return new GeneDrug[] {
			singleGeneIndirect(),
			new GeneDrug(
				genes.get(WITH_INDIRECT_GENE_SYMBOL_2),
				drugs[3],
				true,
				null,
				null,
				ResistanceType.SENSITIVITY,
				0.2,
				asList(INDIRECT_GENE_SYMBOL_2),
				asList(drugSources()[3])
			),
			new GeneDrug(
				genes.get(WITH_INDIRECT_GENE_SYMBOL_2),
				drugs[4],
				true,
				null,
				null,
				ResistanceType.SENSITIVITY,
				0.3,
				asList(INDIRECT_GENE_SYMBOL_2),
				asList(drugSources()[4])
			),
			new GeneDrug(
				genes.get(WITH_INDIRECT_GENE_SYMBOL_2),
				drugs[5],
				true,
				null,
				null,
				ResistanceType.SENSITIVITY,
				0.4,
				asList(INDIRECT_GENE_SYMBOL_2),
				asList(drugSources()[5])
			)
		};
	}
	
	public static GeneDrug[] multipleGeneMixed() {
		return Stream.of(multipleGeneDirect(), multipleGeneIndirect())
			.flatMap(Stream::of)
		.toArray(GeneDrug[]::new);
	}
	
	public static GeneDrugGroup[] singleGeneGroupDirect() {
		return new GeneDrugGroup[] {
			new GeneDrugGroup(
				new String[] { singleGeneSymbolDirect() },
				asList(singleGeneDrugDirect())
			)
		};
	}
	
	public static GeneDrugGroup[] multipleGeneGroupDirect() {
		final GeneDrug[] multipleGeneDirect = multipleGeneDirect();
		
		return new GeneDrugGroup[] {
			new GeneDrugGroup(
				new String[] { singleGeneSymbolDirect(), DIRECT_GENE_SYMBOL_2 },
				asList(
					multipleGeneDirect[0],
					multipleGeneDirect[1]
				)
			),
			new GeneDrugGroup(
				new String[] { DIRECT_GENE_SYMBOL_2 },
				asList(multipleGeneDirect[2])
			),
			new GeneDrugGroup(
				new String[] { DIRECT_GENE_SYMBOL_2 },
				asList(multipleGeneDirect[3])
			)
		};
	}
	
	public static GeneDrugGroup[] singleGeneGroupIndirect() {
		return new GeneDrugGroup[] {
			new GeneDrugGroup(
				new String[] { INDIRECT_GENE_SYMBOL_1 },
				asList(singleGeneIndirect())
			)
		};
	}
	
	public static GeneDrugGroup[] multipleGeneGroupIndirect() {
		final GeneDrug[] multipleGeneIndirect = multipleGeneIndirect();
		
		return new GeneDrugGroup[] {
			new GeneDrugGroup(
				new String[] { singleGeneSymbolIndirect(), INDIRECT_GENE_SYMBOL_2 },
				asList(
					multipleGeneIndirect[0],
					multipleGeneIndirect[1]
				)
			),
			new GeneDrugGroup(
				new String[] { INDIRECT_GENE_SYMBOL_2 },
				asList(multipleGeneIndirect[2])
			),
			new GeneDrugGroup(
				new String[] { INDIRECT_GENE_SYMBOL_2 },
				asList(multipleGeneIndirect[3])
			)
		};
	}
	
	public static GeneDrugGroup[] multipleGeneGroupMixed() {
		final GeneDrug[] multipleGeneMixed = multipleGeneMixed();
		
		return new GeneDrugGroup[] {
			new GeneDrugGroup(
				new String[] { singleGeneSymbolDirect(), DIRECT_GENE_SYMBOL_2 },
				asList(
					multipleGeneMixed[0],
					multipleGeneMixed[1]
				)
			),
			new GeneDrugGroup(
				new String[] { DIRECT_GENE_SYMBOL_2 },
				asList(multipleGeneMixed[2])
			),
			new GeneDrugGroup(
				new String[] { DIRECT_GENE_SYMBOL_2 },
				asList(multipleGeneMixed[3])
			),
			new GeneDrugGroup(
				new String[] { singleGeneSymbolIndirect(), INDIRECT_GENE_SYMBOL_2 },
				asList(
					multipleGeneMixed[4],
					multipleGeneMixed[5]
				)
			),
			new GeneDrugGroup(
				new String[] { INDIRECT_GENE_SYMBOL_2 },
				asList(multipleGeneMixed[6])
			),
			new GeneDrugGroup(
				new String[] { INDIRECT_GENE_SYMBOL_2 },
				asList(multipleGeneMixed[7])
			)
		};
	}
	
	public static GeneDrugGroupInfos singleGeneGroupInfosDirect() {
		return new GeneDrugGroupInfos(asList(singleGeneGroupDirect()));
	}
	
	public static GeneDrugGroupInfos multipleGeneGroupInfosDirect() {
		return new GeneDrugGroupInfos(asList(multipleGeneGroupDirect()));
	}
	
	public static GeneDrugGroupInfos singleGeneGroupInfosIndirect() {
		return new GeneDrugGroupInfos(asList(singleGeneGroupIndirect()));
	}
	
	public static GeneDrugGroupInfos multipleGeneGroupInfosIndirect() {
		return new GeneDrugGroupInfos(asList(multipleGeneGroupIndirect()));
	}
	
	public static GeneDrugGroupInfos multipleGeneGroupInfosMixed() {
		return new GeneDrugGroupInfos(asList(multipleGeneGroupMixed()));
	}
	
	public static String absentGeneSymbol() {
		return ABSENT_GENE_SYMBOL;
	}
	
	public static String singleGeneSymbolDirect() {
		return DIRECT_GENE_SYMBOL_1;
	}

	public static String[] multipleGeneSymbolsDirect() {
		return new String[] { singleGeneSymbolDirect(), DIRECT_GENE_SYMBOL_2 };
	}

	public static String singleGeneSymbolIndirect() {
		return INDIRECT_GENE_SYMBOL_1;
	}

	public static String[] multipleGeneSymbolsIndirect() {
		return new String[] { singleGeneSymbolIndirect(), INDIRECT_GENE_SYMBOL_2 };
	}
	
	public static String[] multipleGeneSymbolsMixed() {
		return concat(stream(multipleGeneSymbolsDirect()), stream(multipleGeneSymbolsIndirect()))
			.toArray(String[]::new);
	}
	
	public static String absentDrugName() {
		return ABSENT_DRUG_NAME;
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
			DRUG_NAME_21,
			DRUG_NAME_22,
			DRUG_NAME_3
		};
	}
	
	public static GeneDrugGroup[] singleDrugGeneDrugGroups() {
		final GeneDrug[] geneDrugs = geneDrugs();
		
		return new GeneDrugGroup[] {
			new GeneDrugGroup(
				new String[] { DIRECT_GENE_SYMBOL_1, DIRECT_GENE_SYMBOL_2 },
				asList(geneDrugs[0], geneDrugs[1])
			)
		};
	}
	
	public static GeneDrugGroup[] multipleDrugGeneDrugGroups() {
		final GeneDrug[] geneDrugs = geneDrugs();
		
		return new GeneDrugGroup[] {
			new GeneDrugGroup(
				new String[] { WITH_INDIRECT_GENE_SYMBOL_2 },
				asList(geneDrugs[7])
			),
			new GeneDrugGroup(
				new String[] { WITH_INDIRECT_GENE_SYMBOL_2 },
				asList(geneDrugs[8])
			),
			new GeneDrugGroup(
				new String[] { DIRECT_GENE_SYMBOL_1, DIRECT_GENE_SYMBOL_2 },
				asList(geneDrugs[0], geneDrugs[1])
			),
			new GeneDrugGroup(
				new String[] { DIRECT_GENE_SYMBOL_2 },
				asList(geneDrugs[2])
			),
			new GeneDrugGroup(
				new String[] { DIRECT_GENE_SYMBOL_2 },
				asList(geneDrugs[3])
			),
			new GeneDrugGroup(
				new String[] { WITH_INDIRECT_GENE_SYMBOL_1, WITH_INDIRECT_GENE_SYMBOL_2 },
				asList(geneDrugs[5], geneDrugs[6])
			)
		};
	}
	
	public static GeneDrugGroupInfos singleDrugGeneDrugGroupsInfos() {
		return new GeneDrugGroupInfos(asList(singleDrugGeneDrugGroups()));
	}
	
	public static GeneDrugGroupInfos multipleDrugGeneDrugGroupsMixed() {
		return new GeneDrugGroupInfos(asList(multipleDrugGeneDrugGroups()));
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
