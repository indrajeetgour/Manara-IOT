package com.slb.manara;

import org.apache.log4j.Logger;

import java.io.File;
import java.util.List;
import java.util.Map;

public class OP14Launcher {
    final static Logger logger = Logger.getLogger(OP14Launcher.class);

    private static final String[] WELL_HEADER_MAPPING = {"WELL_NAME", "WELL_ID", "TIME_ZONE"};
    private static final String[] STATIONIDENTIFIER_HEADER_MAPPING = {"STATION_IDENTIFIER", "STATION_ID", "WELL_ID"};
    private static String wellMappingFileName = "C:\\Manara-raw-data\\mapping-inputs-files\\updated-one\\Mapping-files\\WellMapping.csv";
    //        private static String wellMappingFileName = "/home/manarauser/mapping-inputs-files/updated-one/Mapping-files/WellMapping.csv";
    private static String stationIdentifierMappingFromFile = "C:\\Manara-raw-data\\mapping-inputs-files\\updated-one\\Mapping-files\\StationIdentifierMapping.csv";
//    private static String stationIdentifierMappingFromFile = "/home/manarauser/mapping-inputs-files/updated-one/Mapping-files/StationIdentifierMapping.csv";

    //        Its for OP-14 well
    private static String ipPathOP14 = "C:\\Manara-raw-data\\THM Data OP-14 ENL\\";
    private static String opPathOP14 = "C:\\Manara-raw-data\\THM Data OP-14 ENL Pre-processed Data\\";
    // as per manara server
//    private static String ipPathOP14 = "/home/manarauser/THM Data OP-14 ENL/";
//    private static String opPathOP14 = "/home/manarauser/THM Data OP-14 ENL Pre-processed Data/";

    //    New Output schema is available in this TagNamesAndAliases file
    private static String tagsMappingFile = "C:\\Manara-raw-data\\mapping-inputs-files\\updated-one\\Mapping-files\\TagNamesAndAliases_2018_01_31.xlsx";
//    private static String tagsMappingFile = "/home/manarauser/mapping-inputs-files/updated-one/Mapping-files/TagNamesAndAliases_2018_01_31.xlsx";

    private static String manaraTagSheetName = "WWApp_Tags_to_select";
    private static String manaraMeasurementFile = "C:\\Manara-raw-data\\mapping-inputs-files\\updated-one\\Manara_SRS007_1_10_RTAC_public_measurements_definition.xlsx";
    //        private static String manaraMeasurementFile = "/home/manarauser/mapping-inputs-files/updated-one/Manara_SRS007_1_10_RTAC_public_measurements_definition.xlsx";
    private static String tagMappingSheetName = "TagNameAliases";
    private static String tagsNameSheetName = "TagNames_Schema";


    public static void main(String[] args) throws Exception {


        System.out.println("\n Preprocess started :");
        long start = System.nanoTime(); // its for get the start time of the process

//        Below code is for old SHYB-97 raw file read
//        RawFileReader.getAllFiles(ipPath, opPath);
//        Below code is for newly formatted OP-14 Well and all the future well
        OP14RawFileParser op14RawFileReader = new OP14RawFileParser();
//        op14RawFileReader.createWellAndStationMapping(wellMappingFileName,stat);
        // Just to get all the possibility of disgnostic parameters using Manara SRS007 and TagNamesAlias files
        List<String> diagnosticFilterTags = op14RawFileReader.getDiagnosticFilterTags(manaraMeasurementFile, manaraTagSheetName, tagsMappingFile, tagMappingSheetName);
        // Getting all the files list with WellName as key
        Map<String, List<File>> inputFilesList = op14RawFileReader.getAllFilesList(ipPathOP14, opPathOP14);

        if (inputFilesList == null || inputFilesList.size() == 0) {
            logger.error("ERROR in class RawMainCall : inputFilesList is NULL or empty ");
            System.exit(0);
        }

        FileProcessingInputs fileProcessingInputs = new FileProcessingInputs();
        fileProcessingInputs.setInputFilesList(inputFilesList);
        fileProcessingInputs.setSTATIONIDENTIFIER_HEADER_MAPPING(STATIONIDENTIFIER_HEADER_MAPPING);
        fileProcessingInputs.setWELL_HEADER_MAPPING(WELL_HEADER_MAPPING);
        fileProcessingInputs.setWellMappingFileName(wellMappingFileName);
        fileProcessingInputs.setStationIdentifierMappingFromFile(stationIdentifierMappingFromFile);
        fileProcessingInputs.setDiagnosticFilterTags(diagnosticFilterTags);
        fileProcessingInputs.setTagMappingSheetName(tagMappingSheetName);
        fileProcessingInputs.setTagsMappingFileName(tagsMappingFile);
        fileProcessingInputs.setTagsNameSheetName(tagsNameSheetName);

        // For reading the old well and station mapping file and update the current object with those mapping values
        op14RawFileReader.createWellAndStationMapping(fileProcessingInputs);
        int output = op14RawFileReader.processInputFileList(fileProcessingInputs);


//        OP14RawFileReader.getAllFiles(ipPathOP14, opPathOP14);

        long end = System.nanoTime(); // its for get the end point of the process
        double elpsedTime = (end - start) / 1000000000.0;
        System.out.println("Time taken by job: " + elpsedTime); // calculating the total time taken by the method call above in sec

    }
}