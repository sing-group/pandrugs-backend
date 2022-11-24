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
import javax.persistence.Transient;

@Embeddable
public class PharmCatComputationParameters {

    @Column(name = "parameter_pharmcat")
    private boolean pharmCat = false;

    @Column(name = "parameter_pharmcat_phenotyper_file")
    private String pharmCatPhenotyperTsvFile;

    @Transient
    private Path resultsBasePath;

    public PharmCatComputationParameters() {
    }

    public boolean isPharmCat() {
        return pharmCat;
    }

    public void setPharmCat(boolean pharmCat) {
        this.pharmCat = pharmCat;
    }

    public Path getPharmCatPhenotyperTsvFile() {
        return Paths.get(this.pharmCatPhenotyperTsvFile);
    }

    public boolean hasPharmCatPhenotyperTsvFile() {
        return this.pharmCatPhenotyperTsvFile != null;
    }

    public void setPharmCatPhenotyperTsvFile(Path pharmCatPhenotyperTsvFile) {
        this.pharmCatPhenotyperTsvFile = pharmCatPhenotyperTsvFile.toString();
    }
}
