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
package es.uvigo.ei.sing.pandrugs.controller.entity;

import static org.junit.Assert.assertEquals;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collection;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

@RunWith(Parameterized.class)
public class GeneExpressionCoherenceTest {
    
    @Parameters(name = "{index}: {1}")
    public static Collection<Object[]> data() {
        return Arrays.asList(
                new Object[][] {
                        /*
                         * VCF + CNV
                         */
                        {
                                new GeneExpressionCoherence("GenA", "ONC", SnvAnnotation.MUTATED.toString(), "AMP", ""),
                                "GenA (oncogene) is mutated and amplified"
                        },
                        {
                                new GeneExpressionCoherence("GenA", "ONC", SnvAnnotation.MUTATED.toString(), "DEL", ""),
                                "GenA (oncogene) is mutated and deleted"
                        },
                        {
                                new GeneExpressionCoherence("GenA", "ONC", SnvAnnotation.MUTATED.toString(), "DIPLOID",
                                        ""),
                                "GenA (oncogene) is mutated and diploid"
                        },
                        {
                                new GeneExpressionCoherence("GenA", "ONC", SnvAnnotation.NOT_MUTATED.toString(), "AMP",
                                        ""),
                                "GenA (oncogene) is not mutated and amplified"
                        },
                        {
                                new GeneExpressionCoherence("GenA", "ONC", SnvAnnotation.NOT_MUTATED.toString(), "DEL",
                                        ""),
                                "GenA (oncogene) is not mutated and deleted"
                        },
                        {
                                new GeneExpressionCoherence("GenA", "TSG", SnvAnnotation.MUTATED.toString(), "AMP", ""),
                                "GenA (tumor suppressor gene) is mutated and amplified"
                        },
                        {
                                new GeneExpressionCoherence("GenA", "TSG", SnvAnnotation.MUTATED.toString(), "DEL", ""),
                                "GenA (tumor suppressor gene) is mutated and deleted"
                        },
                        {
                                new GeneExpressionCoherence("GenA", "TSG", SnvAnnotation.MUTATED.toString(), "DIPLOID",
                                        ""),
                                "GenA (tumor suppressor gene) is mutated and diploid"
                        },
                        {
                                new GeneExpressionCoherence("GenA", "TSG", SnvAnnotation.NOT_MUTATED.toString(), "AMP",
                                        ""),
                                "GenA (tumor suppressor gene) is not mutated and amplified"
                        },
                        {
                                new GeneExpressionCoherence("GenA", "TSG", SnvAnnotation.NOT_MUTATED.toString(), "DEL",
                                        ""),
                                "GenA (tumor suppressor gene) is not mutated and deleted"
                        },
                        {
                                new GeneExpressionCoherence("GenA", "UNCLASSIFIED", SnvAnnotation.MUTATED.toString(),
                                        "AMP", ""),
                                "GenA is mutated and amplified"
                        },
                        {
                                new GeneExpressionCoherence("GenA", "UNCLASSIFIED", SnvAnnotation.MUTATED.toString(),
                                        "DEL", ""),
                                "GenA is mutated and deleted"
                        },
                        {
                                new GeneExpressionCoherence("GenA", "UNCLASSIFIED", SnvAnnotation.MUTATED.toString(),
                                        "DIPLOID", ""),
                                "GenA is mutated and diploid"
                        },
                        {
                                new GeneExpressionCoherence("GenA", "UNCLASSIFIED",
                                        SnvAnnotation.NOT_MUTATED.toString(), "AMP", ""),
                                "GenA is not mutated and amplified"
                        },
                        {
                                new GeneExpressionCoherence("GenA", "UNCLASSIFIED",
                                        SnvAnnotation.NOT_MUTATED.toString(), "DEL", ""),
                                "GenA is not mutated and deleted"
                        },
                        /*
                         * VCF + Expression
                         */
                        {
                                new GeneExpressionCoherence("GenA", "ONC", SnvAnnotation.MUTATED.toString(), "",
                                        "HIGHLY_OVEREXPRESSED"),
                                "GenA (oncogene) is mutated and highly overexpressed"
                        },
                        {
                                new GeneExpressionCoherence("GenA", "ONC", SnvAnnotation.MUTATED.toString(), "",
                                        "OVEREXPRESSED"),
                                "GenA (oncogene) is mutated and overexpressed"
                        },
                        {
                                new GeneExpressionCoherence("GenA", "ONC", SnvAnnotation.MUTATED.toString(), "",
                                        "UNDEREXPRESSED"),
                                "GenA (oncogene) is mutated and underexpressed"
                        },
                        {
                                new GeneExpressionCoherence("GenA", "ONC", SnvAnnotation.MUTATED.toString(), "",
                                        "NOT_EXPRESSED"),
                                "GenA (oncogene) is mutated and not expressed"
                        },
                        {
                                new GeneExpressionCoherence("GenA", "ONC", SnvAnnotation.NOT_MUTATED.toString(), "",
                                        "HIGHLY_OVEREXPRESSED"),
                                "GenA (oncogene) is not mutated and highly overexpressed"
                        },
                        {
                                new GeneExpressionCoherence("GenA", "TSG", SnvAnnotation.MUTATED.toString(), "",
                                        "OVEREXPRESSED"),
                                "GenA (tumor suppressor gene) is mutated and overexpressed"
                        },
                        {
                                new GeneExpressionCoherence("GenA", "TSG", SnvAnnotation.MUTATED.toString(), "",
                                        "UNDEREXPRESSED"),
                                "GenA (tumor suppressor gene) is mutated and underexpressed"
                        },
                        {
                                new GeneExpressionCoherence("GenA", "TSG", SnvAnnotation.MUTATED.toString(), "",
                                        "NOT_EXPRESSED"),
                                "GenA (tumor suppressor gene) is mutated and not expressed"
                        },
                        {
                                new GeneExpressionCoherence("GenA", "UNCLASSIFIED", SnvAnnotation.MUTATED.toString(),
                                        "", "OVEREXPRESSED"),
                                "GenA is mutated and overexpressed"
                        },
                        {
                                new GeneExpressionCoherence("GenA", "UNCLASSIFIED", SnvAnnotation.MUTATED.toString(),
                                        "", "UNDEREXPRESSED"),
                                "GenA is mutated and underexpressed"
                        },
                        {
                                new GeneExpressionCoherence("GenA", "UNCLASSIFIED", SnvAnnotation.MUTATED.toString(),
                                        "", "NOT_EXPRESSED"),
                                "GenA is mutated and not expressed"
                        },
                        /*
                         * CNV + Expression
                         */
                        {
                                new GeneExpressionCoherence("GenA", "ONC", "", "AMP", "HIGHLY_OVEREXPRESSED"),
                                "GenA (oncogene) is amplified and highly overexpressed"
                        },
                        {
                                new GeneExpressionCoherence("GenA", "ONC", "", "AMP", "OVEREXPRESSED"),
                                "GenA (oncogene) is amplified and overexpressed"
                        },
                        {
                                new GeneExpressionCoherence("GenA", "ONC", "", "AMP", "UNDEREXPRESSED"),
                                "GenA (oncogene) is amplified and underexpressed"
                        },
                        {
                                new GeneExpressionCoherence("GenA", "ONC", "", "AMP", "NOT_EXPRESSED"),
                                "GenA (oncogene) is amplified and not expressed"
                        },
                        {
                                new GeneExpressionCoherence("GenA", "ONC", "", "DEL", "HIGHLY_OVEREXPRESSED"),
                                "GenA (oncogene) is deleted and highly overexpressed"
                        },
                        {
                                new GeneExpressionCoherence("GenA", "ONC", "", "DEL", "OVEREXPRESSED"),
                                "GenA (oncogene) is deleted and overexpressed"
                        },
                        {
                                new GeneExpressionCoherence("GenA", "ONC", "", "DEL", "UNDEREXPRESSED"),
                                "GenA (oncogene) is deleted and underexpressed"
                        },
                        {
                                new GeneExpressionCoherence("GenA", "ONC", "", "DEL", "NOT_EXPRESSED"),
                                "GenA (oncogene) is deleted and not expressed"
                        },
                        {
                                new GeneExpressionCoherence("GenA", "ONC", "", "DIPLOID", "HIGHLY_OVEREXPRESSED"),
                                "GenA (oncogene) is diploid and highly overexpressed"
                        },
                        {
                                new GeneExpressionCoherence("GenA", "TSG", "", "AMP", "OVEREXPRESSED"),
                                "GenA (tumor suppressor gene) is amplified and overexpressed"
                        },
                        {
                                new GeneExpressionCoherence("GenA", "TSG", "", "AMP", "UNDEREXPRESSED"),
                                "GenA (tumor suppressor gene) is amplified and underexpressed"
                        },
                        {
                                new GeneExpressionCoherence("GenA", "TSG", "", "AMP", "NOT_EXPRESSED"),
                                "GenA (tumor suppressor gene) is amplified and not expressed"
                        },
                        {
                                new GeneExpressionCoherence("GenA", "TSG", "", "DEL", "OVEREXPRESSED"),
                                "GenA (tumor suppressor gene) is deleted and overexpressed"
                        },
                        {
                                new GeneExpressionCoherence("GenA", "TSG", "", "DEL", "UNDEREXPRESSED"),
                                "GenA (tumor suppressor gene) is deleted and underexpressed"
                        },
                        {
                                new GeneExpressionCoherence("GenA", "TSG", "", "DEL", "NOT_EXPRESSED"),
                                "GenA (tumor suppressor gene) is deleted and not expressed"
                        },
                        {
                                new GeneExpressionCoherence("GenA", "UNCLASSIFIED", "", "AMP", "OVEREXPRESSED"),
                                "GenA is amplified and overexpressed"
                        },
                        {
                                new GeneExpressionCoherence("GenA", "UNCLASSIFIED", "", "AMP", "UNDEREXPRESSED"),
                                "GenA is amplified and underexpressed"
                        },
                        {
                                new GeneExpressionCoherence("GenA", "UNCLASSIFIED", "", "AMP", "NOT_EXPRESSED"),
                                "GenA is amplified and not expressed"
                        },
                        {
                                new GeneExpressionCoherence("GenA", "UNCLASSIFIED", "", "DEL", "OVEREXPRESSED"),
                                "GenA is deleted and overexpressed"
                        },
                        {
                                new GeneExpressionCoherence("GenA", "UNCLASSIFIED", "", "DEL", "UNDEREXPRESSED"),
                                "GenA is deleted and underexpressed"
                        },
                        {
                                new GeneExpressionCoherence("GenA", "UNCLASSIFIED", "", "DEL", "NOT_EXPRESSED"),
                                "GenA is deleted and not expressed"
                        },
                        /*
                         * VCF + CNV + Expression
                         */
                        {
                                new GeneExpressionCoherence("GenA", "ONC", SnvAnnotation.MUTATED.toString(), "AMP",
                                        "HIGHLY_OVEREXPRESSED"),
                                "GenA (oncogene) is mutated, amplified and highly overexpressed"
                        },
                        {
                                new GeneExpressionCoherence("GenA", "ONC", SnvAnnotation.MUTATED.toString(), "AMP",
                                        "OVEREXPRESSED"),
                                "GenA (oncogene) is mutated, amplified and overexpressed"
                        },
                        {
                                new GeneExpressionCoherence("GenA", "ONC", SnvAnnotation.MUTATED.toString(), "AMP",
                                        "UNDEREXPRESSED"),
                                "GenA (oncogene) is mutated, amplified and underexpressed"
                        },
                        {
                                new GeneExpressionCoherence("GenA", "ONC", SnvAnnotation.MUTATED.toString(), "AMP",
                                        "NOT_EXPRESSED"),
                                "GenA (oncogene) is mutated, amplified and not expressed"
                        },
                        {
                                new GeneExpressionCoherence("GenA", "ONC", SnvAnnotation.MUTATED.toString(), "DEL",
                                        "HIGHLY_OVEREXPRESSED"),
                                "GenA (oncogene) is mutated, deleted and highly overexpressed"
                        },
                        {
                                new GeneExpressionCoherence("GenA", "ONC", SnvAnnotation.MUTATED.toString(), "DEL",
                                        "OVEREXPRESSED"),
                                "GenA (oncogene) is mutated, deleted and overexpressed"
                        },
                        {
                                new GeneExpressionCoherence("GenA", "ONC", SnvAnnotation.MUTATED.toString(), "DEL",
                                        "UNDEREXPRESSED"),
                                "GenA (oncogene) is mutated, deleted and underexpressed"
                        },
                        {
                                new GeneExpressionCoherence("GenA", "ONC", SnvAnnotation.MUTATED.toString(), "DEL",
                                        "NOT_EXPRESSED"),
                                "GenA (oncogene) is mutated, deleted and not expressed"
                        },
                        {
                                new GeneExpressionCoherence("GenA", "ONC", SnvAnnotation.MUTATED.toString(), "DIPLOID",
                                        "HIGHLY_OVEREXPRESSED"),
                                "GenA (oncogene) is mutated, diploid and highly overexpressed"
                        },
                        {
                                new GeneExpressionCoherence("GenA", "ONC", SnvAnnotation.MUTATED.toString(), "DIPLOID",
                                        "OVEREXPRESSED"),
                                "GenA (oncogene) is mutated, diploid and overexpressed"
                        },
                        {
                                new GeneExpressionCoherence("GenA", "ONC", SnvAnnotation.MUTATED.toString(), "DIPLOID",
                                        "UNDEREXPRESSED"),
                                "GenA (oncogene) is mutated, diploid and underexpressed"
                        },
                        {
                                new GeneExpressionCoherence("GenA", "ONC", SnvAnnotation.MUTATED.toString(), "DIPLOID",
                                        "NOT_EXPRESSED"),
                                "GenA (oncogene) is mutated, diploid and not expressed"
                        },
                        {
                                new GeneExpressionCoherence("GenA", "ONC", SnvAnnotation.NOT_MUTATED.toString(), "AMP",
                                        "HIGHLY_OVEREXPRESSED"),
                                "GenA (oncogene) is not mutated, amplified and highly overexpressed"
                        },
                        {
                                new GeneExpressionCoherence("GenA", "ONC", SnvAnnotation.NOT_MUTATED.toString(), "AMP",
                                        "OVEREXPRESSED"),
                                "GenA (oncogene) is not mutated, amplified and overexpressed"
                        },
                        {
                                new GeneExpressionCoherence("GenA", "ONC", SnvAnnotation.NOT_MUTATED.toString(), "AMP",
                                        "UNDEREXPRESSED"),
                                "GenA (oncogene) is not mutated, amplified and underexpressed"
                        },
                        {
                                new GeneExpressionCoherence("GenA", "ONC", SnvAnnotation.NOT_MUTATED.toString(), "AMP",
                                        "NOT_EXPRESSED"),
                                "GenA (oncogene) is not mutated, amplified and not expressed"
                        },
                        {
                                new GeneExpressionCoherence("GenA", "ONC", SnvAnnotation.NOT_MUTATED.toString(), "DEL",
                                        "HIGHLY_OVEREXPRESSED"),
                                "GenA (oncogene) is not mutated, deleted and highly overexpressed"
                        },
                        {
                                new GeneExpressionCoherence("GenA", "ONC", SnvAnnotation.NOT_MUTATED.toString(), "DEL",
                                        "OVEREXPRESSED"),
                                "GenA (oncogene) is not mutated, deleted and overexpressed"
                        },
                        {
                                new GeneExpressionCoherence("GenA", "ONC", SnvAnnotation.NOT_MUTATED.toString(), "DEL",
                                        "UNDEREXPRESSED"),
                                "GenA (oncogene) is not mutated, deleted and underexpressed"
                        },
                        {
                                new GeneExpressionCoherence("GenA", "ONC", SnvAnnotation.NOT_MUTATED.toString(), "DEL",
                                        "NOT_EXPRESSED"),
                                "GenA (oncogene) is not mutated, deleted and not expressed"
                        },
                        {
                                new GeneExpressionCoherence("GenA", "ONC", SnvAnnotation.NOT_MUTATED.toString(),
                                        "DIPLOID", "HIGHLY_OVEREXPRESSED"),
                                "GenA (oncogene) is not mutated, diploid and highly overexpressed"
                        },
                        {
                                new GeneExpressionCoherence("GenA", "TSG", SnvAnnotation.MUTATED.toString(), "AMP",
                                        "OVEREXPRESSED"),
                                "GenA (tumor suppressor gene) is mutated, amplified and overexpressed"
                        },
                        {
                                new GeneExpressionCoherence("GenA", "TSG", SnvAnnotation.MUTATED.toString(), "AMP",
                                        "UNDEREXPRESSED"),
                                "GenA (tumor suppressor gene) is mutated, amplified and underexpressed"
                        },
                        {
                                new GeneExpressionCoherence("GenA", "TSG", SnvAnnotation.MUTATED.toString(), "AMP",
                                        "NOT_EXPRESSED"),
                                "GenA (tumor suppressor gene) is mutated, amplified and not expressed"
                        },
                        {
                                new GeneExpressionCoherence("GenA", "TSG", SnvAnnotation.MUTATED.toString(), "DEL",
                                        "OVEREXPRESSED"),
                                "GenA (tumor suppressor gene) is mutated, deleted and overexpressed"
                        },
                        {
                                new GeneExpressionCoherence("GenA", "TSG", SnvAnnotation.MUTATED.toString(), "DEL",
                                        "UNDEREXPRESSED"),
                                "GenA (tumor suppressor gene) is mutated, deleted and underexpressed"
                        },
                        {
                                new GeneExpressionCoherence("GenA", "TSG", SnvAnnotation.MUTATED.toString(), "DEL",
                                        "NOT_EXPRESSED"),
                                "GenA (tumor suppressor gene) is mutated, deleted and not expressed"
                        },
                        {
                                new GeneExpressionCoherence("GenA", "TSG", SnvAnnotation.MUTATED.toString(), "DIPLOID",
                                        "OVEREXPRESSED"),
                                "GenA (tumor suppressor gene) is mutated, diploid and overexpressed"
                        },
                        {
                                new GeneExpressionCoherence("GenA", "TSG", SnvAnnotation.MUTATED.toString(), "DIPLOID",
                                        "UNDEREXPRESSED"),
                                "GenA (tumor suppressor gene) is mutated, diploid and underexpressed"
                        },
                        {
                                new GeneExpressionCoherence("GenA", "TSG", SnvAnnotation.MUTATED.toString(), "DIPLOID",
                                        "NOT_EXPRESSED"),
                                "GenA (tumor suppressor gene) is mutated, diploid and not expressed"
                        },
                        {
                                new GeneExpressionCoherence("GenA", "TSG", SnvAnnotation.NOT_MUTATED.toString(), "AMP",
                                        "OVEREXPRESSED"),
                                "GenA (tumor suppressor gene) is not mutated, amplified and overexpressed"
                        },
                        {
                                new GeneExpressionCoherence("GenA", "TSG", SnvAnnotation.NOT_MUTATED.toString(), "AMP",
                                        "UNDEREXPRESSED"),
                                "GenA (tumor suppressor gene) is not mutated, amplified and underexpressed"
                        },
                        {
                                new GeneExpressionCoherence("GenA", "TSG", SnvAnnotation.NOT_MUTATED.toString(), "AMP",
                                        "NOT_EXPRESSED"),
                                "GenA (tumor suppressor gene) is not mutated, amplified and not expressed"
                        },
                        {
                                new GeneExpressionCoherence("GenA", "TSG", SnvAnnotation.NOT_MUTATED.toString(), "DEL",
                                        "OVEREXPRESSED"),
                                "GenA (tumor suppressor gene) is not mutated, deleted and overexpressed"
                        },
                        {
                                new GeneExpressionCoherence("GenA", "TSG", SnvAnnotation.NOT_MUTATED.toString(), "DEL",
                                        "UNDEREXPRESSED"),
                                "GenA (tumor suppressor gene) is not mutated, deleted and underexpressed"
                        },
                        {
                                new GeneExpressionCoherence("GenA", "TSG", SnvAnnotation.NOT_MUTATED.toString(), "DEL",
                                        "NOT_EXPRESSED"),
                                "GenA (tumor suppressor gene) is not mutated, deleted and not expressed"
                        },
                        {
                                new GeneExpressionCoherence("GenA", "UNCLASSIFIED", SnvAnnotation.MUTATED.toString(),
                                        "AMP", "OVEREXPRESSED"),
                                "GenA is mutated, amplified and overexpressed"
                        },
                        {
                                new GeneExpressionCoherence("GenA", "UNCLASSIFIED", SnvAnnotation.MUTATED.toString(),
                                        "AMP", "UNDEREXPRESSED"),
                                "GenA is mutated, amplified and underexpressed"
                        },
                        {
                                new GeneExpressionCoherence("GenA", "UNCLASSIFIED", SnvAnnotation.MUTATED.toString(),
                                        "AMP", "NOT_EXPRESSED"),
                                "GenA is mutated, amplified and not expressed"
                        },
                        {
                                new GeneExpressionCoherence("GenA", "UNCLASSIFIED", SnvAnnotation.MUTATED.toString(),
                                        "DEL", "OVEREXPRESSED"),
                                "GenA is mutated, deleted and overexpressed"
                        },
                        {
                                new GeneExpressionCoherence("GenA", "UNCLASSIFIED", SnvAnnotation.MUTATED.toString(),
                                        "DEL", "UNDEREXPRESSED"),
                                "GenA is mutated, deleted and underexpressed"
                        },
                        {
                                new GeneExpressionCoherence("GenA", "UNCLASSIFIED", SnvAnnotation.MUTATED.toString(),
                                        "DEL", "NOT_EXPRESSED"),
                                "GenA is mutated, deleted and not expressed"
                        },
                        {
                                new GeneExpressionCoherence("GenA", "UNCLASSIFIED", SnvAnnotation.MUTATED.toString(),
                                        "DIPLOID", "OVEREXPRESSED"),
                                "GenA is mutated, diploid and overexpressed"
                        },
                        {
                                new GeneExpressionCoherence("GenA", "UNCLASSIFIED", SnvAnnotation.MUTATED.toString(),
                                        "DIPLOID", "UNDEREXPRESSED"),
                                "GenA is mutated, diploid and underexpressed"
                        },
                        {
                                new GeneExpressionCoherence("GenA", "UNCLASSIFIED", SnvAnnotation.MUTATED.toString(),
                                        "DIPLOID", "NOT_EXPRESSED"),
                                "GenA is mutated, diploid and not expressed"
                        },
                        {
                                new GeneExpressionCoherence("GenA", "UNCLASSIFIED",
                                        SnvAnnotation.NOT_MUTATED.toString(), "AMP", "OVEREXPRESSED"),
                                "GenA is not mutated, amplified and overexpressed"
                        },
                        {
                                new GeneExpressionCoherence("GenA", "UNCLASSIFIED",
                                        SnvAnnotation.NOT_MUTATED.toString(), "AMP", "UNDEREXPRESSED"),
                                "GenA is not mutated, amplified and underexpressed"
                        },
                        {
                                new GeneExpressionCoherence("GenA", "UNCLASSIFIED",
                                        SnvAnnotation.NOT_MUTATED.toString(), "AMP", "NOT_EXPRESSED"),
                                "GenA is not mutated, amplified and not expressed"
                        },
                        {
                                new GeneExpressionCoherence("GenA", "UNCLASSIFIED",
                                        SnvAnnotation.NOT_MUTATED.toString(), "DEL", "OVEREXPRESSED"),
                                "GenA is not mutated, deleted and overexpressed"
                        },
                        {
                                new GeneExpressionCoherence("GenA", "UNCLASSIFIED",
                                        SnvAnnotation.NOT_MUTATED.toString(), "DEL", "UNDEREXPRESSED"),
                                "GenA is not mutated, deleted and underexpressed"
                        },
                        {
                                new GeneExpressionCoherence("GenA", "UNCLASSIFIED",
                                        SnvAnnotation.NOT_MUTATED.toString(), "DEL", "NOT_EXPRESSED"),
                                "GenA is not mutated, deleted and not expressed"
                        }
                });
    }

    private GeneExpressionCoherence coherence;
    private String expectedString;

    public GeneExpressionCoherenceTest(GeneExpressionCoherence coherence, String expectedString) {
        this.coherence = coherence;
        this.expectedString = expectedString;
    }

	@Test
	public void test() throws IOException {
		assertEquals(this.expectedString, this.coherence.toString());
	}
}
