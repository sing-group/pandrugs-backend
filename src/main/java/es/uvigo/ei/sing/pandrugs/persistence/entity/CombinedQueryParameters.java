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
package es.uvigo.ei.sing.pandrugs.persistence.entity;

import java.nio.file.Path;
import java.nio.file.Paths;

import javax.persistence.Column;
import javax.persistence.Embeddable;

@Embeddable
public class CombinedQueryParameters {

    @Column(name = "combined_analysis_cnv_file")
    private String cnvTsvFile;

    @Column(name = "combined_analysis_expression_data_file")
    private String expressionDataFile;

    public CombinedQueryParameters() {
    }

    public Path getCnvTsvFile() {
        return Paths.get(this.cnvTsvFile);
    }

    public boolean hasCnvTsvFile() {
        return this.cnvTsvFile != null;
    }

    public void setCnvTsvFile(Path cnvTsvFile) {
        this.cnvTsvFile = cnvTsvFile.toString();
    }

    public Path getExpressionDataFile() {
        return Paths.get(this.expressionDataFile);
    }

    public boolean hasExpressionDataFile() {
        return this.expressionDataFile != null;
    }

    public void setExpressionDataFile(Path expressionDataFile) {
        this.expressionDataFile = expressionDataFile.toString();
    }
}
