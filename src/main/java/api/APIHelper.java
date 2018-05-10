package api;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.apache.http.client.fluent.Request;

import java.io.IOException;

public class APIHelper {

    /**
     * Creates a http connection and client using the fluent API and gets a response as a json object
     * @param uri - API request string
     * @return - JSONObject of the response from the API
     * @throws IOException - if a connection to the API cannot be obtainted
     */
    private JsonObject getApiResponse(String uri) throws IOException {
        String response = Request.Get(uri) //using fluent api, as to wrap resource management
                .execute()
                .returnContent().asString();
        return new JsonParser().parse(response).getAsJsonObject();
    }

    /**
     * Builds API query and get the response in a form of a json object
     * @param medicationString -
     * @return - JSONObject of the response from the API
     * @throws IOException - if a connection to the API cannot be obtainted
     */
    public JsonObject getMapiDrugSuggestions(String medicationString) throws IOException { // throw to application layer
        medicationString = medicationString.replaceAll("[^a-zA-Z0-9]", "");
        return getApiResponse("http://mapi-us.iterar.co/api/autocomplete?query=" + medicationString); //remove spaces from query
    }
}
