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
package es.uvigo.ei.sing.pandrugsdb.service;

import static es.uvigo.ei.sing.pandrugsdb.matcher.hamcrest.HasTheSameUserDataMatcher.hasTheSameUserDataAs;
import static es.uvigo.ei.sing.pandrugsdb.persistence.entity.UserDataset.absentUser;
import static es.uvigo.ei.sing.pandrugsdb.persistence.entity.UserDataset.presentAdmin;
import static es.uvigo.ei.sing.pandrugsdb.persistence.entity.UserDataset.presentUser;
import static es.uvigo.ei.sing.pandrugsdb.persistence.entity.UserDataset.presentUser2;
import static es.uvigo.ei.sing.pandrugsdb.persistence.entity.UserDataset.users;
import static org.hamcrest.collection.IsIterableContainingInAnyOrder.containsInAnyOrder;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;

import java.util.stream.Stream;

import javax.inject.Inject;
import javax.inject.Named;
import javax.ws.rs.NotAuthorizedException;
import javax.ws.rs.NotFoundException;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.DependencyInjectionTestExecutionListener;
import org.springframework.test.context.support.DirtiesContextTestExecutionListener;

import com.github.springtestdbunit.TransactionDbUnitTestExecutionListener;
import com.github.springtestdbunit.annotation.DatabaseSetup;

import es.uvigo.ei.sing.pandrugsdb.persistence.entity.RoleType;
import es.uvigo.ei.sing.pandrugsdb.persistence.entity.User;
import es.uvigo.ei.sing.pandrugsdb.service.entity.UserLogin;
import es.uvigo.ei.sing.pandrugsdb.service.entity.UserMetadata;
import es.uvigo.ei.sing.pandrugsdb.service.entity.UserMetadatas;
import es.uvigo.ei.sing.pandrugsdb.service.security.SecurityContextStub;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("file:src/test/resources/META-INF/applicationTestContext.xml")
@TestExecutionListeners({
	DependencyInjectionTestExecutionListener.class,
	DirtiesContextTestExecutionListener.class,
	TransactionDbUnitTestExecutionListener.class
})
@DatabaseSetup("file:src/test/resources/META-INF/dataset.user.xml")
@DirtiesContext
public class DefaultUserServiceIntegrationTest {
	@Inject
	@Named("defaultUserService")
	private UserService service;
	
	@Test
	public void testGetUserOwnData() {
		final User user = presentUser();
		
		testGetUserData(user, user);
	}
	
	@Test(expected = NotAuthorizedException.class)
	public void testUserGetOthersData() {
		final User accessingUser = presentUser();
		final User targetUser = presentUser2();
		
		testGetUserData(accessingUser, targetUser);
	}
	
	@Test
	public void testGetAdminOthersData() {
		testGetUserData(presentAdmin(), presentUser());
	}
	
	@Test(expected = NotFoundException.class)
	public void testGetAdminAbsentData() {
		testGetUserData(presentAdmin(), absentUser());
	}

	@Test
	public void testUpdateUserOwnData() {
		final User user = presentUser();
		
		testUpdateUserData(user, user);
	}
	
	@Test(expected = NotAuthorizedException.class)
	public void testUserUpdateOthersData() {
		final User accessingUser = presentUser();
		final User targetUser = presentUser2();
		
		testUpdateUserData(accessingUser, targetUser);
	}

	@Test
	public void testUpdateAdminOwnData() {
		final User user = presentAdmin();
		
		testUpdateUserData(user, user);
	}
	
	@Test
	public void testUpdateAdminOthersData() {
		final User accessingUser = presentAdmin();
		final User targetUser = presentUser();
		
		testUpdateUserData(accessingUser, targetUser);
	}
	
	@Test(expected = NotFoundException.class)
	public void testUpdateAdminAbsentData() {
		final User accessingUser = presentAdmin();
		final User targetUser = absentUser();
		
		testUpdateUserData(accessingUser, targetUser);
	}
	
	@Test
	public void testDelete() {
		final String login = presentUser().getLogin();
		
		assertNotNull(service.delete(new UserLogin(login)));
	}

	@Test(expected = NotFoundException.class)
	public void testDeleteAbsent() {
		final String login = absentUser().getLogin();
		
		service.delete(new UserLogin(login));
	}

	@Test
	public void testList() {
		final User[] users = users();
		final UserMetadata[] metadatas  = Stream.of(users)
			.map(UserMetadata::new)
		.toArray(UserMetadata[]::new);
		
		final UserMetadatas userMetadatas = service.list();
		
		assertThat(userMetadatas.getUsers(), containsInAnyOrder(metadatas));
	}
	
	private void testGetUserData(User accessingUser, User targetUser) {
		this.testGetUserData(accessingUser.getLogin(), accessingUser.getRole(), targetUser);
	}
	
	private void testGetUserData(String accessLogin, RoleType accessRole, User targetUser) {
		final String login = targetUser.getLogin();
		
		final SecurityContextStub security = new SecurityContextStub(users(), accessLogin);
		
		final UserMetadata metadata = service.get(new UserLogin(login), security);
		
		assertThat(metadata, hasTheSameUserDataAs(targetUser));
	}
	
	private void testUpdateUserData(User accessingUser, User targetUser) {
		this.testUpdateUserData(accessingUser.getLogin(), accessingUser.getRole(), targetUser);
	}
	
	private void testUpdateUserData(String accessLogin, RoleType accessRole, User targetUser) {
		final User updatedUser = new User(
			targetUser.getLogin(), "new@email.com", "22af645d1859cb5ca6da0c484f1f37ea", targetUser.getRole());

		final SecurityContextStub security = new SecurityContextStub(users(), accessLogin);
		
		final UserMetadata metadata = service.update(new UserMetadata(updatedUser), security);
		
		assertThat(metadata, hasTheSameUserDataAs(updatedUser));
	}
}
