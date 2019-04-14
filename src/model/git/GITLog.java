/**
 * 
 */
package model.git;

import java.util.Collection;
import java.util.List;

import model.Log;
import model.LogEntry;

/**
 * Written by Saimir Bala 
 * Source repository: https://github.com/s41m1r/MiningVCS/tree/VisualizationBranch/MiningSVN
 * Used to import GIT log files into Java class structure. 
 */

public class GITLog extends Log {
	/**
	 * @param readAll
	 */
   public GITLog(List<LogEntry> list) {
	   this.entries = list;
	   MODIFIED = "M";
	   ADDED = "A";
	   DELETED = "D";
   }

	@Override
	public Collection<LogEntry> getAllEntries() {
		// TODO Auto-generated method stub
		return super.getAllEntries();
	}

	@Override
	public int size() {
		// TODO Auto-generated method stub
		return super.size();
	}

	@Override
	public String toString() {
		// TODO Auto-generated method stub
		return super.toString();
	}

}
