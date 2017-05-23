package es.uvigo.ei.sing.pandrugs.service.entity;

import javax.xml.bind.annotation.XmlRootElement;

import es.uvigo.ei.sing.pandrugs.persistence.entity.CancerType;

@XmlRootElement(name = "cancerType", namespace = "http://sing.ei.uvigo.es/pandrugs")
public class CancerTypeInfo {
	private String name;
	private boolean canBeQueried;
	
	CancerTypeInfo() {}
	
	public CancerTypeInfo(CancerType type) {
		this.name = type.name();
		this.canBeQueried = type.canBeQueried();
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public boolean isCanBeQueried() {
		return canBeQueried;
	}

	public void setCanBeQueried(boolean canBeQueried) {
		this.canBeQueried = canBeQueried;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + (canBeQueried ? 1231 : 1237);
		result = prime * result + ((name == null) ? 0 : name.hashCode());
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
		CancerTypeInfo other = (CancerTypeInfo) obj;
		if (canBeQueried != other.canBeQueried)
			return false;
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name))
			return false;
		return true;
	}

}
