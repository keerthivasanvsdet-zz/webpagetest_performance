package utilities;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.NonReadableChannelException;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import wrappers.CommonApiWrappers;

public class DataInputProvider extends CommonApiWrappers {
	public String dataSheetName;

	public String[][] getSheet(String excelName,String dataSheetName) {	

		String[][] data = null;
		
		String dataType=System.getProperty("DataType");
		String folderName="";
		String lessData=System.getProperty("LessData");
		String normalData=System.getProperty("NormalData");
		String moreData=System.getProperty("MoreData");
		
		if(dataType.equalsIgnoreCase("LessData")){
			folderName=lessData;
		}else if(dataType.equalsIgnoreCase("NormalData")){
			folderName=normalData;
		}else if(dataType.equalsIgnoreCase("MoreData")){
			folderName=moreData;
			System.out.println(folderName);
		}

		try {
			FileInputStream fis = new FileInputStream(new File("./"+folderName+"/"+excelName+".xlsx"));
			@SuppressWarnings("resource")
			XSSFWorkbook workbook = new XSSFWorkbook(fis);
			XSSFSheet sheet = workbook.getSheet(dataSheetName);	
			
			// get the number of rows
			int rowCount = sheet.getLastRowNum();

			// get the number of columns
			int columnCount = sheet.getRow(0).getLastCellNum();
			data = new String[rowCount][columnCount];

			// loop through the rows
			for(int i=1; i <rowCount+1; i++){
				try {
					XSSFRow row = sheet.getRow(i);
					for(int j=0; j <columnCount; j++){ // loop through the columns
						try {
							String cellValue = "";
							try{
								
								if(row.getCell(j).getCellType()==Cell.CELL_TYPE_STRING){
									cellValue = row.getCell(j).getStringCellValue();
								}else if(row.getCell(j).getCellType()==Cell.CELL_TYPE_NUMERIC){
									row.getCell(j).setCellType(Cell.CELL_TYPE_STRING);
									cellValue = row.getCell(j).getStringCellValue();									
								}else{
									cellValue = "";
								}
								System.out.println(cellValue);
							}catch(NullPointerException e){

							}

							data[i-1][j]  = cellValue; // add to the data array
						} catch (NonReadableChannelException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}				
					}

				} catch (NonReadableChannelException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			//workbook.close();
			fis.close();

		} catch (FileNotFoundException e) {
			 Reporter.reportStep("File not found", "Fail");
			e.printStackTrace();
		} catch (IOException e) {
			Reporter.reportStep("File not found", "Fail");
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return data;

	}

	/*public DataInputProvider(String dataSheetName) {
		try {
			FileInputStream fis = new FileInputStream(new File("./data/"+dataSheetName+".xlsx"));
			XSSFWorkbook workbook = new XSSFWorkbook(fis);
			@SuppressWarnings("unused")
			XSSFSheet sheet = workbook.getSheetAt(0);	
			workbook.close();
		}
		catch(Exception e){
			e.printStackTrace();
		}
	}*/
	
	public static String[] getSheets(String dataSheetName,int columnNo) {		

		String[] data = null;

		try {
			FileInputStream fis = new FileInputStream(new File("./data/"+dataSheetName+".xlsx"));
			XSSFWorkbook workbook = new XSSFWorkbook(fis);
			XSSFSheet sheet = workbook.getSheetAt(0);	

			// get the number of rows
			int rowCount = sheet.getLastRowNum();

			// get the number of columns
			//int columnCount = sheet.getRow(0).getLastCellNum();
			data = new String[rowCount];


			// loop through the rows
			for(int i=1; i <rowCount+1; i++){
				try {
					XSSFRow row = sheet.getRow(i);
					
							String cellValue = "";
							try{
								cellValue = row.getCell(columnNo).getStringCellValue();
							}catch(NullPointerException e){

							}

							data[i-1]  = cellValue; // add to the data array
									
					}

				 catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			workbook.close();
			fis.close();

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return data;

	}
	
	public static String[] getSheet(String dataSheetName,int columnNo,int rowno) {		

		String[] data = null;

		try {
			FileInputStream fis = new FileInputStream(new File("./data/"+dataSheetName+".xlsx"));
			@SuppressWarnings("resource")
			XSSFWorkbook workbook = new XSSFWorkbook(fis);
			XSSFSheet sheet = workbook.getSheetAt(0);	

			// get the number of rows
			int rowCount = sheet.getLastRowNum();

			// get the number of columns
			//int columnCount = sheet.getRow(0).getLastCellNum();
			data = new String[rowCount];


			// loop through the rows
			//for(int i=1; i <rowCount+1; i++){
				try {
					XSSFRow row = sheet.getRow(rowno);
					
							String cellValue = "";
							try{
								cellValue = row.getCell(columnNo).getStringCellValue();
							}catch(NullPointerException e){

							}

							data[rowno-1]  = cellValue; // add to the data array
									
					}

				 catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			
			fis.close();

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return data;

	}
	
	
	public  String[][] writeInExcel(String dataSheetName,int dataSheetIndex,String outputName) {	

		String[][] data = null;

		try {
			FileInputStream fis = new FileInputStream(new File("./data/"+dataSheetName+".xlsx"));
			@SuppressWarnings("resource")
			XSSFWorkbook workbook = new XSSFWorkbook(fis);
			XSSFSheet sheet = workbook.getSheetAt(dataSheetIndex);	

			// get the number of rows
			int rowCount = sheet.getLastRowNum();

			// get the number of columns
			int columnCount = sheet.getRow(0).getLastCellNum();
			data = new String[rowCount][columnCount];


			// loop through the rows
			for(int i=1; i <rowCount+1; i++){
				try {
					XSSFRow row = sheet.getRow(i);
					for(int j=0; j <columnCount; j++){ // loop through the columns
						try {
							String cellValue = "";
							try{
								cellValue = row.getCell(j).getStringCellValue();
							}catch(NullPointerException e){

							}

							data[i-1][j]  = cellValue; // add to the data array
						} catch (Exception e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}				
					}

				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			
			fis.close();
	
			XSSFSheet shee =workbook.createSheet(outputName);
			XSSFRow crow=shee.createRow(0);
			XSSFCell cCell=crow.createCell(1);
			cCell.setCellValue("Pass");
			
			//workbook.close();
			
			FileOutputStream out=new FileOutputStream(new File("./data/"+dataSheetName+".xlsx"));
			workbook.write(out);
			out.close();
			

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return data;

	}
	
	
	public void createSheetAndWriteData(String dataSheetName,String sheetName,String[] details,int no) {
		
		@SuppressWarnings("resource")
		XSSFWorkbook wrkbk= new XSSFWorkbook();
		XSSFSheet sheet2 =wrkbk.createSheet(sheetName);
	
		for (int i = 0; i <no; i++)
		{
			XSSFRow rw=sheet2.createRow(i);
			for (int j= 0; j <3; j++){
				XSSFCell cell1=rw.createCell(j);
				cell1.setCellValue(details[j]);
				try{
				FileOutputStream fos=new FileOutputStream(new File("./listingData/"+dataSheetName+".xlsx"));
				wrkbk.write(fos);
				fos.close();
				}
				catch(IOException e){
					e.printStackTrace();
				}

			}
		}	  
		//wrkbk.close();
	}

}