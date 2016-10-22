/*
 * #%L
 * PanDrugsDB Backend
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
package es.uvigo.ei.sing.pandrugsdb.core.variantsanalysis;

import org.easymock.EasyMockRunner;
import org.easymock.Mock;
import org.easymock.TestSubject;
import org.junit.Test;
import org.junit.runner.RunWith;

import javax.servlet.ServletContext;
import java.io.File;

import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.replay;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

@RunWith(EasyMockRunner.class)
public class DefaultFileSystemConfigurationUnitTest {

	@TestSubject
	private FileSystemConfiguration fileSystemConfiguration =
			new DefaultFileSystemConfiguration();

	@Mock
	private ServletContext context;

	@Test(expected = NullPointerException.class)
	public void testNoUserDirIsConfigured() {
		setContextUserDataDirTo(null);

		fileSystemConfiguration.getUserDataBaseDirectory();
	}

	@Test
	public void testUserDataBaseDirectory() {
		setContextUserDataDirTo(System.getProperty("java.io.tmpdir"));

		assertThat(
				fileSystemConfiguration.getUserDataBaseDirectory(),
				is(new File(System.getProperty("java.io.tmpdir")))
		);
	}

	private void setContextUserDataDirTo(String value) {
		expect(context.getInitParameter(DefaultFileSystemConfiguration.USER_DATA_DIRECTORY_PARAMETER))
				.andReturn(value);
		replay(context);
	}
}
