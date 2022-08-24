/**
 * Receives a Spotify song as input and returns a list of similar songs
 * 
 * @author Steven Hoodikoff
 * @date 03/11/2022 
 */

package com.tuned;

import se.michaelthelin.spotify.model_objects.specification.Track;
import java.util.ArrayList;


public class Tuned {
    private static DataStore ds = new DataStore();

    public static void main(String[] args) {
        Util.getApiAccess(); //get access token set up
        Track queryTrack = Util.searchTrack("", "4FTKMpQaNomHJtonNaUQFj"); //"name", "id"
        Song song = Util.getTrackAudioProperties(queryTrack);

        queryTrack = Util.searchTrack("", "3iIOUEPbQDG2XQNeckP62n"); //"name", "id"
        Song remix_song = Util.getTrackAudioProperties(queryTrack);

        System.exit(0);
        ArrayList<Song> songs = Util.getRecommendations(song);
        //ArrayList<Song> songs = searchTracks(song);

         for (Song s : songs) {
             System.out.println(s);
         }
    }


    /**
     * Search for tracks similar to the track given
     * @param track
     * @return ArrayList of Songs
     */
    public static ArrayList<Song> searchTracks(Song track) {
        ArrayList<Song> songs = new ArrayList<Song>();
        
        songs = ds.searchDB(track); //search local database for matches with query song
            
        return songs;
    }
    
    
}//end class