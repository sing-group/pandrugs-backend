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
package es.uvigo.ei.sing.pandrugs.controller.entity;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import es.uvigo.ei.sing.pandrugs.controller.entity.CalculatedGeneAnnotations.CalculatedGeneAnnotationType;
import es.uvigo.ei.sing.pandrugs.persistence.dao.GeneDAO;
import es.uvigo.ei.sing.pandrugs.persistence.entity.DriverGene;
import es.uvigo.ei.sing.pandrugs.persistence.entity.Gene;
import es.uvigo.ei.sing.pandrugs.service.entity.CnvData;

public class MultiOmicsAnalysisQueryData {

    public static final String DEFAULT_NOT_CNV_LABEL = "DIPLOID";

    private static final String DEFAULT_NOT_EXPRESSION_LABEL = GeneExpressionAnnotation.NOT_EXPRESSED.toString();
    private static final String DEFAULT_NOT_MUTATED_LABEL = SnvAnnotation.NOT_MUTATED.toString();
    
    private GeneDAO geneDao;

    private CnvData cnvData;
    private GeneExpression geneExpression;
    private Map<String, String> snvData;

    private CalculatedGeneAnnotations calculatedGeneAnnotations;
    private Set<String> queryGenes;

    public MultiOmicsAnalysisQueryData(GeneDAO geneDao, CnvData cnvData, GeneExpression geneExpression) {
        this(geneDao, cnvData, geneExpression, null);
    }

    public MultiOmicsAnalysisQueryData(GeneDAO geneDao, CnvData cnvData, GeneExpression geneExpression, Map<String, Double> vcfGeneRank) {
        this.cnvData = cnvData;
        this.geneExpression = geneExpression;
        this.initializeSnvAnnotations(vcfGeneRank);
        this.geneDao = geneDao;
    }

    private void initializeSnvAnnotations(Map<String, Double> vcfGeneRank) {
        if (vcfGeneRank == null) {
            return;
        }

        this.snvData = vcfGeneRank.keySet().stream()
            .collect(Collectors.toMap(g -> g, g -> SnvAnnotation.MUTATED.toString()));
    }

    public CalculatedGeneAnnotations getCalculatedGeneAnnotations() {
        if (this.calculatedGeneAnnotations == null) {
            this.calculatedGeneAnnotations = new CalculatedGeneAnnotations();

            if (this.cnvData != null) {
                this.calculatedGeneAnnotations.addAnnotation(
                    CalculatedGeneAnnotationType.CNV, 
                    cnvData.getDataMap()
                );
            }

            if (this.geneExpression != null) {
                this.calculatedGeneAnnotations.addAnnotation(
                    CalculatedGeneAnnotationType.EXPRESSION, geneExpression.getAnnotationsAsStrings()
                );
            }

            if(this.snvData != null) {
                this.calculatedGeneAnnotations.addAnnotation(
                    CalculatedGeneAnnotationType.SNV, this.snvData
                );
            }

            Map<String, String> coherenceMap = new HashMap<>();

            this.getQueryGenes().forEach(g -> {
                DriverGene driverAnnotation = DriverGene.UNCLASSIFIED;

                Gene dbGene = this.geneDao.get(g);

                if (dbGene != null && dbGene.getDriverGene() != null) {
                    driverAnnotation = dbGene.getDriverGene();
                }
                
                String snv = "";
                if (this.snvData != null) {
                    snv = this.snvData.getOrDefault(g, DEFAULT_NOT_MUTATED_LABEL);
                }

                String cnv = "";
                if(this.cnvData != null) {
                    cnv = this.cnvData.getDataMap().getOrDefault(g, DEFAULT_NOT_CNV_LABEL);
                }

                String expression = "";
                if(this.geneExpression != null) {
                    expression = this.geneExpression.getAnnotationsAsStrings().getOrDefault(g, DEFAULT_NOT_EXPRESSION_LABEL);
                }

                coherenceMap.put(
                    g, 
                    new GeneExpressionCoherence(g, driverAnnotation, snv, cnv, expression).getCoherence()
                );
            });

            this.calculatedGeneAnnotations.addAnnotation(
                CalculatedGeneAnnotationType.COHERENCE, 
                coherenceMap
            );
        }
        return this.calculatedGeneAnnotations;
    }

    public Set<String> getQueryGenes() {
        if (this.queryGenes == null) {
            this.queryGenes = new HashSet<>();

            if (this.snvData != null) {
                this.queryGenes.addAll(this.snvData.keySet());
            }

            if (this.cnvData != null) {
                queryGenes.addAll(this.cnvData.getDataMap().keySet());
            }
        }

        return this.queryGenes;
    }
}
