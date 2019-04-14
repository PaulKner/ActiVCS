package reader;

import java.io.Closeable;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import model.Change;
import model.ChangeTemplate;
import model.LogEntry;
import model.git.GITFileChanges;
import model.git.GITLogEntry;

/**
 * Written by Saimir Bala Source repository:
 * https://github.com/s41m1r/MiningVCS/tree/VisualizationBranch/MiningSVN Used
 * to import GIT log files into Java class structure. Altered some lines to
 * better fit with my project (ChangeTemplate)
 */
public class GITLogReader implements LogReader<LogEntry>, Closeable {
	//possible extension to the software, identifying only A,M,D opperations 
	private boolean onlyFileManipulations = false;
	private RandomAccessFile raf;
//	private BufferedReader br;
	// private int lines = 0;

	// public GITLogReader(BufferedReader reader) {
	// raf = reader;
	// }
	//
	// public GITLogReader(File file) throws FileNotFoundException {
	// raf = new BufferedReader(new FileReader(file));
	// }
	// /**
	// * @throws FileNotFoundException
	// *
	// */
	// public GITLogReader(String fileName) throws FileNotFoundException {
	// raf = new BufferedReader(new FileReader(fileName));
	// }

	/**
	 * @throws IOException
	 * 
	 */
	public GITLogReader(String file) throws IOException {
		raf = new RandomAccessFile(file, "r");
//		br = new BufferedReader(new FileReader(raf.getFD()));
	}

	@Override
	public List<LogEntry> readAll() throws IOException {

		List<LogEntry> logEntries = new ArrayList<LogEntry>();
		LogEntry entry;

		while ((entry = readNext()) != null) {
			
			System.out.println(entry.getStartingToken());
			logEntries.add(entry);
		}
//		System.out.println("last entry = "+logEntries.get(logEntries.size()-1));
		return logEntries;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see reader.LogReader#readNext()
	 */
	@Override
	public LogEntry readNext() throws IOException {
		boolean merge = false;
		String line = raf.readLine();
//		System.out.println("Merge="+merge+" Line:"+line);
		if (line == null)
			return null;

		while (!line.startsWith("commit"))
			line = raf.readLine();

		String revision = line.split("commit ")[1];
		line = raf.readLine();
		if(revision.equals("bd82d779ce116656fc954c8058663bba1b2d5c58"))
			System.out.println("hi");
		if (line.startsWith("Merge")) {
			merge = true;
			line = raf.readLine();
		}

		String author = readAuthor(line);
//		System.out.println("Merge="+merge+" Author:"+line);
		line = raf.readLine().trim();
		String dateString = line.split("Date: ")[1];
		line = raf.readLine();

		String message = readMessage(line);

//		System.out.println("Merge="+merge+" Message:"+message);
		// When a merge happens, GIT automatically generates a message describing it.
		// Plus, there is no change-list to be read.
		List<ChangeTemplate> changeList = merge ? new ArrayList<ChangeTemplate>() : readChangeList();

//		System.out.println("Merge="+merge+" Message:"+message);
//		Locale locale = new Locale("de", "AT", "Austria");
		DateTimeFormatter gitFmt = DateTimeFormat.forPattern("EEE MMM dd HH:mm:ss yyyy Z").withLocale(Locale.ENGLISH);
//		System.out.println(new DateTime().toString(gitFmt));
		DateTime date = gitFmt.parseDateTime(dateString.trim());

		LogEntry gitLogEntry = new GITLogEntry(revision, author, date, message, changeList);
//		System.out.println("Merge="+merge+" Message:"+message);
		return gitLogEntry;
	}

	/**
	 * @param line
	 * @return
	 * @throws IOException
	 */
	private String readAuthor(String line) throws IOException {
		String author = "";

		if (line.equals(""))
			return author;

		if (line.startsWith("Author"))
			author = line.split("Author: ")[1];

		// else if(line.startsWith("Merge"))
		// author = readAuthor(br.readLine());

		return author;
	}

	private List<ChangeTemplate> readChangeList() throws IOException {
		List<ChangeTemplate> changeList = new ArrayList<ChangeTemplate>();
		// read the first line
		long fp = raf.getFilePointer();
		String line = raf.readLine();
		if (line == null)
			return changeList;
		String start = line.trim().split("\\s+")[0];
		/**
		switch (start) {
		case GITFileChanges.ADDED:
			System.out.println("SOMETHING FOUND");
			break;
		case GITFileChanges.MODIFIED:
			System.out.println("SOMETHING FOUND");
			break;
		case GITFileChanges.DELETED:
			System.out.println("SOMETHING FOUND");
			break;

		default:
			// System.out.println("seek");
			raf.seek(fp);
			// System.out.println("seeked.");
			return changeList;
		}
		**/
		while (line != null && !line.equals("")) {
			String[] changeLine = line.trim().split("\\s+");
			
			if(onlyFileManipulations) {
				if(changeLine[0].equals(GITFileChanges.ADDED) || changeLine[0].equals(GITFileChanges.MODIFIED) || changeLine[0].equals(GITFileChanges.DELETED)) {
					Change ch = new Change(changeLine[0].trim(), changeLine[1].trim());
					changeList.add(ch);
				}
			} else {
				Change ch = new Change(changeLine[0].trim(), changeLine[1].trim());
				changeList.add(ch);
			}
			line = raf.readLine();
		}
		return changeList;
	}

	private String readMessage(String line) throws IOException {
		String msg = "";
		line = raf.readLine().trim();
		while (line != null && !line.equals("")) {
			msg += line;
			line = raf.readLine();
		}
		return msg;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.io.Closeable#close()
	 */
	@Override
	public void close() throws IOException {
		raf.close();
	}
	/**
	private String inputFile;
	private String startCommitDelimiter;
	private String endMessageDelimiter;
	private String dbname;
	
	public void toDB(File logFile) throws FileNotFoundException {

		FileInputStream fis = new FileInputStream(logFile);
		Scanner scanner = new Scanner(fis);

		Project thisProject = new Project();
		thisProject.setName(inputFile);

		Map<String, Commit> commits = new HashMap<String, Commit>();
		Set<Edit> edits = new HashSet<Edit>();
		Map<String, model.File> files = new HashMap<String, model.File>();
		Set<FileAction> fileActions = new HashSet<FileAction>();
		Set<Rename> renames = new HashSet<Rename>();
		Map<String, User> users = new HashMap<String, User>();
		Map<model.File, List<Edit>> editsToFile = new HashMap<model.File, List<Edit>>();

		scanner.useDelimiter(startCommitDelimiter);
		int numCommits = 0;

		Commit headCommit = null;
		while (scanner.hasNext()) {
			String commitChunk = scanner.next();
			Scanner intraCommitScanner = new Scanner(commitChunk);
			intraCommitScanner.useDelimiter(endMessageDelimiter);
			String commitHeaders = intraCommitScanner.next();

			Scanner headersScanner = new Scanner(commitHeaders);
			headersScanner.useDelimiter("\n");
			// Commit
			String commitLine = headersScanner.next();
			Commit commit = new Commit();
			commit.setProject(thisProject);
			String[] parsedCommitIds = parseCommitId(commitLine);
			commit.setRevisionId(parsedCommitIds[0]);
			if (parsedCommitIds[1] != null) {
				if (commits.containsKey(parsedCommitIds[1])) {
					commit.addParent(commits.get(parsedCommitIds[1]));
					if (commits.containsKey(parsedCommitIds[2])) {
						commit.addParent(commits.get(parsedCommitIds[2]));
					}
				} else {
					System.out.println("Debug me!");
					System.out.println(commitChunk);
				}
			}
			headCommit = commit;

			// User
			String authorLine = headersScanner.next();
			User user = parseUser(parseAuthor(authorLine));
			// do we already know this user?
			if (users.containsKey(user.getName() + user.getEmail()))
				user = users.get(user.getName() + user.getEmail());
			else // unseen user. store him.
				users.put(user.getName() + user.getEmail(), user);

			user.addCommit(commit);
			commit.setUser(user);

			// Timestamp
			String dateString = headersScanner.next();
			DateTime timneStamp = parseDate(dateString);
			commit.setTimeStamp(timneStamp.toDate());

			// Comment
			headersScanner.useDelimiter(endMessageDelimiter);
			String messageString = headersScanner.next();
			String comment = parseMessage(messageString);
			commit.setComment(comment);
			headersScanner.close();

			// From here on there are the diffs and changes
			intraCommitScanner.useDelimiter("\ndiff");

			while (intraCommitScanner.hasNext()) {
				String afterDiff = intraCommitScanner.next();
				if (afterDiff.startsWith(" --git")) {
					model.File fileFrom = new model.File();
					model.File fileTo = new model.File();
					Rename rename = new Rename();
					FileAction fileAction = new FileAction();

					if (isBinaryChange(afterDiff))
						continue; // ignore the changes for binary files

					parseDiffs(afterDiff, fileFrom, fileTo, rename, fileAction);

					if (fileAction.getFile() == null)
						continue;

					// link to user, commit,
					fileAction.setCommit(commit);
					commit.addFileAction(fileAction);

					model.File file = null;
					switch (fileAction.getType()) {
					case DELETED:
						// take the first one
						file = fileFrom;
						break;
					case RENAMED:
					case CREATED:
					case CHMOD:
					case MODIFIED: // take the second one
						file = fileTo;
						break;

					default:
						file = fileFrom;
						break;
					}

					// store into sets/maps
					if (files.containsKey(file.getPath())) {// if it is already there, get it
						file = files.get(file.getPath());
					} else {
						files.put(file.getPath(), file);
					}

					// parse file actions
					fileAction.setFile(file);
					fileActions.add(fileAction);
					file.addFileAction(fileAction);

					if (rename.getFrom() != null && rename.getTo() != null) { // file was renamed
						rename.setCommit(commit);
						commit.addRename(rename);
						renames.add(rename);
						// make sure files referenced from rename are in the files collection
						if (!files.containsKey(rename.getFrom().getPath()))
							files.put(rename.getFrom().getPath(), rename.getFrom());
						if (!files.containsKey(rename.getTo().getPath()))
							files.put(rename.getTo().getPath(), rename.getTo());
					}
					// parse edits
					List<Edit> editsForThisFile = new ArrayList<Edit>();
					parseEdits(afterDiff, editsForThisFile);
					file.addEdits(editsForThisFile);
					for (Edit edit : editsForThisFile) {
						edit.setCommit(commit);
						commit.addEdit(edit);
						edit.setFile(file);
					}
					edits.addAll(editsForThisFile);

					// mapping file to corresponding edits
					if (editsToFile.containsKey(file)) {
						editsToFile.get(file).addAll(editsForThisFile);
					} else
						editsToFile.put(file, editsForThisFile);

					// calculate size at this commit
					fileAction.setTotalLines(countLinesFromEdits(file, editsToFile));
				}
			}

			intraCommitScanner.close();
			commits.put(commit.getRevisionId(), commit);
			numCommits++;
			if (numCommits % 100 == 0)
				System.out.print(".");
		}
		System.out.println();
		updateCommitParentsOf(headCommit);
		statelessBatchPersistEntities(thisProject, commits, files, fileActions, users, renames, edits);
		scanner.close();
	}
	public static String parseAuthor(String authorString){
		String[] splits = authorString.split("Author: ");
		String authorWithEmail = splits[splits.length-1].trim();
		return authorWithEmail;
	} 
	public static DateTime parseDate(String dateString){
		String date = dateString.split("Date: ")[1].trim();		 
		//		Locale locale = new Locale("de", "AT", "Austria");
		DateTimeFormatter gitFmt = DateTimeFormat.forPattern("EEE MMM dd HH:mm:ss yyyy Z").withLocale(Locale.ENGLISH);
		DateTime theDate = gitFmt.parseDateTime(date);
		return theDate;
	}

	public static String parseMessage(String messageString) {
		String theMessage = messageString.split("Message:")[1].trim();
		return theMessage;
	}
	/**
	 * This method iterates the part between two diffs and parses files, file actions and renames entities
	 * 
	 * @param afterDiff the part between two diffs
	 * @param fileFrom
	 * @param fileTo
	 * @param rename
	 * @param fileAction
	 
	private static void parseDiffs(String afterDiff, model.File fileFrom, model.File fileTo, Rename rename, FileAction fileAction) {
		String[] headerLines = afterDiff.split("\n");
		String fileFromPath = "";
		String fileToPath="";
		boolean filePathsParsed = false;
		
//		if(isBinaryChange(afterDiff)) //we ignore binary changes
//			return;
		
		for (int i = 0; !filePathsParsed && i < headerLines.length; i++) {
			if(headerLines[i].startsWith("---")){
				fileFromPath+= parseFileFrom(headerLines[i]);//.split(" ")[1]; // {"---" : " " : "/path/from"}
			}
			if(headerLines[i].startsWith("+++")){
				fileToPath+=parseFileTo(headerLines[i]);//.split(" ")[1]; // {"+++" : " " : "/path/to"}
				filePathsParsed=true;
			}
		}
		
		if(filePathsParsed){
			
			String devNull = "/dev/null";
			boolean nullFirst = false;
			boolean nullSecond = false;
			
			if(fileFromPath.equals(devNull)){
				fileFrom.setPath(devNull);
				nullFirst=true;
			}
			else
				fileFrom.setPath(fileFromPath);
			
			if(fileToPath.equals(devNull)){
				fileTo.setPath(devNull);
				nullSecond=true;
			}
			else
				fileTo.setPath(fileToPath);
			
			if(nullFirst&&!nullSecond){
				//creation
				fileAction.setFile(fileTo);
				fileAction.setType(ActionType.CREATED);
			}
			else if (!nullFirst&&nullSecond) {
				//deletion
				fileAction.setFile(fileFrom);
				fileAction.setType(ActionType.DELETED);
			}
			else if (!nullFirst&&!nullSecond) {
				//modification
				fileAction.setFile(fileFrom);
				
				if(fileFrom.getPath().equals(fileTo.getPath())){
					fileAction.setType(ActionType.MODIFIED);
				}
				else{//modified & different pathnames
					//rename
					rename.setFrom(fileFrom);
					rename.setTo(fileTo);
					fileFrom.addRenameFrom(rename);
					fileTo.addRenameTo(rename);
					fileAction.setType(ActionType.MODIFIED);
				}
			}
		}
		else{ //here we can have a CHMOD or just a rename
			boolean chmod=isChangeMode(afterDiff);
			boolean isRename = false;
			
			for(int i=0;i<headerLines.length;i++){
				if(headerLines[i].startsWith("rename from")){
					fileFromPath = headerLines[i].replaceAll("rename from ","");
					isRename=true;
				}
				if(headerLines[i].startsWith("rename to")){
					fileToPath = headerLines[i].replaceAll("rename to ","").trim();
				}
			}
			
			if(!isRename){
				String[] arr = getFileNameFromSingleHeaderLine(headerLines[0]);
				fileFromPath = arr[0];
				fileToPath = arr[1];
			}
			
			fileFrom.setPath(fileFromPath);
			fileTo.setPath(fileToPath);
			
			if(chmod){
				fileAction.setFile(fileTo);
				fileAction.setType(ActionType.CHMOD);
				fileFrom.addFileAction(fileAction);
				fileTo.addFileAction(fileAction);
			}
			if(isRename){
//				if(chmod)
//					System.out.println(afterDiff);
				rename.setFrom(fileFrom);
				rename.setTo(fileTo);
				fileFrom.addRenameFrom(rename);
				fileTo.addRenameTo(rename);
				
				fileAction.setFile(fileTo);
				fileAction.setType(ActionType.RENAMED);
				fileFrom.addFileAction(fileAction);
				fileTo.addFileAction(fileAction);
			}
		}
	}
	**/
}
