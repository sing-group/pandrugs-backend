package es.uvigo.ei.sing.pandrugsdb.persistence.entity;

import java.io.Serializable;

import javax.persistence.Embeddable;

@Embeddable
public class GeneDrugId implements Serializable {
	private static final long serialVersionUID = 1L;
	
	private String geneSymbol;

	private int drugId;
	
	private boolean target;
	
	GeneDrugId() {}
	
	public GeneDrugId(String geneSymbol, int drugId, boolean target) {
		this.geneSymbol = geneSymbol;
		this.drugId = drugId;
		this.target = target;
	}

	public String getGeneSymbol() {
		return geneSymbol;
	}

	public int getDrugId() {
		return drugId;
	}
	
	public boolean isTarget() {
		return target;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + drugId;
		result = prime * result + ((geneSymbol == null) ? 0 : geneSymbol.hashCode());
		result = prime * result + (target ? 1231 : 1237);
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
		GeneDrugId other = (GeneDrugId) obj;
		if (drugId != other.drugId)
			return false;
		if (geneSymbol == null) {
			if (other.geneSymbol != null)
				return false;
		} else if (!geneSymbol.equals(other.geneSymbol))
			return false;
		if (target != other.target)
			return false;
		return true;
	}
}
