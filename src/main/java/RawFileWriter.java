import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;

import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


public class RawFileWriter {
    //Delimiter used in CSV file
    private static final String NEW_LINE_SEPARATOR = "\n";
    //CSV file header
    private static final String[] FILE_HEADER = {"Timestamp", "SHYB-97 L0L WNLI001, 3.3V_EXT1", "SHYB-97 L0L WNLI001, 5V_EXT1", "SHYB-97 L0L WNLI001, 5V_EXT3", "SHYB-97 L0L WNLI001, 5V_EXT4", "SHYB-97 L0L Actuator, 5V meas", "SHYB-97 L0L Actuator, comm. number of error", "SHYB-97 L0L Actuator, DR_I_Sense", "SHYB-97 L0L Actuator firmware flags 01", "SHYB-97 L0L Actuator firmware flags 02", "SHYB-97 L0L Actuator firmware flags 03", "SHYB-97 L0L Actuator firmware flags 04", "SHYB-97 L0L Act statuses firmware states 01", "SHYB-97 L0L Act statuses firmware states 02", "SHYB-97 L0L Act statuses firmware states 03", "SHYB-97 L0L Act statuses firmware states 04", "SHYB-97 L0L Act statuses firmware states 05", "SHYB-97 L0L Act statuses firmware states 06", "SHYB-97 L0L Act statuses firmware states 07", "SHYB-97 L0L Act statuses firmware states 08", "SHYB-97 L0L Act statuses firmware states 09", "SHYB-97 L0L Act statuses firmware states 10", "SHYB-97 L0L Actuator, I res", "SHYB-97 L0L Actuator, MV meas", "SHYB-97 L0L Actuator other status data 01", "SHYB-97 L0L Actuator, ref 2V", "SHYB-97 L0L Actuator, diag. temp. measure", "SHYB-97 L0L Actuator choke position (motor turns)", "SHYB-97 L0L Actuator choke position (%)", "SHYB-97 L0L Actuator speed", "SHYB-97 L0L Actuator status", "SHYB-97 L0L Capacitance", "SHYB-97 L0L Valve needs calibration", "SHYB-97 L0L [ 1] COUNT_ALL_WNLITE_BAD_RX_MESSAGES", "SHYB-97 L0L Communication status", "SHYB-97 L0L Calibrated Venturi delta P", "SHYB-97 L0L Alpha parameter for delta p computing", "SHYB-97 L0L Deviation capacitance", "SHYB-97 L0L Measured capacitance in 100% oil", "SHYB-97 L0L Flow rate", "SHYB-97 L0L Fluid mixture viscosity at DH conditions", "SHYB-97 L0L Oil viscosity at DH conditions", "SHYB-97 L0L Water viscosity at DH conditions", "SHYB-97 L0L Reynold's number", "SHYB-97 L0L Fluid mixture density at DH conditions", "SHYB-97 L0L Oil density at DH conditions", "SHYB-97 L0L Water density at DH conditions", "SHYB-97 L0L Water rate", "SHYB-97 L0L Water fraction", "SHYB-97 L0L Water fraction (%)", "SHYB-97 L0L Actuator state", "SHYB-97 L0L Power supply state", "SHYB-97 L0L Sensor state", "SHYB-97 L0L WNLI001 state", "SHYB-97 L0L Power supply, 15V_Meas", "SHYB-97 L0L Power supply, Agnd", "SHYB-97 L0L Power supply, comm. last error type", "SHYB-97 L0L Power supply, comm. number of error", "SHYB-97 L0L Power supply, DC_Bus_Test", "SHYB-97 L0L Power supply firmware flags 01", "SHYB-97 L0L Power supply firmware flags 02", "SHYB-97 L0L Power supply statuses Firmware states 01", "SHYB-97 L0L Power supply statuses Firmware states 02", "SHYB-97 L0L Power supply statuses Firmware states 03", "SHYB-97 L0L Power supply statuses Firmware states 04", "SHYB-97 L0L Power supply, I_Flyback", "SHYB-97 L0L Power supply, I_Head", "SHYB-97 L0L Power supply, Ref_2V", "SHYB-97 L0L Power supply, REF1_2V5", "SHYB-97 L0L Power supply, V_Head", "SHYB-97 L0L Power supply status", "SHYB-97 L0L Raw capacitance (Xia)", "SHYB-97 L0L Sensor, comm. number of error", "SHYB-97 L0L Sensor, chip temperature", "SHYB-97 L0L Sensor, head current", "SHYB-97 L0L Sensor, head voltage", "SHYB-97 L0L [ 1] Cumulative error count on gauge 1", "SHYB-97 L0L [ 3] Cumulative error count on gauge 2", "SHYB-97 L0L Phase angle", "SHYB-97 L0L Sensor #1 pressure", "SHYB-97 L0L Sensor #2 pressure", "SHYB-97 L0L Sensor status", "SHYB-97 L0L Sensor #1 temperature", "SHYB-97 L0L Sensor #2 temperature", "SHYB-97 L0L Tool alarm status", "SHYB-97 L0L WNLI001, WellNet current", "SHYB-97 L0L WNLI001, WellNet voltage", "SHYB-97 L0L WNLI001 status"};

    public static void writeCsvFile(String fileName) {
        RawFilePojo record1 = new RawFilePojo("2017-02-28 00:03:01", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "1", "", "", "", "", "", "", "", "", "0", "", "", "", "0", "", "", "", "0", "0", "0", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "1", "", "", "", "", "", "", "", "", "", "", "1", "", "", "", "", "", "1");
        //Create a new list of student objects
        List<RawFilePojo> rawRecords = new ArrayList();
        rawRecords.add(record1);

        FileWriter fileWriter = null;
        CSVPrinter csvFilePrinter = null;

        //Create the CSVFormat object with "\n" as a record delimiter
        CSVFormat csvFileFormat = CSVFormat.DEFAULT.withRecordSeparator(NEW_LINE_SEPARATOR);
        try {
            //initialize FileWriter object
            fileWriter = new FileWriter(fileName);
            //initialize CSVPrinter object
            csvFilePrinter = new CSVPrinter(fileWriter, csvFileFormat);
            //Create CSV file header
            csvFilePrinter.printRecord(FILE_HEADER);
            //Write a new student object list to the CSV file
            for (RawFilePojo raw : rawRecords) {
                List DataRecord = new ArrayList();
                DataRecord.add(raw.getCol1());
                DataRecord.add(raw.getCol2());
                DataRecord.add(raw.getCol3());
                DataRecord.add(raw.getCol4());
                DataRecord.add(raw.getCol5());
                DataRecord.add(raw.getCol6());
                DataRecord.add(raw.getCol7());
                DataRecord.add(raw.getCol8());
                DataRecord.add(raw.getCol9());
                DataRecord.add(raw.getCol10());
                DataRecord.add(raw.getCol11());
                DataRecord.add(raw.getCol12());
                DataRecord.add(raw.getCol13());
                DataRecord.add(raw.getCol14());
                DataRecord.add(raw.getCol15());
                DataRecord.add(raw.getCol16());
                DataRecord.add(raw.getCol17());
                DataRecord.add(raw.getCol18());
                DataRecord.add(raw.getCol19());
                DataRecord.add(raw.getCol20());
                DataRecord.add(raw.getCol21());
                DataRecord.add(raw.getCol22());
                DataRecord.add(raw.getCol23());
                DataRecord.add(raw.getCol24());
                DataRecord.add(raw.getCol25());
                DataRecord.add(raw.getCol26());
                DataRecord.add(raw.getCol27());
                DataRecord.add(raw.getCol28());
                DataRecord.add(raw.getCol29());
                DataRecord.add(raw.getCol30());
                DataRecord.add(raw.getCol31());
                DataRecord.add(raw.getCol32());
                DataRecord.add(raw.getCol33());
                DataRecord.add(raw.getCol34());
                DataRecord.add(raw.getCol35());
                DataRecord.add(raw.getCol36());
                DataRecord.add(raw.getCol37());
                DataRecord.add(raw.getCol38());
                DataRecord.add(raw.getCol39());
                DataRecord.add(raw.getCol40());
                DataRecord.add(raw.getCol41());
                DataRecord.add(raw.getCol42());
                DataRecord.add(raw.getCol43());
                DataRecord.add(raw.getCol44());
                DataRecord.add(raw.getCol45());
                DataRecord.add(raw.getCol46());
                DataRecord.add(raw.getCol47());
                DataRecord.add(raw.getCol48());
                DataRecord.add(raw.getCol49());
                DataRecord.add(raw.getCol50());
                DataRecord.add(raw.getCol51());
                DataRecord.add(raw.getCol52());
                DataRecord.add(raw.getCol53());
                DataRecord.add(raw.getCol54());
                DataRecord.add(raw.getCol55());
                DataRecord.add(raw.getCol56());
                DataRecord.add(raw.getCol57());
                DataRecord.add(raw.getCol58());
                DataRecord.add(raw.getCol59());
                DataRecord.add(raw.getCol60());
                DataRecord.add(raw.getCol61());
                DataRecord.add(raw.getCol62());
                DataRecord.add(raw.getCol63());
                DataRecord.add(raw.getCol64());
                DataRecord.add(raw.getCol65());
                DataRecord.add(raw.getCol66());
                DataRecord.add(raw.getCol67());
                DataRecord.add(raw.getCol68());
                DataRecord.add(raw.getCol69());
                DataRecord.add(raw.getCol70());
                DataRecord.add(raw.getCol71());
                DataRecord.add(raw.getCol72());
                DataRecord.add(raw.getCol73());
                DataRecord.add(raw.getCol74());
                DataRecord.add(raw.getCol75());
                DataRecord.add(raw.getCol76());
                DataRecord.add(raw.getCol77());
                DataRecord.add(raw.getCol78());
                DataRecord.add(raw.getCol79());
                DataRecord.add(raw.getCol80());
                DataRecord.add(raw.getCol81());
                DataRecord.add(raw.getCol82());
                DataRecord.add(raw.getCol83());
                DataRecord.add(raw.getCol84());
                DataRecord.add(raw.getCol85());
                DataRecord.add(raw.getCol86());
                DataRecord.add(raw.getCol87());
                DataRecord.add(raw.getCol88());

                csvFilePrinter.printRecord(DataRecord);
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