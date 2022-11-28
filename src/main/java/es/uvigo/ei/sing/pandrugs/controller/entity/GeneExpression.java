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

import static es.uvigo.ei.sing.pandrugs.controller.entity.GeneExpressionAnnotation.HIGHLY_OVEREXPRESSED;
import static es.uvigo.ei.sing.pandrugs.controller.entity.GeneExpressionAnnotation.OVEREXPRESSED;
import static es.uvigo.ei.sing.pandrugs.controller.entity.GeneExpressionAnnotation.UNDEREXPRESSED;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.Collectors;

import org.apache.commons.math3.stat.descriptive.DescriptiveStatistics;

import es.uvigo.ei.sing.pandrugs.service.entity.GeneExpressionData;

public class GeneExpression extends GeneExpressionData {

    public static final double DEFAULT_HIGHLY_OVEREXPRESSED_PERCENTILE = 90;

    private Double highlyOverExpressedThreshold = null;

    public GeneExpression(Map<String, Double> geneExpression) {
        super(geneExpression);
    }

    public double getHighlyOverExpressedThreshold() {
        if (this.highlyOverExpressedThreshold == null) {
            double[] data = this.getGeneExpression().values().stream()
                    .mapToDouble(Double::doubleValue)
                    .toArray();

            DescriptiveStatistics stats = new DescriptiveStatistics(data);

            this.highlyOverExpressedThreshold = stats.getPercentile(DEFAULT_HIGHLY_OVEREXPRESSED_PERCENTILE);
        }
        return this.highlyOverExpressedThreshold;
    }

    public Map<String, GeneExpressionAnnotation> getAnnotations() {
        double threshold = this.getHighlyOverExpressedThreshold();

        Map<String, GeneExpressionAnnotation> toret = new HashMap<>();
        for (Entry<String, Double> entry : this.getGeneExpression().entrySet()) {
            GeneExpressionAnnotation annotation = UNDEREXPRESSED;
            if (entry.getValue() > threshold) {
                annotation = HIGHLY_OVEREXPRESSED;
            } else if (entry.getValue() > 0) {
                annotation = OVEREXPRESSED;
            }
            toret.put(entry.getKey(), annotation);
        }

        return toret;
    }

    public Map<String, String> getAnnotationsAsStrings() {
        Map<String, GeneExpressionAnnotation> annotations = this.getAnnotations();

        return annotations.keySet().stream()
            .collect(Collectors.toMap(k -> k, k -> annotations.get(k).toString()));
    }
}
