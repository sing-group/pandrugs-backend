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
	public static IsEqualToGeneDrugGroupInfos equalsToGeneDrugGroupInfos(GeneDrugGroupInfos expected) {
		return new IsEqualToGeneDrugGroupInfos(expected);
	}
}
