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

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import java.nio.file.Paths;

import org.junit.Before;
import org.junit.Test;

public class DefaultVcfPreprocessorRunnerTest {
    
    private DefaultVcfPreprocessorConfiguration configuration = new DefaultVcfPreprocessorConfiguration();

    @Before
    public void setUp() {
        configuration.setCommandTemplate("/path/to/pharmcat-pandrugs/check-and-preprocess-vcf.sh %1$s %2$s");
    }

    @Test
    public void testcCreateCheckAndPreprocessCommandWithPharmCat() {
        String command = configuration.createCheckAndPreprocessCommand(
                Paths.get("/a/b/input.vcf"), true);

        assertThat(command, is("/path/to/pharmcat-pandrugs/check-and-preprocess-vcf.sh /a/b/input.vcf yes"));
    }

    @Test
    public void testcCreateCheckAndPreprocessCommandWithoutPharmCat() {
        String command = configuration.createCheckAndPreprocessCommand(
                Paths.get("/a/b/input.vcf"), false);

        assertThat(command, is("/path/to/pharmcat-pandrugs/check-and-preprocess-vcf.sh /a/b/input.vcf no"));
    }
}
