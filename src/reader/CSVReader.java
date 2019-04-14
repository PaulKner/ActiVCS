package reader;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

/**
 * 
 * @author Paul Kneringer
 * Reads a CSV file with the method ReadFile() and returns it in ArrayList<String[]> format
 * with the String[] representing the split up values per row. 
 */
public class CSVReader {
	private BufferedReader br;
	private String delimiter;
	private String line;
	private ArrayList<String[]> expressions;
	
	/**
	 * Default Constructor
	 * Sets delimiter to ','
	 */
	public CSVReader() {
		delimiter = ",";
		line = "";
		expressions = new ArrayList<String[]>();
	}

	/**
	 * Read a CSV file (if it exists) and return the content in structured format 
	 * @param path: A String that contains the location of the CSV file
	 * @return an ArrayList containing string arrays that contain the data of the file
	 */
	public ArrayList<String[]> readFile(String path) {
		try {
			br = new BufferedReader(new FileReader(path));
			String[] x;
			while((line = br.readLine()) != null) {
				x = line.split(delimiter);
				if(x.length > 0) {
					expressions.add(x);	
				}			
			}
			br.close();
		} catch(IOException  e) {
			e.printStackTrace();
		}
		return expressions;
	}
	
	public String getDelimiter() {
		return delimiter;
	}
	
	//Allows to change the delimiter used in CSV files. 
	public void setDelimiter(String delimiter) {
		this.delimiter = delimiter;
	}
}
