package es.uvigo.ei.sing.pandrugsdb.matcher.hamcrest;

import org.hamcrest.Factory;
import org.hamcrest.Matcher;

import es.uvigo.ei.sing.pandrugsdb.service.entity.GeneDrugInfo;

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
	public static IsEqualToGeneDrugInfo equalsToGeneDrugInfo(GeneDrugInfo expected) {
		return new IsEqualToGeneDrugInfo(expected);
	}
	
	@Factory
	public static Matcher<Iterable<? extends GeneDrugInfo>> containsGeneDrugInfos(GeneDrugInfo ... expected) {
		return containsEntityInAnyOrder(IsEqualToGeneDrugInfo::equalsToGeneDrugInfo, expected);
	}
}
