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
    static String letteralMapping = "C:\\Manara-raw-data\\mapping-inputs-files\\StationIdentifierMapping.csv";
    static MappingClass wellName = new MappingClass();

    /*
     * Move the processed file from current raw folder to Archived location
     *
     */
    static MappingClass stationIdentifier = new MappingClass();
    static String tagsMappingFile = "C:\\Manara-raw-data\\mapping-inputs-files\\updated-one\\TagNamesAndAliases_2018_01_12 (1).xlsx";
    //    static String tagsMappingFile = "C:\\Manara-raw-data\\mapping-inputs-files\\TagNamesAndAliases_2018_01_09.xlsx";
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
    private static String acrhivePath = "C:\\Manara-raw-data\\THM Data OP-14 ENL Archive Data\\";

    // TODO : unhandle exceptions for file read
    static Multimap<String, String> readELSXFile(String excelFileLoc, String sheetName) throws Exception {
       logger.info("We are in readELSXFile method !!!");

        Multimap<String, String> returnMap = ArrayListMultimap.create();
        Multimap<String, String> tags_to_select = ArrayListMultimap.create();
//        To maintain the insert of static schema for final file preparation
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
//                removing the header peice from the excel read
                if (!row.get(0).equals("TagNameId") && !row.get(1).equals("TagNameAlias"))
                    tags_wd_alias_lst.put(row.get(0), row.get(1));
            }
//            Just for checking the data within the map
//            Again remove once testing is Done
            for (Object key : tags_wd_alias_lst.keySet()) {
//               logger.info("Key : " + key + " | Values " + tags_wd_alias_lst.get((String) key));
//               logger.info("Values " + tags_wd_alias_lst.get((String) key));
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
//               logger.info("Printing each row : " + row.toString());
                if (row.size() == 31) {
//                   logger.info("Having 31 columns in a row!!");
                    if (row.get(0).equals("Manara") && row.get(28).equals("Diagnostic")) {
//                       logger.info("tuple : " + row.get(0) + " | " + row.get(3) + " | " + row.get(4) + " | " + row.get(28) + " | " + row.get(30));
                        tags_to_select.put(row.get(30), row.get(4));
                    }
                }
            }
//        Just for checking the data within the map
//            This should be remove just for testing purpose
            for (Object key : tags_to_select.keySet()) {
//               logger.info("Key : " + key + " | Values " + tags_to_select.get((String) key));
//               logger.info("Values " + tags_to_select.get((String) key));
            }
            returnMap = tags_to_select;
        }
        if (sheetNm.equals("TagNames")) {
           logger.info("Reading TagNames sheet !!!!!!!");
            for (Row r : sheet) {
                List<String> row = new ArrayList();
                for (Cell c : r) {
                    String colVal = c.getStringCellValue().replaceAll("\"", "");
//                   logger.info("VALLLLL : "+ colVal.toString());
                    if (colVal != null || colVal.length() > 0)
                        row.add(colVal);
                    else
                        row.add("");
                }
                if (!row.get(0).equals("TagNameId") && !row.get(1).equals("Name"))
                    manaraFileFixSchemaMultiMap.put(row.get(0), row.get(1));
                else
                    manaraFileFixSchemaMultiMap.put("0", "Timestamp(+11:00)");
            }
//        Just for checking the data within the map
//            This should be remove just for testing purpose
            for (Object key : manaraFileFixSchemaMultiMap.keySet()) {
//               logger.info("manaraFileFixSchemaMultiMap : Key : " + key + " | Values " + manaraFileFixSchemaMultiMap.get((String) key));
            }
//           logger.info("xlsx read count : "+manaraFileFixSchemaMultiMap.size());
            returnMap = manaraFileFixSchemaMultiMap;
        }
        return returnMap;
    }

    /*
     *  Method is for getting the muliple tags from tagsAlias file and mearge with Manara Measurment file
     */
    public static Multimap<String, String> getOneTagAliasMapping(Multimap<String, String> manaraMeasurementMultiMap, Multimap<String, String> tagsMultiMap) {
       logger.info("We are in getOneTagAliasMapping method !!!");
       logger.info("We are in merger of tags from Alias file ...");
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
//    static String manaraMeasurementFile = "C:\\Manara-raw-data\\mapping-inputs-files\\Manara_SRS007_1_9_RTAC_public_measurements_definition.xlsx";

    public static void moveFilesAfterPreprocessing(String sourceFolder, String acrhiveFolder, String fileName) {
       logger.info("We are in moveFilesAfterPreprocessing method !!!");

       logger.info(" Moving file from source to Archive dir !!!!!!");
        File sourceDir = new File(sourceFolder);
        File archiveDir = new File(acrhiveFolder);
        if (!archiveDir.exists()) {
            archiveDir.mkdirs();
        }
//      Move fileName from argument, source to archive location
//        Condition : same file should not be present in acrhive location before you run this
//                    else the file would not be copied into
        boolean bol = sourceDir.renameTo(new File(archiveDir + "\\" + fileName));
        if (bol)
           logger.info("sourceDir :" + sourceDir + " $$$$ Successful copied into : " + archiveDir + " $$ Loc, file copied was : " + fileName);
        else
           logger.info("sourceDir :" + sourceDir + " #### Failed copied into : " + archiveDir + " ## Loc, file copied was : " + fileName);
    }

    public static void getAllFiles(String ipPath, String opPath) throws Exception {
       logger.info("We are in getAllFiles method !!!");
        List dianosticFilterTags = new ArrayList();

        File curDir = new File(ipPath);
        File[] RTACGeneratedDirList = curDir.listFiles();

        /* Below code will call the Manara measurements file and will join this with TagsAndAlias file
         * to get the all possible tags names from TagsAlias file         *
         */
        Multimap<String, String> manaraMeasurementMultiMap = readELSXFile(manaraMeasurementFile, "WWApp_Tags_to_select");
        Multimap<String, String> tagsMultiMap = readELSXFile(tagsMappingFile, "TagNameAliases");

        allTagsMultiMap = OP14RawFileReader.getOneTagAliasMapping(manaraMeasurementMultiMap, tagsMultiMap);
//       logger.info("allTagsMultiMap :"+ allTagsMultiMap.toString());
        Set<String> set = new HashSet<>();
        set.addAll(allTagsMultiMap.values());
//        dianosticFilterTags is having all Tags which are coming from SRS007(superset) and TagNameAlias(only matching tagIDs) combination
        dianosticFilterTags = new ArrayList(set);
//        Remove after the testing is done
//       logger.info("##################################Printing all values of tags from set :###################################");
       logger.info("All Tags merger into set for uniqueness is done!!! ");
        for (String r : set) {
//           logger.info(" list print :" + r);
        }
        for (String tagNameAliasesParam : tagsMultiMap.values()) {
//           logger.info("tagNameAliasesParam : " + tagNameAliasesParam);
        }
        for (String col : allTagsMultiMap.values()) {
//           logger.info("allTagsMultiMap : "+col.toString());
        }
        /*
         * below code was using only formated csv file having tags ID and Desc, which was not sufficient
         *
//        Just the csv read and get data in List form
        List diagnosticTagsList = readCsvFile(diagnosticTagFile);
//        Preparing to get list of diagnostic tags only
//        getDiagnosticTagsList(diagnosticTagsList);
       logger.info("dianosticFilterTags : " + dianosticFilterTags);
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
//                                        below if block, only for the Manara raw file processing
                                        if (rawFileName.contains("Manara")) {

                                           logger.info("=> Manara raw files :" + allFiles);
//                                            /* COMMENTING ONLY FOR THE TESTING TILL WE ARE ABLE TO READ THE NEW FORMATTED FILES
//                                          Read well csv mapping file and get data into new List with only distinct well with mapping id
                                            List wallMapList = readCsvFile(wellMapping);
                                            createMappingList(wallMapList, wellName);
//                                          Read Latteral csv mapping file and get data into new List with only distinct letteral with mapping id
                                            List LetteralLocList = readCsvFile(letteralMapping);
//                                           logger.info("******************************");
//                                           logger.info("| LetteralLocList list size | :" + LetteralLocList.toString() + " size : " + LetteralLocList.size());
//                                         logger.info("| LetteralLocList list size | :" + Arrays.toString(LetteralLocList.toArray())+" size : "+LetteralLocList.size());
//                                           logger.info("******************************");
                                            createMappingList(LetteralLocList, stationIdentifier);
//                                            */
                                           logger.info("|| " + allFiles.toString() + " ||");
                                            String precessedOpDir = allFiles.toString().replaceAll("THM Data OP-14 ENL", "THM Data OP-14 ENL Pre-processed Data");
                                           logger.info("| " + precessedOpDir + " |");
                                            File fileCheck = new File(precessedOpDir).getParentFile();
                                            if (!fileCheck.exists()) {
//                                               logger.info("get Parent : "+fileCheck.getParentFile());
                                               logger.info("Not exist !!");
                                                fileCheck.mkdirs();
                                            }
//                                           logger.info("OP path"+ opPath.toString());
                                            readNWriteCsvFile(allFiles.toString(), precessedOpDir, dianosticFilterTags, currWellName);
//                                          Move files after processing is done
//                                          Commenting for time being as preproccessing need to fix first
//                                          moveFilesAfterPreprocessing(curDir.getAbsolutePath() + "\\" + dirName + "\\" + entry.getName(), new File(acrhivePath + "\\" + dirName).getAbsolutePath(), entry.getName());


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
               logger.info("___________________________");
//               logger.info(f.getAbsoluteFile() + " | " + f.getAbsolutePath());

                /*
//                If dirName is match with give string then only the parser will start
                if (dirName.equals("L0L") || dirName.equals("L0U") || dirName.equals("L1L") || dirName.equals("L1U") || dirName.equals("L2L") || dirName.equals("L2U")) {
//                    Again listing files with-in above mentioned directory
                    for (File entry : f.listFiles()) {
                        if (entry.isFile()) {
                           logger.info(" -> File : " + entry.getName());
                           logger.info("  =>" + entry.getAbsolutePath() + " :: " + new File(opPath).getAbsolutePath() + "\\" + dirName + "\\" + entry.getName());
                           logger.info("  ==>" + curDir.getAbsolutePath() + "\\" + dirName + "\\" + entry.getName());
//                            readNWriteCsvFile(ipPath + entry.getName(), opPath + entry.getName(), diagnosticTagsList);
                            File newDir = new File(new File(opPath).getAbsolutePath() + "\\" + dirName);
//                            If that dirName like L0L is not exist than create
                            if (!newDir.exists())
                                newDir.mkdir();

                            //        Read well csv mapping file and get data into new List with only distinct well with mapping id
                            List wallMapList = readCsvFile(wellMapping);
                            createMappingList(wallMapList, wellName);
//                          Read Latteral csv mapping file and get data into new List with only distinct letteral with mapping id
                            List LetteralLocList = readCsvFile(letteralMapping);
                           logger.info("******************************");
                           logger.info("| LetteralLocList list size | :" + LetteralLocList.toString() + " size : " + LetteralLocList.size());
//                         logger.info("| LetteralLocList list size | :" + Arrays.toString(LetteralLocList.toArray())+" size : "+LetteralLocList.size());
                           logger.info("******************************");
                            createMappingList(LetteralLocList, stationIdentifier);

                            readNWriteCsvFile(curDir.getAbsolutePath() + "\\" + dirName + "\\" + entry.getName(), newDir + "\\" + entry.getName(), dianosticFilterTags);
//                            Move files after processing is done
//                            Commenting for time being as preprocess need to fix first
//                            moveFilesAfterPreprocessing(curDir.getAbsolutePath() + "\\" + dirName + "\\" + entry.getName(), new File(acrhivePath + "\\" + dirName).getAbsolutePath(), entry.getName());
                        }
                    }
                    //read and give output back to main
                    // write output to output file
                }
                */
            }
        }


    }

    /*
     * method to read the csv file
     * and return the file records as list
     * */
    public static List readCsvFile(String fileName) {
       logger.info("We are in readCsvFile method !!!");
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
           logger.info("Error in CsvFileReader !!!");
            e.printStackTrace();
        } finally {
            try {
                fileReader.close();
                csvFileParser.close();
            } catch (IOException e) {
               logger.info("Error while closing fileReader/csvFileParser !!!");
                e.printStackTrace();
            }
        }
        return csvRecords;
    }

    /* Method is for the Well and Lateral csv file read
     * and to map both csv file reading into list
     * to Create the  new distinct mapping csv file for both
     * */
    public static void createMappingList(List<CSVRecord> allRecords, MappingClass mappingList) {
       logger.info("We are in createMappingList method !!!");
//       logger.info("Size of current csv : " + allRecords.size());
        for (int i = 0; i < allRecords.size(); i++) {
            CSVRecord row = allRecords.get(i);
            if (i > 0) {
//                mappingList/wellName is reference of MappingClass
//                below put will going to add only new well/lateral into maps its and custom maps implementation in MappingClass
                mappingList.put(row.get(0), Integer.parseInt(row.get(1)));
            }
        }
//       logger.info("mapping output :" + mappingList.toString());
    }
    /*
    Read each raw file one by one from getAllFiles Method
     */

    public static void readNWriteCsvFile(String ipFilePath, String opFilePath, List diagnosticHeader, String currWellName) {
       logger.info("We are in readNWriteCsvFile method !!!");

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
           logger.info("Error in CsvFileReader !!!");
            e.printStackTrace();
        } finally {
            try {
                fileReader.close();
                csvFileParser.close();
            } catch (IOException e) {
               logger.info("Error while closing fileReader/csvFileParser !!!");
                e.printStackTrace();
            }
        }
//        calling Parser
        createProcessedFile(opFilePath, csvRecords, diagnosticHeader, currWellName);
    }
    /*
     * Purpose : Getting updated header and saving the final version of file into disk
     */

    public static void createProcessedFile(String fileOut, List<CSVRecord> allRecords, List diagnosticHeader, String currWellName) {
       logger.info("We are in createProcessedFile method !!!");

        FileWriter fileWriter = null;
        CSVPrinter csvFilePrinter = null;
        Map updatedHeader = null;
        Multimap<String, String> tagNamesMultiMap;
        Multimap<String, String> tagNamesMultiMap2; //= ArrayListMultimap.create();

        ArrayList<HashMap<Integer, Integer>> columnMapping;
        ArrayList<String> columnMapping2;
        Map<Integer, String> columnMapping12;

        try {
            fileWriter = new FileWriter(fileOut);
            csvFilePrinter = new CSVPrinter(fileWriter, CSVFormat.EXCEL);
//            Static tagName which we have to consider
            tagNamesMultiMap = readELSXFile(tagsMappingFile, "TagNames");

            tagNamesMultiMap2 = readELSXFile(tagsMappingFile, "TagNames");

           logger.info("tagNamesMultiMap count : " + tagNamesMultiMap.size());
           logger.info("tagNamesMul tiMap2 count : " + tagNamesMultiMap2.size());
//           logger.info("Integer.parseInt(Collections.max(tagNamesMultiMap2.keySet())) :"+ Collections.max(tagNamesMultiMap2.keySet()));
//           logger.info("Integer.parseInt(Collections.max(tagNamesMultiMap2.keySet())) :"+ Arrays.toString(tagNamesMultiMap2.keySet()));
            columnMapping2 = new ArrayList<String>(Collections.nCopies(tagNamesMultiMap2.size(), "-1"));
//            Initialized the map value with default with NULL only
//            InitialCapacity need to change it should be configurable
            columnMapping12 = new HashMap<Integer, String>(217);

            for (String key : tagNamesMultiMap.keySet()) {
                columnMapping12.put(Integer.parseInt(key), "-1");
            }
            for (Object key : tagNamesMultiMap.keySet()) {
                if (allTagsMultiMap.containsKey(key)) {
//                    Collecting all parameters name basis of tagID selected just for all diagnostic parameters from input rawfile
                    tagNamesMultiMap2.putAll((String) key, allTagsMultiMap.get((String) key));
                }
            }
           logger.info("tagNamesMultiMap2 count : " + tagNamesMultiMap2.size());
           logger.info("Just print tagNamesMultiMap2 :" + tagNamesMultiMap2.toString());
           logger.info("Just print tagNamesMultiMap :" + tagNamesMultiMap.toString());
           logger.info("tagNamesMultiMap count : " + tagNamesMultiMap.size());

            Multimap<String, String> invertedMultimap = Multimaps.invertFrom(tagNamesMultiMap2, LinkedListMultimap.<String, String>create());

            for (int i = 0; i < allRecords.size(); i++) {
                List dataRecord = new ArrayList();
                CSVRecord raw = allRecords.get(i);
//                It is for the header preparation
                if (i == 0) {
                    /**
                     * WE HAVE TO THINK HERE FOR HOW TO MAKE STATIC FROM GIVE MIHITHA COLUMNS
                     */
                    String[] headerList = new String[]{raw.get(0), raw.get(1), raw.get(2), raw.get(3), raw.get(4), raw.get(5), raw.get(6), raw.get(7), raw.get(8), raw.get(9), raw.get(10), raw.get(11), raw.get(12), raw.get(13), raw.get(14), raw.get(15), raw.get(16), raw.get(17), raw.get(18), raw.get(19), raw.get(20), raw.get(21), raw.get(22), raw.get(23), raw.get(24), raw.get(25), raw.get(26), raw.get(27), raw.get(28), raw.get(29), raw.get(30), raw.get(31), raw.get(32), raw.get(33), raw.get(34), raw.get(35), raw.get(36), raw.get(37), raw.get(38), raw.get(39), raw.get(40), raw.get(41), raw.get(42), raw.get(43), raw.get(44), raw.get(45), raw.get(46), raw.get(47), raw.get(48), raw.get(49), raw.get(50), raw.get(51), raw.get(52), raw.get(53), raw.get(54), raw.get(55), raw.get(56), raw.get(57), raw.get(58), raw.get(59), raw.get(60), raw.get(61), raw.get(62), raw.get(63), raw.get(64), raw.get(65), raw.get(66), raw.get(67), raw.get(68), raw.get(69), raw.get(70), raw.get(71), raw.get(72), raw.get(73), raw.get(74), raw.get(75), raw.get(76)};
//                    Creating header with use of diagnostic tags
//                    This makeNewHeader method will take care for the : check if current file header is in the diagnostic list if not just skip those columns
                    updatedHeader = makeNewHeader(headerList, diagnosticHeader, currWellName);

//                    Started with 1 as TimeStamp is not included on this
//                    Checking which all parameter from raw file is in static schema tagName file
//                    Changing iteration from zero, so that timestamp can be used
                    for (int tagRef = 0; tagRef < updatedHeader.size(); tagRef++) {
                        if (tagRef == 0) {
                            if (String.valueOf(updatedHeader.get(tagRef)).contains("Timestamp"))
                                columnMapping12.put(tagRef, String.valueOf(tagRef));
                        } else {
                            if (tagNamesMultiMap2.containsValue(String.valueOf(updatedHeader.get(tagRef)))) {
                               logger.info("updatedHeader parameter which is matching : " + updatedHeader.get(tagRef) + " | with index of :" + tagRef);
                               logger.info("Checking updatedHeader parameter is in InvertedMultiMap or not : " + invertedMultimap.containsKey(updatedHeader.get(tagRef)));
                                Integer keyOfStaticSchema = Integer.parseInt(invertedMultimap.get(String.valueOf(updatedHeader.get(tagRef))).toString().replaceAll("\\[|\\]", ""));
                               logger.info("keyOfStaticSchema : " + keyOfStaticSchema);
                                columnMapping2.set(tagRef, String.valueOf(keyOfStaticSchema));
                                columnMapping12.put(tagRef, String.valueOf(keyOfStaticSchema));
                            }
                        }
                    }
//                    dataRecord.addAll(updatedHeader.values());
                    dataRecord.addAll(tagNamesMultiMap.values());
                } else {
                    Set<Integer> colIndex = updatedHeader.keySet();
//                    Static tagID for schema in order for final file
                    Set<String> allTagIDFromStaticSchema = tagNamesMultiMap.keySet(); // allTagIDFromStaticSchema should be start with 0 not 1
//                    LinkedHashSet linkedHashSet = new LinkedHashSet();
//                    linkedHashSet.add("0");
//                    linkedHashSet.addAll(allTagIDFromStaticSchema);
//                    We have to create List with define size and pre-fill data should be there
                    List<String> colValues = new ArrayList<String>(Collections.nCopies(tagNamesMultiMap.size(), ""));

//                    Iterating the tagName keys

//                    Iterator itrator = linkedHashSet.iterator();
                    Iterator itrator = allTagIDFromStaticSchema.iterator();
//                    Integer[] mapKeys = new Integer[tagNamesMultiMap.size()];
                    int pos = 0;
                    while (itrator.hasNext()) {
//                        if (pos == 0)
//                            colValues.set(pos, raw.get(pos));
                        String ind = (String) itrator.next();
//                        if (Integer.parseInt(ind) > 203) {System.out.println("Getting into greater then loop...");}
                        if (!columnMapping12.get(Integer.parseInt(ind)).equals("-1")) {
                            String tagIDVal = columnMapping12.get(Integer.parseInt(ind)).trim();
                            int inx = Integer.parseInt(tagIDVal);
                            String val = raw.get(Integer.parseInt(ind));
                            colValues.set(inx, val);
                        } else {
                            colValues.set(pos, "");
//                            colValues.set(Integer.parseInt(ind), "");
                        }
                       logger.info("colValues : " + ind + " | " + colValues.toString());
                        /*
                        if (!columnMapping2.get(Integer.parseInt(ind)).equals("-1")) {
                            int inx = Integer.parseInt(columnMapping2.get(Integer.parseInt(ind)));
                            if (inx < tagNamesMultiMap.size()) {
//                                if (Integer.parseInt(ind))
                                String val = raw.get(Integer.parseInt(ind));
                                colValues.set(inx, val);
                            } else {
                                colValues.set(Integer.parseInt(ind), "");
                            }
                        } else {
                            colValues.set(Integer.parseInt(ind), "");
                        }
*/
                        pos += 1;
                    }

                    dataRecord.addAll(colValues);
                }
                csvFilePrinter.printRecord(dataRecord);
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
     * its for Header preparation
     * args 1 - header list
     * args 2 - all the diagnostic tags for matching
     * return - diagnostic tags only in new header in the form of map of Integer,String
     */

    public static Map<Integer, String> makeNewHeader(String[] headerList, List diagnosticHeader, String currWellName) {
       logger.info("We are in makeNewHeader method !!!");

//       logger.info("");
        int count = 0;
        int index = 0;
        Map newHeaderWithReplacedVal = new HashMap<Integer, String>();
        for (String column : headerList) {
           logger.info("-------------------------------------------------- Start@ makeNewHeader ");

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
//                System.exit(0);

                wellName.addToMap(currWellName);
                stationIdentifier.addToMap(stationName);
//                Removing header toUpperCase will see if any problem arises(if invert multimap case because of that we were getting same value key twice if they are duplicate)
//                if (diagnosticHeader.contains(parameterName.toUpperCase())) {
                if (diagnosticHeader.contains(parameterName)) {
//                    We have to taken care for the well name and station name as this has to be pass into final file in some consumable format
//                    newHeaderWithReplacedVal.put(index, wellName.get(currWellName) + " " + stationIdentifier.get(stationName) + " " + parameterName);
                    newHeaderWithReplacedVal.put(index, parameterName);
                } else {
                   logger.info("parameter is not diagnostic one " + parameterName + " diagnosticHeader List was : " + diagnosticHeader.toString());
                }
            }
            index += 1;
           logger.info("-------------------------------------------------- end@");

        }
       logger.info("wellSet : letteralSet" + wellName.toString() + " : " + stationIdentifier.toString());

//        Writing the final well and Station mapping files into csv file for each run read
        writeIntoMappingCSV(wellMapping, wellName, WELL_HEADER_MAPPING);
        writeIntoMappingCSV(letteralMapping, stationIdentifier, STATIONIDENTIFIER_HEADER_MAPPING);
        return newHeaderWithReplacedVal;
    }

    public static void writeIntoMappingCSV(String fileName, MappingClass newRecords, String[] header) {
       logger.info("We are in writeIntoMappingCSV method !!!");

        FileWriter fileWriter = null;
        CSVPrinter csvFilePrinter = null;
        List mappingFileRecords = new ArrayList();

        CSVFormat csvFileFormat = CSVFormat.DEFAULT.withHeader(header);
        try {
            fileWriter = new FileWriter(fileName);
            csvFilePrinter = new CSVPrinter(fileWriter, csvFileFormat);
            for (Map.Entry<String, Integer> entry : newRecords.entrySet()) {
                mappingFileRecords.clear();
               logger.info("Key = " + entry.getKey() +
                        ", Value = " + entry.getValue());
                mappingFileRecords.add(entry.getKey());
                mappingFileRecords.add(entry.getValue());
                csvFilePrinter.printRecord(mappingFileRecords);
            }

           logger.info("Successfully : writeIntoMappingCSV CSV file was created successfully !!!");
        } catch (Exception e) {
           logger.info("Error : writeIntoMappingCSV in CsvFileWriter !!!");
            e.printStackTrace();
        } finally {
            try {
                fileWriter.flush();
                fileWriter.close();
                csvFilePrinter.close();
            } catch (IOException e) {
               logger.info("Error : writeIntoMappingCSV while flushing/closing fileWriter/csvPrinter !!!");
                e.printStackTrace();
            }
        }
    }


}

