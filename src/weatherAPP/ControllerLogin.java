package weatherAPP;

import java.io.IOException;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import java.io.FileWriter;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;

public class ControllerLogin{
	@FXML
	private Hyperlink hyperLink;
	
	@FXML
	private TextField inputLogin;
	
	@FXML
	private Label invalidLabel;
	
	@FXML
	private CheckBox rememberCheckBox;
	
	private Stage stage;
	
	private Scene scene;
	
	private Parent root;
	
	public void getApiKey(ActionEvent e) throws IOException{
		invalidLabel.setVisible(false);
		String input = inputLogin.getText();
		
		APIresponse apiTest = new APIresponse("https://api.openweathermap.org/data/2.5/weather?&appid="+input+"&units=metric&q=");
		
		JsonStruct jTest = apiTest.makeCall("monaco");
		
		if(jTest == null) { // If the test is null is bc of the incorrect api key
			invalidLabel.setVisible(true);
		} 
		else {
			if(rememberCheckBox.isSelected()) {
				saveApiKey(input, false);
			}
			else{
				saveApiKey(input, true);
			}
			changeScene(e, input);
		}
	}


	public void changeScene(ActionEvent e, String apiKey) throws IOException {
		FXMLLoader loader = new FXMLLoader(getClass().getResource("/weatherAPP/resources/home.fxml"));
		root = loader.load();
		/* linking the new scene to a controller (in this Case ControllerWeather)
		 * if you don't link it, the controller tries to interact with elements that are not injected yet and
		 * throws a NullPointerException*/
		ControllerWeather c = loader.getController();
		/* To move information between controllers 
		 * (even if It's the same class), you need to make it via public methods or variables.
		 * */
		
		 /*	Saving the valid APIKey in the other controller (ControllerWeather)
			ControllerWeather.setApiKey(apiKey); This crap doesn't work to send data through the other Controller
		 	An improvised solution: make a temporary file to save there the api and then remove it.
		 */
		
		
		
		stage = (Stage) ((Node)e.getSource()).getScene().getWindow();	
		scene = new Scene(root);
		// linking the css file to the home scene (the same as in the launcher class)
		scene.getStylesheets().add(getClass().getResource("/weatherAPP/resources/application.css").toExternalForm());
		stage.setScene(scene);
		stage.show();

	}
	
	public static void saveApiKey(String api, boolean temp) {
		String fileName = temp ? "temp.txt" : "apikey.txt";
		try(BufferedWriter b = new BufferedWriter(new FileWriter(fileName))){
			b.write(api);
			if(!temp){
				b.write("\n--------------------------------\nYou can modify the apikey in this file, the program will check if its valid");
			}
		}
		catch(IOException ex) {
			System.out.println(ex);
		}
	}
	public static String loadApiKeyTxt(boolean temp) {
		String api = "";
		String fileName = temp ? "temp.txt" : "apikey.txt";
		try(BufferedReader b = new BufferedReader(new FileReader(fileName))){
			api = b.readLine();
		}
		catch(IOException ex) {
			System.out.println(ex);
			api = null;
		}

		if(temp){
			try(BufferedWriter b = new BufferedWriter(new FileWriter(fileName))){
				b.write("");
			}
			catch (IOException ex){
				System.out.println(ex);
			}
		}
		
		return api;
	}
	
	public void openLink(ActionEvent e) {
		ControllerWeather.linkOpener("https://openweathermap.org/price");
	}
}
