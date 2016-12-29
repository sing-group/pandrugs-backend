/*
 * #%L
 * PanDrugs Backend
 * %%
 * Copyright (C) 2015 - 2016 Fátima Al-Shahrour, Elena Piñeiro, Daniel Glez-Peña and Miguel Reboiro-Jato
 * %%
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public
 * License along with this program.  If not, see
 * <http://www.gnu.org/licenses/gpl-3.0.html>.
 * #L%
 */
package es.uvigo.ei.sing.pandrugs.matcher.hamcrest;

import java.util.stream.StreamSupport;

import org.hamcrest.Factory;
import org.hamcrest.Matcher;

import es.uvigo.ei.sing.pandrugs.persistence.entity.ClinicalGenomeVariation;

public class IsEqualToClinicalGenomeVariation extends IsEqualToEntity<ClinicalGenomeVariation> {
	public IsEqualToClinicalGenomeVariation(ClinicalGenomeVariation expected) {
		super(expected);
	}
	
	@Override
	protected boolean matchesSafely(ClinicalGenomeVariation actual) {
		this.clearDescribeTo();
		
		if (actual == null) {
			this.addTemplatedDescription("actual", expected.toString());
			return false;
		} else {
			return checkAttribute("chromosome", ClinicalGenomeVariation::getChromosome, actual)
				&& checkAttribute("start", ClinicalGenomeVariation::getStart, actual)
				&& checkAttribute("end", ClinicalGenomeVariation::getEnd, actual)
				&& checkAttribute("hgvs", ClinicalGenomeVariation::getHgvs, actual)
				&& checkAttribute("disease", ClinicalGenomeVariation::getDisease, actual)
				&& checkAttribute("accession", ClinicalGenomeVariation::getAccession, actual)
				&& checkAttribute("clinical_significance", ClinicalGenomeVariation::getClinicalSignificance, actual)
				&& checkAttribute("db_snp", ClinicalGenomeVariation::getDbSnp, actual);
		}
	}

	@Factory
	public static IsEqualToClinicalGenomeVariation equalToClinicalGenomeVariation(ClinicalGenomeVariation expected) {
		return new IsEqualToClinicalGenomeVariation(expected);
	}
	
	@Factory
	public static Matcher<Iterable<? extends ClinicalGenomeVariation>> containsClinicalGenomeVariations(ClinicalGenomeVariation ... expected) {
		return containsEntityInAnyOrder(IsEqualToClinicalGenomeVariation::equalToClinicalGenomeVariation, expected);
	}
	
	@Factory
	public static Matcher<Iterable<? extends ClinicalGenomeVariation>> containsClinicalGenomeVariations(Iterable<ClinicalGenomeVariation> expected) {
		final ClinicalGenomeVariation[] expectedArray = StreamSupport.stream(expected.spliterator(), false)
			.toArray(ClinicalGenomeVariation[]::new);
		
		return containsEntityInAnyOrder(IsEqualToClinicalGenomeVariation::equalToClinicalGenomeVariation, expectedArray);
	}
}
