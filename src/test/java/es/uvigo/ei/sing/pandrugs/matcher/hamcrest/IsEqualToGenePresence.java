/*-
 * #%L
 * PanDrugs Backend
 * %%
 * Copyright (C) 2015 - 2023 Fátima Al-Shahrour, Elena Piñeiro, Daniel Glez-Peña
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

package es.uvigo.ei.sing.pandrugs.matcher.hamcrest;

import org.hamcrest.Factory;
import org.hamcrest.Matcher;
import org.hamcrest.collection.IsArrayContainingInAnyOrder;

import es.uvigo.ei.sing.pandrugs.service.entity.GenePresence;

public class IsEqualToGenePresence extends IsEqualToEntity<GenePresence> {
	public IsEqualToGenePresence(GenePresence expected) {
		super(expected);
	}
	
	@Override
	protected boolean matchesSafely(GenePresence actual) {
		this.clearDescribeTo();
		
		if (actual == null) {
			this.addTemplatedDescription("actual", expected.toString());
			return false;
		} else {
			return checkAttribute("presentGenes", GenePresence::getPresentGenes, actual,
					IsArrayContainingInAnyOrder::arrayContainingInAnyOrder)
				&& checkAttribute("absentGenes", GenePresence::getAbsentGenes, actual,
					IsArrayContainingInAnyOrder::arrayContainingInAnyOrder);
		}
	}

	@Factory
	public static IsEqualToGenePresence equalToGenePresence(GenePresence expected) {
		return new IsEqualToGenePresence(expected);
	}
	
	@Factory
	public static Matcher<Iterable<? extends GenePresence>> containsGenePresences(GenePresence ... expected) {
		return containsEntityInAnyOrder(IsEqualToGenePresence::equalToGenePresence, expected);
	}
}
