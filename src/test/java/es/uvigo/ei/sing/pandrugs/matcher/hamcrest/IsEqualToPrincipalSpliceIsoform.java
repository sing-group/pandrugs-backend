/*-
 * #%L
 * PanDrugs Backend
 * %%
 * Copyright (C) 2015 - 2022 Fátima Al-Shahrour, Elena Piñeiro, Daniel Glez-Peña
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

import es.uvigo.ei.sing.pandrugs.persistence.entity.PrincipalSpliceIsoform;

public class IsEqualToPrincipalSpliceIsoform extends IsEqualToEntity<PrincipalSpliceIsoform> {
	public IsEqualToPrincipalSpliceIsoform(PrincipalSpliceIsoform expected) {
		super(expected);
	}
	
	@Override
	protected boolean matchesSafely(PrincipalSpliceIsoform actual) {
		this.clearDescribeTo();
		
		if (actual == null) {
			this.addTemplatedDescription("actual", expected.toString());
			return false;
		} else {
			return checkAttribute("transcriptId", PrincipalSpliceIsoform::getTranscriptId, actual)
				&& checkAttribute("isoformType", PrincipalSpliceIsoform::getIsoformType, actual);
		}
	}

	@Factory
	public static IsEqualToPrincipalSpliceIsoform equalPrincipalSpliceIsoform(PrincipalSpliceIsoform expected) {
		return new IsEqualToPrincipalSpliceIsoform(expected);
	}
	
	@Factory
	public static Matcher<Iterable<? extends PrincipalSpliceIsoform>> containsPrincipalSpliceIsoforms(PrincipalSpliceIsoform ... expected) {
		return containsEntityInAnyOrder(IsEqualToPrincipalSpliceIsoform::equalPrincipalSpliceIsoform, expected);
	}
	
	@Factory
	public static Matcher<Iterable<? extends PrincipalSpliceIsoform>> containsPrincipalSpliceIsoforms(Iterable<PrincipalSpliceIsoform> expected) {
		final PrincipalSpliceIsoform[] expectedArray = StreamSupport.stream(expected.spliterator(), false)
			.toArray(PrincipalSpliceIsoform[]::new);
		
		return containsEntityInAnyOrder(IsEqualToPrincipalSpliceIsoform::equalPrincipalSpliceIsoform, expectedArray);
	}
}
