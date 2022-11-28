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

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;

public class MapFileParser {
    public static Map<String, String> loadFile(File file) throws FileNotFoundException, IOException {
        try (final InputStreamReader isr = new InputStreamReader(new FileInputStream(file))) {
            return loadInputStreamReader(isr);
        }
    }

    public static Map<String, String> loadInputStreamReader(InputStreamReader reader) throws IOException {
        try (BufferedReader br = new BufferedReader(reader)) {
            String line;
            Map<String, String> map = new HashMap<>();
            while ((line = br.readLine()) != null) {
                line = line.trim();
                if (line.isEmpty())
                    continue;

                final String[] tokens = line.split("\t");

                if (tokens.length != 2) {
                    throw new RuntimeException("Illegal format in line: " + line);
                } else {
                    map.put(tokens[0], tokens[1]);
                }
            }

            return map;
        }
    }
}
