/*-
 * #%L
 * PanDrugs Backend
 * %%
 * Copyright (C) 2015 - 2018 Fátima Al-Shahrour, Elena Piñeiro, Daniel Glez-Peña and Miguel Reboiro-Jato
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

import static es.uvigo.ei.sing.pandrugs.persistence.entity.DriverLevel.CANDIDATE_DRIVER;
import static es.uvigo.ei.sing.pandrugs.persistence.entity.DriverLevel.HIGH_CONFIDENCE_DRIVER;
import static es.uvigo.ei.sing.pandrugs.persistence.entity.GeneDataset.newGene;
import static es.uvigo.ei.sing.pandrugs.persistence.entity.TumorPortalMutationLevel.HIGHLY_SIGNIFICANTLY_MUTATED;
import static es.uvigo.ei.sing.pandrugs.persistence.entity.TumorPortalMutationLevel.NEAR_SIGNIFICANCE;
import static es.uvigo.ei.sing.pandrugs.persistence.entity.TumorPortalMutationLevel.SIGNIFICANTLY_MUTATED;
import static java.util.Arrays.asList;
import static java.util.Arrays.stream;
import static java.util.Collections.emptyList;
import static java.util.Collections.emptyMap;
import static java.util.stream.Collectors.toList;
import static org.easymock.EasyMock.expect;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.collection.IsArrayContainingInAnyOrder.arrayContainingInAnyOrder;
import static org.hamcrest.collection.IsArrayContainingInOrder.arrayContaining;
import static org.hamcrest.collection.IsArrayWithSize.emptyArray;
import static org.hamcrest.collection.IsIterableContainingInAnyOrder.containsInAnyOrder;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.SortedMap;

import org.easymock.EasyMockSupport;
import org.junit.After;
import org.junit.Test;

import com.google.common.collect.ImmutableSortedMap;

import es.uvigo.ei.sing.pandrugs.persistence.entity.CancerType;
import es.uvigo.ei.sing.pandrugs.persistence.entity.Drug;
import es.uvigo.ei.sing.pandrugs.persistence.entity.DrugSource;
import es.uvigo.ei.sing.pandrugs.persistence.entity.DrugStatus;
import es.uvigo.ei.sing.pandrugs.persistence.entity.Extra;
import es.uvigo.ei.sing.pandrugs.persistence.entity.Gene;
import es.uvigo.ei.sing.pandrugs.persistence.entity.GeneDrug;
import es.uvigo.ei.sing.pandrugs.persistence.entity.OncodriveRole;

public class GeneDrugGroupTest extends EasyMockSupport {
	@After
	public void tearDown() throws Exception {
		verifyAll();
	}

	@Test
	public void testConstructorSingleDirectGeneDrug() {
		final String[] genes = new String[] { "G1" };
		final List<GeneDrug> genesDrugs = asList(newGeneDrug("G1", newDrug("D1")));
		
		replayAll();
		
		new GeneDrugGroup(genes, genesDrugs, emptyMap());
	}

	@Test
	public void testConstructorMultipleDirectGeneDrug() {
		final String[] genes = new String[] { "G1", "G2", "G3" };
		final Drug drug = newDrug("D1");
		final List<GeneDrug> geneDrugs = asList(
			newGeneDrug("G1", drug),
			newGeneDrug("G2", drug),
			newGeneDrugWithIndirect("G3", drug, "IG1", "IG2")
		);
		
		replayAll();
		
		new GeneDrugGroup(genes, geneDrugs, emptyMap());
	}

	@Test
	public void testConstructorSingleIndirectGeneDrug() {
		final String[] genes = new String[] { "IG1" };
		final List<GeneDrug> geneDrugs = asList(newGeneDrugWithIndirect("G1", newDrug("D1"), "IG1"));
		
		replayAll();
		
		new GeneDrugGroup(genes, geneDrugs, emptyMap());
	}

	@Test
	public void testConstructorMultipleIndirectGeneDrug() {
		final String[] genes = new String[] { "IG1", "IG2", "IG3" };
		final Drug drug = newDrug("D1");
		final List<GeneDrug> geneDrugs = asList(
			newGeneDrugWithIndirect("G1", drug, "IG1"),
			newGeneDrugWithIndirect("G2", drug, "IG2"),
			newGeneDrugWithIndirect("G3", drug, "IG1", "IG2")
		);
		
		replayAll();
		
		new GeneDrugGroup(genes, geneDrugs, emptyMap());
	}

	@Test
	public void testConstructorMultipleBothGeneDrug() {
		final String[] genes = new String[] { "G1", "G2", "IG1", "IG2" };
		final Drug drug = newDrug("D1");
		final List<GeneDrug> geneDrugs = asList(
			newGeneDrug("G1", drug),
			newGeneDrugWithIndirect("G2", drug, "IG200"),
			newGeneDrugWithIndirect("G3", drug, "IG1"),
			newGeneDrugWithIndirect("G3", drug, "IG100", "IG2")
		);
		
		replayAll();
		
		new GeneDrugGroup(genes, geneDrugs, emptyMap());
	}

	@Test(expected = IllegalArgumentException.class)
	public void testConstructorMissingGeneDrug() {
		final String[] genes = new String[] { "MG1" };
		final List<GeneDrug> geneDrugs = asList(newGeneDrugWithIndirect("G1", newDrug("D1"), new String[0]));
		
		replayAll();
		
		new GeneDrugGroup(genes, geneDrugs, emptyMap());
	}

	@Test(expected = IllegalArgumentException.class)
	public void testConstructorEmptyGeneDrug() {
		final String[] genes = new String[] { "G1" };
		final List<GeneDrug> geneDrugs = emptyList();
		
		replayAll();
		
		new GeneDrugGroup(genes, geneDrugs, emptyMap());
	}

	@Test(expected = NullPointerException.class)
	public void testConstructorNullGeneDrug() {
		final String[] genes = new String[] { "G1" };
		
		new GeneDrugGroup(genes, null, emptyMap());
	}

	@Test(expected = IllegalArgumentException.class)
	public void testConstructorEmptyGenes() {
		final List<GeneDrug> geneDrugs = asList(newGeneDrug("G1", newDrug("D1")));
		
		replayAll();
		
		new GeneDrugGroup(new String[0], geneDrugs, emptyMap());
	}

	@Test(expected = NullPointerException.class)
	public void testConstructorNullGenes() {
		final List<GeneDrug> geneDrugs = asList(newGeneDrug("G1", newDrug("D1")));

		replayAll();
		
		new GeneDrugGroup(null, geneDrugs, emptyMap());
	}

	@Test(expected = IllegalArgumentException.class)
	public void testConstructorDifferentDrugs() {
		final String[] genes = new String[] { "G1", "G2", "G3" };
		
		final Drug drug1 = newDrug("D1");
		final Drug drug2 = newDrug("D2");
		final GeneDrug[] gd = new GeneDrug[] {
			newGeneDrug("G1", drug1),
			newGeneDrug("G2", drug1),
			newGeneDrugWithIndirect("G3", drug2, "IG1", "IG2")
		};
		
		replayAll();
		
		new GeneDrugGroup(genes, asList(gd), emptyMap());
	}

	@Test(expected = IllegalArgumentException.class)
	public void testConstructorDifferentStatus() {
		final String[] genes = new String[] { "G1", "G2" };
		
		final Drug drug = newDrug("D1");
		final List<GeneDrug> geneDrugs = asList(
			newGeneDrug("G1", drug, DrugStatus.APPROVED, null, null, null),
			newGeneDrug("G2", drug, DrugStatus.CLINICAL_TRIALS, null, null, null)
		);
		
		replayAll();
		
		new GeneDrugGroup(genes, geneDrugs, emptyMap());
	}

	@Test(expected = IllegalArgumentException.class)
	public void testConstructorDifferentPathology() {
		final String[] genes = new String[] { "G1", "G2" };
		
		final Drug drug = newDrug("D1");
		final List<GeneDrug> geneDrugs = asList(
			newGeneDrug("G1", drug, null, new String[] { "P1" }, null, null),
			newGeneDrug("G2", drug, null, new String[] { "P2" }, null, null)
		);
		
		replayAll();
		
		new GeneDrugGroup(genes, geneDrugs, emptyMap());
	}

	@Test(expected = IllegalArgumentException.class)
	public void testConstructorDifferentCancer() {
		final String[] genes = new String[] { "G1", "G2" };
		
		final Drug drug = newDrug("D1");
		final List<GeneDrug> geneDrugs = asList(
			newGeneDrug("G1", drug, null, null, new CancerType[] { CancerType.ADRENAL_GLAND }, null),
			newGeneDrug("G2", drug, null, null, new CancerType[] { CancerType.BLADDER }, null)
		);
		
		replayAll();
		
		new GeneDrugGroup(genes, geneDrugs, emptyMap());
	}

	@Test(expected = IllegalArgumentException.class)
	public void testConstructorDifferentExtra() {
		final String[] genes = new String[] { "G1", "G2" };
		
		final Drug drug = newDrug("D1");
		final List<GeneDrug> geneDrugs = asList(
			newGeneDrug("G1", drug, null, null, null, Extra.CHEMOTHERAPY),
			newGeneDrug("G2", drug, null, null, null, Extra.IMMUNOTHERAPY)
		);
		
		replayAll();
		
		new GeneDrugGroup(genes, geneDrugs, emptyMap());
	}

	@Test
	public void testGetGeneDrugs() {
		final String[] targetGenes = new String[] { "G1", "G2", "IG1" };
		final Drug drug = newDrug("D1");
		final GeneDrug[] geneDrugs = new GeneDrug[] {
			newGeneDrug("G1", drug),
			newGeneDrugWithIndirect("G2", drug, "IG200"),
			newGeneDrugWithIndirect("G3", drug, "IG1")
		};
		
		replayAll();
		
		final GeneDrugGroup group = new GeneDrugGroup(
			targetGenes, asList(geneDrugs), emptyMap()
		);
		
		assertThat(group.getGeneDrugs(), containsInAnyOrder(geneDrugs));
	}

	@Test
	public void testGetTargetGenes() {
		final String[] genes = new String[] { "G1", "G2", "IG1" };
		final Drug drug = newDrug("D1");
		final List<GeneDrug> geneDrugs = asList(
			newGeneDrug("G1", drug),
			newGeneDrugWithIndirect("G2", drug, "IG200"),
			newGeneDrugWithIndirect("G3", drug, "IG1")
		);
		
		replayAll();
		
		final GeneDrugGroup group = new GeneDrugGroup(genes, geneDrugs, emptyMap());

		assertThat(group.getQueryGeneSymbols(), is(arrayContainingInAnyOrder(genes)));
	}

	@Test
	public void testGetDirectGenes() {
		final String[] genes = new String[] { "G1", "G2", "IG1" };
		final Drug drug = newDrug("D1");
		final List<GeneDrug> geneDrugs = asList(
			newGeneDrug("G1", drug),
			newGeneDrugWithIndirect("G2", drug, "IG200"),
			newGeneDrugWithIndirect("G3", drug, "IG1")
		);
		
		replayAll();
		
		final GeneDrugGroup group = new GeneDrugGroup(genes, geneDrugs, emptyMap());

		assertThat(group.getDirectGeneSymbols(), is(arrayContaining("G1", "G2")));
	}

	@Test
	public void testGetIndirectGenes() {
		final String[] genes = new String[] { "G1", "G2", "IG1" };
		final Drug drug = newDrug("D1");
		final List<GeneDrug> geneDrugs = asList(
			newGeneDrug("G1", drug),
			newGeneDrugWithIndirect("G2", drug, "IG200"),
			newGeneDrugWithIndirect("G3", drug, "IG1")
		);
		
		replayAll();
		
		final GeneDrugGroup group = new GeneDrugGroup(genes, geneDrugs, emptyMap());

		assertThat(group.getIndirectGeneSymbols(), is(arrayContaining("IG1")));
	}

	@Test
	public void testIsDirect() {
		final Drug drug = newDrug("D1");
		final GeneDrug directGeneDrug = newGeneDrug("G1", drug);
		final String[] genes = new String[] { "G1", "G2", "IG1" };
		final List<GeneDrug> geneDrugs = asList(
			directGeneDrug,
			newGeneDrugWithIndirect("G2", drug, "IG200"),
			newGeneDrugWithIndirect("G3", drug, "IG1")
		);
		
		replayAll();
		
		final GeneDrugGroup group = new GeneDrugGroup(genes, geneDrugs, emptyMap());
		
		assertTrue(group.isDirect(directGeneDrug));
	}

	@Test
	public void testIsIndirect() {
		final Drug drug = newDrug("D1");
		final GeneDrug indirectGeneDrug = newGeneDrugWithIndirect("G3", drug, "IG1");
		final List<GeneDrug> geneDrugs = asList(
			newGeneDrugWithIndirect("G1", drug, new String[0]),
			newGeneDrugWithIndirect("G2", drug, "IG200"),
			indirectGeneDrug
		);
		final String[] genes = new String[] { "G1", "G2", "IG1" };
		
		replayAll();
		
		final GeneDrugGroup group = new GeneDrugGroup(genes, geneDrugs, emptyMap());
		
		assertTrue(group.isIndirect(indirectGeneDrug));
	}

	@Test(expected = IllegalArgumentException.class)
	public void testIsDirectNotPresent() {
		final GeneDrug notPresentGD = newGeneDrug("GX", newDrug("DX"));
		final String[] genes = new String[] { "G1", "G2", "IG1" };
		final Drug drug = newDrug("D1");
		final List<GeneDrug> geneDrugs = asList(
			newGeneDrug("G1", drug),
			newGeneDrugWithIndirect("G2", drug, "IG200"),
			newGeneDrugWithIndirect("G3", drug, "IG1")
		);
		
		replayAll();
		
		final GeneDrugGroup group = new GeneDrugGroup(genes, geneDrugs, emptyMap());
		
		group.isDirect(notPresentGD);
	}

	@Test(expected = IllegalArgumentException.class)
	public void testIsIndirectNotPresent() {
		final Drug drug = newDrug("D1");
		final GeneDrug indirectGeneDrug = newGeneDrugWithIndirect("G3", drug, "IG1");
		final GeneDrug notPresentGD = newGeneDrug("GX", newDrug("DX"));
		
		final String[] genes = new String[] { "G1", "G2", "IG1" };
		final List<GeneDrug> geneDrugs = asList(
			newGeneDrug("G1", drug),
			newGeneDrugWithIndirect("G2", drug, "IG200"),
			indirectGeneDrug
		);
		
		replayAll();
		
		final GeneDrugGroup group = new GeneDrugGroup(genes, geneDrugs, emptyMap());
		
		group.isDirect(notPresentGD);
	}

	@Test
	public void testCountTargetGenes() {
		final String[] genes = new String[] { "G1", "G2", "IG1" };
		final Drug drug = newDrug("D1");
		final List<GeneDrug> geneDrugs = asList(
			newGeneDrug("G1", drug),
			newGeneDrugWithIndirect("G2", drug, "IG200"),
			newGeneDrugWithIndirect("G3", drug, "IG1")
		);
		
		replayAll();
		
		final GeneDrugGroup group = new GeneDrugGroup(genes, geneDrugs, emptyMap());
		
		assertThat(group.countQueryGenes(), is(3));
	}

	@Test
	public void testCountDirectGenes() {
		final String[] genes = new String[] { "G1", "G2", "IG1" };
		final Drug drug = newDrug("D1");
		final List<GeneDrug> geneDrugs = asList(
			newGeneDrug("G1", drug),
			newGeneDrugWithIndirect("G2", drug, "IG200"),
			newGeneDrugWithIndirect("G3", drug, "IG1")
		);
		
		replayAll();
		
		final GeneDrugGroup group = new GeneDrugGroup(genes, geneDrugs, emptyMap());

		assertThat(group.countDirectGenes(), is(2));
	}

	@Test
	public void testCountIndirectGenes() {
		final String[] genes = new String[] { "G1", "G2", "IG1" };
		final Drug drug = newDrug("D1");
		final List<GeneDrug> geneDrugs = asList(
			newGeneDrug("G1", drug),
			newGeneDrugWithIndirect("G2", drug, "IG200"),
			newGeneDrugWithIndirect("G3", drug, "IG1")
		);
		
		replayAll();
		
		final GeneDrugGroup group = new GeneDrugGroup(genes, geneDrugs, emptyMap());

		assertThat(group.countIndirectGenes(), is(1));
	}

	@Test
	public void testIsOnlyIndirectDirect() {
		final String[] genes = new String[] { "G1", "G2", "G3" };
		final Drug drug = newDrug("D1");
		final List<GeneDrug> geneDrugs = asList(
			newGeneDrug("G1", drug),
			newGeneDrug("G2", drug),
			newGeneDrugWithIndirect("G3", drug, "IG1", "IG2")
		);
		
		replayAll();
		
		final GeneDrugGroup group = new GeneDrugGroup(genes, geneDrugs, emptyMap());
		
		assertFalse(group.isOnlyIndirect());
	}

	@Test
	public void testIsOnlyIndirectIndirect() {
		final String[] genes = new String[] { "IG1", "IG2", "IG3" };
		final Drug drug = newDrug("D1");
		final List<GeneDrug> geneDrugs = asList(
			newGeneDrugWithIndirect("G1", drug, "IG1"),
			newGeneDrugWithIndirect("G2", drug, "IG2"),
			newGeneDrugWithIndirect("G3", drug, "IG1", "IG2")
		);
		
		replayAll();
		
		final GeneDrugGroup group = new GeneDrugGroup(genes, geneDrugs, emptyMap());
		
		assertTrue(group.isOnlyIndirect());
	}

	@Test
	public void testIsOnlyIndirectBoth() {
		final String[] genes = new String[] { "G1", "G2", "IG1", "IG2" };
		final Drug drug = newDrug("D1");
		final List<GeneDrug> geneDrugs = asList(
			newGeneDrug("G1", drug),
			newGeneDrugWithIndirect("G2", drug, "IG200"),
			newGeneDrugWithIndirect("G3", drug, "IG1"),
			newGeneDrugWithIndirect("G3", drug, "IG100", "IG2")
		);
		
		replayAll();
		
		final GeneDrugGroup group = new GeneDrugGroup(genes, geneDrugs, emptyMap());
		
		assertFalse(group.isOnlyIndirect());
	}

	@Test
	public void testGetStandardDrugName() {
		final String[] genes = new String[] { "G1", "G2", "IG1", "IG2" };
		final Drug drug = newDrug("D1");
		final List<GeneDrug> geneDrugs = asList(
			newGeneDrug("G1", drug),
			newGeneDrugWithIndirect("G2", drug, "IG200"),
			newGeneDrugWithIndirect("G3", drug, "IG1"),
			newGeneDrugWithIndirect("G3", drug, "IG100", "IG2")
		);
		
		replayAll();
		
		final GeneDrugGroup group = new GeneDrugGroup(genes, geneDrugs, emptyMap());
		
		assertThat(group.getStandardDrugName(), is("D1"));
	}

	@Test
	public void testGetStatus() {
		final DrugStatus status = DrugStatus.APPROVED;
		final String[] genes = new String[] { "G1", "G2" };
		
		final Drug drug = newDrug("D1", status);
		final List<GeneDrug> geneDrugs = asList(
			newGeneDrug("G1", drug),
			newGeneDrug("G2", drug)
		);
		
		replayAll();
		
		final GeneDrugGroup group = new GeneDrugGroup(genes, geneDrugs, emptyMap());
		
		assertThat(group.getStatus(), is(status));
	}

	@Test
	public void testGetCancer() {
		final CancerType[] cancers = new CancerType[] { CancerType.CANCER, CancerType.BLOOD };
		final String[] genes = new String[] { "G1", "G2" };
		
		final Drug drug = newDrug("D1", cancers);
		final List<GeneDrug> geneDrugs = asList(
			newGeneDrug("G1", drug),
			newGeneDrug("G2", drug)
		);
		
		replayAll();
		
		final GeneDrugGroup group = new GeneDrugGroup(genes, geneDrugs, emptyMap());
		
		assertThat(group.getCancers(), arrayContainingInAnyOrder(cancers));
	}

	@Test
	public void testGetExtra() {
		final Extra extra = Extra.ANTIHORMONE_THERAPY;
		final String[] genes = new String[] { "G1", "G2" };
		
		final Drug drug = newDrug("D1", extra);
		final List<GeneDrug> geneDrugs = asList(
			newGeneDrug("G1", drug),
			newGeneDrug("G2", drug)
		);
		
		replayAll();
		
		final GeneDrugGroup group = new GeneDrugGroup(genes, geneDrugs, emptyMap());
		
		assertThat(group.getExtra(), is(extra));
	}

	@Test
	public void testGetFamily() {
		final String[] genes = new String[] { "G1", "G2", "G3" };
		
		final Drug drug = newDrugWithFamily("D1", "F1", "F2");
		final List<GeneDrug> geneDrugs = asList(
			newGeneDrug("G1", drug, null, null, null, null),
			newGeneDrug("G2", drug, null, null, null, null),
			newGeneDrug("G3", drug, null, null, null, null)
		);
		
		replayAll();
		
		final GeneDrugGroup group = new GeneDrugGroup(genes, geneDrugs, emptyMap());
		
		assertThat(group.getFamilies(), arrayContaining("F1", "F2"));
	}

	@Test
	public void testGetSources() {
		final DrugSource ds1 = newDrugSource("DS1");
		final DrugSource ds2 = newDrugSource("DS2");
		final DrugSource ds3 = newDrugSource("DS3");
		
		final GeneDrug gd1 = newGeneDrugWithDrugSources("G1", newDrug("D1", ds1), asSet(ds1));
		final GeneDrug gd2 = newGeneDrugWithDrugSources("G2", newDrug("D1", ds2), asSet(ds2));
		final GeneDrug gd3 = newGeneDrugWithDrugSources("G3", newDrug("D1", ds1, ds3), asSet(ds1, ds3));
		
		replayAll();
		
		final GeneDrugGroup geneDrugGroup = new GeneDrugGroup(
			new String[] { "G1", "G2", "G3" },
			asList(gd1, gd2, gd3),
			emptyMap()
		);
		
		assertThat(geneDrugGroup.getSources(), is(arrayContainingInAnyOrder(ds1, ds2, ds3)));
	}

	@Test
	public void testGetSourceNames() {
		final DrugSource ds1 = newDrugSource("DS1");
		final DrugSource ds2 = newDrugSource("DS2");
		final DrugSource ds3 = newDrugSource("DS3");
		
		final GeneDrug gd1 = newGeneDrugWithDrugSources("G1", newDrug("D1", ds1), asSet(ds1));
		final GeneDrug gd2 = newGeneDrugWithDrugSources("G2", newDrug("D1", ds2), asSet(ds2));
		final GeneDrug gd3 = newGeneDrugWithDrugSources("G3", newDrug("D1", ds1, ds3), asSet(ds1, ds3));
		
		replayAll();
		
		final GeneDrugGroup geneDrugGroup = new GeneDrugGroup(
			new String[] { "G1", "G2", "G3" },
			asList(gd1, gd2, gd3),
			emptyMap()
		);
		
		assertThat(geneDrugGroup.getSourceNames(),
			is(arrayContaining("DS1", "DS2", "DS3"))
		);
	}

	@Test
	public void testGetSourceLinks() {
		final String[] targetGenes = new String[] { "G1", "G2", "G3" };
		
		final DrugSource ds1 = newDrugSource("DS1");
		final DrugSource ds2 = newDrugSource("DS2");
		final DrugSource ds3 = newDrugSource("DS3");
		
		expect(ds1.getDrugURL(targetGenes))
			.andReturn("DS1_URL");
		expect(ds2.getDrugURL(targetGenes))
			.andReturn("DS2_URL");
		expect(ds3.getDrugURL(targetGenes))
			.andReturn("DS3_URL");
		
		final GeneDrug gd1 = newGeneDrugWithDrugSources("G1", newDrug("D1", ds1), asSet(ds1));
		final GeneDrug gd2 = newGeneDrugWithDrugSources("G2", newDrug("D1", ds2), asSet(ds2));
		final GeneDrug gd3 = newGeneDrugWithDrugSources("G3", newDrug("D1", ds1, ds3), asSet(ds1, ds3));
		
		final SortedMap<String, String> expectedLinks = ImmutableSortedMap.of(
			"DS1", "DS1_URL", "DS2", "DS2_URL", "DS3", "DS3_URL"
		);
		
		replayAll();
		
		final GeneDrugGroup geneDrugGroup = new GeneDrugGroup(
			targetGenes,
			asList(gd1, gd2, gd3),
			emptyMap()
		);
		
		
		assertThat(geneDrugGroup.getSourceLinks(), is(expectedLinks));
	}

	@Test
	public void testGetCuratedSources() {
		final DrugSource ds1 = newDrugSource("DS1", true);
		final DrugSource ds2 = newDrugSource("DS2", false);
		final DrugSource ds3 = newDrugSource("DS3", true);

		final GeneDrug gd1 = newGeneDrugWithDrugSources("G1", newDrug("D1", ds1), asSet(ds1), asList(ds1));
		final GeneDrug gd2 = newGeneDrugWithDrugSources("G2", newDrug("D1", ds2), asSet(ds2));
		final GeneDrug gd3 = newGeneDrugWithDrugSources("G3", newDrug("D1", ds1, ds3), asSet(ds1, ds3), asList(ds1, ds3));
		
		replayAll();
		
		final GeneDrugGroup geneDrugGroup = new GeneDrugGroup(
			new String[] { "G1", "G2", "G3" },
			asList(gd1, gd2, gd3),
			emptyMap()
		);
		
		assertThat(geneDrugGroup.getCuratedSources(),
			is(arrayContainingInAnyOrder(ds1, ds3)));
	}

	@Test
	public void testGetCuratedSourcesEmpty() {
		final DrugSource ds1 = newDrugSource("DS1", false);
		final DrugSource ds2 = newDrugSource("DS2", false);
		final DrugSource ds3 = newDrugSource("DS3", false);

		final GeneDrug gd1 = newGeneDrugWithDrugSources("G1", newDrug("D1", ds1), asSet(ds1));
		final GeneDrug gd2 = newGeneDrugWithDrugSources("G2", newDrug("D1", ds2), asSet(ds2));
		final GeneDrug gd3 = newGeneDrugWithDrugSources("G3", newDrug("D1", ds1, ds3), asSet(ds1, ds3));
		
		replayAll();
		
		final GeneDrugGroup geneDrugGroup = new GeneDrugGroup(
			new String[] { "G1", "G2", "G3" },
			asList(gd1, gd2, gd3),
			emptyMap()
		);
		
		assertThat(geneDrugGroup.getCuratedSources(), is(emptyArray()));
	}

	@Test
	public void testGetCuratedSourceNames() {
		final DrugSource ds1 = newDrugSource("DS1", true);
		final DrugSource ds2 = newDrugSource("DS2", false);
		final DrugSource ds3 = newDrugSource("DS3", true);

		final GeneDrug gd1 = newGeneDrugWithDrugSources("G1", newDrug("D1", ds1), asSet(ds1), asList(ds1));
		final GeneDrug gd2 = newGeneDrugWithDrugSources("G2", newDrug("D1", ds2), asSet(ds2));
		final GeneDrug gd3 = newGeneDrugWithDrugSources("G3", newDrug("D1", ds1, ds3), asSet(ds1, ds3), asList(ds1, ds3));
		
		replayAll();
		
		final GeneDrugGroup geneDrugGroup = new GeneDrugGroup(
			new String[] { "G1", "G2", "G3" },
			asList(gd1, gd2, gd3),
			emptyMap()
		);
		
		assertThat(geneDrugGroup.getCuratedSourceNames(),
			is(arrayContaining("DS1", "DS3"))
		);
	}

	@Test
	public void testGetCuratedSourceNamesEmpty() {
		final DrugSource ds1 = newDrugSource("DS1", false);
		final DrugSource ds2 = newDrugSource("DS2", false);
		final DrugSource ds3 = newDrugSource("DS3", false);

		final GeneDrug gd1 = newGeneDrugWithDrugSources("G1", newDrug("D1", ds1), asSet(ds1));
		final GeneDrug gd2 = newGeneDrugWithDrugSources("G2", newDrug("D1", ds2), asSet(ds2));
		final GeneDrug gd3 = newGeneDrugWithDrugSources("G3", newDrug("D1", ds1, ds3), asSet(ds1, ds3));
		
		replayAll();
		
		final GeneDrugGroup geneDrugGroup = new GeneDrugGroup(
			new String[] { "G1", "G2", "G3" },
			asList(gd1, gd2, gd3),
			emptyMap()
		);
		
		assertThat(geneDrugGroup.getCuratedSourceNames(),
			is(emptyArray()));
	}
	
	@Test
	public void testIsTarget() {
		final Drug drug = newDrug("D1");
		final GeneDrug gd1 = newGeneDrug("G1", drug, true);
		final GeneDrug gd2 = newGeneDrug("G2", drug, false);
		final GeneDrug gd3 = newGeneDrug("G3", drug, false);
		
		replayAll();
		
		final GeneDrugGroup geneDrugGroup = new GeneDrugGroup(
			new String[] { "G1", "G2", "G3" },
			asList(gd1, gd2, gd3),
			emptyMap()
		);
		
		assertTrue(geneDrugGroup.isTarget());
	}
	
	@Test
	public void testIsTargetFalse() {
		final Drug drug = newDrug("D1");
		final GeneDrug gd1 = newGeneDrug("G1", drug, false);
		final GeneDrug gd2 = newGeneDrug("G2", drug, false);
		final GeneDrug gd3 = newGeneDrug("G3", drug, false);
		
		replayAll();
		
		final GeneDrugGroup geneDrugGroup = new GeneDrugGroup(
			new String[] { "G1", "G2", "G3" },
			asList(gd1, gd2, gd3),
			emptyMap()
		);
		
		assertFalse(geneDrugGroup.isTarget());
	}

	@Test
	public void testTargetGeneNames() {
		final String[] genes = new String[] { "G1", "G2", "IG1", "IG2" };
		
		final Drug drug = newDrug("D1");
		final GeneDrug gd1 = newGeneDrug("G1", drug);
		final GeneDrug gd2 = newGeneDrugWithIndirect("G2", drug, "IG200");
		final GeneDrug gd3 = newGeneDrugWithIndirect("G3", drug, "IG1", "IG2");
		
		final List<GeneDrug> geneDrugs = asList(gd1, gd2, gd3);
		
		replayAll();
		
		final GeneDrugGroup group = new GeneDrugGroup(genes, geneDrugs, emptyMap());
		
		assertThat(group.getQueryGeneSymbolsForGeneDrug(gd1, false), is(arrayContaining("G1")));
		assertThat(group.getQueryGeneSymbolsForGeneDrug(gd2, false), is(arrayContaining("G2")));
		assertThat(group.getQueryGeneSymbolsForGeneDrug(gd3, false), is(arrayContaining("IG1", "IG2")));
	}

	@Test
	public void testIndirectGeneNames() {
		final String[] genes = new String[] { "G1", "G2", "IG1", "IG2" };
		
		final Drug drug = newDrug("D1");
		final GeneDrug gd1 = newGeneDrug("G1", drug);
		final GeneDrug gd2 = newGeneDrugWithIndirect("G2", drug, "IG200");
		final GeneDrug gd3 = newGeneDrugWithIndirect("G3", drug, "IG1", "IG2");
		
		final List<GeneDrug> geneDrugs = asList(gd1, gd2, gd3);
		
		replayAll();
		
		final GeneDrugGroup group = new GeneDrugGroup(genes, geneDrugs, emptyMap());
		
		assertThat(group.getIndirectGeneSymbol(gd1, false), is(nullValue()));
		assertThat(group.getIndirectGeneSymbol(gd2, false), is(nullValue()));
		assertThat(group.getIndirectGeneSymbol(gd3, false), is("G3"));
	}
	
	@Test
	public void testGScore() {
		final String[] genes = new String[] { "G_LOW", "G_MED", "G_HIGH" };
		
		final Gene giLow = newGene("G_LOW", NEAR_SIGNIFICANCE, false, CANDIDATE_DRIVER, 0.125d, false, OncodriveRole.NONE, 0d);
		final Gene giMedium = newGene("G_MED", SIGNIFICANTLY_MUTATED, true, CANDIDATE_DRIVER, 0.5d, false, OncodriveRole.NONE, 2d);
		final Gene giHigh = newGene("G_HIGH", HIGHLY_SIGNIFICANTLY_MUTATED, true, HIGH_CONFIDENCE_DRIVER, 1d, false, OncodriveRole.NONE, 4d);
		
		final Drug drug = newDrug("D1");
		final List<GeneDrug> geneDrugs = asList(
			newGeneDrug(giLow, drug),
			newGeneDrug(giHigh, drug),
			newGeneDrug(giMedium, drug)
		);
		geneDrugs.forEach(gd -> expect(gd.getIndirectGeneSymbols()).andReturn(emptyList()).anyTimes());
		
		replayAll();
		
		final GeneDrugGroup group = new GeneDrugGroup(genes, geneDrugs, emptyMap());
		
		assertThat(group.getGScore(), is(1d));
	}
	
	private final Drug newDrug(String drugName) {
		final Drug drug = createNiceMock(Drug.class);
		
		expect(drug.getId()).andReturn(drugName.hashCode()).anyTimes();
		expect(drug.getStandardName()).andReturn(drugName).anyTimes();
		
		return drug;
	}
	
	private final Drug newDrugWithFamily(String drugName, String ... families) {
		final Drug drug = createNiceMock(Drug.class);
		
		expect(drug.getId()).andReturn(drugName.hashCode()).anyTimes();
		expect(drug.getStandardName()).andReturn(drugName).anyTimes();
		expect(drug.getFamilies()).andReturn(families).anyTimes();
		
		return drug;
	}
	
	private final Drug newDrug(String drugName, Extra extra) {
		final Drug drug = newDrug(drugName);
		
		expect(drug.getExtra()).andReturn(extra);
		
		return drug;
	}
	
	private final Drug newDrug(String drugName, DrugStatus status) {
		final Drug drug = newDrug(drugName);
		
		expect(drug.getStatus()).andReturn(status);
		
		return drug;
	}
	
	private final Drug newDrug(String drugName, CancerType ... cancers) {
		final Drug drug = newDrug(drugName);
		
		expect(drug.getCancers()).andReturn(cancers);
		
		return drug;
	}
	
	private final Drug newDrug(String drugName, DrugSource ... drugSources) {
		final Drug drug = newDrug(drugName);
		
		expect(drug.getDrugSources()).andReturn(asSet(drugSources)).anyTimes();
		expect(drug.getCuratedDrugSources())
			.andAnswer(() -> stream(drugSources)
				.filter(DrugSource::isCurated)
			.collect(toList())
		).anyTimes();
		
		return drug;
	}
	
	private final DrugSource newDrugSource(String source) {
		final DrugSource ds = createNiceMock(DrugSource.class);
		expect(ds.getSource()).andReturn(source).anyTimes();
		
		return ds;
	}

	private final DrugSource newDrugSource(String source, boolean curated) {
		final DrugSource ds = createNiceMock(DrugSource.class);
		
		expect(ds.getSource()).andReturn(source).anyTimes();
		expect(ds.isCurated()).andReturn(curated).anyTimes();
		
		return ds;
	}

	private final GeneDrug newGeneDrug(String gene, Drug drug) {
		return newGeneDrugWithIndirect(gene, drug, new String[0]);
	}

	private final GeneDrug newGeneDrugWithDrugSources(String gene, Drug drug, Set<DrugSource> drugSources) {
		return newGeneDrugWithIndirect(gene, drug, drugSources, emptyList(), new String[0]);
	}

	private final GeneDrug newGeneDrugWithDrugSources(String gene, Drug drug, Set<DrugSource> drugSources, List<DrugSource> curatedDrugSources) {
		return newGeneDrugWithIndirect(gene, drug, drugSources, curatedDrugSources, new String[0]);
	}
	
	private final GeneDrug newGeneDrugWithIndirect(String gene, Drug drug, String ... indirect) {
		final GeneDrug gd = createNiceMock(GeneDrug.class);
		
		expect(gd.getGeneSymbol()).andReturn(gene).anyTimes();
		expect(gd.getDrug()).andReturn(drug).anyTimes();
		expect(gd.getDrugId()).andAnswer(drug::getId).anyTimes();
		expect(gd.getStandardDrugName()).andAnswer(drug::getStandardName).anyTimes();
		expect(gd.getIndirectGeneSymbols()).andReturn(asList(indirect)).anyTimes();
		
		return gd;
	}
	
	private final GeneDrug newGeneDrugWithIndirect(String gene, Drug drug, Set<DrugSource> drugSources, List<DrugSource> curatedDrugSources, String ... indirect) {
		final GeneDrug gd = createNiceMock(GeneDrug.class);
		
		expect(gd.getGeneSymbol()).andReturn(gene).anyTimes();
		expect(gd.getDrug()).andReturn(drug).anyTimes();
		expect(gd.getDrugId()).andAnswer(drug::getId).anyTimes();
		expect(gd.getStandardDrugName()).andAnswer(drug::getStandardName).anyTimes();
		expect(gd.getIndirectGeneSymbols()).andReturn(asList(indirect)).anyTimes();
		expect(gd.getDrugSources()).andReturn(drugSources).anyTimes();
		expect(gd.getCuratedDrugSources()).andReturn(curatedDrugSources).anyTimes();
		
		return gd;
	}

	private final GeneDrug newGeneDrug(
		String gene, Drug drug, boolean isTarget
	) {
		final GeneDrug gd = newGeneDrug(gene, drug);
		
		expect(gd.isTarget()).andReturn(isTarget).anyTimes();
		
		return gd;
	}
	
	private final GeneDrug newGeneDrug(
		String gene,
		Drug drug,
		DrugStatus status,
		String[] pathology,
		CancerType[] cancer,
		Extra extra
	) {
		final GeneDrug gd = newGeneDrug(gene, drug);
		
		expect(gd.getStatus()).andReturn(status).anyTimes();
		expect(gd.getPathologies()).andReturn(pathology).anyTimes();
		expect(gd.getCancers()).andReturn(cancer).anyTimes();
		expect(gd.getExtra()).andReturn(extra).anyTimes();
		
		return gd;
	}

	private final GeneDrug newGeneDrug(Gene gene, Drug drug) {
		final GeneDrug gd = createNiceMock(GeneDrug.class);
		
		expect(gd.getGene()).andReturn(gene).anyTimes();
		expect(gd.getDrug()).andReturn(drug).anyTimes();
		expect(gd.getGeneSymbol()).andAnswer(gene::getGeneSymbol).anyTimes();
		expect(gd.getStandardDrugName()).andAnswer(drug::getStandardName).anyTimes();
		
		return gd;
	}
	
	private final static <T> Set<T> asSet(@SuppressWarnings("unchecked") T ... values) {
		return new HashSet<>(asList(values));
	}
}
