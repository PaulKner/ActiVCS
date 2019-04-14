package reader;

import java.io.Closeable;
import java.io.IOException;
import java.util.List;

/**
 * Written by Saimir Bala 
 * Source repository: https://github.com/s41m1r/MiningVCS/tree/VisualizationBranch/MiningSVN
 * Used to import GIT log files into Java class structure. 
 */
public interface LogReader<LogEntry> extends Closeable{	
	/**
	 * @return all the log entries
	 * @throws IOException
	 */
	public List<LogEntry> readAll() throws IOException;
	
	/**
	 * @return the next LogEntry, if there is one
	 * @throws IOException
	 */
	public LogEntry readNext() throws IOException;
	
}
