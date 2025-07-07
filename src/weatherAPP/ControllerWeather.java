package weatherAPP;
import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;


public class ControllerWeather implements Initializable{
	
	@FXML 
	private Label tempLabel;
	
	@FXML
	private ImageView imageWeather;
	
	@FXML
	private ImageView backgroundImage;
	
	@FXML
	private Label statusLabel;

	@FXML
	private TextField input;
	
	private CountryParser c;
	
	@FXML 
	private Label locationLabel;
	
	private APIresponse api;

	@FXML
	private Menu fav;

	@FXML
	private ImageView favIcon;
	
	@FXML
	private Label feelsLikeLabel;
	
	@FXML
	private ImageView logoImage;
	
	@FXML
	private ImageView humImage;
	
	@FXML
	private Label humLabel;
	
	private ArrayList<String> favLocations;
	
	public static String apiKey;

	@FXML
	private MenuItem changeItem;

	public boolean isInCelsius;
	
	public JsonStruct j;
	
	@FXML
	private Button hiddenButton;
	
	@FXML
	private Label userGuideLabel;
	
	@Override
 	public void initialize(URL arg0, ResourceBundle arg1) {
		// Tests if it is in temp file
		apiKey = ControllerLogin.loadApiKeyTxt(true);
		// if still null, then load it from the apikey.txt file
		if(apiKey == null){
			apiKey = ControllerLogin.loadApiKeyTxt(false);
		}
		setupHomeScene();
		isInCelsius = true;
	}
	
	// Method that is executed when enter is pressed 
	public void searchLocation(ActionEvent e) {
		j = null;
		
		// Get the input
		String location = input.getText().toLowerCase();
		// Split the text into 2 parts, the city and the country name.
		
		String split[] = location.trim().split(",");
		
		if(split.length == 2) { // if the split length is 2 (the location and the country name or code)
			
			// if its already a country code keeps running with no problem at all.
			split[1] = c.getCountryCode(split[1]);
			
			if(split[1] != null) {
				j = api.makeCall(split[0]+","+split[1]);
			}
			
		}
		else { // Only the name, without country name/code;
			j = api.makeCall(split[0]);
			
			// add to the split the country code to prevent exceptions using the prettierPhrase method.
			// Only if the request was successful
			if(j != null) {
				split = Arrays.copyOf(split, 2);
				split[1] = j.sys.country;
			}
		}
		
		if(j != null) {
			String r[] = putUnits(j.main.temp, j.main.feelsLike);
			changeWeatherInfo(prettierPhrase(split).trim(), r[0], r[1] ,j.weather.get(0).status, j.weather.get(0).icon, j.main.humidity);
			
			
			// Check the favorite status of this location 
            changeFavIcon(isInFav(locationLabel.getText()));
			toggleVisible(true);
		}
		else {
			notFound();
		}
		
	}
	

	// Method that changes the background image and the weather icon by the icon code of the API response
	public void changeBackgroundIcon(String icon) {
		logoImage.setVisible(false);
		String dir = "/weatherAPP/resources/images/"+icon+"-gradient.png";
		String dir2 = "/weatherAPP/resources/images/"+icon+".png";
		Image i = new Image(getClass().getResourceAsStream(dir));
		backgroundImage.setImage(i);
		i = new Image(getClass().getResourceAsStream(dir2));
		imageWeather.setImage(i);
	}

	// Method to change the scene to a not found state
	public void notFound() {
		toggleVisible(false);
		locationLabel.setText("Location not found!!");
		locationLabel.setVisible(true);
		changeFavIcon(false);
		logoImage.setVisible(false);
	}
	
	
	// Hide and show all the weather elements
	private void toggleVisible(boolean a) {
		locationLabel.setVisible(a);
		tempLabel.setVisible(a);
		statusLabel.setVisible(a);
		imageWeather.setVisible(a);
		feelsLikeLabel.setVisible(a);
		humImage.setVisible(a);
		humLabel.setVisible(a);
		hiddenButton.setVisible(a);
		userGuideLabel.setVisible(false);
		
	}
	
	
	private String prettierPhrase(String phrase[]) {
		String p = "";
		p += phrase[0]+", "+phrase[1].toUpperCase();
		p = p.substring(0,1).toUpperCase() + p.substring(1);
		
		return p;
	}
	
	
	// method executed when the menuItem Credits is clicked
	public void showCredits(ActionEvent e) {
		changeBackgroundIcon("01d");
		input.setText(null);
		toggleVisible(false);
		feelsLikeLabel.setText("K-Weather v1");
		
		statusLabel.setText("Powered by JavaFX.");
		humLabel.setText("Humidity icon by icons8.com");
		humLabel.setVisible(true);
		statusLabel.setVisible(true);
		feelsLikeLabel.setVisible(true);
		changeFavIcon(false);
		logoImage.setVisible(true);
	}
	
	// method activated when the star is pressed 
	public void toggleFav() {
		
		if(tempLabel.isVisible()) { /*
		A way to check if the location is valid or not to fav or unfav.
		If the location is not found, the templabel is not visible*/
			String l = locationLabel.getText();
			
			if(!isInFav(l)) {
				addFavorite(l);
				saveFavLocations();
			}
			else {
				removeFavorite(l);
				saveFavLocations();
			}
			
		}
	}
	
	@SuppressWarnings("unused")
	// Add every location to the menu list, and add it to the arraylist to save it later.
	public void addFavorite(String l) {
		changeFavIcon(true);
		MenuItem f1 = new MenuItem(); 
		f1.setText(l);	// Create the menuItem and set text
		f1.setOnAction(e -> putLocationByFav(l)); // Putting an action event to every new menuItem
		fav.getItems().add(f1);
		favLocations.add(l);
		saveFavLocations();
		// Make here the action listeners for each MenuItem
	}
	
	
	//remove favorite from menu list and arraylist
	public void removeFavorite(String l) {
		changeFavIcon(false);
		Iterator<MenuItem> iterator = fav.getItems().iterator();
		while(iterator.hasNext()) {
			MenuItem i = iterator.next();
			if(i.getText().equals(l)) {
				iterator.remove(); // removing from the menuItem list
				favLocations.remove(l);
				saveFavLocations();
				break;
			}
		}
	}
	

	// change the star
	public void changeFavIcon(boolean a) {
		Image i = a ? new Image(getClass().getResourceAsStream("/weatherAPP/resources/images/fav.png")) : new Image(getClass().getResourceAsStream("/weatherAPP/resources/images/no-fav.png"));
		favIcon.setImage(i);
	}
	
	
	// check if a location is already in favorite
	public boolean isInFav(String l) {
		return favLocations.contains(l);
	}
	
	// Similar to the method searchLocation, but this is only used when you click a starred location
	public void putLocationByFav(String l) {
		toggleVisible(true);
		j = api.makeCall(l);
		input.setText(l);
		String r[] = putUnits(j.main.temp, j.main.feelsLike);
		changeWeatherInfo(l, r[0], r[1], j.weather.get(0).status, j.weather.get(0).icon,j.main.humidity);
		changeFavIcon(true);
	}
	
	
	// Used by putLocationByFav and searchLocation, to show the API response info
	public void changeWeatherInfo(String location, String temp, String feelsLike, String status, String background, String humidity) {
		status = status.substring(0,1).toUpperCase()+status.substring(1);
		changeBackgroundIcon(background);
		locationLabel.setText(location);
		locationLabel.setText(location);
		statusLabel.setText(status);
		feelsLikeLabel.setText("Feels like "+feelsLike);
		tempLabel.setText(temp);
		humLabel.setText(humidity+"%");
		// Simply to put the first letter capital of the status
	}
	
	
	// Saving the starred locations
	public void saveFavLocations() {
		try(ObjectOutputStream o = new ObjectOutputStream(new FileOutputStream("favorites.bin"))){
			o.writeObject(favLocations);
		}
		catch(IOException ex) {
			System.out.println(ex);
		}
	}
	
	@SuppressWarnings("unchecked")
	// Loading the fav locations, only executed once when the app starts
	public void loadFavLocations() {
		try(ObjectInputStream o = new ObjectInputStream(new FileInputStream("favorites.bin")) ){
			favLocations = ((ArrayList<String>) o.readObject());
		}
		catch(ClassNotFoundException | IOException ex) {
			System.out.println(ex);
		}
		
		if(!favLocations.isEmpty()) {
			for(String location: favLocations) {
				MenuItem l = new MenuItem();
				l.setText(location);
				// Settin an on Action to every menuItem that loads
				l.setOnAction(e -> this.putLocationByFav(location));
				// Add the menuItem to the MenuList (Object Menu)
				fav.getItems().add(l);
			}
		}
	}
	
	public void toggleLogoImage(boolean a) {
		logoImage.setVisible(a);
	}

	public void setupHomeScene() {
		c = new CountryParser();
		
		// creation of the api object with the default url
		api = new APIresponse("https://api.openweathermap.org/data/2.5/weather?&appid="+apiKey+"&units=metric&q=");
		toggleVisible(false);
		favLocations = new ArrayList<>();
		loadFavLocations();
	}
	
	public static void setApiKey(String a) {
		apiKey = a;
	}
	
	public void changeApiKey(ActionEvent e) throws IOException {
		
		removeSavedApi();
		// Changing scene procedure
		Parent root = FXMLLoader.load(getClass().getResource("/weatherAPP/resources/APILogin.fxml"));
		Scene scene = new Scene(root);
		// This is different bc the menuItem can't get in which window is so here we are asking it to his parent (Menu)
		Stage stage =  (Stage) changeItem.getParentPopup().getOwnerWindow();
		stage.setScene(scene);
		scene.getStylesheets().add(getClass().getResource("/weatherAPP/resources/application.css").toExternalForm());
		stage.show();
	}
	
	// Actually it doesn't remove, just overwrites the file leaving it empty xd
	public void removeSavedApi() {
		try (BufferedWriter b = new BufferedWriter(new FileWriter("apikey.txt"))){
			b.write("");
		}
		catch(IOException ex) {
			System.out.println(ex);
		}
	}
	
	public void openGithub(MouseEvent e) {
		if(logoImage.isVisible()) { // check if its in About screen
			linkOpener("https://github.com/su-kaizen");
		}
	}
	
	
	/*Used in both controllers*/
	public static void linkOpener(String link) {
		try {
	        if (System.getProperty("os.name").contains("Windows")) {
	        	 new ProcessBuilder("cmd","/c","start "+link).inheritIO().start().waitFor();
	        }
			else{
				new ProcessBuilder("xdg-open",link).start();
			}
	        
	    } catch (IOException | InterruptedException ex) {
	    	System.out.println(ex);
	    }
	}
	
	/*At index 0 saves the temp and at index 1 saves the feelslike temp*/
	public String[] putUnits(String unit1, String unit2){
		String result[] = new String[2];
		int u1 = Integer.valueOf(unit1);
		int u2 = Integer.valueOf(unit2);
		
		if(isInCelsius) { // Just add the celcius unit
			result[0] = j.main.temp+"ºC";
			result[1] = j.main.feelsLike+"ºC";
		}
		else { // Convert to farenheit
			result[0] = ((u1*9/5) + 32)+"ºF";
			result[1] = ((u2*9/5) + 32)+"ºF";
		}
		
		return result;
	}
	
	public void toggleUnits(ActionEvent e) {
		// Only works it isn't visible the credits
		if(!statusLabel.getText().equals("Powered by JavaFX.")) {
			isInCelsius = !isInCelsius; //Invert the value of the boolean
			String r[] = putUnits(j.main.temp, j.main.feelsLike);
			tempLabel.setText(r[0]);
			feelsLikeLabel.setText("Feels like "+r[1]);
		}
		
		
	}
	
	public void showGuide(ActionEvent e) {
		changeBackgroundIcon("01d");
		logoImage.setVisible(false);
		changeFavIcon(false);
		input.setText("");
		toggleVisible(false);
		locationLabel.setText("Thank you for using K-Weather!");
		locationLabel.setVisible(true);
		userGuideLabel.setVisible(true);
	}
	
}
