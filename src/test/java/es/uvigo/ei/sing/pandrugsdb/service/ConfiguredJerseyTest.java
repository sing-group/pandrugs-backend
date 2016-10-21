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

import java.util.HashMap;
import java.util.Map;

import javax.inject.Inject;
import javax.inject.Named;
import javax.ws.rs.container.ContainerRequestFilter;

import org.glassfish.jersey.client.ClientConfig;
import org.glassfish.jersey.jackson.JacksonFeature;
import org.glassfish.jersey.server.ResourceConfig;
import org.glassfish.jersey.server.filter.RolesAllowedDynamicFeature;
import org.glassfish.jersey.server.spring.scope.RequestContextFilter;
import org.glassfish.jersey.server.validation.ValidationFeature;
import org.glassfish.jersey.servlet.ServletContainer;
import org.glassfish.jersey.test.DeploymentContext;
import org.glassfish.jersey.test.JerseyTest;
import org.glassfish.jersey.test.ServletDeploymentContext;
import org.glassfish.jersey.test.grizzly.GrizzlyWebTestContainerFactory;
import org.glassfish.jersey.test.spi.TestContainerException;
import org.glassfish.jersey.test.spi.TestContainerFactory;
import org.junit.runner.RunWith;
import org.springframework.orm.jpa.support.OpenEntityManagerInViewFilter;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.annotation.DirtiesContext.ClassMode;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.DependencyInjectionTestExecutionListener;
import org.springframework.test.context.support.DirtiesContextTestExecutionListener;
import org.springframework.web.context.ContextLoaderListener;
import org.springframework.web.context.request.RequestContextListener;

import com.github.springtestdbunit.TransactionDbUnitTestExecutionListener;

import es.uvigo.ei.sing.pandrugsdb.UnexpectedExceptionMapper;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("file:src/test/resources/META-INF/applicationTestContext.xml")
@TestExecutionListeners({
	DependencyInjectionTestExecutionListener.class,
	TransactionDbUnitTestExecutionListener.class,
	DirtiesContextTestExecutionListener.class
})
@DirtiesContext(classMode = ClassMode.AFTER_EACH_TEST_METHOD)
public abstract class ConfiguredJerseyTest extends JerseyTest {
	@Inject
	@Named("securityContextFilter")
	protected ContainerRequestFilter securityFilter;
	
	@Override
	protected TestContainerFactory getTestContainerFactory()
	throws TestContainerException {
		return new GrizzlyWebTestContainerFactory();
	}
	
	@Override
	protected DeploymentContext configureDeployment() {
		final Map<String, String> params = new HashMap<>();
		params.put("singleSession", "true");
		params.put("flushMode", "AUTO");

		return ServletDeploymentContext.forServlet(new ServletContainer(configure()))
			.addFilter(OpenEntityManagerInViewFilter.class, "OpenEntityManagerInViewFilter", params)
			.addListener(RequestContextListener.class)
			.addListener(ContextLoaderListener.class)
			.contextParam("contextConfigLocation", 
				this.getClass().getAnnotation(ContextConfiguration.class).value()[0])
		.build();
	}
	
	@Override
	protected ResourceConfig configure() {
		// Lazy is needed because injection is done after this method is called
		final ContainerRequestFilter lazySecurityFilter = requestContext ->
			this.securityFilter.filter(requestContext);
			
		return new ResourceConfig(getServiceClasses())
			.registerInstances(lazySecurityFilter)
			.register(UnexpectedExceptionMapper.class)
			.register(RolesAllowedDynamicFeature.class)
			.register(RequestContextFilter.class)
			.register(JacksonFeature.class)
			.register(ValidationFeature.class);
	}
	
	protected Class<?>[] getServiceClasses() {
        throw new UnsupportedOperationException(
        	"The getServiceClasses method must be implemented by the extending class");
	}
	
	@Override
	protected void configureClient(ClientConfig config) {
		config.register(JacksonFeature.class)
			.register(ValidationFeature.class);
	}
}
