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

import es.uvigo.ei.sing.pandrugs.persistence.entity.Pathway;

public class IsEqualToPathway extends IsEqualToEntity<Pathway> {
	public IsEqualToPathway(Pathway expected) {
		super(expected);
	}
	
	@Override
	protected boolean matchesSafely(Pathway actual) {
		this.clearDescribeTo();
		
		if (actual == null) {
			this.addTemplatedDescription("actual", expected.toString());
			return false;
		} else {
			return checkAttribute("id", Pathway::getId, actual)
				&& checkAttribute("name", Pathway::getName, actual);
		}
	}

	@Factory
	public static IsEqualToPathway equalToPathway(Pathway expected) {
		return new IsEqualToPathway(expected);
	}
	
	@Factory
	public static Matcher<Iterable<? extends Pathway>> containsPathways(Pathway ... expected) {
		return containsEntityInAnyOrder(IsEqualToPathway::equalToPathway, expected);
	}
}
