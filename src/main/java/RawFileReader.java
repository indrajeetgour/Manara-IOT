import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVPrinter;
import org.apache.commons.csv.CSVRecord;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.poifs.filesystem.POIFSFileSystem;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;

import java.io.*;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;

import static java.nio.file.Files.*;

public class RawFileReader {

    static List indexOfDiagnosticTags = new ArrayList();

    static MappingClass wellName = new MappingClass();
    static MappingClass letteralLoc = new MappingClass();
    static DiagnosticTagMappingClass diagnosticTagMappingClass = new DiagnosticTagMappingClass();

    static String wellMapping = "C:\\Manara-raw-data\\mapping-inputs-files\\WellMapping.csv";
    static String letteralMapping = "C:\\Manara-raw-data\\mapping-inputs-files\\LetteralMapping.csv";
    static String diagnosticTagFile = "C:\\Manara-raw-data\\mapping-inputs-files\\Manara_SRS007_1_9_RTAC_public_measurements_definition_abstract.csv";

    private static final String[] WELL_HEADER_MAPPING = {"WellName", "Replaced with"};
    private static final String[] LETTREAL_HEADER_MAPPING = {"Letteral Location", "Replaced with"};
    private static final String[] FILTER_HEADER = {"Tag ID", "Description"};
    private static List dianosticFilterTags = new ArrayList();


    //    public void moveFilesAfterPreprocessing(String inPath, String acrhivePath) {
    public static void moveFilesAfterPreprocessing() {
        String acrhivePath = "C:\\Manara-raw-data\\SHYB-97 Raw Data\\Manara Stations Acrhive Of Processed Data\\L0L\\";
        String ipPath = "C:\\Manara-raw-data\\SHYB-97 Raw Data\\Manara Stations\\L0L\\";
        String fileName = "L0L 20160507-0700AM to 20160507-1200PM.csv";
        File acrhiveDir = new File(acrhivePath);
        File sourceDir = new File(ipPath);
        if (!acrhiveDir.exists()) {
            acrhiveDir.mkdirs();
            System.out.println("making acrhive Dir");
        }
        if (sourceDir.exists() && sourceDir.isDirectory()) {
            File[] listOfFiles = sourceDir.listFiles();
            if (listOfFiles != null) {
                for (File child : listOfFiles) {
                    child.renameTo(new File(acrhiveDir
                            + "\\" + child.getName()));

                }

            }

        }

    }

    public static void readFromExcel() {
        HSSFWorkbook filename = null;
        try {
            //test file is located in your project path
            FileInputStream fileIn = new FileInputStream("E:\\downloads\\mining.xls");

            //read file
            POIFSFileSystem fs = new POIFSFileSystem(fileIn);
            filename = new HSSFWorkbook(fs);


        } catch (Exception e) {
        }
        //HSSFSheet sheet = filename.getSheetAt(0);
        HSSFSheet sheet = filename.getSheet("data");

        //HSSFSheet sheet = filename.getSheet("sheet1");
        Map<Double, String> TagNameIdMap = new HashMap<Double, String>();

        for (int rowNumber = sheet.getFirstRowNum(); rowNumber < sheet.getLastRowNum(); rowNumber++) {
            Row row = sheet.getRow(rowNumber);
            if (row != null) {
                Cell scope = row.getCell(1);
                Cell catalog = row.getCell(2);
                Cell Description = row.getCell(3);
                Cell DB_TagName = row.getCell(4);
                String scopeValue = scope.getStringCellValue();
                String catalogValue = catalog.getStringCellValue();

                if (scopeValue != null && scopeValue.equalsIgnoreCase("Manara")
                        && catalogValue != null && catalogValue.equalsIgnoreCase("Diagnostic")) {
                    TagNameIdMap.put(DB_TagName.getNumericCellValue(), Description.getStringCellValue());
                }
            }
        }

        if (TagNameIdMap.isEmpty()) {
            System.out.println("No data found");
        } else {
            System.out.println("MAP : " + TagNameIdMap.toString());
        }
    }


    public static void getAllFiles(String ipPath, String opPath) {
        File curDir = new File(ipPath);
        File[] filesList = curDir.listFiles();
        List diagnosticTagsList = readCsvFile(diagnosticTagFile);
        getDiagnosticTagsList(diagnosticTagsList);

        System.out.println("dianosticFilterTags : " + dianosticFilterTags);
        List wallMapList = readCsvFile(wellMapping);
        createMappingList(wallMapList, wellName);

        List LetteralLocList = readCsvFile(letteralMapping);
        createMappingList(LetteralLocList, letteralLoc);

        for (File f : filesList) {
            if (f.isDirectory()) {
                String dirName = f.getName();
                System.out.println(f.getAbsoluteFile() + " | " + f.getAbsolutePath());

                System.out.println(">Directory : " + dirName);
//                File nDir =new File(opPath).getAbsolutePath()+"\\"+dirName;
//                System.out.println("++++++++++"+opPath);
                if (dirName.equals("L0L") || dirName.equals("L0U") || dirName.equals("L1L") || dirName.equals("L1U") || dirName.equals("L2L") || dirName.equals("L2U")) {
                    for (File entry : f.listFiles()) {
                        if (entry.isFile()) {
                            System.out.println(" -> File : " + entry.getName());
                            System.out.println("  =>" + entry.getAbsolutePath() + " :: " + new File(opPath).getAbsolutePath() + "\\" + dirName + "\\" + entry.getName());
                            System.out.println("  ==>" + curDir.getAbsolutePath() + "\\" + dirName + "\\" + entry.getName());
//                            readNWriteCsvFile(ipPath + entry.getName(), opPath + entry.getName(), diagnosticTagsList);
                            File newDir = new File(new File(opPath).getAbsolutePath() + "\\" + dirName);
                            if (!newDir.exists())
                                newDir.mkdir();
//                            readNWriteCsvFile(curDir.getAbsolutePath() + "\\" + dirName + "\\" + entry.getName(), newDir + "\\" + entry.getName(), diagnosticTagsList);
                        }
                    }
                    //read and give output back to main
                    // write output to output file
                }
            }
        }
    }

    static void getDiagnosticTagsList(List list) {

        for (int i = 0; i < list.size(); i++) {
            List dataRecord = new ArrayList();
            CSVRecord raw = (CSVRecord) list.get(i);
            System.out.println(raw.get(0));
            System.out.println(raw.get(1));
            dianosticFilterTags.add(raw.get(1));
        }
    }

    /*
    * method to read the csv file
    * and return the file records as list
    * */
    public static List readCsvFile(String fileName) {
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

    /*
     * method to map csv reading to list
     * */
    public static void createMappingList(List<CSVRecord> allRecords, MappingClass mappingList) {
        for (int i = 0; i < allRecords.size(); i++) {
            CSVRecord row = allRecords.get(i);
            if (i > 0) {
                mappingList.put(row.get(0), Integer.parseInt(row.get(1)));
            }
        }
        System.out.println("mapping output :" + mappingList.toString());
    }

    public static void readNWriteCsvFile(String ipFilePath, String opFilePath, List diagnosticTagsList) {
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
        createProcessedFile(opFilePath, csvRecords, diagnosticTagsList);
//        makeNewHeader();
    }

    public static void createProcessedFile(String fileOut, List<CSVRecord> allRecords, List diagnosticHeader) {
        FileWriter fileWriter = null;
        CSVPrinter csvFilePrinter = null;
        Map updatedHeader = null;
        try {
            fileWriter = new FileWriter(fileOut);
            csvFilePrinter = new CSVPrinter(fileWriter, CSVFormat.EXCEL);
            for (int i = 0; i < allRecords.size(); i++) {
                List dataRecord = new ArrayList();
                CSVRecord raw = allRecords.get(i);
                if (i == 0) {
                    String[] headerList = new String[]{raw.get(0), raw.get(1), raw.get(2), raw.get(3), raw.get(4), raw.get(5), raw.get(6), raw.get(7), raw.get(8), raw.get(9), raw.get(10), raw.get(11), raw.get(12), raw.get(13), raw.get(14), raw.get(15), raw.get(16), raw.get(17), raw.get(18), raw.get(19), raw.get(20), raw.get(21), raw.get(22), raw.get(23), raw.get(24), raw.get(25), raw.get(26), raw.get(27), raw.get(28), raw.get(29), raw.get(30), raw.get(31), raw.get(32), raw.get(33), raw.get(34), raw.get(35), raw.get(36), raw.get(37), raw.get(38), raw.get(39), raw.get(40), raw.get(41), raw.get(42), raw.get(43), raw.get(44), raw.get(45), raw.get(46), raw.get(47), raw.get(48), raw.get(49), raw.get(50), raw.get(51), raw.get(52), raw.get(53), raw.get(54), raw.get(55), raw.get(56), raw.get(57), raw.get(58), raw.get(59), raw.get(60), raw.get(61), raw.get(62), raw.get(63), raw.get(64), raw.get(65), raw.get(66), raw.get(67), raw.get(68), raw.get(69), raw.get(70), raw.get(71), raw.get(72), raw.get(73), raw.get(74), raw.get(75), raw.get(76), raw.get(77), raw.get(78), raw.get(79), raw.get(80), raw.get(81), raw.get(82), raw.get(83), raw.get(84), raw.get(85), raw.get(86), raw.get(87)};

                    updatedHeader = makeNewHeader(headerList, diagnosticHeader);
                    System.out.println(" New Header with Diagnostic tags only  : " + updatedHeader.toString());

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

    public static Map<Integer, String> makeNewHeader(String[] headerList, List diagnosticHeader) {
        int count = 0;
        int index = 0;
//        List newHeaderWithReplacedVal = new ArrayList();
        Map newHeaderWithReplacedVal = new HashMap<Integer, String>();
        for (String column : headerList) {
            System.out.println("------------------------Start--------------------------");
            System.out.println("column : " + column);
            System.out.println("-------------------------------------------------- in : makeNewHeader");

            if (column.contains("Timestamp")) {
                newHeaderWithReplacedVal.put(index, column);
//                System.out.println("This is Timestamp column ");
            } else {
                int indexOfSecSpace = 1 + column.indexOf(" ", column.indexOf(" ") + 1);
                String well_and_lateral = column.substring(0, indexOfSecSpace);
                System.out.println("Well_leteral : " + well_and_lateral);

                String sensorChannel = column.substring(indexOfSecSpace);
                System.out.println("sensor name : " + sensorChannel);

                String[] well_lateral_array = well_and_lateral.split(" ");
                System.out.println("well_leteral_array  : " + Arrays.toString(well_lateral_array));

                if (well_lateral_array.length > 1) {
                    count = count + 1;
                    if (well_lateral_array[0] != null) {
                        wellName.addToMap(well_lateral_array[0]);
                    } else {
                        System.out.println("key is null for index 0");
                    }
                    if (well_lateral_array[1] != null) {
                        letteralLoc.addToMap(well_lateral_array[1]);
                        if (count == 2)
                            System.out.println("Second Column..");
//                            checkIfExistInMapping(well_leteral_array[1]);
                    } else {
                        System.out.println("key is null for index 1");
                    }
                }
// Comment for implimenting the diagnostic tag logic
//                newHeaderWithReplacedVal.add(wellName.get(well_lateral_array[0]) + " " + letteralLoc.get(well_lateral_array[1]) + " " + sensorChannel);
                if (dianosticFilterTags.contains(sensorChannel))
                    newHeaderWithReplacedVal.put(index, wellName.get(well_lateral_array[0]) + " " + letteralLoc.get(well_lateral_array[1]) + " " + sensorChannel);
                else
                    System.out.println("diagnosticHeader does not contains " + sensorChannel + " In the diagnosticHeader List : " + diagnosticHeader.toString());

            }
            index += 1;
            System.out.println("-------------------------------------------------- end : makeNewHeader ");

        }
        System.out.println("wellSet : letteralSet" + wellName.toString() + " : " + letteralLoc.toString());

        writeIntoMappingCSV(wellMapping, wellName, WELL_HEADER_MAPPING);
        writeIntoMappingCSV(letteralMapping, letteralLoc, LETTREAL_HEADER_MAPPING);
        return newHeaderWithReplacedVal;
    }

    public static void writeIntoMappingCSV(String fileName, MappingClass newRecords, String[] header) {
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