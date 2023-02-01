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
package es.uvigo.ei.sing.pandrugs.controller.entity;

import static es.uvigo.ei.sing.pandrugs.controller.entity.GeneExpressionAnnotation.HIGHLY_OVEREXPRESSED;
import static es.uvigo.ei.sing.pandrugs.controller.entity.GeneExpressionAnnotation.OVEREXPRESSED;
import static es.uvigo.ei.sing.pandrugs.controller.entity.GeneExpressionAnnotation.UNDEREXPRESSED;

import java.util.HashMap;
import java.util.Map;

import org.junit.Assert;
import org.junit.Test;

public class GeneExpressionTest {

    public static final Map<String, Double> TEST_DATA;
    public static final Map<String, GeneExpressionAnnotation> TEST_DATA_ANNOTATIONS;

    static {
        TEST_DATA = new HashMap<>();
        TEST_DATA.put("G001", -0.5);
        TEST_DATA.put("G002", -0.4);
        TEST_DATA.put("G003", -0.3);
        TEST_DATA.put("G004", -0.2);
        TEST_DATA.put("G005", -0.1);
        TEST_DATA.put("G006", 0.1);
        TEST_DATA.put("G007", 0.2);
        TEST_DATA.put("G008", 0.3);
        TEST_DATA.put("G009", 0.4);
        TEST_DATA.put("G010", 0.5);
        TEST_DATA.put("G011", 0.6);
        TEST_DATA.put("G012", 0.7);
        TEST_DATA.put("G013", 0.8);
        TEST_DATA.put("G014", 0.9);
        TEST_DATA.put("G015", 0.95);
        TEST_DATA.put("G016", 0.99);

        TEST_DATA_ANNOTATIONS = new HashMap<>();
        TEST_DATA_ANNOTATIONS.put("G001", UNDEREXPRESSED);
        TEST_DATA_ANNOTATIONS.put("G002", UNDEREXPRESSED);
        TEST_DATA_ANNOTATIONS.put("G003", UNDEREXPRESSED);
        TEST_DATA_ANNOTATIONS.put("G004", UNDEREXPRESSED);
        TEST_DATA_ANNOTATIONS.put("G005", UNDEREXPRESSED);
        TEST_DATA_ANNOTATIONS.put("G006", OVEREXPRESSED);
        TEST_DATA_ANNOTATIONS.put("G007", OVEREXPRESSED);
        TEST_DATA_ANNOTATIONS.put("G008", OVEREXPRESSED);
        TEST_DATA_ANNOTATIONS.put("G009", OVEREXPRESSED);
        TEST_DATA_ANNOTATIONS.put("G010", OVEREXPRESSED);
        TEST_DATA_ANNOTATIONS.put("G011", OVEREXPRESSED);
        TEST_DATA_ANNOTATIONS.put("G012", OVEREXPRESSED);
        TEST_DATA_ANNOTATIONS.put("G013", OVEREXPRESSED);
        TEST_DATA_ANNOTATIONS.put("G014", OVEREXPRESSED);
        TEST_DATA_ANNOTATIONS.put("G015", OVEREXPRESSED);
        TEST_DATA_ANNOTATIONS.put("G016", HIGHLY_OVEREXPRESSED);
    }

    @Test
    public void testGeneExpression() {
        GeneExpression gE = new GeneExpression(TEST_DATA);
        /*
         * To get the same result in R, we must use type = 6:
         * quantile(values, prob, type = 6)
         */
        Assert.assertEquals(0.962, gE.getHighlyOverExpressedThreshold(), 0.00000001);
        Assert.assertEquals(TEST_DATA_ANNOTATIONS, gE.getAnnotations());
    }
}
