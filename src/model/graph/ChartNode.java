package model.graph;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;

import javax.swing.JDialog;

import org.joda.time.DateTime;

import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
/**
 * The basic structure (and name) of this class was taken from https://gist.github.com/jewelsea/4681797. 
 * The node enables hovering of elements in the LineChart element. 
**/
import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import model.ChangeTemplate;
import model.EnrichedChange;
import model.LogEntry;
import sun.rmi.log.ReliableLog.LogFile;

/**
 * One node that is displayed in an Area- or Linechart object
 * Idea for this class was derived from Stackoverflow.com
 * Major changes to fit the requirements of this project
 */
public class ChartNode extends StackPane {
	private TableView<EnrichedChange> fileTable = new TableView<EnrichedChange>();
	private TableView<LogEntry> commitTable = new TableView<LogEntry>();
	private boolean filelevel;
	private int timeUnit = 0;
	private HashMap<Integer,ArrayList<LogEntry>> commits = null;
	private HashMap<Integer,ArrayList<EnrichedChange>> files = null;
	
	public ChartNode(int priorValue, int value, int max, HashMap<Integer,ArrayList<LogEntry>> commits, HashMap<Integer,  ArrayList<EnrichedChange>> files, int timeUnit) {
		this.timeUnit = timeUnit;
		this.files = files;
		this.commits = commits;
		setPrefSize(10, 10);
		double relativPos = (double) value/ (double)max;
		//Calculating, where the point is located in the graph so that the labels are not cut off 
		if(relativPos <= 0.6)
			setAlignment(Pos.BOTTOM_CENTER);
		else 
			setAlignment(Pos.TOP_CENTER);
		//System.out.println(boundsInScreen);
		final Label label = createLabel(priorValue, value);
		
		if(commits == null)
			filelevel = true;
		if(files == null)
			filelevel = false;
		
		setOnMouseClicked(new EventHandler<MouseEvent>() {

			@Override
			public void handle(MouseEvent event) {
				Alert alert = new Alert(AlertType.INFORMATION);
				alert.setTitle("Information Dialog");	
				alert.setResizable(true);
				
				if(filelevel) {
					alert.setHeaderText("List of all filechanges during the selected timeframe");
					fileTable = new TableView<EnrichedChange>();
					//alert.setContentText(files.toString());
					
					//Action
					TableColumn<EnrichedChange, String> actionCol = new TableColumn<EnrichedChange, String>("Action");
					actionCol.setMinWidth(50);
					//actionCol.prefWidthProperty().bind(alert.widthProperty().divide(3));
					actionCol.setCellValueFactory(new PropertyValueFactory<EnrichedChange,String>("action"));
					//Path
					TableColumn<EnrichedChange, String> pathCol = new TableColumn<EnrichedChange, String>("Path");
					pathCol.setMinWidth(300);
					pathCol.setCellValueFactory(new PropertyValueFactory<EnrichedChange,String>("path"));
					//ActivityLabel
					TableColumn<EnrichedChange, String> activityCol = new TableColumn<EnrichedChange, String>("Activity Label");
					activityCol.setMinWidth(150);
					activityCol.setCellValueFactory(new PropertyValueFactory<EnrichedChange,String>("activityLabel"));
					//Add to table and display
					fileTable.getColumns().addAll(actionCol, pathCol, activityCol);
					fileTable.setItems(getFileChanges());
					alert.getDialogPane().setContent(fileTable);
					
				}
				else {
					alert.setHeaderText("List of all commits during the selected timeframe");
					commitTable = new TableView<LogEntry>();
					//Author
					TableColumn<LogEntry, String> authorCol = new TableColumn<LogEntry, String>("Author");
					authorCol.setMinWidth(200);
					authorCol.setCellValueFactory(new PropertyValueFactory<LogEntry,String>("author"));
					//Date
					TableColumn<LogEntry, DateTime> dateCol = new TableColumn<LogEntry, DateTime>("Date");
					//dateCol.setMinWidth(150);
					//Solution to alter the displayed DateTime value
					dateCol.setCellFactory(column -> {
				        TableCell<LogEntry, DateTime> cell = new TableCell<LogEntry, DateTime>() {
				            private SimpleDateFormat format = new SimpleDateFormat("dd.MM.yyyy");

				            @Override
				            protected void updateItem(DateTime item, boolean empty) {
				                super.updateItem(item, empty);
				                if(empty) {
				                    setText(null);
				                }
				                else {
				                    this.setText(format.format(item.toDate()));
				                }
				            }
				        };
				        return cell;
				    });
					dateCol.setCellValueFactory(new PropertyValueFactory<LogEntry, DateTime>("Date"));
					//Comment
					TableColumn<LogEntry, String> commentCol = new TableColumn<LogEntry, String>("Comment");
					commentCol.setMinWidth(400);
					commentCol.setCellValueFactory(new PropertyValueFactory<LogEntry,String>("comment"));
					
					commitTable.getColumns().addAll(authorCol,commentCol, dateCol);
					commitTable.setItems(getCommits());
					alert.getDialogPane().setContent(commitTable);
					
				}	
				alert.showAndWait();
				
			}
		});
		
		setOnMouseEntered(new EventHandler<MouseEvent>() {
			@Override
			public void handle(MouseEvent mouseEvent) {
				label.toFront();
				getChildren().add(label);
				setCursor(Cursor.CROSSHAIR);
				
				System.out.println(relativPos);
			}
		});
		setOnMouseExited(new EventHandler<MouseEvent>() {
			@Override
			public void handle(MouseEvent mouseEvent) {
				getChildren().clear();
				setCursor(Cursor.DEFAULT);
			}
		});
	}

	private Label createLabel(int priorValue, int value) {
		final Label label = new Label(value + "");
		label.getStyleClass().addAll("default-color0", "chart-line-symbol", "chart-series-line");
		label.setStyle("-fx-font-size: 12; -fx-font-weight: bold;");

		if (priorValue == value) {
			label.setTextFill(Color.DARKGRAY);
		} else if (value > priorValue) {
			label.setTextFill(Color.LIMEGREEN);
		} else {
			label.setTextFill(Color.RED);
		}

		label.setMinSize(Label.USE_PREF_SIZE, Label.USE_PREF_SIZE);

		label.toFront();
		return label;
	}
	
	private ObservableList<EnrichedChange> getFileChanges() {
		ObservableList<EnrichedChange> ol = FXCollections.observableArrayList();
		if(files != null) {
			for(EnrichedChange ec : files.get(timeUnit)) {
				ol.add(ec);
			}
		}
		return ol;
	}
	
	private ObservableList<LogEntry> getCommits() {
		ObservableList<LogEntry> ol = FXCollections.observableArrayList();
		if(commits != null) {
			for(LogEntry le : commits.get(timeUnit)) {
				ol.add(le);
			}
		}
		return ol;
	}
}
