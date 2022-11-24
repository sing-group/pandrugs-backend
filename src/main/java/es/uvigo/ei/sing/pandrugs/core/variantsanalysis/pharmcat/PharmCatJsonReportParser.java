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
package es.uvigo.ei.sing.pandrugs.core.variantsanalysis.pharmcat;

import static java.nio.file.Files.readAllLines;
import static java.util.stream.Collectors.joining;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.json.*;

public class PharmCatJsonReportParser {

    public static final String IMPLICATION_NOT_VALID_START_STRING = "The guideline does not provide a description of the impact of";

    private static void processDrugs(JSONObject drugs, Map<String, List<ReportAnnotation>> drugAnnotations) {
        for (String drugName : drugs.keySet()) {
            JSONObject drugObject = drugs.getJSONObject(drugName);
            String source = drugObject.getString("source");

            JSONArray drugGuidelines = drugObject.getJSONArray("guidelines");
            if (drugGuidelines != null && !drugGuidelines.isEmpty()) {
                for (int i = 0; i < drugGuidelines.length(); i++) {
                    JSONObject guideline = drugGuidelines.getJSONObject(i);

                    JSONArray guidelineAnnotations = guideline.getJSONArray("annotations");
                    if (guidelineAnnotations != null && !guidelineAnnotations.isEmpty()) {
                        for (int j = 0; j < guidelineAnnotations.length(); j++) {
                            JSONObject annotation = guidelineAnnotations.getJSONObject(j);

                            if (annotation.isNull("classification")) {
                                if (!drugAnnotations.containsKey(drugName)) {
                                    drugAnnotations.put(drugName, new ArrayList<>());
                                }
                                drugAnnotations.get(drugName).add(new WarningReportAnnotation(source));
                                break;
                            }

                            String classification = annotation.getString("classification");

                            if (!classification.equals("Strong") && !classification.equals("Moderate")) {
                                break;
                            }

                            JSONObject implications = annotation.getJSONObject("implications");

                            boolean excludeAnnotation = false;
                            Map<String, String> implicationsMap = new HashMap<>();
                            for (String implicationKey : implications.keySet()) {
                                String implicationValue = implications.getString(implicationKey);
                                if (implicationValue.startsWith(IMPLICATION_NOT_VALID_START_STRING)) {
                                    excludeAnnotation = true;
                                } else {
                                    implicationsMap.put(implicationKey, implicationValue);
                                }
                            }
                            if (excludeAnnotation) {
                                break;
                            }

                            String drugRecommendation = annotation.getString("drugRecommendation");
                            String population = annotation.getString("population");

                            if (!drugAnnotations.containsKey(drugName)) {
                                drugAnnotations.put(drugName, new ArrayList<>());
                            }
                            drugAnnotations.get(drugName).add(
                                    new ReportAnnotation(drugRecommendation, classification, population, source));
                        }
                    }
                }
            }
        }
    }

    private static Map<String, PharmCatAnnotation> processReportAnnotations(
            Map<String, List<ReportAnnotation>> reportAnnotations) {

        Map<String, PharmCatAnnotation> annotations = new HashMap<>();

        for (Entry<String, List<ReportAnnotation>> entry : reportAnnotations.entrySet()) {
            annotations.put(entry.getKey(), PharmCatAnnotation.fromData(entry.getKey(), entry.getValue()));
        }

        return annotations;
    }

    public static Map<String, PharmCatAnnotation> getPharmCatFilteredAnnotations(File input) throws IOException {
        String jsonString = readAllLines(input.toPath()).stream().collect(joining());
        JSONObject obj = new JSONObject(jsonString);

        Map<String, List<ReportAnnotation>> reportAnnotations = new HashMap<>();
        if (obj.has("drugs")) {
            JSONObject drugsObject = obj.getJSONObject("drugs");

            for (String drugSource : Arrays.asList("DPWG", "CPIC")) {
                if (drugsObject.has(drugSource)) {
                    processDrugs(drugsObject.getJSONObject(drugSource), reportAnnotations);
                }
            }
        }

        return processReportAnnotations(reportAnnotations);
    }
}
