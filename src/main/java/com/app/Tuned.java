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
import se.michaelthelin.spotify.model_objects.specification.Recommendations;
import se.michaelthelin.spotify.model_objects.miscellaneous.AudioAnalysis;
import org.apache.hc.core5.http.ParseException;

import java.net.UnknownHostException;
import java.util.ArrayList;
import java.io.IOException;


public class Tuned {
    private static final String CLIENT_ID = "a615c99da01740138c5cd1eeffcc272c";
    private static final String CLIENT_SECRET = "0c8f5ab92b4a41e6bd082260119ff326";

    private static final SpotifyApi spotifyApi = new SpotifyApi.Builder().setClientId(CLIENT_ID).setClientSecret(CLIENT_SECRET).build();
    

    public static void main(String[] args) {
        getApiAccess(); //get access token set up
        Song song = getTrackAudioProperties();
        // ArrayList<Song> songs = searchTracks(song);
        
        // for (Song s : songs) {
        //     System.out.println(s);
        // }
    }


    /**
     * get properties of a track and instantiate a Song object
     * @return Song
     */
    public static Song getTrackAudioProperties() {
        Song song = new Song();
        final String QUERY = "forever&more"; //what to search
        
        final SearchTracksRequest searchTracksRequest = spotifyApi.searchTracks(QUERY).build();
        
        try {
            final Paging<Track> result = searchTracksRequest.execute(); //perform search
            System.out.println("\tSearch");
            System.out.println("Results: " + result.getTotal());
            System.out.println("Track: " + result.getItems()[0].getName() + " by " + result.getItems()[0].getArtists()[0].getName());
            String id = result.getItems()[0].getId();

            final GetAudioAnalysisForTrackRequest getAudioAnalysisForTrackRequest = spotifyApi.getAudioAnalysisForTrack(id).build();
            final GetAudioFeaturesForTrackRequest getAudioFeaturesForTrackRequest = spotifyApi.getAudioFeaturesForTrack(id).build();
            final GetRecommendationsRequest getRecommendationsRequest = spotifyApi.getRecommendations()
            //                                                                        // .limit(10)
            //                                                                        // .min_energy(0.85f)
            //                                                                        // .min_danceability(0.8f)
            //                                                                        // .min_energy(0.8f)
                                                                                    .seed_tracks(id)                                                           
                                                                                    .build();

            final AudioAnalysis audioAnalysis = getAudioAnalysisForTrackRequest.execute();
            final AudioFeatures audioFeatures = getAudioFeaturesForTrackRequest.execute();
            final Recommendations recommendations = getRecommendationsRequest.execute();

            song = new Song(id, result.getItems()[0].getName(),
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

            System.out.println("Track duration: " + audioAnalysis.getTrack().getDuration());
            System.out.println("Main key: " + audioAnalysis.getTrack().getKey());
            System.out.println("Tempo: " + audioAnalysis.getTrack().getTempo());
            System.out.println("Danceability: " + audioFeatures.getDanceability() +
                            " \nEnergy: " + audioFeatures.getEnergy() + 
                            " \nSpeechiness: " + audioFeatures.getSpeechiness() +
                            " \nValence: " + audioFeatures.getValence());

                            
            System.out.println("\n\n");
            for (TrackSimplified t : recommendations.getTracks()) {
                System.out.println("\nName: " + t.getName() + "\nURL: " + t.getExternalUrls());
            }
            
        } catch (IOException | SpotifyWebApiException | ParseException e) {
            System.out.println("Error: " + e.getMessage());
        }

        return song;
    }


    /**
     * Search for tracks similar to the track given
     * @param track
     */
    public static ArrayList<Song> searchTracks(Song track) {
        ArrayList<Song> songs = new ArrayList<Song>();
        try{ 
            MongoDB db = new MongoDB();
            songs = db.searchDB(track);
            
        } catch(UnknownHostException e) {
            System.out.println(e.getMessage());
        }
        
        return songs;
    }


    /**
     * Use client credentials to retrieve access token
     */
    public static void getApiAccess() {
        final ClientCredentialsRequest clientCredentialsRequest = spotifyApi.clientCredentials().build();
        try {
            final ClientCredentials clientCredentials = clientCredentialsRequest.execute();
      
            spotifyApi.setAccessToken(clientCredentials.getAccessToken());
          } catch (IOException | SpotifyWebApiException | ParseException e) {
            System.out.println("Error: " + e.getMessage());
          }
    }
    
}

