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
package es.uvigo.ei.sing.pandrugsdb.controller;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Map;

import es.uvigo.ei.sing.pandrugsdb.service.entity.ComputationStatusMetadata;
import es.uvigo.ei.sing.pandrugsdb.service.entity.GeneRanking;
import es.uvigo.ei.sing.pandrugsdb.service.entity.UserLogin;
import es.uvigo.ei.sing.pandrugsdb.service.entity.UserMetadata;

public interface VariantsAnalysisController {

	public UserMetadata getUserOfComputation(Integer computationId);

	public GeneRanking getGeneRankingForComputation(int computationId);

	public int startVariantsScopeUserComputation(
			UserLogin userLogin,
			InputStream vcfFile
	) throws IOException;

	public ComputationStatusMetadata getComputationsStatus(Integer computationId);

	public Map<Integer, ComputationStatusMetadata> getComputationsForUser(UserLogin userLogin);
}
