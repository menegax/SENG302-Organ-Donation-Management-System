package service;

import com.google.maps.GeoApiContext;
import com.google.maps.GeocodingApi;
import com.google.maps.errors.ApiException;
import com.google.maps.model.GeocodingResult;
import com.google.maps.model.LatLng;

import java.io.IOException;

public class APIGoogleMaps {

    private static APIGoogleMaps apiGoogleMaps;

    private static GeoApiContext context;

    private static String kyleApiKey = "AIzaSyCTrWWBWfYPGFsHvv8sr4tkfpeubCiDIE0";

    private APIGoogleMaps() {
        context = new GeoApiContext.Builder().apiKey(kyleApiKey)
                .build();
    }

    public static APIGoogleMaps getApiGoogleMaps() {
        if (apiGoogleMaps == null) {
            apiGoogleMaps = new APIGoogleMaps();
        }
        return apiGoogleMaps;
    }

    public LatLng geocodeAddress(String address) throws InterruptedException, ApiException, IOException {
        GeocodingResult[] results = GeocodingApi.geocode(context, address)
                .await();
        return results[0].geometry.location;
    }

}
