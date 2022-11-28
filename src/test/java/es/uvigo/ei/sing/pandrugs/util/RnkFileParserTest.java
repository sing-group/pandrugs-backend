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
package es.uvigo.ei.sing.pandrugs.util;

import static java.nio.file.Files.createTempFile;
import static org.apache.commons.io.FileUtils.copyInputStreamToFile;
import static org.junit.Assert.assertEquals;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;

public class RnkFileParserTest {

    private static final Map<String, Double> TEST_DATA;

    static {
        TEST_DATA = new HashMap<>();
        TEST_DATA.put("CALML5", 11.7463301629058);
        TEST_DATA.put("KRT81", 10.4754299026783);
        TEST_DATA.put("H19", 9.46356803504428);
        TEST_DATA.put("NPTX2", 9.31361309597756);
        TEST_DATA.put("KRT6B", 9.04072245114886);
        TEST_DATA.put("SOX11", 8.96697807121534);
        TEST_DATA.put("KRT75", 8.8418503479639);
        TEST_DATA.put("PAX7", 8.60721699036809);
        TEST_DATA.put("BARX2", 8.59920873291726);
        TEST_DATA.put("MMP10", 8.43216989174878);
        TEST_DATA.put("SLC35A3", 0.0016);
        TEST_DATA.put("ADAMTS17", 1.9);
    }

    private String rnkFilePath = "test.rnk";

    private File rnk;

    @Before
    public void testSetup() throws IOException {
        rnk = createTempFile(new File(System.getProperty("java.io.tmpdir")).toPath(), rnkFilePath, "").toFile();
        copyInputStreamToFile(getClass().getResourceAsStream(rnkFilePath), rnk);
    }

    @Test
    public void testParseRnk() throws FileNotFoundException, IOException {
        Map<String, Double> map = RnkFileParser.loadFile(this.rnk);

        assertEquals(TEST_DATA, map);
    }
}
