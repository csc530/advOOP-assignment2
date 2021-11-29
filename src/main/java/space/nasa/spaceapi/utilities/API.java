package space.nasa.spaceapi.utilities;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;
import space.nasa.spaceapi.models.APOD;

import java.io.IOException;
import java.io.StringReader;
import java.lang.reflect.Type;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.TreeSet;

public class API{
	private static final Gson gson = new Gson();
	private static final HttpClient client = HttpClient.newHttpClient();
	private static final String uri = "https://api.nasa.gov/planetary/apod?thumbs=true&api_key=1rp568Tl7gR9976UiFzaPbedFvxnBFFYbdqxXazV";
	private static float progress = 0.00F;
	
	public static APOD getAPOD(){
		return getAPOD(uri);
	}
	
	public static APOD getAPOD(LocalDate date){
		return getAPOD(uri + "&date=" + date);
	}
	
	public static APOD getAPOD(String uri){
		progress = 0;
		HttpRequest request = HttpRequest.newBuilder().uri(URI.create(uri)).build();
		try
		{
			String response = client.send(request, HttpResponse.BodyHandlers.ofString()).body();
			return gson.fromJson(response, APOD.class);
		}
		catch(IOException | InterruptedException e)
		{
			e.printStackTrace();
			return new APOD();
		}
		finally
		{
			progress = 100;
		}
	}
	
	public static float getProgress(){
		return progress;
	}
	
	public static void setProgress(float progress){
		API.progress = progress;
	}
	
	public static TreeSet<APOD> getAPODs(LocalDate start, LocalDate end){
		Type typeOf = new TypeToken<TreeSet<APOD>>(){}.getType();
		//break up requests for many apods
		long days = ChronoUnit.DAYS.between(start, end);
		if(days > 50)
		{
			progress = 0;
			TreeSet<APOD> apods = new TreeSet<>();
			for(long i = 0; i < ((days / 50) + 1); i++)
			{
				//access the start date, must be final, so it doesn't change when accessed by thread
				final LocalDate threadStart = start.plusDays(50 * i);
				//async thread to call numbered amount of APODs query
				Thread query = new Thread(() -> {
					if(threadStart.isAfter(end))
						apods.add(getAPOD(end));
					else
						apods.addAll(getAPODs(threadStart, threadStart.plusDays(50)));
					progress = (float) apods.size() / days;
				});
				query.start();
				//wait for the query to complete
				try {query.join();}
				catch(InterruptedException e) {return null;}
			}
			return apods;
		}
		else
		{
			HttpRequest request = HttpRequest.newBuilder().uri(URI.create(uri + "&start_date=" + start + "&end_date=" + end)).build();
			try
			{
				String response = client.send(request, HttpResponse.BodyHandlers.ofString()).body();
				return gson.fromJson(response, typeOf);
			}
			catch(IOException | InterruptedException e)
			{
				e.printStackTrace();
				return null;
			}
		}
	}
}
