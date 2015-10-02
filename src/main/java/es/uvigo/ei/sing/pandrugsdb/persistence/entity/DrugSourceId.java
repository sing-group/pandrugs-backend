package es.uvigo.ei.sing.pandrugsdb.persistence.entity;

import java.io.Serializable;

import javax.persistence.Embeddable;

@Embeddable
public class DrugSourceId implements Serializable {
	private static final long serialVersionUID = 1L;

	private String source;
	
	private String sourceDrugName;

	DrugSourceId() {}
	
	public DrugSourceId(String source, String sourceDrugName) {
		this.source = source;
		this.sourceDrugName = sourceDrugName;
	}

	public String getSource() {
		return source;
	}

	public String getSourceDrugName() {
		return sourceDrugName;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((source == null) ? 0 : source.hashCode());
		result = prime * result + ((sourceDrugName == null) ? 0 : sourceDrugName.hashCode());
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
		DrugSourceId other = (DrugSourceId) obj;
		if (source == null) {
			if (other.source != null)
				return false;
		} else if (!source.equals(other.source))
			return false;
		if (sourceDrugName == null) {
			if (other.sourceDrugName != null)
				return false;
		} else if (!sourceDrugName.equals(other.sourceDrugName))
			return false;
		return true;
	}
}
