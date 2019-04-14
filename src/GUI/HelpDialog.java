package GUI;

import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
/**
 * The Dialog that provides help information to the user
 * @author Paul Kneringer
 *
 */
public class HelpDialog extends Alert {
	
	public HelpDialog(AlertType alertType) {
		super(alertType);

	}

	/**
	 * Method to display the Dialog
	 */
	public void showDia() {
		this.setTitle("Information Dialog");
		TextFlow flow = new TextFlow();

		Text text1 =new Text("The tool enables the inspection of GIT event log files.\n\n");
		text1.setFont(Font.font(14));
		text1.setStyle("-fx-font-weight: bold");
		text1.setSmooth(true);
		Text text2 = new Text ("  - Please generate log files via the GIT command line with: ");
		
		Text text3=new Text("git log --name-status --reverse [> filename]\n");
		text3.setStyle("-fx-font-weight: bold");

		Text text4 = new Text("  - Load the log file into the tool by selecting File/Select Log from the menu bar\n"
				+"  - Alternatively, you can load an analysis that was previously saved, by using the File/Load functionality\n"
				+"  - Once the log is loaded into the tool,");
		
		Text text5 = new Text(" activity labels ");
		text5.setStyle("-fx-font-weight: bold");
		
		Text text6 = new Text("are automatically attached.\n"
				+ "  - This may take a while for large repositories. Once the log file is loaded, feel free to save it in order to\n"
				+"     reduce loading times for the next analysis on the same data!");
		//text3.setStyle("-fx-font-weight: regular");
		text3.setWrappingWidth(600);
		flow.getChildren().addAll(text1, text2, text3, text4, text5,text6);
		
		this.setHeaderText("About the ActiVCS repository mining tool: ");
	
		flow.setPrefWidth(600);  
		
		this.setWidth(600);
		this.setHeight(450);
		this.getDialogPane().setContent(flow);
		/**
		alert.setContentText();
				("The tool enables the inspection of GIT event log files.\n"
				+ "Please generate log files via the GIT command line with the following command: GIT log --name-status --reverse \n"
				+ "Once the log is loaded into the tool, activity labels are automatically attached.\n"
				+ "This may take a while for large repositories so once the log file is loaded feel free to save it in order to reduce loading times for the next analysis on the data you want to perform!");
		**/
		this.showAndWait();
	}
}
