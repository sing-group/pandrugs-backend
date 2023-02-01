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
package es.uvigo.ei.sing.pandrugs.core.variantsanalysis.pharmcat;

import static java.util.Objects.requireNonNull;

import java.nio.file.Path;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import es.uvigo.ei.sing.pandrugs.util.ContextParameter;
import es.uvigo.ei.sing.pandrugs.util.ContextParameterName;

@Component
public class DefaultPharmCatConfiguration implements PharmCatConfiguration {
    public static final String PHARMCAT_COMMAND_TEMPLATE_1_PARAMETER = "pharmcat.command.template.1";
    public static final String PHARMCAT_COMMAND_TEMPLATE_2_PARAMETER = "pharmcat.command.template.2";

    @Autowired
    @ContextParameter
    @ContextParameterName(PHARMCAT_COMMAND_TEMPLATE_1_PARAMETER)
    private String pharmCatCommandTemplate1;

    @Autowired
    @ContextParameter
    @ContextParameterName(PHARMCAT_COMMAND_TEMPLATE_2_PARAMETER)
    private String pharmCatCommandTemplate2;

    @Override
    public String createPharmCatCommand(Path inputVCF) {
        String commandTemplate = requireNonNull(
                pharmCatCommandTemplate1,
                "The context init parameter " + PHARMCAT_COMMAND_TEMPLATE_1_PARAMETER + " was not found. Please" +
                        " configure it in your server configuration");

        return createPharmCatCommand(commandTemplate, inputVCF, Optional.empty());
    }

    @Override
    public String createPharmCatCommand(Path inputVCF, Path inputPhenotyperOutsideCalFile) {
        String commandTemplate = requireNonNull(
                pharmCatCommandTemplate2,
                "The context init parameter " + PHARMCAT_COMMAND_TEMPLATE_2_PARAMETER + " was not found. Please" +
                        " configure it in your server configuration");

        return createPharmCatCommand(commandTemplate, inputVCF, Optional.of(inputPhenotyperOutsideCalFile));

    }

    private String createPharmCatCommand(String commandTemplate, Path inputVCF,
            Optional<Path> inputPhenotyperOutsideCalFile) {
        if (!inputVCF.isAbsolute()) {
            throw new IllegalArgumentException("PharmCat inputVCF must be an absolute path, given: " + inputVCF);
        }
        
        if (inputPhenotyperOutsideCalFile.isPresent()) {
            if (!inputPhenotyperOutsideCalFile.get().isAbsolute()) {
                throw new IllegalArgumentException(
                        "PharmCat inputPhenotyperOutsideCalFile must be an absolute path, given: "
                                + inputPhenotyperOutsideCalFile.get());
            }

            return String.format(commandTemplate, inputVCF.toString(), inputPhenotyperOutsideCalFile.get().toString());
        } else {
            return String.format(commandTemplate, inputVCF.toString());
        }

    }

    public void setpharmCatCommandTemplate1(String pharmCatCommandTemplate1) {
        this.pharmCatCommandTemplate1 = pharmCatCommandTemplate1;
    }

    public void setpharmCatCommandTemplate2(String pharmCatCommandTemplate2) {
        this.pharmCatCommandTemplate2 = pharmCatCommandTemplate2;
    }
}
