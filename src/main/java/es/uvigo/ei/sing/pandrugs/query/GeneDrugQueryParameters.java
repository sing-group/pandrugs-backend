/*-
 * #%L
 * PanDrugs Backend
 * %%
 * Copyright (C) 2015 - 2023 Fátima Al-Shahrour, Elena Piñeiro, Daniel Glez-Peña
 * and Miguel Reboiro-Jato
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

package es.uvigo.ei.sing.pandrugs.query;

import static es.uvigo.ei.sing.pandrugs.persistence.entity.CancerType.getQueryableCancerTypes;
import static java.util.Arrays.asList;
import static java.util.Arrays.stream;
import static java.util.stream.Collectors.joining;
import static java.util.stream.Collectors.toSet;

import java.util.Optional;
import java.util.Set;

import es.uvigo.ei.sing.pandrugs.persistence.entity.CancerType;
import es.uvigo.ei.sing.pandrugs.persistence.entity.DrugStatus;

public class GeneDrugQueryParameters {
	public static final CancerType[] DEFAULT_CANCER_TYPES = getQueryableCancerTypes();
	public static final DrugStatus[] DEFAULT_CANCER_DRUG_STATUS =
		new DrugStatus[] { DrugStatus.APPROVED, DrugStatus.CLINICAL_TRIALS };
	public static final DrugStatus[] DEFAULT_NON_CANCER_DRUG_STATUS =
		new DrugStatus[] { DrugStatus.APPROVED, DrugStatus.CLINICAL_TRIALS, DrugStatus.EXPERIMENTAL };
	public static final boolean DEFAULT_BIOMARKER = true;
	public static final boolean DEFAULT_PATHWAY_MEMBER = true;
	public static final boolean DEFAULT_DIRECT_TARGET = true;
	public static final boolean DEFAULT_GENE_DEPENDENCY = true;

	private final DrugStatus[] cancerDrugStatus;
	private final DrugStatus[] nonCancerDrugStatus;
	private final CancerType[] cancerTypes;
	private final boolean biomarker;
	private final boolean pathwayMember;
	private final boolean directTarget;
	private final boolean geneDependency;
	
	public GeneDrugQueryParameters() {
		this(
			DEFAULT_CANCER_DRUG_STATUS,
			DEFAULT_NON_CANCER_DRUG_STATUS,
			DEFAULT_CANCER_TYPES,
			DEFAULT_DIRECT_TARGET,
			DEFAULT_BIOMARKER,
			DEFAULT_PATHWAY_MEMBER,
			DEFAULT_GENE_DEPENDENCY
		);
	}
	
	public GeneDrugQueryParameters(
		Set<String> cancerDrugStatus,
		Set<String> nonCancerDrugStatus,
		Set<String> cancerTypes,
		boolean directTarget,
		boolean biomarker,
		boolean pathwayMember,
		boolean geneDependency
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
			parseCancerTypes(
				cancerTypes,
				DEFAULT_CANCER_TYPES,
				"Invalid cancer types"
			),
			directTarget,
			biomarker,
			pathwayMember,
			geneDependency
		);
	}
	
	public GeneDrugQueryParameters(
		DrugStatus[] cancerDrugStatus,
		DrugStatus[] nonCancerDrugStatus,
		CancerType[] cancerTypes,
		boolean directTarget,
		boolean biomarker,
		boolean pathwayMember,
		boolean geneDependency
	) {
		this.cancerDrugStatus = Optional.ofNullable(cancerDrugStatus)
			.orElse(DEFAULT_CANCER_DRUG_STATUS);
		this.nonCancerDrugStatus = Optional.ofNullable(nonCancerDrugStatus)
			.orElse(DEFAULT_NON_CANCER_DRUG_STATUS);
		this.cancerTypes = Optional.ofNullable(cancerTypes)
			.orElse(DEFAULT_CANCER_TYPES);
		this.biomarker = biomarker;
		this.pathwayMember = pathwayMember;
		this.directTarget = directTarget;
		this.geneDependency = geneDependency;
		
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
	
	public boolean areAllDrugStatusIncluded() {
		return this.isAnyCancerDrugStatus() && this.isAnyNonCancerDrugStatus();
	}
	
	public boolean areCancerDrugStatusIncluded() {
		return this.cancerDrugStatus.length > 0;
	}
	
	public boolean areNonCancerDrugStatusIncluded() {
		return this.nonCancerDrugStatus.length > 0;
	}
	
	public CancerType[] getCancerTypes() {
		return cancerTypes;
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
	
	public boolean isBiomarker() {
		return biomarker;
	}

	public boolean isPathwayMember() {
		return pathwayMember;
	}

	public boolean isDirectTarget() {
		return directTarget;
	}
	
	public boolean isGeneDependency() {
		return geneDependency;
	}

	private final static CancerType[] parseCancerTypes(
		Set<String> cancerTypes, CancerType[] defaultValues, String parseErrorMessage
	) {
		if (cancerTypes == null || cancerTypes.isEmpty()) {
			return defaultValues;
		} else {
			try {
				final CancerType[] converted = cancerTypes.stream()
					.map(String::toUpperCase)
					.map(CancerType::valueOf)
				.toArray(CancerType[]::new);
				
				if (hasInvalidCancerType(converted)) {
					final String invalidCancerTypes = stream(converted)
						.filter(ct -> !ct.canBeQueried())
						.map(CancerType::name)
					.collect(joining(", "));
					
					if (!invalidCancerTypes.isEmpty()) {
						throw new IllegalArgumentException("Invalid cancer types: " + invalidCancerTypes);
					}
				}
				
				return converted;
			} catch (RuntimeException e) {
				throw new IllegalArgumentException(parseErrorMessage, e);
			}
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
	
	private static boolean hasInvalidCancerType(CancerType[] cancerTypes) {
		return !stream(cancerTypes)
			.allMatch(CancerType::canBeQueried);
	}

	public boolean areAllCancerTypesIncluded() {
		final Set<CancerType> selected = stream(this.cancerTypes).collect(toSet());
		final Set<CancerType> queryable = stream(getQueryableCancerTypes()).collect(toSet());
		
		return selected.equals(queryable);
	}

	public boolean areDirectIncluded() {
		return this.biomarker || this.directTarget;
	}

	public boolean areIndirectIncluded() {
		return this.pathwayMember || this.geneDependency;
	}

	public boolean areTargetIncluded() {
		return this.directTarget || this.pathwayMember || this.geneDependency;
	}

	public boolean areMarkerIncluded() {
		return this.biomarker;
	}
}
