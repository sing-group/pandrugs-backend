package es.uvigo.ei.sing.pandrugsdb.persistence.entity;

import static java.util.Collections.unmodifiableSet;

import java.util.Set;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToMany;

@Entity(name = "protein")
public class Protein {
	@Id
	private String uniprotId;
	
	@ManyToMany(fetch = FetchType.LAZY)
	@JoinColumn(name = "interactionId", referencedColumnName = "uniprotId")
	private Set<Protein> iteractions;
	
	@ManyToMany(fetch = FetchType.LAZY)
	@JoinColumn(name = "gene", referencedColumnName = "geneSymbol")
	private Set<GeneInformation> genes;
	
	public String getUniprotId() {
		return uniprotId;
	}
	
	public Set<Protein> getIteractions() {
		return unmodifiableSet(iteractions);
	}

	public Set<GeneInformation> getGenes() {
		return unmodifiableSet(genes);
	}
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((uniprotId == null) ? 0 : uniprotId.hashCode());
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
		Protein other = (Protein) obj;
		if (uniprotId == null) {
			if (other.uniprotId != null)
				return false;
		} else if (!uniprotId.equals(other.uniprotId))
			return false;
		return true;
	}
}
