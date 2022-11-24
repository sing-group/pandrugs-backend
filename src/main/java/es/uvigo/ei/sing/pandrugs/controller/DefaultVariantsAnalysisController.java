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

import static java.util.Arrays.asList;
import static java.util.stream.Collectors.joining;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.inject.Inject;

import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.stereotype.Controller;

import es.uvigo.ei.sing.pandrugs.core.variantsanalysis.FileSystemConfiguration;
import es.uvigo.ei.sing.pandrugs.core.variantsanalysis.VariantsScoreComputation;
import es.uvigo.ei.sing.pandrugs.core.variantsanalysis.VariantsScoreComputer;
import es.uvigo.ei.sing.pandrugs.core.variantsanalysis.pharmcat.PharmCatAnnotation;
import es.uvigo.ei.sing.pandrugs.core.variantsanalysis.pharmcat.PharmCatJsonReportParser;
import es.uvigo.ei.sing.pandrugs.mail.Mailer;
import es.uvigo.ei.sing.pandrugs.persistence.dao.UserDAO;
import es.uvigo.ei.sing.pandrugs.persistence.dao.VariantsScoreUserComputationDAO;
import es.uvigo.ei.sing.pandrugs.persistence.entity.PharmCatComputationParameters;
import es.uvigo.ei.sing.pandrugs.persistence.entity.User;
import es.uvigo.ei.sing.pandrugs.persistence.entity.VariantsScoreComputationParameters;
import es.uvigo.ei.sing.pandrugs.persistence.entity.VariantsScoreComputationStatus;
import es.uvigo.ei.sing.pandrugs.persistence.entity.VariantsScoreUserComputation;
import es.uvigo.ei.sing.pandrugs.service.entity.ComputationMetadata;
import es.uvigo.ei.sing.pandrugs.service.entity.GeneRanking;
import es.uvigo.ei.sing.pandrugs.service.entity.UserInfo;
import es.uvigo.ei.sing.pandrugs.service.entity.UserLogin;
import es.uvigo.ei.sing.vcfparser.vcf.DefaultVCFMetaDataBuilder;
import es.uvigo.ei.sing.vcfparser.vcf.VCFMetaData;
import es.uvigo.ei.sing.vcfparser.vcf.VCFParseException;
import es.uvigo.ei.sing.vcfparser.vcf.VCFReader;
import es.uvigo.ei.sing.vcfparser.vcf.VCFVariant;
import es.uvigo.ei.sing.vcfparser.vcf.VCFVariantDataBuilder;

@Controller
public class DefaultVariantsAnalysisController implements
		VariantsAnalysisController, ApplicationListener<ContextRefreshedEvent> {

	public static final String INPUT_VCF_NAME = "input.vcf";
	public static final String INPUT_PHARMCAT_PHENOTYPER_OUTSIDE_CALL_NAME = "input_pharmcat_phenotyper_outside_call_file.tsv";

	private static final List<String> PHARMCAT_REPORT_EXTENSIONS = asList("html", "json");

	private Logger LOG = LoggerFactory.getLogger(DefaultVariantsAnalysisController.class);

	@Inject
	private VariantsScoreUserComputationDAO variantsScoreUserComputationDAO;

	@Inject
	private Mailer mailer;

	@Inject
	private UserDAO userDAO;

	@Inject
	private VariantsScoreComputer
			variantsScoreComputer;

	@Inject
	private FileSystemConfiguration fileSystemConfiguration;

	private String startVariantsScopeUserComputation(
		UserLogin userLogin,
		InputStream vcfFileInputStream,
		Boolean withPharmCat,
		Optional<InputStream> tsvFileInputStream,
		String computationName,
		String resultsURLTemplate
	) throws IOException {

		User user = userDAO.get(userLogin.getLogin());
		File dataDir = createComputationDataDirectory(user);
		final File vcfFile = new File(dataDir + File.separator + INPUT_VCF_NAME);
		FileUtils.copyInputStreamToFile(vcfFileInputStream, vcfFile);

		final VariantsScoreComputationParameters parameters = new VariantsScoreComputationParameters();
		parameters.setResultsBasePath(dataDir.toPath().getFileName());
		parameters.setVcfFile(Paths.get(INPUT_VCF_NAME));
		try {
			parameters.setNumberOfInputVariants(this.countVariantsInInput(vcfFile));
		} catch (VCFParseException e) {
			throw new IOException(e);
		}
		parameters.setResultsURLTemplate(resultsURLTemplate);

		final PharmCatComputationParameters pharmCatComputationParameters = new PharmCatComputationParameters();
		pharmCatComputationParameters.setPharmCat(withPharmCat);
		if (tsvFileInputStream.isPresent()) {
			final File tsvFile = new File(dataDir + File.separator + INPUT_PHARMCAT_PHENOTYPER_OUTSIDE_CALL_NAME);
			FileUtils.copyInputStreamToFile(tsvFileInputStream.get(), tsvFile);
			pharmCatComputationParameters
					.setPharmCatPhenotyperTsvFile(Paths.get(INPUT_PHARMCAT_PHENOTYPER_OUTSIDE_CALL_NAME));
		}

		VariantsScoreUserComputation computation = 
			this.startVariantsScoreComputation(user, computationName, parameters, pharmCatComputationParameters);

		return computation.getId();
	}

	@Override
	public String startVariantsScopeUserComputationWithPharmCat(
		UserLogin userLogin,
		InputStream vcfFileInputStream,
		InputStream tsvFileInputStream,
		String computationName,
		String resultsURLTemplate
	) throws IOException {
		return this.startVariantsScopeUserComputation(userLogin, vcfFileInputStream, true, Optional.of(tsvFileInputStream), computationName, resultsURLTemplate);
	}

	@Override
	public String startVariantsScopeUserComputationWithPharmCat(
		UserLogin userLogin,
		InputStream vcfFileInputStream,
		InputStream tsvFileInputStream,
		String computationName
	) throws IOException {
		return this.startVariantsScopeUserComputationWithPharmCat(userLogin, vcfFileInputStream, tsvFileInputStream, computationName, null);
	}

	@Override
	public String startVariantsScopeUserComputation(
		UserLogin userLogin,
		InputStream vcfFileInputStream,
		Boolean withPharmCat,
		String computationName,
		String resultsURLTemplate
	) throws IOException {
		return this.startVariantsScopeUserComputation(userLogin, vcfFileInputStream, withPharmCat, Optional.empty(), computationName, resultsURLTemplate);
	}

	@Override
	public String startVariantsScopeUserComputation(
		UserLogin userLogin,
		InputStream vcfFileInputStream,
		Boolean withPharmCat,
		String computationName
	) throws IOException {
		return this.startVariantsScopeUserComputation(userLogin, vcfFileInputStream, withPharmCat, computationName, null);
	}

	@Override
	public ComputationMetadata getComputationStatus(String computationId) {
		VariantsScoreUserComputation computation = variantsScoreUserComputationDAO.get(computationId);
		if (computation == null) {
			throw new IllegalArgumentException("computationId " + computationId + " not found.");
		}
		return new ComputationMetadata(computation,
				getAffectedGenes(computation),
				getAffectedGenesInfo(computation));
	}

	@Override
	public Map<String, ComputationMetadata> getComputationsForUser(UserLogin userLogin) {
		User user = userDAO.get(userLogin.getLogin());

		Map<String, ComputationMetadata> computations = new HashMap<>();

		for (VariantsScoreUserComputation computation : variantsScoreUserComputationDAO.retrieveComputationsBy(user)) {
				computations.put(computation.getId(), new ComputationMetadata(computation, getAffectedGenes
						(computation),
						getAffectedGenesInfo(computation)));
		}

		return computations;
	}

	private Set<String> getAffectedGenes(VariantsScoreUserComputation computation) {
		if (computation.getComputationDetails().getStatus().isFinished() &&
				!computation.getComputationDetails().getStatus().hasErrors()
				) {

			return this.getGeneRanking(computation).asMap().keySet();

		} else {
			return null;
		}
	}

	private Integer countVariantsInInput(File variantsFile) throws IOException,
			VCFParseException {

			final AtomicInteger variantsCount = new AtomicInteger(0);

			try {
				new VCFReader<>(
					variantsFile.toURI().toURL(),
					new DefaultVCFMetaDataBuilder(),
					new VCFVariantDataBuilder<VCFMetaData, VCFVariant<VCFMetaData>>() {

						// count variants
						@Override
						public VCFVariantDataBuilder<VCFMetaData, VCFVariant<VCFMetaData>> endVariant() {
							variantsCount.incrementAndGet();
							return this;
						}

						// dummy methods
						@Override
						public VCFVariantDataBuilder<VCFMetaData, VCFVariant<VCFMetaData>> setMetadata(VCFMetaData vcfMetaData) {
							return this;
						}

						@Override
						public VCFVariantDataBuilder<VCFMetaData, VCFVariant<VCFMetaData>> setVariantQuality(double v) {
							return this;
						}

						@Override
						public VCFVariantDataBuilder<VCFMetaData, VCFVariant<VCFMetaData>> setVariantHasNoFilters() {
							return this;
						}

						@Override
						public VCFVariantDataBuilder<VCFMetaData, VCFVariant<VCFMetaData>> setVariantPassesFilters() {
							return this;
						}

						@Override
						public VCFVariantDataBuilder<VCFMetaData, VCFVariant<VCFMetaData>> setVariantId(String s) {
							return this;
						}

						@Override
						public Collection<VCFVariant<VCFMetaData>> build() {
							return null;
						}

						@Override
						public VCFVariantDataBuilder<VCFMetaData, VCFVariant<VCFMetaData>> addVariantSample(String s, Map<String, List<String>> map) {
							return this;
						}

						@Override
						public VCFVariantDataBuilder<VCFMetaData, VCFVariant<VCFMetaData>> setVariantInfo(Map<String, List<String>> map) {
							return this;
						}

						@Override
						public VCFVariantDataBuilder<VCFMetaData, VCFVariant<VCFMetaData>> setVariantFilters(List<String> list) {
							return this;
						}

						@Override
						public VCFVariantDataBuilder<VCFMetaData, VCFVariant<VCFMetaData>> startVariant(String s, long l, String s1, Set<String> set) {
							return this;
						}
					}
				).getVariants();

				return variantsCount.intValue();
			} catch (MalformedURLException e) {
				throw new RuntimeException(e);
			}

	}

	@Override
	public void deleteComputation(String computationId) {
		VariantsScoreUserComputation variantsScoreUserComputation = variantsScoreUserComputationDAO.get(computationId);
		Objects.requireNonNull(variantsScoreUserComputation, "computation with id =" + computationId);

		if (!variantsScoreUserComputation.getComputationDetails().getStatus().isFinished()) {
			throw new IllegalStateException("Only finished computations can be deleted");
		}

		File computationDataDir = this.obtainComputationFile(variantsScoreUserComputation, Paths.get("."));
		try {
			LOG.info("Deleting dir: " + computationDataDir);
			FileUtils.deleteDirectory(computationDataDir);
		} catch (IOException e) {
			LOG.warn("Could not delete computation directory: " + computationDataDir);
		}

		variantsScoreUserComputationDAO.remove(variantsScoreUserComputation);
	}

	@Override
	public UserInfo getUserOfComputation(String computationId) {
		if (variantsScoreUserComputationDAO.get(computationId) == null) {
			throw new IllegalArgumentException("computationId " + computationId + " not found");
		}

		return new UserInfo(variantsScoreUserComputationDAO.get(computationId).getUser());
	}

	@Override
	public GeneRanking getGeneRankingForComputation(String computationId) {
		VariantsScoreUserComputation computation = this.variantsScoreUserComputationDAO.get(computationId);

		if (computation == null) {
			throw new IllegalArgumentException("Computation with id " + computationId + " not found");
		}

		if (!computation.getComputationDetails().getStatus().isFinished()) {
			throw new IllegalStateException("Computation has not finished yet");
		}

		return getGeneRanking(computation);
	}

	private GeneRanking getGeneRanking(VariantsScoreUserComputation computation) {
		try {
			File affectedGenesFile = getAffectedGenesFile(computation);

			try (Stream<String> lines = Files.lines(affectedGenesFile.toPath(), StandardCharsets.ISO_8859_1)) {
				Map<String, Double> geneRankingMap = lines
					.skip(1)
					.filter(line -> line.length() > 0)
					.map(line -> line.split("\t"))
					.collect(Collectors.toMap(
							tokens -> tokens[0],
							tokens -> Double.parseDouble(tokens[1]),
							(d1, __) -> d1,
							LinkedHashMap::new
					));

				return new GeneRanking(geneRankingMap);
			}
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	private Map<String, Map<String, String>> getAffectedGenesInfo(VariantsScoreUserComputation computation) {
		if (computation.getComputationDetails().getStatus().isFinished() &&
				!computation.getComputationDetails().getStatus().hasErrors()
				) {
			try {
				Map<String, Map<String, String>> affectedGenesInfo = new HashMap<>();
				File affectedGenesFile = getAffectedGenesFile(computation);
				List<String> lines = Files.readAllLines(affectedGenesFile.toPath(), StandardCharsets.ISO_8859_1);
				if (lines.size() > 0) {
					String[] headers = lines.get(0).split("\t");

					for (int i = 1; i < lines.size(); i++) {
						if (lines.get(i).length() == 0) continue;
						String[] tokens = lines.get(i).split("\t");

						Map<String, String> affectedGeneInfo = new HashMap<>();
						for (int j = 0; j < tokens.length; j++) {
							if (j == 0) {
								affectedGenesInfo.put(tokens[j], affectedGeneInfo);
							} else {
								affectedGeneInfo.put(headers[j], tokens[j]);
							}
						}
					}
				}

				return affectedGenesInfo;
			} catch (IOException e) {
				throw new RuntimeException(e);
			}
		} else {
			return null;
		}
	}

	private File getAffectedGenesFile(VariantsScoreUserComputation computation) {
		return this.obtainComputationFile(
				computation,
				computation.getComputationDetails().getResults().getAffectedGenesPath());
	}

	@Override
	public File getVariantsScoreFile(String computationId) {
		VariantsScoreUserComputation computation = this.variantsScoreUserComputationDAO.get(computationId);

		if (!computation.getComputationDetails().getStatus().isFinished()) {
			throw new IllegalStateException("Computation has not finished yet");
		}

		return this.obtainComputationFile(
				computation, computation.getComputationDetails().getResults().getVscorePath());
	}

	@Override
	public List<String> listPharmCatReportExtensions() {
		return PHARMCAT_REPORT_EXTENSIONS;
	}

	@Override
	public boolean isValidPharmCatReportExtension(String extension) {
		return PHARMCAT_REPORT_EXTENSIONS.stream().anyMatch(e -> e.equals(extension));
	}

	@Override
	public File getPharmCatReport(String computationId, String extension) {
		if (!isValidPharmCatReportExtension(extension)) {
			throw new IllegalStateException("Invalid extension. Supported extensions: "
					+ listPharmCatReportExtensions().stream().collect(joining(", ")));
		}

		VariantsScoreUserComputation computation = this.variantsScoreUserComputationDAO.get(computationId);

		if(!computation.getComputationDetails().getPharmCatComputationParameters().isPharmCat()) {
			throw new IllegalStateException("Computation has not PharmCAT results associated");
		}

		if (!computation.getComputationDetails().getStatus().isFinished()) {
			throw new IllegalStateException("Computation has not finished yet");
		}

		return this.obtainComputationFile(computation,
				Paths.get(computation.getComputationDetails().getResults().getPharmCatResults().getFilePath().toString()
						+ "." + extension));
	}

	@Override
	public Map<String, PharmCatAnnotation> getPharmCatAnnotations(String computationId) {
		VariantsScoreUserComputation computation = this.variantsScoreUserComputationDAO.get(computationId);

		File pharmCatReport = this.getPharmCatReport(computationId, "json");

		try {
			return PharmCatJsonReportParser.getPharmCatFilteredAnnotations(pharmCatReport);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	};

	private File createComputationDataDirectory(User user) {
		File computationDir = new File(fileSystemConfiguration.getUserDataBaseDirectory() +
				File.separator + user.getLogin() +
				"-" +
				UUID.randomUUID());
		computationDir.mkdir();
		return computationDir;
	}

	private VariantsScoreUserComputation startVariantsScoreComputation(User user, String computationName,
			VariantsScoreComputationParameters parameters,
			PharmCatComputationParameters pharmCatComputationParameters) {

		File userDir = fileSystemConfiguration.getUserDataBaseDirectory().toPath().resolve(parameters
				.getResultsBasePath()).toFile();

		if (!userDir.exists()) {
			userDir.mkdir();
		}

		final VariantsScoreComputation computation =
				variantsScoreComputer.createComputation(parameters, pharmCatComputationParameters);


		VariantsScoreUserComputation userComputation = new VariantsScoreUserComputation(UUID.randomUUID().toString());
		userComputation.setUser(user);
		userComputation.setName(computationName);
		userComputation.getComputationDetails().setStatus(computation.getStatus());
		userComputation.getComputationDetails().setParameters(parameters);
		userComputation.getComputationDetails().setPharmCatComputationParameters(pharmCatComputationParameters);
		variantsScoreUserComputationDAO.storeComputation(userComputation);

		addChangeListener(userComputation, computation);

		//speculate if the computation has already finished before the listener could be added...
		processStatus(userComputation, computation, computation.getStatus());

		return userComputation;
	}

	private void addChangeListener(VariantsScoreUserComputation userComputation, VariantsScoreComputation
			computation) {
		computation.getStatus().onChange((status) -> {
			processStatus(userComputation, computation, status);
		});
	}

	public void processStatus(VariantsScoreUserComputation userComputation, VariantsScoreComputation computation,
		VariantsScoreComputationStatus status
	) {

		userComputation.getComputationDetails().setStatus(status);
		if (computation.getStatus().isFinished()) {
			try {
				userComputation.getComputationDetails().setResults(computation.get());
			} catch (InterruptedException e) {
				throw new RuntimeException(e);
			} catch (ExecutionException e) {
				userComputation.getComputationDetails().setResults(null);
			}
		}

		variantsScoreUserComputationDAO.update(userComputation);

		// send confirmation mail
		if (computation.getStatus().isFinished()) {
			try {
				if (!userComputation.getUser().getLogin().equals("guest")) {
					userComputation.getUser();
					mailer.sendComputationFinished(userComputation);
				}
			} catch (Exception e) {
				LOG.error("Error sending mail " + e.getMessage());
				e.printStackTrace();
			}
		}
	}

	@Override
	public void onApplicationEvent(ContextRefreshedEvent event) {
		LOG.info("Context refresh event. Trying to resume computations...");
		for (VariantsScoreUserComputation userComputation : this.variantsScoreUserComputationDAO.list()) {

			// resume unfinished computations that have not been resumed by previous
			// refreshing events
			if (!userComputation.getComputationDetails().getStatus().isFinished()) {
				LOG.info("Resuming computation id=" + userComputation.getId());
				VariantsScoreComputation computation =
						variantsScoreComputer.resumeComputation(userComputation.getComputationDetails());

				addChangeListener(userComputation, computation);

				//speculate if the computation has already finished before the listener could be added...
				processStatus(userComputation, computation, computation.getStatus());
			}
		}
	}

	private File obtainComputationFile(VariantsScoreUserComputation computation, Path elementPath) {
		try {
			return new File(this.fileSystemConfiguration.getUserDataBaseDirectory() +
					File.separator +
					computation.getComputationDetails().getParameters().getResultsBasePath().resolve(elementPath)
							.toString()).getCanonicalFile();
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}
}
