package miner;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import model.ChangeTemplate;
import model.EnrichedChange;
import model.Log;
import model.LogEntry;
import reader.CSVReader;

/**
 * Class for the allocation of activity labels based on regular expressions
 * @author Paul Kneringer
 *
 */
public class ActivityIdentifier {
	private CSVReader csvRead;
	private ArrayList<String[]> expressions;

	
	public ActivityIdentifier(String csvPath) {
		csvRead = new CSVReader();
		expressions = csvRead.readFile(csvPath);

	}
	
	/**
	 * Receives a log and transforms all Change objects into EnrichedChange objects that contain an acitity label
	 */
	public Log enrichLog(Log log) {
		EnrichedChange eC;

		List<ChangeTemplate> newChangeList;
		for(LogEntry l : log.getAllEntries()) {
			newChangeList = new ArrayList<ChangeTemplate>();
			for(ChangeTemplate c : l.getChangeList()) {
				eC = identifyActivityLabel(c);
				newChangeList.add(eC);
			}
			l.setChangeList(newChangeList);
		}		
		return log;
	}
	
	/**
	 * Identifies the activity type with regular expressions
	 * @param c: Change object that is transformed into an EnrichedChange object
	 */
	public EnrichedChange identifyActivityLabel(ChangeTemplate c) {
		Pattern pat;
		Matcher mat;
		String result = "unknown";

		//Read all regular expressions for the identification of activities 
		for(String[] line : expressions) {
			//System.out.println(line[0]);		
			for(int i = 1; i <= line.length-1; i++) {
				//System.out.println("REGEX: " + line[0]);		
				pat = Pattern.compile("(?i)^"+line[i]+"$");
				mat = pat.matcher(c.getPath());

				if (mat.find()) {
					//System.out.println("Found value: " + mat.group(0) + " - IN: " + line[0] + " - REGEX: " + line[i]);
					result = line[0];		
				}
			}
			
		}
		//System.out.println("ACTUAL RESULT: " + result);
		EnrichedChange eC = new EnrichedChange(c.getAction(), c.getPath(), result);
		return eC;
	}
	
	/**
	 * Returns all identified activity types from the list of regular expressions
	 * @param csvPath: Path to regular expressions
	 * @return ArrayList of activity type labels
	 */
	public static ArrayList<String> getActivityLabels(String csvPath) {
		CSVReader csvRead = new CSVReader();
		ArrayList<String[]> expressions = csvRead.readFile(csvPath);
		ArrayList<String> activityLabels = new ArrayList<String>();
		for(String[] s : expressions) {
			activityLabels.add(s[0]);
		}
		return activityLabels;
	}
}
