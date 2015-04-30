package es.uvigo.ei.sing.pandrugsdb.suite;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

import es.uvigo.ei.sing.pandrugsdb.core.variantsanalysis.DefaultVariantsCandidateTherapiesComputerUnitTest;
import es.uvigo.ei.sing.pandrugsdb.core.variantsanalysis.vcf.DefaultVCFMetaDataBuilderUnitTest;
import es.uvigo.ei.sing.pandrugsdb.core.variantsanalysis.vcf.DefaultVCFVariantDataBuilderUnitTest;
import es.uvigo.ei.sing.pandrugsdb.core.variantsanalysis.vcf.VCFMetaDataUnitTest;
import es.uvigo.ei.sing.pandrugsdb.core.variantsanalysis.vcf.VCFReaderUnitTest;

@SuiteClasses({
	DefaultVariantsCandidateTherapiesComputerUnitTest.class,
	VCFReaderUnitTest.class,
	DefaultVCFMetaDataBuilderUnitTest.class,
	DefaultVCFVariantDataBuilderUnitTest.class,
	VCFMetaDataUnitTest.class
})
@RunWith(Suite.class)
public class VariantsAnalysisUnitTestSuite {

}
