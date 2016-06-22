package es.uvigo.ei.sing.pandrugsdb.service.entity;

import java.util.Arrays;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;

import es.uvigo.ei.sing.pandrugsdb.persistence.entity.GeneDrug;

@XmlRootElement(name = "drug-interaction", namespace = "http://sing.ei.uvigo.es/pandrugsdb")
@XmlAccessorType(XmlAccessType.FIELD)
public class DrugInteraction {
	private String showDrugName;
	private String standardDrugName;
	
	private String target;
	
	@XmlElementWrapper(name = "indirect-genes")
	@XmlElement(name = "indirect-gene")
	private String[] indirect;
	
	DrugInteraction() {}
	
	public DrugInteraction(GeneDrug geneDrug) {
		this.showDrugName = geneDrug.getShowDrugName();
		this.standardDrugName = geneDrug.getStandardDrugName();
		
		this.target = geneDrug.isTarget() ? "target" : "marker";
		
		this.indirect = null;
	}
//	
//	public DrugInteraction(String showDrugName, String standardDrugName, String target, String[] indirect) {
//		this.showDrugName = showDrugName;
//		this.standardDrugName = standardDrugName;
//		this.target = target;
//		this.indirect = indirect;
//	}

	public String getShowDrugName() {
		return showDrugName;
	}

	public void setShowDrugName(String showDrugName) {
		this.showDrugName = showDrugName;
	}

	public String getStandardDrugName() {
		return standardDrugName;
	}

	public void setStandardDrugName(String standardDrugName) {
		this.standardDrugName = standardDrugName;
	}
	
	public String getTarget() {
		return target;
	}
	
	public void setTarget(String target) {
		this.target = target;
	}

	public String[] getIndirect() {
		return indirect;
	}

	public void setIndirect(String[] indirect) {
		this.indirect = indirect;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + Arrays.hashCode(indirect);
		result = prime * result + ((showDrugName == null) ? 0 : showDrugName.hashCode());
		result = prime * result + ((standardDrugName == null) ? 0 : standardDrugName.hashCode());
		result = prime * result + ((target == null) ? 0 : target.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		DrugInteraction other = (DrugInteraction) obj;
		if (!Arrays.equals(indirect, other.indirect))
			return false;
		if (showDrugName == null) {
			if (other.showDrugName != null)
				return false;
		} else if (!showDrugName.equals(other.showDrugName))
			return false;
		if (standardDrugName == null) {
			if (other.standardDrugName != null)
				return false;
		} else if (!standardDrugName.equals(other.standardDrugName))
			return false;
		if (target == null) {
			if (other.target != null)
				return false;
		} else if (!target.equals(other.target))
			return false;
		return true;
	}
}
