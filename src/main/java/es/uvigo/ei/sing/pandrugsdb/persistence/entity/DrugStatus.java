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
package es.uvigo.ei.sing.pandrugsdb.persistence.entity;

public enum DrugStatus {
	EXPERIMENTAL(true, "in pre-clinical studies"),
	APPROVED(true, "approved by FDA"),
	CLINICAL(true, "in Clinical Trials"),
	WITHDRAWN(false, null),
	UNDEFINED(false, null);
	
	private final boolean active;
	private final String description;
	
	private DrugStatus(boolean active, String description) {
		this.active = active;
		this.description = description;
	}
	
	public boolean isActive() {
		return this.active;
	}
	
	public String getDescription() {
		return description;
	}
	
	@Override
	public String toString() {
		final String name = name();
		
		return name.charAt(0) + name.substring(1).toLowerCase();
	}
}
