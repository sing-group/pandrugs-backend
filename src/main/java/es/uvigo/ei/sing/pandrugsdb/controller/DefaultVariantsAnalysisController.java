/*
 * #%L
 * PanDrugsDB Backend
 * %%
 * Copyright (C) 2015 Fátima Al-Shahrour, Elena Piñeiro, Daniel Glez-Peña and Miguel Reboiro-Jato
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
package es.uvigo.ei.sing.pandrugsdb.controller;

import static java.util.Objects.requireNonNull;
import static java.util.stream.Collectors.toMap;
import static org.apache.commons.io.FileUtils.deleteDirectory;
import static org.apache.commons.io.FileUtils.readLines;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ExecutionException;

import javax.inject.Inject;

import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.stereotype.Controller;

import es.uvigo.ei.sing.pandrugsdb.core.variantsanalysis.FileSystemConfiguration;
import es.uvigo.ei.sing.pandrugsdb.core.variantsanalysis.VariantsScoreComputation;
import es.uvigo.ei.sing.pandrugsdb.core.variantsanalysis.VariantsScoreComputer;
import es.uvigo.ei.sing.pandrugsdb.persistence.dao.UserDAO;
import es.uvigo.ei.sing.pandrugsdb.persistence.dao.VariantsScoreUserComputationDAO;
import es.uvigo.ei.sing.pandrugsdb.persistence.entity.User;
import es.uvigo.ei.sing.pandrugsdb.persistence.entity.VariantsScoreComputationParameters;
import es.uvigo.ei.sing.pandrugsdb.persistence.entity.VariantsScoreComputationStatus;
import es.uvigo.ei.sing.pandrugsdb.persistence.entity.VariantsScoreUserComputation;
import es.uvigo.ei.sing.pandrugsdb.service.entity.ComputationMetadata;
import es.uvigo.ei.sing.pandrugsdb.service.entity.GeneRanking;
import es.uvigo.ei.sing.pandrugsdb.service.entity.UserLogin;
import es.uvigo.ei.sing.pandrugsdb.service.entity.UserMetadata;

@Controller
public class DefaultVariantsAnalysisController implements
		VariantsAnalysisController, ApplicationListener<ContextRefreshedEvent> {

	public static final String INPUT_VCF_NAME = "input.vcf";

	private Logger LOG = LoggerFactory.getLogger(DefaultVariantsAnalysisController.class);
    

	@Inject
	private VariantsScoreUserComputationDAO variantsScoreUserComputationDAO;

	@Inject
	private UserDAO userDAO;

	@Inject
	private VariantsScoreComputer 
		variantsScoreComputer;
	
	@Inject
	private FileSystemConfiguration fileSystemConfiguration;
	


	@Override
	public int startVariantsScopeUserComputation(
			UserLogin userLogin,
			InputStream vcfFileInputStream,
			String computationName)
			throws IOException {

		User user = userDAO.get(userLogin.getLogin());
		File dataDir = createComputationDataDirectory(user);
		final File vcfFile = new File(dataDir + File.separator + INPUT_VCF_NAME);
		FileUtils.copyInputStreamToFile(vcfFileInputStream, vcfFile);

		final VariantsScoreComputationParameters parameters = new VariantsScoreComputationParameters();
		parameters.setResultsBasePath(dataDir.toPath().getFileName());
		parameters.setVcfFile(Paths.get(INPUT_VCF_NAME));

		VariantsScoreUserComputation computation = this.startVariantsScoreComputation(user, computationName,
				parameters);
		return computation.getId();
	}

	@Override
	public ComputationMetadata getComputationStatus(Integer computationId) {
		VariantsScoreUserComputation computation = variantsScoreUserComputationDAO.get(computationId);
		if (computation == null) {
			throw new IllegalArgumentException("computationId "+computationId+" not found.");
		}
		return new ComputationMetadata(computation, getAffectedGenes(computation));
	}

	@Override
	public Map<Integer, ComputationMetadata> getComputationsForUser(UserLogin userLogin) {
		User user = userDAO.get(userLogin.getLogin());

		Map<Integer, ComputationMetadata> computations = new HashMap<>();

		for (VariantsScoreUserComputation computation : variantsScoreUserComputationDAO.retrieveComputationsBy(user)) {
			computations.put(computation.getId(), new ComputationMetadata(computation, getAffectedGenes(computation)));
		}

		return computations;
	}

	private Integer getAffectedGenes(VariantsScoreUserComputation computation) {
		if (computation.getComputationDetails().getStatus().isFinished() &&
			!computation.getComputationDetails().getStatus().hasErrors()
			) {

			return this.getGeneRanking(computation).asMap().keySet().size();

		} else {
			return null;
		}
	}

	@Override
	public void deleteComputation(Integer computationId) {
		VariantsScoreUserComputation variantsScoreUserComputation = variantsScoreUserComputationDAO.get(computationId);
		requireNonNull(variantsScoreUserComputation, "computation with id ="+computationId);

		if (!variantsScoreUserComputation.getComputationDetails().getStatus().isFinished()) {
			throw new IllegalStateException("Only finished computations can be deleted");
		}

		File computationDataDir = this.obtainComputationFile(variantsScoreUserComputation, Paths.get("."));
		try {
			LOG.info("Deleting dir: "+computationDataDir);
			deleteDirectory(computationDataDir);
		} catch (IOException e) {
				LOG.warn("Could not delete computation directory: " + computationDataDir);
		}

		variantsScoreUserComputationDAO.remove(variantsScoreUserComputation);
	}

	@Override
	public UserMetadata getUserOfComputation(Integer computationId) {
		if (variantsScoreUserComputationDAO.get(computationId) == null) {
			throw new IllegalArgumentException("computationId "+computationId+" not found");
		}

		return new UserMetadata(variantsScoreUserComputationDAO.get(computationId).getUser());
	}

	@Override
	public GeneRanking getGeneRankingForComputation(int computationId) {
		VariantsScoreUserComputation computation = this.variantsScoreUserComputationDAO.get(computationId);

		if (computation == null) {
			throw new IllegalArgumentException("Computation with id "+computationId+" not found");
		}

		if (!computation.getComputationDetails().getStatus().isFinished()) {
			throw new IllegalStateException("Computation has not finished yet");
		}

		return getGeneRanking(computation);
	}

	private GeneRanking getGeneRanking(VariantsScoreUserComputation computation) {

		try {
			File affectedGenesFile = this.obtainComputationFile(
					computation,
					computation.getComputationDetails().getResults().getAffectedGenesPath());

			Map<String, Double> geneRankingMap =
					readLines(affectedGenesFile).stream()
					.skip(1)
					.filter(line -> line.length() > 0)
					.map(line -> line.split("\t"))
					.collect(toMap(
						tokens -> tokens[0],
						tokens -> Double.parseDouble(tokens[1]),
						(d1, __) -> d1,
						LinkedHashMap::new
					));

			return new GeneRanking(geneRankingMap);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	private File createComputationDataDirectory(User user) {
		File computationDir = new File(fileSystemConfiguration.getUserDataBaseDirectory() +
				File.separator + user.getLogin() +
				"-"+
				UUID.randomUUID());
		computationDir.mkdir();
		return computationDir;
	}

	private VariantsScoreUserComputation
	startVariantsScoreComputation(User user, String computationName, VariantsScoreComputationParameters parameters) {

		File userDir = fileSystemConfiguration.getUserDataBaseDirectory().toPath().resolve(parameters.getResultsBasePath()).toFile();

		if (!userDir.exists()) {
			userDir.mkdir();
		}

		final VariantsScoreComputation computation =
				variantsScoreComputer.createComputation(parameters);


		VariantsScoreUserComputation userComputation = new VariantsScoreUserComputation();
		userComputation.setUser(user);
		userComputation.setName(computationName);
		userComputation.getComputationDetails().setStatus(computation.getStatus());
		userComputation.getComputationDetails().setParameters(parameters);
		variantsScoreUserComputationDAO.storeComputation(userComputation);

		addChangeListener(userComputation, computation);

		//speculate if the computation has already finished before the listener could be added...
		processStatus(userComputation, computation, computation.getStatus());

		return userComputation;
	}

	private void addChangeListener(VariantsScoreUserComputation userComputation, VariantsScoreComputation computation) {
		computation.getStatus().onChange((status)-> {
			processStatus(userComputation, computation, status);
		});
	}


	public void processStatus(VariantsScoreUserComputation userComputation, VariantsScoreComputation computation,
			VariantsScoreComputationStatus status) {
		
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
	}
	
	private Set<Integer> resumedComputations = new HashSet<>();

	@Override
	public void onApplicationEvent(ContextRefreshedEvent event) {
			LOG.info("Context refresh event. Trying to resume computations...");
			for (VariantsScoreUserComputation userComputation: this.variantsScoreUserComputationDAO.list()) {
		
			// resume unfinished computations that have not been resumed by previous
			// refreshing events
			if (!userComputation.getComputationDetails().getStatus().isFinished() &&
					!resumedComputations.contains(userComputation.getId())) {
				LOG.info("Resuming computation id="+userComputation.getId());
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
			return new File(this.fileSystemConfiguration.getUserDataBaseDirectory()+
					File.separator+
					computation.getComputationDetails().getParameters().getResultsBasePath().resolve(elementPath)
							.toString()).getCanonicalFile();
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}
}
