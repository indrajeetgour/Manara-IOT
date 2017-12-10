import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;


public class CsvFileWriter {

    //Delimiter used in CSV file
    private static final String NEW_LINE_SEPARATOR = "\n";

    //CSV file header
//    private static final Object[] FILE_HEADER = {"id", "firstName", "lastName", "gender", "age"};
    private static final String[] FILE_HEADER = {"Timestamp", "SHYB-97 L0L WNLI001, 3.3V_EXT1", "SHYB-97 L0L WNLI001, 5V_EXT1", "SHYB-97 L0L WNLI001, 5V_EXT3", "SHYB-97 L0L WNLI001, 5V_EXT4", "SHYB-97 L0L Actuator, 5V meas", "SHYB-97 L0L Actuator, comm. number of error", "SHYB-97 L0L Actuator, DR_I_Sense", "SHYB-97 L0L Actuator firmware flags 01", "SHYB-97 L0L Actuator firmware flags 02", "SHYB-97 L0L Actuator firmware flags 03", "SHYB-97 L0L Actuator firmware flags 04", "SHYB-97 L0L Act statuses firmware states 01", "SHYB-97 L0L Act statuses firmware states 02", "SHYB-97 L0L Act statuses firmware states 03", "SHYB-97 L0L Act statuses firmware states 04", "SHYB-97 L0L Act statuses firmware states 05", "SHYB-97 L0L Act statuses firmware states 06", "SHYB-97 L0L Act statuses firmware states 07", "SHYB-97 L0L Act statuses firmware states 08", "SHYB-97 L0L Act statuses firmware states 09", "SHYB-97 L0L Act statuses firmware states 10", "SHYB-97 L0L Actuator, I res", "SHYB-97 L0L Actuator, MV meas", "SHYB-97 L0L Actuator other status data 01", "SHYB-97 L0L Actuator, ref 2V", "SHYB-97 L0L Actuator, diag. temp. measure", "SHYB-97 L0L Actuator choke position (motor turns)", "SHYB-97 L0L Actuator choke position (%)", "SHYB-97 L0L Actuator speed", "SHYB-97 L0L Actuator status", "SHYB-97 L0L Capacitance", "SHYB-97 L0L Valve needs calibration", "SHYB-97 L0L [ 1] COUNT_ALL_WNLITE_BAD_RX_MESSAGES", "SHYB-97 L0L Communication status", "SHYB-97 L0L Calibrated Venturi delta P", "SHYB-97 L0L Alpha parameter for delta p computing", "SHYB-97 L0L Deviation capacitance", "SHYB-97 L0L Measured capacitance in 100% oil", "SHYB-97 L0L Flow rate", "SHYB-97 L0L Fluid mixture viscosity at DH conditions", "SHYB-97 L0L Oil viscosity at DH conditions", "SHYB-97 L0L Water viscosity at DH conditions", "SHYB-97 L0L Reynold's number", "SHYB-97 L0L Fluid mixture density at DH conditions", "SHYB-97 L0L Oil density at DH conditions", "SHYB-97 L0L Water density at DH conditions", "SHYB-97 L0L Water rate", "SHYB-97 L0L Water fraction", "SHYB-97 L0L Water fraction (%)", "SHYB-97 L0L Actuator state", "SHYB-97 L0L Power supply state", "SHYB-97 L0L Sensor state", "SHYB-97 L0L WNLI001 state", "SHYB-97 L0L Power supply, 15V_Meas", "SHYB-97 L0L Power supply, Agnd", "SHYB-97 L0L Power supply, comm. last error type", "SHYB-97 L0L Power supply, comm. number of error", "SHYB-97 L0L Power supply, DC_Bus_Test", "SHYB-97 L0L Power supply firmware flags 01", "SHYB-97 L0L Power supply firmware flags 02", "SHYB-97 L0L Power supply statuses Firmware states 01", "SHYB-97 L0L Power supply statuses Firmware states 02", "SHYB-97 L0L Power supply statuses Firmware states 03", "SHYB-97 L0L Power supply statuses Firmware states 04", "SHYB-97 L0L Power supply, I_Flyback", "SHYB-97 L0L Power supply, I_Head", "SHYB-97 L0L Power supply, Ref_2V", "SHYB-97 L0L Power supply, REF1_2V5", "SHYB-97 L0L Power supply, V_Head", "SHYB-97 L0L Power supply status", "SHYB-97 L0L Raw capacitance (Xia)", "SHYB-97 L0L Sensor, comm. number of error", "SHYB-97 L0L Sensor, chip temperature", "SHYB-97 L0L Sensor, head current", "SHYB-97 L0L Sensor, head voltage", "SHYB-97 L0L [ 1] Cumulative error count on gauge 1", "SHYB-97 L0L [ 3] Cumulative error count on gauge 2", "SHYB-97 L0L Phase angle", "SHYB-97 L0L Sensor #1 pressure", "SHYB-97 L0L Sensor #2 pressure", "SHYB-97 L0L Sensor status", "SHYB-97 L0L Sensor #1 temperature", "SHYB-97 L0L Sensor #2 temperature", "SHYB-97 L0L Tool alarm status", "SHYB-97 L0L WNLI001, WellNet current", "SHYB-97 L0L WNLI001, WellNet voltage", "SHYB-97 L0L WNLI001 status"};


    public static void writeCsvFile(String fileName) {

        //Create new students objects
        //new RawDataPojo("1", "Ahmed", "Mohamed", "M", "25");
        RawDataPojo student1 = new RawDataPojo("1", "Ahmed", "Mohamed", "M", "25");
        RawDataPojo student2 = new RawDataPojo("2", "Sara", "Said", "F", "23");
        RawDataPojo student3 = new RawDataPojo("3", "Ali", "Hassan", "M", "24");
        RawDataPojo student4 = new RawDataPojo("4", "Sama", "Karim", "F", "20");
        RawDataPojo student5 = new RawDataPojo("5", "Khaled", "Mohamed", "M", "22");
        RawDataPojo student6 = new RawDataPojo("6", "Ghada", "Sarhan", "F", "21");

        //Create a new list of student objects
        List<RawDataPojo> students = new ArrayList();
        students.add(student1);
        students.add(student2);
        students.add(student3);
        students.add(student4);
        students.add(student5);
        students.add(student6);

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
            for (RawDataPojo student : students) {
                List studentDataRecord = new ArrayList();
                studentDataRecord.add(String.valueOf(student.getId()));
                studentDataRecord.add(student.getFirstName());
                studentDataRecord.add(student.getLastName());
                studentDataRecord.add(student.getGender());
                studentDataRecord.add(String.valueOf(student.getAge()));
                csvFilePrinter.printRecord(studentDataRecord);
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