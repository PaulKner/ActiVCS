package miner;

import java.util.List;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.joda.time.DateTime;
import org.junit.jupiter.api.Test;

import junit.framework.Assert;
import model.Change;
import model.ChangeTemplate;
import model.EnrichedChange;
import model.Log;
import model.LogEntry;
import model.git.GITLog;
import model.git.GITLogEntry;
import reader.CSVReader;
public class ActivityIdentifier {
	private CSVReader csvRead;
	private ArrayList<String[]> expressions;

	
	public ActivityIdentifier(String csvPath) {
		csvRead = new CSVReader();
		expressions = csvRead.readFile(csvPath);

	}
	
	
	
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
					System.out.println("Found value: " + mat.group(0) + " - IN: " + line[0] + " - REGEX: " + line[i]);
					result = line[0];
					
					
				}
			}
			
		}
		//System.out.println("ACTUAL RESULT: " + result);
		
		EnrichedChange eC = new EnrichedChange(c.getAction(), c.getPath(), result);
		return eC;
	}
	
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
