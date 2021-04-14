/*-
 * #%L
 * PanDrugs Backend
 * %%
 * Copyright (C) 2015 - 2021 Fátima Al-Shahrour, Elena Piñeiro, Daniel Glez-Peña
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

import es.uvigo.ei.sing.pandrugs.service.entity.GeneDrugGroupInfo;

//TODO: Enable gscore and dscore check
public class IsEqualToGeneDrugGroupInfo extends IsEqualToEntity<GeneDrugGroupInfo> {
	public IsEqualToGeneDrugGroupInfo(GeneDrugGroupInfo expected) {
		super(expected);
	}
	
	@Override
	protected boolean matchesSafely(GeneDrugGroupInfo actual) {
		this.clearDescribeTo();
		
		if (actual == null) {
			this.addTemplatedDescription("actual", expected.toString());
			return false;
		} else {
			return /*checkAttribute("dScore", GeneDrugGroupInfo::getDScore, actual)
				&& checkAttribute("gScore", GeneDrugGroupInfo::getGScore, actual)
				&& */checkArrayAttribute("genes", GeneDrugGroupInfo::getGenes, actual)
				&& checkAttribute("showDrugName", GeneDrugGroupInfo::getShowDrugName, actual)
				&& checkAttribute("standardDrugName", GeneDrugGroupInfo::getStandardDrugName, actual)
				&& checkAttribute("target", GeneDrugGroupInfo::isTarget, actual)
				&& checkAttribute("status", GeneDrugGroupInfo::getStatus, actual)
				&& checkAttribute("statusDescription", GeneDrugGroupInfo::getStatusDescription, actual)
				&& checkAttribute("therapy", GeneDrugGroupInfo::getTherapy, actual)
				&& checkArrayAttribute("cancers", GeneDrugGroupInfo::getCancers, actual)
				&& checkArrayAttribute("curatedSources", GeneDrugGroupInfo::getCuratedSources, actual)
				&& checkArrayAttribute("families", GeneDrugGroupInfo::getFamilies, actual)
				&& checkIntArrayAttribute("pubchemId", GeneDrugGroupInfo::getPubchemId, actual)
				&& checkArrayAttribute("sourceLinks", GeneDrugGroupInfo::getSourceLinks, actual)
				&& checkArrayAttribute("indirect", GeneDrugGroupInfo::getIndirect, actual)
				&& checkArrayAttribute("geneDrugs", GeneDrugGroupInfo::getGeneDrugs, actual, IsEqualToGeneDrugInfo::containsGeneDrugInfos);
		}
	}

	@Factory
	public static IsEqualToGeneDrugGroupInfo equalToGeneDrugGroup(GeneDrugGroupInfo expected) {
		return new IsEqualToGeneDrugGroupInfo(expected);
	}
	
	@Factory
	public static Matcher<Iterable<? extends GeneDrugGroupInfo>> containsGeneDrugGroupInfos(GeneDrugGroupInfo ... expected) {
		return containsEntityInAnyOrder(IsEqualToGeneDrugGroupInfo::equalToGeneDrugGroup, expected);
	}
	
	@Factory
	public static Matcher<Iterable<? extends GeneDrugGroupInfo>> containsGeneDrugGroupInfos(Iterable<GeneDrugGroupInfo> expected) {
		final GeneDrugGroupInfo[] expectedArray = StreamSupport.stream(expected.spliterator(), false)
			.toArray(GeneDrugGroupInfo[]::new);
		
		return containsGeneDrugGroupInfos(expectedArray);
	}
}
