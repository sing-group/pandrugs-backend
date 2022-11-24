/*-
 * #%L
 * PanDrugs Backend
 * %%
 * Copyright (C) 2015 - 2022 Fátima Al-Shahrour, Elena Piñeiro, Daniel Glez-Peña
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
package es.uvigo.ei.sing.pandrugs.core.variantsanalysis.pharmcat;

import static es.uvigo.ei.sing.pandrugs.core.variantsanalysis.pharmcat.GermLineAnnotation.MODERATELY_NOT_RECOMMENDED;
import static es.uvigo.ei.sing.pandrugs.core.variantsanalysis.pharmcat.GermLineAnnotation.MODERATELY_RECOMMENDED;
import static es.uvigo.ei.sing.pandrugs.core.variantsanalysis.pharmcat.GermLineAnnotation.STRONGLY_NOT_RECOMMENDED;
import static es.uvigo.ei.sing.pandrugs.core.variantsanalysis.pharmcat.GermLineAnnotation.STRONGLY_RECOMMENDED;
import static es.uvigo.ei.sing.pandrugs.core.variantsanalysis.pharmcat.GermLineAnnotation.WARNING;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class PharmCatAnnotation {

    private String drugName;
    private GermLineAnnotation germLineAnnotation;
    private Optional<ReportAnnotation> reportAnnotation;

    public PharmCatAnnotation(String drugName, GermLineAnnotation germLineAnnotation) {
        this(drugName, germLineAnnotation, null);
    }

    public PharmCatAnnotation(String drugName, GermLineAnnotation germLineAnnotation, ReportAnnotation annotation) {
        this.drugName = drugName;
        this.germLineAnnotation = germLineAnnotation;
        this.reportAnnotation = Optional.ofNullable(annotation);
    }

    public static PharmCatAnnotation fromData(String drugName, List<ReportAnnotation> list) {
        ReportAnnotation firsAnnotation = list.get(0);

        if (firsAnnotation instanceof WarningReportAnnotation) {
            return new PharmCatAnnotation(drugName, WARNING);
        }

        if (list.size() > 1) {
            for (int i = 1; i < list.size(); i++) {
                if (!list.get(i).getClassification().equals(firsAnnotation.getClassification())
                        || !list.get(i).getDrugRecommendation().equals(firsAnnotation.getDrugRecommendation())) {
                    return new PharmCatAnnotation(drugName, WARNING);
                }
            }

            String mergedPopulatioString = list.stream()
                    .map(ReportAnnotation::getPopulation)
                    .map(String::trim)
                    .distinct()
                    .collect(Collectors.joining(", "));

            ReportAnnotation mergedReportAnnotation = new ReportAnnotation(firsAnnotation.getDrugRecommendation(),
                    firsAnnotation.getClassification(), mergedPopulatioString, firsAnnotation.getSource());

            return new PharmCatAnnotation(drugName, germLineAnnotation(mergedReportAnnotation), mergedReportAnnotation);
        } else {
            return new PharmCatAnnotation(drugName, germLineAnnotation(firsAnnotation), firsAnnotation);
        }
    }

    public static GermLineAnnotation germLineAnnotation(ReportAnnotation annotation) {
        String recommendation = annotation.getDrugRecommendation();
        String classification = annotation.getClassification();

        boolean recommended = true;
        if (recommendation.endsWith("is not recommended") || recommendation.endsWith("is not recommended.")
                || recommendation.startsWith("avoid") || recommendation.startsWith("Avoid")) {
            recommended = false;
        }
        if (classification.equals("Strong")) {
            return recommended ? STRONGLY_RECOMMENDED : STRONGLY_NOT_RECOMMENDED;
        } else if (classification.equals("Moderate")) {
            return recommended ? MODERATELY_RECOMMENDED : MODERATELY_NOT_RECOMMENDED;
        } else {
            throw new IllegalArgumentException("The classification should be Strong or Moderate");
        }
    }

    public String getDrugName() {
        return drugName;
    }

    public GermLineAnnotation getGermLineAnnotation() {
        return germLineAnnotation;
    }

    public Optional<ReportAnnotation> getReportAnnotation() {
        return reportAnnotation;
    }
}
