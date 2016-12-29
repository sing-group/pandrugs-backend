/*
 * #%L
 * PanDrugs Backend
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
package es.uvigo.ei.sing.pandrugs;

import javax.ws.rs.ApplicationPath;

import org.glassfish.jersey.jackson.JacksonFeature;
import org.glassfish.jersey.media.multipart.MultiPartFeature;
import org.glassfish.jersey.server.ResourceConfig;
import org.glassfish.jersey.server.filter.RolesAllowedDynamicFeature;
import org.glassfish.jersey.server.spring.scope.RequestContextFilter;
import org.glassfish.jersey.server.validation.ValidationFeature;

import es.uvigo.ei.sing.pandrugs.service.DefaultCancerService;
import es.uvigo.ei.sing.pandrugs.service.DefaultGeneDrugService;
import es.uvigo.ei.sing.pandrugs.service.DefaultGeneService;
import es.uvigo.ei.sing.pandrugs.service.DefaultRegistrationService;
import es.uvigo.ei.sing.pandrugs.service.DefaultSessionService;
import es.uvigo.ei.sing.pandrugs.service.DefaultUserService;
import es.uvigo.ei.sing.pandrugs.service.DefaultVariantsAnalysisService;
import es.uvigo.ei.sing.pandrugs.service.mime.GeneRankingMessageBodyReader;

@ApplicationPath("api")
public class PanDrugsApplication extends ResourceConfig {
	public PanDrugsApplication() {
		super(
			DefaultRegistrationService.class,
			DefaultGeneDrugService.class,
			DefaultSessionService.class,
			DefaultGeneService.class,
			DefaultCancerService.class,
			DefaultUserService.class,
			DefaultVariantsAnalysisService.class
		);
		
		register(RequestContextFilter.class);
		register(JacksonFeature.class);
		register(MultiPartFeature.class);
		register(RolesAllowedDynamicFeature.class);
		register(ValidationFeature.class);
		register(UnexpectedExceptionMapper.class);
		register(GeneRankingMessageBodyReader.class);
	}
}
