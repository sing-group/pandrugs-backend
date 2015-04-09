package es.uvigo.ei.sing.pandrugsdb.service.entity;

import static java.util.Collections.unmodifiableList;
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
	
	GeneDrugBasicInfos() {}

	public GeneDrugBasicInfos(List<GeneDrug> geneDrugs) {
		this.geneDrugs = geneDrugs.stream()
			.map(GeneDrugBasicInfo::new)
		.collect(toList());
	}

	public List<GeneDrugBasicInfo> getGeneDrugs() {
		return unmodifiableList(geneDrugs);
	}
}
