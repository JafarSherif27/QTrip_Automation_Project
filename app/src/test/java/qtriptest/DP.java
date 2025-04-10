package qtriptest;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.testng.annotations.DataProvider;

public class DP {
    // TODO: use correct annotation to connect the Data Provider with your Test Cases
    @DataProvider(name= "data-provider")
    public Object[][] dpMethod(Method m) throws IOException {
        int rowIndex = 0;
        int cellIndex = 0;
        List<List> outputList = new ArrayList<List>();

        FileInputStream excelFile = new FileInputStream(new File(
                "/home/crio-user/workspace/jafarsherif27-ME_QTRIP_QA_V2/app/src/test/resources/DatasetsforQTrip.xlsx"));
        Workbook workbook = new XSSFWorkbook(excelFile);

        Sheet selectedSheet = workbook.getSheet(m.getName());

        Iterator<Row> iterator = selectedSheet.iterator(); //to iterate rows in xlsx sheet

        //outter while loop for row
        while (iterator.hasNext()) {  //iterator.hasNext() - returns true if the row has next row 
            Row nextRow = iterator.next(); //iterator.next() - returns object of row
            Iterator<Cell> cellIterator = nextRow.cellIterator(); // returns object of cell
            List<String> innerList = new ArrayList<String>();

            while (cellIterator.hasNext()) { //inner while loop for cell
                Cell cell = cellIterator.next();
                if (rowIndex > 0 && cellIndex > 0) { //skip title row and column and add values to list
                    if (cell.getCellType() == CellType.STRING) {
                        innerList.add(cell.getStringCellValue());
                    } else if (cell.getCellType() == CellType.NUMERIC) {
                        innerList.add(String.valueOf(cell.getNumericCellValue())); 
                    }
                }
                cellIndex = cellIndex + 1; //increase cell index after every iteration 
            }

            rowIndex = rowIndex + 1;  // after finishing one row iteration increase count of row index
            cellIndex = 0; //reset the count of cell index - since each time the cell index starts with 0
            
            if (innerList.size() > 0)   //add the inner list to 2d list if it had some value
                outputList.add(innerList);

        }

        excelFile.close(); 
        

        String[][] stringArray = outputList.stream().map(u -> u.toArray(new String[0])).toArray(String[][]::new);

        // //My own implementation to convert 2dlist into 2d array
        // Object [][] strArr = new String[outputList.size()][];

        // int i=0;
        // for(List<Object> str: outputList){
        //     Object[] arr = str.toArray(new String[0]);
        //     strArr[i] = arr;
        //     i++;
        // }
        // System.out.println("MY OBJECT ARRAY INSIDE DP: "+ Arrays.deepToString(strArr));
        // System.out.println("ALREADY IMPLEMENT CODE INSIDE DP: "+ Arrays.deepToString(stringArray));
        // //
        
        workbook.close();
        return stringArray;

    }
}
