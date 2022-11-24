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

public class ReportAnnotation {
    private String drugRecommendation;
    private String classification;
    private String population;
    private String source;

    public ReportAnnotation(String drugRecommendation, String classification, String population, String source) {
        this.drugRecommendation = drugRecommendation;
        this.classification = classification;
        this.population = population;
        this.source = source;
    }

    public String getDrugRecommendation() {
        return drugRecommendation;
    }

    public String getClassification() {
        return classification;
    }

    public String getPopulation() {
        return population;
    }

    public String getSource() {
        return source;
    }

    @Override
    public String toString() {
        return "{" +
                " drugRecommendation='" + getDrugRecommendation() + "'" +
                ", classification='" + getClassification() + "'" +
                ", population='" + getPopulation() + "'" +
                ", source='" + getSource() + "'" +
                "}";
    }
}
