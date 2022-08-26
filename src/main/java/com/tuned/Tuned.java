/**
 * Receives a Spotify song as input and returns a list of similar songs
 * 
 * @author Steven Hoodikoff
 * @date 03/11/2022 
 */

package com.tuned;

import java.io.*; //IOException, File
import java.util.*; //ArrayList, Arrays, logging
import java.util.logging.Logger;
import org.apache.hc.core5.http.ParseException;

import se.michaelthelin.spotify.SpotifyApi;
import se.michaelthelin.spotify.requests.data.tracks.*; //GetAudioAnalysis/FeaturesForTrackRequest,GetTrackRequest 
import se.michaelthelin.spotify.model_objects.specification.*; //AudioFeatures,Paging,Recommendations,Track
import se.michaelthelin.spotify.model_objects.miscellaneous.*; //AudioAnalysis,AudioAnalysisSection,AudioAnalysisSegment
import se.michaelthelin.spotify.model_objects.specification.Track;
import se.michaelthelin.spotify.exceptions.SpotifyWebApiException;
import se.michaelthelin.spotify.requests.data.browse.GetRecommendationsRequest;
import se.michaelthelin.spotify.requests.data.search.simplified.SearchTracksRequest;

public class Tuned {
    private static DataStore ds = new DataStore();

    public static void main(String[] args) {
        Logger logger = Util.openLog();
        SpotifyApi api = Util.getApiAccess(); //get API access set up 

        Track queryTrack = searchTrack(api, "", "4FTKMpQaNomHJtonNaUQFj"); //"name", "id"
        Song song = getTrackAudioProperties(api, queryTrack);

        queryTrack = searchTrack(api, "", "3iIOUEPbQDG2XQNeckP62n"); //"name", "id"
        Song remix_song = getTrackAudioProperties(api, queryTrack);

        System.exit(0);
        ArrayList<Song> songs = getRecommendations(api, song);
        //ArrayList<Song> songs = searchTracks(song);

         for (Song s : songs) {
             System.out.println(s);
         }
    }



    /**
     * Search Spotify for song with name of song
     * @param name String name of song (leave empty if searching by ID)
     * @param id Spotify ID of the song (leave empty if searching by name)
     * @return Track
     */
    public static Track searchTrack(final SpotifyApi SPOTIFY_API, String name, String id) {
        Track track = null;

        try {
            //if the id was given, search by id
            if (id.length() != 0) {
                GetTrackRequest getTrackRequest = SPOTIFY_API.getTrack(id).build();
                track = getTrackRequest.execute();
            } else { //else the track name was given
                SearchTracksRequest searchTracksRequest = SPOTIFY_API.searchTracks(name).build();
                Paging<Track> result = searchTracksRequest.execute(); //perform search, returns list of Tracks
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
    public static Song getTrackAudioProperties(final SpotifyApi SPOTIFY_API, Track track) {
        Song song = null; 
        String id = track.getId(); //used to search song audio properties
        
        try {
            GetAudioAnalysisForTrackRequest getAudioAnalysis = SPOTIFY_API.getAudioAnalysisForTrack(id).build();
            GetAudioFeaturesForTrackRequest getAudioFeatures = SPOTIFY_API.getAudioFeaturesForTrack(id).build();
            
            AudioAnalysis audioAnalysis = getAudioAnalysis.execute();
            AudioFeatures audioFeatures = getAudioFeatures.execute();

            song = new Song(track, audioAnalysis, audioFeatures); //create song object
            Util.outputResults(track, audioAnalysis, audioFeatures); //print audio properties of song
            
            /* 
            AudioAnalysisSegment[] segments = audioAnalysis.getSegments();
            System.out.println(segments.length);
            for (AudioAnalysisSegment s : segments)
                System.out.println("Segment Loudness: " + s.getLoudnessMax()
                                + "\nSegment Pitches: " + Arrays.toString(s.getPitches())
                                + "\nSegment Timbre: " + Arrays.toString(s.getTimbre()));

            AudioAnalysisSection[] sections = audioAnalysis.getSections();
            System.out.println("Sections: " + sections.length);
            for (AudioAnalysisSection s : sections)
               System.out.println("Section Key: " + s.getKey() + "\nSection Tempo: " + s.getTempo());
            */
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
    public static ArrayList<Song> getRecommendations(final SpotifyApi SPOTIFY_API, Song song) {
        ArrayList<Song> songs = new ArrayList<Song>();
        
        try {
            GetRecommendationsRequest getRecRequest = SPOTIFY_API.getRecommendations().seed_tracks(song.getId()).build();
            Recommendations recommendations = getRecRequest.execute();
            TrackSimplified[] simpleTracks = recommendations.getTracks();
            for (TrackSimplified t : simpleTracks) {
                songs.add(getTrackAudioProperties(SPOTIFY_API, searchTrack(SPOTIFY_API, "", t.getId()))); //Convert a Spotify Track object into a Song object and add to list
            }

        } catch (IOException | SpotifyWebApiException | ParseException e) {
            System.out.println("Error: " + e.getMessage());
        }
        return songs;
    }



    /**
     * Search for tracks similar to the track given
     * @param track
     * @return ArrayList of Songs
     */
    public static ArrayList<Song> searchDatabaseTracks(Song track) {
        ArrayList<Song> songs = new ArrayList<Song>();
        
        songs = ds.searchDB(track); //search local database for matches with query song
            
        return songs;
    }
    
    
}//end class