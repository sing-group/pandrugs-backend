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
package es.uvigo.ei.sing.pandrugsdb;

import es.uvigo.ei.sing.pandrugsdb.core.variantsanalysis.DefaultVEPConfiguration;
import org.springframework.context.annotation.Scope;
import org.springframework.mock.web.MockServletContext;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
@Scope("prototype")
public class TestServletContext extends MockServletContext {

	public static Map<String, String> INIT_PARAMETERS = new HashMap<>();

	public TestServletContext() {
		INIT_PARAMETERS.entrySet().forEach((e)->
			this.setInitParameter(e.getKey(), e.getValue())
		);
	}
}
