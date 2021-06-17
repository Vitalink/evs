package org.imec.ivlab.core.constants;

import java.io.File;

public class CoreConstants {

    public static final String EVS_NAME = "EVS";
    public static final String EVS_WIKI_URL = "http://wiki.ivlab.ilabt.imec.be/";

    public static final String PATH_TO_PATIENT_CONFIG_FOLDER = ".." + File.separator + "config" + File.separator + "patients";
    public static final String PATH_TO_ACTOR_CONFIG_FOLDER = ".." + File.separator + "config" + File.separator + "actors";

    public static final String LOCATION_KMEHR_TABLES = "kmehr" + File.separator + "tables";
    public static final String LOCATION_KMEHR_TABLE_VERSIONS = "kmehr" + File.separator + "tableversions";

    public static final String KMEHR_LANGUAGE_L_ATTRIBUTE = "nl";

    public static final String EXPORT_NAME_VITALINK_PDF = "gatewayscheme";
    public static final String EXPORT_NAME_DAILYSCHEME = "dailyscheme";
    public static final String EXPORT_NAME_GLOBALSCHEME = "globalscheme";
    public static final String EXPORT_NAME_SUMEHR_OVERVIEW = "overview";
    public static final String EXPORT_NAME_DIARYNOTE_OVERVIEW = "visualization";
    public static final String EXPORT_NAME_VACCINATION_OVERVIEW = "visualization";
    public static final String EXPORT_NAME_POPULATION_BASED_SCREENING_OVERVIEW = "visualization";
    public static final String EXPORT_NAME_CHILD_PREVENTION_FILE = "visualization";
    public static final String EXPORT_NAME_CHILD_PREVENTION_FILE_EMBEDDED = "visualization-embedded";
    public static final String EXPORT_NAME_VACCINATION_LIST_OVERVIEW = "vaccination-list-visualization";

}
