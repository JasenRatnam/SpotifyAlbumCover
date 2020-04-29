
package albumcovers;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Base64;
import javafx.scene.image.Image;

/**
 * 2017-11-12
 * @author Jasen Ratnam 1622549
 */
public class SpotifyController{
    final static private String SPOTIFY_CLIENT_ID     = "9f702a3345c94d80abd5c94e819c8c70";
    final static private String SPOTIFY_CLIENT_SECRET = "55153f8c20b24a4a97eedc089703ef79";
    
    private static boolean debug = false;
    
    // Get the artist ID from spotify.
    public static String getArtistId(String artistName)
    {
        // From an artist string, get the spotify ID
        // Endpoint: https://api.spotify.com/v1/search
        // Parse the JSON output to retrieve the ID
        
        try
        {
            String endpoint = "https://api.spotify.com/v1/search";
            String params = "type=artist&q=" + artistName;
            String jsonOutput = spotifyEndpointToJson(endpoint, params);
            
            // Parse the JSON output in order to retrieve the artist id
            
            JsonElement jelement = new JsonParser().parse(jsonOutput);
            JsonObject rootObject = jelement.getAsJsonObject();
            JsonObject artist = rootObject.get("artists").getAsJsonObject();
            JsonArray items = artist.get("items").getAsJsonArray();
            JsonObject item0 = items.get(0).getAsJsonObject();
            String id = item0.get("id").getAsString();
            
            if(debug)
            {
                System.out.println(rootObject);
            }
            
            return id;
        }
        catch(Exception e)
        {
            // If entered Name is wrong.
            FXMLDocumentController.setMessage("Please enter a valid artist name!");
            
            if(debug)
            {
                System.out.println("Error in getArtistId()");
                e.printStackTrace(); 
            }
        }
        return "";
    }
    
    // Get the image of artist's albums.
    public static ArrayList<Image> getAlbumCoversFromArtist(String spotifyArtistId)
    {
        // Retrieve album cover urls.
        // Transform URL to Image and save all images in an array.
        // Endpoint: https://api.spotify.com/v1/artists/spotifyArtistId/albums
        // Filters for the CA market, and limit search to 50 albums
        
        ArrayList<Image> albumsImages = new ArrayList<>();
        
        String endpoint = "https://api.spotify.com/v1/artists/"+ spotifyArtistId + "/albums";
        String params = "limit=50&market=CA";
        String jsonOutput = spotifyEndpointToJson(endpoint, params);

        // Parse the JSON output in order to retrieve the URL's
        
        JsonElement jelement = new JsonParser().parse(jsonOutput);
        JsonObject rootObject = jelement.getAsJsonObject();
        JsonArray items = rootObject.getAsJsonArray("items");
        
        for(int i = 0; i < items.size(); ++i)
        {
            JsonObject imagesObject = items.get(i).getAsJsonObject();
            JsonArray images = imagesObject.getAsJsonArray("images");
            JsonObject images0 = images.get(0).getAsJsonObject();
            String url = images0.get("url").getAsString();
            // Make URL an Image and save them.
            albumsImages.add(new Image(url));
        }

        if(debug)
        {
            System.out.println(rootObject);
        }
        
        return albumsImages;
    }
    

    // This code will help you retrieve the JSON data from a spotify end point
    // It takes care of the complicated parts such as the authentication and 
    // Connection to the Web API
    private static String spotifyEndpointToJson(String endpoint, String params)
    {
        params = params.replace(' ', '+');

        try
        {
            String fullURL = endpoint;
            if (params.isEmpty() == false)
            {
                fullURL += "?"+params;
            }
            
            URL requestURL = new URL(fullURL);
            
            HttpURLConnection connection = (HttpURLConnection)requestURL.openConnection();
            String bearerAuth = "Bearer " + getSpotifyAccessToken();
            connection.setRequestProperty ("Authorization", bearerAuth);
            connection.setRequestMethod("GET");

            BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));

            String inputLine;
            String jsonOutput = "";
            while((inputLine = in.readLine()) != null)
            {
                jsonOutput += inputLine;
            }
            in.close();
            
            return jsonOutput;
        }
        catch(Exception e)
        {
            if(debug)
            {
                System.out.println("spotifyEndpointToJson");
                e.printStackTrace();
            }
        }
        
        return "";
    }


    // This implements the Client Credentials Authorization Flows
    // Based on the Spotify API documentation
    // It retrieves the Access Token based on the client ID and client Secret  
    private static String getSpotifyAccessToken()
    {
        try
        {
            URL requestURL = new URL("https://accounts.spotify.com/api/token");
            
            HttpURLConnection connection = (HttpURLConnection)requestURL.openConnection();
            String keys = SPOTIFY_CLIENT_ID+":"+SPOTIFY_CLIENT_SECRET;
            String postData = "grant_type=client_credentials";
            
            String basicAuth = "Basic " + new String(Base64.getEncoder().encode(keys.getBytes()));
            
            // Send header parameter
            connection.setRequestProperty ("Authorization", basicAuth);
            
            connection.setRequestMethod("POST");
            connection.setDoOutput(true);

            // Send body parameters
            OutputStream os = connection.getOutputStream();
            os.write( postData.getBytes() );
            os.close();

            BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            
            String inputLine;
            String jsonOutput = "";
            while((inputLine = in.readLine()) != null)
            {
                jsonOutput += inputLine;
            }
            in.close();
            
            JsonElement jelement = new JsonParser().parse(jsonOutput);
            JsonObject rootObject = jelement.getAsJsonObject();
            String token = rootObject.get("access_token").getAsString();

            return token;
        }
        catch(Exception e)
        {
            if(debug)
            {
                System.out.println("Something wrong here... make sure you set your Client ID and Client Secret properly!");
                e.printStackTrace();
            }  
        }
        return "";
    }
}
