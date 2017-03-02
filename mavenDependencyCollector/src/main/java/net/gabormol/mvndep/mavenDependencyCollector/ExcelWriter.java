package net.gabormol.mvndep.mavenDependencyCollector;

import java.io.File;
import java.io.FileOutputStream;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.Month;
import java.time.Year;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.stream.Collectors;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import net.gabormol.mvndep.mavenDependencyCollector.model.MvnDep;

public class ExcelWriter 
{
    public ExcelWriter ()
    {
    }
    
    public void createExcel(List<MvnDep> anInputData){
        //Blank workbook
        XSSFWorkbook workbook = new XSSFWorkbook(); 
         
        //Create a blank sheet
        XSSFSheet sheet = workbook.createSheet("All top level dependencies");
          
        //This data needs to be written (Object[])
        Map<String, Object[]> data = new TreeMap<String, Object[]>();
        data.put("1", new Object[] {"artifactId", "groupID", "version", "scope", "project's artifactId"});
        int rowId = 2;
        for (MvnDep dep : anInputData){
        	data.put(new Integer(rowId).toString(), new Object[] 
        			{dep.getArtifact(), dep.getGroupId(), dep.getVersion(), dep.getScope(), dep.getProjectArtifactId()});
        	rowId++;
        }
          
        //Iterate over data and write to sheet
        Set<String> keyset = data.keySet();
        //There's a little bug in this, because the key set looks like [1, 10, 11, 12, 2, 3...]
        //So we will convert the Set<String> to List<String>, but ordered by int value
        List<String> keySetUnchanged = keyset
        		.stream()
        		.map(s->Integer.parseInt(s))
        		.collect(Collectors.toSet())
        		.stream()
        		.map(i->String.valueOf(i))
        		.collect(Collectors.toList());
        int rownum = 0;
        for (String key : keySetUnchanged)
        {
            Row row = sheet.createRow(rownum++);
            Object [] objArr = data.get(key);
            int cellnum = 0;
            for (Object obj : objArr)
            {
               Cell cell = row.createCell(cellnum++);
               if(obj instanceof String)
                    cell.setCellValue((String)obj);
                else if(obj instanceof Integer)
                    cell.setCellValue((Integer)obj);
            }
        }
        try
        {
        	DateTimeFormatter formatter =
                    DateTimeFormatter.ofPattern("yyyy-MM-dd_HH-mm-ss");
        	
        	String datetimeString = LocalDateTime.now().format(formatter); 
            
            String xlsFileName = new String("toplevelDependencies_" + datetimeString + ".xlsx");
        	
            //Write the workbook in file system
            FileOutputStream out = new FileOutputStream(new File(xlsFileName));
            workbook.write(out);
            out.close();
            System.out.println("\n" + xlsFileName + " written successfully on disk.");
        } 
        catch (Exception e) 
        {
            e.printStackTrace();
        }
    }
}