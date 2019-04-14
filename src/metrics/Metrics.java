package metrics;

import java.util.ArrayList;
import java.util.HashMap;

import model.ChangeTemplate;
import model.EnrichedChange;
import model.Log;
import model.LogEntry;
 
public class Metrics {
	// APTW(p, a,t) = number of touches to files of activity type t by author a for
	// project p over its entire history. - Vasilescu et al.
	// APT I that determines for project p if an author a has been involved in at
	// least one
	// i.e., has touched at least one file of) activity type t:
	// APT I(p, a,t) =

	// 1, if APTW(p, a,t) > 0;
	// 0, otherwise.
	private Log log;

	public Metrics(Log log) {
		this.log = log;
	}

	public boolean APTI(String author, String type, HashMap<String, HashMap<String, Integer>> ATW) {
		boolean ret = false;
		Integer val = 0;
		if (ATW.containsKey(author)) {
			if (ATW.get(author).containsKey(type)) {
				val = ATW.get(author).get(type);
				if (val > 0) {
					ret = true;
				}
			}
		}
		return ret;
	}

	// Synonym with TW because only one project/log file can be created and used as
	// input in the software
	public int PTW(String type, HashMap<String, HashMap<String, Integer>> ATW) {
		int sum = 0;
		for (String author : getAuthors()) {
			if (APTI(author, type, ATW)) {
				sum += ATW.get(author).get(type);
			}
		}
		return sum;
	}

	public int PW(HashMap<String, HashMap<String, Integer>> ATW) {
		int ret = 0;
		for (String author : ATW.keySet()) {
			for (int i : ATW.get(author).values()) {
				ret += i;
			}
		}
		return ret;
	}

	public int ATPW(String author, String type, HashMap<String, HashMap<String, Integer>> ATW) {
		int ret = 0;
		if (ATW.containsKey(author))
			if (ATW.get(author).containsKey(type))
				ret = ATW.get(author).get(type);

		return ret;
	}

	public double RPTW(String type, HashMap<String, HashMap<String, Integer>> ATW) {
		double ret = 0.0;
		ret = ((double) PTW(type, ATW)) / ((double) PW(ATW));

		return ret;
	}

	public double PWS(HashMap<String, HashMap<String, Integer>> ATW) {
		double ret = 0.0;
		int i = 0;
		Integer[] PTWList = new Integer[getTypes().size()];
		for (String type : getTypes()) {
			PTWList[i] = PTW(type, ATW);
			i++;
		}
		ret = Gini.compute(PTWList, false);
		return ret;
	}

	public double RPWS(HashMap<String, HashMap<String, Integer>> ATW) {
		double ret = 0.0;
		int i = 0;
		Double[] RPTWList = new Double[getTypes().size()];
		for (String type : getTypes()) {
			RPTWList[i] = RPTW(type, ATW);
			i++;
		}
		ret = Gini.compute(RPTWList, false);
		return ret;
	}

	public int PTI(String type, HashMap<String, HashMap<String, Integer>> ATW) {
		int ret = 0;
		for (String author : getAuthors()) {
			if (APTI(author, type, ATW)) {
				ret += 1;
			}
		}
		return ret;
	}

	// Implementation of NTP is different in the paper, but I already implemented
	// the required base functionality
	public int NTP() {
		return getTypes().size();
	}

	// Implementation of NAP is different in the paper, but I already implemented
	// the required base functionality
	public int NAP() {
		return getAuthors().size();
	}

	// Relative involvement of authors on type (e.g. 5 out of 10 authors were
	// coding: 50%)
	public double RPTI(String type, HashMap<String, HashMap<String, Integer>> ATW) {
		double ret = 0.0;
		ret = (double) PTI(type, ATW) / (double) NAP();
		return ret;
	}

	// Specialisation of involvement GINI over list of all involvement numbers ("How
	// much do my author participations vary across types")
	public double PIS(HashMap<String, HashMap<String, Integer>> ATW) {
		double ret = 0.0;
		Integer[] list = new Integer[getTypes().size()];
		int i = 0;
		for (String type : getTypes()) {
			list[i] = PTI(type, ATW);
			i++;
		}
		ret = Gini.compute(list, false);
		return ret;
	}

	public double RPIS(HashMap<String, HashMap<String, Integer>> ATW) {
		double ret = 0.0;
		Double[] list = new Double[getTypes().size()];
		int i = 0;
		for (String type : getTypes()) {
			list[i] = RPTI(type, ATW);
			i++;
		}
		ret = Gini.compute(list, false);
		return ret;
	}

	/**
	 * Creates a structure that contains the ATW value of every author of the project.
	 * Structure: Author, Type, nr. of changes
	 * @return Hashmap storing the Author and another HashMap storing type and number of file changes of that type
	 */
	public HashMap<String, HashMap<String, Integer>> getATW() {
		HashMap<String, HashMap<String, Integer>> ATW = new HashMap<String, HashMap<String, Integer>>();
		if (log != null) {

			HashMap<String, Integer> innerList = new HashMap<String, Integer>();
			Integer currVal = 0;
			String author;
			String type;
			for (LogEntry l : log.getAllEntries()) {
				// System.out.println(l);
				author = l.getAuthor();
				for (ChangeTemplate c : l.getChangeList()) {
					type = ((EnrichedChange) c).getActivityLabel();
					if (ATW.containsKey(author)) {
						innerList = ATW.get(author);
					} else {
						innerList = new HashMap<String, Integer>();
					}
					if (innerList.containsKey(type)) {
						currVal = innerList.get(type);
					} else {
						currVal = 0;
					}
					currVal += 1;
					innerList.put(type, currVal);
					ATW.put(author, innerList);
				}
			}
		}
		return ATW;
	}

	private ArrayList<String> getAuthors() {
		ArrayList<String> authors = new ArrayList<String>();
		if (log != null) {
			for (LogEntry l : log.getAllEntries()) {
				if (authors.contains(l.getAuthor()) == false) {
					authors.add(l.getAuthor());
				}
			}
		}
		return authors;
	}
	
	private ArrayList<String> getTypes() {
		ArrayList<String> categories = new ArrayList<String>();
		if (log != null) {
			for (LogEntry l : log.getAllEntries()) {
				if (!categories.contains(l.getType())) {
					categories.add(l.getType());
					// if (l.getType() == "")
					// System.out.println("yi");
				}
			}
			// laneHeight = (int) (canvas.getHeight() / map.keySet().size());
			// laneHeight = (int)(getHeight()*boxHeightScaling) / map.keySet().size();
			// System.out.println("Numvber of different things: " + map.keySet().size() + "
			// - HEIGHT: " + laneHeight);
		}
		return categories;
	}
	/**
	 * // APTW(p, a,t) = number of touches to files of activity type t by author a
	 * for // project p over its entire history. - Vasilescu et al. // APT I that
	 * determines for project p if an author a has been involved in at // least one
	 * // i.e., has touched at least one file of) activity type t: // APT I(p, a,t)
	 * =
	 * 
	 * // 1, if APTW(p, a,t) > 0; // 0, otherwise.
	 * 
	 * public static boolean APTI(String author, String type, HashMap<String,
	 * HashMap<String, Integer>> ATW) { boolean ret = false; Integer val = 0; if
	 * (ATW.containsKey(author)) { if (ATW.get(author).containsKey(type)) { val =
	 * ATW.get(author).get(type); if (val > 0) { ret = true; } } } return ret; }
	 * 
	 * // Synonym with TW because only one project/log file can be created and used
	 * as // input in the software public static int PTW(String type,
	 * HashMap<String, HashMap<String, Integer>> ATW) { int sum = 0; for (String
	 * author : getAuthors()) { if (APTI(author, type, ATW)) { sum +=
	 * ATW.get(author).get(type); } } return sum; }
	 * 
	 * public static int PW(HashMap<String, HashMap<String, Integer>> ATW) { int ret
	 * = 0; for (String author : ATW.keySet()) { for (int i :
	 * ATW.get(author).values()) { ret += i; } } return ret; }
	 * 
	 * public static int ATPW(String author, String type, HashMap<String,
	 * HashMap<String, Integer>> ATW) { int ret = 0; if (ATW.containsKey(author)) if
	 * (ATW.get(author).containsKey(type)) ret = ATW.get(author).get(type);
	 * 
	 * return ret; }
	 * 
	 * public static double RPTW(String type, HashMap<String, HashMap<String,
	 * Integer>> ATW) { double ret = 0.0; ret = ((double) PTW(type, ATW)) /
	 * ((double) PW(ATW));
	 * 
	 * return ret; }
	 * 
	 * public static double PWS(HashMap<String, HashMap<String, Integer>> ATW) {
	 * double ret = 0.0; int i = 0; Integer[] PTWList = new
	 * Integer[getTypes().size()]; for (String type : getTypes()) { PTWList[i] =
	 * PTW(type, ATW); i++; } ret = Gini.compute(PTWList, false); return ret; }
	 * 
	 * public static double RPWS(HashMap<String, HashMap<String, Integer>> ATW) {
	 * double ret = 0.0; int i = 0; Double[] RPTWList = new
	 * Double[getTypes().size()]; for (String type : getTypes()) { RPTWList[i] =
	 * RPTW(type, ATW); i++; } ret = Gini.compute(RPTWList, false); return ret; }
	 * 
	 * public static int PTI(String type, HashMap<String, HashMap<String, Integer>>
	 * ATW) { int ret = 0; for (String author : getAuthors()) { if (APTI(author,
	 * type, ATW)) { ret += 1; } } return ret; }
	 * 
	 * // Implementation of NTP is different in the paper, but I already implemented
	 * // the required base functionality public static int NTP() { return
	 * getTypes().size(); }
	 * 
	 * // Implementation of NAP is different in the paper, but I already implemented
	 * // the required base functionality public static int NAP() { return
	 * getAuthors().size(); }
	 * 
	 * // Relative involvement of authors on type (e.g. 5 out of 10 authors were //
	 * coding: 50%) public static double RPTI(String type, HashMap<String,
	 * HashMap<String, Integer>> ATW) { double ret = 0.0; ret = (double) PTI(type,
	 * ATW) / (double) NAP(); return ret; }
	 * 
	 * // Specialisation of involvement GINI over list of all involvement numbers
	 * ("How // much do my author participations vary across types") public static
	 * double PIS(HashMap<String, HashMap<String, Integer>> ATW) { double ret = 0.0;
	 * Integer[] list = new Integer[getTypes().size()]; int i = 0; for (String type
	 * : getTypes()) { list[i] = PTI(type, ATW); i++; } ret = Gini.compute(list,
	 * false); return ret; }
	 * 
	 * public static double RPIS(HashMap<String, HashMap<String, Integer>> ATW) {
	 * double ret = 0.0; Double[] list = new Double[getTypes().size()]; int i = 0;
	 * for (String type : getTypes()) { list[i] = RPTI(type, ATW); i++; } ret =
	 * Gini.compute(list, false); return ret; }
	 **/
}
