package api;

import com.google.gson.JsonArray;
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
     * Creates an http connection and client using the fluent API and gets a response as a json array
     * @param uri API request string
     * @return  JsonArray of the response from the API
     * @throws IOException If a connection to the API cannot be obtained
     */
    private JsonArray getApiResponseAsArray(String uri) throws IOException {
        String response = Request.Get(uri)
                .execute()
                .returnContent().asString();
        return new JsonParser().parse(response).getAsJsonArray();
    }

    /**
     * Builds API query and get the response in a form of a json object
     * @param medicationString -
     * @return - JSONObject of the response from the API
     * @throws IOException - if a connection to the API cannot be obtainted
     */
    public JsonObject getMapiDrugSuggestions(String medicationString) throws IOException { // throw to application layer
        return getApiResponse("http://mapi-us.iterar.co/api/autocomplete?query=" + medicationString);
    }

    /**
     * Builds API query and gets the response as a json array
     * @param medicationString The medication to get the ingredients for
     * @return JsonArray of the response from the API
     * @throws IOException If a connection to the API cannot be obtained
     */
    public JsonArray getMapiDrugIngredients(String medicationString) throws IOException {
        return getApiResponseAsArray("http://mapi-us.iterar.co/api/" + medicationString + "/substances.json");
    }

    /**
     * Builds API query and gets the response as a json object
     * @param drugOne - Drug to be compared with when getting interactions
     * @param drugTwo - Drug to be compared with when getting interactions
     * @return - JsonObject
     * @throws IOException -
     */
    public JsonObject getDrugInteractions(String drugOne, String drugTwo) throws IOException{
        try {
            return getApiResponse(String.format("https://www.ehealthme.com/api/v1/drug-interaction/%s/%s/", drugOne, drugTwo));
        } catch (IOException e) { //make a second API call be this is literally the worst API ever (like really)
            return getApiResponse(String.format("https://www.ehealthme.com/api/v1/drug-interaction/%s/%s/", drugTwo, drugOne));
        }

    }

}
