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

import es.uvigo.ei.sing.pandrugsdb.service.entity.GeneDrugGroupInfos;

//TODO: Enable gscore and dscore check
public class IsEqualToGeneDrugGroupInfos extends IsEqualToEntity<GeneDrugGroupInfos> {
	public IsEqualToGeneDrugGroupInfos(GeneDrugGroupInfos expected) {
		super(expected);
	}
	
	@Override
	protected boolean matchesSafely(GeneDrugGroupInfos actual) {
		this.clearDescribeTo();
		
		if (actual == null) {
			this.addTemplatedDescription("actual", expected.toString());
			return false;
		} else {
			return checkIterableAttribute("geneDrugs",
				GeneDrugGroupInfos::getGeneDrugs, actual,
				IsEqualToGeneDrugGroupInfo::containsGeneDrugGroupInfos
			);
		}
	}

	@Factory
	public static IsEqualToGeneDrugGroupInfos equalToGeneDrugGroupInfos(GeneDrugGroupInfos expected) {
		return new IsEqualToGeneDrugGroupInfos(expected);
	}
}