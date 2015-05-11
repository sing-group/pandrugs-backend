package es.uvigo.ei.sing.pandrugsdb.persistence.entity;

import java.util.Optional;

public interface Weighted {
	public abstract double getWeight();
	
	public static double getWeightOf(Weighted level) {
		return Optional.ofNullable(level)
			.map(Weighted::getWeight)
		.orElse(0d);
	}

}
