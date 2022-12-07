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
package es.uvigo.ei.sing.pandrugs.service;

import java.util.Map;

import javax.xml.bind.annotation.XmlRootElement;

import es.uvigo.ei.sing.pandrugs.controller.entity.CalculatedGeneAnnotations;
import es.uvigo.ei.sing.pandrugs.controller.entity.CalculatedGeneAnnotations.CalculatedGeneAnnotationType;

@XmlRootElement(name = "calculatedGeneAnnotation", namespace = "https://www.pandrugs.org")
public class CalculatedGeneAnnotation {

    private Map<String, String> cnv;
    private Map<String, String> expression;
    private Map<String, String> snv;
    private Map<String, String> coherence;

    public CalculatedGeneAnnotation() {
    }

    public Map<String, String> getCnv() {
        return cnv;
    }

    public void setCnv(Map<String, String> cnv) {
        this.cnv = cnv;
    }

    public Map<String, String> getExpression() {
        return expression;
    }

    public void setExpression(Map<String, String> expression) {
        this.expression = expression;
    }

    public Map<String, String> getSnv() {
        return snv;
    }

    public void setSnv(Map<String, String> snv) {
        this.snv = snv;
    }

    public void setCoherence(Map<String, String> coherence) {
        this.coherence = coherence;
    }

    public Map<String, String> getCoherence() {
        return coherence;
    }

    public static CalculatedGeneAnnotation from(CalculatedGeneAnnotations annotations) {
        CalculatedGeneAnnotation toret = new CalculatedGeneAnnotation();

        if (annotations.getAnnotations().containsKey(CalculatedGeneAnnotationType.CNV)) {
            toret.setCnv(annotations.getAnnotations().get(CalculatedGeneAnnotationType.CNV));
        }

        if (annotations.getAnnotations().containsKey(CalculatedGeneAnnotationType.EXPRESSION)) {
            toret.setExpression(annotations.getAnnotations().get(CalculatedGeneAnnotationType.EXPRESSION));
        }

        if (annotations.getAnnotations().containsKey(CalculatedGeneAnnotationType.SNV)) {
            toret.setSnv(annotations.getAnnotations().get(CalculatedGeneAnnotationType.SNV));
        }

        if (annotations.getAnnotations().containsKey(CalculatedGeneAnnotationType.COHERENCE)) {
            toret.setCoherence(annotations.getAnnotations().get(CalculatedGeneAnnotationType.COHERENCE));
        }

        return toret;
    }
}
