/*-
 * #%L
 * PanDrugs Backend
 * %%
 * Copyright (C) 2015 - 2018 Fátima Al-Shahrour, Elena Piñeiro, Daniel Glez-Peña and Miguel Reboiro-Jato
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

package es.uvigo.ei.sing.pandrugs.controller;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Map;

import es.uvigo.ei.sing.pandrugs.service.entity.ComputationMetadata;
import es.uvigo.ei.sing.pandrugs.service.entity.GeneRanking;
import es.uvigo.ei.sing.pandrugs.service.entity.UserInfo;
import es.uvigo.ei.sing.pandrugs.service.entity.UserLogin;

public interface VariantsAnalysisController {

	public UserInfo getUserOfComputation(String computationId);

	public GeneRanking getGeneRankingForComputation(String computationId);

	public File getVariantsScoreFile(String computationId);

	public String startVariantsScopeUserComputation(
		UserLogin userLogin,
		InputStream vcfFile,
		String computationName
	) throws IOException;

	public String startVariantsScopeUserComputation(
		UserLogin login,
		InputStream vcfFile,
		String computationName,
		String resultsURLTemplate
	) throws IOException;

	public ComputationMetadata getComputationStatus(String computationId);

	public Map<String, ComputationMetadata> getComputationsForUser(UserLogin userLogin);

	void deleteComputation(String computationId);
}
