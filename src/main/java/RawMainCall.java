import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class RawMainCall {

    public static void main(String[] args) {
//        String ipPath = "C:\\Manara-raw-data\\";
//        String opPath = "C:\\Manara-raw-data\\processed-data\\";

        String ipPath = "C:\\Manara-raw-data\\SHYB-97 Raw Data\\Manara Stations\\";
        String opPath = "C:\\Manara-raw-data\\SHYB-97 Raw Data\\Manara Stations Processed\\";

        /*
        String fileName = "C:\\Manara-raw-data\\L0L 20170228 To 20170302.csv";
        String fileNameOut = "C:\\Manara-raw-data\\L0L 20170228 To 20170302_Preprocessed.csv";
        String wellMapping = "C:\\Manara-raw-data\\WellMapping.csv";
        */
        //Code for read from csv file
        System.out.println("\n Preprocess started :");
//        RawFileReader.readNWriteCsvFile(fileName, fileNameOut);

//        RawFileReader.getAllFiles(ipPath, opPath);

//        RawFileReader.readFromExcel();


//        RawFileReader.moveFilesAfterPreprocessing();

    }

/*
        MappingClass wellCSV = new MappingClass();
        wellCSV.put("Well1", 1);
        wellCSV.put("Well2", 2);
        wellCSV.put("Well3", 2);

        // using for-each loop for iteration over Map.entrySet()
        for (Map.Entry<String, Integer> entry : wellCSV.entrySet())
            System.out.println("Key = " + entry.getKey() + ", Value = " + entry.getValue());
            */

//        RawFileReader.makeNewHeader();

//        CsvFileReader.readNWriteCsvFile(fileName);

/*
        System.out.println("--------------------------------------------------");

        String s12 = "SHYB-97 L0L Fluid mixture density at DH conditions";
        String s1 = "SHYB-97 L0L [ 1] COUNT_ALL_WNLITE_BAD_RX_MESSAGES";

        int tt = 1 + s1.indexOf(" ", s1.indexOf(" ") + 1);
        System.out.println("print : " + s1.substring(0, tt));
        System.out.println("print : " + s1.substring(tt));

        System.out.println("--------------------------------------------------");
        String[] wordss = s1.substring(0, tt).split(" ");
        for (String a : wordss) {
            System.out.println(":" + a + ":");
        }


        System.out.println("--------------------------------------------------");

*/


//        RawFileReader.createProcessedFile(fileNameOut, RawFileReader.readNWriteCsvFile(fileName,fileNameOut));


//        RawFileReader.readNWriteCsvFile(fileName,fileNameOut);
//        RawFileReader.makeNewHeader();

//        RawFileReader.createProcessedFile();

//            public static List<RawFilePojo> readNWriteCsvFile(String fileName,String fileNameout)

//            public static void createProcessedFile(fileNameout, readNWriteCsvFile(fileName,fileNameout))


//      Code for write into csv
//        System.out.println("Write CSV file:");
//        RawFileWriter.createProcessedFile(fileNameOut);
}