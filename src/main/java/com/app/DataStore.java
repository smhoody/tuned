/**
 * Connection to the database which receives Songs and checks the database
 * for any matches or similarities
 * 
 * @author Steven Hoodikoff
 * @date 03/13/2022
 */

package com.app;

import java.util.ArrayList;

import org.bson.Document;
import org.bson.conversions.Bson;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.MongoCursor;
// import com.mongodb.client.model.Projections;
// import com.mongodb.client.model.Sorts;
// import com.mongodb.ConnectionString;
// import com.mongodb.MongoClientSettings;
// import com.mongodb.ServerApi;
// import com.mongodb.ServerApiVersion;

import static com.mongodb.client.model.Filters.eq;

//mongodb+srv://smhoody:database-1@cluster0.mjenf.mongodb.net/Tuned?retryWrites=true&w=majority
public class DataStore {
    //private static String uri = "mongodb+srv://smhoody:database-1@cluster0.mjenf.mongodb.net/Tuned";
    private static MongoClient mongoClient;
    private static MongoCollection<Document> tracks;

    
    
    public DataStore() {
        // mongoClient = new MongoClient(new MongoClientURI("mongodb://localhost:27017"));
        // database = mongoClient.getDB("Results");
        // tracks = database.getCollection("Tracks");
        
        // ConnectionString connectionString = new ConnectionString(uri);
        // MongoClientSettings settings = MongoClientSettings.builder()
        //     .applyConnectionString(connectionString)
        //     // .serverApi(ServerApi.builder()
        //     //     .version(ServerApiVersion.V1)
        //     //     .build())
        //     .build();
        // mongoClient = MongoClients.create(settings);

        //to use the altas cluster, just replace the string literal with the connection string
        mongoClient = MongoClients.create("mongodb://localhost:27017");
        MongoDatabase database = mongoClient.getDatabase("Tuned");
        tracks = database.getCollection("Tracks");

        
        
    }


    /**
     * Search database for similar songs
     * @param track
     * @return ArrayList<Song>  (list of similar tracks)
     */
    public static ArrayList<Song> searchDB(Song track) {
        ArrayList<Song> songs = new ArrayList<Song>();
        MongoCursor<Document> cursor = checkTrackMatch(track);

        if (cursor != null) { //if the query was found in the db
            while (cursor.hasNext()) { //loop through documents
                Document next = cursor.next();
                next.remove("_id"); //remove _id cuz it wont go into a Song object
                //System.out.println(next.toJson());
                ObjectMapper mapper = new ObjectMapper();
                try {
                    //convert document to Song object
                    Song song = mapper.readValue(next.toJson(), Song.class);
                    songs.add(song);
                } catch (JsonProcessingException e) {
                    System.out.println(e.getMessage());
                }
            }//end while
            cursor.close();
        }//end if

        return songs;
    }//end searchDB()


    /**
     * Search the database for the exact song, if not found, add it
     * @param track
     * @return DBCursor  (null if nothing found)
     */
    public static MongoCursor<Document> checkTrackMatch(Song track) {
        MongoCursor<Document> cursor = tracks.find().iterator(); //perform DB search for Track
        if (!cursor.hasNext()) {
            tracks.insertOne(convert(track)); //document wasnt found in the db, so add it
            System.out.println("Inserted" + track.toString());
            cursor = null;  //return null
        } 
        return cursor;
    }

    /**
     * Convert a Song object into a document for placing in the db
     * @param track
     * @return DBOject
     */
    public static Document convert(Song track) {
        return new Document("id", track.getId())
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