package com.app;

import se.michaelthelin.spotify.SpotifyApi;
import se.michaelthelin.spotify.exceptions.SpotifyWebApiException;
import se.michaelthelin.spotify.model_objects.miscellaneous.AudioAnalysis;
import se.michaelthelin.spotify.requests.data.tracks.GetAudioAnalysisForTrackRequest;
import org.apache.hc.core5.http.ParseException;

import java.io.IOException;
import java.util.concurrent.CancellationException;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;


public class GetAudioAnalysis {
    private static final String accessToken = "BQDVFEXih9x9j4oqZoGiNJ5VgHa8G-MFT3wjM0-OIrJxhKVuns9S_BeqPty0J9NU1oQVkHyc5IuGqvEE8t9lwEB08SyU7z3teBYCH5TQZG9HIZAATDcCShk1qtjvptJkJUnWfMA";
    private static final String id = "01iyCAUm8EvOFqVWYJ3dVX";

    private static final SpotifyApi spotifyApi = new SpotifyApi.Builder()
        .setAccessToken(accessToken)
        .build();
    private static final GetAudioAnalysisForTrackRequest getAudioAnalysisForTrackRequest = spotifyApi
        .getAudioAnalysisForTrack(id)
        .build();

    public static void getAudioAnalysisForTrack_Sync() {
        try {
            final AudioAnalysis audioAnalysis = getAudioAnalysisForTrackRequest.execute();

            System.out.println("Track duration: " + audioAnalysis.getTrack().getDuration());
        } catch (IOException | SpotifyWebApiException | ParseException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    // public static void getAudioAnalysisForTrack_Async() {
    //     try {
    //         final CompletableFuture<AudioAnalysis> audioAnalysisFuture = getAudioAnalysisForTrackRequest.executeAsync();

    //         // Thread free to do other tasks...

    //         // Example Only. Never block in production code.
    //         final AudioAnalysis audioAnalysis = audioAnalysisFuture.join();

    //         System.out.println("Track duration: " + audioAnalysis.getTrack().getDuration());
    //     } catch (CompletionException e) {
    //         System.out.println("Error: " + e.getCause().getMessage());
    //     } catch (CancellationException e) {
    //         System.out.println("Async operation cancelled.");
    //     }
    // }

    public static void main(String[] args) {
        getAudioAnalysisForTrack_Sync();
        //getAudioAnalysisForTrack_Async();
    }
}

