package es.uvigo.ei.sing.pandrugsdb.persistence.entity;

import java.io.Serializable;

import javax.persistence.Embeddable;

@Embeddable
public class SomaticMutationInCancerId implements Serializable {
	private static final long serialVersionUID = 1L;
	
	private int sampleId;
	
	private String geneSymbol;
	
	private int mutationId;
	
	private String status;
	
	SomaticMutationInCancerId() {}

	public SomaticMutationInCancerId(int sampleId, String geneSymbol, int mutationId, String status) {
		this.sampleId = sampleId;
		this.geneSymbol = geneSymbol;
		this.mutationId = mutationId;
		this.status = status;
	}
	
	public static SomaticMutationInCancerId of(SomaticMutationInCancer smic) {
		return new SomaticMutationInCancerId(
			smic.getSampleId(), smic.getGeneSymbol(),
			smic.getMutationId(), smic.getStatus()
		);
	}

	public int getSampleId() {
		return sampleId;
	}

	public String getGeneSymbol() {
		return geneSymbol;
	}

	public int getMutationId() {
		return mutationId;
	}

	public String getStatus() {
		return status;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((geneSymbol == null) ? 0 : geneSymbol.hashCode());
		result = prime * result + mutationId;
		result = prime * result + sampleId;
		result = prime * result + ((status == null) ? 0 : status.hashCode());
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
		SomaticMutationInCancerId other = (SomaticMutationInCancerId) obj;
		if (geneSymbol == null) {
			if (other.geneSymbol != null)
				return false;
		} else if (!geneSymbol.equals(other.geneSymbol))
			return false;
		if (mutationId != other.mutationId)
			return false;
		if (sampleId != other.sampleId)
			return false;
		if (status == null) {
			if (other.status != null)
				return false;
		} else if (!status.equals(other.status))
			return false;
		return true;
	}
	
}
