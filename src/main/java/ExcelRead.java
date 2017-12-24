import com.monitorjbl.xlsx.StreamingReader;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.*;


public class ExcelRead {
    Map<Integer, String>  readELSXFile(String excelFileLoc) throws IOException {
        List<List> dataRecord = new ArrayList();
        InputStream is = new FileInputStream(new File(excelFileLoc));
        Workbook workbook = StreamingReader.builder()
                .rowCacheSize(100)    // number of rows to keep in memory (defaults to 10)
                .bufferSize(4096)     // buffer size to use when reading InputStream to file (defaults to 1024)
                .open(is);            // InputStream or File for XLSX file (required)
        // TODO: 12/11/2017 handle exception if sheet not present java.lang.NullPointerException

        int rwoC = 0;
        Sheet sheet = workbook.getSheet("WWApp_Tags_to_select");
        System.out.println(sheet.getSheetName());
        Map<Integer, String> tags_to_select = new TreeMap<>();

        for (Row r : sheet) {
            rwoC += 1;
            System.out.println("Row :" + rwoC);
            List<java.lang.String> row = new ArrayList();
            for (Cell c : r) {
                java.lang.String colVal = c.getStringCellValue();
                System.out.println("Colval : " + colVal);
                if (colVal != null || colVal.length() > 0) {
                    row.add(colVal);
                } else
                    row.add("");
            }
            if (row.size() == 31) {
                System.out.println("row have 31 cloumns in size !!");
                if (row.get(0).equals("Manara") && row.get(28).equals("Diagnostic")) {
                    System.out.println("tuple : " + row.get(0) + " | " + row.get(3) + " | " + row.get(4) + " | " + row.get(28) + " | " + row.get(30));
                    tags_to_select.put(Integer.parseInt(row.get(30)), row.get(4));
                    dataRecord.add(Arrays.asList(row.get(0), row.get(3), row.get(4), row.get(28), row.get(30)));
                }
            }
            System.out.println("----------------row end -----------------");
        }
//        TODO: just to test locally remove it on final version
        /*
        for (List<String> al : dataRecord) {
            if (al.get(0).equals("Manara") && al.get(3).equals("Diagnostic")) {
                System.out.println("Print :" + al.toString());
                finalData.add(al);
                tags_to_select.put(Integer.parseInt(al.get(4)), al.get(2));
            } else {
                System.out.println("Not Manra raw");
            }
        }
        */
        Iterator<Map.Entry<Integer, String>> iter = tags_to_select.entrySet().iterator();
        while (iter.hasNext()) {
            Map.Entry<Integer, String> entry = iter.next();
            System.out.println("Map => key : " + entry.getKey() + ", Values : " + entry.getValue());
        }
        System.out.println("Map size : " + tags_to_select.size());
        return tags_to_select;
    }


    public static void main(String[] args) throws IOException {
        String excelFileLoc = "C:\\Manara-raw-data\\mapping-inputs-files\\Manara_SRS007_1_9_RTAC_public_measurements_definition.xlsx";
        new ExcelRead().readELSXFile(excelFileLoc);

    }
}