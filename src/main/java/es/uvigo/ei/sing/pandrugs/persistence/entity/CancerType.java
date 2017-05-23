/*
 * #%L
 * PanDrugs Backend
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
package es.uvigo.ei.sing.pandrugs.persistence.entity;

import static java.util.Arrays.stream;

public enum CancerType {
	ADRENAL_GLAND(true),
	BLADDER(true),
	BLOOD(true),
	BONE(true),
	BONE_MARROW(true),
	BRAIN(true),
	BREAST(true),
	CANCER(false), // Added
	CERVIX(true),
	CLINICAL_CANCER(false), // Added
	COLON(true),
	HEAD_AND_NECK(true),
	INTESTINE(true),
	KIDNEY(true),
	LIVER(true),
	LUNG(true),
	NECK(true),
	NERVOUS_SYSTEM(true),
	OVARY(true),
	PANCREAS(true),
	PROSTATE(true),
	RECTUM(true),
	RETINA(true),
	SKIN(true),
	SOFT_TISSUE(true),
	STOMACH(true),
	TESTIS(true),
	THYROID(true),
	UTERUS(true);
	
	private boolean canBeQueried;
	
	private CancerType(boolean canBeQueried) {
		this.canBeQueried = canBeQueried;
	}
	
	public boolean canBeQueried() {
		return this.canBeQueried;
	}
	
	public static CancerType[] getQueryableCancerTypes() {
		return stream(values())
			.filter(CancerType::canBeQueried)
		.toArray(CancerType[]::new);
	}
}
