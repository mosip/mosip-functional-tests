package io.mosip.testrig.apirig.utils;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.opencsv.CSVReader;
import com.opencsv.CSVReaderBuilder;

public class CSVHelper {

	String fileName;
	CSVReader csvReader;
	int recCount;
	FileInputStream inputStream = null;
	InputStreamReader filereader = null;
	
	public int getRecordCount() {
		return recCount;
	}
	public CSVHelper(String csvFile) throws IOException {
		fileName = csvFile;
		
		open();
      
        //get line count
        recCount = 0;
        while (csvReader.readNext() != null) { 
                recCount++;  
        } 
        csvReader.close();
	}
	public void open() throws FileNotFoundException, UnsupportedEncodingException {
		

		try {
			inputStream = new FileInputStream(fileName);		
			filereader = new InputStreamReader(inputStream , StandardCharsets.UTF_8);
			csvReader = new CSVReaderBuilder(filereader)
        								.withSkipLines(1)
        								.build();
		}finally {
			AdminTestUtil.closeInputStream(inputStream);
		}
        
	}
	
	//pass an array of record numbers to read
	 List<String[]> readRecords(int [] recnos) throws IOException{
		
		List<String[]> outList = new ArrayList<String[]>();
		Arrays.sort(recnos);
		
		String [] nextRecord;
		int recno =0;
		
		int i=0;
		
		while((nextRecord = csvReader.readNext()) != null) {
			
			if(i >= recnos.length) break;
			
			if(recno == recnos[i]) {
				i++;
		
				if(nextRecord  != null) { 
					outList.add(nextRecord);
				}
			}
			recno++;
						
			
		}
		return outList;
	}
	 
	public String[] readRecord() throws IOException{
			return csvReader.readNext();
		
	}
	public void close() throws IOException {
		csvReader.close();
		filereader.close();
	}
	public List<String>  readAttribute(int col, int[] recnos) throws IOException{
		List<String> retCols = new ArrayList<String>();
		List<String[]> recs = readRecords( recnos);
		for(String[] r: recs) {
			String val = r[ (col >= r.length) ? 0: col];
			if(val != null)
				val =  toCaptialize(r[0]);
			retCols.add(val);
		}
		return retCols;
	}
	
	public static String toCaptialize(String text) {
		return text.substring(0, 1).toUpperCase() + text.substring(1).toLowerCase();
	}
}
