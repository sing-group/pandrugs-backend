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

package es.uvigo.ei.sing.pandrugs.core.variantsanalysis;

import static java.util.Objects.requireNonNull;

import java.nio.file.Path;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import es.uvigo.ei.sing.pandrugs.util.ContextParameter;
import es.uvigo.ei.sing.pandrugs.util.ContextParameterName;
@Component
public class DefaultVEPConfiguration implements VEPConfiguration {

	@Autowired
	@ContextParameter
	@ContextParameterName(VEP_COMMAND_TEMPLATE_PARAMETER)
	private String vepCommandTemplate;

	/**
	 * The context variable where the VEP command template should be stablished.
	 * This template is a command program to run VEP in the machine. The template should use the placeholders
	 * %1$s and %2$s for the input and output files, respectively.
	 *
	 * An example template could be:
	 *
	 * perl /opt/ensembl-tools-release-85/scripts/variant_effect_predictor/variant_effect_predictor.pl
	 * --format vcf --sift b --polyphen b --ccds --uniprot --hgvs --symbol --numbers --domains --regulatory --canonical
	 * --protein --biotype --uniprot --tsl --gmaf --variant_class --xref_refseq --maf_1kg --maf_esp --maf_exac
	 * --dir /opt/ensembl-tools-release-85/scripts/variant_effect_predictor/.vep
	 * --config /opt/ensembl-tools-release-85/scripts/variant_effect_predictor/registry.local
	 * --plugin Condel,/opt/ensembl-tools-release-85/scripts/variant_effect_predictor/
	 * .vep/Plugins/config/Condel/config,b
	 * --fork 8 --offline
	 * --force_overwrite --vcf --no_progress
	 * --buffer_size 20
	 * -i %1$s
	 * --output_file %1$s
	 */
	public static final String VEP_COMMAND_TEMPLATE_PARAMETER = "vep.command.template";

	@Override
	public String createVEPCommand(Path inputVCF, Path outputFileName) {
		String commandTemplate = requireNonNull(
				vepCommandTemplate,
				"The context init parameter " + VEP_COMMAND_TEMPLATE_PARAMETER + " was not found. Please" +
						" configure it in your server configuration");

						if (!inputVCF.isAbsolute()) {
			throw new IllegalArgumentException("inputVCF must be an absolute path, given: " + inputVCF);
		}

		if (!outputFileName.isAbsolute()) {
			throw new IllegalArgumentException("outputFileName must be an absolute path, given: " + outputFileName);
		}

		return String.format(commandTemplate, inputVCF.toString(), outputFileName.toString());
	}

	public void setVepCommandTemplate(String vepCommandTemplate) {
		this.vepCommandTemplate = vepCommandTemplate;
	}
}
