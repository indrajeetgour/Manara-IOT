package com.slb.manara;

import au.com.bytecode.opencsv.CSVReader;
import au.com.bytecode.opencsv.CSVWriter;
import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.LinkedListMultimap;
import com.google.common.collect.Multimap;
import com.google.common.collect.Multimaps;
import com.monitorjbl.xlsx.StreamingReader;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;
import org.apache.log4j.Logger;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class OP14RawFileParser {
    final static Logger logger = Logger.getLogger(OP14RawFileParser.class);
    private MappingClass wellName = new MappingClass();

    /*
     * Move the processed file from current raw folder to Archived location
     */
    private MappingClass stationIdentifier = new MappingClass();
    private MappingClass iWICIdentifier = new MappingClass();

    /*
     * Starting point of code
     * Step 1 : Taken care for all the diagnostic parameters from different sources and merging into one
     * Step 2 : Read all the data for wells and laterals from the csv file so that actual values can be replaced
     * Step 3 : Reading the directory one by one and for parsing
     * Step 4 : Start the parsing process by calling readCsvFile method
     * Step 5 : moving files from original directory Archive directory after the successful completion of parsing
     */
    private Multimap<String, String> diagnosticFilterELSXRead(String excelFileLoc, String sheetName, String fileType) throws Exception {
        logger.info("Start executing diagnosticFilterELSXRead method !!!");

        Multimap<String, String> returnMap = ArrayListMultimap.create();
        InputStream is = new FileInputStream(new File(excelFileLoc));
        Workbook workbook = StreamingReader.builder()
                .rowCacheSize(100)    // number of rows to keep in memory (defaults to 10)
                .bufferSize(4096)     // buffer size to use when reading InputStream to file (defaults to 1024)
                .open(is);            // InputStream or File for XLSX file (required)

        Sheet sheet = workbook.getSheet(sheetName);
        if (sheet == null) {
            logger.error("excel Sheet with name : " + sheetName + " does not exist.");
            throw new IOException("Excel sheet with name not found.");
        }
//        Printing process sheet name
        String sheetNm = sheet.getSheetName();
        logger.info("Current WorkBook/Sheet Name : " + sheetNm);
        if (sheetNm.equals("TagNameAliases") || sheetNm.equals("IWIC_TagNameAliases")) {
            logger.info("Reading " + sheetNm + " sheet !!!!!!!");
            for (Row r : sheet) {
                List<String> row = new ArrayList();
                for (Cell c : r) {
                    String colVal = c.getStringCellValue();
                    if (colVal != null || colVal.length() > 0)
                        row.add(colVal);
                    else
                        row.add("");
                }
//                Preventing to read header i.e. TagNameID and TagNameAlias just read rest
                if (!row.get(0).equals("TagNameId") && !row.get(1).equals("TagNameAlias"))
                    returnMap.put(row.get(0), row.get(1));
            }
        } else if (sheetNm.equals("WWApp_Tags_to_select")) {
            logger.info("Reading WWApp_Tags_to_select sheet !!!!!!!");
            for (Row r : sheet) {
                List<String> row = new ArrayList();
                for (Cell c : r) {
                    String colVal = c.getStringCellValue();
                    if (colVal != null || colVal.length() > 0)
                        row.add(colVal);
                    else
                        row.add("");
                }
                if (row.size() == 31) {
                    if (fileType.equals("Manara")) {
                        // Preventing to read header i.e. Manara and Diagnostic just read rest | also added "Critical Parameter" as 1 for selected records
                        if (row.get(0).equals("Manara") && row.get(27).equals("1") && row.get(28).equals("Diagnostic")) {
                            returnMap.put(row.get(30), row.get(4));
                        }
                    } else if (fileType.equals("IWIC")) {
                        // Preventing to read header i.e. Manara and Diagnostic just read rest | also added "Critical Parameter" as 1 for selected records
                        if (row.get(0).equals("Iwic") && row.get(27).equals("1") && row.get(28).equals("Diagnostic")) {
                            returnMap.put(row.get(30), row.get(4));
                        }
                    } else {
                        System.out.println("diagnosticFilterELSXRead method not able to read data for Manara as well as IWIC!!");
                    }

                }
            }
        }
        return returnMap;
    }


    private Multimap<String, String> readELSXFile(String excelFileLoc, String sheetName, String timeStampColumnName, Boolean isTagIdRequired) throws Exception {
        logger.info("Start executing readELSXFile method !!!");

        Multimap<String, String> returnMap = LinkedListMultimap.create();
        InputStream is = new FileInputStream(new File(excelFileLoc));
        Workbook workbook = StreamingReader.builder()
                .rowCacheSize(100)    // number of rows to keep in memory (defaults to 10)
                .bufferSize(4096)     // buffer size to use when reading InputStream to file (defaults to 1024)
                .open(is);            // InputStream or File for XLSX file (required)

        Sheet sheet = workbook.getSheet(sheetName);
        if (sheet == null) {
            logger.error("excel Sheet with name : " + sheetName + " does not exist.");
            throw new IOException("Excel sheet with name not found.");
        }
//        Printing process sheet name
        String sheetNm = sheet.getSheetName();
        logger.info("WorkBook/Sheet Name : " + sheetNm);
        if (sheetNm.equals("TagNames_Schema")) {
            logger.info("Reading TagNames_Schema sheet !!!!!!!");
            int index = 0;
            for (Row r : sheet) {
                List<String> row = new ArrayList();
                for (Cell c : r) {
                    String colVal = c.getStringCellValue().replaceAll("\"", "");
                    if (colVal != null || colVal.length() > 0)
                        row.add(colVal);
                    else
                        row.add("");
                }
                if (!row.get(0).equals("TagNameId") && !row.get(1).equals("Name")) {
                    if (isTagIdRequired) {
                        returnMap.put(row.get(0), row.get(1));
                        index = Integer.parseInt(row.get(0));
                    }
                } else {
                    returnMap.put("0", timeStampColumnName.toUpperCase());
                }
            }
            returnMap.put(String.valueOf(index + 1), "WELL_ID");
            returnMap.put(String.valueOf(index + 2), "STATION_ID");
        }
        if (sheetNm.equals("IWIC_TagNames_Schema")) {
            logger.info("Reading TagNames_Schema sheet !!!!!!!");
            int index = 0;
            for (Row r : sheet) {
                List<String> row = new ArrayList();
                for (Cell c : r) {
                    String colVal = c.getStringCellValue().replaceAll("\"", "");
                    if (colVal != null || colVal.length() > 0)
                        row.add(colVal);
                    else
                        row.add("");
                }
                if (!row.get(0).equals("TagNameId") && !row.get(1).equals("Name")) {
                    if (isTagIdRequired) {
                        returnMap.put(row.get(0), row.get(1));
                        index = Integer.parseInt(row.get(0));
                    }
                } else {
                    returnMap.put("0", timeStampColumnName.toUpperCase());
                }
            }
            returnMap.put(String.valueOf(index + 1), "WELL_ID");
        }
        return returnMap;
    }


    private LinkedHashMap<String, String> readELSXFile(String excelFileLoc, String sheetName, String timeStampColumnName) throws Exception {
        logger.info("Start executing readELSXFile method !!!");

        LinkedHashMap<String, String> returnMap = new LinkedHashMap<String, String>();
//        Multimap<String, String> returnMap = LinkedListMultimap.create();
        InputStream is = new FileInputStream(new File(excelFileLoc));
        Workbook workbook = StreamingReader.builder()
                .rowCacheSize(100)    // number of rows to keep in memory (defaults to 10)
                .bufferSize(4096)     // buffer size to use when reading InputStream to file (defaults to 1024)
                .open(is);            // InputStream or File for XLSX file (required)

        Sheet sheet = workbook.getSheet(sheetName);
        if (sheet == null) {
            logger.error("excel Sheet with name : " + sheetName + " does not exist.");
            throw new IOException("Excel sheet with name not found.");
        }
//        Printing process sheet name
        String sheetNm = sheet.getSheetName();
        logger.info("WorkBook/Sheet Name : " + sheetNm);
        if (sheetNm.equals("TagNames_Schema")) {
            logger.info("Reading TagNames_Schema sheet !!!!!!!");
            int index = 0;
            for (Row r : sheet) {
                List<String> row = new ArrayList();
                for (Cell c : r) {
                    String colVal = c.getStringCellValue().replaceAll("\"", "");
                    if (colVal != null || colVal.length() > 0)
                        row.add(colVal);
                    else
                        row.add("");
                }
                if (!row.get(0).equals("TagNameId") && !row.get(1).equals("Name")) {
                    returnMap.put(row.get(0), row.get(1));
                    index = Integer.parseInt(row.get(0));
                } else { // this else is just to add TimeStamp at the header place means first line only :)
                    returnMap.put("0", timeStampColumnName.toUpperCase());
                }
            }
            // adding well_id and station_id added at the end
            returnMap.put(String.valueOf(index + 1), "WELL_ID");
            returnMap.put(String.valueOf(index + 2), "STATION_ID");
        }
        if (sheetNm.equals("IWIC_TagNames_Schema")) {
            logger.info("Reading TagNames_Schema sheet !!!!!!!");
            int index = 0;
            for (Row r : sheet) {
                List<String> row = new ArrayList();
                for (Cell c : r) {
                    String colVal = c.getStringCellValue().replaceAll("\"", "");
                    if (colVal != null || colVal.length() > 0)
                        row.add(colVal);
                    else
                        row.add("");
                }
                if (!row.get(0).equals("TagNameId") && !row.get(1).equals("Name")) {
                    returnMap.put(row.get(0), row.get(1));
                    index = Integer.parseInt(row.get(0));
                } else { // this else is just to add TimeStamp at the header place means first line only :)
                    returnMap.put("0", timeStampColumnName.toUpperCase());
                }
            }
            // adding well_id added at the end
            returnMap.put(String.valueOf(index + 1), "WELL_ID");
        }
        return returnMap;
    }

    /*
     *  Method is for getting the muliple tags from tagsAlias file and mearge with Manara Measurment file
     */
    public Multimap<String, String> getOneTagAliasMapping(Multimap<String, String> manaraMeasurementMultiMap, Multimap<String, String> tagsMultiMap) {
        logger.info("Start executing getOneTagAliasMapping method !!!");
        Multimap<String, String> allTagsMultiMap = ArrayListMultimap.create();

        for (Object key : manaraMeasurementMultiMap.keySet()) {
//            Manara Diagnostic parameters will always to part of that
            allTagsMultiMap.putAll((String) key, manaraMeasurementMultiMap.get((String) key));
            if (tagsMultiMap.containsKey(key)) {
                allTagsMultiMap.putAll((String) key, tagsMultiMap.get((String) key));
            }
        }
//        Can be search for removing duplicate key value pairs
        return allTagsMultiMap;
    }

    public void moveFilesAfterPreprocessing(String sourceFolder, String archiveFolder, String fileName) {
        logger.info("Start executing moveFilesAfterPreprocessing method !!!");

        logger.info(" Moving file from source to Archive dir !!!!!!");
        File sourceDir = new File(sourceFolder);
        File archiveParentDir = new File(archiveFolder).getParentFile();
        if (!archiveParentDir.exists()) {
            archiveParentDir.mkdirs();
        }
//      Move fileName from argument, source to archive location
//        Condition : same file should not be present in archive location before you run this
//                    else the file would not be copied into
        boolean bol = sourceDir.renameTo(new File(archiveFolder));
        if (bol)
            logger.info("sourceDir :" + sourceDir + " $$$$ Successful copied into : " + archiveParentDir + " $$ Loc, file copied was : " + fileName);
        else
            logger.info("sourceDir :" + sourceDir + " #### Failed copied into : " + archiveParentDir + " ## Loc, file copied was : " + fileName);
    }

    /*
     * Method is for the Well and Lateral csv file read
     * and to map both csv file reading into list
     * to Create the new distinct mapping csv file for both
     * */
    public void createMappingList(List<String[]> allRecords, MappingClass mappingList) {
        logger.info("Start executing createMappingList method !!!");
        try {

            for (int i = 0; i < allRecords.size(); i++) {
                String[] row = allRecords.get(i);
                if (i > 0) {
//                mappingList/wellName is reference of MappingClass
//                below put will going to add only new well/lateral into maps its and custom maps implementation in MappingClass
                    mappingList.put(row[0], Integer.parseInt(row[1]), row[2]);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            logger.error("Failed : whiling reading the " + mappingList.toString() + " records !!");
            logger.error("Exception found : " + e);
        }
    }

    /***
     * Read each raw file one by one
     */
    public List<String[]> readCsvFile(String ipFilePath) {
        logger.info("Start executing readCsvFile method !!!");

        CSVReader csvReader = null;
        List<String[]> allRecrods = null;
        Reader reader = null;
        try {
            reader = Files.newBufferedReader(Paths.get(ipFilePath));
//            reader = Files.newBufferedReader(Paths.get(ipFilePath), Charset.forName("ISO-8859-1"));
            csvReader = new CSVReader(reader);
            allRecrods = csvReader.readAll();
        } catch (Exception e) {
            logger.error("Error in CsvFileReader !!!");
            e.printStackTrace();
            logger.error("Exception : ", e);
        } finally {
            try {
                reader.close();
                csvReader.close();
            } catch (IOException e) {
                logger.error("Error while closing fileReader/csvFileParser !!!");
                e.printStackTrace();
                logger.error("IOException : ", e);
            }
        }
        return allRecrods;
    }

    /**
     * @param excelFileLoc
     * @param sheetName
     * @purpose Make read the Unit conversion column A and B to do base conversion part
     */
    private Multimap<String, TagsDetails> createTagDetailsMap(String excelFileLoc, String sheetName) throws Exception {
        logger.info("Start executing createTagDetailsMap method !!!");

        Multimap<String, TagsDetails> returnMap = LinkedListMultimap.create();
        InputStream is = new FileInputStream(new File(excelFileLoc));
        Workbook workbook = StreamingReader.builder()
                .rowCacheSize(100)    // number of rows to keep in memory (defaults to 10)
                .bufferSize(4096)     // buffer size to use when reading InputStream to file (defaults to 1024)
                .open(is);            // InputStream or File for XLSX file (required)

        Sheet sheet = workbook.getSheet(sheetName);
        if (sheet == null) {
            logger.error("excel Sheet with name : " + sheetName + " does not exist.");
            throw new IOException("Excel sheet with name not found.");
        }
//        Printing process sheet name
        String sheetNm = sheet.getSheetName();
        logger.info("Sheet Name : " + sheetNm);
        if (sheetNm.equals("TagNameAliases") || sheetNm.equals("IWIC_TagNameAliases")) {
            logger.info("Reading " + sheetNm + " sheet !!!!!!!");
            for (Row r : sheet) {
                List<String> row = new ArrayList();
                for (Cell c : r) {
                    String colVal = c.getStringCellValue().replaceAll("\"", "");
                    if (colVal != null || colVal.length() > 0)
                        row.add(colVal);
                    else
                        row.add("");
                }
                if (!row.get(0).equals("TagNameId") && !row.get(1).equals("TagNameAlias")) {
                    TagsDetails tagsDetailsForRow = new TagsDetails(row.get(0), row.get(1), row.get(3), row.get(4));
                    returnMap.put(tagsDetailsForRow.getTagName(), tagsDetailsForRow);
                }
            }
        }
        return returnMap;
    }

    public void createProcessedFileManara(FileProcessingInputs fileProcessingInputs) throws WellTimestampConversionUnitException {
        logger.info("Start executing createProcessedFileManara method !!!");

        Map<Integer, String> updatedHeader = null;
        LinkedHashMap<String, String> tagNamesMultiMap = null;
        Multimap<String, String> allTagNamesMultiMap = null;
        List<String[]> allRecords = fileProcessingInputs.getAllRecords();
        String timestampUnitConvVal = null;
        String timestampColumnNameFinalValue = null;
        String strWellName = fileProcessingInputs.getCurrWellName();
        String currStationName = fileProcessingInputs.getCurrStationName();

        /***
         * preparing the TIMESTAMP Column name, if conversion unit is there or not
         */
        String timeStampColumnName = allRecords.get(0)[0];
        if (timeStampColumnName.matches(".*\\d+.*")) { // this is, when timestamp col is having Conversion Unit attached to it like +11:30
            fileProcessingInputs.setTimestampConversionFlag(true);
            if (timeStampColumnName.split("\\(")[0].toLowerCase().contains("Timestamp".toLowerCase())) {
                timestampColumnNameFinalValue = "TS";
            }
            // It's for timestamp conversion unit only
            Matcher matcher = Pattern.compile("\\((.*?)\\)").matcher(timeStampColumnName);
            if (matcher.find()) {
                timestampUnitConvVal = matcher.group(1);
                fileProcessingInputs.setTimestampUnitConvVal(timestampUnitConvVal);
            }
            // override the value if not match
            if (wellName.containsKey(strWellName)) {
                WellAndStationMapping objWellAndStationMapping = wellName.get(strWellName);
                // If current timestampUnitConvVal is not matching with existing wellName entry, then overriding that
                if (!objWellAndStationMapping.getWellOrTimeStamp().equals(timestampUnitConvVal)) {
                    objWellAndStationMapping.setWellOrTimeStamp(timestampUnitConvVal);
                    // ASK AS will that put will replace the new override value
                    wellName.put(strWellName, objWellAndStationMapping);
                }
            } else {
                wellName.addToMap(strWellName, timestampUnitConvVal);
            }
        } else { // this else is, when TimestampUnitConversion not found in timestamp col
            fileProcessingInputs.setTimestampConversionFlag(false);
            if (timeStampColumnName.toLowerCase().contains("Timestamp".toLowerCase())) {
                timestampColumnNameFinalValue = "TS";
                // Now checking if the CSV WELL mapping file has that TimestampUnitConversion for current WELL
                if (wellName.containsKey(strWellName)) {
                    WellAndStationMapping objWellAndStationMapping = wellName.get(strWellName);
                    try {
                        //  If TimestampUnitConversion is empty of null
                        if (objWellAndStationMapping.getWellOrTimeStamp() != null && objWellAndStationMapping.getWellOrTimeStamp().length() != 0) {
                            // Get the value of TimestampUnitConversion from well mapping file
                            timestampUnitConvVal = objWellAndStationMapping.getWellOrTimeStamp();
                            //read from csv file and set into obj : fileProcessingInputs.setTimestampUnitConvVal
                            fileProcessingInputs.setTimestampUnitConvVal(timestampUnitConvVal);
                        } else {
                            // throw error if we did not get the TimestampUnitConversion unit from WELL mapping file too
                            throw new WellTimestampConversionUnitException("TimestampUnitConversion Unit did NOT FOUND in Well Mapping CSV file!!", objWellAndStationMapping.toString());
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        logger.error("WellTimestampConversionUnitException : TimestampUnitConversion Unit NOT found in Mapping Well CSV file !!", e);
                        throw new WellTimestampConversionUnitException("TimestampUnitConversion Unit NOT FOUND in Well Mapping CSV file !!", objWellAndStationMapping.toString(), e);
                    }
                } else {
                    /***
                     * Although don't have Timestamp conversion unit but adding that current wellName and wellID to well mapping file, so for her just to update Conversion Unit for that Well would be really easy.
                     * Below list of code is just meant for that
                     */
                    timestampUnitConvVal = "";
                    fileProcessingInputs.setTimestampUnitConvVal(timestampUnitConvVal);
                    wellName.addToMap(strWellName, fileProcessingInputs.getTimestampUnitConvVal());
                    try {
                        writeIntoMappingCSV(fileProcessingInputs.getWellMappingFileName(), wellName, fileProcessingInputs.getWELL_HEADER_MAPPING());
                    } catch (Exception e) {
                        e.printStackTrace();
                        logger.error("Writing to well mapping csv is filed : " + e);
                    }
                    logger.info("Although we don't have Timestamp conversion unit, but adding Current WellName and WellID to well mapping file, Only need to update latest Conversion Unit for Current Well : " + fileProcessingInputs.getCurrWellName());
                    throw new WellTimestampConversionUnitException("TimestampUnitConversion Unit NOT FOUND in either Raw file as well as Well Mapping CSV file!!", timestampUnitConvVal);
                }
            } else {
                // In case if timestamp col does not contains timestamp is value, then just copy as is to first column
                timestampColumnNameFinalValue = timeStampColumnName;
            }
        }

        /***
         * Below code is for the updating well and station name and ID in the Mapping CSV files for each Manara/IWIC file read
         */
        wellName.addToMap(fileProcessingInputs.getCurrWellName(), fileProcessingInputs.getTimestampUnitConvVal());
        String wellMappingId = String.valueOf(wellName.get(fileProcessingInputs.getCurrWellName()).getIdCol());
        /***
         * Implementation 1
         * Writing the final well mapping files into csv for each run
         */
        try {
            writeIntoMappingCSV(fileProcessingInputs.getWellMappingFileName(), wellName, fileProcessingInputs.getWELL_HEADER_MAPPING());
        } catch (Exception e) {
            e.printStackTrace();
        }

        // Just taken care, stationIdentifier is mapped only in case of Manara file not IWIC
        String stationMappingId = "";
        // If new station came, well id for that would always be empty, she cloud fill that
        stationIdentifier.addToMap(fileProcessingInputs.getCurrStationName(), "");
        stationMappingId = String.valueOf(stationIdentifier.get(currStationName).getIdCol());
        /***
         * Implementation 2
         * Writing the final well and Station mapping files into csv file for each run read
         */
        try {
            writeIntoMappingCSV(fileProcessingInputs.getStationIdentifierMappingFromFile(), stationIdentifier, fileProcessingInputs.getSTATIONIDENTIFIER_HEADER_MAPPING());
        } catch (Exception e) {
            e.printStackTrace();
        }


        /***
         * Making finalOPFileName ready by changing the final file name with mapping CSV to hide all wellName and station name from file name
         */
        String updatedWellMappingOPFileName = fileProcessingInputs.getCurrProcessingFileName().replaceAll(fileProcessingInputs.getCurrWellName(), String.valueOf(wellName.get(fileProcessingInputs.getCurrWellName()).getIdCol()));
        String updatedWellStationMappingOPFileName = updatedWellMappingOPFileName.replaceAll(fileProcessingInputs.getCurrStationName(), String.valueOf(stationIdentifier.get(fileProcessingInputs.getCurrStationName()).getIdCol()));
        String updatedWellStationLateralMappingOPFileName = updatedWellStationMappingOPFileName.replaceAll(fileProcessingInputs.getCurrLateralName(), String.valueOf(stationIdentifier.get(fileProcessingInputs.getCurrStationName()).getIdCol()));

        String finalOPFileName = fileProcessingInputs.getPreProcessedOpDir() + "/" + updatedWellStationLateralMappingOPFileName;
        fileProcessingInputs.setFileOut(finalOPFileName);


        Map<Integer, String> outFileColumnMapping;
        Multimap<String, TagsDetails> tagsDetailsMultimap = ArrayListMultimap.create();
        Map<Integer, TagsDetails> UnitConvertDetailsMap = new HashMap<>();

        try {
            Writer writer = Files.newBufferedWriter(Paths.get(fileProcessingInputs.getFileOut()));
            CSVWriter csvWriter = new CSVWriter(writer, CSVWriter.DEFAULT_SEPARATOR, CSVWriter.NO_QUOTE_CHARACTER,
                    CSVWriter.DEFAULT_ESCAPE_CHARACTER, CSVWriter.DEFAULT_LINE_END);
            /***
             * Output Schema preparation, adding TimeStamp to schema at position one
             */
            // This thing need to be call as per MANARA or IWIC one level
            tagNamesMultiMap = readELSXFile(fileProcessingInputs.getTagsMappingFileName(),
                    fileProcessingInputs.getTagsNameSheetName(), timestampColumnNameFinalValue);
            allTagNamesMultiMap = readELSXFile(fileProcessingInputs.getTagsMappingFileName(),
                    fileProcessingInputs.getTagsNameSheetName(), timestampColumnNameFinalValue, true);


            outFileColumnMapping = new LinkedHashMap<Integer, String>((int) tagNamesMultiMap.size());
            // Initializing the outFileColumnMapping values with -1
            for (int outputColId = 0; outputColId < tagNamesMultiMap.size(); outputColId++) {
                outFileColumnMapping.put(outputColId, "-1");
            }
            // Adding all the rest of the tagsNames/parameters from TagName_Schema/IWIC_TagNames_Schema
            for (Object key : tagNamesMultiMap.keySet()) {
                if (fileProcessingInputs.getSuperSetOfAllTagsMultiMapManara().containsKey(key)) {
                    allTagNamesMultiMap.putAll((String) key, fileProcessingInputs.getSuperSetOfAllTagsMultiMapManara().get((String) key));
                }
            }

            Multimap<String, String> invertedMultimap = Multimaps.invertFrom(allTagNamesMultiMap, LinkedListMultimap.<String, String>create());
            List<String> tagIdList = new ArrayList<String>(tagNamesMultiMap.keySet());

            /***
             * Below code is for getting col A and B for each columns Unit conversion
             */
            // Read sheet TagNameAliases to get the Unit conversion part A and B column
            tagsDetailsMultimap = createTagDetailsMap(fileProcessingInputs.getTagsMappingFileName(), fileProcessingInputs.getTagMappingSheetName());

            for (int i = 0; i < allRecords.size(); i++) {
                String[] dataRecord;
                String[] raw = allRecords.get(i);

                if (i == 0) { // This if is for the header preparation
                    String[] headerList = new String[raw.length];
                    for (int headerIndex = 0; headerIndex < raw.length; headerIndex++) {
                        headerList[headerIndex] = raw[headerIndex];
                    }
                    // Creating header with use of diagnostic tags
                    // This filterHeaderList method will take care for the :
                    // check if current file header is in the diagnostic list if not just skip those columns
                    fileProcessingInputs.setHeaderList(headerList);
                    //input file header list filtered by Manara diagnostic tags
                    updatedHeader = makeHeaderAndUpdateMappingCSV(fileProcessingInputs);

                    // Checking which all parameter from raw file, is in Output schema tagName file or not
                    for (int headerIndex = 0; headerIndex < updatedHeader.size(); headerIndex++) {
                        if (headerIndex == 0) {
                            // Just to timstamp column mapping part
                            if (String.valueOf(updatedHeader.get(headerIndex)).contains("Timestamp")) {
                                outFileColumnMapping.put(headerIndex, String.valueOf(headerIndex));
                            }
                        } else {
                            if (allTagNamesMultiMap.containsValue(String.valueOf(updatedHeader.get(headerIndex)))) {
                                // get the  tag id from inverted map
                                Integer keyOfStaticSchema = Integer.parseInt(invertedMultimap.get(
                                        String.valueOf(updatedHeader.get(headerIndex))).toString().replaceAll("\\[|\\]", ""));
                                int pos = tagIdList.indexOf(String.valueOf(keyOfStaticSchema));
                                //find on tag id and tag name from tagsDetailsMultimap
                                if (tagsDetailsMultimap.containsKey(updatedHeader.get(headerIndex))) {
                                    Collection<TagsDetails> tagDetails = tagsDetailsMultimap.get(updatedHeader.get(headerIndex));
                                    UnitConvertDetailsMap.put(headerIndex, tagDetails.iterator().next());
                                }
                                outFileColumnMapping.put(headerIndex, String.valueOf(pos));
                            }
                        }
                    }
                    dataRecord = tagNamesMultiMap.values().toArray(new String[tagNamesMultiMap.size()]);
                } else { // this else is for all rows except header part
                    // Get static tagNameID not the indexID
                    Set<String> allTagIDFromStaticSchema = tagNamesMultiMap.keySet();
                    // Initializing the list with empty string, which will act as in tuple
                    String[] colValues = new String[tagNamesMultiMap.size()];
                    // This logic will take care for the assignment of raw parameters to final final level
                    int pos;
                    for (pos = 0; pos < (allTagIDFromStaticSchema.size() - 2); pos++) {
                        String tagIDVal = outFileColumnMapping.get(pos).trim();
                        String tspVal;
                        if (!tagIDVal.equals("-1")) {
                            if (pos == 0 && fileProcessingInputs.isTimestampConversionFlag()) {
                                // below code is for timestamp unit conversion only
                                String convertedTSVal = null;
                                tspVal = raw[pos];
                                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                                try {
                                    String[] hourMinutes = timestampUnitConvVal.split(":");

                                    Calendar date = GregorianCalendar.getInstance();
                                    date.setTime(sdf.parse(tspVal));
                                    Integer hourVal = Integer.parseInt(hourMinutes[0]);
                                    Integer minutesVal = Integer.parseInt(hourMinutes[1]);

                                    int checkSign = Integer.signum(hourVal);
                                    // Adding Hours and Minutes in Timestamp and making data at GMT/UTC level
                                    if (checkSign == 0) {
//                                        System.out.println("Val is zero ");
                                        date.add(Calendar.HOUR_OF_DAY, 0);
                                        date.add(Calendar.MINUTE, 0);
                                    } else if (checkSign == 1) {
//                                        System.out.println("Val is positive ");
//                                        System.out.println("date.getTime() :" + date.getTime());
                                        date.add(Calendar.HOUR_OF_DAY, -1 * hourVal);
//                                        System.out.println("date.getTime() :" + date.getTime());
                                        date.add(Calendar.MINUTE, -1 * minutesVal);
//                                        System.out.println("date.getTime() :" + date.getTime());
                                    } else if (checkSign == -1) {
                                        System.out.println("Val is negative ");
                                        date.add(Calendar.HOUR_OF_DAY, -1 * hourVal);
                                        date.add(Calendar.MINUTE, -1 * minutesVal);
                                    }
                                    convertedTSVal = sdf.format(date.getTime());
                                } catch (ParseException e) {
                                    e.printStackTrace();
                                    logger.error("TimeStamp conversion code is failed Due to : " + e);
                                }
                                colValues[Integer.parseInt(tagIDVal)] = convertedTSVal;
                            } else {
                                tspVal = raw[pos];
                                TagsDetails objTagsDetails = UnitConvertDetailsMap.get(pos);
                                if (tspVal != null && !tspVal.equals("") && tspVal.matches("[0-9]+")) {
                                    Integer finalConvertedVal = (Integer.parseInt(tspVal) * Integer.parseInt(objTagsDetails.getTagUnitConversionA())) + Integer.parseInt(objTagsDetails.getTagUnitConversionB());
                                    colValues[Integer.parseInt(tagIDVal)] = String.valueOf(finalConvertedVal);
                                } else {
                                    colValues[Integer.parseInt(tagIDVal)] = tspVal;
                                }
                            }
                        }
                    }
                    colValues[pos] = wellMappingId;
                    colValues[pos + 1] = stationMappingId;
                    dataRecord = colValues;
                }
                csvWriter.writeNext(dataRecord);
                csvWriter.flush();
            }
            logger.info("successfully : createProcessedFile, CSV file was created !!!");
        } catch (Exception e) {
            logger.info("Error : createProcessedFile in csvWriter !!!");
            e.printStackTrace();
        }
    }

    public void createProcessedFileIWIC(FileProcessingInputs fileProcessingInputs) throws WellTimestampConversionUnitException {
        logger.info("Start executing createProcessedFileIWIC method !!!");

        Map<Integer, String> updatedHeader = null;
        LinkedHashMap<String, String> tagNamesMultiMap = null;
        Multimap<String, String> allTagNamesMultiMap = null;
        List<String[]> allRecords = fileProcessingInputs.getAllRecords();
        String timestampUnitConvVal = null;
        String timestampColumnNameFinalValue = null;
        String strWellName = fileProcessingInputs.getCurrWellName();
//        String currStationName = fileProcessingInputs.getCurrStationName();

        /***
         * Read second column of each file we read, getting iWICIdentifier which is common throughout the file, so read only second column
         * At the end writing into final iWICIdentifier mapping csv for future reference
         */
        String iWICIdentifierColumnName = allRecords.get(0)[1];
        String[] iWICIdentifierColumnNameList = iWICIdentifierColumnName.split("_");
        String iWICIdentifierName = iWICIdentifierColumnNameList[0];
        // second parameter is for well_id, which we are not passing right now, its just empty
        iWICIdentifier.addToMap(iWICIdentifierName, "");
        // iWICIMappingId is not in use currently, but if in future we needed iWICIMappingId from mapping file we can use iWICIMappingId.
        // current we are just storing iWICIdentifier into mapping csv
        String iWICIMappingId = String.valueOf(iWICIdentifier.get(iWICIdentifierName).getIdCol());
        // Writing the final iWICIdentifier Name and Id mapping into iWICIdentifier mapping csv file, for each file read
        try {
            writeIntoMappingCSV(fileProcessingInputs.getiWICIdentifierMappingFromFile(), iWICIdentifier, fileProcessingInputs.getIWICIDENTIFIER_HEADER_MAPPING());
        } catch (Exception e) {
            e.printStackTrace();
        }

        /***
         * preparing the TIMESTAMP Column name, if conversion unit is there or not
         */
        String timeStampColumnName = allRecords.get(0)[0];

        if (timeStampColumnName.matches(".*\\d+.*")) { // this is, when timestamp col is having Conversion Unit attached to it like +11:30
            fileProcessingInputs.setTimestampConversionFlag(true);
            if (timeStampColumnName.split("\\(")[0].toLowerCase().contains("Timestamp".toLowerCase())) {
                timestampColumnNameFinalValue = "TS";
            }
            // It's for timestamp conversion unit only
            Matcher matcher = Pattern.compile("\\((.*?)\\)").matcher(timeStampColumnName);
            if (matcher.find()) {
                timestampUnitConvVal = matcher.group(1);
                fileProcessingInputs.setTimestampUnitConvVal(timestampUnitConvVal);
            }
            // override the value if not match
            if (wellName.containsKey(strWellName)) {
                WellAndStationMapping objWellAndStationMapping = wellName.get(strWellName);
                // If current timestampUnitConvVal is not matching with existing wellName entry, then overriding that
                if (!objWellAndStationMapping.getWellOrTimeStamp().equals(timestampUnitConvVal)) {
                    objWellAndStationMapping.setWellOrTimeStamp(timestampUnitConvVal);
                    // ASK AS will that put will replace the new override value
                    wellName.put(strWellName, objWellAndStationMapping);
                }
            } else {
                wellName.addToMap(strWellName, timestampUnitConvVal);
            }
        } else { // this else is, when TimestampUnitConversion not found in timestamp col
            fileProcessingInputs.setTimestampConversionFlag(false);
            if (timeStampColumnName.toLowerCase().contains("Timestamp".toLowerCase())) {
                timestampColumnNameFinalValue = "TS";
                // Now checking if the CSV WELL mapping file has that TimestampUnitConversion for current WELL
                if (wellName.containsKey(strWellName)) {
                    WellAndStationMapping objWellAndStationMapping = wellName.get(strWellName);
                    try {
                        //  If TimestampUnitConversion is empty of null
                        if (objWellAndStationMapping.getWellOrTimeStamp() != null && objWellAndStationMapping.getWellOrTimeStamp().length() != 0) {
                            // Get the value of TimestampUnitConversion from well mapping file
                            timestampUnitConvVal = objWellAndStationMapping.getWellOrTimeStamp();
                            //read from csv file and set into obj : fileProcessingInputs.setTimestampUnitConvVal
                            fileProcessingInputs.setTimestampUnitConvVal(timestampUnitConvVal);
                        } else {
                            // throw error if we did not get the TimestampUnitConversion unit from WELL mapping file too
                            throw new WellTimestampConversionUnitException("TimestampUnitConversion Unit did NOT FOUND in Well Mapping CSV file!!", objWellAndStationMapping.toString());
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        logger.error("WellTimestampConversionUnitException : TimestampUnitConversion Unit NOT found in Mapping Well CSV file !!", e);
                        throw new WellTimestampConversionUnitException("TimestampUnitConversion Unit NOT FOUND in Well Mapping CSV file !!", objWellAndStationMapping.toString(), e);
                    }
                } else {
                    /***
                     * Although don't have Timestamp conversion unit but adding that current wellName and wellID to well mapping file, so for her just to update Conversion Unit for that Well would be really easy.
                     * Below list of code is just meant for that
                     */
                    timestampUnitConvVal = "";
                    fileProcessingInputs.setTimestampUnitConvVal(timestampUnitConvVal);
                    wellName.addToMap(strWellName, fileProcessingInputs.getTimestampUnitConvVal());
                    try {
                        writeIntoMappingCSV(fileProcessingInputs.getWellMappingFileName(), wellName, fileProcessingInputs.getWELL_HEADER_MAPPING());
                    } catch (Exception e) {
                        e.printStackTrace();
                        logger.error("Writing to well mapping csv is filed : " + e);
                    }
                    logger.info("Although we don't have Timestamp conversion unit, but adding Current WellName and WellID to well mapping file, Only need to update latest Conversion Unit for Current Well : " + fileProcessingInputs.getCurrWellName());
                    throw new WellTimestampConversionUnitException("TimestampUnitConversion Unit NOT FOUND in either Raw file as well as Well Mapping CSV file!!", timestampUnitConvVal);
                }
            } else {
                // In case if timestamp col does not contains timestamp is value, then just copy as is to first column
                timestampColumnNameFinalValue = timeStampColumnName;
            }
        }

        /***
         * Below code is for the updating well and station name and ID in the Mapping CSV files for each Manara/IWIC file read
         */
        wellName.addToMap(fileProcessingInputs.getCurrWellName(), fileProcessingInputs.getTimestampUnitConvVal());
        String wellMappingId = String.valueOf(wellName.get(fileProcessingInputs.getCurrWellName()).getIdCol());
        /***
         * Writing the final well mapping files into csv for each run
         */
        try {
            writeIntoMappingCSV(fileProcessingInputs.getWellMappingFileName(), wellName, fileProcessingInputs.getWELL_HEADER_MAPPING());
        } catch (Exception e) {
            e.printStackTrace();
        }

        /***
         * Making finalOPFileName ready by changing the final file name with mapping CSV to hide all wellName and station name from file name
         */

        String updatedWellMappingOPFileName = fileProcessingInputs.getCurrProcessingFileName().replaceAll(fileProcessingInputs.getCurrWellName(), String.valueOf(wellName.get(fileProcessingInputs.getCurrWellName()).getIdCol()));
        String finalOPFileName = fileProcessingInputs.getPreProcessedOpDir() + "/" + updatedWellMappingOPFileName;
        fileProcessingInputs.setFileOut(finalOPFileName);


        Map<Integer, String> outFileColumnMapping;
        Multimap<String, TagsDetails> tagsDetailsMultimap = ArrayListMultimap.create();
        // its for Unit conversion of each cell values using col A and col B value
        Map<Integer, TagsDetails> UnitConvertDetailsMap = new HashMap<>();

        try {
            Writer writer = Files.newBufferedWriter(Paths.get(fileProcessingInputs.getFileOut()));
            CSVWriter csvWriter = new CSVWriter(writer, CSVWriter.DEFAULT_SEPARATOR, CSVWriter.NO_QUOTE_CHARACTER,
                    CSVWriter.DEFAULT_ESCAPE_CHARACTER, CSVWriter.DEFAULT_LINE_END);
            /***
             * Output Schema preparation, adding TimeStamp to schema at position one
             * In both the cases reading Manara SRS007, WWApp_Tags_to_select TAB
             */
            tagNamesMultiMap = readELSXFile(fileProcessingInputs.getTagsMappingFileName(),
                    fileProcessingInputs.getTagsNameIWICSheetName(), timestampColumnNameFinalValue);
            allTagNamesMultiMap = readELSXFile(fileProcessingInputs.getTagsMappingFileName(),
                    fileProcessingInputs.getTagsNameIWICSheetName(), timestampColumnNameFinalValue, true);


            outFileColumnMapping = new LinkedHashMap<Integer, String>((int) tagNamesMultiMap.size());
            // Initializing the outFileColumnMapping values with -1
            for (int outputColId = 0; outputColId < tagNamesMultiMap.size(); outputColId++) {
                outFileColumnMapping.put(outputColId, "-1");
            }
            // Adding all the rest of the tagsNames/parameters from TagName_Schema/IWIC_TagNames_Schema
            for (Object key : tagNamesMultiMap.keySet()) {
                if (fileProcessingInputs.getSuperSetOfAllTagsMultiMapIWIC().containsKey(key)) {
                    allTagNamesMultiMap.putAll((String) key, fileProcessingInputs.getSuperSetOfAllTagsMultiMapIWIC().get((String) key));
                }
            }

            Multimap<String, String> invertedMultimap = Multimaps.invertFrom(allTagNamesMultiMap, LinkedListMultimap.<String, String>create());
            // Get the sequence of output schema columns
            List<String> tagIdList = new ArrayList<String>(tagNamesMultiMap.keySet());

            /***
             * Below code is for getting col A and B for each columns Unit conversion
             */
            // Read sheet IWIC_TagNameAliases to get the Unit conversion part A and B column
            tagsDetailsMultimap = createTagDetailsMap(fileProcessingInputs.getTagsMappingFileName(), fileProcessingInputs.getTagMappingIWICSheetName());
            // To know the max of sequence, of columns appeared in raw file for write into output schema sequence
            Integer maxKey = null;
            for (int rowNum = 0; rowNum < allRecords.size(); rowNum++) {
                String[] dataRecord;
                String[] raw = allRecords.get(rowNum);


                if (rowNum == 0) { // This if is for the header preparation
                    String[] headerList = new String[raw.length];
                    for (int headerIndex = 0; headerIndex < raw.length; headerIndex++) {
                        headerList[headerIndex] = raw[headerIndex];
                    }
                    // Creating header with use of diagnostic tags
                    // This filterHeaderList method will take care for the :
                    // check if current file header is in the diagnostic list if not just skip those columns
                    fileProcessingInputs.setHeaderList(headerList);
                    //input file header list filtered by Manara diagnostic tags
                    updatedHeader = makeHeaderAndUpdateMappingCSVIWIC(fileProcessingInputs);
                    // Checking which all parameter from raw file, is in Output schema tagName file or not
                    // Getting max of updated header sequence
                    maxKey = Collections.max(updatedHeader.keySet());
                    for (int headerIndex = 0; headerIndex <= maxKey; headerIndex++) {
                        if (headerIndex == 0) {
                            // Just to timstamp column mapping part
                            if (String.valueOf(updatedHeader.get(headerIndex)).contains("Timestamp")) {
                                outFileColumnMapping.put(headerIndex, String.valueOf(headerIndex));
                            }
                        } else {
                            String currHeaderCol = String.valueOf(updatedHeader.get(headerIndex));
                            if (allTagNamesMultiMap.containsValue(currHeaderCol)) {
                                // get the  tag id from inverted map
                                Integer keyOfStaticSchema = Integer.parseInt(invertedMultimap.get(
                                        currHeaderCol).toString().replaceAll("\\[|\\]", ""));
                                int pos = tagIdList.indexOf(String.valueOf(keyOfStaticSchema));
                                //find on tag id and tag name from tagsDetailsMultimap
                                if (tagsDetailsMultimap.containsKey(currHeaderCol)) {
                                    Collection<TagsDetails> tagDetails = tagsDetailsMultimap.get(currHeaderCol);
                                    UnitConvertDetailsMap.put(headerIndex, tagDetails.iterator().next());
                                }
                                outFileColumnMapping.put(headerIndex, String.valueOf(pos));
                            }
                        }
                    }

                    dataRecord = tagNamesMultiMap.values().toArray(new String[tagNamesMultiMap.size()]);
                } else { // this else is for all rows except header part
                    // Get static tagNameID not the indexID
                    Set<String> allTagIDFromStaticSchema = tagNamesMultiMap.keySet();
                    // Initializing the list with empty string, which will act as in tuple
//                    String[] colValues = new String[tagNamesMultiMap.size()];
                    String[] colValues = new String[maxKey];
//                    Map<Integer, String> colValues = new LinkedHashMap<>(maxKey);
                    // This logic will take care for the assignment of raw parameters to final final level
                    int columnPos;
//                    for (columnPos = 0; columnPos < allTagIDFromStaticSchema.size(); columnPos++) {
                    for (columnPos = 0; columnPos <= maxKey; columnPos++) {
                        if (columnPos > 25) {
//                            System.out.println("Rukh bhai and check");
                        }
                        String tagIDVal = outFileColumnMapping.get(columnPos).trim();
                        String tspVal;
                        if (!tagIDVal.equals("-1")) {
                            if (columnPos == 0 && fileProcessingInputs.isTimestampConversionFlag()) {
                                // below code is for timestamp unit conversion only
                                String convertedTSVal = null;
                                tspVal = raw[columnPos];
                                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                                try {
                                    String[] hourMinutes = timestampUnitConvVal.split(":");

                                    Calendar date = GregorianCalendar.getInstance();
                                    date.setTime(sdf.parse(tspVal));
                                    Integer hourVal = Integer.parseInt(hourMinutes[0]);
                                    Integer minutesVal = Integer.parseInt(hourMinutes[1]);

                                    int checkSign = Integer.signum(hourVal);
                                    // Adding Hours and Minutes in Timestamp and making data at GMT/UTC level
                                    if (checkSign == 0) {
//                                        System.out.println("Val is zero ");
                                        date.add(Calendar.HOUR_OF_DAY, 0);
                                        date.add(Calendar.MINUTE, 0);
                                    } else if (checkSign == 1) {
//                                        System.out.println("Val is positive ");
//                                        System.out.println("date.getTime() :" + date.getTime());
                                        date.add(Calendar.HOUR_OF_DAY, -1 * hourVal);
//                                        System.out.println("date.getTime() :" + date.getTime());
                                        date.add(Calendar.MINUTE, -1 * minutesVal);
//                                        System.out.println("date.getTime() :" + date.getTime());
                                    } else if (checkSign == -1) {
                                        System.out.println("Val is negative ");
                                        date.add(Calendar.HOUR_OF_DAY, -1 * hourVal);
                                        date.add(Calendar.MINUTE, -1 * minutesVal);
                                    }
                                    convertedTSVal = sdf.format(date.getTime());
                                } catch (ParseException e) {
                                    e.printStackTrace();
                                    logger.error("TimeStamp conversion code is failed Due to : " + e);
                                }
                                colValues[Integer.parseInt(tagIDVal)] = convertedTSVal;
//                                colValues.put(Integer.parseInt(tagIDVal), convertedTSVal);
                            } else {
                                tspVal = raw[columnPos];
                                TagsDetails objTagsDetails = UnitConvertDetailsMap.get(columnPos);
                                if (tspVal != null && !tspVal.equals("") && tspVal.matches("[1-9]\\d*(\\.\\d+)?")) {
                                    Double finalConvertedVal = (Double.parseDouble(tspVal) * Double.parseDouble(objTagsDetails.getTagUnitConversionA())) + Double.parseDouble(objTagsDetails.getTagUnitConversionB());
//                                    colValues.put(Integer.parseInt(tagIDVal),String.valueOf(finalConvertedVal));
                                    colValues[Integer.parseInt(tagIDVal)] = String.valueOf(finalConvertedVal);
                                } else {
//                                    colValues.put(Integer.parseInt(tagIDVal), tspVal);
                                    colValues[Integer.parseInt(tagIDVal)] = tspVal;
                                }
                            }
                        }
//                        System.out.println("columnPos : "+columnPos);
                    }
                    if (columnPos == 28)
                        System.out.println("columnPos : " + columnPos + " : " + Arrays.asList(colValues) + " : " + colValues.length);
                    else
                        System.out.println("columnPos not 28 : " + columnPos + " : " + Arrays.asList(colValues) + " : " + colValues.length);
                    colValues[columnPos - 3] = wellMappingId;
//                    colValues.put(columnPos,wellMappingId);
                    // Fill stationId only for Manara file
//                    colValues[columnPos + 1] = iWICIMappingId;
//                    dataRecord = colValues.values().toArray(new String[maxKey]);
                    dataRecord = colValues;
                }
                csvWriter.writeNext(dataRecord);
                csvWriter.flush();
            }
            logger.info("successfully : createProcessedFile, CSV file was created !!!");
        } catch (Exception e) {
            logger.info("Error : createProcessedFile in csvWriter !!!");
            e.printStackTrace();
        }
    }

    public Map<Integer, String> makeHeaderAndUpdateMappingCSV(FileProcessingInputs fileProcessingInputs) throws Exception {
        logger.info("Start executing makeHeaderAndUpdateMappingCSV method !!!");
        logger.info("wellSet : " + wellName.toString() + " | Station : " + stationIdentifier.toString());
        int index = 0;
        Map newHeaderWithReplacedVal = new HashMap<Integer, String>();

        for (String columnName : fileProcessingInputs.getHeaderList()) {
            if (columnName.contains("Timestamp")) {
                //To include Timestamp columnName in the header always
                newHeaderWithReplacedVal.put(index, columnName);
            } else {
                int indexOfFirstSpace = columnName.indexOf("_");
//                Get parameter/tag from columnName
                String tagOrParameterName = columnName.substring(indexOfFirstSpace + 1);
//                logger.info("Current tagOrParameterName :" + tagOrParameterName);
                if (fileProcessingInputs.getFileTypeFlag().equals("Manara")) {
                    if (fileProcessingInputs.getDiagnosticFilterTags().contains(tagOrParameterName)) {
                        newHeaderWithReplacedVal.put(index, tagOrParameterName);
                    } else {
                        logger.info("Not a Diagnostic parameter : " + tagOrParameterName);
                    }
                } else if (fileProcessingInputs.getFileTypeFlag().equals("IWIC")) {
                    if (fileProcessingInputs.getDiagnosticIWICFilterTags().contains(tagOrParameterName)) {
                        newHeaderWithReplacedVal.put(index, tagOrParameterName);
                    } else {
                        logger.info("Not a Diagnostic parameter : " + tagOrParameterName);
                    }
                } else {
                    System.out.println("FileTypeFlag Not match : " + fileProcessingInputs.getFileTypeFlag());
                }
            }
            index += 1;
        }

        return newHeaderWithReplacedVal;
    }

    public Map<Integer, String> makeHeaderAndUpdateMappingCSVIWIC(FileProcessingInputs fileProcessingInputs) throws Exception {
        logger.info("Start executing makeHeaderAndUpdateMappingCSV method !!!");
        logger.info("wellSet : " + wellName.toString() + " | Station : " + stationIdentifier.toString());
        int index = 0;
        Map newHeaderWithReplacedVal = new HashMap<Integer, String>();

        for (String columnName : fileProcessingInputs.getHeaderList()) {
            if (columnName.contains("Timestamp")) {
                //To include Timestamp columnName in the header always
                newHeaderWithReplacedVal.put(index, columnName);
            } else {
                int indexOfFirstSpace = columnName.indexOf("_");
//                Get parameter/tag from columnName
                String tagOrParameterName = columnName.substring(indexOfFirstSpace + 1);
//                logger.info("Current tagOrParameterName :" + tagOrParameterName);
                if (fileProcessingInputs.getFileTypeFlag().equals("Manara")) {
                    if (fileProcessingInputs.getDiagnosticFilterTags().contains(tagOrParameterName)) {
                        newHeaderWithReplacedVal.put(index, tagOrParameterName);
                    } else {
                        logger.info("Not a Diagnostic parameter : " + tagOrParameterName);
                    }
                } else if (fileProcessingInputs.getFileTypeFlag().equals("IWIC")) {
                    if (fileProcessingInputs.getDiagnosticIWICFilterTags().contains(tagOrParameterName)) {
                        newHeaderWithReplacedVal.put(index, tagOrParameterName);
                    } else {
                        logger.info("Not a Diagnostic parameter : " + tagOrParameterName);
                    }
                } else {
                    System.out.println("FileTypeFlag Not match : " + fileProcessingInputs.getFileTypeFlag());
                }
            }
            index += 1;
        }

        return newHeaderWithReplacedVal;
    }
    /*
     * Purpose : Getting updated header and saving the final version of file into disk
     */

    public void writeIntoMappingCSV(String fileName, MappingClass newRecords, String[] header) throws Exception {
        logger.info("Start executing writeIntoMappingCSV method !!!");

        FileWriter fileWriter = null;
        CSVPrinter csvFilePrinter = null;
        List mappingFileRecords = new ArrayList();

        CSVFormat csvFileFormat = CSVFormat.DEFAULT.withHeader(header);
        try {
            fileWriter = new FileWriter(fileName);
            csvFilePrinter = new CSVPrinter(fileWriter, csvFileFormat);
            for (WellAndStationMapping mappingObj : newRecords.values()) {
                mappingFileRecords.add(mappingObj.getNameCol());
                mappingFileRecords.add(mappingObj.getIdCol());
                mappingFileRecords.add(mappingObj.getWellOrTimeStamp());
                csvFilePrinter.printRecord(mappingFileRecords);
                mappingFileRecords.clear();
            }
            logger.info("Successfully : writeIntoMappingCSV CSV file was created successfully !!!");
        } finally {
            try {
                fileWriter.flush();
                fileWriter.close();
                csvFilePrinter.close();
            } finally {
                logger.warn("End executing writeIntoMappingCSV method !!!");
            }
        }
    }
    /*
     * Its for Header preparation
     * args 1 - header list
     * args 2 - all the diagnostic tags for matching
     * return - diagnostic tags only in new header in the form of map of Integer,String
     */

    /*
     * Below code will call the Manara measurements file and will join this with TagsAndAlias file
     * to get the all possible tags names from TagsAlias file         *
     */
    // This getDiagnosticFilterTags is for Manara files only
//    public List<String> getDiagnosticFilterTags(String manaraMeasurementFile, String manaraTagSheetName, String tagsMappingFile, String tagMappingSheetName, String currFileType) throws Exception {
    public List<String> getDiagnosticFilterTagsManara(FileProcessingInputs fileProcessingInputs) throws Exception {

        List<String> diagnosticFilterTags;
        // Reading Manara SRS007 file to get all the critical tagID and parameter Names
        Multimap<String, String> manaraMeasurementMultiMap = diagnosticFilterELSXRead(fileProcessingInputs.getManaraMeasurementFile(), fileProcessingInputs.getManaraTagSheetName(), fileProcessingInputs.getmFileType());
        // Reading TagNameAliases/IWIC_TagNameAliases tab file to get all the possible tagIDs and parameters name, which would do the governing part which parameter need to pass from raw file or not
        Multimap<String, String> tagsMultiMap = diagnosticFilterELSXRead(fileProcessingInputs.getTagsMappingFileName(), fileProcessingInputs.getTagMappingSheetName(), fileProcessingInputs.getmFileType());
        // Merger of all Manara critical tagsID with tagsNameAlias to get the multiple parameter names for same TagIDs
        Multimap<String, String> allTagsMultiMap = getOneTagAliasMapping(manaraMeasurementMultiMap, tagsMultiMap);
        // Setting allTagsMultiMap into setter
        fileProcessingInputs.setSuperSetOfAllTagsMultiMapManara(allTagsMultiMap);
        // For all the Critical and Diagnostic TagIDs getting all possible distinct parameters name as diagnosticFilterTags
        Set<String> set = new HashSet<>();
        set.addAll(allTagsMultiMap.values());
        //DiagnosticFilterTags is having all Tags which are coming from SRS007(superset) and TagNameAlias(only matching tagIDs) combination
        diagnosticFilterTags = new ArrayList(set);
        logger.info("All Tags merger into set for uniqueness is done!!! ");
        return diagnosticFilterTags;
    }

    // This getDiagnosticFilterTags is for IWIC files only
    public List<String> getDiagnosticFilterTagsIWIC(FileProcessingInputs fileProcessingInputs) throws Exception {
        List<String> diagnosticFilterTags;
        // Reading Manara SRS007 file to get all the critical tagID and parameter Names
        Multimap<String, String> manaraMeasurementMultiMap = diagnosticFilterELSXRead(fileProcessingInputs.getManaraMeasurementFile(), fileProcessingInputs.getManaraTagSheetName(), fileProcessingInputs.getiFileType());
        // Reading TagNameAliases/IWIC_TagNameAliases tab file to get all the possible tagIDs and parameters name, which would do the governing part which parameter need to pass from raw file or not
        Multimap<String, String> tagsMultiMap = diagnosticFilterELSXRead(fileProcessingInputs.getTagsMappingFileName(), fileProcessingInputs.getTagMappingIWICSheetName(), fileProcessingInputs.getiFileType());
        // Merger of all Manara critical tagsID with tagsNameAlias to get the multiple parameter names for same TagIDs
        Multimap<String, String> allTagsMultiMap = getOneTagAliasMapping(manaraMeasurementMultiMap, tagsMultiMap);
        // Setting allTagsMultiMap into setter
        fileProcessingInputs.setSuperSetOfAllTagsMultiMapIWIC(allTagsMultiMap);
        // For all the Critical and Diagnostic TagIDs getting all possible distinct parameters name as diagnosticFilterTags
        Set<String> set = new HashSet<>();
        set.addAll(allTagsMultiMap.values());
        //DiagnosticFilterTags is having all Tags which are coming from SRS007(superset) and TagNameAlias(only matching tagIDs) combination
        diagnosticFilterTags = new ArrayList(set);
        logger.info("All Tags merger into set for uniqueness is done!!! ");
        return diagnosticFilterTags;
    }

    /***
     *
     * @param ipPath
     * @return Map of String,List<File>, key is WELL name and LIST is files available in all directory
     * @Purpose Get the list of available files in all directory with well name as key
     */
    public Map<String, List<File>> getAllFilesList(String ipPath) throws Exception {
        logger.info("Start executing getAllFiles method !!!");
        File curDir = new File(ipPath);
        File[] RTACGeneratedDirList = curDir.listFiles();
        Map<String, List<File>> filesListPerWell = new HashMap<String, List<File>>();
        Map<String, List<File>> finalFilesListPerWell = new HashMap<String, List<File>>();
        List<File> fileList = null;
        String currWellName = "";
        // Iterate over the input directory and getting required files for pre-processing
        for (File rTACDateDir : RTACGeneratedDirList) {
            // Will going to filter out all the file like zip/z02/z01, will read only data in folders
            if (rTACDateDir.isDirectory()) {
                String rTACDateDirName = rTACDateDir.getName();
                logger.info(">Print curr RTAC folder : " + rTACDateDirName);
                File[] eventAndHistoricalDirList = rTACDateDir.listFiles();

                for (File eventAndHistoricalDir : eventAndHistoricalDirList) {
                    logger.info(">>Print eventAndHistoricalDirList : " + eventAndHistoricalDir);
                    String eventAndHistoricalDirName = eventAndHistoricalDir.getName();

                    if (eventAndHistoricalDirName.equals("Historical-data")) {
                        //logger.info(">>> With-in eventAndHistoricalDirList : " + eventAndHistoricalDir);
                        File[] allWellNameList = eventAndHistoricalDir.listFiles();


                        for (File allWellName : allWellNameList) { // having all well names in list

                            if (!allWellName.getName().startsWith("Well")) { // getting only right wells
                                logger.info(">>>Print allWellName : " + allWellName);
                                // Name of current well from iterator
                                currWellName = allWellName.getName();
                                logger.info(">>> @OP-14 well name :" + currWellName);
                                for (File dtDir : allWellName.listFiles()) {
                                    logger.info(">>>> Date dir under Op-14 well :" + dtDir);
                                    logger.info(">>>> Coming files are of date : " + dtDir.getName());
                                    fileList = new ArrayList<File>();
                                    fileList.addAll(Arrays.asList(dtDir.listFiles()));
                                }
                                if (filesListPerWell.containsKey(currWellName)) {
                                    List<File> fileIntermediateList = filesListPerWell.get(currWellName);
                                    fileIntermediateList.addAll(fileList);
                                    filesListPerWell.put(currWellName, fileIntermediateList);
                                } else {
                                    filesListPerWell.put(currWellName, fileList);
                                }
                            }
                        }
                    }
                }
            }
        }
        return filesListPerWell;
    }

    public int processInputFileList(FileProcessingInputs fileProcessingInputs) throws WellTimestampConversionUnitException {
        int outputCode = 0;
        // This is one time read for all entire file available for current file to pick
        Map<String, List<File>> inputFilesList = fileProcessingInputs.getInputFilesList();

        /***
         * Below set of code would take care for to iterating over each file one at a time
         */
        Set<String> wellTypeSet = inputFilesList.keySet();
        for (String strWellName : wellTypeSet) {
            // Setting Current Well Name
            fileProcessingInputs.setCurrWellName(strWellName);
            List<File> inputFiles = inputFilesList.get(strWellName);
            for (File inputFile : inputFiles) {
                String rawFileName = inputFile.getName();

                // Setting Current process file name
                fileProcessingInputs.setCurrProcessingFileName(rawFileName);
                //Raw input file absolute path with the file name include
                String sourceFileAbsoluteDir = inputFile.toString();
                String getArchivedRawFileAbsoluteDir = sourceFileAbsoluteDir.replaceAll("THM Data OP-14 ENL", "THM Data OP-14 ENL Archive Data");
                // Making output pre-process directory
                String processedOpDir = inputFile.toString().replaceAll("THM Data OP-14 ENL", "THM Data OP-14 ENL Pre-processed Data");

                // Check pre-process directory parent is created or not, if not then create it
                File preProcessedOpDir = new File(processedOpDir).getParentFile();
                // Setting processedOpDir into class obj for later use
                fileProcessingInputs.setPreProcessedOpDir(String.valueOf(preProcessedOpDir));
                // Creating dir for ouput if not exist
                if (!preProcessedOpDir.exists()) {
                    logger.info(preProcessedOpDir + " was not exist before !!");
                    preProcessedOpDir.mkdirs();
                    logger.info(preProcessedOpDir + " directory created !!");
                }


                // Started reading files now
                List<String[]> csvFileRecords = readCsvFile(inputFile.toString());
                fileProcessingInputs.setAllRecords(csvFileRecords);

                //Only for the Manara raw file processing
                if (rawFileName.contains("Manara")) {
                    logger.info("=> Manara raw files :" + inputFile);
                    // set flag for manara/IWIC
                    fileProcessingInputs.setFileTypeFlag("Manara");
                    // Setting Current Station name
                    String currStationName = rawFileName.split("-")[3];
                    fileProcessingInputs.setCurrStationName(currStationName);

                    // Setting Current Lateral name
                    String currLateralName = rawFileName.split("-")[2];
                    fileProcessingInputs.setCurrLateralName(currLateralName);


                    // OP14 manara raw input parsing starts here
                    createProcessedFileManara(fileProcessingInputs);


                } else {
                    if (rawFileName.contains("IWIC")) { // CHECK FOR MSU ALSO ???
                        logger.info("=> MSU/IWIC raw files :" + inputFile);
                        // set flag for manara/IWIC
                        fileProcessingInputs.setFileTypeFlag("IWIC");

                        // OP14 IWIC raw input parsing starts here
                        createProcessedFileIWIC(fileProcessingInputs);
                    }
                }
                /***
                 * Moving processing file into archival dir one by one as we process them
                 * Can we pass both ip and archive path including file name, will check the archive parent path in created or not before pushing/moving data
                 */
                moveFilesAfterPreprocessing(sourceFileAbsoluteDir, getArchivedRawFileAbsoluteDir, rawFileName);
            }
        }
        return outputCode;
    }

    public void createWellAndStationMapping(FileProcessingInputs fileProcessingInputs) {

        // Read well csv mapping file and get data into new List with only distinct well with mapping id
        List wallMapList = readCsvFile(fileProcessingInputs.getWellMappingFileName());
        createMappingList(wallMapList, wellName);
        // Read Lateral csv mapping file and get data into new List with only distinct lateral with mapping id
        List stationIdentifierList = readCsvFile(fileProcessingInputs.getStationIdentifierMappingFromFile());
        createMappingList(stationIdentifierList, stationIdentifier);
        // Read Iwic csv mapping file and get data into new List with only distinct Iwic with mapping id
        List iWICIdentifierList = readCsvFile(fileProcessingInputs.getiWICIdentifierMappingFromFile());
        createMappingList(iWICIdentifierList, iWICIdentifier);
    }
}


