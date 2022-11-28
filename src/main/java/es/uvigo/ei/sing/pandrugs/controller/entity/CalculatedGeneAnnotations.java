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

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CalculatedGeneAnnotations {
    public static enum CalculatedGeneAnnotationType {
        CNV
    };

    private Map<CalculatedGeneAnnotationType, Map<String, String>> annotations;

    public CalculatedGeneAnnotations() {
        this(new HashMap<>());
    }

    public CalculatedGeneAnnotations(Map<CalculatedGeneAnnotationType, Map<String, String>> annotations) {
        this.annotations = annotations;
    }

    public Map<CalculatedGeneAnnotationType, Map<String, String>> getAnnotations() {
        return annotations;
    }

    public void addAnnotation(CalculatedGeneAnnotationType type, Map<String, String> annotations) {
        this.annotations.put(type, annotations);
    }

    public CalculatedGeneAnnotations filterByGenes(List<String> genes) {
        Map<CalculatedGeneAnnotationType, Map<String, String>> filteredAnnotations = new HashMap<>();

        for (CalculatedGeneAnnotationType cga : this.annotations.keySet()) {
            Map<String, String> filteredMap = new HashMap<>();
            this.annotations.get(cga)
                    .entrySet().stream()
                    .filter(e -> genes.contains(e.getKey()))
                    .forEach(e -> {
                        filteredMap.put(e.getKey(), e.getValue());
                    });
            filteredAnnotations.put(cga, filteredMap);
        }

        return new CalculatedGeneAnnotations(filteredAnnotations);
    }
}
