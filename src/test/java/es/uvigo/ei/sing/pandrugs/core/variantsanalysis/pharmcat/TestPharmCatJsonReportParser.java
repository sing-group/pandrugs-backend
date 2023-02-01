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

import static java.nio.file.Files.createTempFile;
import static org.apache.commons.io.FileUtils.copyInputStreamToFile;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.Map;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

public class TestPharmCatJsonReportParser {

    private static String reportJsonPath = "pharmcat.report.json";
    private static Path reportJson;

    private static String emptyReportJsonPath = "pharmcat.report.empty.json";
    private static Path emptyReportJson;

    @BeforeClass
    public static void testSetup() throws IOException {
        reportJson = createTempFile(new File(System.getProperty("java.io.tmpdir")).toPath(), reportJsonPath, "");
        copyInputStreamToFile(TestPharmCatJsonReportParser.class.getResourceAsStream(reportJsonPath), reportJson.toFile());
        emptyReportJson = createTempFile(new File(System.getProperty("java.io.tmpdir")).toPath(), emptyReportJsonPath, "");
        copyInputStreamToFile(TestPharmCatJsonReportParser.class.getResourceAsStream(emptyReportJsonPath), emptyReportJson.toFile());
    }

    @Test
    public void testParseReport() throws IOException {
        Map<String, PharmCatAnnotation> annotations = PharmCatJsonReportParser
                .getPharmCatFilteredAnnotations(TestPharmCatJsonReportParser.reportJson.toFile());

        assertEquals(44, annotations.size());

        PharmCatAnnotation warfarinAnnotation = annotations.get("warfarin");
        assertEquals(warfarinAnnotation.getGermLineAnnotation(), GermLineAnnotation.WARNING);
        assertFalse(warfarinAnnotation.getReportAnnotation().isPresent());

        PharmCatAnnotation clopidogrelAnnotation = annotations.get("clopidogrel");
        assertEquals(clopidogrelAnnotation.getGermLineAnnotation(), GermLineAnnotation.STRONGLY_RECOMMENDED);
        assertTrue(clopidogrelAnnotation.getReportAnnotation().isPresent());
        assertEquals(clopidogrelAnnotation.getReportAnnotation().get().getClassification(), "Strong");
        assertEquals(clopidogrelAnnotation.getReportAnnotation().get().getDrugRecommendation(),
                "If considering clopidogrel, use at standard dose (75 mg/day)");
        assertEquals(clopidogrelAnnotation.getReportAnnotation().get().getPopulation(),
                "NVI, CVI ACS PCI, CVI non-ACS non-PCI");

        PharmCatAnnotation ivacaftorAnnotation = annotations.get("ivacaftor");
        assertEquals(ivacaftorAnnotation.getGermLineAnnotation(), GermLineAnnotation.MODERATELY_NOT_RECOMMENDED);
        assertTrue(ivacaftorAnnotation.getReportAnnotation().isPresent());
        assertEquals(ivacaftorAnnotation.getReportAnnotation().get().getClassification(), "Moderate");
        assertEquals(ivacaftorAnnotation.getReportAnnotation().get().getDrugRecommendation(),
                "Ivacaftor is not recommended");
        assertEquals(ivacaftorAnnotation.getReportAnnotation().get().getPopulation(), "general");
    }
    
    @Test
    public void testParseEmptyReport() throws IOException {
        Map<String, PharmCatAnnotation> annotations = PharmCatJsonReportParser
                .getPharmCatFilteredAnnotations(TestPharmCatJsonReportParser.emptyReportJson.toFile());
        
        assertEquals(0, annotations.size());
    }

    @AfterClass
    public static void removeUserDir() throws IOException {
        reportJson.toFile().delete();
        emptyReportJson.toFile().delete();
    }
}
