import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.LinkedListMultimap;
import com.google.common.collect.Multimap;
import com.google.common.collect.Multimaps;
import com.monitorjbl.xlsx.StreamingReader;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVPrinter;
import org.apache.commons.csv.CSVRecord;
import org.apache.commons.lang.StringEscapeUtils;
import org.apache.log4j.Logger;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;

import java.io.*;
import java.util.*;


public class OP14RawFileReader {
    final static Logger logger = Logger.getLogger(OP14RawFileReader.class);

    private static final String[] WELL_HEADER_MAPPING = {"WellName", "Well ID"};
    private static final String[] STATIONIDENTIFIER_HEADER_MAPPING = {"StationIdentifier", "Station ID"};
    static String wellMapping = "C:\\Manara-raw-data\\mapping-inputs-files\\WellMapping.csv";
    static String lateralMapping = "C:\\Manara-raw-data\\mapping-inputs-files\\StationIdentifierMapping.csv";
    static MappingClass wellName = new MappingClass();

    /*
     * Move the processed file from current raw folder to Archived location
     */
    static MappingClass stationIdentifier = new MappingClass();
    static String tagsMappingFile = "C:\\Manara-raw-data\\mapping-inputs-files\\updated-one\\TagNamesAndAliases_2018_01_12 (1).xlsx";

    static String manaraMeasurementFile = "C:\\Manara-raw-data\\mapping-inputs-files\\updated-one\\Manara_SRS007_1_10_RTAC_public_measurements_definition.xlsx";
    /*
     * Starting point of code
     * Step 1 : Taken care for all the diagnostic parameters from different sources and merging into one
     * Step 2 : Read all the data for wells and laterals from the csv file so that actual values can be replaced
     * Step 3 : Reading the directory one by one and for parsing
     * Step 4 : Start the parsing process by calling readNWriteCsvFile method
     * Step 5 : moving files from original directory Archive directory after the successful completion of parsing
     */
    static Multimap<String, String> allTagsMultiMap;

    // TODO : unhandle exceptions for file read
    static Multimap<String, String> readELSXFile(String excelFileLoc, String sheetName) throws Exception {
        logger.info("Start executing readELSXFile method !!!");

        Multimap<String, String> returnMap = ArrayListMultimap.create();
        Multimap<String, String> tags_to_select = ArrayListMultimap.create();
        Multimap<String, String> manaraFileFixSchemaMultiMap = LinkedListMultimap.create();
        Multimap<String, String> tags_wd_alias_lst = ArrayListMultimap.create();
        InputStream is = new FileInputStream(new File(excelFileLoc));

        Workbook workbook = StreamingReader.builder()
                .rowCacheSize(100)    // number of rows to keep in memory (defaults to 10)
                .bufferSize(4096)     // buffer size to use when reading InputStream to file (defaults to 1024)
                .open(is);            // InputStream or File for XLSX file (required)

        // TODO: 12/11/2017 handle exception if sheet not present java.lang.NullPointerException
        Sheet sheet = workbook.getSheet(sheetName);
//        Printing process sheet name
        String sheetNm = sheet.getSheetName();
        logger.info("WorkBook/Sheet Name : " + sheetNm);
        if (sheetNm.equals("TagNameAliases")) {
            logger.info("Reading TagNameAliases sheet !!!!!!!");
            for (Row r : sheet) {
                List<String> row = new ArrayList();
                for (Cell c : r) {
                    String colVal = c.getStringCellValue();
//                    TODO: do we have to take care for the empty/null values coming from columns or we can use directly add command
                    if (colVal != null || colVal.length() > 0)
                        row.add(colVal);
                    else
                        row.add("");
                }
//                Removing the header from the excel read
                if (!row.get(0).equals("TagNameId") && !row.get(1).equals("TagNameAlias"))
                    tags_wd_alias_lst.put(row.get(0), row.get(1));
            }
            returnMap = tags_wd_alias_lst;
        }

        if (sheetNm.equals("WWApp_Tags_to_select")) {
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
                    if (row.get(0).equals("Manara") && row.get(28).equals("Diagnostic")) {
                        tags_to_select.put(row.get(30), row.get(4));
                    }
                }
            }
            returnMap = tags_to_select;
        }
        if (sheetNm.equals("TagNames")) {
            logger.info("Reading TagNames sheet !!!!!!!");
            for (Row r : sheet) {
                List<String> row = new ArrayList();
                for (Cell c : r) {
                    String colVal = c.getStringCellValue().replaceAll("\"", "");
                    if (colVal != null || colVal.length() > 0)
                        row.add(colVal);
                    else
                        row.add("");
                }
                if (!row.get(0).equals("TagNameId") && !row.get(1).equals("Name"))
                    manaraFileFixSchemaMultiMap.put(row.get(0), row.get(1));
                else {
//                    THIS HAS TO BE CHECKED
                    manaraFileFixSchemaMultiMap.put("0", "Timestamp(+11:00)");
                }
            }
            returnMap = manaraFileFixSchemaMultiMap;
        }
        return returnMap;
    }

    /*
     *  Method is for getting the muliple tags from tagsAlias file and mearge with Manara Measurment file
     */
    public static Multimap<String, String> getOneTagAliasMapping(Multimap<String, String> manaraMeasurementMultiMap, Multimap<String, String> tagsMultiMap) {
        logger.info("Start executing getOneTagAliasMapping method !!!");
        logger.info("Start executing merger of tags from Alias file ...");
        Multimap<String, String> allTagsMultiMap = ArrayListMultimap.create();

        for (Object key : manaraMeasurementMultiMap.keySet()) {
//            Below line is out of the contains check as Manara file parameters always has to be consider
            allTagsMultiMap.putAll((String) key, manaraMeasurementMultiMap.get((String) key));
            if (tagsMultiMap.containsKey(key)) {
                allTagsMultiMap.putAll((String) key, tagsMultiMap.get((String) key));
            }
        }
        return allTagsMultiMap;
    }

    public static void moveFilesAfterPreprocessing(String sourceFolder, String archiveFolder, String fileName) {
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
     * method to read the csv file
     * and return the file records as list
     * */
    public static List readCsvFile(String fileName) {
        logger.info("Start executing readCsvFile method !!!");
        FileReader fileReader = null;
        CSVParser csvFileParser = null;
        List csvRecords = null;
        try {
            //initialize FileReader object
            fileReader = new FileReader(fileName);
            //initialize CSVParser object
            csvFileParser = new CSVParser(fileReader, CSVFormat.EXCEL);
            //Get a list of CSV file records
            csvRecords = csvFileParser.getRecords();
        } catch (Exception e) {
            logger.error("Error in CsvFileReader !!!");
            e.printStackTrace();
            logger.error("Exception : ", e);
        } finally {
            try {
                fileReader.close();
                csvFileParser.close();
            } catch (IOException e) {
                logger.error("Error while closing fileReader/csvFileParser !!!");
                e.printStackTrace();
                logger.error("IOException : ", e);
            }
        }
        return csvRecords;
    }

    /*
     * Method is for the Well and Lateral csv file read
     * and to map both csv file reading into list
     * to Create the  new distinct mapping csv file for both
     * */
    public static void createMappingList(List<CSVRecord> allRecords, MappingClass mappingList) {
        logger.info("Start executing createMappingList method !!!");
        for (int i = 0; i < allRecords.size(); i++) {
            CSVRecord row = allRecords.get(i);
            if (i > 0) {
//                mappingList/wellName is reference of MappingClass
//                below put will going to add only new well/lateral into maps its and custom maps implementation in MappingClass
                mappingList.put(row.get(0), Integer.parseInt(row.get(1)));
            }
        }
    }

    /*
     *    Read each raw file one by one from getAllFiles Method
     */
    public static void readNWriteCsvFile(String ipFilePath, String opFilePath, List diagnosticHeader, String currWellName) {
        logger.info("Start executing readNWriteCsvFile method !!!");

        FileReader fileReader = null;
        CSVParser csvFileParser = null;
        List csvRecords = null;
        try {
            //initialize FileReader object
            fileReader = new FileReader(ipFilePath);
            //initialize CSVParser object
            csvFileParser = new CSVParser(fileReader, CSVFormat.EXCEL);
            //Get a list of CSV file records
            csvRecords = csvFileParser.getRecords();
        } catch (Exception e) {
            logger.error("Error in CsvFileReader !!!");
            e.printStackTrace();
            logger.error("Exception : ", e);
        } finally {
            try {
                fileReader.close();
                csvFileParser.close();
            } catch (IOException e) {
                logger.error("Error while closing fileReader/csvFileParser !!!");
                e.printStackTrace();
                logger.error("IOException : ", e);
            }
        }
//        Calling Parser
        createProcessedFile(opFilePath, csvRecords, diagnosticHeader, currWellName);
    }

    public static void createProcessedFile(String fileOut, List<CSVRecord> allRecords, List diagnosticHeader, String currWellName) {
        logger.info("Start executing createProcessedFile method !!!");

        FileWriter fileWriter = null;
        CSVPrinter csvFilePrinter = null;
        Map updatedHeader = null;
        Multimap<String, String> tagNamesMultiMap;
        Multimap<String, String> tagNamesMultiMap2;

        Map<Integer, String> columnMapping12;

        try {
            fileWriter = new FileWriter(fileOut);
            csvFilePrinter = new CSVPrinter(fileWriter, CSVFormat.EXCEL);
//            MAKE SURE TO FIX THIS
            tagNamesMultiMap = readELSXFile(tagsMappingFile, "TagNames");
            tagNamesMultiMap2 = readELSXFile(tagsMappingFile, "TagNames");

            columnMapping12 = new HashMap<Integer, String>((int) ((tagNamesMultiMap.size() / 0.75) + 1));
//            Initializing the columnMapping12 values with -1
            for (String key : tagNamesMultiMap.keySet()) columnMapping12.put(Integer.parseInt(key), "-1");
//            Adding all the rest of the tagsNames/parameters from static TagName excel tab file

            for (Object key : tagNamesMultiMap.keySet()) {
                if (allTagsMultiMap.containsKey(key))
                    tagNamesMultiMap2.putAll((String) key, allTagsMultiMap.get((String) key));
            }

            Multimap<String, String> invertedMultimap = Multimaps.invertFrom(tagNamesMultiMap2, LinkedListMultimap.<String, String>create());

            for (int i = 0; i < allRecords.size(); i++) {
                List<String> dataRecord = new ArrayList<String>(tagNamesMultiMap.size());

                CSVRecord raw = allRecords.get(i);
//                It is for the header preparation
                if (i == 0) {
                    String[] headerList = new String[raw.size()];
                    for (int headerIndex = 0; headerIndex < raw.size(); headerIndex++) {
                        headerList[headerIndex] = raw.get(headerIndex);
                    }
//                    Creating header with use of diagnostic tags
//                    This makeNewHeader method will take care for the : check if current file header is in the diagnostic list if not just skip those columns
                    updatedHeader = makeNewHeader(headerList, diagnosticHeader, currWellName);

//                    Checking which all parameter from raw file is in static schema tagName file
                    for (int tagRef = 0; tagRef < updatedHeader.size(); tagRef++) {
                        if (tagRef == 0) {
                            if (String.valueOf(updatedHeader.get(tagRef)).contains("Timestamp"))
                                columnMapping12.put(tagRef, String.valueOf(tagRef));
                        } else {
                            if (tagNamesMultiMap2.containsValue(String.valueOf(updatedHeader.get(tagRef)))) {
                                Integer keyOfStaticSchema = Integer.parseInt(invertedMultimap.get(String.valueOf(updatedHeader.get(tagRef))).toString().replaceAll("\\[|\\]", ""));
                                columnMapping12.put(tagRef, String.valueOf(keyOfStaticSchema));
                            }
                        }
                    }
                    dataRecord.addAll(tagNamesMultiMap.values());
                } else {
//                    Get static tagNameID not the indexID
                    Set<String> allTagIDFromStaticSchema = tagNamesMultiMap.keySet();
//                    Initializing the list with empty string, which will act as in tuple
                    List<String> colValues = new ArrayList<String>(Collections.nCopies(tagNamesMultiMap.size(), ""));

                    Iterator iterator = allTagIDFromStaticSchema.iterator();

//                    This logic will take care for the assignment of raw parameters to final final level
                    for (int pos = 0; pos < allTagIDFromStaticSchema.size(); pos++) {
                        String ind = (String) iterator.next();
                        String tagIDVal = columnMapping12.get(Integer.parseInt(ind)).trim();
                        int inx = Integer.parseInt(tagIDVal);
                        if (!columnMapping12.get(Integer.parseInt(ind)).equals("-1")) {
                            String val = raw.get(Integer.parseInt(ind));
                            colValues.set(inx, val);
//                            logger.info("Line number : " + i + " Index : " + ind + ", " + inx + " colValue " + colValues.get(inx));
                        }
                    }
                    dataRecord.addAll(colValues);
                }
                csvFilePrinter.printRecord(dataRecord);
                dataRecord.clear();
            }
            logger.info("successfully : createProcessedFile, CSV file was created !!!");
        } catch (Exception e) {
            logger.info("Error : createProcessedFile in CsvFileWriter !!!");
            e.printStackTrace();
        } finally {
            try {
                fileWriter.flush();
                fileWriter.close();
                csvFilePrinter.close();
            } catch (IOException e) {
                logger.info("Error : createProcessedFile, while flushing/closing fileWriter/csvPrinter !!!");
                e.printStackTrace();
            }
        }
    }

    /*
     * Purpose : Getting updated header and saving the final version of file into disk
     */

    public static Map<Integer, String> makeNewHeader(String[] headerList, List diagnosticHeader, String currWellName) throws Exception {
        logger.info("Start executing makeNewHeader method !!!");
        int count = 0;
        int index = 0;
        Map newHeaderWithReplacedVal = new HashMap<Integer, String>();
        for (String column : headerList) {
            if (column.contains("Timestamp")) {
//                To include Timestamp column in the header always
                newHeaderWithReplacedVal.put(index, column);
            } else {
                int indexOfFirstSpace = column.indexOf("_");

                int indexOfSecSpace = 1 + column.indexOf("_", 1);
                int indexStartOfUnitConvrs = column.indexOf("(");
                int indexEndOfUnitConvrs = column.indexOf(")");

                logger.info("indexStartOfUnitConvrs  : " + indexStartOfUnitConvrs);
                logger.info("indexEndOfUnitConvrs : " + indexEndOfUnitConvrs);
//                This Unit table need to be maintain into the another table | ASK MIHITHA FIRST
                logger.info("Unit : " + column.substring(indexStartOfUnitConvrs + 1, indexEndOfUnitConvrs));

//                Get Well and lateral from raw column name
                String stationName = column.substring(0, indexOfFirstSpace);
                logger.info("stationName :" + stationName);
                String parameterName = column.substring(indexOfFirstSpace + 1);
                logger.info("parameterName :" + parameterName);
                logger.info("StringEscapeUtils : " + StringEscapeUtils.escapeJava(parameterName));

                wellName.addToMap(currWellName);
                stationIdentifier.addToMap(stationName);
//                Removing header toUpperCase will see if any problem arises(if invert multimap case because of that we were getting same value key twice if they are duplicate)
                if (diagnosticHeader.contains(parameterName)) {
//                    We have to taken care for the well name and station name as this has to be pass into final file in some consumable format
//                    newHeaderWithReplacedVal.put(index, wellName.get(currWellName) + " " + stationIdentifier.get(stationName) + " " + parameterName);
                    newHeaderWithReplacedVal.put(index, parameterName);
                } else {
                    logger.info("parameter is not diagnostic one " + parameterName + " diagnosticHeader List was : " + diagnosticHeader.toString());
                }
            }
            index += 1;
        }
        logger.info("wellSet : " + wellName.toString() + " | Station : " + stationIdentifier.toString());

//        Writing the final well and Station mapping files into csv file for each run read
        writeIntoMappingCSV(wellMapping, wellName, WELL_HEADER_MAPPING);
        writeIntoMappingCSV(lateralMapping, stationIdentifier, STATIONIDENTIFIER_HEADER_MAPPING);
        return newHeaderWithReplacedVal;
    }
    /*
     * Its for Header preparation
     * args 1 - header list
     * args 2 - all the diagnostic tags for matching
     * return - diagnostic tags only in new header in the form of map of Integer,String
     */

    public static void writeIntoMappingCSV(String fileName, MappingClass newRecords, String[] header) throws Exception {
        logger.info("Start executing writeIntoMappingCSV method !!!");

        FileWriter fileWriter = null;
        CSVPrinter csvFilePrinter = null;
        List mappingFileRecords = new ArrayList();

        CSVFormat csvFileFormat = CSVFormat.DEFAULT.withHeader(header);
        try {
            fileWriter = new FileWriter(fileName);
            csvFilePrinter = new CSVPrinter(fileWriter, csvFileFormat);
            for (Map.Entry<String, Integer> entry : newRecords.entrySet()) {
                mappingFileRecords.clear();
                mappingFileRecords.add(entry.getKey());
                mappingFileRecords.add(entry.getValue());
                csvFilePrinter.printRecord(mappingFileRecords);
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

    public void getAllFiles(String ipPath, String opPath) throws Exception {
        logger.info("Start executing getAllFiles method !!!");
        List diagnosticFilterTags = new ArrayList();
        File curDir = new File(ipPath);
        File[] RTACGeneratedDirList = curDir.listFiles();

        /*
         * Below code will call the Manara measurements file and will join this with TagsAndAlias file
         * to get the all possible tags names from TagsAlias file         *
         */
        Multimap<String, String> manaraMeasurementMultiMap = readELSXFile(manaraMeasurementFile, "WWApp_Tags_to_select");
        Multimap<String, String> tagsMultiMap = readELSXFile(tagsMappingFile, "TagNameAliases");

        allTagsMultiMap = OP14RawFileReader.getOneTagAliasMapping(manaraMeasurementMultiMap, tagsMultiMap);
        Set<String> set = new HashSet<>();
        set.addAll(allTagsMultiMap.values());
//        DiagnosticFilterTags is having all Tags which are coming from SRS007(superset) and TagNameAlias(only matching tagIDs) combination
        diagnosticFilterTags = new ArrayList(set);
        logger.info("All Tags merger into set for uniqueness is done!!! ");

        /*
         * below code was using only formated csv file having tags ID and Desc, which was not sufficient
         *
//        Just the csv read and get data in List form
        List diagnosticTagsList = readCsvFile(diagnosticTagFile);
//        Preparing to get list of diagnostic tags only
//        getDiagnosticTagsList(diagnosticTagsList);
       logger.info("diagnosticFilterTags : " + diagnosticFilterTags);
        */

//        iterate over the input directory
        for (File f : RTACGeneratedDirList) {
            if (f.isDirectory()) {
                String dirName = f.getName();
                logger.info(">Print curr RTAC per date dir : " + dirName);
                File[] historicalDirList = f.listFiles();

                for (File histDir : historicalDirList) {
                    logger.info(">>Print historicalDirList : " + histDir);
                    String dirName1 = histDir.getName();

                    if (dirName1.equals("Historical-data")) {
//                           logger.info(">>> With-in historicalDirList : " + histDir);
                        for (File wellType : histDir.listFiles()) {
//                           logger.info(">>>Print wellType : " + wellType);
                            String wellTyp = wellType.getName();

                            if (!wellTyp.startsWith("Well")) {
//                               logger.info("Non OP-14 well name :"+wellTyp);
                                logger.info(">>>Print wellType : " + wellType);
                                String currWellName = wellTyp;
                                logger.info(">>> @OP-14 well name :" + wellTyp);
                                for (File dt : wellType.listFiles()) {
                                    logger.info(">>>> Date dir under Op-14 well :" + dt);
                                    logger.info(">>>> Coming files are of date : " + dt.getName());
                                    for (File allFiles : dt.listFiles()) {
                                        String rawFileName = allFiles.getName();
//                                        Only for the Manara raw file processing
                                        if (rawFileName.contains("Manara")) {

                                            logger.info("=> Manara raw files :" + allFiles);

//                                          Read well csv mapping file and get data into new List with only distinct well with mapping id
                                            List wallMapList = readCsvFile(wellMapping);
                                            createMappingList(wallMapList, wellName);
//                                          Read Lateral csv mapping file and get data into new List with only distinct lateral with mapping id
                                            List LateralLocList = readCsvFile(lateralMapping);
                                            createMappingList(LateralLocList, stationIdentifier);

                                            logger.info("|| " + allFiles.toString() + " ||");

                                            String precessedOpDir = allFiles.toString().replaceAll("THM Data OP-14 ENL", "THM Data OP-14 ENL Pre-processed Data");
                                            logger.info("| " + precessedOpDir + " |");
                                            File fileCheck = new File(precessedOpDir).getParentFile();
                                            if (!fileCheck.exists()) {
//                                               logger.info("get Parent : "+fileCheck.getParentFile());
                                                logger.info("Not exist !!");
                                                fileCheck.mkdirs();
                                            }
                                            readNWriteCsvFile(allFiles.toString(), precessedOpDir, diagnosticFilterTags, currWellName);

//                                          Absolute path with the file name is included
                                            String getRawFileAbsoluteDir = allFiles.toString();
                                            String getArchivedRawFileAbsoluteDir = getRawFileAbsoluteDir.replaceAll("THM Data OP-14 ENL", "THM Data OP-14 ENL Archive Data");

//                                          Can we pass both ip and archive path including file name, will check the archive parent path in created or not before pushing/moving data
                                            moveFilesAfterPreprocessing(getRawFileAbsoluteDir, getArchivedRawFileAbsoluteDir, rawFileName);

                                        } else {
//                                            this block is only for processing MSU/IWIC raw files | COMMENTED TO TEST ONLY MANARA FILE FIRST
//                                           logger.info("=> MSU/IWIC raw files :" + allFiles);
                                        }


                                    }
                                }
                            }


                        }


                    }
                }
                logger.info("_____________END______________");
            }
        }
    }
}

