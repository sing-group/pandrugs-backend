package es.uvigo.ei.sing.pandrugsdb.service.entity;

import static java.util.stream.Collectors.toList;

import java.util.List;

import javax.validation.constraints.NotNull;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

import es.uvigo.ei.sing.pandrugsdb.persistence.entity.GeneDrug;

@XmlRootElement(name = "gene-drugs", namespace = "http://sing.ei.uvigo.es/pandrugsdb")
@XmlAccessorType(XmlAccessType.FIELD)
public class GeneDrugBasicInfos {
	@NotNull
	private List<GeneDrugBasicInfo> geneDrugs;
	
	public GeneDrugBasicInfos() {}
	
	public static GeneDrugBasicInfos buildFor(List<GeneDrug> geneDrugs) {
		return new GeneDrugBasicInfos(
			geneDrugs.stream().map(GeneDrugBasicInfo::new).collect(toList()));
	}

	public GeneDrugBasicInfos(List<GeneDrugBasicInfo> users) {
		this.geneDrugs = users;
	}

	public List<GeneDrugBasicInfo> getGeneDrugs() {
		return geneDrugs;
	}

	public void setGeneDrugs(List<GeneDrugBasicInfo> users) {
		this.geneDrugs = users;
	}
}
