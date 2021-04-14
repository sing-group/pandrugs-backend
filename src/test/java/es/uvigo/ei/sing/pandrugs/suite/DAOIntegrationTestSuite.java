/*-
 * #%L
 * PanDrugs Backend
 * %%
 * Copyright (C) 2015 - 2021 Fátima Al-Shahrour, Elena Piñeiro, Daniel Glez-Peña
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

package es.uvigo.ei.sing.pandrugs.suite;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

import es.uvigo.ei.sing.pandrugs.persistence.dao.DefaultClinicalGenomeVariationDAOTest;
import es.uvigo.ei.sing.pandrugs.persistence.dao.DefaultGeneDAOIntegrationTest;
import es.uvigo.ei.sing.pandrugs.persistence.dao.DefaultGeneDrugDAOIntegrationTest;
import es.uvigo.ei.sing.pandrugs.persistence.dao.DefaultPrincipalSpliceIsoformDAOTest;
import es.uvigo.ei.sing.pandrugs.persistence.dao.DefaultRegistrationDAOIntegrationTest;
import es.uvigo.ei.sing.pandrugs.persistence.dao.DefaultSomaticMutationInCancerDAOTest;
import es.uvigo.ei.sing.pandrugs.persistence.dao.DefaultUserDAOIntegrationTest;

@SuiteClasses({
	DefaultRegistrationDAOIntegrationTest.class,
	DefaultUserDAOIntegrationTest.class,
	DefaultGeneDrugDAOIntegrationTest.class,
	DefaultGeneDAOIntegrationTest.class,
	DefaultClinicalGenomeVariationDAOTest.class,
	DefaultPrincipalSpliceIsoformDAOTest.class,
	DefaultSomaticMutationInCancerDAOTest.class
})
@RunWith(Suite.class)
public class DAOIntegrationTestSuite {
}
