package service;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.maps.GeoApiContext;
import com.google.maps.GeocodingApi;
import com.google.maps.errors.ApiException;
import com.google.maps.model.GeocodingResult;
import com.google.maps.model.LatLng;
import utility.SystemLogger;

import java.io.IOException;
import java.util.ArrayList;
import java.util.logging.Level;

public class APIGoogleMaps {

    private static APIGoogleMaps apiGoogleMaps;

    private static String placeApiKey = "AIzaSyDBlnI4yksZyDRL2y7le6SnRymPfTHRTRQ";

    private static String originalApiKey = "AIzaSyB9o0cMPFkqyKOTyifZYMiOdlMXUyMgpzA";

    private static GeoApiContext context = new GeoApiContext.Builder().apiKey(placeApiKey)
            .build();

    private APIGoogleMaps() {
        //todo make into proper singleton
    }

    public static void main(String[] ars) throws InterruptedException, ApiException, IOException {

        GeocodingResult[] results = GeocodingApi.geocode(context, "1600 Amphitheatre Parkway Mountain View, CA 94043")
                .await();
        Gson gson = new GsonBuilder().setPrettyPrinting()
                .create();

        double lat = results[0].geometry.location.lat;
        double lng = results[0].geometry.location.lng;

        System.out.println("Lat: " + lat);
        System.out.println("Lng: " + lng);
        System.out.println("Location: " + results[0].geometry.location);
    }

    public APIGoogleMaps getApiGoogleMaps() {
        return apiGoogleMaps;
    }

    public LatLng getLatLng(String address) throws InterruptedException, ApiException, IOException {
        //todo should this be caught? or method thrown?

        GeocodingResult[] results = GeocodingApi.geocode(context, "1600 Amphitheatre Parkway Mountain View, CA 94043").await();
        return results[0].geometry.location;

    }

}
