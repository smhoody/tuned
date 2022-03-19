/**
 * Connection to the database which receives Songs and checks the database
 * for any matches or similarities
 * 
 * @author Steven Hoodikoff
 * @date 03/13/2022
 */

package com.app;

import java.util.ArrayList;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mongodb.*;
import java.net.UnknownHostException;


public class MongoDB {
    private static MongoClient mongoClient;
    private static DB database;
    private static DBCollection tracks;


    public MongoDB() throws UnknownHostException {
        mongoClient = new MongoClient(new MongoClientURI("mongodb://localhost:27017"));
        database = mongoClient.getDB("Results");
        tracks = database.getCollection("Tracks");
        // Song song = new Song();
        // song.setId("id123");
        // song.setTempo(12f);
        // song.setKey(3);   
        // song.setDuration(239f);
        // tracks.insert(convert(song));
    }


    /**
     * Search database for similar songs
     * @param track
     * @return ArrayList<Song>  (list of similar tracks)
     */
    public static ArrayList<Song> searchDB(Song track) {
        ArrayList<Song> songs = new ArrayList<Song>();
        DBCursor cursor = checkTrackMatch(track);

        if (cursor != null) { //if the query was found in the db
            while (cursor.hasNext()) { //loop through documents
                DBObject next = cursor.next();
                next.removeField("_id"); //remove _id cuz it wont go into a Song object
                
                ObjectMapper mapper = new ObjectMapper();
                try {
                    //convert document to Song object
                    Song song = mapper.readValue(next.toString(), Song.class);
                    songs.add(song);
                } catch (JsonProcessingException e) {
                    System.out.println(e.getMessage());
                }
            }//end while
        
        }//end if

        return songs;
    }//end searchDB()


    /**
     * Search the database for the exact song, if not found, add it
     * @param track
     * @return DBCursor  (null if nothing found)
     */
    public static DBCursor checkTrackMatch(Song track) {
        BasicDBObject query = new BasicDBObject();
        query.put("id", track.getId()); //set the field value you want to search
        DBCursor cursor = tracks.find(query); //perform DB search for Track
        if (cursor.count() == 0) {
            tracks.insert(convert(track)); //document wasnt found in the db, so add it
            cursor = null;  //return null
        } 
        return cursor;
    }

    /**
     * Convert a Song object into a document for placing in the db
     * @param track
     * @return DBOject
     */
    public static DBObject convert(Song track) {
        return new BasicDBObject("id", track.getId())
                        .append("name", track.getName())
                        .append("artist", track.getArtist())
                        .append("duration", track.getDuration())
                        .append("tempo", track.getTempo())
                        .append("key", track.getKey())
                        .append("mode", track.getMode())
                        .append("danceability", track.getDanceability())
                        .append("energy", track.getEnergy())
                        .append("speechiness", track.getSpeechiness())
                        .append("valence", track.getValence())
                        .append("acousticness", track.getValence())
                        .append("instrumentalness", track.getInstrumentalness());
    }
}