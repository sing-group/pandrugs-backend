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

package es.uvigo.ei.sing.pandrugs.service.mime;

import static java.lang.Double.parseDouble;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;

import javax.ws.rs.ext.Provider;

import es.uvigo.ei.sing.pandrugs.service.entity.GeneRanking;

@Provider
public class GeneRankingMessageBodyReader extends MultipartMessageBodyReader<GeneRanking> {
	private Map<String, Double> ranking;

	@Override
	protected void init() {}

	@Override
	protected void add(String name, byte[] bs) {
		if (name.equalsIgnoreCase("generank")) {
			try (final BufferedReader br = new BufferedReader(new InputStreamReader(new ByteArrayInputStream(bs)))) {
				String line;
				
				this.ranking = new HashMap<>();
				while ((line = br.readLine()) != null) {
					line = line.trim();
					if (line.isEmpty()) continue;
					
					final String[] tokens = line.split("\t");
					
					if (tokens.length != 2) {
						throw new RuntimeException("Illegal format in line: " + line);
					} else {
						final String[] geneNames = tokens[0].split("[ /]+");
						
						for (String gene : geneNames) {
							ranking.put(gene.trim(), parseDouble(tokens[1]));
						}
					}
				}
			} catch (IOException ioe) {
				throw new RuntimeException(ioe);
			}
		}
	}

	@Override
	protected GeneRanking build() {
		return new GeneRanking(this.ranking);
	}

}
