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

import java.io.File;
import java.util.Objects;

import javax.servlet.ServletContext;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

import static java.util.Objects.requireNonNull;

@Component
public class DefaultFileSystemConfiguration implements FileSystemConfiguration {

	@Autowired
	private ServletContext context;

	public static final String USER_DATA_DIRECTORY_PARAMETER = "user.data.directory";
	@Override
	public File getUserDataBaseDirectory() {
		return new File(requireNonNull(
				context.getInitParameter(USER_DATA_DIRECTORY_PARAMETER),
				"The context init parameter "+USER_DATA_DIRECTORY_PARAMETER+" was not found. Please" +
						" configure it in your server configuration"));
	}
}
