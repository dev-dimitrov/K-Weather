package weatherAPP;
import java.net.http.*;
import java.net.URI;
import java.net.URISyntaxException;
import java.io.IOException;
import com.fasterxml.jackson.databind.ObjectMapper;


public class APIresponse {
	private String defaultURL;
	private HttpClient client;
	public APIresponse(String r) {
		defaultURL = r;
		client = HttpClient.newHttpClient();
	}
	
	public JsonStruct makeCall(String location) {
		JsonStruct j = null;
		try {
			if(location != null) {
				// Replace the spaces with a code that represents space
				location = location.replace(" ", "%20").trim();
				// Build the request
				HttpRequest request = HttpRequest.newBuilder(new URI(defaultURL+location)).build();
				// Save the response
				HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
				
				
				ObjectMapper mapper = new ObjectMapper();
				
				// Read the response and save it in a JsonStruct object
				j = mapper.readValue(response.body(), JsonStruct.class);
				
				if(j.cod == 200) { // If the HTTP code is 200 (success)...
					j.main.temp = round(j.main.temp);
					j.main.feelsLike = round(j.main.feelsLike);
				}
				else {
					j = null;
				}
			}
		} 
		catch (IOException | InterruptedException | URISyntaxException e) {
		
			e.printStackTrace();
			j = null;
		} 
		return j;
	}
	
	private String round(String t) {
		int temp = (int)Math.round(Double.valueOf(t));
		return temp+"";
	}
	

}
