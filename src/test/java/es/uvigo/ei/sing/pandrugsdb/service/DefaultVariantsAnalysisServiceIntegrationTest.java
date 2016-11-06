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
package es.uvigo.ei.sing.pandrugsdb.service;

import com.github.springtestdbunit.DbUnitTestExecutionListener;
import com.github.springtestdbunit.annotation.DatabaseOperation;
import com.github.springtestdbunit.annotation.DatabaseSetup;
import com.github.springtestdbunit.annotation.DatabaseTearDown;
import es.uvigo.ei.sing.pandrugsdb.TestServletContext;
import es.uvigo.ei.sing.pandrugsdb.controller.DefaultVariantsAnalysisControllerIntegrationTest;
import es.uvigo.ei.sing.pandrugsdb.core.variantsanalysis.DefaultVEPConfiguration;
import es.uvigo.ei.sing.pandrugsdb.persistence.entity.RoleType;
import es.uvigo.ei.sing.pandrugsdb.persistence.entity.User;
import es.uvigo.ei.sing.pandrugsdb.service.entity.ComputationStatusMetadata;
import es.uvigo.ei.sing.pandrugsdb.service.entity.UserLogin;
import es.uvigo.ei.sing.pandrugsdb.service.security.SecurityContextStub;
import org.apache.commons.io.FileUtils;
import org.easymock.Capture;
import org.easymock.EasyMock;
import org.hamcrest.CoreMatchers;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.DependencyInjectionTestExecutionListener;
import org.springframework.test.context.support.DirtiesContextTestExecutionListener;

import javax.inject.Inject;
import javax.inject.Named;
import javax.servlet.ServletContext;
import javax.ws.rs.ForbiddenException;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriBuilder;
import javax.ws.rs.core.UriInfo;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;

import static es.uvigo.ei.sing.pandrugsdb.persistence.entity.UserDataset.*;
import static org.hamcrest.CoreMatchers.instanceOf;
import static org.junit.Assert.assertThat;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("file:src/test/resources/META-INF/applicationTestContext.xml")
@TestExecutionListeners({
		DependencyInjectionTestExecutionListener.class,
		DirtiesContextTestExecutionListener.class,
		DbUnitTestExecutionListener.class
})
@DirtiesContext
@DatabaseSetup(value = {
		"file:src/test/resources/META-INF/dataset.user.xml",
		"file:src/test/resources/META-INF/dataset.variantanalysis.xml"
})
@DatabaseTearDown(value = "file:src/test/resources/META-INF/dataset.variantanalysis.xml",
		type = DatabaseOperation.DELETE_ALL)
public class DefaultVariantsAnalysisServiceIntegrationTest {
	@BeforeClass
	public static void initContext() {
		TestServletContext.INIT_PARAMETERS.put("user.data.directory", System.getProperty("java.io.tmpdir"));
		TestServletContext.INIT_PARAMETERS.put(DefaultVEPConfiguration.VEP_COMMAND_TEMPLATE_PARAMETER,"touch %s %s");
		new TestServletContext();
	}

	@Inject
	@Named("defaultVariantsAnalysisService")
	private VariantsAnalysisService service;

	@Test(expected = ForbiddenException.class)
	public void testCreateComputationForOtherUser() {
		final User accessingUser = presentUser();
		final User targetUser = presentUser2();

		testCreateComputation(accessingUser, targetUser);
	}

	@Test
	public void testCreateComputationForPresentUser() {
		final User accessingUser = presentUser();

		testCreateComputation(accessingUser, accessingUser);
	}

	@Test(expected = ForbiddenException.class)
	public void testGetComputationStatusForOtherUser() {
		final User accesingUser = users()[0];
		final User targetUser = presentUser2();

		testGetComputationStatus(1, accesingUser, targetUser);
	}
	@Test(expected = ForbiddenException.class)
	public void testGetComputationStatusOfAnotherUserForPresentUser() {
		final User accesingUser = presentUser2();

		//computation id=1 is not owned by presentUser2()
		testGetComputationStatus(1, accesingUser, accesingUser);
	}

	@Test
	public void testGetComputationStatusForPresentUser() {
		final User accesingUser = users()[0];

		//computation id=2 is not owned by presentUser2()
		testGetComputationStatus(1, accesingUser, accesingUser);
	}

	private void testGetComputationStatus(Integer computationId, User accesingUser, User targetUser) {
		this.testGetComputationStatus(computationId, accesingUser.getLogin(), accesingUser.getRole(), targetUser);
	}

	private void testGetComputationStatus(Integer computationId, String accessLogin, RoleType role, User targetUser) {
		final String login = targetUser.getLogin();

		final SecurityContextStub security = new SecurityContextStub(users(), accessLogin);

		Response response = service.getComputationStatus(new UserLogin(login), computationId, security);

		assertThat(response.getEntity(), instanceOf(ComputationStatusMetadata.class));
	}

	private void testCreateComputation(User accessingUser, User targetUser) {
		this.testCreateComputation(accessingUser.getLogin(), accessingUser.getRole(), targetUser);
	}

	private void testCreateComputation(String accessLogin, RoleType role, User targetUser) {
		final String login = targetUser.getLogin();

		final SecurityContextStub security = new SecurityContextStub(users(), accessLogin);

		final UriInfo currentUri = EasyMock.createNiceMock(UriInfo.class);
		final UriBuilder uriBuilder = EasyMock.createNiceMock(UriBuilder.class);
		Capture<String> capture = EasyMock.newCapture();

		EasyMock.expect(currentUri.getAbsolutePathBuilder()).andReturn(uriBuilder);
		EasyMock.expect(uriBuilder.path(EasyMock.capture(capture))).andReturn(uriBuilder);
		EasyMock.expect(uriBuilder.build()).andAnswer(() -> new URI(
						"http://testhost/api/variantsanalysis/"+login+capture.getValue()));

		EasyMock.replay(currentUri);
		EasyMock.replay(uriBuilder);
		Response response = service.startVariantsScoreUserComputation(new UserLogin(login), emptyInputStream(),
				security,
				currentUri
				);

		assertThat(response.getStatus(), CoreMatchers.is(201));

		try {
			assertThat(response.getLocation(), CoreMatchers.equalTo(new URI(
					"http://testhost/api/variantsanalysis/"+login+capture.getValue())));
		} catch (URISyntaxException e) {
			throw new RuntimeException(e);
		}
	}

	private InputStream emptyInputStream() {
		return new InputStream() {
			@Override
			public int read() throws IOException {
				return -1;
			}
		};
	}
}
