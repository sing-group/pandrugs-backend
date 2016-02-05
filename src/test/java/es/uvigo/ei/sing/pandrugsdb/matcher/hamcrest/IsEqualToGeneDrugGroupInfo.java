package es.uvigo.ei.sing.pandrugsdb.matcher.hamcrest;

import java.util.stream.StreamSupport;

import org.hamcrest.Factory;
import org.hamcrest.Matcher;

import es.uvigo.ei.sing.pandrugsdb.service.entity.GeneDrugGroupInfo;

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
	public static IsEqualToGeneDrugGroupInfo equalsToGeneDrugGroup(GeneDrugGroupInfo expected) {
		return new IsEqualToGeneDrugGroupInfo(expected);
	}
	
	@Factory
	public static Matcher<Iterable<? extends GeneDrugGroupInfo>> containsGeneDrugGroupInfos(GeneDrugGroupInfo ... expected) {
		return containsEntityInAnyOrder(IsEqualToGeneDrugGroupInfo::equalsToGeneDrugGroup, expected);
	}
	
	@Factory
	public static Matcher<Iterable<? extends GeneDrugGroupInfo>> containsGeneDrugGroupInfos(Iterable<GeneDrugGroupInfo> expected) {
		final GeneDrugGroupInfo[] expectedArray = StreamSupport.stream(expected.spliterator(), false)
			.toArray(GeneDrugGroupInfo[]::new);
		
		return containsEntityInAnyOrder(IsEqualToGeneDrugGroupInfo::equalsToGeneDrugGroup, expectedArray);
	}
}
