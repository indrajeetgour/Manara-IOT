package com.slb.manara;

import org.apache.log4j.Logger;

import java.io.File;
import java.util.List;
import java.util.Map;

public class OP14Launcher {


    // Common variable throughout the program
    final static Logger logger = Logger.getLogger(OP14Launcher.class);
    private static final String[] WELL_HEADER_MAPPING = {"WELL_NAME", "WELL_ID", "TIME_ZONE"};
    private static final String[] STATIONIDENTIFIER_HEADER_MAPPING = {"STATION_IDENTIFIER", "STATION_ID", "WELL_ID"};
    private static final String[] IWICIDENTIFIER_HEADER_MAPPING = {"IWIC_IDENTIFIER", "IWIC_ID", "WELL_ID"};

    private static String tagMappingSheetName = "TagNameAliases";
    private static String tagMappingIWICSheetName = "IWIC_TagNameAliases";
    private static String tagsNameSheetName = "TagNames_Schema";
    private static String tagsNameIWICSheetName = "IWIC_TagNames_Schema";
    private static String manaraTagSheetName = "WWApp_Tags_to_select";
    private static String mfileType = "Manara";
    private static String ifileType = "IWIC";
    /***
     * Below variable for local run use
     */
    private static String wellMappingFileName = "C:\\Manara-raw-data\\mapping-inputs-files\\updated-one\\Mapping-files\\WellMapping.csv";
    private static String stationIdentifierMappingFromFile = "C:\\Manara-raw-data\\mapping-inputs-files\\updated-one\\Mapping-files\\StationIdentifierMapping.csv";
    private static String iWICIdentifierMappingFromFile = "C:\\Manara-raw-data\\mapping-inputs-files\\updated-one\\Mapping-files\\IWICIdentifierMapping.csv";

    /***
     * Below variable is used for CentOS run only
     */
//    private static String wellMappingFileName = "/home/manarauser/mapping-inputs-files/updated-one/Mapping-files/WellMapping.csv";
//    private static String stationIdentifierMappingFromFile = "/home/manarauser/mapping-inputs-files/updated-one/Mapping-files/StationIdentifierMapping.csv";
//    private static String ipPathOP14 = "/home/manarauser/THM Data OP-14 ENL/";
//    private static String tagsMappingFile = "/home/manarauser/mapping-inputs-files/updated-one/Mapping-files/TagNamesAndAliases_2018_01_31.xlsx";
//    private static String manaraMeasurementFile = "/home/manarauser/mapping-inputs-files/updated-one/Manara_SRS007_1_10_RTAC_public_measurements_definition.xlsx";
    private static String ipPathOP14 = "C:\\Manara-raw-data\\THM Data OP-14 ENL\\";
    // New Output schema is available in this TagNamesAndAliases file
    private static String tagsMappingFile = "C:\\Manara-raw-data\\mapping-inputs-files\\updated-one\\Mapping-files\\TagNamesAndAliases_2018_01_31.xlsx";
    private static String manaraMeasurementFile = "C:\\Manara-raw-data\\mapping-inputs-files\\updated-one\\Manara_SRS007_1_10_RTAC_public_measurements_definition.xlsx";
    boolean serverVariableFlag = false;
    boolean localVariableFlag = true;

    public static void main(String[] args) {
        /*
        Properties prop = new Properties();
        InputStream input = null;
        try {
            input = new FileInputStream("local-config.properties");
            // load a properties file
            prop.load(input);
            // get the property value and print it out
            System.out.println(":::::::::::"+prop.getProperty("tagMappingSheetName"));
            System.out.println("::");


        } catch (IOException ex) {
            ex.printStackTrace();
        } finally {
            if (input != null) {
                try {
                    input.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
*/

        System.out.println("\nManara raw files Pre-processing Started : \n");
        long start = System.nanoTime(); // its for get the start time of the process
        FileProcessingInputs fileProcessingInputs = new FileProcessingInputs();
        fileProcessingInputs.setSTATIONIDENTIFIER_HEADER_MAPPING(STATIONIDENTIFIER_HEADER_MAPPING);
        fileProcessingInputs.setWELL_HEADER_MAPPING(WELL_HEADER_MAPPING);
        fileProcessingInputs.setWellMappingFileName(wellMappingFileName);
        fileProcessingInputs.setStationIdentifierMappingFromFile(stationIdentifierMappingFromFile);
        fileProcessingInputs.setTagsNameSheetName(tagsNameSheetName);
        fileProcessingInputs.setTagsNameIWICSheetName(tagsNameIWICSheetName);

        fileProcessingInputs.setTagMappingSheetName(tagMappingSheetName);
        fileProcessingInputs.setTagMappingIWICSheetName(tagMappingIWICSheetName);
        fileProcessingInputs.setTagsMappingFileName(tagsMappingFile);
        fileProcessingInputs.setmFileType(mfileType);
        fileProcessingInputs.setiFileType(ifileType);
        fileProcessingInputs.setManaraTagSheetName(manaraTagSheetName);
        fileProcessingInputs.setManaraMeasurementFile(manaraMeasurementFile);
        fileProcessingInputs.setIWICIDENTIFIER_HEADER_MAPPING(IWICIDENTIFIER_HEADER_MAPPING);
        fileProcessingInputs.setiWICIdentifierMappingFromFile(iWICIdentifierMappingFromFile);
//        Below code is for old SHYB-97 raw file read
//        RawFileReader.getAllFiles(ipPath, opPath);
//        Below code is for newly formatted OP-14 Well and all the future well
        OP14RawFileParser op14RawFileReader = new OP14RawFileParser();
//        op14RawFileReader.createWellAndStationMapping(wellMappingFileName,stat);
        Map<String, List<File>> inputFilesList = null;
        List<String> diagnosticFilterTagsManara = null;
        List<String> diagnosticFilterTagsIWIC = null;

        try {
            // Just to get all the possibility of disgnostic parameters using Manara SRS007 and TagNamesAlias files
            diagnosticFilterTagsManara = op14RawFileReader.getDiagnosticFilterTagsManara(fileProcessingInputs);
//            diagnosticFilterTagsManara = op14RawFileReader.getDiagnosticFilterTagsManara(manaraMeasurementFile, manaraTagSheetName, tagsMappingFile, tagMappingSheetName, "Manara");
            fileProcessingInputs.setDiagnosticFilterTagsManara(diagnosticFilterTagsManara);

            // Just to get all the possibility of disgnostic parameters using Manara SRS007 and IWIC_TagNameAliases files
            diagnosticFilterTagsIWIC = op14RawFileReader.getDiagnosticFilterTagsIWIC(fileProcessingInputs);
//            diagnosticFilterTagsIWIC = op14RawFileReader.getDiagnosticFilterTagsManara(manaraMeasurementFile, manaraTagSheetName, tagsMappingFile, tagMappingIWICSheetName, "IWIC");
            fileProcessingInputs.setDiagnosticIWICFilterTags(diagnosticFilterTagsIWIC);

            // Getting all the files list with WellName as key
            inputFilesList = op14RawFileReader.getAllFilesList(ipPathOP14);
            fileProcessingInputs.setInputFilesList(inputFilesList);

        } catch (Exception e) {
            e.printStackTrace();
        }

        if (inputFilesList == null || inputFilesList.size() == 0) {
            logger.error("ERROR in class RawMainCall : inputFilesList is NULL or empty ");
            System.exit(0);
        }


        try {
            // For reading the existing/old Well and Station mapping file from disk and update the current object with those mapping values
            op14RawFileReader.createWellAndStationMapping(fileProcessingInputs);

            /***
             * Starting Main Parser now
             */
            int output = op14RawFileReader.processInputFileList(fileProcessingInputs);

        } catch (WellTimestampConversionUnitException e) {
            e.printStackTrace();
            logger.error(e.getMessage());
            logger.error(e);
            if (e.getCause() != null) {
                logger.error(" Original Exception : " + e.getCause().getMessage());
            }
            logger.error("Now explicitly Failing the program !!");
            System.exit(-1);
        } catch (Exception e) {
            e.printStackTrace();
            logger.error("Exception found : " + e);
        }

        long end = System.nanoTime(); // its for get the end point of the process
        double elapsedTime = (end - start) / 1000000000.0;
        System.out.println("Time taken by job: " + elapsedTime); // calculating the total time taken by the method call above in sec

    }
}