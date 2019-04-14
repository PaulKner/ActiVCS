package test;

import org.junit.jupiter.api.Test;
import miner.ActivityIdentifier;
import model.Change;
import model.ChangeTemplate;

/**
 * Test routines for the ActivityIdentifier class
 * Ensures, that the regular expressions work as intended
 * @author Paul Kneringer
 */
public class ActivityIdentificationTest {
	@Test
	public void testRegex() {
		ActivityIdentifier ai = new ActivityIdentifier("src/data/RegularExpressions.csv");
		//Defining the categories (expected results)
		String unknown = "unknown";
		String doc = "doc";
		String img = "img";
		String loc = "loc";
		String ui = "ui";
		String media = "media";
		String code = "code";
		String meta = "meta";
		String config = "config";
		String build = "build";
		String devdoc = "devdoc";
		String db = "db";
		String test = "test";
		String lib = "lib";
		
		//Testing unique identification of extensions
		String testS1 = "";
		String testS2 = "document.doc";
		String testS3 = "Image.jpeg";
		String testS4 = "location.pot~";
		String testS5 = "dskt.desktop";
		String testS6 = "soundtrack.mp3";
		String testS7 = "GITLogReader.java";
		String testS8 = "repository.git";
		String testS9 = "configuration.gnorba";
		String testS10 = "bld.cmake";
		String testS11 = "dvdoc.readme";
		String testS12 = "database.sql";
		String testS13 = "somewhere/LogReadertest.java";
		
		//Testing unique identification of file paths
		String testS14 = "/libraries/lib/library1";
		String testS15 = "documentation/doc-books/docu";
		String testS16 = "Java/test/reader/somedocument.someformat";
		
		//Testing multiple detections (last identified
		String testS17 = "Java/src/reader/GITLogReaderTest.java";
		String testS18 = "Java/test/reader/GITLogReader.java";
		String testS19 = "Java/library/reader/doc.doc";
		String testS20 = "media/GITLogReader.java";

		ChangeTemplate s1 = new Change("A",testS1);
		ChangeTemplate s2 = new Change("A",testS2);
		ChangeTemplate s3 = new Change("A",testS3);
		ChangeTemplate s4 = new Change("A",testS4);
		ChangeTemplate s5 = new Change("A",testS5);
		ChangeTemplate s6 = new Change("A",testS6);
		ChangeTemplate s7 = new Change("A",testS7);
		ChangeTemplate s8 = new Change("A",testS8);
		ChangeTemplate s9 = new Change("A",testS9);
		ChangeTemplate s10 = new Change("A",testS10);
		ChangeTemplate s11 = new Change("A",testS11);
		ChangeTemplate s12 = new Change("A",testS12);
		ChangeTemplate s13 = new Change("A",testS13);
		ChangeTemplate s14 = new Change("A",testS14);
		ChangeTemplate s15 = new Change("A",testS15);
		ChangeTemplate s16 = new Change("A",testS16);
		ChangeTemplate s17 = new Change("A",testS17);
		ChangeTemplate s18 = new Change("A",testS18);
		ChangeTemplate s19 = new Change("A",testS19);
		ChangeTemplate s20 = new Change("A",testS20);
		
		org.junit.Assert.assertEquals(ai.identifyActivityLabel(s1).getActivityLabel(), unknown);
		org.junit.Assert.assertEquals(ai.identifyActivityLabel(s2).getActivityLabel(), doc);
		org.junit.Assert.assertEquals(ai.identifyActivityLabel(s3).getActivityLabel(), img);
		org.junit.Assert.assertEquals(ai.identifyActivityLabel(s4).getActivityLabel(), loc);
		org.junit.Assert.assertEquals(ai.identifyActivityLabel(s5).getActivityLabel(), ui);
		org.junit.Assert.assertEquals(ai.identifyActivityLabel(s6).getActivityLabel(), media);
		org.junit.Assert.assertEquals(ai.identifyActivityLabel(s7).getActivityLabel(), code);
		org.junit.Assert.assertEquals(ai.identifyActivityLabel(s8).getActivityLabel(), meta);
		org.junit.Assert.assertEquals(ai.identifyActivityLabel(s9).getActivityLabel(), config);
		org.junit.Assert.assertEquals(ai.identifyActivityLabel(s10).getActivityLabel(), build);
		org.junit.Assert.assertEquals(ai.identifyActivityLabel(s11).getActivityLabel(), devdoc);
		org.junit.Assert.assertEquals(ai.identifyActivityLabel(s12).getActivityLabel(), db);
		org.junit.Assert.assertEquals(ai.identifyActivityLabel(s13).getActivityLabel(), test);
		org.junit.Assert.assertEquals(ai.identifyActivityLabel(s14).getActivityLabel(), lib);
		org.junit.Assert.assertEquals(ai.identifyActivityLabel(s15).getActivityLabel(), doc);
		org.junit.Assert.assertEquals(ai.identifyActivityLabel(s16).getActivityLabel(), test);
		org.junit.Assert.assertEquals(ai.identifyActivityLabel(s17).getActivityLabel(), test);
		org.junit.Assert.assertEquals(ai.identifyActivityLabel(s18).getActivityLabel(), test);
		org.junit.Assert.assertEquals(ai.identifyActivityLabel(s19).getActivityLabel(), lib);
		org.junit.Assert.assertEquals(ai.identifyActivityLabel(s20).getActivityLabel(), code);
		/**
		String testS21 = "Java/test/reader/GITLogReader.java";
		String testS22 = "Java/test/reader/GITLogReader.java";
		String testS23 = "Java/test/reader/GITLogReader.java";
		String testS24 = "Java/test/reader/GITLogReader.java";
		String testS25 = "Java/test/reader/GITLogReader.java";
		String testS26 = "Java/test/reader/GITLogReader.java";
		String testS27 = "Java/test/reader/GITLogReader.java";
		String testS28 = "Java/test/reader/GITLogReader.java";
		String testS29 = "Java/test/reader/GITLogReader.java";
		String testS30 = "Java/test/reader/GITLogReader.java";
		 * **/		
	}
}
