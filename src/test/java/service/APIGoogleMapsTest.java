//package service;
//
//import static org.junit.jupiter.api.Assertions.*;
//
//import com.google.maps.model.LatLng;
//import org.junit.jupiter.api.Test;
//
//import java.util.concurrent.ExecutionException;
//
//class APIGoogleMapsTest {
//
//    private APIGoogleMaps apiGoogleMaps = APIGoogleMaps.getInstance();
//
//    /**
//     * Tests to ensure the google maps api can convert from street address to lat and long
//     */
//    @Test
//    public void addressToLatLng() throws ExecutionException, InterruptedException {
//        String givenAddress = "1600 Amphitheatre Parkway Mountain View, CA 94043";
//        assertEquals(new LatLng(37.42262310,-122.08458390), apiGoogleMaps.getLatLng(givenAddress));
//    }
//
//}