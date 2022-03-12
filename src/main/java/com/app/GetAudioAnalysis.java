package com.app;

import se.michaelthelin.spotify.SpotifyApi;
import se.michaelthelin.spotify.exceptions.SpotifyWebApiException;
import se.michaelthelin.spotify.model_objects.miscellaneous.AudioAnalysis;
import se.michaelthelin.spotify.requests.data.tracks.GetAudioAnalysisForTrackRequest;
import se.michaelthelin.spotify.model_objects.credentials.ClientCredentials;
import se.michaelthelin.spotify.requests.data.search.simplified.SearchTracksRequest;
import se.michaelthelin.spotify.requests.authorization.client_credentials.ClientCredentialsRequest;
import se.michaelthelin.spotify.model_objects.specification.Paging;
import se.michaelthelin.spotify.model_objects.specification.Track;
import org.apache.hc.core5.http.ParseException;

import java.io.IOException;


public class GetAudioAnalysis {
    private static final String CLIENT_ID = "a615c99da01740138c5cd1eeffcc272c";
    private static final String CLIENT_SECRET = "0c8f5ab92b4a41e6bd082260119ff326";

    private static final SpotifyApi spotifyApi = new SpotifyApi.Builder().setClientId(CLIENT_ID).setClientSecret(CLIENT_SECRET).build();
    

    /**
     * get properties of a song
     */
    public static void getAudioAnalysisForTrack_Sync() {
        final String QUERY = "SAWCE";

        final SearchTracksRequest searchTracksRequest = spotifyApi.searchTracks(QUERY).build();

        try {
            final Paging<Track> track = searchTracksRequest.execute();
            System.out.println("\tSearch");
            System.out.println("Results: " + track.getTotal());
            System.out.println("Track: " + track.getItems()[0].getName() + " by " + track.getItems()[0].getArtists()[0].getName());
            String id = track.getItems()[0].getId();

            final GetAudioAnalysisForTrackRequest getAudioAnalysisForTrackRequest = spotifyApi.getAudioAnalysisForTrack(id).build();
            final AudioAnalysis audioAnalysis = getAudioAnalysisForTrackRequest.execute();

            System.out.println("Track duration: " + audioAnalysis.getTrack().getDuration());
            System.out.println("Main key: " + audioAnalysis.getTrack().getKey());
            System.out.println("Tempo: " + audioAnalysis.getTrack().getTempo());
            //System.out.println("Bars: " + audioAnalysis.getBars().toString());

            

        } catch (IOException | SpotifyWebApiException | ParseException e) {
            System.out.println("Error: " + e.getMessage());
        }
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
    public static void main(String[] args) {
        getApiAccess(); //get access token set up
        getAudioAnalysisForTrack_Sync();
    }
}

