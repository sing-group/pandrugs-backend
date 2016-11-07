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
package es.uvigo.ei.sing.pandrugsdb.matcher.hamcrest;

import org.hamcrest.Factory;
import org.hamcrest.Matcher;

import es.uvigo.ei.sing.pandrugsdb.persistence.entity.Gene;

public class IsEqualToGene extends IsEqualToEntity<Gene> {
	public IsEqualToGene(Gene expected) {
		super(expected);
	}
	
	@Override
	protected boolean matchesSafely(Gene actual) {
		this.clearDescribeTo();
		
		if (actual == null) {
			this.addTemplatedDescription("actual", expected.toString());
			return false;
		} else {
			return checkAttribute("geneSymbol", Gene::getGeneSymbol, actual)
				&& checkAttribute("tumorPortalMutationLevel", Gene::getTumorPortalMutationLevel, actual)
				&& checkAttribute("cgc", Gene::isCgc, actual)
				&& checkAttribute("driverLevel", Gene::getDriverLevel, actual)
				&& checkAttribute("geneEssentialitiyScore", Gene::getGeneEssentialityScore, actual)
				&& checkAttribute("gScore", Gene::getGScore, actual);
		}
	}

	@Factory
	public static IsEqualToGene equalsToGene(Gene expected) {
		return new IsEqualToGene(expected);
	}
	
	@Factory
	public static Matcher<Iterable<? extends Gene>> containsGenes(Gene ... expected) {
		return containsEntityInAnyOrder(IsEqualToGene::equalsToGene, expected);
	}
}
