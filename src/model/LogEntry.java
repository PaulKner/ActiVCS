package model;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.List;

import org.joda.time.DateTime;


/**
 * Based on a solution by Saimir Bala Source repository:
 * https://github.com/s41m1r/MiningVCS/tree/VisualizationBranch/MiningSVN
 * Altered the class to conform to the newly created EnrichedChanges
 */

public abstract class LogEntry implements Serializable {
	protected String startingToken;
	protected String author;
	protected DateTime date;
	protected String comment;
	protected List<ChangeTemplate> changeList; // changed, modified, added, etc

	public String getStartingToken() {
		return startingToken;
	}

	public void setStartingToken(String startingToken) {
		this.startingToken = startingToken;
	}

	public String getAuthor() {
		return author;
	}

	public void setAuthor(String author) {
		this.author = author;
	}

	public DateTime getDate() {
		return date;
	}

	public void setDate(DateTime date) {
		this.date = date;
	}

	public String getComment() {
		return comment;
	}

	public void setComment(String comment) {
		this.comment = comment;
	}

	public List<ChangeTemplate> getChangeList() {
		return changeList;
	}

	public void setChangeList(List<ChangeTemplate> changeList) {
		this.changeList = changeList;
	}

	@Override
   public String toString() {
		SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.YYYY HH:mm:ss");
		try { 
		return "Author=" + author
	         + "\nDate=" + sdf.format(date.toDate()) + " - type: "+getType() + "\ncomment=" + comment +" Changes: " + changeList;
		} catch(Exception e) {
			e.printStackTrace();
			return "Could not parse date ";
		}
	}

	/**
	 * Returns the type of the logfile by calculating a majorityvote on changes that happend during the commit
	 * 
	*/
	public String getType() {
		String s ="unknown" ;
		HashMap<String, Integer> majorityVote = new HashMap<String, Integer>();
		Integer cnt;
		//int changecount = 0;
 		if (changeList.size() > 0) {
			for (ChangeTemplate c : changeList) {
				//changecount++;
				try {
				s = ((EnrichedChange) c).getActivityLabel();
				} catch(Exception e) {
					System.err.println("Could not convert from Change to EnrichedChange in LogEntry getType");
				}
				cnt = majorityVote.get(s);
				if(cnt == null) {
					cnt = 0;
				}
				cnt ++;
				majorityVote.put(s,cnt);
				//System.out.println(s+ " " + c.getPath());
				
			}
			//System.out.println("NR changes: "+ changecount);
		}
		int max = -1;
 		for(String type : majorityVote.keySet()) {
			if(majorityVote.get(type) > max) {
				max = majorityVote.get(type);
				s = type;
			}
		}
 		//System.out.println("MAJORITY VOTE FOR TYPE: " +s + "   - Because of nr of changes: " + max);
 		return s;
	}
}
