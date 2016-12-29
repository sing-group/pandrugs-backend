/*
 * #%L
 * PanDrugs Backend
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
package es.uvigo.ei.sing.pandrugs.matcher.hamcrest;

import org.hamcrest.Factory;
import org.hamcrest.Matcher;

import es.uvigo.ei.sing.pandrugs.service.entity.GeneDrugInfo;

//TODO: Enable gscore and dscore check
public class IsEqualToGeneDrugInfo extends IsEqualToEntity<GeneDrugInfo> {
	public IsEqualToGeneDrugInfo(GeneDrugInfo expected) {
		super(expected);
	}
	
	@Override
	protected boolean matchesSafely(GeneDrugInfo actual) {
		this.clearDescribeTo();
		
		if (actual == null) {
			this.addTemplatedDescription("actual", expected.toString());
			return false;
		} else {
			return /*checkAttribute("dScore", GeneDrugInfo::getDScore, actual)
				&& checkAttribute("gScore", GeneDrugInfo::getGScore, actual)
				&& */checkArrayAttribute("genes", GeneDrugInfo::getGenes, actual)
				&& checkAttribute("drug", GeneDrugInfo::getDrug, actual)
				&& checkAttribute("family", GeneDrugInfo::getFamily, actual)
				&& checkAttribute("status", GeneDrugInfo::getStatus, actual)
				&& checkArrayAttribute("cancers", GeneDrugInfo::getCancers, actual)
				&& checkAttribute("therapy", GeneDrugInfo::getTherapy, actual)
				&& checkAttribute("indirect", GeneDrugInfo::getIndirect, actual)
				&& checkAttribute("target", GeneDrugInfo::getTarget, actual)
				&& checkAttribute("sensitivity", GeneDrugInfo::getSensitivity, actual)
				&& checkAttribute("alteration", GeneDrugInfo::getAlteration, actual)
				&& checkAttribute("drugStatusInfo", GeneDrugInfo::getDrugStatusInfo, actual)
				&& checkArrayAttribute("sources", GeneDrugInfo::getSources, actual);
		}
	}

	@Factory
	public static IsEqualToGeneDrugInfo equalToGeneDrugInfo(GeneDrugInfo expected) {
		return new IsEqualToGeneDrugInfo(expected);
	}
	
	@Factory
	public static Matcher<Iterable<? extends GeneDrugInfo>> containsGeneDrugInfos(GeneDrugInfo ... expected) {
		return containsEntityInAnyOrder(IsEqualToGeneDrugInfo::equalToGeneDrugInfo, expected);
	}
}
