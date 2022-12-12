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

import static java.lang.ProcessBuilder.Redirect.appendTo;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.Arrays;

import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import es.uvigo.ei.sing.pandrugs.controller.DefaultVariantsAnalysisController;
import es.uvigo.ei.sing.pandrugs.util.ContextParameter;
import es.uvigo.ei.sing.pandrugs.util.ContextParameterName;

@Component
public class DefaultVcfPreprocessorRunner implements VcfPreprocessorRunner {
	private final static Logger LOG = LoggerFactory.getLogger(DefaultVcfPreprocessorRunner.class);

    public static final String CONTEXT_PARAM_KEEP_VCFS = "pandrugs.param.keep.vcfs";

    @Autowired
    @ContextParameter
    @ContextParameterName(CONTEXT_PARAM_KEEP_VCFS)
    private String contextParamKeepVcfs;

	@Inject
	private FileSystemConfiguration configuration;

    @Inject
    private VcfPreprocessorConfiguration vcfPreprocessorConfiguration;

	protected DefaultVcfPreprocessorRunner() { }

	public DefaultVcfPreprocessorRunner(FileSystemConfiguration configuration) {
		super();
		this.configuration = configuration;
	}
    
    @Override
    public void checkAndPreprocessVcf(Path vcfFile, boolean pharmCat, Path userPath) {
        File inputFile = configuration.getUserDataBaseDirectory().toPath().resolve(
			userPath.resolve(vcfFile)).toFile();

        String command = this.vcfPreprocessorConfiguration
            .createCheckAndPreprocessCommand(inputFile.toPath(), pharmCat);

        try {
            LOG.info("Starting VCF checking and preprocessing process over " + inputFile + " with command: " + command);
            ProcessBuilder pb = new ProcessBuilder(Arrays.asList(command.split(" ")))
                    .redirectErrorStream(true)
                    .redirectOutput(
                        appendTo(new File(inputFile.getParent() + File.separator + "check-and-preprocess-vcf-out.log")));

            int retValue = pb.start().waitFor();

            if (retValue == 130) {
                // bash exit code for CTRL+C (Interruption)
                // When the server stops, these child processes are interrupted also
                // In this case, we will throw an InterruptedException, indicating that
                // this process has no inherent-error, it was deliberately interrupted, so
                // it maybe restarted with the same parameters in the future.
                LOG.error("VCF checking and preprocessing process over " + inputFile + " was interrupted");
                throw new InterruptedException("VCF checking and preprocessing process was interrupted");
            }
            if (retValue != 0) {
                LOG.error("Error during VCF checking and preprocessing process over " + inputFile + " due to non-zero (" + retValue
                        + ") exit status");
                throw new RuntimeException("VCF checking and preprocessing process had non 0 exit status, exit status: " + retValue);
            }
            LOG.info("Finished VCF checking and preprocessing process over " + inputFile);
        } catch (IOException | InterruptedException e) {
            LOG.error("Exception during VCF checking and preprocessing process over " + inputFile + ". Exception: " + e);
            throw new RuntimeException(e);
        }
    }

    @Override
    public Path getPharmCatVcfFile(Path vcfFile) {
        return getRenamedVcfFile(vcfFile, "input.pharmcat.vcf");
    }

    @Override
    public Path getVepVcfFile(Path vcfFile) {
        return getRenamedVcfFile(vcfFile, "input.vep.vcf");
    }

    private boolean isValidInputVcfName(Path vcfFile) {
        return vcfFile.toString().equals(DefaultVariantsAnalysisController.INPUT_VCF_NAME);
    }

    private void checkInputVcfName(Path vcfFile) {
        if (!this.isValidInputVcfName(vcfFile)) {
            throw new IllegalArgumentException(
                "The VCF name should be " + DefaultVariantsAnalysisController.INPUT_VCF_NAME);
        }
    }

    private Path getRenamedVcfFile(Path vcfFile, String newName) {
        this.checkInputVcfName(vcfFile);

        return new File(
            vcfFile.toString().replace(DefaultVariantsAnalysisController.INPUT_VCF_NAME, newName)).toPath();
    }

    @Override
    public void deleteVcfs(Path userPath, Path vcfFile) {
        this.checkInputVcfName(vcfFile);

        if (contextParamKeepVcfs == null || !contextParamKeepVcfs.equals("yes")) {
            Path[] vcfFiles = { vcfFile, getPharmCatVcfFile(vcfFile), getVepVcfFile(vcfFile) };

            for (Path vcf : vcfFiles) {
                File file = configuration.getUserDataBaseDirectory().toPath().resolve(
                        userPath.resolve(vcf)).toFile();

                if (file.exists()) {
                    if (file.delete()) {
                        LOG.info("Deleted VCF file: " + file.getAbsolutePath());
                    } else {
                        LOG.info("VCF file: " + file.getAbsolutePath() + " could not be deleted");
                    }
                }
            }
        }
    }
}
