package service;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.maps.GeoApiContext;
import com.google.maps.GeocodingApi;
import com.google.maps.errors.ApiException;
import com.google.maps.model.GeocodingResult;

import java.io.IOException;

public class APIGoogleMaps {

    private static APIGoogleMaps apiGoogleMaps;

    private static String placeApiKey = "AIzaSyDBlnI4yksZyDRL2y7le6SnRymPfTHRTRQ";

    private static String originalApiKey = "AIzaSyB9o0cMPFkqyKOTyifZYMiOdlMXUyMgpzA";

    private APIGoogleMaps() throws InterruptedException, ApiException, IOException {

    }

    public static void main(String[] ars) throws InterruptedException, ApiException, IOException {
        GeoApiContext context = new GeoApiContext.Builder().apiKey(placeApiKey)
                .build();
        GeocodingResult[] results = GeocodingApi.geocode(context, "1600 Amphitheatre Parkway Mountain View, CA 94043")
                .await();
        Gson gson = new GsonBuilder().setPrettyPrinting()
                .create();
        System.out.println(gson.toJson(results[0].addressComponents));
    }

    public APIGoogleMaps getApiGoogleMaps() {
        return apiGoogleMaps;
    }

}
