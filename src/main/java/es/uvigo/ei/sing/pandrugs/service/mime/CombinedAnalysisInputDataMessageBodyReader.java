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
package es.uvigo.ei.sing.pandrugs.service.mime;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStreamReader;

import es.uvigo.ei.sing.pandrugs.service.entity.CnvData;
import es.uvigo.ei.sing.pandrugs.service.entity.CombinedAnalysisInputData;
import es.uvigo.ei.sing.pandrugs.service.entity.GeneExpressionData;
import es.uvigo.ei.sing.pandrugs.util.MapFileParser;
import es.uvigo.ei.sing.pandrugs.util.RnkFileParser;

public class CombinedAnalysisInputDataMessageBodyReader extends MultipartMessageBodyReader<CombinedAnalysisInputData> {
    private CnvData cnvData;
    private GeneExpressionData geneExpressionData;

    @Override
    protected void init() {
    }

    @Override
    protected void add(String name, byte[] bs) {
        if (name.equalsIgnoreCase("expressiongenerank")) {
            try (final InputStreamReader isr = new InputStreamReader(new ByteArrayInputStream(bs))) {
                this.geneExpressionData = new GeneExpressionData(RnkFileParser.loadInputStreamReader(isr));
            } catch (IOException ioe) {
                throw new RuntimeException(ioe);
            }
        }
        if (name.equalsIgnoreCase("cnv")) {
            try (final InputStreamReader isr = new InputStreamReader(new ByteArrayInputStream(bs))) {
                this.cnvData = new CnvData(MapFileParser.loadInputStreamReader(isr));
            } catch (IOException ioe) {
                throw new RuntimeException(ioe);
            }
        }
    }

    @Override
    protected CombinedAnalysisInputData build() {
        return new CombinedAnalysisInputData(this.cnvData, this.geneExpressionData);
    }
}
