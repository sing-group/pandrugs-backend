/*-
 * #%L
 * PanDrugs Backend
 * %%
 * Copyright (C) 2015 - 2018 Fátima Al-Shahrour, Elena Piñeiro, Daniel Glez-Peña and Miguel Reboiro-Jato
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

package es.uvigo.ei.sing.pandrugs.persistence.entity;

import java.util.Arrays;

public enum DrugStatus {
	EXPERIMENTAL(true, "in pre-clinical studies", "Experimental"),
	APPROVED(true, "approved by FDA", "Approved"),
	CLINICAL_TRIALS(true, "in Clinical Trials", "Clinical Trials"),
	WITHDRAWN(false, null, "Withdrawn"),
	UNDEFINED(false, null, "Undefined");
	
	private final boolean active;
	private final String description;
	private final String title;
	
	private DrugStatus(boolean active, String description, String title) {
		this.active = active;
		this.description = description;
		this.title = title;
	}
	
	public boolean isActive() {
		return this.active;
	}
	
	public String getDescription() {
		return description;
	}
	
	public String getTitle() {
		return title;
	}
	
	public static DrugStatus[] activeDrugStatus() {
		return Arrays.stream(values())
			.filter(DrugStatus::isActive)
		.toArray(DrugStatus[]::new);
	}
	
	@Override
	public String toString() {
		final String name = name();
		
		return name.charAt(0) + name.substring(1).toLowerCase();
	}
}
