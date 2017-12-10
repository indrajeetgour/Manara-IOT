import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVPrinter;
import org.apache.commons.csv.CSVRecord;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class RawFileReader_bckv2 {
    //CSV file header
//    private static final String[] FILE_HEADER_MAPPING = {"Timestamp", "WNLI001, 3.3V_EXT1", "WNLI001, 5V_EXT1", "WNLI001, 5V_EXT3", "WNLI001, 5V_EXT4", "Actuator, 5V meas", "Actuator, comm. number of error", "Actuator, DR_I_Sense", "Actuator firmware flags 01", "Actuator firmware flags 02", "Actuator firmware flags 03", "Actuator firmware flags 04", "Act statuses firmware states 01", "Act statuses firmware states 02", "Act statuses firmware states 03", "Act statuses firmware states 04", "Act statuses firmware states 05", "Act statuses firmware states 06", "Act statuses firmware states 07", "Act statuses firmware states 08", "Act statuses firmware states 09", "Act statuses firmware states 10", "Actuator, I res", "Actuator, MV meas", "Actuator other status data 01", "Actuator, ref 2V", "Actuator, diag. temp. measure", "Actuator choke position (motor turns)", "Actuator choke position (%)", "Actuator speed", "Actuator status", "Capacitance", "Valve needs calibration", "[ 1] COUNT_ALL_WNLITE_BAD_RX_MESSAGES", "Communication status", "Calibrated Venturi delta P", "Alpha parameter for delta p computing", "Deviation capacitance", "Measured capacitance in 100% oil", "Flow rate", "Fluid mixture viscosity at DH conditions", "Oil viscosity at DH conditions", "Water viscosity at DH conditions", "Reynold's number", "Fluid mixture density at DH conditions", "Oil density at DH conditions", "Water density at DH conditions", "Water rate", "Water fraction", "Water fraction (%)", "Actuator state", "Power supply state", "Sensor state", "WNLI001 state", "Power supply, 15V_Meas", "Power supply, Agnd", "Power supply, comm. last error type", "Power supply, comm. number of error", "Power supply, DC_Bus_Test", "Power supply firmware flags 01", "Power supply firmware flags 02", "Power supply statuses Firmware states 01", "Power supply statuses Firmware states 02", "Power supply statuses Firmware states 03", "Power supply statuses Firmware states 04", "Power supply, I_Flyback", "Power supply, I_Head", "Power supply, Ref_2V", "Power supply, REF1_2V5", "Power supply, V_Head", "Power supply status", "Raw capacitance (Xia)", "Sensor, comm. number of error", "Sensor, chip temperature", "Sensor, head current", "Sensor, head voltage", "[ 1] Cumulative error count on gauge 1", "[ 3] Cumulative error count on gauge 2", "Phase angle", "Sensor #1 pressure", "Sensor #2 pressure", "Sensor status", "Sensor #1 temperature", "Sensor #2 temperature", "Tool alarm status", "WNLI001, WellNet current", "WNLI001, WellNet voltage", "WNLI001 status"};

    //Delimiter used in CSV file
    private static final String NEW_LINE_SEPARATOR = "\n";
    //CSV file header
//    private static final String[] FILE_HEADER_RAW = {"Timestamp", "SHYB-97 L0L WNLI001, 3.3V_EXT1", "SHYB-97 L0L WNLI001, 5V_EXT1", "SHYB-97 L0L WNLI001, 5V_EXT3", "SHYB-97 L0L WNLI001, 5V_EXT4", "SHYB-97 L0L Actuator, 5V meas", "SHYB-97 L0L Actuator, comm. number of error", "SHYB-97 L0L Actuator, DR_I_Sense", "SHYB-97 L0L Actuator firmware flags 01", "SHYB-97 L0L Actuator firmware flags 02", "SHYB-97 L0L Actuator firmware flags 03", "SHYB-97 L0L Actuator firmware flags 04", "SHYB-97 L0L Act statuses firmware states 01", "SHYB-97 L0L Act statuses firmware states 02", "SHYB-97 L0L Act statuses firmware states 03", "SHYB-97 L0L Act statuses firmware states 04", "SHYB-97 L0L Act statuses firmware states 05", "SHYB-97 L0L Act statuses firmware states 06", "SHYB-97 L0L Act statuses firmware states 07", "SHYB-97 L0L Act statuses firmware states 08", "SHYB-97 L0L Act statuses firmware states 09", "SHYB-97 L0L Act statuses firmware states 10", "SHYB-97 L0L Actuator, I res", "SHYB-97 L0L Actuator, MV meas", "SHYB-97 L0L Actuator other status data 01", "SHYB-97 L0L Actuator, ref 2V", "SHYB-97 L0L Actuator, diag. temp. measure", "SHYB-97 L0L Actuator choke position (motor turns)", "SHYB-97 L0L Actuator choke position (%)", "SHYB-97 L0L Actuator speed", "SHYB-97 L0L Actuator status", "SHYB-97 L0L Capacitance", "SHYB-97 L0L Valve needs calibration", "SHYB-97 L0L [ 1] COUNT_ALL_WNLITE_BAD_RX_MESSAGES", "SHYB-97 L0L Communication status", "SHYB-97 L0L Calibrated Venturi delta P", "SHYB-97 L0L Alpha parameter for delta p computing", "SHYB-97 L0L Deviation capacitance", "SHYB-97 L0L Measured capacitance in 100% oil", "SHYB-97 L0L Flow rate", "SHYB-97 L0L Fluid mixture viscosity at DH conditions", "SHYB-97 L0L Oil viscosity at DH conditions", "SHYB-97 L0L Water viscosity at DH conditions", "SHYB-97 L0L Reynold's number", "SHYB-97 L0L Fluid mixture density at DH conditions", "SHYB-97 L0L Oil density at DH conditions", "SHYB-97 L0L Water density at DH conditions", "SHYB-97 L0L Water rate", "SHYB-97 L0L Water fraction", "SHYB-97 L0L Water fraction (%)", "SHYB-97 L0L Actuator state", "SHYB-97 L0L Power supply state", "SHYB-97 L0L Sensor state", "SHYB-97 L0L WNLI001 state", "SHYB-97 L0L Power supply, 15V_Meas", "SHYB-97 L0L Power supply, Agnd", "SHYB-97 L0L Power supply, comm. last error type", "SHYB-97 L0L Power supply, comm. number of error", "SHYB-97 L0L Power supply, DC_Bus_Test", "SHYB-97 L0L Power supply firmware flags 01", "SHYB-97 L0L Power supply firmware flags 02", "SHYB-97 L0L Power supply statuses Firmware states 01", "SHYB-97 L0L Power supply statuses Firmware states 02", "SHYB-97 L0L Power supply statuses Firmware states 03", "SHYB-97 L0L Power supply statuses Firmware states 04", "SHYB-97 L0L Power supply, I_Flyback", "SHYB-97 L0L Power supply, I_Head", "SHYB-97 L0L Power supply, Ref_2V", "SHYB-97 L0L Power supply, REF1_2V5", "SHYB-97 L0L Power supply, V_Head", "SHYB-97 L0L Power supply status", "SHYB-97 L0L Raw capacitance (Xia)", "SHYB-97 L0L Sensor, comm. number of error", "SHYB-97 L0L Sensor, chip temperature", "SHYB-97 L0L Sensor, head current", "SHYB-97 L0L Sensor, head voltage", "SHYB-97 L0L [ 1] Cumulative error count on gauge 1", "SHYB-97 L0L [ 3] Cumulative error count on gauge 2", "SHYB-97 L0L Phase angle", "SHYB-97 L0L Sensor #1 pressure", "SHYB-97 L0L Sensor #2 pressure", "SHYB-97 L0L Sensor status", "SHYB-97 L0L Sensor #1 temperature", "SHYB-97 L0L Sensor #2 temperature", "SHYB-97 L0L Tool alarm status", "SHYB-97 L0L WNLI001, WellNet current", "SHYB-97 L0L WNLI001, WellNet voltage", "SHYB-97 L0L WNLI001 status"};
    static int count = 0;
//    static MappingClass wellName, letteralLoc;

    static MappingClass wellName = new MappingClass();
    static MappingClass letteralLoc = new MappingClass();


    static String wellMapping = "C:\\Manara-raw-data\\processed-data\\WellMapping.csv";
    static String letteralMapping = "C:\\Manara-raw-data\\processed-data\\LetteralMapping.csv";
    private static final String[] WELL_HEADER_MAPPING = {"WellName", "Replaced with"};
    private static final String[] LETTREAL_HEADER_MAPPING = {"Letteral Location", "Replaced with"};

    public static void getAllFiles(String ipPath, String opPath) {
        File curDir = new File(ipPath);
        File[] filesList = curDir.listFiles();
        for (File f : filesList) {
            if (f.isDirectory())
                System.out.println("It is the directory : " + f.getName());

            if (f.isFile()) {
                System.out.println("It is the file : " + f.getName());

                readNWriteCsvFile(ipPath + f.getName(), opPath + f.getName());
            }
        }
    }

    public static void readNWriteCsvFile(String fileName, String fileNameout) {
        FileReader fileReader = null;
        CSVParser csvFileParser = null;
        List csvRecords = null;
        List<RawFilePojo> rawRecords = new ArrayList();
        try {
            //initialize FileReader object
            fileReader = new FileReader(fileName);
            //initialize CSVParser object
//            csvFileParser = new CSVParser(fileReader, csvFileFormat);
            csvFileParser = new CSVParser(fileReader, CSVFormat.EXCEL);
            //Get a list of CSV file records
            csvRecords = csvFileParser.getRecords();
/*            Map<String, Integer> headerMap = csvFileParser.getHeaderMap();
            for (Entry<String, Integer> entry : headerMap.entrySet()) {
                System.out.println("Key : " + entry.getKey() + " | value : " + entry.getValue());
            }
//            System.out.println("CSVparser header : ******************** " + csvFileParser.getHeaderMap());
*/
//            RawFileWriter.createProcessedFile();
/*            //Print the new student list
            for (Object record : rawRecords) {
                System.out.println(record);
            }*/
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
        createProcessedFile(fileNameout, csvRecords);
//        makeNewHeader();
    }

    public static void createProcessedFile(String fileNameout, List<CSVRecord> allRecords) {
        FileWriter fileWriter = null;
        CSVPrinter csvFilePrinter = null;
        try {
            fileWriter = new FileWriter(fileNameout);
            csvFilePrinter = new CSVPrinter(fileWriter, CSVFormat.EXCEL);
            for (int i = 0; i < allRecords.size(); i++) {
                List dataRecord = new ArrayList();
                String[] headerList = null; //= new ArrayList();
                CSVRecord raw = allRecords.get(i);
                if (i == 0) {
                    headerList = new String[]{raw.get(0), raw.get(1), raw.get(2), raw.get(3), raw.get(4), raw.get(5), raw.get(6), raw.get(7), raw.get(8), raw.get(9), raw.get(10), raw.get(11), raw.get(12), raw.get(13), raw.get(14), raw.get(15), raw.get(16), raw.get(17), raw.get(18), raw.get(19), raw.get(20), raw.get(21), raw.get(22), raw.get(23), raw.get(24), raw.get(25), raw.get(26), raw.get(27), raw.get(28), raw.get(29), raw.get(30), raw.get(31), raw.get(32), raw.get(33), raw.get(34), raw.get(35), raw.get(36), raw.get(37), raw.get(38), raw.get(39), raw.get(40), raw.get(41), raw.get(42), raw.get(43), raw.get(44), raw.get(45), raw.get(46), raw.get(47), raw.get(48), raw.get(49), raw.get(50), raw.get(51), raw.get(52), raw.get(53), raw.get(54), raw.get(55), raw.get(56), raw.get(57), raw.get(58), raw.get(59), raw.get(60), raw.get(61), raw.get(62), raw.get(63), raw.get(64), raw.get(65), raw.get(66), raw.get(67), raw.get(68), raw.get(69), raw.get(70), raw.get(71), raw.get(72), raw.get(73), raw.get(74), raw.get(75), raw.get(76), raw.get(77), raw.get(78), raw.get(79), raw.get(80), raw.get(81), raw.get(82), raw.get(83), raw.get(84), raw.get(85), raw.get(86), raw.get(87)};
//                    List updatedHeader = makeNewHeader(headerList);
                    dataRecord.addAll(makeNewHeader(headerList));
                } else {
                    dataRecord.addAll(Arrays.asList(raw.get(0), raw.get(1), raw.get(2), raw.get(3), raw.get(4), raw.get(5), raw.get(6), raw.get(7), raw.get(8), raw.get(9), raw.get(10), raw.get(11), raw.get(12), raw.get(13), raw.get(14), raw.get(15), raw.get(16), raw.get(17), raw.get(18), raw.get(19), raw.get(20), raw.get(21), raw.get(22), raw.get(23), raw.get(24), raw.get(25), raw.get(26), raw.get(27), raw.get(28), raw.get(29), raw.get(30), raw.get(31), raw.get(32), raw.get(33), raw.get(34), raw.get(35), raw.get(36), raw.get(37), raw.get(38), raw.get(39), raw.get(40), raw.get(41), raw.get(42), raw.get(43), raw.get(44), raw.get(45), raw.get(46), raw.get(47), raw.get(48), raw.get(49), raw.get(50), raw.get(51), raw.get(52), raw.get(53), raw.get(54), raw.get(55), raw.get(56), raw.get(57), raw.get(58), raw.get(59), raw.get(60), raw.get(61), raw.get(62), raw.get(63), raw.get(64), raw.get(65), raw.get(66), raw.get(67), raw.get(68), raw.get(69), raw.get(70), raw.get(71), raw.get(72), raw.get(73), raw.get(74), raw.get(75), raw.get(76), raw.get(77), raw.get(78), raw.get(79), raw.get(80), raw.get(81), raw.get(82), raw.get(83), raw.get(84), raw.get(85), raw.get(86), raw.get(87)));
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

    public static List makeNewHeader(String[] headerList) {
        int count = 0;
        List newHeaderWithReplacedVal = new ArrayList();

//        wellName = new MappingClass();
//        letteralLoc = new MappingClass();
        for (String column : headerList) {
            System.out.println("------------------------Start--------------------------");
            System.out.println("column : " + column);
            System.out.println("-------------------------------------------------- in : makeNewHeader");

            if (column.contains("Timestamp")) {
                newHeaderWithReplacedVal.add(column);
//                System.out.println("This is Timestamp column ");
            } else {
                int indexOfSecSpace = 1 + column.indexOf(" ", column.indexOf(" ") + 1);
                String well_leteral = column.substring(0, indexOfSecSpace);
                System.out.println("Well_leteral : " + well_leteral);

                String sensorChannel = column.substring(indexOfSecSpace);
                System.out.println("sensor name : " + sensorChannel);
                newHeaderWithReplacedVal.add(sensorChannel);
                String[] well_leteral_array = well_leteral.split(" ");
                System.out.println("well_leteral_array  : " + Arrays.toString(well_leteral_array));

                if (well_leteral_array.length > 1) {
                    count = count + 1;
                    if (well_leteral_array[0] != null) {
                        wellName.addToMap(well_leteral_array[0]);
                    } else {
                        System.out.println("key is null for index 0");
                    }
                    if (well_leteral_array[1] != null) {
                        letteralLoc.addToMap(well_leteral_array[1]);
                        if (count == 2)
                            System.out.println("Second Column..");
//                            checkIfExistInMapping(well_leteral_array[1]);
                    } else {
                        System.out.println("key is null for index 1");
                    }
                }
            }
            count += 1;
            System.out.println("-------------------------------------------------- end : makeNewHeader ");
        }
        System.out.println("wellSet : letteralSet" + wellName.toString() + " : " + letteralLoc.toString());
//        writeIntoMappingCSV(wellMapping, wellName, WELL_HEADER_MAPPING);
        writeIntoMappingCSV(letteralMapping, letteralLoc, LETTREAL_HEADER_MAPPING);
        return newHeaderWithReplacedVal;
    }

    public static void writeIntoMappingCSV(String fileName, MappingClass newRecords, String[] header) {
        FileWriter fileWriter = null;
        CSVPrinter csvFilePrinter = null;
        List mappingFileRecords = new ArrayList();

        checkIfExistInMapping(fileName, newRecords, header);

        CSVFormat csvFileFormat = CSVFormat.DEFAULT.withHeader(header);
        try {
            fileWriter = new FileWriter(fileName);
            csvFilePrinter = new CSVPrinter(fileWriter, csvFileFormat);
            for (Map.Entry<String, Integer> entry : newRecords.entrySet()) {
                System.out.println("Key = " + entry.getKey() +
                        ", Value = " + entry.getValue());
                mappingFileRecords.add(entry.getKey());
                mappingFileRecords.add(entry.getValue());
            }
            csvFilePrinter.printRecord(mappingFileRecords);
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

    public static void checkIfExistInMapping(String fileName, MappingClass newRecords, String[] header) {
    }
}