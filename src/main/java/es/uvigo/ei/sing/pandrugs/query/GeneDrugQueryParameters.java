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
package es.uvigo.ei.sing.pandrugsdb.query;

import static java.util.Arrays.asList;

import java.util.Optional;
import java.util.Set;

import es.uvigo.ei.sing.pandrugsdb.persistence.entity.DrugStatus;

public class GeneDrugQueryParameters {
	public static final DirectIndirectStatus DEFAULT_DIRECT_INDIRECT = DirectIndirectStatus.BOTH;
	public static final TargetMarkerStatus DEFAULT_TARGET_MARKER = TargetMarkerStatus.BOTH;
	public static final DrugStatus[] DEFAULT_CANCER_DRUG_STATUS =
		new DrugStatus[] { DrugStatus.APPROVED, DrugStatus.CLINICAL_TRIALS };
	public static final DrugStatus[] DEFAULT_NON_CANCER_DRUG_STATUS =
		new DrugStatus[] { DrugStatus.APPROVED, DrugStatus.CLINICAL_TRIALS, DrugStatus.EXPERIMENTAL };

	private final DrugStatus[] cancerDrugStatus;
	private final DrugStatus[] nonCancerDrugStatus;
	private final TargetMarkerStatus targetMarker;
	private final DirectIndirectStatus directIndirect;
	
	public GeneDrugQueryParameters() {
		this(
			DEFAULT_CANCER_DRUG_STATUS,
			DEFAULT_NON_CANCER_DRUG_STATUS,
			DEFAULT_TARGET_MARKER,
			DEFAULT_DIRECT_INDIRECT
		);
	}
	
	public GeneDrugQueryParameters(
		Set<String> cancerDrugStatus,
		Set<String> nonCancerDrugStatus,
		String targetMarker,
		String directIndirect
	) {
		this(
			parseDrugStatus(
				cancerDrugStatus,
				DEFAULT_CANCER_DRUG_STATUS,
				"Invalid cancer drug status values"
			),
			parseDrugStatus(
				nonCancerDrugStatus,
				DEFAULT_NON_CANCER_DRUG_STATUS,
				"Invalid non cancer drug status values"
			),
			parseEnum(
				TargetMarkerStatus.class,
				DEFAULT_TARGET_MARKER,
				targetMarker,
				"Invalid target value"
			),
			parseEnum(
				DirectIndirectStatus.class,
				DEFAULT_DIRECT_INDIRECT,
				directIndirect,
				"Invalid direct value"
			)
		);
	}
	
	public GeneDrugQueryParameters(
		DrugStatus[] cancerDrugStatus,
		DrugStatus[] nonCancerDrugStatus,
		TargetMarkerStatus targetMarker,
		DirectIndirectStatus directIndirect
	) {
		this.cancerDrugStatus = Optional.ofNullable(cancerDrugStatus)
			.orElse(DEFAULT_CANCER_DRUG_STATUS);
		this.nonCancerDrugStatus = Optional.ofNullable(nonCancerDrugStatus)
			.orElse(DEFAULT_NON_CANCER_DRUG_STATUS);
		this.targetMarker = Optional.ofNullable(targetMarker)
			.orElse(DEFAULT_TARGET_MARKER);
		this.directIndirect = Optional.ofNullable(directIndirect)
			.orElse(DEFAULT_DIRECT_INDIRECT);
		
		if (!this.areCancerDrugStatusIncluded() && 
			!this.areNonCancerDrugStatusIncluded()
		) {
			throw new IllegalArgumentException("NONE can't be used for cancer "
				+ "and non cancer status at the same time");
		}
	}

	public DrugStatus[] getCancerDrugStatus() {
		return cancerDrugStatus;
	}

	public DrugStatus[] getNonCancerDrugStatus() {
		return nonCancerDrugStatus;
	}

	public DirectIndirectStatus getDirectIndirect() {
		return directIndirect;
	}
	
	public boolean areDirectIncluded() {
		return this.directIndirect != DirectIndirectStatus.INDIRECT;
	}
	
	public boolean areIndirectIncluded() {
		return this.directIndirect != DirectIndirectStatus.DIRECT;
	}
	
	public boolean areTargetIncluded() {
		return this.targetMarker != TargetMarkerStatus.MARKER;
	}
	
	public boolean areMarkerIncluded() {
		return this.targetMarker != TargetMarkerStatus.TARGET;
	}
	
	public boolean areAllDrugStatusIncluded() {
		return this.isAnyCancerDrugStatus() && this.isAnyNonCancerDrugStatus();
	}
	
	public boolean areCancerDrugStatusIncluded() {
		return this.cancerDrugStatus.length > 0;
	}
	
	public boolean areNonCancerDrugStatusIncluded() {
		return this.nonCancerDrugStatus.length > 0;
	}

	public TargetMarkerStatus getTargetMarker() {
		return targetMarker;
	}
	
	public boolean isAnyCancerDrugStatus() {
		return hasAllDrugStatus(this.cancerDrugStatus);
	}
	
	public boolean isAnyNonCancerDrugStatus() {
		return hasAllDrugStatus(this.nonCancerDrugStatus);
	}
	
	private boolean hasAllDrugStatus(DrugStatus[] status) {
		return asList(status).containsAll(asList(DrugStatus.values()));
	}
	
	private final static <T extends Enum<T>> T parseEnum(
		Class<T> enumType, T defaultValue, String value, String parseErrorMessage
	) {
		try {
			return Optional.ofNullable(value)
				.map(String::toUpperCase)
				.map(name -> Enum.valueOf(enumType, name))
			.orElse(defaultValue);
		} catch (RuntimeException iae) {
			throw new IllegalArgumentException(parseErrorMessage);
		}
	}
	
	private final static DrugStatus[] parseDrugStatus(
		Set<String> status, DrugStatus[] defaultValues, String parseErrorMessage
	) {
		if (status == null || status.isEmpty()) {
			return defaultValues;
		} else if (status.contains("NONE")) {
			if (status.size() > 1) {
				throw new IllegalArgumentException(
					"When used, NONE must be the unique status value");
			} else {
				return new DrugStatus[0];
			}
		} else {
			try {
				return status.stream()
					.map(String::toUpperCase)
					.map(DrugStatus::valueOf)
				.toArray(DrugStatus[]::new);
			} catch (RuntimeException e) {
				throw new IllegalArgumentException(parseErrorMessage, e);
			}
		}
	}
}
