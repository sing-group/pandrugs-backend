/*
 * #%L
 * PanDrugsDB Backend
 * %%
 * Copyright (C) 2015 Fátima Al-Shahrour, Elena Piñeiro, Daniel Glez-Peña and Miguel Reboiro-Jato
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
package es.uvigo.ei.sing.pandrugsdb.controller;

import java.net.URL;
import java.util.List;

import javax.inject.Inject;

import es.uvigo.ei.sing.pandrugsdb.core.variantsanalysis.ComputationsStore;
import es.uvigo.ei.sing.pandrugsdb.core.variantsanalysis.VariantsCandidateTherapiesComputation;
import es.uvigo.ei.sing.pandrugsdb.core.variantsanalysis.VariantsCandidateTherapiesComputer;
import es.uvigo.ei.sing.pandrugsdb.persistence.entity.User;

public class DefaultVariantCallingAnalysisController implements
		VariantsAnalysisController {

	@Inject
	private ComputationsStore computationStore;
	
	@Inject
	private VariantsCandidateTherapiesComputer 
		variantCallingCandidateTherapiesComputer;
	
	@Override
	public VariantsCandidateTherapiesComputation 
		startCandidateTherapiesComputation(User user, URL vcfFile) {
		
		final VariantsCandidateTherapiesComputation computation = 
				variantCallingCandidateTherapiesComputer.createComputation(vcfFile);

		computationStore.storeComputation(computation, user);
		
		return computation;
	}


	@Override
	public List<VariantsCandidateTherapiesComputation> getComputations(
			User user) {
		return computationStore.retrieveComputations(user);
	}
}
