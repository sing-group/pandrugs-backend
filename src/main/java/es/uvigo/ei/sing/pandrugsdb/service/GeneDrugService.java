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

import java.util.List;

import javax.ws.rs.BadRequestException;
import javax.ws.rs.InternalServerErrorException;
import javax.ws.rs.core.Response;

public interface GeneDrugService {
	/**
	 * Returns a list with the genes-drugs with the provided gene symbol/s.
	 * 
	 * @param genes a list of gene symbol names to search in the database.
	 * @param startPosition the starting position in the database to return the
	 * results. Optional (can be {@code null});
	 * @param maxResults the maximum number of results to return. Optional (can
	 * be {@code null});
	 * @return a list of gene drugs that match the provided genes symbol.
	 * @throws BadRequestException if not gene symbol is provided.
	 * @throws InternalServerErrorException in an unexpected error occurs.
	 */
	public abstract Response list(
		List<String> genes, Integer startPosition, Integer maxResults
	) throws BadRequestException, InternalServerErrorException;
}