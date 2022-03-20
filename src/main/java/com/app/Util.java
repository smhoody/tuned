/**
 * Utility functions for Tuned
 * 
 * @author Steven Hoodikoff
 * @date 03/20/2022
 */

package com.app;

import se.michaelthelin.spotify.SpotifyApi;
import se.michaelthelin.spotify.exceptions.SpotifyWebApiException;
import se.michaelthelin.spotify.requests.data.tracks.GetAudioAnalysisForTrackRequest;
import se.michaelthelin.spotify.requests.data.tracks.GetAudioFeaturesForTrackRequest;
import se.michaelthelin.spotify.requests.data.tracks.GetTrackRequest;
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
import java.io.IOException;
import java.io.File;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.StringTokenizer;

public class Util {
    private static final String CLIENT_ID = getCredentials("id");
    private static final String CLIENT_SECRET = getCredentials("secret");
    private static final SpotifyApi spotifyApi = new SpotifyApi.Builder().setClientId(CLIENT_ID).setClientSecret(CLIENT_SECRET).build();
    
    
    public Util() {

    }


    /**
     * Create API object instance
     * @return SpotifyApi
     */
    public static SpotifyApi getSpotifyApi() {
        return spotifyApi;
    }

    /**
     * Search Spotify for song with name of song
     * @param name String name of song (leave empty if searching by ID)
     * @param id Spotify ID of the song (leave empty if searching by name)
     * @return Track
     */
    public static Track searchTrack(String name, String id) {
        Track track = null;

        try {
            //if the id was given, search by id
            if (id.length() != 0) {
                final GetTrackRequest getTrackRequest = spotifyApi.getTrack(id).build();
                track = getTrackRequest.execute();
            } else { //else the track name was given
                final SearchTracksRequest searchTracksRequest = spotifyApi.searchTracks(name).build();
                final Paging<Track> result = searchTracksRequest.execute(); //perform search, returns list of Tracks
                track = result.getItems()[0]; //get first Track
            }
        } catch (IOException | SpotifyWebApiException | ParseException e) {
            System.out.println("Error: " + e.getMessage());
        }
    
        return track;
    }

    /**
     * get properties of a track and instantiate a Song object
     * @param track  Track object
     * @return Song  Song object
     */
    public static Song getTrackAudioProperties(Track track) {
        Song song = new Song();
        String id = track.getId(); //used to search song audio properties
        
        try {
            final GetAudioAnalysisForTrackRequest getAudioAnalysis = spotifyApi.getAudioAnalysisForTrack(id).build();
            final GetAudioFeaturesForTrackRequest getAudioFeatures = spotifyApi.getAudioFeaturesForTrack(id).build();

            final AudioAnalysis audioAnalysis = getAudioAnalysis.execute();
            final AudioFeatures audioFeatures = getAudioFeatures.execute();

            song = convertToSong(track, audioAnalysis, audioFeatures); //create song object
            outputResults(track, audioAnalysis, audioFeatures); //print audio properties of song
            
        } catch (IOException | SpotifyWebApiException | ParseException e) {
            System.out.println("Error: " + e.getMessage());
        }

        return song;
    }


    /**
     * Get Spotify recommendations from query song
     * @param song Song object
     * @return ArrayList<Song> 
     */
    public static ArrayList<Song> getRecommendations(Song song) {
        ArrayList<Song> songs = new ArrayList<Song>();
        try {
            final GetRecommendationsRequest getRecRequest = spotifyApi.getRecommendations().seed_tracks(song.getId()).build();
            final Recommendations recommendations = getRecRequest.execute();
            TrackSimplified[] simpleTracks = recommendations.getTracks();
            for (TrackSimplified t : simpleTracks) {
                songs.add(getTrackAudioProperties(searchTrack("", t.getId()))); //Convert a Spotify Track object into a Song object
            }

        } catch (IOException | SpotifyWebApiException | ParseException e) {
            System.out.println("Error: " + e.getMessage());
        }
        return songs;
    }


    /**
     * Create a Song object from the attributes of the song
     * @param track Track object
     * @param audioAnalysis AudioAnalysis object
     * @param audioFeatures AudioFeatures object
     * @return Song
     */
    public static Song convertToSong(Track track, AudioAnalysis audioAnalysis, AudioFeatures audioFeatures) {
        Song song = new Song(track.getId(), track.getName(),
                                track.getArtists()[0].getName(),
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
    public static void outputResults(Track track, AudioAnalysis audioAnalysis, AudioFeatures audioFeatures) {
        System.out.println("\tSearch");
        System.out.println("Track: " + track.getName() + " by " + track.getArtists()[0].getName());
        System.out.println("Track duration: " + audioAnalysis.getTrack().getDuration() +
                        " \nMain key: " + audioAnalysis.getTrack().getKey() +
                        " \nTempo: " + audioAnalysis.getTrack().getTempo() +
                        " \nDanceability: " + audioFeatures.getDanceability() +
                        " \nEnergy: " + audioFeatures.getEnergy() + 
                        " \nSpeechiness: " + audioFeatures.getSpeechiness() +
                        " \nValence: " + audioFeatures.getValence());
        System.out.println("\n\n");
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
        File infile = new File(folder.getAbsolutePath() + "/util/spotify-credentials.txt");

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
