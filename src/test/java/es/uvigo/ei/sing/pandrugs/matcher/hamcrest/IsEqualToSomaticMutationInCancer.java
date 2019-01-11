/*-
 * #%L
 * PanDrugs Backend
 * %%
 * Copyright (C) 2015 - 2019 Fátima Al-Shahrour, Elena Piñeiro, Daniel Glez-Peña
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

import java.util.stream.StreamSupport;

import org.hamcrest.Factory;
import org.hamcrest.Matcher;

import es.uvigo.ei.sing.pandrugs.persistence.entity.SomaticMutationInCancer;

public class IsEqualToSomaticMutationInCancer extends IsEqualToEntity<SomaticMutationInCancer> {
	public IsEqualToSomaticMutationInCancer(SomaticMutationInCancer expected) {
		super(expected);
	}
	
	@Override
	protected boolean matchesSafely(SomaticMutationInCancer actual) {
		this.clearDescribeTo();
		
		if (actual == null) {
			this.addTemplatedDescription("actual", expected.toString());
			return false;
		} else {
			return checkAttribute("sampleId", SomaticMutationInCancer::getSampleId, actual)
				&& checkAttribute("mutationId", SomaticMutationInCancer::getMutationId, actual)
				&& checkAttribute("mutationAA", SomaticMutationInCancer::getMutationAA, actual)
				&& checkAttribute("fathmmPrediction", SomaticMutationInCancer::getFathmmPrediciton, actual)
				&& checkAttribute("status", SomaticMutationInCancer::getStatus, actual)
				&& checkAttribute("geneSymbol", SomaticMutationInCancer::getGeneSymbol, actual);
		}
	}

	@Factory
	public static IsEqualToSomaticMutationInCancer equalSomaticMutationInCancer(SomaticMutationInCancer expected) {
		return new IsEqualToSomaticMutationInCancer(expected);
	}
	
	@Factory
	public static Matcher<Iterable<? extends SomaticMutationInCancer>> containsSomaticMutationInCancers(SomaticMutationInCancer ... expected) {
		return containsEntityInAnyOrder(IsEqualToSomaticMutationInCancer::equalSomaticMutationInCancer, expected);
	}
	
	@Factory
	public static Matcher<Iterable<? extends SomaticMutationInCancer>> containsSomaticMutationInCancers(Iterable<SomaticMutationInCancer> expected) {
		final SomaticMutationInCancer[] expectedArray = StreamSupport.stream(expected.spliterator(), false)
			.toArray(SomaticMutationInCancer[]::new);
		
		return containsEntityInAnyOrder(IsEqualToSomaticMutationInCancer::equalSomaticMutationInCancer, expectedArray);
	}
}
