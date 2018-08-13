package service;

import com.google.maps.GeoApiContext;
import com.google.maps.GeocodingApi;
import com.google.maps.model.GeocodingResult;
import com.google.maps.model.LatLng;
import utility.CachedThreadPool;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

public class APIGoogleMaps {

    private static APIGoogleMaps apiGoogleMaps = null;

    private static String joshLegacyApiKey = "AIzaSyDs1-bWc2o4-NxAyMDX7xD9yxVHDcllSfc";

    private static GeoApiContext context = new GeoApiContext.Builder().apiKey(joshLegacyApiKey)
            .build();


    private APIGoogleMaps() {

    }

    public static APIGoogleMaps getInstance() {
        if (apiGoogleMaps == null) {
            apiGoogleMaps = new APIGoogleMaps();
        }
        return apiGoogleMaps;
    }


    public LatLng getLatLng(String address) throws InterruptedException, ExecutionException {
        //todo should this be caught? or method thrown?

        CachedThreadPool pool = CachedThreadPool.getCachedThreadPool();

        Callable<LatLng> task = () -> {
            GeocodingResult[] results = GeocodingApi.geocode(context, address)
                    .await();
            return results[0].geometry.location;

        };

        Future<LatLng> results = pool.getThreadService()
                .submit(task);

        return results.get();
    }

}
