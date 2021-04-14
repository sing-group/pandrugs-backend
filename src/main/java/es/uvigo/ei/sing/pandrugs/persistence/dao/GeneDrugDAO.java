/*-
 * #%L
 * PanDrugs Backend
 * %%
 * Copyright (C) 2015 - 2021 Fátima Al-Shahrour, Elena Piñeiro, Daniel Glez-Peña
 * and Miguel Reboiro-Jato
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

package es.uvigo.ei.sing.pandrugs.persistence.dao;

import java.util.List;
import java.util.Map;

import es.uvigo.ei.sing.pandrugs.persistence.entity.Drug;
import es.uvigo.ei.sing.pandrugs.persistence.entity.GeneDrug;
import es.uvigo.ei.sing.pandrugs.query.GeneDrugQueryParameters;

public interface GeneDrugDAO {
	public abstract List<GeneDrug> searchByGene(
		GeneDrugQueryParameters queryParameters, String ... geneNames
	);

	public abstract List<GeneDrug> searchByDrug(
		GeneDrugQueryParameters queryParameters, String ... drugNames
	);

	public abstract String[] listGeneSymbols(String queryFilter, int maxResults);

	public abstract Drug[] listDrugs(String query, int maxResults);

	public abstract Map<String, Boolean> checkGenePresence(String[] geneSymbols);
}
