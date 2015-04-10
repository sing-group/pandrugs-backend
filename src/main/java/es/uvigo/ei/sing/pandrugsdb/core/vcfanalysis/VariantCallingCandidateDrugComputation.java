package es.uvigo.ei.sing.pandrugsdb.core.vcfanalysis;

import java.util.List;
import java.util.concurrent.Future;

import es.uvigo.ei.sing.pandrugsdb.service.entity.CandidateDrug;

public interface VariantCallingCandidateDrugComputation extends Future<List<CandidateDrug>>{

}
