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

import static es.uvigo.ei.sing.pandrugsdb.persistence.entity.DriverLevel.CANDIDATE_DRIVER;
import static es.uvigo.ei.sing.pandrugsdb.persistence.entity.DriverLevel.HIGH_CONFIDENCE_DRIVER;
import static es.uvigo.ei.sing.pandrugsdb.persistence.entity.TumorPortalMutationLevel.HIGHLY_SIGNIFICANTLY_MUTATED;
import static es.uvigo.ei.sing.pandrugsdb.persistence.entity.TumorPortalMutationLevel.NEAR_SIGNIFICANCE;
import static es.uvigo.ei.sing.pandrugsdb.persistence.entity.TumorPortalMutationLevel.SIGNIFICANTLY_MUTATED;
import static java.util.Arrays.asList;
import static java.util.Collections.emptyList;
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

import java.util.List;
import java.util.SortedMap;
import java.util.stream.Stream;

import org.easymock.EasyMockSupport;
import org.junit.After;
import org.junit.Test;

import com.google.common.collect.ImmutableSortedMap;

import es.uvigo.ei.sing.pandrugsdb.persistence.entity.DrugSource;
import es.uvigo.ei.sing.pandrugsdb.persistence.entity.DrugStatus;
import es.uvigo.ei.sing.pandrugsdb.persistence.entity.GeneDrug;
import es.uvigo.ei.sing.pandrugsdb.persistence.entity.GeneInformation;

public class GeneDrugGroupTest extends EasyMockSupport {
	@After
	public void tearDown() throws Exception {
		verifyAll();
	}

	@Test
	public void testConstructorSingleDirectGeneDrug() {
		final String[] genes = new String[] { "G1" };
		final List<GeneDrug> genesDrugs = asList(newGeneDrug("G1", "D1"));
		
		replayAll();
		
		new GeneDrugGroup(genes, genesDrugs);
	}

	@Test
	public void testConstructorMultipleDirectGeneDrug() {
		final String[] genes = new String[] { "G1", "G2", "G3" };
		final List<GeneDrug> geneDrugs = asList(
			newGeneDrug("G1", "D1"),
			newGeneDrug("G2", "D1"),
			newGeneDrug("G3", "D1", "IG1", "IG2")
		);
		
		replayAll();
		
		new GeneDrugGroup(genes, geneDrugs);
	}

	@Test
	public void testConstructorSingleIndirectGeneDrug() {
		final String[] genes = new String[] { "IG1" };
		final List<GeneDrug> geneDrugs = asList(newGeneDrug("G1", "D1", "IG1"));
		
		replayAll();
		
		new GeneDrugGroup(genes, geneDrugs);
	}

	@Test
	public void testConstructorMultipleIndirectGeneDrug() {
		final String[] genes = new String[] { "IG1", "IG2", "IG3" };
		final List<GeneDrug> geneDrugs = asList(
			newGeneDrug("G1", "D1", "IG1"),
			newGeneDrug("G2", "D1", "IG2"),
			newGeneDrug("G3", "D1", "IG1", "IG2")
		);
		
		replayAll();
		
		new GeneDrugGroup(genes, geneDrugs);
	}

	@Test
	public void testConstructorMultipleBothGeneDrug() {
		final String[] genes = new String[] { "G1", "G2", "IG1", "IG2" };
		final List<GeneDrug> geneDrugs = asList(
			newGeneDrug("G1", "D1"),
			newGeneDrug("G2", "D1", "IG200"),
			newGeneDrug("G3", "D1", "IG1"),
			newGeneDrug("G3", "D1", "IG100", "IG2")
		);
		
		replayAll();
		
		new GeneDrugGroup(genes, geneDrugs);
	}

	@Test(expected = IllegalArgumentException.class)
	public void testConstructorMissingGeneDrug() {
		final String[] genes = new String[] { "MG1" };
		final List<GeneDrug> geneDrugs = asList(newGeneDrug("G1", "D1"));
		
		replayAll();
		
		new GeneDrugGroup(genes, geneDrugs);
	}

	@Test(expected = IllegalArgumentException.class)
	public void testConstructorEmptyGeneDrug() {
		final String[] genes = new String[] { "G1" };
		final List<GeneDrug> geneDrugs = emptyList();
		
		replayAll();
		
		new GeneDrugGroup(genes, geneDrugs);
	}

	@Test(expected = NullPointerException.class)
	public void testConstructorNullGeneDrug() {
		final String[] genes = new String[] { "G1" };
		
		new GeneDrugGroup(genes, null);
	}

	@Test(expected = IllegalArgumentException.class)
	public void testConstructorEmptyGenes() {
		final List<GeneDrug> geneDrugs = asList(newGeneDrug("G1", "D1"));
		
		replayAll();
		
		new GeneDrugGroup(new String[0], geneDrugs);
	}

	@Test(expected = NullPointerException.class)
	public void testConstructorNullGenes() {
		final List<GeneDrug> geneDrugs = asList(newGeneDrug("G1", "D1"));

		replayAll();
		
		new GeneDrugGroup(null, geneDrugs);
	}

	@Test(expected = IllegalArgumentException.class)
	public void testConstructorDifferentDrugs() {
		final String[] genes = new String[] { "G1", "G2", "G3" };
		final List<GeneDrug> geneDrugs = asList(
			newGeneDrug("G1", "D1"),
			newGeneDrug("G2", "D1"),
			newGeneDrug("G3", "D2", "IG1", "IG2")
		);
		
		replayAll();
		
		new GeneDrugGroup(genes, geneDrugs);
	}

	@Test(expected = IllegalArgumentException.class)
	public void testConstructorDifferentStatus() {
		final String[] genes = new String[] { "G1", "G2" };
		final List<GeneDrug> geneDrugs = asList(
			newGeneDrug("G1", "D1", DrugStatus.APPROVED, null, null, null, null),
			newGeneDrug("G2", "D1", DrugStatus.CLINICAL, null, null, null, null)
		);
		
		replayAll();
		
		new GeneDrugGroup(genes, geneDrugs);
	}

	@Test(expected = IllegalArgumentException.class)
	public void testConstructorDifferentPathology() {
		final String[] genes = new String[] { "G1", "G2" };
		final List<GeneDrug> geneDrugs = asList(
			newGeneDrug("G1", "D1", null, "P1", null, null, null),
			newGeneDrug("G2", "D1", null, "P2", null, null, null)
		);
		
		replayAll();
		
		new GeneDrugGroup(genes, geneDrugs);
	}

	@Test(expected = IllegalArgumentException.class)
	public void testConstructorDifferentCancer() {
		final String[] genes = new String[] { "G1", "G2" };
		final List<GeneDrug> geneDrugs = asList(
			newGeneDrug("G1", "D1", null, null, "C1", null, null),
			newGeneDrug("G2", "D1", null, null, "C2", null, null)
		);
		
		replayAll();
		
		new GeneDrugGroup(genes, geneDrugs);
	}

	@Test(expected = IllegalArgumentException.class)
	public void testConstructorDifferentExtra() {
		final String[] genes = new String[] { "G1", "G2" };
		final List<GeneDrug> geneDrugs = asList(
			newGeneDrug("G1", "D1", null, null, null, "E1", null),
			newGeneDrug("G2", "D1", null, null, null, "E2", null)
		);
		
		replayAll();
		
		new GeneDrugGroup(genes, geneDrugs);
	}

	@Test
	public void testGetGeneDrugs() {
		final String[] targetGenes = new String[] { "G1", "G2", "IG1" };
		final GeneDrug[] geneDrugs = new GeneDrug[] {
			newGeneDrug("G1", "D1"),
			newGeneDrug("G2", "D1", "IG200"),
			newGeneDrug("G3", "D1", "IG1")
		};
		
		replayAll();
		
		final GeneDrugGroup group = new GeneDrugGroup(
			targetGenes, asList(geneDrugs)
		);
		
		assertThat(group.getGeneDrugs(), containsInAnyOrder(geneDrugs));
	}

	@Test
	public void testGetTargetGenes() {
		final String[] genes = new String[] { "G1", "G2", "IG1" };
		final List<GeneDrug> geneDrugs = asList(
			newGeneDrug("G1", "D1"),
			newGeneDrug("G2", "D1", "IG200"),
			newGeneDrug("G3", "D1", "IG1")
		);
		
		replayAll();
		
		final GeneDrugGroup group = new GeneDrugGroup(genes, geneDrugs);

		assertThat(group.getTargetGenes(), is(arrayContainingInAnyOrder(genes)));
	}

	@Test
	public void testGetDirectGenes() {
		final String[] genes = new String[] { "G1", "G2", "IG1" };
		final List<GeneDrug> geneDrugs = asList(
			newGeneDrug("G1", "D1"),
			newGeneDrug("G2", "D1", "IG200"),
			newGeneDrug("G3", "D1", "IG1")
		);
		
		replayAll();
		
		final GeneDrugGroup group = new GeneDrugGroup(genes, geneDrugs);

		assertThat(group.getDirectGenes(), is(arrayContaining("G1", "G2")));
	}

	@Test
	public void testGetIndirectGenes() {
		final String[] genes = new String[] { "G1", "G2", "IG1" };
		final List<GeneDrug> geneDrugs = asList(
			newGeneDrug("G1", "D1"),
			newGeneDrug("G2", "D1", "IG200"),
			newGeneDrug("G3", "D1", "IG1")
		);
		
		replayAll();
		
		final GeneDrugGroup group = new GeneDrugGroup(genes, geneDrugs);

		assertThat(group.getIndirectGenes(), is(arrayContaining("G3")));
	}

	@Test
	public void testIsDirect() {
		final GeneDrug directGeneDrug = newGeneDrug("G1", "D1");
		final String[] genes = new String[] { "G1", "G2", "IG1" };
		final List<GeneDrug> geneDrugs = asList(
			directGeneDrug,
			newGeneDrug("G2", "D1", "IG200"),
			newGeneDrug("G3", "D1", "IG1")
		);
		
		replayAll();
		
		final GeneDrugGroup group = new GeneDrugGroup(genes, geneDrugs);
		
		assertTrue(group.isDirect(directGeneDrug));
	}

	@Test
	public void testIsIndirect() {
		final GeneDrug indirectGeneDrug = newGeneDrug("G3", "D1", "IG1");
		final String[] genes = new String[] { "G1", "G2", "IG1" };
		final List<GeneDrug> geneDrugs = asList(
			newGeneDrug("G1", "D1"),
			newGeneDrug("G2", "D1", "IG200"),
			indirectGeneDrug
		);
		
		replayAll();
		
		final GeneDrugGroup group = new GeneDrugGroup(genes, geneDrugs);
		
		assertTrue(group.isIndirect(indirectGeneDrug));
	}

	@Test(expected = IllegalArgumentException.class)
	public void testIsDirectNotPresent() {
		final GeneDrug notPresentGD = newGeneDrug("GX", "DX");
		final String[] genes = new String[] { "G1", "G2", "IG1" };
		final List<GeneDrug> geneDrugs = asList(
			newGeneDrug("G1", "D1"),
			newGeneDrug("G2", "D1", "IG200"),
			newGeneDrug("G3", "D1", "IG1")
		);
		
		replayAll();
		
		final GeneDrugGroup group = new GeneDrugGroup(genes, geneDrugs);
		
		group.isDirect(notPresentGD);
	}

	@Test(expected = IllegalArgumentException.class)
	public void testIsIndirectNotPresent() {
		final GeneDrug indirectGeneDrug = newGeneDrug("G3", "D1", "IG1");
		final GeneDrug notPresentGD = newGeneDrug("GX", "DX");
		
		final String[] genes = new String[] { "G1", "G2", "IG1" };
		final List<GeneDrug> geneDrugs = asList(
			newGeneDrug("G1", "D1"),
			newGeneDrug("G2", "D1", "IG200"),
			indirectGeneDrug
		);
		
		replayAll();
		
		final GeneDrugGroup group = new GeneDrugGroup(genes, geneDrugs);
		
		group.isDirect(notPresentGD);
	}

	@Test
	public void testCountTargetGenes() {
		final String[] genes = new String[] { "G1", "G2", "IG1" };
		final List<GeneDrug> geneDrugs = asList(
			newGeneDrug("G1", "D1"),
			newGeneDrug("G2", "D1", "IG200"),
			newGeneDrug("G3", "D1", "IG1")
		);
		
		replayAll();
		
		final GeneDrugGroup group = new GeneDrugGroup(genes, geneDrugs);
		
		assertThat(group.countTargetGenes(), is(3));
	}

	@Test
	public void testCountDirectGenes() {
		final String[] genes = new String[] { "G1", "G2", "IG1" };
		final List<GeneDrug> geneDrugs = asList(
			newGeneDrug("G1", "D1"),
			newGeneDrug("G2", "D1", "IG200"),
			newGeneDrug("G3", "D1", "IG1")
		);
		
		replayAll();
		
		final GeneDrugGroup group = new GeneDrugGroup(genes, geneDrugs);

		assertThat(group.countDirectGenes(), is(2));
	}

	@Test
	public void testCountIndirectGenes() {
		final String[] genes = new String[] { "G1", "G2", "IG1" };
		final List<GeneDrug> geneDrugs = asList(
			newGeneDrug("G1", "D1"),
			newGeneDrug("G2", "D1", "IG200"),
			newGeneDrug("G3", "D1", "IG1")
		);
		
		replayAll();
		
		final GeneDrugGroup group = new GeneDrugGroup(genes, geneDrugs);

		assertThat(group.countIndirectGenes(), is(1));
	}

	@Test
	public void testIsOnlyIndirectDirect() {
		final String[] genes = new String[] { "G1", "G2", "G3" };
		final List<GeneDrug> geneDrugs = asList(
			newGeneDrug("G1", "D1"),
			newGeneDrug("G2", "D1"),
			newGeneDrug("G3", "D1", "IG1", "IG2")
		);
		
		replayAll();
		
		final GeneDrugGroup group = new GeneDrugGroup(genes, geneDrugs);
		
		assertFalse(group.isOnlyIndirect());
	}

	@Test
	public void testIsOnlyIndirectIndirect() {
		final String[] genes = new String[] { "IG1", "IG2", "IG3" };
		final List<GeneDrug> geneDrugs = asList(
			newGeneDrug("G1", "D1", "IG1"),
			newGeneDrug("G2", "D1", "IG2"),
			newGeneDrug("G3", "D1", "IG1", "IG2")
		);
		
		replayAll();
		
		final GeneDrugGroup group = new GeneDrugGroup(genes, geneDrugs);
		
		assertTrue(group.isOnlyIndirect());
	}

	@Test
	public void testIsOnlyIndirectBoth() {
		final String[] genes = new String[] { "G1", "G2", "IG1", "IG2" };
		final List<GeneDrug> geneDrugs = asList(
			newGeneDrug("G1", "D1"),
			newGeneDrug("G2", "D1", "IG200"),
			newGeneDrug("G3", "D1", "IG1"),
			newGeneDrug("G3", "D1", "IG100", "IG2")
		);
		
		replayAll();
		
		final GeneDrugGroup group = new GeneDrugGroup(genes, geneDrugs);
		
		assertFalse(group.isOnlyIndirect());
	}

	@Test
	public void testGetDrug() {
		final String[] genes = new String[] { "G1", "G2", "IG1", "IG2" };
		final List<GeneDrug> geneDrugs = asList(
			newGeneDrug("G1", "D1"),
			newGeneDrug("G2", "D1", "IG200"),
			newGeneDrug("G3", "D1", "IG1"),
			newGeneDrug("G3", "D1", "IG100", "IG2")
		);
		
		replayAll();
		
		final GeneDrugGroup group = new GeneDrugGroup(genes, geneDrugs);
		
		assertThat(group.getDrug(), is("D1"));
	}

	@Test
	public void testGetStatus() {
		final DrugStatus status = DrugStatus.APPROVED;
		final String[] genes = new String[] { "G1", "G2" };
		final List<GeneDrug> geneDrugs = asList(
			newGeneDrug("G1", "D1", status, null, null, null, null),
			newGeneDrug("G2", "D1", status, null, null, null, null)
		);
		
		replayAll();
		
		final GeneDrugGroup group = new GeneDrugGroup(genes, geneDrugs);
		
		assertThat(group.getStatus(), is(status));
	}

	@Test
	public void testGetCancer() {
		final String cancer = "C1";
		final String[] genes = new String[] { "G1", "G2" };
		final List<GeneDrug> geneDrugs = asList(
			newGeneDrug("G1", "D1", null, null, cancer, null, null),
			newGeneDrug("G2", "D1", null, null, cancer, null, null)
		);
		
		replayAll();
		
		final GeneDrugGroup group = new GeneDrugGroup(genes, geneDrugs);
		
		assertThat(group.getCancer(), is(cancer));
	}

	@Test
	public void testGetExtra() {
		final String extra = "E1";
		final String[] genes = new String[] { "G1", "G2" };
		final List<GeneDrug> geneDrugs = asList(
			newGeneDrug("G1", "D1", null, null, null, extra, null),
			newGeneDrug("G2", "D1", null, null, null, extra, null)
		);
		
		replayAll();
		
		final GeneDrugGroup group = new GeneDrugGroup(genes, geneDrugs);
		
		assertThat(group.getExtra(), is(extra));
	}

	@Test
	public void testGetFamily() {
		final String[] genes = new String[] { "G1", "G2", "G3" };
		final List<GeneDrug> geneDrugs = asList(
			newGeneDrug("G1", "D1", null, null, null, null, "F1"),
			newGeneDrug("G2", "D1", null, null, null, null, "F2"),
			newGeneDrug("G3", "D1", null, null, null, null, "F1")
		);
		
		replayAll();
		
		final GeneDrugGroup group = new GeneDrugGroup(genes, geneDrugs);
		
		assertThat(group.getFamilies(), arrayContaining("F1", "F2"));
	}

	@Test
	public void testGetSources() {
		final DrugSource ds1 = newDrugSource("DS1");
		final DrugSource ds2 = newDrugSource("DS2");
		final DrugSource ds3 = newDrugSource("DS3");
		
		final GeneDrug gd1 = newGeneDrugWithSources("G1", "D1", ds1);
		final GeneDrug gd2 = newGeneDrugWithSources("G2", "D1", ds2);
		final GeneDrug gd3 = newGeneDrugWithSources("G3", "D1", ds1, ds3);
		
		replayAll();
		
		final GeneDrugGroup geneDrugGroup = new GeneDrugGroup(
			new String[] { "G1", "G2", "G3" },
			asList(gd1, gd2, gd3)
		);
		
		assertThat(geneDrugGroup.getSources(), is(arrayContainingInAnyOrder(ds1, ds2, ds3)));
	}

	@Test
	public void testGetSourceNames() {
		final GeneDrug gd1 = newGeneDrugWithSources("G1", "D1", "DS1");
		final GeneDrug gd2 = newGeneDrugWithSources("G2", "D1", "DS2");
		final GeneDrug gd3 = newGeneDrugWithSources("G3", "D1", "DS3", "DS1");
		
		replayAll();
		
		final GeneDrugGroup geneDrugGroup = new GeneDrugGroup(
			new String[] { "G1", "G2", "G3" },
			asList(gd1, gd2, gd3)
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
		
		final GeneDrug gd1 = newGeneDrugWithSources("G1", "D1", ds1);
		final GeneDrug gd2 = newGeneDrugWithSources("G2", "D1", ds2);
		final GeneDrug gd3 = newGeneDrugWithSources("G3", "D1", ds1, ds3);
		
		final SortedMap<String, String> expectedLinks = ImmutableSortedMap.of(
			"DS1", "DS1_URL", "DS2", "DS2_URL", "DS3", "DS3_URL"
		);
		
		replayAll();
		
		final GeneDrugGroup geneDrugGroup = new GeneDrugGroup(
			targetGenes,
			asList(gd1, gd2, gd3)
		);
		
		
		assertThat(geneDrugGroup.getSourceLinks(), is(expectedLinks));
	}

	@Test
	public void testGetCuratedSources() {
		final DrugSource ds1 = newDrugSource("DS1", true);
		final DrugSource ds2 = newDrugSource("DS2", false);
		final DrugSource ds3 = newDrugSource("DS3", true);
		
		final GeneDrug gd1 = newGeneDrugWithSources("G1", "D1", ds1);
		final GeneDrug gd2 = newGeneDrugWithSources("G2", "D1", ds2);
		final GeneDrug gd3 = newGeneDrugWithSources("G3", "D1", ds1, ds3);
		
		replayAll();
		
		final GeneDrugGroup geneDrugGroup = new GeneDrugGroup(
			new String[] { "G1", "G2", "G3" },
			asList(gd1, gd2, gd3)
		);
		
		assertThat(geneDrugGroup.getCuratedSources(),
			is(arrayContainingInAnyOrder(ds1, ds3)));
	}

	@Test
	public void testGetCuratedSourcesEmpty() {
		final DrugSource ds1 = newDrugSource("DS1", false);
		final DrugSource ds2 = newDrugSource("DS2", false);
		final DrugSource ds3 = newDrugSource("DS3", false);
		
		final GeneDrug gd1 = newGeneDrugWithSources("G1", "D1", ds1);
		final GeneDrug gd2 = newGeneDrugWithSources("G2", "D1", ds2);
		final GeneDrug gd3 = newGeneDrugWithSources("G3", "D1", ds1, ds3);
		
		replayAll();
		
		final GeneDrugGroup geneDrugGroup = new GeneDrugGroup(
			new String[] { "G1", "G2", "G3" },
			asList(gd1, gd2, gd3)
		);
		
		assertThat(geneDrugGroup.getCuratedSources(),
			is(emptyArray()));
	}

	@Test
	public void testGetCuratedSourceNames() {
		final DrugSource ds1 = newDrugSource("DS1", true);
		final DrugSource ds2 = newDrugSource("DS2", false);
		final DrugSource ds3 = newDrugSource("DS3", true);
		
		final GeneDrug gd1 = newGeneDrugWithSources("G1", "D1", ds1);
		final GeneDrug gd2 = newGeneDrugWithSources("G2", "D1", ds2);
		final GeneDrug gd3 = newGeneDrugWithSources("G3", "D1", ds1, ds3);
		
		replayAll();
		
		final GeneDrugGroup geneDrugGroup = new GeneDrugGroup(
			new String[] { "G1", "G2", "G3" },
			asList(gd1, gd2, gd3)
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
		
		final GeneDrug gd1 = newGeneDrugWithSources("G1", "D1", ds1);
		final GeneDrug gd2 = newGeneDrugWithSources("G2", "D1", ds2);
		final GeneDrug gd3 = newGeneDrugWithSources("G3", "D1", ds1, ds3);
		
		replayAll();
		
		final GeneDrugGroup geneDrugGroup = new GeneDrugGroup(
			new String[] { "G1", "G2", "G3" },
			asList(gd1, gd2, gd3)
		);
		
		assertThat(geneDrugGroup.getCuratedSourceNames(),
			is(emptyArray()));
	}
	
	@Test
	public void testIsTarget() {
		final GeneDrug gd1 = newGeneDrug("G1", "D1", true);
		final GeneDrug gd2 = newGeneDrug("G2", "D1", false);
		final GeneDrug gd3 = newGeneDrug("G3", "D1", false);
		
		replayAll();
		
		final GeneDrugGroup geneDrugGroup = new GeneDrugGroup(
			new String[] { "G1", "G2", "G3" },
			asList(gd1, gd2, gd3)
		);
		
		assertTrue(geneDrugGroup.isTarget());
	}
	
	@Test
	public void testIsTargetFalse() {
		final GeneDrug gd1 = newGeneDrug("G1", "D1", false);
		final GeneDrug gd2 = newGeneDrug("G2", "D1", false);
		final GeneDrug gd3 = newGeneDrug("G3", "D1", false);
		
		replayAll();
		
		final GeneDrugGroup geneDrugGroup = new GeneDrugGroup(
			new String[] { "G1", "G2", "G3" },
			asList(gd1, gd2, gd3)
		);
		
		assertFalse(geneDrugGroup.isTarget());
	}

	@Test
	public void testTargetGeneNames() {
		final String[] genes = new String[] { "G1", "G2", "IG1", "IG2" };
		
		final GeneDrug gd1 = newGeneDrug("G1", "D1");
		final GeneDrug gd2 = newGeneDrug("G2", "D1", "IG200");
		final GeneDrug gd3 = newGeneDrug("G3", "D1", "IG1", "IG2");
		
		final List<GeneDrug> geneDrugs = asList(gd1, gd2, gd3);
		
		replayAll();
		
		final GeneDrugGroup group = new GeneDrugGroup(genes, geneDrugs);
		
		assertThat(group.getTargetGeneNames(gd1), is(arrayContaining("G1")));
		assertThat(group.getTargetGeneNames(gd2), is(arrayContaining("G2")));
		assertThat(group.getTargetGeneNames(gd3), is(arrayContaining("IG1", "IG2")));
	}

	@Test
	public void testIndirectGeneNames() {
		final String[] genes = new String[] { "G1", "G2", "IG1", "IG2" };
		
		final GeneDrug gd1 = newGeneDrug("G1", "D1");
		final GeneDrug gd2 = newGeneDrug("G2", "D1", "IG200");
		final GeneDrug gd3 = newGeneDrug("G3", "D1", "IG1", "IG2");
		
		final List<GeneDrug> geneDrugs = asList(gd1, gd2, gd3);
		
		replayAll();
		
		final GeneDrugGroup group = new GeneDrugGroup(genes, geneDrugs);
		
		assertThat(group.getIndirectGeneName(gd1), is(nullValue()));
		assertThat(group.getIndirectGeneName(gd2), is(nullValue()));
		assertThat(group.getIndirectGeneName(gd3), is("G3"));
	}
	
	@Test
	public void testGScore() {
		final String[] genes = new String[] { "G_LOW", "G_MED", "G_HIGH" };
		
		final GeneInformation giLow = 
			new GeneInformation("G_LOW", NEAR_SIGNIFICANCE, false, CANDIDATE_DRIVER, 0.125d);
		final GeneInformation giMedium = 
			new GeneInformation("G_MED", SIGNIFICANTLY_MUTATED, true, CANDIDATE_DRIVER, 0.5d);
		final GeneInformation giHigh = 
			new GeneInformation("G_HIGH", HIGHLY_SIGNIFICANTLY_MUTATED, true, HIGH_CONFIDENCE_DRIVER, 1d);
		
		final List<GeneDrug> geneDrugs = asList(
			newGeneDrug("G_LOW", "D1", giLow),
			newGeneDrug("G_HIGH", "D1", giHigh),
			newGeneDrug("G_MED", "D1", giMedium)
		);
		
		replayAll();
		
		final GeneDrugGroup group = new GeneDrugGroup(genes, geneDrugs);
		
		assertThat(group.getGScore(), is(1d));
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

	private final GeneDrug newGeneDrug(
		String gene, String drug, String ... indirect
	) {
		final GeneDrug gd = createNiceMock(GeneDrug.class);
		expect(gd.getGeneSymbol()).andReturn(gene).anyTimes();
		expect(gd.getStandardDrugName()).andReturn(drug).anyTimes();
		expect(gd.getIndirectGenes()).andReturn(asList(indirect)).anyTimes();
		
		return gd;
	}

	private final GeneDrug newGeneDrug(
		String gene, String drug, boolean isTarget
	) {
		final GeneDrug gd = createNiceMock(GeneDrug.class);
		expect(gd.getGeneSymbol()).andReturn(gene).anyTimes();
		expect(gd.getStandardDrugName()).andReturn(drug).anyTimes();
		expect(gd.isTarget()).andReturn(isTarget).anyTimes();
		
		return gd;
	}

	private final GeneDrug newGeneDrug(
		String gene, String drug,
		DrugStatus status,
		String pathology,
		String cancer,
		String extra,
		String family
	) {
		final GeneDrug gd = createNiceMock(GeneDrug.class);
		expect(gd.getGeneSymbol()).andReturn(gene).anyTimes();
		expect(gd.getStandardDrugName()).andReturn(drug).anyTimes();
		expect(gd.getStatus()).andReturn(status).anyTimes();
		expect(gd.getPathology()).andReturn(pathology).anyTimes();
		expect(gd.getCancer()).andReturn(cancer).anyTimes();
		expect(gd.getExtra()).andReturn(extra).anyTimes();
		expect(gd.getFamily()).andReturn(family).anyTimes();
		
		return gd;
	}

	private final GeneDrug newGeneDrug(
		String gene, String drug, GeneInformation geneInfo
	) {
		final GeneDrug gd = createNiceMock(GeneDrug.class);
		expect(gd.getGeneSymbol()).andReturn(gene).anyTimes();
		expect(gd.getStandardDrugName()).andReturn(drug).anyTimes();
		expect(gd.getGeneInformation()).andReturn(geneInfo).anyTimes();
		
		return gd;
	}

	private final GeneDrug newGeneDrugWithSources(
		String gene, String drug, String ... sourceNames
	) {
		final DrugSource[] drugSources = Stream.of(sourceNames)
			.map(this::newDrugSource)
		.toArray(DrugSource[]::new);
		
		return newGeneDrugWithSources(gene, drug, drugSources);
	}
	
	private final GeneDrug newGeneDrugWithSources(
		String gene, String drug, DrugSource ... drugSources
	) {
		final GeneDrug gd = createNiceMock(GeneDrug.class);
		expect(gd.getGeneSymbol()).andReturn(gene).anyTimes();
		expect(gd.getStandardDrugName()).andReturn(drug).anyTimes();
		expect(gd.getDrugSources()).andReturn(asList(drugSources)).anyTimes();
		
		return gd;
	}
}
