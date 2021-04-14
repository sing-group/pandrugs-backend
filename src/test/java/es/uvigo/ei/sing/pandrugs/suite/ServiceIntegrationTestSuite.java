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

import es.uvigo.ei.sing.pandrugs.service.DefaultCancerServiceIntegrationTest;
import es.uvigo.ei.sing.pandrugs.service.DefaultGeneDrugServiceIntegrationTest;
import es.uvigo.ei.sing.pandrugs.service.DefaultGeneServiceIntegrationTest;
import es.uvigo.ei.sing.pandrugs.service.DefaultRegistrationServiceIntegrationTest;
import es.uvigo.ei.sing.pandrugs.service.DefaultSessionServiceIntegrationTest;
import es.uvigo.ei.sing.pandrugs.service.DefaultUserServiceIntegrationTest;
import es.uvigo.ei.sing.pandrugs.service.DefaultVariantsAnalysisServiceIntegrationTest;

@RunWith(Suite.class)
@SuiteClasses({
	DefaultSessionServiceIntegrationTest.class,
	DefaultRegistrationServiceIntegrationTest.class,
	DefaultUserServiceIntegrationTest.class,
	DefaultGeneDrugServiceIntegrationTest.class,
	DefaultGeneServiceIntegrationTest.class,
	DefaultCancerServiceIntegrationTest.class,
	DefaultVariantsAnalysisServiceIntegrationTest.class
})
public class ServiceIntegrationTestSuite {
}
