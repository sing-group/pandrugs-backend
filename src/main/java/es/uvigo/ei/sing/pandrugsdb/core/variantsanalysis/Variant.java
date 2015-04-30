package es.uvigo.ei.sing.pandrugsdb.core.variantsanalysis;

import java.util.Set;

public class Variant {

	private String sequenceName;
	private long position;
	
	private String referenceAllele;
	private Set<String> alternativeAlleles;
	
	public Variant(String sequenceName, long position, String referenceAllele,
			Set<String> alternativeAlleles) {
		super();
		this.sequenceName = sequenceName;
		this.position = position;
		this.referenceAllele = referenceAllele;
		this.alternativeAlleles = alternativeAlleles;
	}
	
	public String getSequenceName() {
		return sequenceName;
	}
	
	public long getPosition() {
		return position;
	}
	
	public String getReferenceAllele() {
		return referenceAllele;
	}
	
	public Set<String> getAlternativeAlleles() {
		return alternativeAlleles;
	}
	
}
