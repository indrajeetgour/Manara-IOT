import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;
import com.monitorjbl.xlsx.StreamingReader;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVPrinter;
import org.apache.commons.csv.CSVRecord;
import org.apache.commons.lang.StringEscapeUtils;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;

import java.io.*;
import java.util.*;

public class OP14RawFileReader {


    private static final String[] WELL_HEADER_MAPPING = {"WellName", "Well ID"};
    private static final String[] STATIONIDENTIFIER_HEADER_MAPPING = {"StationIdentifier", "Station ID"};
//    private static final String[] FILTER_HEADER = {"Tag ID", "Description"};

    private static String acrhivePath = "C:\\Manara-raw-data\\THM Data OP-14 ENL Archive Data\\";

    // TODO : unhandle exceptions for file read
    static Multimap<String, String> readELSXFile(String excelFileLoc, String sheetName) throws Exception {
        System.out.println("We are in readELSXFile method !!!");

        Multimap<String, String> returnMap = ArrayListMultimap.create();
        Multimap<String, String> tags_to_select = ArrayListMultimap.create();
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
        System.out.println("WorkBook/Sheet Name : " + sheetNm);
        if (sheetNm.equals("TagNameAliases")) {
            System.out.println("Reading TagNameAliases sheet !!!!!!!");
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
                    tags_wd_alias_lst.put(row.get(0), row.get(1).toUpperCase());
            }
//            Just for checking the data within the map
//            Again remove once testing is Done
            for (Object key : tags_wd_alias_lst.keySet()) {
//                System.out.println("Key : " + key + " | Values " + tags_wd_alias_lst.get((String) key));
//                System.out.println("Values " + tags_wd_alias_lst.get((String) key));
            }
            returnMap = tags_wd_alias_lst;
        }

        if (sheetNm.equals("WWApp_Tags_to_select")) {
            System.out.println("Reading WWApp_Tags_to_select sheet !!!!!!!");
            for (Row r : sheet) {
                List<String> row = new ArrayList();
                for (Cell c : r) {
                    String colVal = c.getStringCellValue();
                    if (colVal != null || colVal.length() > 0)
                        row.add(colVal);
                    else
                        row.add("");
                }
//                System.out.println("Printing each row : " + row.toString());
                if (row.size() == 31) {
//                    System.out.println("Having 31 columns in a row!!");
                    if (row.get(0).equals("Manara") && row.get(28).equals("Diagnostic")) {
//                        System.out.println("tuple : " + row.get(0) + " | " + row.get(3) + " | " + row.get(4) + " | " + row.get(28) + " | " + row.get(30));
                        tags_to_select.put(row.get(30), row.get(4).toUpperCase());
                    }
                }
            }
//        Just for checking the data within the map
//            This should be remove just for testing purpose
            for (Object key : tags_to_select.keySet()) {
//                System.out.println("Key : " + key + " | Values " + tags_to_select.get((String) key));
//                System.out.println("Values " + tags_to_select.get((String) key));
            }
            returnMap = tags_to_select;
        }
        return returnMap;
    }

    /*
     *  Method is for getting the muliple tags from tagsAlias file and mearge with Manara Measurment file
     */
    public static Multimap<String, String> getOneTagAliasMapping(Multimap<String, String> manaraMeasurementMultiMap, Multimap<String, String> tagsMultiMap) {
        System.out.println("We are in getOneTagAliasMapping method !!!");
        System.out.println("We are in merger of tags from Alias file ...");
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
    /*
     * Move the processed file from current raw folder to Archived location
     *
     */

    public static void moveFilesAfterPreprocessing(String sourceFolder, String acrhiveFolder, String fileName) {
        System.out.println("We are in moveFilesAfterPreprocessing method !!!");

        System.out.println(" Moving file from source to Archive dir !!!!!!");
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
            System.out.println("sourceDir :" + sourceDir + " $$$$ Successful copied into : " + archiveDir + " $$ Loc, file copied was : " + fileName);
        else
            System.out.println("sourceDir :" + sourceDir + " #### Failed copied into : " + archiveDir + " ## Loc, file copied was : " + fileName);
    }

    static String wellMapping = "C:\\Manara-raw-data\\mapping-inputs-files\\WellMapping.csv";
    static String letteralMapping = "C:\\Manara-raw-data\\mapping-inputs-files\\StationIdentifierMapping.csv";
    static MappingClass wellName = new MappingClass();
    static MappingClass stationIdentifier = new MappingClass();

    static String tagsMappingFile = "C:\\Manara-raw-data\\mapping-inputs-files\\updated-one\\TagNamesAndAliases_2018_01_12.xlsx";
//    static String tagsMappingFile = "C:\\Manara-raw-data\\mapping-inputs-files\\TagNamesAndAliases_2018_01_09.xlsx";
    static String manaraMeasurementFile = "C:\\Manara-raw-data\\mapping-inputs-files\\updated-one\\Manara_SRS007_1_10_RTAC_public_measurements_definition.xlsx";
//    static String manaraMeasurementFile = "C:\\Manara-raw-data\\mapping-inputs-files\\Manara_SRS007_1_9_RTAC_public_measurements_definition.xlsx";

    /*
     * Starting point of code
     * Step 1 : Taken care for all the diagnostic parameters from different sources and merging into one
     * Step 2 : Read all the data for wells and laterals from the csv file so that actual values can be replaced
     * Step 3 : Reading the directory one by one and for parsing
     * Step 4 : Start the parsing process by calling readNWriteCsvFile method
     * Step 5 : moving files from original directory Archive directory after the successful completion of parsing
     */
    public static void getAllFiles(String ipPath, String opPath) throws Exception {
        System.out.println("We are in getAllFiles method !!!");
        List dianosticFilterTags = new ArrayList();

        File curDir = new File(ipPath);
        File[] RTACGeneratedDirList = curDir.listFiles();

        /* Below code will call the Manara measurements file and will join this with TagsAndAlias file
         * to get the all possible tags names from TagsAlias file         *
         */
        Multimap<String, String> manaraMeasurementMultiMap = readELSXFile(manaraMeasurementFile, "WWApp_Tags_to_select");
        Multimap<String, String> tagsMultiMap = readELSXFile(tagsMappingFile, "TagNameAliases");

        Multimap<String, String> allTagsMultiMap = OP14RawFileReader.getOneTagAliasMapping(manaraMeasurementMultiMap, tagsMultiMap);
        Set<String> set = new HashSet<>();
        set.addAll(allTagsMultiMap.values());
        dianosticFilterTags = new ArrayList(set);
//        Remove after the testing is done
//        System.out.println("##################################Printing all values of tags from set :###################################");
        System.out.println("All Tags merger into set for uniqueness is done!!! ");
        for (String r : set) {
            System.out.println(" list print :" + r);
        }
        for (String tagNameAliasesParam : tagsMultiMap.values()) {
            System.out.println("tagNameAliasesParam : " + tagNameAliasesParam);
        }
        for (String col : allTagsMultiMap.values()) {
//            System.out.println("allTagsMultiMap : "+col.toString());
        }
        /*
         * below code was using only formated csv file having tags ID and Desc, which was not sufficient
         *
//        Just the csv read and get data in List form
        List diagnosticTagsList = readCsvFile(diagnosticTagFile);
//        Preparing to get list of diagnostic tags only
//        getDiagnosticTagsList(diagnosticTagsList);
        System.out.println("dianosticFilterTags : " + dianosticFilterTags);
        */

//        iterate over the input directory
        for (File f : RTACGeneratedDirList) {
            if (f.isDirectory()) {
                String dirName = f.getName();
                System.out.println(">Print curr RTAC per date dir : " + dirName);

                File[] historicalDirList = f.listFiles();

                for (File histDir : historicalDirList) {
                    System.out.println(">>Print historicalDirList : " + histDir);
                    String dirName1 = histDir.getName();

                    if (dirName1.equals("Historical-data")) {
//                            System.out.println(">>> With-in historicalDirList : " + histDir);
                        for (File wellType : histDir.listFiles()) {
//                            System.out.println(">>>Print wellType : " + wellType);
                            String wellTyp = wellType.getName();

                            if (!wellTyp.startsWith("Well")) {
//                                System.out.println("Non OP-14 well name :"+wellTyp);
                                System.out.println(">>>Print wellType : " + wellType);
                                String currWellName = wellTyp;
                                System.out.println(">>> @OP-14 well name :" + wellTyp);
                                for (File dt : wellType.listFiles()) {
                                    System.out.println(">>>> Date dir under Op-14 well :" + dt);
                                    System.out.println(">>>> Coming files are of date : " + dt.getName());
                                    for (File allFiles : dt.listFiles()) {
                                        String rawFileName = allFiles.getName();
//                                        below if block, only for the Manara raw file processing
                                        if (rawFileName.contains("Manara")) {

                                            System.out.println("=> Manara raw files :" + allFiles);
//                                            /* COMMENTING ONLY FOR THE TESTING TILL WE ARE ABLE TO READ THE NEW FORMATTED FILES
//                                          Read well csv mapping file and get data into new List with only distinct well with mapping id
                                            List wallMapList = readCsvFile(wellMapping);
                                            createMappingList(wallMapList, wellName);
//                                          Read Latteral csv mapping file and get data into new List with only distinct letteral with mapping id
                                            List LetteralLocList = readCsvFile(letteralMapping);
                                            System.out.println("******************************");
                                            System.out.println("| LetteralLocList list size | :" + LetteralLocList.toString() + " size : " + LetteralLocList.size());
//                                          System.out.println("| LetteralLocList list size | :" + Arrays.toString(LetteralLocList.toArray())+" size : "+LetteralLocList.size());
                                            System.out.println("******************************");
                                            createMappingList(LetteralLocList, stationIdentifier);
//                                            */
                                            System.out.println("|| " + allFiles.toString() + " ||");
                                            String precessedOpDir = allFiles.toString().replaceAll("THM Data OP-14 ENL", "THM Data OP-14 ENL Pre-processed Data");
                                            System.out.println("| " + precessedOpDir + " |");
                                            File fileCheck = new File(precessedOpDir).getParentFile();
                                            if (!fileCheck.exists()) {
//                                                System.out.println("get Parent : "+fileCheck.getParentFile());
                                                System.out.println("Not exist !!");
                                                fileCheck.mkdirs();
                                            }
//                                            System.out.println("OP path"+ opPath.toString());
                                            readNWriteCsvFile(allFiles.toString(), precessedOpDir, dianosticFilterTags, currWellName);
//                                          Move files after processing is done
//                                          Commenting for time being as preproccessing need to fix first
//                                          moveFilesAfterPreprocessing(curDir.getAbsolutePath() + "\\" + dirName + "\\" + entry.getName(), new File(acrhivePath + "\\" + dirName).getAbsolutePath(), entry.getName());


                                        } else {
//                                            this block is only for processing MSU/IWIC raw files | COMMENTED TO TEST ONLY MANARA FILE FIRST
//                                            System.out.println("=> MSU/IWIC raw files :" + allFiles);
                                        }


                                    }
                                }
                            }


                        }


                    }
                }
                System.out.println("___________________________");
//                System.out.println(f.getAbsoluteFile() + " | " + f.getAbsolutePath());
//                System.exit(0);

                /*
//                If dirName is match with give string then only the parser will start
                if (dirName.equals("L0L") || dirName.equals("L0U") || dirName.equals("L1L") || dirName.equals("L1U") || dirName.equals("L2L") || dirName.equals("L2U")) {
//                    Again listing files with-in above mentioned directory
                    for (File entry : f.listFiles()) {
                        if (entry.isFile()) {
                            System.out.println(" -> File : " + entry.getName());
                            System.out.println("  =>" + entry.getAbsolutePath() + " :: " + new File(opPath).getAbsolutePath() + "\\" + dirName + "\\" + entry.getName());
                            System.out.println("  ==>" + curDir.getAbsolutePath() + "\\" + dirName + "\\" + entry.getName());
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
                            System.out.println("******************************");
                            System.out.println("| LetteralLocList list size | :" + LetteralLocList.toString() + " size : " + LetteralLocList.size());
//                          System.out.println("| LetteralLocList list size | :" + Arrays.toString(LetteralLocList.toArray())+" size : "+LetteralLocList.size());
                            System.out.println("******************************");
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
        System.out.println("We are in readCsvFile method !!!");
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
            System.out.println("Error in CsvFileReader !!!");
            e.printStackTrace();
        } finally {
            try {
                fileReader.close();
                csvFileParser.close();
            } catch (IOException e) {
                System.out.println("Error while closing fileReader/csvFileParser !!!");
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
        System.out.println("We are in createMappingList method !!!");
        System.out.println("Size of current csv : " + allRecords.size());
        for (int i = 0; i < allRecords.size(); i++) {
            CSVRecord row = allRecords.get(i);
            if (i > 0) {
//                mappingList/wellName is reference of MappingClass
//                below put will going to add only new well/lateral into maps its and custom maps implementation in MappingClass
                mappingList.put(row.get(0), Integer.parseInt(row.get(1)));
            }
        }
//        System.out.println("mapping output :" + mappingList.toString());
    }
    /*
    Read each raw file one by one from getAllFiles Method
     */

    public static void readNWriteCsvFile(String ipFilePath, String opFilePath, List diagnosticHeader, String currWellName) {
        System.out.println("We are in readNWriteCsvFile method !!!");

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
            System.out.println("Error in CsvFileReader !!!");
            e.printStackTrace();
        } finally {
            try {
                fileReader.close();
                csvFileParser.close();
            } catch (IOException e) {
                System.out.println("Error while closing fileReader/csvFileParser !!!");
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
        System.out.println("We are in createProcessedFile method !!!");

        FileWriter fileWriter = null;
        CSVPrinter csvFilePrinter = null;
        Map updatedHeader = null;
        try {
            fileWriter = new FileWriter(fileOut);
            csvFilePrinter = new CSVPrinter(fileWriter, CSVFormat.EXCEL);
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
                    updatedHeader = makeNewHeader(headerList, diagnosticHeader, currWellName);
//                    System.out.println(" New Header with Diagnostic tags only  : " + updatedHeader.toString());
                    dataRecord.addAll(updatedHeader.values());
                } else {
                    Set<Integer> colIndex = updatedHeader.keySet();
                    List<String> colValues = new ArrayList<String>();
                    for (Integer ind : colIndex) {
                        colValues.add(raw.get(ind));
                    }
                    dataRecord.addAll(colValues);
                }
                csvFilePrinter.printRecord(dataRecord);
            }
            System.out.println("successfully : createProcessedFile, CSV file was created !!!");
        } catch (Exception e) {
            System.out.println("Error : createProcessedFile in CsvFileWriter !!!");
            e.printStackTrace();
        } finally {
            try {
                fileWriter.flush();
                fileWriter.close();
                csvFilePrinter.close();
            } catch (IOException e) {
                System.out.println("Error : createProcessedFile, while flushing/closing fileWriter/csvPrinter !!!");
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
        System.out.println("We are in makeNewHeader method !!!");

        System.out.println("");
        int count = 0;
        int index = 0;
        Map newHeaderWithReplacedVal = new HashMap<Integer, String>();
        for (String column : headerList) {
            System.out.println("+++++++++ makeNewHeader +++++++++++ ## START ## ");

            if (column.contains("Timestamp")) {
//                To include Timestamp column in the header always
                newHeaderWithReplacedVal.put(index, column);
            } else {
                int indexOfFirstSpace = column.indexOf("_");

                int indexOfSecSpace = 1 + column.indexOf("_", 1);
                int indexStartOfUnitConvrs = column.indexOf("(");
                int indexEndOfUnitConvrs = column.indexOf(")");

                System.out.println("indexStartOfUnitConvrs  : " + indexStartOfUnitConvrs);
                System.out.println("indexEndOfUnitConvrs : " + indexEndOfUnitConvrs);
//                This Unit table need to be maintain into the another table | ASK MIHITHA FIRST
                System.out.println("Unit : " + column.substring(indexStartOfUnitConvrs + 1, indexEndOfUnitConvrs));

//                System.out.println("indexOfFirstSpace " + indexOfFirstSpace + " || " + column.substring(0,column.indexOf("_")));

//                Get Well and lateral from raw column name
                String stationName = column.substring(0, indexOfFirstSpace);
                String well_and_lateral = column.substring(0, indexOfSecSpace);

                System.out.println("stationName :" + stationName);
//                System.out.println("Well_leteral : " + well_and_lateral);
//                Get sensorChannel/parameter name from raw column name
                String parameterName = column.substring(indexOfFirstSpace + 1);
                System.out.println("parameterName :" + parameterName);
                System.out.println("StringEscapeUtils : "+StringEscapeUtils.escapeJava(parameterName));

                String sensorChannel = column.substring(indexOfSecSpace);
//                System.out.println("sensor name : " + sensorChannel);

//                System.exit(0);

//                Array of well and lateral at 0 and 1 location of array
                String[] well_lateral_array = well_and_lateral.split(" ");
//                System.out.println("well_leteral_array  : " + Arrays.toString(well_lateral_array));

                wellName.addToMap(currWellName);
                stationIdentifier.addToMap(stationName);
                if (well_lateral_array.length > 1) {
                    count = count + 1;
                    if (well_lateral_array[0] != null) {
//                        Adding only new well name which are not there in well mapping file
                        wellName.addToMap(well_lateral_array[0]);
                    } else {
                        System.out.println("well_lateral_array[0], key is null for index 0");
                    }
                    if (well_lateral_array[1] != null) {
//                        Adding new Lateral loc if not found in lateral mapping file
                        stationIdentifier.addToMap(well_lateral_array[1]);
                    } else {
                        System.out.println("well_lateral_array[1], key is null for index 1");
                    }
                }
//                Comment for implementing the diagnostic tag logic , currWellName
//                newHeaderWithReplacedVal.add(wellName.get(well_lateral_array[0]) + " " + letteralLoc.get(well_lateral_array[1]) + " " + sensorChannel);
                if (diagnosticHeader.contains(parameterName.toUpperCase()))
                    newHeaderWithReplacedVal.put(index, wellName.get(currWellName) + " " + stationIdentifier.get(stationName) + " " + parameterName);
                else
                    System.out.println("diagnosticHeader does not contains " + parameterName + " In the diagnosticHeader List : " + diagnosticHeader.toString());
            }
            index += 1;
            System.out.println("-------------------------------------------------- end : makeNewHeader ");

        }
        System.out.println("wellSet : letteralSet" + wellName.toString() + " : " + stationIdentifier.toString());

//        Writing the final well and Station mapping files into csv file for each run read
        writeIntoMappingCSV(wellMapping, wellName, WELL_HEADER_MAPPING);
        writeIntoMappingCSV(letteralMapping, stationIdentifier, STATIONIDENTIFIER_HEADER_MAPPING);

        return newHeaderWithReplacedVal;
    }

    public static void writeIntoMappingCSV(String fileName, MappingClass newRecords, String[] header) {
        System.out.println("We are in writeIntoMappingCSV method !!!");

        FileWriter fileWriter = null;
        CSVPrinter csvFilePrinter = null;
        List mappingFileRecords = new ArrayList();

        CSVFormat csvFileFormat = CSVFormat.DEFAULT.withHeader(header);
        try {
            fileWriter = new FileWriter(fileName);
            csvFilePrinter = new CSVPrinter(fileWriter, csvFileFormat);
            for (Map.Entry<String, Integer> entry : newRecords.entrySet()) {
                mappingFileRecords.clear();
                System.out.println("Key = " + entry.getKey() +
                        ", Value = " + entry.getValue());
                mappingFileRecords.add(entry.getKey());
                mappingFileRecords.add(entry.getValue());
                csvFilePrinter.printRecord(mappingFileRecords);
            }

            System.out.println("Successfully : writeIntoMappingCSV CSV file was created successfully !!!");
        } catch (Exception e) {
            System.out.println("Error : writeIntoMappingCSV in CsvFileWriter !!!");
            e.printStackTrace();
        } finally {
            try {
                fileWriter.flush();
                fileWriter.close();
                csvFilePrinter.close();
            } catch (IOException e) {
                System.out.println("Error : writeIntoMappingCSV while flushing/closing fileWriter/csvPrinter !!!");
                e.printStackTrace();
            }
        }
    }


}

