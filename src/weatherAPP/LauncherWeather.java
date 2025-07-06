package weatherAPP;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.stage.Stage;
// import javafx.stage.StageStyle;
import javafx.scene.Parent;
import javafx.scene.Scene;


public class LauncherWeather extends Application{
	public static String s = "APILogin.fxml";
	@Override
	public void start(Stage primaryStage) {
		try {
			if(checkAPIkey()) { // If the api saved is valid then change the fxml file name to home.fxml
				s = "home.fxml";
			}
			
			Parent root = FXMLLoader.load(getClass().getResource("/weatherAPP/resources/"+s));
			Scene scene = new Scene(root);
			scene.getStylesheets().add(getClass().getResource("/weatherAPP/resources/application.css").toExternalForm());
			primaryStage.setScene(scene);
			primaryStage.show();
			primaryStage.setResizable(false);

		} catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	public static void main(String[] args) {
		launch(args);
	}
	
	public static boolean checkAPIkey() {
		boolean result = false;
		String api = ControllerLogin.loadApiKeyTxt();
		if(api != null) {
			// Make a test if this API key is valid, if not, return false.
			APIresponse test = new APIresponse("https://api.openweathermap.org/data/2.5/weather?&appid="+api+"&units=metric&q=");
			JsonStruct jTest = test.makeCall("monaco");
			result = jTest != null;
		}
		
		
		return result;
	}

}
