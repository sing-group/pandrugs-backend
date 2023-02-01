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

package es.uvigo.ei.sing.pandrugs.persistence.dao;

import static es.uvigo.ei.sing.pandrugs.matcher.hamcrest.IsEqualToClinicalGenomeVariation.containsClinicalGenomeVariations;
import static es.uvigo.ei.sing.pandrugs.matcher.hamcrest.IsEqualToClinicalGenomeVariation.equalToClinicalGenomeVariation;
import static es.uvigo.ei.sing.pandrugs.persistence.entity.ClinicalGenomeVariationDataset.withChromosomeLocation;
import static es.uvigo.ei.sing.pandrugs.persistence.entity.ClinicalGenomeVariationDataset.withDbSnp;
import static es.uvigo.ei.sing.pandrugs.persistence.entity.ClinicalGenomeVariationDataset.withId;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.collection.IsEmptyCollection.empty;
import static org.junit.Assert.assertThat;

import javax.inject.Inject;
import javax.inject.Named;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.DependencyInjectionTestExecutionListener;
import org.springframework.transaction.annotation.Transactional;

import com.github.springtestdbunit.TransactionDbUnitTestExecutionListener;
import com.github.springtestdbunit.annotation.DatabaseSetup;
import com.github.springtestdbunit.annotation.ExpectedDatabase;
import com.github.springtestdbunit.assertion.DatabaseAssertionMode;

import es.uvigo.ei.sing.pandrugs.persistence.entity.ClinicalGenomeVariationId;

@RunWith(SpringJUnit4ClassRunner.class)
@Transactional
@ContextConfiguration("file:src/test/resources/META-INF/applicationTestContext.xml")
@TestExecutionListeners({
	DependencyInjectionTestExecutionListener.class,
	TransactionDbUnitTestExecutionListener.class
})
@DatabaseSetup("file:src/test/resources/META-INF/dataset.clinvar.xml")
@ExpectedDatabase(
	value = "file:src/test/resources/META-INF/dataset.clinvar.xml",
	assertionMode = DatabaseAssertionMode.NON_STRICT_UNORDERED
)
public class DefaultClinicalGenomeVariationDAOTest {
	@Inject
	@Named("defaultClinicalGenomeVariationDAO")
	private ClinicalGenomeVariationDAO dao;

	@Test
	public void testGetById() {
		final String chromosome = "1";
		final int start = 123456789;
		final int end = 123456789;
		final String hgvs = "c.321G>A";
		final String disease = "Disease 1";
		final String accession = "ACC00001";
		
		assertThat(
			dao.get(new ClinicalGenomeVariationId(chromosome, start, end, hgvs, disease, accession)),
			is(equalToClinicalGenomeVariation(withId(chromosome, start, end, hgvs, disease, accession)))
		);
	}

	@Test
	public void testGetByIdMissing() {
		assertThat(dao.get(new ClinicalGenomeVariationId("10", 1, 1, "X", "X", "X")), is(nullValue()));
	}

	@Test
	public void testListByDbSnpNoResults() {
		assertThat(dao.listByDbSnp("XXX"), is(empty()));
	}

	@Test
	public void testListByDbSnpSingleResult() {
		final String dbSnp = "rs1000000";
		
		assertThat(dao.listByDbSnp(dbSnp), containsClinicalGenomeVariations(withDbSnp(dbSnp)));
	}

	@Test
	public void testListByDbSnpMultipleResults() {
		final String dbSnp = "rs2000000";
		
		assertThat(dao.listByDbSnp(dbSnp), containsClinicalGenomeVariations(withDbSnp(dbSnp)));
	}

	@Test
	public void testListByChromosomePositionNoResults() {
		assertThat(dao.listByChromosomePosition("10", 1, 1), is(empty()));
	}

	@Test
	public void testListByChromosomePositionSingleResult() {
		final String chr = "2";
		final int start = 222222222;
		final int end = 222222223;
		
		assertThat(
			dao.listByChromosomePosition(chr, start, end),
			containsClinicalGenomeVariations(withChromosomeLocation(chr, start, end))
		);
	}

	@Test
	public void testListByChromosomePositionMultipleResults() {
		final String chr = "1";
		final int start = 123456789;
		final int end = 123456789;
		
		assertThat(
			dao.listByChromosomePosition(chr, start, end),
			containsClinicalGenomeVariations(withChromosomeLocation(chr, start, end))
		);
	}

}
