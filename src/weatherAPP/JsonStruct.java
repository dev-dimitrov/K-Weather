package weatherAPP;
import java.util.ArrayList;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class JsonStruct {
	public Main main;
	public ArrayList<Weather> weather;
	public Integer cod;
	public Sys sys;
	@JsonIgnoreProperties(ignoreUnknown = true)
	public static class Main{
		public String temp;
		
		@JsonProperty("feels_like")
		public String feelsLike;
		
		public String humidity;
	}
	
	@JsonIgnoreProperties(ignoreUnknown = true)
	public static class Weather{
		public String icon;
		
		@JsonProperty("description")
		public String status;
		
	}
	
	@JsonIgnoreProperties(ignoreUnknown = true)
	public static class Sys{
		public String country;
	}
}
