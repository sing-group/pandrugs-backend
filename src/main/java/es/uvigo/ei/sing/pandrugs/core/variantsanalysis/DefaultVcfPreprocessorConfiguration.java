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
package es.uvigo.ei.sing.pandrugs.core.variantsanalysis;

import static java.util.Objects.requireNonNull;

import java.nio.file.Path;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import es.uvigo.ei.sing.pandrugs.util.ContextParameter;
import es.uvigo.ei.sing.pandrugs.util.ContextParameterName;

@Component
public class DefaultVcfPreprocessorConfiguration implements VcfPreprocessorConfiguration {
    public static final String VFC_CHECK_PREPROCESS_COMMAND_TEMPLATE_1_PARAMETER = "vcf.check.preprocess.command.template";

    @Autowired
    @ContextParameter
    @ContextParameterName(VFC_CHECK_PREPROCESS_COMMAND_TEMPLATE_1_PARAMETER)
    private String commandTemplate;

    @Override
    public String createCheckAndPreprocessCommand(Path inputVcf, boolean pharmCat) {
        String commandTemplate = requireNonNull(
            this.commandTemplate,
            "The context init parameter " + VFC_CHECK_PREPROCESS_COMMAND_TEMPLATE_1_PARAMETER
                    + " was not found. Please configure it in your server configuration");

        if (!inputVcf.isAbsolute()) {
            throw new IllegalArgumentException("The input VCF must be an absolute path, given: " + inputVcf);
        }

        return String.format(commandTemplate, inputVcf.toString(), pharmCat ? "yes" : "no");
    }

    public void setCommandTemplate(String commandTemplate) {
        this.commandTemplate = commandTemplate;
    }
}
