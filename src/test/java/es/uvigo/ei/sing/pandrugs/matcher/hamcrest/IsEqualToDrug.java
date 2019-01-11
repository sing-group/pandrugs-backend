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

import org.hamcrest.Factory;
import org.hamcrest.Matcher;

import es.uvigo.ei.sing.pandrugs.persistence.entity.Drug;

public class IsEqualToDrug extends IsEqualToEntity<Drug> {
	public IsEqualToDrug(Drug expected) {
		super(expected);
	}
	
	@Override
	protected boolean matchesSafely(Drug actual) {
		this.clearDescribeTo();
		
		if (actual == null) {
			this.addTemplatedDescription("actual", expected.toString());
			return false;
		} else {
			return checkAttribute("id", Drug::getId, actual)
				&& checkAttribute("standardName", Drug::getStandardName, actual)
				&& checkAttribute("showName", Drug::getShowName, actual)
				&& checkAttribute("status", Drug::getStatus, actual)
				&& checkAttribute("extra", Drug::getExtra, actual)
				&& checkAttribute("extraDetails", Drug::getExtraDetails, actual);
		}
	}

	@Factory
	public static IsEqualToDrug equalToDrug(Drug expected) {
		return new IsEqualToDrug(expected);
	}
	
	@Factory
	public static Matcher<Iterable<? extends Drug>> containsDrugs(Drug ... expected) {
		return containsEntityInAnyOrder(IsEqualToDrug::equalToDrug, expected);
	}
}
