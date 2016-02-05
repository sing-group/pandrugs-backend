package es.uvigo.ei.sing.pandrugsdb.service.entity;

import static es.uvigo.ei.sing.pandrugsdb.util.ConversionUtils.toLinkedHashMap;
import static java.util.stream.Collectors.toList;

import java.util.List;
import java.util.Map;

import javax.validation.constraints.NotNull;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "gene-ranking", namespace = "http://sing.ei.uvigo.es/pandrugsdb")
@XmlAccessorType(XmlAccessType.FIELD)
public class GeneRanking {
	@NotNull
	@XmlElement(name = "gene-rank")
	private List<GeneRank> geneRank;
	
	GeneRanking() {}

	public GeneRanking(Map<String, Double> ranking) {
		this.geneRank = ranking.entrySet().stream()
			.map(entry -> new GeneRank(entry.getKey(), entry.getValue()))
		.collect(toList());
	}
	
	public List<GeneRank> getGeneRank() {
		return geneRank;
	}
	
	public Map<String, Double> asMap() {
		return this.geneRank.stream()
			.collect(toLinkedHashMap(GeneRank::getGene, GeneRank::getRank));
	}
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((geneRank == null) ? 0 : geneRank.hashCode());
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
		GeneRanking other = (GeneRanking) obj;
		if (geneRank == null) {
			if (other.geneRank != null)
				return false;
		} else if (!geneRank.equals(other.geneRank))
			return false;
		return true;
	}
}
