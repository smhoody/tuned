/**
 * Receives a Spotify song as input and returns a list of similar songs
 * 
 * @author Steven Hoodikoff
 * @date 03/11/2022 
 */

package com.app;

import se.michaelthelin.spotify.SpotifyApi;
import se.michaelthelin.spotify.exceptions.SpotifyWebApiException;
import se.michaelthelin.spotify.requests.data.tracks.GetAudioAnalysisForTrackRequest;
import se.michaelthelin.spotify.requests.data.tracks.GetAudioFeaturesForTrackRequest;
import se.michaelthelin.spotify.requests.data.browse.GetRecommendationsRequest;
import se.michaelthelin.spotify.requests.data.search.simplified.SearchTracksRequest;
import se.michaelthelin.spotify.requests.authorization.client_credentials.ClientCredentialsRequest;
import se.michaelthelin.spotify.model_objects.specification.AudioFeatures;
import se.michaelthelin.spotify.model_objects.specification.Paging;
import se.michaelthelin.spotify.model_objects.specification.Recommendations;
import se.michaelthelin.spotify.model_objects.specification.Track;
import se.michaelthelin.spotify.model_objects.specification.TrackSimplified;
import se.michaelthelin.spotify.model_objects.credentials.ClientCredentials;
import se.michaelthelin.spotify.model_objects.miscellaneous.AudioAnalysis;
import org.apache.hc.core5.http.ParseException;

import java.net.UnknownHostException;
import java.util.ArrayList;
import java.io.IOException;
import java.io.File;
import java.util.Scanner;
import java.util.StringTokenizer;

public class Tuned {
    private static final String CLIENT_ID = getCredentials("id");
    private static final String CLIENT_SECRET = getCredentials("secret");

    private static final SpotifyApi spotifyApi = new SpotifyApi.Builder().setClientId(CLIENT_ID).setClientSecret(CLIENT_SECRET).build();
    

    public static void main(String[] args) {
        getApiAccess(); //get access token set up
        Song song = getTrackAudioProperties();
        ArrayList<Song> songs = searchTracks(song);
        
        for (Song s : songs) {
            System.out.println(s);
        }
    }


    /**
     * get properties of a track and instantiate a Song object
     * @return Song
     */
    public static Song getTrackAudioProperties() {
        final String QUERY = "forever&more"; //what to search
        Song song = new Song();
        final SearchTracksRequest searchTracksRequest = spotifyApi.searchTracks(QUERY).build();
        
        try {
            final Paging<Track> result = searchTracksRequest.execute(); //perform search
            String id = result.getItems()[0].getId();

            final GetAudioAnalysisForTrackRequest getAudioAnalysis = spotifyApi.getAudioAnalysisForTrack(id).build();
            final GetAudioFeaturesForTrackRequest getAudioFeatures = spotifyApi.getAudioFeaturesForTrack(id).build();
            final GetRecommendationsRequest getRecRequest = spotifyApi.getRecommendations()
            //                                                                        // .limit(10)
            //                                                                        // .min_energy(0.85f)
            //                                                                        // .min_danceability(0.8f)
            //                                                                        // .min_energy(0.8f)
                                                                                    .seed_tracks(id)                                                           
                                                                                    .build();

            final AudioAnalysis audioAnalysis = getAudioAnalysis.execute();
            final AudioFeatures audioFeatures = getAudioFeatures.execute();
            final Recommendations recommendations = getRecRequest.execute();


            song = convertToSong(id, result, audioAnalysis, audioFeatures);
            outputResults(result, audioAnalysis, audioFeatures, recommendations);
            
        } catch (IOException | SpotifyWebApiException | ParseException e) {
            System.out.println("Error: " + e.getMessage());
        }

        return song;
    }


    /**
     * Search for tracks similar to the track given
     * @param track
     * @return ArrayList of Songs
     */
    public static ArrayList<Song> searchTracks(Song track) {
        ArrayList<Song> songs = new ArrayList<Song>();
        try{ 
            DataStore ds = new DataStore();
            songs = ds.searchDB(track); //search local database for matches with query song
            
        } catch(UnknownHostException e) {
            System.out.println(e.getMessage());
        }
        
        return songs;
    }


    /**
     * Create a Song object from the attributes of the song
     * @param id
     * @param result
     * @param audioAnalysis
     * @param audioFeatures
     * @return Song
     */
    public static Song convertToSong(String id, Paging<Track> result, AudioAnalysis audioAnalysis, AudioFeatures audioFeatures) {
        Song song = new Song(id, result.getItems()[0].getName(),
                                result.getItems()[0].getArtists()[0].getName(),
                                audioAnalysis.getTrack().getDuration(),
                                audioAnalysis.getTrack().getTempo(),
                                audioAnalysis.getTrack().getKey(),
                                audioFeatures.getMode().getType(),
                                audioFeatures.getDanceability(),
                                audioFeatures.getEnergy(),
                                audioFeatures.getSpeechiness(),
                                audioFeatures.getValence(),
                                audioFeatures.getAcousticness(),
                                audioFeatures.getInstrumentalness());
        return song;
    }

    
    /**
     * Print the attributes of the query song and recommendations
     * @param audioAnalysis
     * @param audioFeatures
     * @param recommendations
     */
    public static void outputResults(Paging<Track> result, AudioAnalysis audioAnalysis, AudioFeatures audioFeatures, Recommendations recommendations) {
        System.out.println("\tSearch");
        System.out.println("Results: " + result.getTotal());
        System.out.println("Track: " + result.getItems()[0].getName() + " by " + result.getItems()[0].getArtists()[0].getName());
        System.out.println("Track duration: " + audioAnalysis.getTrack().getDuration() +
                        " \nMain key: " + audioAnalysis.getTrack().getKey() +
                        " \nTempo: " + audioAnalysis.getTrack().getTempo() +
                        " \nDanceability: " + audioFeatures.getDanceability() +
                        " \nEnergy: " + audioFeatures.getEnergy() + 
                        " \nSpeechiness: " + audioFeatures.getSpeechiness() +
                        " \nValence: " + audioFeatures.getValence());
        System.out.println("\n\n");
        for (TrackSimplified t : recommendations.getTracks()) { //loop through spotify recommended tracks
            System.out.println("\nName: " + t.getName() + "\nURL: " + t.getExternalUrls());
        }
    }


    /**
     * Use client credentials to retrieve access token
     */
    public static void getApiAccess() {
        final ClientCredentialsRequest clientCredentialsRequest = spotifyApi.clientCredentials().build();
        try {
            final ClientCredentials clientCredentials = clientCredentialsRequest.execute();
      
            spotifyApi.setAccessToken(clientCredentials.getAccessToken()); //get and set access token
          } catch (IOException | SpotifyWebApiException | ParseException e) {
            System.out.println("Error: " + e.getMessage());
          }
    }


    /**
     * Read client credentials
     * @param type ('id' or 'secret')
     * @return String (client id or secret)
     */
    public static String getCredentials(String type) {
        String value = "";
        File folder = new File("."); //open current folder
        File infile = new File(folder.getAbsolutePath() + "/util/credentials.txt");

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
    
}//end class