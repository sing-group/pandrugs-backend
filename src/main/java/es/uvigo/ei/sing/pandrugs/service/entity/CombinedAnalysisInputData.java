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
package es.uvigo.ei.sing.pandrugs.service.entity;

import javax.validation.constraints.NotNull;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "combinedAnalysisInputData", namespace = "https://www.pandrugs.org")
@XmlAccessorType(XmlAccessType.FIELD)
public class CombinedAnalysisInputData {
    @XmlElement(name = "cnvData")
    private CnvData cnvData;

    @XmlElement(name = "geneExpressionData")
    private GeneExpressionData geneExpressionData;

    public CombinedAnalysisInputData(CnvData cnvData, GeneExpressionData geneExpressionData) {
        this.cnvData = cnvData;
        this.geneExpressionData = geneExpressionData;
    }

    public CnvData getCnvData() {
        return cnvData;
    }

    public GeneExpressionData getGeneExpressionData() {
        return geneExpressionData;
    }
}
