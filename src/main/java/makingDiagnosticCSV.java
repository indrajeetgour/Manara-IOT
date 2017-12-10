import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVPrinter;
import org.apache.commons.csv.CSVRecord;

import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class makingDiagnosticCSV {
    /*
   * method to read the csv file
   * and return the file records as list
   * */
    public static List readCsvFile(String fileName, String[] header) {
        FileReader fileReader = null;
        CSVParser csvFileParser = null;
        List csvRecords = null;
        try {
            //initialize FileReader object
            fileReader = new FileReader(fileName);
            //initialize CSVParser object
            csvFileParser = new CSVParser(fileReader, CSVFormat.DEFAULT.withHeader(header));
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

    public static void writeIntoMappingCSV(String fileOut, List allRecords, String[] header) {
        FileWriter fileWriter = null;
        CSVPrinter csvFilePrinter = null;
        try {
            String[] headerList = null;
            fileWriter = new FileWriter(fileOut);
            csvFilePrinter = new CSVPrinter(fileWriter, CSVFormat.DEFAULT.withHeader(header));
            for (int i = 1; i < allRecords.size(); i++) {
                CSVRecord raw = (CSVRecord) allRecords.get(i);

                List dataRecord = new ArrayList();
/*                if (raw.get(28).contains("Production")) {
                    System.out.println("Found production data!!!");
                    continue;
                } else {
                    dataRecord.addAll(Arrays.asList(raw.get(3), raw.get(4), raw.get(28)));
                }*/
                if (raw.get(28).contains("Diagnostic")) {
//                    dataRecord.addAll(Arrays.asList(raw.get(3), raw.get(4), raw.get(28)));
                    dataRecord.addAll(Arrays.asList(raw.get(3), raw.get(4)));
                    csvFilePrinter.printRecord(dataRecord);

                }
//                dataRecord.addAll(Arrays.asList(headerList));

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

    public static void main(String[] args) {

        String[] FILE_HEADER_RAW = {"Scope", "Type", "In use", "Tag ID", "Description", "Device mapping", "EGU low", "EGU high", "EGU units", "Alarming", "Alarming: LOLO", "Alarming: LO", "Alarming: HI", "Alarming: HIHI", "Alarming: Deadband", "Archiving", "Archiving: Interval (ms)", "Archiving, compr", "Archiving, compr: Deadband", "Archiving, compr: timeout (ms)", "Data analyst", "FW/Elec. engineer", "Field engineer", "Physician", "WWA", "Surface Unit Diagnostic", "Expert", "Critical Parameter", "Catalog", "DB_Name", "DB_TagNameId"};

//        String[] FILE_HEADER_FINAL = {"Tag ID", "Description", "Catalog"};
        String[] FILE_HEADER_FINAL = {"Tag ID", "Description"};
        String fileOut = "C:\\Manara-raw-data\\Manara_SRS007_1_9_RTAC_public_measurements_definition_abstract.csv";
        String fileIn = "C:\\Manara-raw-data\\Manara_SRS007_1_9_RTAC_public_measurements_definition.csv";


        List allData = readCsvFile(fileIn, FILE_HEADER_RAW);

        writeIntoMappingCSV(fileOut, allData, FILE_HEADER_FINAL);

    }
}
