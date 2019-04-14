package GUI;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;

import org.joda.time.DateTime;
import org.joda.time.Days;
import org.joda.time.Months;
import org.joda.time.Seconds;
import org.joda.time.Weeks;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.geometry.Bounds;
import javafx.geometry.Pos;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.chart.AreaChart;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.Chart;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.SplitPane;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.util.StringConverter;
import metrics.Metrics;
import miner.ActivityIdentifier;
import model.ChangeTemplate;
import model.EnrichedChange;
import model.Log;
import model.LogEntry;
import model.git.GITLog;
import model.graph.ChartNode;
import reader.GITLogReader;
import reader.LogReader;

/**
 * Main controller class of the project
 * Manages interaction between GUI and model classes
 * Transforms log into visual model and KPIs with the use of other classes
 * 
 * 
 * @author Paul Kneringer
 * @date: 14.4.2019
 */
public class GUIController {
	private Stage stage;
	private final String REGEX_LOCATION = "src/data/RegularExpressions.csv";
	private Log log;
	private boolean asAreaChart = true;
	private boolean showZero = false;
	public boolean autosize = false;
	private boolean logging = false;
	private Metrics m;
	
	//Set of attributes required by the transformLog method
	private boolean alreadySet = false;
	private int zoomElem = 0;
	private DateTime minDate = null;
	private DateTime maxDate = null;
	private HashMap<String, HashMap<Integer, ArrayList<LogEntry>>> logsPerUnit;
	private HashMap<Integer, ArrayList<LogEntry>> innerLogList;
	private HashMap<String, HashMap<Integer, ArrayList<EnrichedChange>>> changePerUnit;
	private HashMap<Integer, ArrayList<EnrichedChange>> innerChangeList;
	
	//Set of attributes required by visualizeKPIData method
	private Font f1 = new Font(15);
	private Font f2 = new Font(45);
	
	//GUI elements: 
	@FXML
	private AnchorPane chartPane;
	@FXML
	private Tab chartTab;
	@FXML
	private Tab kpiTab;
	@FXML
	private TabPane tabPane;
	@FXML
	private SplitPane splitPane;
	@FXML
	private ChoiceBox<String> choiceBox;
	@FXML
	private CheckBox displayValueCheckBox;
	@FXML
	private CheckBox showZeroCheckBox;
	@FXML
	private AnchorPane tabAnchorPane;
	@FXML
	private CheckBox autoSizeCheckBox;
	@FXML
	private VBox vBoxSettings;
	@FXML
	private ChoiceBox<String> choiceBoxLevel;
	@FXML
	private Tab diagramTab;

	/**
	 * Called by GUI
	 * Swaps value of property autosize
	 * Calls transfromLog method to redraw charts
	 */
	public void setAutoSize() {
		if (autosize)
			autosize = false;
		else
			autosize = true;
		transformLog();
	}
	
	/**
	 * Called by GUI
	 * Swaps value of property asAreaChart
	 * Calls transfromLog method to redraw charts
	 */
	public void displayValues() {
		if (asAreaChart)
			asAreaChart = false;
		else
			asAreaChart = true;
		transformLog();
	}

	/**
	 * Called by GUI
	 * Swaps value of property logging
	 * Calls transfromLog method to redraw charts
	 */
	public void setLogging() {
		if (logging) {
			logging = false;
			choiceBox.setVisible(true);
			choiceBoxLevel.setVisible(true);
		} else {
			logging = true;
			choiceBox.setVisible(false);
			choiceBoxLevel.setVisible(false);
		}
		transformLog();
	}

	/**
	 * Primary method to calculate metrics that are displayed in the "Details" tab of the ActiVCS tool
	 * Creates a TreeItem object per KPI
	 * Builds tree structure in TreeView item
	 * Calls the visualizeKPIData method
	 */
	private void calculateMetrics() {
		VBox vb = new VBox();
		m = new Metrics(log);
		HashMap<String, HashMap<String, Integer>> ATW = m.getATW();
		// Building the Author Tree
		TreeItem<String> rootItem = new TreeItem<String>("KPIs");
		rootItem.setExpanded(true);
		// Project KPIs
		TreeItem<String> projectItems = new TreeItem<String>("Project KPIs");
		TreeItem<String> giniWorkItem = new TreeItem<String>("GINI Workload : " + m.PWS(ATW));
		TreeItem<String> giniRelWorkItem = new TreeItem<String>("GINI Relative Workload : " + m.RPWS(ATW));
		TreeItem<String> giniAuthorItem = new TreeItem<String>("GINI Author Participation : " + m.PIS(ATW));
		TreeItem<String> giniRelAuthorItem = new TreeItem<String>(
				"GINI Relative Author Participation : " + m.RPIS(ATW));
		projectItems.getChildren().addAll(giniWorkItem, giniRelWorkItem, giniAuthorItem, giniRelAuthorItem);
		rootItem.getChildren().add(projectItems);
		// Author KPIs
		TreeItem<String> authorsItem = new TreeItem<String>("Author KPIs");
		for (String author : ATW.keySet()) {
			TreeItem<String> item = new TreeItem<String>(author);
			item.setExpanded(false);
			for (String type : ATW.get(author).keySet()) {
				TreeItem<String> subitem = new TreeItem<String>(type + " : " + ATW.get(author).get(type) + "");
				item.getChildren().add(subitem);
			}
			authorsItem.getChildren().add(item);
		}
		rootItem.getChildren().add(authorsItem);
		// Activity-type KPIs
		TreeItem<String> typeItem = new TreeItem<String>("Activity-type KPIs");
		for (String type : getSortedTypes(false)) {
			TreeItem<String> item = new TreeItem<String>(type);
			item.setExpanded(false);
			TreeItem<String> PTWItem = new TreeItem<String>("PTW: " + m.PTW(type, ATW));
			TreeItem<String> RPTWItem = new TreeItem<String>("RPTW: " + m.RPTW(type, ATW));
			TreeItem<String> PTIItem = new TreeItem<String>("PTI: " + m.PTI(type, ATW));
			TreeItem<String> NAPItem = new TreeItem<String>("NAP: " + m.NAP());
			TreeItem<String> RPTIItem = new TreeItem<String>("RPTI: " + m.RPTI(type, ATW));
			item.getChildren().addAll(PTWItem, RPTWItem, PTIItem, NAPItem, RPTIItem);
			typeItem.getChildren().add(item);
		}
		rootItem.getChildren().add(typeItem);
		/**
		System.out.println("PW: " + m.PW(ATW));
		for (String type : getSortedTypes(false)) {
			System.out.println("Type: " + type + " PTW: " + m.PTW(type, ATW));
			System.out.println("RPTW: " + m.RPTW(type, ATW));
			System.out.println("PTI: " + m.PTI(type, ATW));
			System.out.println("NAP: " + m.NAP());
			System.out.println("Relative involvement of authors on type " + type + ": " + m.RPTI(type, ATW));
		}
		**/
		TreeView<String> tree = new TreeView<String>(rootItem);
		tree.prefHeightProperty().bind(vb.heightProperty());
		vb.getChildren().add(tree);
		kpiTab.setContent(vb);

		visualizeKPIData(ATW);
	}


	/**
	 * Creates the diagrams and KPIs that are displayed in the "KPIs" tab of the application
	 * @param ATW: Author Type Workload
	 */
	private void visualizeKPIData(HashMap<String, HashMap<String, Integer>> ATW) {
		// Setting up the Tab
		GridPane gp = new GridPane();
		m = new Metrics(log);
		gp.prefWidthProperty().bind(stage.widthProperty());
		gp.prefHeightProperty().bind(stage.heightProperty());

		// First Chart: Barchart
		NumberAxis na = new NumberAxis();
		na.setLabel("% of Workload");
		// Format axis for percentages
		na.setTickLabelFormatter(new StringConverter<Number>() {

			@Override
			public String toString(Number object) {
				String ret = "%" + (int) (object.floatValue() * 100);
				return ret;
			}

			@Override
			public Number fromString(String string) {
				return null;
			}
		});
		na.setAutoRanging(false);
		na.setLowerBound(0);
		na.setUpperBound(1);
		na.setTickUnit(0.05);

		CategoryAxis ca = new CategoryAxis();
		ca.setLabel("Activity Type");
		XYChart.Series<Number, String> series1 = new XYChart.Series<Number, String>();
		series1.setName("Workload");

		// Second Chart:
		NumberAxis na2 = new NumberAxis();
		na2.setLabel("Number of Authors");
		na2.setTickLabelFormatter(new StringConverter<Number>() {

			@Override
			public String toString(Number object) {
				String ret = "%" + (int) (object.floatValue() * 100);
				return ret;
			}

			@Override
			public Number fromString(String string) {
				return null;
			}
		});
		CategoryAxis ca2 = new CategoryAxis();
		ca2.setLabel("Activity Type");
		XYChart.Series<Number, String> series2 = new XYChart.Series<Number, String>();
		series2.setName("Authors participating in type");

		na2.setAutoRanging(false);
		na2.setLowerBound(0);
		na2.setUpperBound(1);
		na2.setTickUnit(0.05);

		// true: means on file level
		for (String type : getSortedTypes(false)) {
			final XYChart.Data<Number, String> dat1 = new XYChart.Data<Number, String>(m.RPTW(type, ATW), type);
			final XYChart.Data<Number, String> dat2 = new XYChart.Data<Number, String>(m.RPTI(type, ATW), type);
			// Add ChangeListener to display value on Barchart
			dat1.nodeProperty().addListener(new ChangeListener<Node>() {

				@Override
				public void changed(ObservableValue<? extends Node> observable, Node oldNode, Node node) {
					if (node != null) {
						displayLabel(dat1, m.PTW(type, ATW) + "");
					}
				}

			});
			// Same for second chart
			dat2.nodeProperty().addListener(new ChangeListener<Node>() {

				@Override
				public void changed(ObservableValue<? extends Node> observable, Node oldNode, Node node) {
					if (node != null) {
						displayLabel(dat2, m.PTI(type, ATW) + "");
					}
				}
			});
			series1.getData().add(0, dat1);
			series2.getData().add(0, dat2);
		}

		// Add series to charts
		BarChart<Number, String> bc = new BarChart<Number, String>(na, ca);
		bc.getData().add(series1);
		bc.prefWidthProperty().bind(stage.widthProperty().divide(2));
		BarChart<Number, String> bc2 = new BarChart<Number, String>(na2, ca2);
		bc2.getData().add(series2);
		bc2.prefWidthProperty().bind(stage.widthProperty().divide(2));

		// First Metric
		HBox hBox = new HBox();
		Text t = new Text("Project Workload: ");
		t.setFont(f1);
		// t.setStyle("-fx-font-weight: bold");
		Text t1 = new Text(m.PW(ATW) + "");
		t1.setFont(f2);
		/**
		 * Was trying to add a Mouseevent displaying info about the KPIs final Label
		 * caption = new Label(""); caption.setTextFill(Color.DARKORANGE);
		 * 
		 * 
		 * EventHandler<MouseEvent> ev = new EventHandler<MouseEvent>() {
		 * 
		 * @Override public void handle(MouseEvent event) { String s =
		 *           (((Text)event.getSource()).getText()); switch (s) { case "Project
		 *           Workload: ": caption.relocate(event.getScreenX(),
		 *           event.getSceneY()); caption.setText("Displays the number of changes
		 *           to files of the respective category"); caption.setVisible(true);
		 *           break;
		 * 
		 *           default: break; } } };
		 * 
		 *           t.addEventHandler(MouseEvent.MOUSE_ENTERED, ev);
		 **/
		// t1.setStyle("-fx-font-weight: bold");
		hBox.setAlignment(Pos.CENTER);
		hBox.setSpacing(10);
		// flow.getChildren().addAll(t,t1,t2);

		// Second Metric
		HBox hBox2 = new HBox();
		Text t2 = new Text("GINI Workload: ");
		t2.setFont(f1);
		Text t3 = new Text((int) (m.PWS(ATW) * 100) + "%");
		t3.setFont(f2);
		// t3.setStyle("-fx-font-weight: bold");
		hBox2.setAlignment(Pos.CENTER);
		hBox2.setSpacing(10);
		//Third
		HBox hBox3 = new HBox();
		Text t4 = new Text("GINI Authors: ");
		t4.setFont(f1);
		Text t5 = new Text((int) (m.PIS(ATW) * 100) + "%");
		t5.setFont(f2);
		// t3.setStyle("-fx-font-weight: bold");
		hBox3.setAlignment(Pos.CENTER);
		hBox3.setSpacing(10);
		//Fourth
		HBox hBox4 = new HBox();
		Text t6 = new Text("Nr. of Authors: ");
		t6.setFont(f1);
		Text t7 = new Text(m.NAP() + "");
		t7.setFont(f2);
		// t3.setStyle("-fx-font-weight: bold");
		hBox4.setAlignment(Pos.CENTER);
		hBox4.setSpacing(10);

		hBox.getChildren().add(0, t);
		hBox.getChildren().add(1, t1);
		hBox2.getChildren().add(0, t2);
		hBox2.getChildren().add(1, t3);
		hBox3.getChildren().addAll(t4, t5);
		hBox4.getChildren().addAll(t6, t7);

		// Add charts to pane
		gp.add(bc, 0, 0);
		gp.add(bc2, 1, 0);
		gp.add(hBox, 0, 1, 2, 1);
		gp.add(hBox2, 0, 2, 2, 1);
		gp.add(hBox3, 0, 4, 2, 1);
		gp.add(hBox4, 0, 3, 2, 1);

		// Add pane to overarching node
		diagramTab.setContent(gp);

	}

	// This method was inspired by an online source and changed according to my needs:
	// https://gist.github.com/jewelsea/5094893
	/**
	 * Adds a node to the bar chart data elements that displays the provided text String at the correct position in the parent object.
	 * @param data: One Bar of the BarChart
	 * @param text: PTW value that should be displayed on the Bar
	 */
	private void displayLabel(XYChart.Data<Number, String> data, String text) {
		Node node = data.getNode();
		Text dText = new Text(text);
		dText.setFont(Font.font("Courier New", 12));
		dText.setSmooth(true);

		node.parentProperty().addListener(new ChangeListener<Parent>() {
			@Override
			public void changed(ObservableValue<? extends Parent> ov, Parent oldParent, Parent parent) {
				Group parents = (Group) parent;
				parents.getChildren().add(dText);
			}
		});

		node.boundsInParentProperty().addListener(new ChangeListener<Bounds>() {
			@Override
			public void changed(ObservableValue<? extends Bounds> ov, Bounds oldBounds, Bounds bounds) {
				dText.setLayoutX(Math.round(bounds.getMaxX()));
				dText.setLayoutY(Math.round(bounds.getMaxY() - bounds.getHeight() / 2 + dText.prefHeight(-1) / 2));
			}
		});
	}

	/**
	 * Writes the enriched log file into a file that is specified by the user
	 * This is enabled by the Serializable interface
	 */
	public void saveLog() {
		if (log != null) {
			try {
				FileChooser fileChooser = new FileChooser();
				fileChooser.setTitle("Store Resource File");
				fileChooser.setInitialDirectory(new File(System.getProperty("user.home")));
				File f;
				f = fileChooser.showSaveDialog(stage);
				if (f != null) {
					FileOutputStream fos = new FileOutputStream(f);
					ObjectOutputStream oos = new ObjectOutputStream(fos);
					oos.writeObject(log);
					oos.close();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		} else {
			Alert errorAlert = new Alert(AlertType.ERROR);
			errorAlert.setTitle("Error");
			errorAlert.setHeaderText("No Analysis Created");
			errorAlert.setContentText("Please load a log file into the system first");
			errorAlert.showAndWait();
		}
	}

	/**
	 * Loads a log file from the file system as selected by a user
	 * This is enabled by the Serializable interface
	 * If successful, calls transformLog, calculateMetrics in order to create visualization and metrics
	 */
	public void loadLog() {
		try {
			FileChooser fileChooser = new FileChooser();
			fileChooser.setTitle("Open Resource File");
			fileChooser.setInitialDirectory(new File(System.getProperty("user.home")));
			File f;
			f = fileChooser.showOpenDialog(stage);
			if (f != null) {
				FileInputStream fis = new FileInputStream(f);
				ObjectInputStream ois = new ObjectInputStream(fis);
				log = (Log) ois.readObject();
				ois.close();
			}
		} catch (Exception e) {
			Alert errorAlert = new Alert(AlertType.ERROR);
			errorAlert.setTitle("Error");
			errorAlert.setHeaderText("Input not valid");
			errorAlert.setContentText("Please select a file that was created with the ActiVCS tool\n"+e.getMessage());
			errorAlert.showAndWait();
			log = null;
		}
		if (log != null) {
			transformLog();
			// drawGrid();
			calculateMetrics();
			if (chartTab.isSelected())
				displaySettings();
			else
				hideSettings();
			// drawDataPoints(graph);

		}
	}

	/**
	 * Triggered by GUI
	 * Sets value of showZero variable
	 * Calls transformLog method to repaint the charts according to the changed perspective
	 */
	public void displayZero() {
		if (showZero)
			showZero = false;
		else
			showZero = true;

		transformLog();

	}
	
	/**
	 * hides the setting panel
	 */
	public void hideSettings() {
		vBoxSettings.setVisible(false);
	}

	/**
	 * Show the setting panel
	 */
	public void displaySettings() {
		if (log != null)
			vBoxSettings.setVisible(true);
	}

	/**
	 * Method is called by menu item: Select Log
	 * Creates a Dialog and calls the method initiateLog in order to create a new Log object
	 * If this was successful, the method calls the transformLog and the calcualateMetrics methods in order to create visualizations
	 * Displays and hides the settings panel on the left side of the GUI
	 */
	public void selectFile() {
		FileChooser fileChooser = new FileChooser();
		fileChooser.setTitle("Open Resource File");
		File f;
		f = fileChooser.showOpenDialog(stage);
		if (f != null) {
			log = initiateLog(f.getAbsolutePath());
			if (log != null) {
				transformLog();
				calculateMetrics();
				if (chartTab.isSelected())
					displaySettings();
				else
					hideSettings();
			}
		}
	}

	/**
	 * private void drawDataPoints(List<GraphElement> lg) { GraphicsContext gc =
	 * canvas.getGraphicsContext2D(); gc.setFill(Paint.valueOf("green")); for
	 * (GraphElement ge : lg) { gc.fillRect(ge.x, ge.y, 1, ge.height / 2); } }
	 **/
	private Log initiateLog(String path) {
		Log log;
		try {
			LogReader<LogEntry> lr = new GITLogReader(path);
			log = new GITLog(lr.readAll());
			ActivityIdentifier AI = new ActivityIdentifier(REGEX_LOCATION);
			log = AI.enrichLog(log);
			lr.close();
		} catch (Exception e) {
			log = null;
			Alert errorAlert = new Alert(AlertType.ERROR);
			errorAlert.setTitle("Error");
			errorAlert.setHeaderText("Input not valid");
			errorAlert.setContentText("Please select a file that was created using the specified GIT log command (see Help Dialog) \n" +e.getMessage());
			errorAlert.showAndWait();
		}
		return log;
	}

	/**
	 * This method sets the stage element of the controller class
	 * Additionally, the choiceBox items are filled with values
	 * @param stage: The main stage, passed by the GUI.java class
	 */
	public void setStage(Stage stage) {
		this.stage = stage;
		splitPane.prefWidthProperty().bind(stage.widthProperty());
		tabPane.prefHeightProperty().bind(chartPane.heightProperty());
		tabPane.prefWidthProperty().bind(chartPane.widthProperty());
		choiceBox.getItems().removeAll(choiceBox.getItems());
		choiceBox.getItems().addAll("Days", "Weeks", "Months");
		choiceBox.getSelectionModel().select("Weeks");

		choiceBoxLevel.getItems().removeAll(choiceBox.getItems());
		choiceBoxLevel.getItems().add(0, "Commit-level");
		choiceBoxLevel.getItems().add(1, "File-Level");
		choiceBoxLevel.getSelectionModel().select(0);
	}
	
	/**
	 * Identifies the selected element from the choiceBox object for the selection of the time-perspective
	 * @return: Integer that represents which time-perspective is selected
	 */
	private int getSelection() {
		int retVal = 0;
		switch (choiceBox.getSelectionModel().getSelectedItem()) {
		case "Days":
			retVal = 0;
			break;
		case "Weeks":
			retVal = 1;
			break;
		case "Months":
			retVal = 2;
			break;
		}
		return retVal;
	}

	/**
	 * Depending on the chosen time-perspective, this method returns the amount of days, weeks or months between two dates. 
	 * @param t1: First date
	 * @param t2: Second date
	 * @return: amount of days,weeks, month between t1 and t2
	 */
	private int getUnit(DateTime t1, DateTime t2) {
		int unit = 0;
		switch (getSelection()) {
		case 0:
			unit = Days.daysBetween(t1.withTimeAtStartOfDay(), t2.withTimeAtStartOfDay()).getDays();
			break;

		case 1:
			unit = Weeks.weeksBetween(t1.dayOfWeek().withMinimumValue(), t2.dayOfWeek().withMaximumValue()).getWeeks();
			break;
		case 2:
			unit = Months.monthsBetween(t1, t2).getMonths();
		}
		return unit;
	}

	/**
	 * Returns true if file-level is selected in dropdown list Returns false if
	 * commit-level is selected in dropdown list
	 * @return: true: file-level, false: commit-level
	 */
	private boolean getLevel() {
		boolean ret;

		if (choiceBoxLevel.getSelectionModel().getSelectedIndex() == 0)
			ret = false;
		else
			ret = true;

		if (logging)
			ret = true;
		return ret;
	}
	


	/**
	 * Main method for visualization
	 * Transforms the Log object into a Hashmap object called curves
	 * This object is then used to create one chart per identified activity type
	 * Charts receive mouse listeners that enables zooming 
	 * All data points in the charts receive a ChartNode object that allows the detailed inspection of the data points and displays the values on mouse hover
	 * Also implements the logging functionality
	 */
	public void transformLog() {
		if (log == null)
			return;
		minDate = null;
		maxDate = null;
		zoomElem = 0;
		alreadySet = false;
		// String = type, Integer = week, Integer = number of changes
		Map<String, HashMap<Integer, Integer>> Curves = new HashMap<String, HashMap<Integer, Integer>>();
		// First integer is week, second integer is number of changes
		HashMap<Integer, Integer> typeValues;
		logsPerUnit = new HashMap<String, HashMap<Integer, ArrayList<LogEntry>>>();
		changePerUnit = new HashMap<String, HashMap<Integer, ArrayList<EnrichedChange>>>();
		int unit = 0;
		
		// min and max
		DateTime dt;
		for (LogEntry l : log.getAllEntries()) {
			dt = l.getDate();
			if (minDate == null)
				minDate = dt;
			if (maxDate == null)
				maxDate = dt;
			if (dt.compareTo(maxDate) > 0) {
				maxDate = dt;
			}
			if (dt.compareTo(minDate) < 0) {
				minDate = dt;
			}
		}
		
		//Logging functionality, required to split the duration of the project into percentages
		int loggingStepSize = 100;
		long loggingUnit = maxDate.getMillis() - minDate.getMillis();
		double loggingStep;
		double loggdays = ((double) loggingUnit / (double) (1000 * 60 * 60 * 24));
		if (loggdays >= loggingStepSize) {
			loggingStep = loggdays / loggingStepSize;
		} else {
			loggingStep = loggingUnit / loggdays;
		}

		for (LogEntry l : log.getAllEntries()) {
			if (logging) {

				unit = (int) Math
						.floor((double) (Seconds.secondsBetween(minDate, l.getDate()).getSeconds() / (60 * 60 * 24))
								/ loggingStep);
			} else {
				unit = getUnit(minDate, l.getDate());
			}
			// Iterate over all changes: Here the distinction between filelevel and
			// commitlevel visualization is made
			Integer count;
			if (getLevel()) {
				for (ChangeTemplate change : l.getChangeList()) {
					EnrichedChange ec = (EnrichedChange) change;
					if(ec.getPath().equals("docum2ent.doc"))
						System.out.println("hi");
					if (Curves.containsKey(ec.getActivityLabel())) {
						typeValues = Curves.get(ec.getActivityLabel());
					}else {
						typeValues = new HashMap<Integer, Integer>();
					}
					if (typeValues.containsKey(unit)) {
						count = typeValues.get(unit);
					}else {
						count = 0;
					}
					count = count + 1;
					typeValues.put(unit, count);

					ArrayList<EnrichedChange> al;
					// Storage of logfiles per type per week/day
					if (changePerUnit.containsKey(ec.getActivityLabel())) {
						innerChangeList = changePerUnit.get(ec.getActivityLabel());
					} else {
						innerChangeList = new HashMap<Integer, ArrayList<EnrichedChange>>();
					}
					if (innerChangeList.containsKey(unit)) {
						al = innerChangeList.get(unit);
					} else {
						al = new ArrayList<EnrichedChange>();

					}
					al.add(ec);
					innerChangeList.put(unit, al);
					changePerUnit.put(ec.getActivityLabel(), innerChangeList);
					Curves.put(ec.getActivityLabel(), typeValues);
				}
			} else {
				if (Curves.containsKey(l.getType())) {
					typeValues = Curves.get(l.getType());
				} else {
					typeValues = new HashMap<Integer, Integer>();
				}
				if (typeValues.containsKey(unit)) {
					count = typeValues.get(unit);
				} else {
					count = 0;
				}
				count = count + 1;
				typeValues.put(unit, count);
				Curves.put(l.getType(), typeValues);

				ArrayList<LogEntry> al;
				// Storage of logfiles per type per week/day
				if (logsPerUnit.containsKey(l.getType())) {
					innerLogList = logsPerUnit.get(l.getType());
				} else {
					innerLogList = new HashMap<Integer, ArrayList<LogEntry>>();
				}
				if (innerLogList.containsKey(unit)) {
					al = innerLogList.get(unit);
				} else {
					al = new ArrayList<LogEntry>();

				}
				al.add(l);
				innerLogList.put(unit, al);
				logsPerUnit.put(l.getType(), innerLogList);
			}

		}

		VBox vb = new VBox();
		for (String type : getSortedTypes(getLevel())) {
			if (Curves.get(type) == null) {
				System.err.println("SOMETHING IS WRONG");
				break;
			}
			NumberAxis na = new NumberAxis();
			if (!logging)
				na.setLabel(choiceBox.getSelectionModel().getSelectedItem());
			else
				na.setLabel("% of time");
			na.setAutoRanging(false);
			na.setLowerBound(0);
			//If logging is active, the upper bound is set to 100 (loggingStepSize)
			if (!logging)
				na.setUpperBound(getUnit(minDate, maxDate) + 2);
			else
				na.setUpperBound(loggingStepSize);

			// na.setTickLabelGap(1);
			//Generic chart definition, can either be Line- or Areachart
			Chart lc;
			NumberAxis nb = new NumberAxis();
			if (getLevel()) {
				nb.setLabel("File changes");
			} else {
				nb.setLabel("Commits");
			}
			na.setOnMousePressed(new EventHandler<MouseEvent>() {

				@Override
				public void handle(MouseEvent event) {
					//Zooming functionality: If a label on the axis was pressed, it is stored until a second label was pressed
					if (event.getButton().equals(MouseButton.PRIMARY)) {
						if (event.getPickResult().getIntersectedNode().getClass()
								.equals(javafx.scene.text.Text.class)) {
							try {
								int value = Integer.parseInt(
										((javafx.scene.text.Text) (event.getPickResult().getIntersectedNode()))
												.getText().replace(".", ""));
								if (alreadySet) {
									if (zoomElem > value) {
										na.setLowerBound(value);
										na.setUpperBound(zoomElem);
									} else {
										na.setLowerBound(zoomElem);
										na.setUpperBound(value);
									}
									alreadySet = false;
								} else {
									alreadySet = true;
									zoomElem = value;
								}

							} catch (Exception e) {
								System.err.println("Incorrect format in axis (requires numbers to zoom); Java error: "
										+ e.getMessage());
							}
						}
					} else {
						alreadySet = false;
						zoomElem = -1;
						na.setLowerBound(0);
						if (!logging)
							na.setUpperBound(getUnit(minDate, maxDate) + 2);
						else
							na.setUpperBound(100);
					}
				}

			});
			XYChart.Series<Number, Number> series = new XYChart.Series<Number, Number>();
			if (choiceBoxLevel.getSelectionModel().getSelectedIndex() == 0)
				series.setName("Commits");
			else
				series.setName("Files");
			int[] xVal = new int[java.util.Collections.max(Curves.get(type).keySet()) + 1];
			//Transforming the curve object to an XYChart.Data object in order to fill the charts with values
			for (int j = 0; j <= java.util.Collections.max(Curves.get(type).keySet()); j++) {
				if (Curves.get(type).keySet().contains(j)) {
					xVal[j] = Curves.get(type).get(j);
					System.out.println("An der stelle j: " + j + " -für type : " + type + " - wird die Axe auf : "
							+ Curves.get(type).get(j) + " gesetzt");
					series.getData().add(new XYChart.Data<Number, Number>(j, Curves.get(type).get(j)));
				} else {

					xVal[j] = 0;
					if (showZero)
						series.getData().add(new XYChart.Data<Number, Number>(j, 0));
				}
			}
			//Creates an Area- or a LineChart object
			//Adds ChartNode objects to the data of the XYChart.Series
			if (asAreaChart) {
				lc = new AreaChart<Number, Number>(na, nb, FXCollections.observableArrayList(
						new XYChart.Series("Data point", FXCollections.observableArrayList(plot(type, xVal)))));
			} else {
				lc = new LineChart<Number, Number>(na, nb, FXCollections.observableArrayList(
						new XYChart.Series(FXCollections.observableArrayList(plot(type, xVal)))));
				((LineChart) lc).setStyle("-fx-stroke-width: 1px;");
				((LineChart) lc).setCreateSymbols(false);
			}
			lc.setPrefHeight(350);
			lc.setTitle(type);
			lc.setId(type);
			vb.getChildren().add(lc);

			lc.prefWidthProperty().bind(chartPane.widthProperty().subtract(15));
			if (autosize)
				lc.prefHeightProperty().bind(chartPane.heightProperty().divide(Curves.keySet().size())
						.subtract(60 / Curves.keySet().size()));

		}
		// System.out.println("CHILDREN: "+ vb.getChildren());

		javafx.scene.control.ScrollPane sp = new javafx.scene.control.ScrollPane(vb);
		sp.autosize();
		chartTab.setContent(sp);

		//Logging functionality
		if (logging) {
			log(Curves, loggingStepSize,"Statistics.csv");
		}
	}

	/**
	 * Logs the KPIs of a project 
	 * Logs the workload per activity type of a project in percentages of time (to make data comparable)
	 * @param Curves: The data structure containing modelling data
	 * @param loggingStepSize: usually 100 (percent) 
	 * @param filename: Name of the log file that is created/extended
	 */
	private void log(Map<String, HashMap<Integer, Integer>> curves, int loggingStepSize, String filename) {
		try {

			filename = "Statistic_Table.txt";
			FileWriter fw = new FileWriter(filename, true);
			BufferedWriter bw = new BufferedWriter(fw);
			PrintWriter out = new PrintWriter(bw);
			m = new Metrics(log);
			HashMap<String, HashMap<String, Integer>> ATW = m.getATW();
			int sum = 0;
			int val = 0;
			//Out 2:
			out.println("-- NEW LOG --");
			int nrCommits = log.getAllEntries().size();
			//format: Nr Commits & Workload & Nr Authors & Nr Types & Gini Type & Gini Authors & WL Code & WL Test & WL Doc
			out.println(nrCommits + " & " + m.PW(ATW) +" & " + m.NAP() +" & " + m.NTP()+" & " + ((double)Math.round(m.PWS(ATW) * 100000d) / 100000d) +" & "+ ((double)Math.round(m.PIS(ATW) * 100000d) / 100000d)+" & "+m.PTW("code",ATW)+" & " + m.PTW("doc",ATW)+" & " + m.PTW("test",ATW)+" & "+m.PTW("unknown",ATW)+"\n");
			/**
			out.println("%--%NEW LOG%--%");
			out.println("--NAP:" + m.NAP() + "--NTP:" + m.NTP() + "--PIS:" + m.PIS(ATW) + "--RPIS:" + m.RPIS(ATW)
					+ "--PW:" + m.PW(ATW) + "--PWS:" + m.PWS(ATW) + "--RPWS:" + m.RPWS(ATW));

			for (String type : ActivityIdentifier.getActivityLabels(REGEX_LOCATION)) {
				out.print(type + ",");
				for (int i = 0; i < loggingStepSize; i++) {
					// out.print(i+": ");
					if (curves.containsKey(type)) {
						if (curves.get(type).keySet().contains(i)) {
							val = curves.get(type).get(i);
							out.print(val);
							sum += val;

						} else {
							out.print("0");
						}
					} else {
						out.print("-1");
						break;
					}
					if (i != loggingStepSize - 1) {
						out.print(",");
					}
				}
				out.print("--PTW:" + m.PTW(type, ATW) + "--RPTW:" + m.RPTW(type, ATW) + "--PTI:" + m.PTI(type, ATW)
						+ "R--PTI:" + m.RPTI(type, ATW) + "--CNTRL:" + sum + "\n");
						
			}*/
			// out.print(type+",");
			out.close();
		} catch (IOException e) {
			System.err.println("Can't access the statistic file");
		}
	}
	/**
	 * Called by the transformLog method
	 * Appends ChartNode objects to the list of integers that is passed
	 * @param type: Activity type
	 * @param y: Array of integer values that are displayed in a chart
	 * @return: List to be inserted into a chart
	 */
	public ObservableList<XYChart.Data<Integer, Integer>> plot(String type, int... y) {
		final ObservableList<XYChart.Data<Integer, Integer>> dataset = FXCollections.observableArrayList();
		int i = 0;
		int nrOfJumps = 0;
		int max = 0;
		for (int j : y) {
			if (j > max)
				max = j;
		}
		while (i < y.length) {
			// Do we want to display 0 values?
			if (y[i] == 0 && !showZero) {
				i++;
				nrOfJumps++;
				continue;
			}
			final XYChart.Data<Integer, Integer> data = new XYChart.Data<>(i, y[i]);
			ChartNode chtN;
			if (y[i] > 0) {
				if (!getLevel())
					chtN = new ChartNode((i == 0) ? 0 : y[i - nrOfJumps], y[i], max, logsPerUnit.get(type), null, i);
				else
					chtN = new ChartNode((i == 0) ? 0 : y[i - nrOfJumps], y[i], max, null, changePerUnit.get(type), i);
				data.setNode(chtN);
			}
			dataset.add(data);
			i++;
			nrOfJumps = 1;
		}
		return dataset;
	}

	/**
	 * Returns a list of all activity types that exist in the project If commitLevel
	 * is false, returns the activity types for file level If commitLevel is true,
	 * returns the activity types for commit level (with majority vote)
	 * 
	 * @return: ArrayList of all activity types
	 */
	private ArrayList<String> getSortedTypes(boolean fileLevel) {
		m = new Metrics(log);
		ArrayList<String> categories = new ArrayList<String>();
		if (log != null) {
			if (!fileLevel) {
				for (LogEntry l : log.getAllEntries()) {
					if (!categories.contains(l.getType())) {
						categories.add(l.getType());
						// if (l.getType() == "")
						// System.out.println("yi");
					}
				}
			} else {
				for (LogEntry l : log.getAllEntries()) {
					for (ChangeTemplate ct : l.getChangeList()) {
						EnrichedChange ec = (EnrichedChange) ct;
						if (!categories.contains(ec.getActivityLabel())) {
							categories.add(ec.getActivityLabel());
						}
					}
				}
			}
			HashMap<String, HashMap<String, Integer>> ATW = m.getATW();
			Collections.sort(categories, new Comparator<String>() {

				@Override
				public int compare(String o1, String o2) {
					int i1 = m.PTW(o1, ATW);
					int i2 = m.PTW(o2, ATW);

					return Integer.compare(i2, i1);
				}
			});
		}
		return categories;
	}

	/**
	 * Called by the GUI
	 * Displays the Help Dialog
	 */
	public void displayAbout() {
		HelpDialog hp = new HelpDialog(AlertType.INFORMATION);
		hp.showDia();
	}

}
