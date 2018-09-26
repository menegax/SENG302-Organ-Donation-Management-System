package service;

import com.google.maps.GeoApiContext;
import com.google.maps.GeocodingApi;
import com.google.maps.errors.ApiException;
import com.google.maps.model.GeocodingResult;
import com.google.maps.model.LatLng;

import java.io.IOException;

/**
 * This class contains Google API functionality. Designed as a singleton.
 */
public class APIGoogleMaps {

    private static APIGoogleMaps apiGoogleMaps;

    private static GeoApiContext context;

    private static String kyleApiKey = "AIzaSyBDlXgvQkIHbddpwFvEdhzASc2MB13mqLw";

    private APIGoogleMaps() {
        context = new GeoApiContext.Builder().apiKey(kyleApiKey)
                .build();
    }

    /**
     * Retrieve the google maps singleton
     * @return the singleton class
     */
    public static APIGoogleMaps getApiGoogleMaps() {
        if (apiGoogleMaps == null) {
            apiGoogleMaps = new APIGoogleMaps();
        }
        return apiGoogleMaps;
    }


    /**
     * Retrieves the LatLng object from a string address
     * @param address the address to geocode
     * @return LatLng object containing the coordinates
     * @throws InterruptedException InterruptedException
     * @throws ApiException ApiException
     * @throws IOException IOException
     */
    public LatLng geocodeAddress(String address) throws InterruptedException, ApiException, IOException {
        GeocodingResult[] results = GeocodingApi.geocode(context, address)
                .await();
        if (results.length > 0) {
            return results[0].geometry.location;
        } else {
            return null;
        }
    }

}
