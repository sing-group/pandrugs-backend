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

import static java.util.Arrays.asList;
import static java.util.Collections.emptyList;
import static java.util.stream.Collectors.toMap;

import java.util.Map;
import java.util.function.Function;
import java.util.stream.Stream;

import es.uvigo.ei.sing.pandrugsdb.controller.entity.GeneDrugGroup;
import es.uvigo.ei.sing.pandrugsdb.service.entity.GeneDrugGroupInfos;

public final class GeneDrugDataset {
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
	
	public static Map<String, GeneInformation> geneInfos() {
		return Stream.of(
			new GeneInformation("DIRECT GENE 1", null, false, null, 0d),
			new GeneInformation("DIRECT GENE 2", null, false, null, 0d),
			new GeneInformation("INDIRECT GENE 1", null, false, null, 0d),
			new GeneInformation("INDIRECT GENE 2", null, false, null, 0d)
		).collect(toMap(
			GeneInformation::getGeneSymbol,
			Function.identity()
		));
	}
	
	public static Drug[] drugs() {
		return new Drug[] {
			new Drug("Drug 1", "Show Drug 1", DrugStatus.APPROVED, null, null, null, null, null),
			new Drug("Drug 2", "Show Drug 2", DrugStatus.APPROVED, null, null, null, null, null),
			new Drug("Drug 3", "Show Drug 3", DrugStatus.APPROVED, null, null, null, null, null),
			new Drug("Drug 10", "Show Drug 10", DrugStatus.APPROVED, null, null, null, null, null),
			new Drug("Drug 11", "Show Drug 11", DrugStatus.APPROVED, null, null, null, null, null),
			new Drug("Drug 12", "Show Drug 12", DrugStatus.APPROVED, null, null, null, null, null),
		};
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
			)
		};
	}
	
	public static GeneDrug singleGeneDrugDirect() {
		return new GeneDrug(
			geneInfos().get("DIRECT GENE 1"),
			drugs()[0],
			true,
			null,
			null,
			ResistanceType.SENSITIVITY,
			0.1,
			emptyList(),
			asList(drugSources()[0]),
			emptyList()
		);
	}
	
	public static GeneDrug[] multipleGeneDirect() {
		final Map<String, GeneInformation> geneInfos = geneInfos();
		final Drug[] drugs = drugs();
		
		return new GeneDrug[] {
			singleGeneDrugDirect(),
			new GeneDrug(
				geneInfos.get("DIRECT GENE 2"),
				drugs[0],
				true,
				null,
				null,
				ResistanceType.SENSITIVITY,
				0.2,
				emptyList(),
				asList(drugSources()[0]),
				emptyList()
			),
			new GeneDrug(
				geneInfos.get("DIRECT GENE 2"),
				drugs[1],
				true,
				null,
				null,
				ResistanceType.SENSITIVITY,
				0.3,
				emptyList(),
				asList(drugSources()[1]),
				emptyList()
			),
			new GeneDrug(
				geneInfos.get("DIRECT GENE 2"),
				drugs[2],
				true,
				null,
				null,
				ResistanceType.SENSITIVITY,
				0.4,
				emptyList(),
				asList(drugSources()[2]),
				emptyList()
			)
		};
	}
	
	public static GeneDrug singleGeneIndirect() {
		return new GeneDrug(
			geneInfos().get("INDIRECT GENE 1"),
			drugs()[3],
			true,
			null,
			null,
			ResistanceType.SENSITIVITY,
			0.1,
			asList("IG1"),
			asList(drugSources()[3]),
			emptyList()
		);
	}
	
	public static GeneDrug[] multipleGeneIndirect() {
		final Map<String, GeneInformation> geneInfos = geneInfos();
		final Drug[] drugs = drugs();
		
		return new GeneDrug[] {
			singleGeneIndirect(),
			new GeneDrug(
				geneInfos.get("INDIRECT GENE 2"),
				drugs[3],
				true,
				null,
				null,
				ResistanceType.SENSITIVITY,
				0.2,
				asList("IG2"),
				asList(drugSources()[3]),
				emptyList()
			),
			new GeneDrug(
				geneInfos.get("INDIRECT GENE 2"),
				drugs[4],
				true,
				null,
				null,
				ResistanceType.SENSITIVITY,
				0.3,
				asList("IG2"),
				asList(drugSources()[4]),
				emptyList()
			),
			new GeneDrug(
				geneInfos.get("INDIRECT GENE 2"),
				drugs[5],
				true,
				null,
				null,
				ResistanceType.SENSITIVITY,
				0.4,
				asList("IG2"),
				asList(drugSources()[5]),
				emptyList()
			)
		};
	}
	
	public static GeneDrug[] multipleGeneMixed() {
		return Stream.of(multipleGeneDirect(), multipleGeneIndirect())
			.flatMap(Stream::of)
		.toArray(GeneDrug[]::new);
	}
	
	public static GeneDrugGroup singleGeneGroupDirect() {
		return new GeneDrugGroup(
			new String[] { "DIRECT GENE 1" },
			asList(singleGeneDrugDirect())
		);
	}
	
	public static GeneDrugGroup[] multipleGeneGroupDirect() {
		final GeneDrug[] multipleGeneDirect = multipleGeneDirect();
		
		return new GeneDrugGroup[] {
			new GeneDrugGroup(
				new String[] { "DIRECT GENE 1", "DIRECT GENE 2" },
				asList(
					multipleGeneDirect[0],
					multipleGeneDirect[1]
				)
			),
			new GeneDrugGroup(
				new String[] { "DIRECT GENE 2" },
				asList(multipleGeneDirect[2])
			),
			new GeneDrugGroup(
				new String[] { "DIRECT GENE 2" },
				asList(multipleGeneDirect[3])
			)
		};
	}
	
	public static GeneDrugGroup singleGeneGroupIndirect() {
		return new GeneDrugGroup(
			new String[] { "IG1" },
			asList(singleGeneIndirect())
		);
	}
	
	public static GeneDrugGroup[] multipleGeneGroupIndirect() {
		final GeneDrug[] multipleGeneIndirect = multipleGeneIndirect();
		
		return new GeneDrugGroup[] {
			new GeneDrugGroup(
				new String[] { "IG1", "IG2" },
				asList(
					multipleGeneIndirect[0],
					multipleGeneIndirect[1]
				)
			),
			new GeneDrugGroup(
				new String[] { "IG2" },
				asList(multipleGeneIndirect[2])
			),
			new GeneDrugGroup(
				new String[] { "IG2" },
				asList(multipleGeneIndirect[3])
			)
		};
	}
	
	public static GeneDrugGroup[] multipleGeneGroupMixed() {
		final GeneDrug[] multipleGeneMixed = multipleGeneMixed();
		
		return new GeneDrugGroup[] {
			new GeneDrugGroup(
				new String[] { "DIRECT GENE 1", "DIRECT GENE 2" },
				asList(
					multipleGeneMixed[0],
					multipleGeneMixed[1]
				)
			),
			new GeneDrugGroup(
				new String[] { "DIRECT GENE 2" },
				asList(multipleGeneMixed[2])
			),
			new GeneDrugGroup(
				new String[] { "DIRECT GENE 2" },
				asList(multipleGeneMixed[3])
			),
			new GeneDrugGroup(
				new String[] { "IG1", "IG2" },
				asList(
					multipleGeneMixed[4],
					multipleGeneMixed[5]
				)
			),
			new GeneDrugGroup(
				new String[] { "IG2" },
				asList(multipleGeneMixed[6])
			),
			new GeneDrugGroup(
				new String[] { "IG2" },
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
}
