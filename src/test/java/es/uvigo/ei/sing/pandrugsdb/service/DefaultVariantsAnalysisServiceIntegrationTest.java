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

import static es.uvigo.ei.sing.pandrugsdb.matcher.hamcrest.HasHttpStatus.hasCreatedStatus;
import static es.uvigo.ei.sing.pandrugsdb.matcher.hamcrest.HasHttpStatus.hasOkStatus;
import static es.uvigo.ei.sing.pandrugsdb.persistence.entity.UserDataset.presentUser;
import static es.uvigo.ei.sing.pandrugsdb.persistence.entity.UserDataset.presentUser2;
import static es.uvigo.ei.sing.pandrugsdb.persistence.entity.UserDataset.users;
import static es.uvigo.ei.sing.pandrugsdb.util.EmptyInputStream.emptyInputStream;
import static org.easymock.EasyMock.capture;
import static org.easymock.EasyMock.createNiceMock;
import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.newCapture;
import static org.easymock.EasyMock.replay;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.collection.IsMapWithSize.aMapWithSize;
import static org.junit.Assert.assertThat;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Map;
import java.util.UUID;

import javax.inject.Inject;
import javax.inject.Named;
import javax.ws.rs.ForbiddenException;
import javax.ws.rs.NotFoundException;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriBuilder;
import javax.ws.rs.core.UriInfo;

import org.easymock.Capture;
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
import org.springframework.transaction.annotation.Transactional;

import com.github.springtestdbunit.TransactionDbUnitTestExecutionListener;
import com.github.springtestdbunit.annotation.DatabaseOperation;
import com.github.springtestdbunit.annotation.DatabaseSetup;
import com.github.springtestdbunit.annotation.DatabaseTearDown;

import es.uvigo.ei.sing.pandrugsdb.TestServletContext;
import es.uvigo.ei.sing.pandrugsdb.core.variantsanalysis.DefaultVEPConfiguration;
import es.uvigo.ei.sing.pandrugsdb.persistence.entity.RoleType;
import es.uvigo.ei.sing.pandrugsdb.persistence.entity.User;
import es.uvigo.ei.sing.pandrugsdb.persistence.entity.VariantsScoreUserComputationDataset;
import es.uvigo.ei.sing.pandrugsdb.service.entity.ComputationMetadata;
import es.uvigo.ei.sing.pandrugsdb.service.entity.UserLogin;
import es.uvigo.ei.sing.pandrugsdb.service.security.SecurityContextStub;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("file:src/test/resources/META-INF/applicationTestContext.xml")
@TestExecutionListeners({
	DependencyInjectionTestExecutionListener.class,
	DirtiesContextTestExecutionListener.class,
	TransactionDbUnitTestExecutionListener.class
})
@DirtiesContext
@DatabaseSetup(value = {
	"file:src/test/resources/META-INF/dataset.user.xml",
	"file:src/test/resources/META-INF/dataset.variantanalysis.xml"
})
@Transactional //open transaction in view
@DatabaseTearDown(value = "file:src/test/resources/META-INF/dataset.variantanalysis.xml",
		type = DatabaseOperation.DELETE_ALL)
public class DefaultVariantsAnalysisServiceIntegrationTest {
	@BeforeClass
	public static void initContext() {
		TestServletContext.INIT_PARAMETERS.put("user.data.directory", System.getProperty("java.io.tmpdir"));
		TestServletContext.INIT_PARAMETERS.put(DefaultVEPConfiguration.VEP_COMMAND_TEMPLATE_PARAMETER,"touch %s %s");
		new TestServletContext();
	}

	@Before
	public void prepareComputationFilesStorage() throws IOException {
		String systemTmpDir = TestServletContext.INIT_PARAMETERS.get("user.data.directory");
		VariantsScoreUserComputationDataset.copyComputationFilesToDir(systemTmpDir);
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

	@Test
	public void testGetComputationStatusForPresentUser() {
		final User accesingUser = users()[0];

		//computation id=2 is not owned by presentUser2()
		testGetComputationStatus(1, accesingUser, accesingUser);
	}

	@Test(expected = NotFoundException.class)
	public void testGetUnexistentComputationStatusForPresentUser() {
		final User accesingUser = users()[0];

		//computation id=2 is not owned by presentUser2()
		testGetComputationStatus(99, accesingUser, accesingUser);
	}

	@Test(expected = ForbiddenException.class)
	public void testGetComputationStatusOfAnotherUserForPresentUser() {
		final User accesingUser = presentUser2();

		//computation id=1 is not owned by presentUser2()
		testGetComputationStatus(1, accesingUser, accesingUser);
	}

	@Test(expected = ForbiddenException.class)
	public void testGetComputationStatusForOtherUser() {
		final User accesingUser = users()[0];
		final User targetUser = presentUser2();

		testGetComputationStatus(1, accesingUser, targetUser);
	}

	@Test
	@SuppressWarnings("unchecked")
	public void testGetComputationsForUser() {
		User user = users()[0];
		final SecurityContextStub security = new SecurityContextStub(users(), user.getLogin());

		Response response = service.getComputationsForUser(new UserLogin(user.getLogin()), security);

		assertThat(response, hasOkStatus());
		assertThat(response.getEntity(), is(instanceOf(Map.class)));
		assertThat((Map<Integer, ComputationMetadata>) response.getEntity(), is(aMapWithSize(2)));
	}

	@Test(expected = ForbiddenException.class)
	public void testGetComputationsForOtherUser() {
		User user = users()[0];
		final SecurityContextStub security = new SecurityContextStub(users(), user.getLogin());

		service.getComputationsForUser(new UserLogin(users()[1].getLogin()), security);
	}

	@Test
	public void testDeleteComputation() {
		User user = users()[0];
		final SecurityContextStub security = new SecurityContextStub(users(), user.getLogin());

		service.deleteComputation(new UserLogin(user.getLogin()), 2, security);
	}

	@Test(expected = ForbiddenException.class)
	public void testDeleteComputationOfOtherUser() {
		User user = users()[0];
		final SecurityContextStub security = new SecurityContextStub(users(), user.getLogin());

		service.deleteComputation(new UserLogin(users()[1].getLogin()), 2, security);
	}

	@Test(expected = NotFoundException.class)
	public void testDeleteUnexistentComputation() {
		User user = users()[0];
		final SecurityContextStub security = new SecurityContextStub(users(), user.getLogin());

		service.deleteComputation(new UserLogin(user.getLogin()), 99, security);
	}

	private void testGetComputationStatus(Integer computationId, User accesingUser, User targetUser) {
		this.testGetComputationStatus(computationId, accesingUser.getLogin(), accesingUser.getRole(), targetUser);
	}

	private void testGetComputationStatus(Integer computationId, String accessLogin, RoleType role, User targetUser) {
		final String login = targetUser.getLogin();

		final SecurityContextStub security = new SecurityContextStub(users(), accessLogin);

		final Response response = service.getComputationStatus(new UserLogin(login), computationId, security);

		assertThat(response.getEntity(), instanceOf(ComputationMetadata.class));
	}

	private int testCreateComputation(User accessingUser, User targetUser) {
		return this.testCreateComputation(accessingUser.getLogin(), accessingUser.getRole(), targetUser);
	}

	private int testCreateComputation(String accessLogin, RoleType role, User targetUser) {
		final String login = targetUser.getLogin();

		final SecurityContextStub security = new SecurityContextStub(users(), accessLogin);

		final UriInfo currentUri = createNiceMock(UriInfo.class);
		final UriBuilder uriBuilder = createNiceMock(UriBuilder.class);
		final Capture<String> capture = newCapture();

		expect(currentUri.getAbsolutePathBuilder()).andReturn(uriBuilder);
		expect(uriBuilder.path(capture(capture))).andReturn(uriBuilder);
		expect(uriBuilder.build()).andAnswer(() -> new URI(
			"http://testhost/api/variantsanalysis/" + login + capture.getValue()));

		replay(currentUri);
		replay(uriBuilder);
		final Response response = service.startVariantsScoreUserComputation(
			new UserLogin(login), emptyInputStream(), UUID.randomUUID().toString(), security, currentUri
		);
		
		assertThat(response, hasCreatedStatus());
		
		try {
			assertThat(response.getLocation(), is(equalTo(new URI(
				"http://testhost/api/variantsanalysis/" + login + capture.getValue()))
			));
		} catch (URISyntaxException e) {
			throw new RuntimeException(e);
		}
		
		int computationId = Integer.parseInt(capture.getValue().substring(1));
		return computationId;
	}
}
