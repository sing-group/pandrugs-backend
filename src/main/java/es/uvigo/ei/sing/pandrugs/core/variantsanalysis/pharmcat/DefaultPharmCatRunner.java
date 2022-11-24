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
package es.uvigo.ei.sing.pandrugs.core.variantsanalysis.pharmcat;

import static java.lang.ProcessBuilder.Redirect.appendTo;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;

import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import es.uvigo.ei.sing.pandrugs.core.variantsanalysis.FileSystemConfiguration;
import es.uvigo.ei.sing.pandrugs.persistence.entity.PharmCatResults;


@Component
public class DefaultPharmCatRunner implements PharmCatRunner {
	private final static Logger LOG = LoggerFactory.getLogger(DefaultPharmCatRunner.class);

    public static final String REPORT_HTML = "report.html";
    public static final String REPORT_JSON = "report.json";
	
	@Inject
	private FileSystemConfiguration configuration;

	@Inject
	private PharmCatConfiguration pharmCatConfiguration;

	protected DefaultPharmCatRunner() { }


	public DefaultPharmCatRunner(FileSystemConfiguration configuration) {
		super();
		this.configuration = configuration;
	}

	@Override
	public PharmCatResults pharmCat(Path vcfFile, Path phenotyperOutsideCallFile, Path userPath) {
		File inputFile = configuration.getUserDataBaseDirectory().toPath().resolve(
			userPath.resolve(vcfFile)).toFile();

		String command;
		if (phenotyperOutsideCallFile == null) {
			command = this.pharmCatConfiguration.createPharmCatCommand(inputFile.toPath());
		} else {
			File inputPhenotyperOutsideCallFile = configuration.getUserDataBaseDirectory().toPath().resolve(
					userPath.resolve(phenotyperOutsideCallFile)).toFile();
			command = this.pharmCatConfiguration.createPharmCatCommand(inputFile.toPath(),
					inputPhenotyperOutsideCallFile.toPath());
		}

		try {
			LOG.info("Starting PharmCat computation over " + inputFile + " with command: " + command);
			ProcessBuilder pb = new ProcessBuilder(Arrays.asList(command.split(" ")))
					.redirectErrorStream(true)
					.redirectOutput(
							appendTo(new File(inputFile.getParent() + File.separator + "pharmcat-out.log")));

			int retValue = pb.start().waitFor();

			if (retValue == 130) {
				// bash exit code for CTRL+C (Interruption)
				// When the server stops, these child processes are interrupted also
				// In this case, we will throw an InterruptedException, indicating that
				// this process has no inherent-error, it was deliberately interrupted, so
				// it maybe restarted with the same parameters in the future.
				LOG.error("PharmCat process over " + inputFile + " was interrupted");
				throw new InterruptedException("PharmCat process was interrupted");
			}
			if (retValue != 0) {
				LOG.error("Error during PharmCat computation over " + inputFile + " due to non-zero (" + retValue
						+ ") exit status");
				throw new RuntimeException("PharmCat process had non 0 exit status, exit status: " + retValue);
			}
			LOG.info("Finished PharmCat computation over " + inputFile);
		} catch (IOException | InterruptedException e) {
			LOG.error("Exception during PharmCat computation over " + inputFile + ". Exception: " + e);
			throw new RuntimeException(e);
		}

		return new PharmCatResults(Paths.get("pharmcat.report"));
	}

	@Override
	public PharmCatResults pharmCat(Path vcfFile, Path userPath) {
		return  this.pharmCat(vcfFile, null, userPath);
	}
}
