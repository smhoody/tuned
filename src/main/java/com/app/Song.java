/**
 * Object for storing song attributes
 * 
 * @author Steven Hoodikoff
 * @date 03/17/2022
 */

package com.app;

import java.lang.StringBuilder;

import se.michaelthelin.spotify.model_objects.miscellaneous.AudioAnalysis;
import se.michaelthelin.spotify.model_objects.specification.AudioFeatures;
import se.michaelthelin.spotify.model_objects.specification.Track;

public class Song {
    private String id = "";
    private String name = "";
    private String artist = "";
    private Float duration = 0f;
    private Float tempo = 0f;
    private Integer key = 0;
    private int mode = 0;
    private Float danceability = 0f;
    private Float energy = 0f;
    private Float speechiness = 0f;
    private Float valence = 0f;
    private Float acousticness = 0f;
    private Float instrumentalness = 0f;

    public Song() {

    }

    /**
     * Overloaded constructor using custom properties
     * @param id
     * @param name
     * @param artist
     * @param duration
     * @param tempo
     * @param key
     * @param mode
     * @param danceability
     * @param energy
     * @param speechiness
     * @param valence
     * @param acousticness
     * @param instrumentalness
     */
    public Song(String id, String name, String artist, Float duration, Float tempo, Integer key, int mode,
                Float danceability, Float energy, Float speechiness,
                Float valence, Float acousticness, Float instrumentalness) 
    {
        this.id = id;
        this.name = name;
        this.artist = artist;
        this.duration = duration;
        this.tempo = tempo;
        this.key = key;
        this.mode = mode;
        this.danceability = danceability;
        this.energy = energy;
        this.speechiness = speechiness;
        this.valence = valence;
        this.acousticness = acousticness;
        this.instrumentalness = instrumentalness;
    }

    
    /**
     * Overloaded constructor using Track, AudioAnalysis, AudioFeatures
     * @param track
     * @param audioAnalysis
     * @param audioFeatures
     */
    public Song(Track track, AudioAnalysis audioAnalysis, AudioFeatures audioFeatures) {
        this.id = track.getId();
        this.name = track.getName();
        this.artist = track.getArtists()[0].getName();
        this.duration = audioAnalysis.getTrack().getDuration();
        this.tempo = audioAnalysis.getTrack().getTempo();
        this.key = audioAnalysis.getTrack().getKey();
        this.mode = audioFeatures.getMode().getType();
        this.danceability = audioFeatures.getDanceability();
        this.energy = audioFeatures.getEnergy();
        this.speechiness = audioFeatures.getSpeechiness();
        this.valence = audioFeatures.getValence();
        this.acousticness = audioFeatures.getAcousticness();
        this.instrumentalness = audioFeatures.getInstrumentalness();
    }



    // ACCESSORS

    public String getId() {
        return this.id;
    }

    public String getName() {
        return this.name;
    }

    public String getArtist() {
        return this.artist;
    }

    public Float getDuration() {
        return this.duration;
    }

    public Float getTempo() {
        return this.tempo;
    }

    public int getKey() {
        return this.key;
    }

    public int getMode() {
        return this.mode;
    }

    public Float getDanceability() {
        return this.danceability;
    }

    public Float getEnergy() {
        return this.energy;
    }

    public Float getSpeechiness() {
        return this.speechiness;
    }

    public Float getValence() {
        return this.valence;
    }

    public Float getAcousticness() {
        return this.acousticness;
    }

    public Float getInstrumentalness() {
        return this.instrumentalness;
    }


    // MUTATORS

    public void setId(String id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setArtist(String artist) {
        this.artist = artist;
    }

    public void setDuration(Float duration) {
        this.duration = duration;
    }

    public void setTempo(Float tempo) {
        this.tempo = tempo;
    }

    public void setKey(int key) { 
        this.key = key;
    }

    public void setMode(int mode) {
        this.mode = mode;
    }

    public void setDanceability(Float danceability) {
        this.danceability = danceability;
    }

    public void setEnergy(Float energy) {
        this.energy = energy;
    }

    public void setSpeechiness(Float speechiness) {
        this.speechiness = speechiness;
    }

    public void setValence(Float valence) { 
        this.valence = valence;
    }

    public void setAcousticness(Float acousticness) {
        this.acousticness = acousticness;
    }

    public void setInstrumentalness(Float instrumentalness) {
        this.instrumentalness = instrumentalness;
    }

    public boolean equals(Song song) {


        return true;
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("\nID: " + id);
        sb.append("\nName: " + name);
        sb.append("\nArtist: " + artist);
        sb.append("\nDuration: " + duration);
        sb.append("\nTempo: " + tempo);
        sb.append("\nKey: " + key);
        sb.append("\nMode: " + mode);
        sb.append("\nDanceability: " + danceability);
        sb.append("\nEnergy: " + energy);
        sb.append("\nSpeechiness: " + speechiness);
        sb.append("\nValence: " + valence);
        sb.append("\nAcousticness: " + acousticness);
        sb.append("\nInstrumentalnnes: " + instrumentalness);
        return sb.toString();
    }
}
