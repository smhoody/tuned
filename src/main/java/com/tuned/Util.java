/**
 * Utility functions for Tuned
 * 
 * @author Steven Hoodikoff
 * @date 03/20/2022
 */

package com.tuned;

import se.michaelthelin.spotify.SpotifyApi;
import se.michaelthelin.spotify.exceptions.SpotifyWebApiException;
import se.michaelthelin.spotify.requests.authorization.client_credentials.ClientCredentialsRequest;
import se.michaelthelin.spotify.model_objects.specification.AudioFeatures;
import se.michaelthelin.spotify.model_objects.specification.Track;
import se.michaelthelin.spotify.model_objects.credentials.ClientCredentials;
import se.michaelthelin.spotify.model_objects.miscellaneous.AudioAnalysis;

import org.apache.hc.core5.http.ParseException;

import java.io.IOException;
import java.io.File;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.StringTokenizer;
import java.util.logging.FileHandler;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

public class Util {
    private static final String CLIENT_ID = getCredentials("id");
    private static final String CLIENT_SECRET = getCredentials("secret");
    public static final SpotifyApi SPOTIFY_API = new SpotifyApi.Builder().setClientId(CLIENT_ID).setClientSecret(CLIENT_SECRET).build();
    
    
    public Util() {

    }


    /**
     * Create API object instance
     * @return SpotifyApi
     */
    public static SpotifyApi getSpotifyApi() {
        return SPOTIFY_API;
    }


    /**
     * Print the attributes of the query song and recommendations
     * @param audioAnalysis
     * @param audioFeatures
     * @param recommendations
     */
    public static void outputResults(Track track, AudioAnalysis audioAnalysis, AudioFeatures audioFeatures) {
        System.out.println("\n\tSearch");
        System.out.println("Track: " + track.getName() + " by " + track.getArtists()[0].getName());
        System.out.println("Track duration: " + audioAnalysis.getTrack().getDuration() +
                        " \nMain key: " + audioAnalysis.getTrack().getKey() +
                        " \nTempo: " + audioAnalysis.getTrack().getTempo() +
                        " \nDanceability: " + audioFeatures.getDanceability() +
                        " \nEnergy: " + audioFeatures.getEnergy() + 
                        " \nSpeechiness: " + audioFeatures.getSpeechiness() +
                        " \nValence: " + audioFeatures.getValence() +
                        " \nAcousticness: " + audioFeatures.getAcousticness() +
                        " \nInstrumentalness: " + audioFeatures.getInstrumentalness());
        System.out.println("\n");
    }

    
    /**
     * Create log file
     * @return logger (Logger object)
     */
    public static Logger openLog() {
        Logger logger = Logger.getLogger(Tuned.class.getName());
        
        try {
            File folder = new File("."); //open current folder
            String filePath = folder.getAbsolutePath() + "/src/main/resources/logs.log";

            File f = new File(filePath);
            if (f.createNewFile()) // if file is new 
                logger.info("Created log file");

            FileHandler fh = new FileHandler(filePath);
            logger.addHandler(fh);

            SimpleFormatter formatter = new SimpleFormatter();
            fh.setFormatter(formatter);

        } catch (SecurityException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return logger;
    }


    /**
     * Use client credentials to retrieve access token
     * @return SPOTIFY_API (SpotifyApi object)
     */
    public static SpotifyApi getApiAccess() {
        ClientCredentialsRequest clientCredentialsRequest = SPOTIFY_API.clientCredentials().build();
        try {
            ClientCredentials clientCredentials = clientCredentialsRequest.execute();
      
            SPOTIFY_API.setAccessToken(clientCredentials.getAccessToken()); //get and set access token
          } catch (IOException | SpotifyWebApiException | ParseException e) {
            System.out.println("Error: " + e.getMessage());
          }
        
        return SPOTIFY_API;
    }


    /**
     * Read client credentials
     * @param type ('id' or 'secret')
     * @return String (client id or secret)
     */
    public static String getCredentials(String type) {
        String value = "";
        File folder = new File("."); //open current folder
        File infile = new File(folder.getAbsolutePath() + "/src/main/resources/spotify-credentials.txt");

        if (!infile.exists()) {
            System.out.println("Credentials file not found");
            System.exit(0);
        }
        try {
            Scanner scan = new Scanner(infile);
            String line = scan.nextLine();
            StringTokenizer st = new StringTokenizer(line, ":");
            
            switch(type) {
                case "id":  st.nextToken(); //skip "Client ID: "
                            value = st.nextToken(); //read client id
                            break;
                case "secret":  line = scan.nextLine();
                                st = new StringTokenizer(line, ":");
                                st.nextToken();
                                value = st.nextToken(); //read client secret
                                break;
            }//end switch
            scan.close();
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }

        return value;
    }//end getCredentials()


}
