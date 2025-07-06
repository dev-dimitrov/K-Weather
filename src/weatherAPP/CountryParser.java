package weatherAPP;
import java.util.*;

import java.io.*;

/*The only purpose of this class is to translate a country name to a country code
 * Im making my own class bc im lazy to learn now a made library*/
public class CountryParser {
	Map<String, String> countries;
	
	public CountryParser() {
		countries = new HashMap<>();
		loadFromBin();
	}
	
	
	
	public void loadFromTxt() {
		try(BufferedReader b = new BufferedReader(new FileReader("c.txt"))){
			String line = b.readLine();
			while(line != null) {
				String s[] = line.split("#");
				countries.put(s[0].trim(),s[1].trim());
				line = b.readLine();
			}
			System.out.println("Finished the loading from text");
		}
		catch(IOException ex) {
			System.out.println(ex);
		}
	}
	
	
	public void saveAtBin() {
		try (ObjectOutputStream o = new ObjectOutputStream(new FileOutputStream("countries.bin"))){
			o.writeObject(countries);
			System.out.println("Finished the saving at bin");
		}
		catch(IOException ex) {
			System.out.println(ex);
		}
	}
	
	@SuppressWarnings("unchecked") // This is for ignore the warning of line 49
	public void loadFromBin() {
		try (InputStream i = getClass().getResourceAsStream("/weatherAPP/resources/countries.bin");
			ObjectInputStream o = new ObjectInputStream(i)){
			
			 countries = (HashMap<String,String>)o.readObject();
		}
		catch(IOException | ClassNotFoundException ex) {
			System.out.println(ex);
		}
	}
		
	public String getCountryCode(String country) {
		country = country.toLowerCase().trim();
		/*If the param is a value in the map means that is an actual country code and is not necessary to be parsed*/
		return countries.containsValue(country) ? country : countries.get(country);
	}
}
