import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVPrinter;
import org.apache.commons.csv.CSVRecord;

import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class RawFileReader_bckv1 {
    //CSV file header
    private static final String[] FILE_HEADER_MAPPING = {"Timestamp", "WNLI001, 3.3V_EXT1", "WNLI001, 5V_EXT1", "WNLI001, 5V_EXT3", "WNLI001, 5V_EXT4", "Actuator, 5V meas", "Actuator, comm. number of error", "Actuator, DR_I_Sense", "Actuator firmware flags 01", "Actuator firmware flags 02", "Actuator firmware flags 03", "Actuator firmware flags 04", "Act statuses firmware states 01", "Act statuses firmware states 02", "Act statuses firmware states 03", "Act statuses firmware states 04", "Act statuses firmware states 05", "Act statuses firmware states 06", "Act statuses firmware states 07", "Act statuses firmware states 08", "Act statuses firmware states 09", "Act statuses firmware states 10", "Actuator, I res", "Actuator, MV meas", "Actuator other status data 01", "Actuator, ref 2V", "Actuator, diag. temp. measure", "Actuator choke position (motor turns)", "Actuator choke position (%)", "Actuator speed", "Actuator status", "Capacitance", "Valve needs calibration", "[ 1] COUNT_ALL_WNLITE_BAD_RX_MESSAGES", "Communication status", "Calibrated Venturi delta P", "Alpha parameter for delta p computing", "Deviation capacitance", "Measured capacitance in 100% oil", "Flow rate", "Fluid mixture viscosity at DH conditions", "Oil viscosity at DH conditions", "Water viscosity at DH conditions", "Reynold's number", "Fluid mixture density at DH conditions", "Oil density at DH conditions", "Water density at DH conditions", "Water rate", "Water fraction", "Water fraction (%)", "Actuator state", "Power supply state", "Sensor state", "WNLI001 state", "Power supply, 15V_Meas", "Power supply, Agnd", "Power supply, comm. last error type", "Power supply, comm. number of error", "Power supply, DC_Bus_Test", "Power supply firmware flags 01", "Power supply firmware flags 02", "Power supply statuses Firmware states 01", "Power supply statuses Firmware states 02", "Power supply statuses Firmware states 03", "Power supply statuses Firmware states 04", "Power supply, I_Flyback", "Power supply, I_Head", "Power supply, Ref_2V", "Power supply, REF1_2V5", "Power supply, V_Head", "Power supply status", "Raw capacitance (Xia)", "Sensor, comm. number of error", "Sensor, chip temperature", "Sensor, head current", "Sensor, head voltage", "[ 1] Cumulative error count on gauge 1", "[ 3] Cumulative error count on gauge 2", "Phase angle", "Sensor #1 pressure", "Sensor #2 pressure", "Sensor status", "Sensor #1 temperature", "Sensor #2 temperature", "Tool alarm status", "WNLI001, WellNet current", "WNLI001, WellNet voltage", "WNLI001 status"};

    //Raw file attributes
    private static final String col1 = "Timestamp";
    private static final String col2 = "WNLI001, 3.3V_EXT1";
    private static final String col3 = "WNLI001, 5V_EXT1";
    private static final String col4 = "WNLI001, 5V_EXT3";
    private static final String col5 = "WNLI001, 5V_EXT4";
    private static final String col6 = "Actuator, 5V meas";
    private static final String col7 = "Actuator, comm. number of error";
    private static final String col8 = "Actuator, DR_I_Sense";
    private static final String col9 = "Actuator firmware flags 01";
    private static final String col10 = "Actuator firmware flags 02";
    private static final String col11 = "Actuator firmware flags 03";
    private static final String col12 = "Actuator firmware flags 04";
    private static final String col13 = "Act statuses firmware states 01";
    private static final String col14 = "Act statuses firmware states 02";
    private static final String col15 = "Act statuses firmware states 03";
    private static final String col16 = "Act statuses firmware states 04";
    private static final String col17 = "Act statuses firmware states 05";
    private static final String col18 = "Act statuses firmware states 06";
    private static final String col19 = "Act statuses firmware states 07";
    private static final String col20 = "Act statuses firmware states 08";
    private static final String col21 = "Act statuses firmware states 09";
    private static final String col22 = "Act statuses firmware states 10";
    private static final String col23 = "Actuator, I res";
    private static final String col24 = "Actuator, MV meas";
    private static final String col25 = "Actuator other status data 01";
    private static final String col26 = "Actuator, ref 2V";
    private static final String col27 = "Actuator, diag. temp. measure";
    private static final String col28 = "Actuator choke position (motor turns)";
    private static final String col29 = "Actuator choke position (%)";
    private static final String col30 = "Actuator speed";
    private static final String col31 = "Actuator status";
    private static final String col32 = "Capacitance";
    private static final String col33 = "Valve needs calibration";
    private static final String col34 = "[ 1] COUNT_ALL_WNLITE_BAD_RX_MESSAGES";
    private static final String col35 = "Communication status";
    private static final String col36 = "Calibrated Venturi delta P";
    private static final String col37 = "Alpha parameter for delta p computing";
    private static final String col38 = "Deviation capacitance";
    private static final String col39 = "Measured capacitance in 100% oil";
    private static final String col40 = "Flow rate";
    private static final String col41 = "Fluid mixture viscosity at DH conditions";
    private static final String col42 = "Oil viscosity at DH conditions";
    private static final String col43 = "Water viscosity at DH conditions";
    private static final String col44 = "Reynold's number";
    private static final String col45 = "Fluid mixture density at DH conditions";
    private static final String col46 = "Oil density at DH conditions";
    private static final String col47 = "Water density at DH conditions";
    private static final String col48 = "Water rate";
    private static final String col49 = "Water fraction";
    private static final String col50 = "Water fraction (%)";
    private static final String col51 = "Actuator state";
    private static final String col52 = "Power supply state";
    private static final String col53 = "Sensor state";
    private static final String col54 = "WNLI001 state";
    private static final String col55 = "Power supply, 15V_Meas";
    private static final String col56 = "Power supply, Agnd";
    private static final String col57 = "Power supply, comm. last error type";
    private static final String col58 = "Power supply, comm. number of error";
    private static final String col59 = "Power supply, DC_Bus_Test";
    private static final String col60 = "Power supply firmware flags 01";
    private static final String col61 = "Power supply firmware flags 02";
    private static final String col62 = "Power supply statuses Firmware states 01";
    private static final String col63 = "Power supply statuses Firmware states 02";
    private static final String col64 = "Power supply statuses Firmware states 03";
    private static final String col65 = "Power supply statuses Firmware states 04";
    private static final String col66 = "Power supply, I_Flyback";
    private static final String col67 = "Power supply, I_Head";
    private static final String col68 = "Power supply, Ref_2V";
    private static final String col69 = "Power supply, REF1_2V5";
    private static final String col70 = "Power supply, V_Head";
    private static final String col71 = "Power supply status";
    private static final String col72 = "Raw capacitance (Xia)";
    private static final String col73 = "Sensor, comm. number of error";
    private static final String col74 = "Sensor, chip temperature";
    private static final String col75 = "Sensor, head current";
    private static final String col76 = "Sensor, head voltage";
    private static final String col77 = "[ 1] Cumulative error count on gauge 1";
    private static final String col78 = "[ 3] Cumulative error count on gauge 2";
    private static final String col79 = "Phase angle";
    private static final String col80 = "Sensor #1 pressure";
    private static final String col81 = "Sensor #2 pressure";
    private static final String col82 = "Sensor status";
    private static final String col83 = "Sensor #1 temperature";
    private static final String col84 = "Sensor #2 temperature";
    private static final String col85 = "Tool alarm status";
    private static final String col86 = "WNLI001, WellNet current";
    private static final String col87 = "WNLI001, WellNet voltage";
    private static final String col88 = "WNLI001 status";

    //Delimiter used in CSV file
    private static final String NEW_LINE_SEPARATOR = "\n";
    //CSV file header
    private static final String[] FILE_HEADER_RAW = {"Timestamp", "SHYB-97 L0L WNLI001, 3.3V_EXT1", "SHYB-97 L0L WNLI001, 5V_EXT1", "SHYB-97 L0L WNLI001, 5V_EXT3", "SHYB-97 L0L WNLI001, 5V_EXT4", "SHYB-97 L0L Actuator, 5V meas", "SHYB-97 L0L Actuator, comm. number of error", "SHYB-97 L0L Actuator, DR_I_Sense", "SHYB-97 L0L Actuator firmware flags 01", "SHYB-97 L0L Actuator firmware flags 02", "SHYB-97 L0L Actuator firmware flags 03", "SHYB-97 L0L Actuator firmware flags 04", "SHYB-97 L0L Act statuses firmware states 01", "SHYB-97 L0L Act statuses firmware states 02", "SHYB-97 L0L Act statuses firmware states 03", "SHYB-97 L0L Act statuses firmware states 04", "SHYB-97 L0L Act statuses firmware states 05", "SHYB-97 L0L Act statuses firmware states 06", "SHYB-97 L0L Act statuses firmware states 07", "SHYB-97 L0L Act statuses firmware states 08", "SHYB-97 L0L Act statuses firmware states 09", "SHYB-97 L0L Act statuses firmware states 10", "SHYB-97 L0L Actuator, I res", "SHYB-97 L0L Actuator, MV meas", "SHYB-97 L0L Actuator other status data 01", "SHYB-97 L0L Actuator, ref 2V", "SHYB-97 L0L Actuator, diag. temp. measure", "SHYB-97 L0L Actuator choke position (motor turns)", "SHYB-97 L0L Actuator choke position (%)", "SHYB-97 L0L Actuator speed", "SHYB-97 L0L Actuator status", "SHYB-97 L0L Capacitance", "SHYB-97 L0L Valve needs calibration", "SHYB-97 L0L [ 1] COUNT_ALL_WNLITE_BAD_RX_MESSAGES", "SHYB-97 L0L Communication status", "SHYB-97 L0L Calibrated Venturi delta P", "SHYB-97 L0L Alpha parameter for delta p computing", "SHYB-97 L0L Deviation capacitance", "SHYB-97 L0L Measured capacitance in 100% oil", "SHYB-97 L0L Flow rate", "SHYB-97 L0L Fluid mixture viscosity at DH conditions", "SHYB-97 L0L Oil viscosity at DH conditions", "SHYB-97 L0L Water viscosity at DH conditions", "SHYB-97 L0L Reynold's number", "SHYB-97 L0L Fluid mixture density at DH conditions", "SHYB-97 L0L Oil density at DH conditions", "SHYB-97 L0L Water density at DH conditions", "SHYB-97 L0L Water rate", "SHYB-97 L0L Water fraction", "SHYB-97 L0L Water fraction (%)", "SHYB-97 L0L Actuator state", "SHYB-97 L0L Power supply state", "SHYB-97 L0L Sensor state", "SHYB-97 L0L WNLI001 state", "SHYB-97 L0L Power supply, 15V_Meas", "SHYB-97 L0L Power supply, Agnd", "SHYB-97 L0L Power supply, comm. last error type", "SHYB-97 L0L Power supply, comm. number of error", "SHYB-97 L0L Power supply, DC_Bus_Test", "SHYB-97 L0L Power supply firmware flags 01", "SHYB-97 L0L Power supply firmware flags 02", "SHYB-97 L0L Power supply statuses Firmware states 01", "SHYB-97 L0L Power supply statuses Firmware states 02", "SHYB-97 L0L Power supply statuses Firmware states 03", "SHYB-97 L0L Power supply statuses Firmware states 04", "SHYB-97 L0L Power supply, I_Flyback", "SHYB-97 L0L Power supply, I_Head", "SHYB-97 L0L Power supply, Ref_2V", "SHYB-97 L0L Power supply, REF1_2V5", "SHYB-97 L0L Power supply, V_Head", "SHYB-97 L0L Power supply status", "SHYB-97 L0L Raw capacitance (Xia)", "SHYB-97 L0L Sensor, comm. number of error", "SHYB-97 L0L Sensor, chip temperature", "SHYB-97 L0L Sensor, head current", "SHYB-97 L0L Sensor, head voltage", "SHYB-97 L0L [ 1] Cumulative error count on gauge 1", "SHYB-97 L0L [ 3] Cumulative error count on gauge 2", "SHYB-97 L0L Phase angle", "SHYB-97 L0L Sensor #1 pressure", "SHYB-97 L0L Sensor #2 pressure", "SHYB-97 L0L Sensor status", "SHYB-97 L0L Sensor #1 temperature", "SHYB-97 L0L Sensor #2 temperature", "SHYB-97 L0L Tool alarm status", "SHYB-97 L0L WNLI001, WellNet current", "SHYB-97 L0L WNLI001, WellNet voltage", "SHYB-97 L0L WNLI001 status"};
    static int count = 0;

    public static void makeNewHeader() {

        for (String column : FILE_HEADER_RAW) {
            String s1 = column;
            System.out.println("------------------------Start--------------------------");
            System.out.println("column : " + column);
            System.out.println("--------------------------------------------------in");

            int tt = 1 + s1.indexOf(" ", s1.indexOf(" ") + 1);
            System.out.println("print : " + s1.substring(0, tt));

            String well_leteral = s1.substring(0, tt);
            String[] well_leteral12 = new String[]{well_leteral};

            String[] wordss = s1.substring(0, tt).split(" ");

            if (well_leteral12.length > 1) {
                count = count + 1;
                System.out.println("Well name : " + well_leteral12[0]);
                System.out.println("Letral loc : " + well_leteral12[1]);
            }
            System.out.println("sensor name : " + s1.substring(tt));

            System.out.println("--------------------------------------------------end");

        }
    }

    public static void readNWriteCsvFile(String fileName, String fileNameout) {
        FileReader fileReader = null;
        CSVParser csvFileParser = null;
        List csvRecords = null;

        List<RawFilePojo> rawRecords = new ArrayList();

        //Create the CSVFormat object with the header mapping
        CSVFormat csvFileFormat = CSVFormat.DEFAULT.withHeader(FILE_HEADER_MAPPING);
        try {
            //initialize FileReader object
            fileReader = new FileReader(fileName);
            //initialize CSVParser object
//            csvFileParser = new CSVParser(fileReader, csvFileFormat);
            csvFileParser = new CSVParser(fileReader, CSVFormat.EXCEL);
            //Get a list of CSV file records
            csvRecords = csvFileParser.getRecords();
/*
            Map<String, Integer> headerMap = csvFileParser.getHeaderMap();
            for (Entry<String, Integer> entry : headerMap.entrySet()) {
                System.out.println("Key : " + entry.getKey() + " | value : " + entry.getValue());
            }
//            System.out.println("CSVparser header : ******************** " + csvFileParser.getHeaderMap());
*/
/*
//Read the CSV file records starting from the second record to skip the header
            for (int i = 1; i < csvRecords.size(); i++) {
                CSVRecord record = (CSVRecord) csvRecords.get(i);
                //Create a new student object and fill his data
                RawFilePojo rawRecord = new RawFilePojo(record.get(col1), record.get(col2), record.get(col3), record.get(col4), record.get(col5), record.get(col6), record.get(col7), record.get(col8), record.get(col9), record.get(col10), record.get(col11), record.get(col12), record.get(col13), record.get(col14), record.get(col15), record.get(col16), record.get(col17), record.get(col18), record.get(col19), record.get(col20), record.get(col21), record.get(col22), record.get(col23), record.get(col24), record.get(col25), record.get(col26), record.get(col27), record.get(col28), record.get(col29), record.get(col30), record.get(col31), record.get(col32), record.get(col33), record.get(col34), record.get(col35), record.get(col36), record.get(col37), record.get(col38), record.get(col39), record.get(col40), record.get(col41), record.get(col42), record.get(col43), record.get(col44), record.get(col45), record.get(col46), record.get(col47), record.get(col48), record.get(col49), record.get(col50), record.get(col51), record.get(col52), record.get(col53), record.get(col54), record.get(col55), record.get(col56), record.get(col57), record.get(col58), record.get(col59), record.get(col60), record.get(col61), record.get(col62), record.get(col63), record.get(col64), record.get(col65), record.get(col66), record.get(col67), record.get(col68), record.get(col69), record.get(col70), record.get(col71), record.get(col72), record.get(col73), record.get(col74), record.get(col75), record.get(col76), record.get(col77), record.get(col78), record.get(col79), record.get(col80), record.get(col81), record.get(col82), record.get(col83), record.get(col84), record.get(col85), record.get(col86), record.get(col87), record.get(col88));
//                rawRecords.add(rawRecord);
                rawRecords.add((RawFilePojo) record);
            } */
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
//        createProcessedFile(fileNameout, rawRecords);
        writeCsvFile(fileNameout, csvRecords);
    }

    //    public static void createProcessedFile(String fileNameout, List<RawFilePojo> allRecords) {
    public static void writeCsvFile(String fileNameout, List<CSVRecord> allRecords) {
        FileWriter fileWriter = null;
        CSVPrinter csvFilePrinter = null;
        //Create the CSVFormat object with the header mapping
        CSVFormat csvFileFormat = CSVFormat.DEFAULT.withHeader(FILE_HEADER_MAPPING);
        try {
            fileWriter = new FileWriter(fileNameout);
            //initialize CSVPrinter object
            csvFilePrinter = new CSVPrinter(fileWriter, csvFileFormat);
//            csvFilePrinter = new CSVPrinter(fileWriter, CSVFormat.EXCEL);

            //Write a new student object list to the CSV file
//            for (CSVRecord raw : allRecords) {
            for (int i = 1; i < allRecords.size(); i++) {
                List dataRecord = new ArrayList();
                CSVRecord raw = allRecords.get(i);
                dataRecord.add(raw.get(0));
                dataRecord.add(raw.get(1));
                dataRecord.add(raw.get(2));
                dataRecord.add(raw.get(3));
                dataRecord.add(raw.get(4));
                dataRecord.add(raw.get(5));
                dataRecord.add(raw.get(6));
                dataRecord.add(raw.get(7));
                dataRecord.add(raw.get(8));
                dataRecord.add(raw.get(9));
                dataRecord.add(raw.get(10));
                dataRecord.add(raw.get(11));
                dataRecord.add(raw.get(12));
                dataRecord.add(raw.get(13));
                dataRecord.add(raw.get(14));
                dataRecord.add(raw.get(15));
                dataRecord.add(raw.get(16));
                dataRecord.add(raw.get(17));
                dataRecord.add(raw.get(18));
                dataRecord.add(raw.get(19));
                dataRecord.add(raw.get(20));
                dataRecord.add(raw.get(21));
                dataRecord.add(raw.get(22));
                dataRecord.add(raw.get(23));
                dataRecord.add(raw.get(24));
                dataRecord.add(raw.get(25));
                dataRecord.add(raw.get(26));
                dataRecord.add(raw.get(27));
                dataRecord.add(raw.get(28));
                dataRecord.add(raw.get(29));
                dataRecord.add(raw.get(30));
                dataRecord.add(raw.get(31));
                dataRecord.add(raw.get(32));
                dataRecord.add(raw.get(33));
                dataRecord.add(raw.get(34));
                dataRecord.add(raw.get(35));
                dataRecord.add(raw.get(36));
                dataRecord.add(raw.get(37));
                dataRecord.add(raw.get(38));
                dataRecord.add(raw.get(39));
                dataRecord.add(raw.get(40));
                dataRecord.add(raw.get(41));
                dataRecord.add(raw.get(42));
                dataRecord.add(raw.get(43));
                dataRecord.add(raw.get(44));
                dataRecord.add(raw.get(45));
                dataRecord.add(raw.get(46));
                dataRecord.add(raw.get(47));
                dataRecord.add(raw.get(48));
                dataRecord.add(raw.get(49));
                dataRecord.add(raw.get(50));
                dataRecord.add(raw.get(51));
                dataRecord.add(raw.get(52));
                dataRecord.add(raw.get(53));
                dataRecord.add(raw.get(54));
                dataRecord.add(raw.get(55));
                dataRecord.add(raw.get(56));
                dataRecord.add(raw.get(57));
                dataRecord.add(raw.get(58));
                dataRecord.add(raw.get(59));
                dataRecord.add(raw.get(60));
                dataRecord.add(raw.get(61));
                dataRecord.add(raw.get(62));
                dataRecord.add(raw.get(63));
                dataRecord.add(raw.get(64));
                dataRecord.add(raw.get(65));
                dataRecord.add(raw.get(66));
                dataRecord.add(raw.get(67));
                dataRecord.add(raw.get(68));
                dataRecord.add(raw.get(69));
                dataRecord.add(raw.get(70));
                dataRecord.add(raw.get(71));
                dataRecord.add(raw.get(72));
                dataRecord.add(raw.get(73));
                dataRecord.add(raw.get(74));
                dataRecord.add(raw.get(75));
                dataRecord.add(raw.get(76));
                dataRecord.add(raw.get(77));
                dataRecord.add(raw.get(78));
                dataRecord.add(raw.get(79));
                dataRecord.add(raw.get(80));
                dataRecord.add(raw.get(81));
                dataRecord.add(raw.get(82));
                dataRecord.add(raw.get(83));
                dataRecord.add(raw.get(84));
                dataRecord.add(raw.get(85));
                dataRecord.add(raw.get(86));
                dataRecord.add(raw.get(87));
                csvFilePrinter.printRecord(dataRecord);
            }
            System.out.println("CSV file was created successfully !!!");
        } catch (Exception e) {
            System.out.println("Error in CsvFileWriter !!!");
            e.printStackTrace();
        } finally {
            try {
                fileWriter.flush();
                fileWriter.close();
                csvFilePrinter.close();
            } catch (IOException e) {
                System.out.println("Error while flushing/closing fileWriter/csvPrinter !!!");
                e.printStackTrace();
            }
        }
    }
}