import com.google.common.collect.Multimap;

import java.util.HashSet;
import java.util.Set;

public class RawMainCall {

    public static void main(String[] args) throws Exception {
//        Its for SHYB-97 well old data
        String ipPath = "C:\\Manara-raw-data\\SHYB-97 Raw Data\\Manara Stations\\";
        String opPath = "C:\\Manara-raw-data\\SHYB-97 Raw Data\\Manara Stations Processed\\";
//        Its for OP-14 well
        String ipPathOP14 = "C:\\Manara-raw-data\\THM Data OP-14 ENL\\";
        String opPathOP14 = "C:\\Manara-raw-data\\THM Data OP-14 ENL Stations Processed\\";

        System.out.println("\n Preprocess started :");
        long start = System.nanoTime(); // its for get the start time of the process

//        Below code is for old SHYB-97 raw file read
//        RawFileReader.getAllFiles(ipPath, opPath);
//        Below code is for newly formatted OP-14 Well and all the future well

        OP14RawFileReader.getAllFiles(ipPathOP14, opPathOP14);

        long end = System.nanoTime(); // its for get the end point of the process
        double elpsedTime = (end - start) / 1000000000.0;
        System.out.println("Time taken by job: " + elpsedTime); // calculating the total time taken by the method call above in sec


        /*
        Commenting : below code is just for the getting tags from the tagsAlias file as well| commenting out

        String manaraMeasurementFile = "C:\\Manara-raw-data\\mapping-inputs-files\\Manara_SRS007_1_9_RTAC_public_measurements_definition.xlsx";
        Multimap<String, String> manaraMeasurementMultiMap = RawFileReader.readELSXFile(manaraMeasurementFile, "WWApp_Tags_to_select");
        String tagsMappingFile = "C:\\Manara-raw-data\\mapping-inputs-files\\TagNamesAndAliases_updated.xlsx";
        Multimap<String, String> tagsMultiMap = RawFileReader.readELSXFile(tagsMappingFile, "TagNameAliases");

        System.out.println("+++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++");

        Multimap<String, String> allTagsMultiMap = RawFileReader.getOneTagAliasMapping(manaraMeasurementMultiMap, tagsMultiMap);
        Set<String> set = new HashSet<>();
        set.addAll(allTagsMultiMap.values());
        System.out.println("Printing all values of tags from set :");
        for (String r : set) {
            System.out.println(" list print :" + r);
        }
        System.out.println("Printing all values from allTagsMultiMap output : ");
        for (Object key : allTagsMultiMap.keySet()) {
            System.out.println("allTagsMultiMap Key : " + key+ " \n >> Values : " + allTagsMultiMap.get((String) key));
//            System.out.println("allTagsMultiMap Values " + allTagsMultiMap.get((String) key));
        }
        */


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