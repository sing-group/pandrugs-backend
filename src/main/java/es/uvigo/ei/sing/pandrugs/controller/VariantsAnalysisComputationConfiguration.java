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
package es.uvigo.ei.sing.pandrugs.controller;

import static java.util.Objects.requireNonNull;

import java.io.InputStream;
import java.util.Optional;

import es.uvigo.ei.sing.pandrugs.service.entity.UserLogin;

public class VariantsAnalysisComputationConfiguration {
    private UserLogin userLogin;
    private InputStream vcfFileInputStream;
    private boolean pharmCat;
    private String computationName;
    private String resultsURLTemplate;

    private InputStream tsvFileInputStream;
    private InputStream cnvTsvFileInputStream;
    private InputStream expressionDataRnkFileInputStream;

    public VariantsAnalysisComputationConfiguration(
        UserLogin userLogin,
        InputStream vcfFileInputStream,
        boolean pharmCat,
        String computationName
    ) {
        this(userLogin, vcfFileInputStream, pharmCat, computationName, null);
    }

    public VariantsAnalysisComputationConfiguration(
        UserLogin userLogin,
        InputStream vcfFileInputStream,
        boolean pharmCat,
        String computationName,
        String resultsURLTemplate
    ) {
        this.userLogin = requireNonNull(userLogin);
        this.vcfFileInputStream = requireNonNull(vcfFileInputStream);
        this.pharmCat = pharmCat;
        this.computationName = requireNonNull(computationName);
        this.resultsURLTemplate = requireNonNull(resultsURLTemplate);
    }

    public UserLogin getUserLogin() {
        return userLogin;
    }

    public InputStream getVcfFileInputStream() {
        return vcfFileInputStream;
    }

    public boolean isPharmCat() {
        return pharmCat;
    }

    public String getComputationName() {
        return computationName;
    }

    public String getResultsURLTemplate() {
        return resultsURLTemplate;
    }

    public Optional<InputStream> getTsvFileInputStream() {
        return Optional.ofNullable(tsvFileInputStream);
    }

    public void setTsvFileInputStream(InputStream tsvFileInputStream) {
        this.tsvFileInputStream = tsvFileInputStream;
    }

    public Optional<InputStream> getCnvTsvFileInputStream() {
        return Optional.ofNullable(cnvTsvFileInputStream);
    }

    public void setCnvTsvFileInputStream(InputStream cnvTsvFileInputStream) {
        this.cnvTsvFileInputStream = cnvTsvFileInputStream;
    }

    public Optional<InputStream> getExpressionDataRnkFileInputStream() {
        return Optional.ofNullable(expressionDataRnkFileInputStream);
    }
    public void setExpressionDataRnkFileInputStream(InputStream expressionDataRnkFileInputStream) {
        this.expressionDataRnkFileInputStream = expressionDataRnkFileInputStream;
    }
}
