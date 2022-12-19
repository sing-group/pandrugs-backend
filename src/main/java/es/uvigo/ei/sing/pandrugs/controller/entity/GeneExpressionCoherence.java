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
package es.uvigo.ei.sing.pandrugs.controller.entity;

import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

public class GeneExpressionCoherence {

    private String gene;
    private String geneDriverAnnotation;
    private String snvAnnotation;
    private String cnvAnnotation;
    private String expressionAnnotation;

    public GeneExpressionCoherence(String gene, String geneDriverAnnotation, String snvAnnotation, String cnvAnnotation,
        String expressionAnnotation
    ) {
        this.gene = gene;
        this.geneDriverAnnotation = geneDriverAnnotation;
        this.snvAnnotation = snvAnnotation;
        this.cnvAnnotation = cnvAnnotation;
        this.expressionAnnotation = expressionAnnotation;
    }

    public String getCoherence() {
        return this.toString();
    }

    @Override
    public String toString() {
        List<String> annotations = new LinkedList<>();
        if (!this.snvAnnotation.isEmpty()) {
            annotations.add(snvText());
        }
        if (!this.cnvAnnotation.isEmpty()) {
            annotations.add(cnvText());
        }
        if (!this.expressionAnnotation.isEmpty()) {
            annotations.add(expressionText());
        }
        String annotationsString = annotations.stream().collect(Collectors.joining(", "));

        StringBuilder coherence = new StringBuilder(gene);
        String driverText = geneDriverText();
        if(!driverText.isEmpty()) {
            coherence.append(" ").append(driverText);
        }
        coherence.append(" is ");
        if (annotations.size() == 1) {
            coherence.append(annotationsString);
        } else if (annotations.size() == 2) {
            coherence.append(annotationsString.substring(0, annotationsString.lastIndexOf(",")));
            coherence.append(" and it is ");
            coherence.append(annotationsString.substring(annotationsString.lastIndexOf(",") + 2));
        } else {
            coherence.append(annotationsString.substring(0, annotationsString.indexOf(",")));
            coherence.append(" and it is ");
            coherence.append(annotationsString.substring(annotationsString.indexOf(",") + 2, annotationsString.lastIndexOf(",")));
            coherence.append(" and ");
            coherence.append(annotationsString.substring(annotationsString.lastIndexOf(",") + 2));
        }

        return coherence.toString();
    }

    public String getSnvAnnotation() {
        return snvAnnotation;
    }

    public String getCnvAnnotation() {
        return cnvAnnotation;
    }

    public String getExpressionAnnotation() {
        return expressionAnnotation;
    }

    private String snvText() {
        return this.snvAnnotation.toLowerCase();
    }

    public String getGeneDriverAnnotation() {
        return this.geneDriverAnnotation;
    }

    private String geneDriverText() {
        if(this.geneDriverAnnotation.equals("ONC")) {
            return "(oncogene)";
        } else if(this.geneDriverAnnotation.equals("TSG")) {
            return "(tumor suppressor gene)";
        } else if(this.geneDriverAnnotation.equals("UNCLASSIFIED")) {
            return "";
        } else {
            return "not specified (driver)";
        }
    }

    private String cnvText() {
        if (this.cnvAnnotation.equals("AMP")) {
            return "amplified";
        } else if (this.cnvAnnotation.equals("DEL")) {
            return "deleted";
        }  else if (this.cnvAnnotation.equals("DIPLOID")) {
            return "diploid";
        } else {
            return "not specified (CNV)";
        }
    }

    private String expressionText() {
        if (this.expressionAnnotation.isEmpty()) {
            return "not specified (expression)";

        } else {
            return GeneExpressionAnnotation.valueOf(this.expressionAnnotation).getDescription();
        }
    }
}
