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
package es.uvigo.ei.sing.pandrugs.persistence.dao;

import static es.uvigo.ei.sing.pandrugs.matcher.hamcrest.IsEqualToPrincipalSpliceIsoform.equalPrincipalSpliceIsoform;
import static es.uvigo.ei.sing.pandrugs.persistence.entity.PrincipalSpliceIsoformDataset.withId;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.nullValue;
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

@RunWith(SpringJUnit4ClassRunner.class)
@Transactional
@ContextConfiguration("file:src/test/resources/META-INF/applicationTestContext.xml")
@TestExecutionListeners({
	DependencyInjectionTestExecutionListener.class,
	TransactionDbUnitTestExecutionListener.class
})
@DatabaseSetup("file:src/test/resources/META-INF/dataset.psi.xml")
@ExpectedDatabase(
	value = "file:src/test/resources/META-INF/dataset.psi.xml",
	assertionMode = DatabaseAssertionMode.NON_STRICT_UNORDERED
)
public class DefaultPrincipalSpliceIsoformDAOTest {
	@Inject
	@Named("defaultPrincipalSpliceIsoformDAO")
	private PrincipalSpliceIsoformDAO dao;

	@Test
	public void testGetById() {
		final String[] ids = new String[] { "ENST00000366621", "ENST00000458013", "ENST00000371320" };
		
		for (String id : ids) {
			assertThat(dao.get(id), is(equalPrincipalSpliceIsoform(withId(id))));
		}
	}

	@Test
	public void testGetByNonexistentId() {
		final String[] ids = new String[] { "XXXX00000366621", "XXXX00000458013", "XXXX00000371320" };
		
		for (String id : ids) {
			assertThat(dao.get(id), is(nullValue()));
		}
	}
}
