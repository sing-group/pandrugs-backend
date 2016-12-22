/*
 * #%L
 * PanDrugsDB Backend
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
package es.uvigo.ei.sing.pandrugsdb.persistence.entity;

import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;

@Entity(name = "variants_score_user_computation")
public class VariantsScoreUserComputation {

	@Id
	private String id;

	private String name;

	@ManyToOne
	private User user;
	
	@Embedded
	private VariantsScoreComputationDetails computationDetails =
		new VariantsScoreComputationDetails();

	VariantsScoreUserComputation() { }

	public VariantsScoreUserComputation(String id) {
		this.id = id;
	}

	public VariantsScoreUserComputation(
			String id,
			User user,
			VariantsScoreComputationDetails details) {

		this.id = id;
		this.user = user;
		this.computationDetails = details;

	}

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	public String getName() { return name; }

	public void setName(String name) { this.name = name; }

	public VariantsScoreComputationDetails getComputationDetails() {
		return computationDetails;
	}

	public void setComputationDetails(VariantsScoreComputationDetails computationDetails) {
		this.computationDetails = computationDetails;
	}

	public String getId() {
		return id;
	}
}
