package xyz.yeems214.MusicPlayer.Extensions;

import xyz.yeems214.MusicPlayer.Interfaces.NowPlaying;

import javax.sound.sampled.Clip;
import javax.sound.sampled.FloatControl;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import static xyz.yeems214.MusicPlayer.Main.*;

// This source code holds the tracking of the song's timer and displaying its lyrics on console.
public class ElapsedTimeTracker extends Thread {
    private final Clip clip;
    private boolean isPaused = false;
    private volatile boolean running = true;
    private static String filePath;
    private String lyricsFile;
    private String title;
    private String artist;
    private String album;
    private String albumArt;
    private List<LyricLine> lyrics;
    private long startTime;
    private int currentLyricIndex = 0;
    private Thread lyricsThread;
    private int counter = 0;

    public ElapsedTimeTracker(Clip clip, String filePath, String lyricsFile, String title, String artist, String album) {
        this.clip = clip;
        this.filePath = filePath;
        this.lyricsFile = lyricsFile;
        this.title = title;
        this.artist = artist;
        this.album = album;
        this.lyrics = loadLyrics(lyricsFile);
    }

    public ElapsedTimeTracker(Clip clip) {
        this.clip = clip;
        this.filePath = filePath;
    }

    public void pause() {
        isPaused = true;
    }

    public void resumeSong() {
        isPaused = false;
    }

    public void stopRunning() {
        running = false;
        filePath = null;
    }

    private static class LyricLine {
        private long timestamp;
        private String lyric;

        public LyricLine(long timestamp, String lyric) {
            this.timestamp = timestamp;
            this.lyric = lyric;
        }

        public long getTimestamp() {
            return timestamp;
        }

        public String getLyric() {
            return lyric;
        }
    }

    private List<LyricLine> loadLyrics(String file) {
        List<LyricLine> lyrics = new ArrayList<>();
        if (file == null || file.isEmpty()) {
            return lyrics;
        }
        File lyricsFile = new File(file);
        if (!lyricsFile.exists()) {
            System.err.println("Lyrics file does not exist: " + file);
            return null;
        }
        try (BufferedReader reader = new BufferedReader(new FileReader(lyricsFile))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split("]");
                if (parts.length == 2) {
                    String timestamp = parts[0].substring(1);
                    String lyric = parts[1].trim();
                    lyrics.add(new LyricLine(parseTimestamp(timestamp), lyric));
                }
            }
        } catch (IOException e) {
            System.err.println("Error loading lyrics file: " + e.getMessage());
            lyrics = null;
        }
        return lyrics;
    }

    private void displayLyrics() {
        if (lyrics == null) {
            System.out.println("Lyrics not available.");
            return;
        }
        lyricsThread = new Thread(() -> {
            LyricLine currentLyric = null;
            currentLyricIndex = 0;
            while (running && (clip.isRunning() || isPaused)) {
                while (currentLyricIndex < lyrics.size() && lyrics.get(currentLyricIndex).getTimestamp() <= clip.getMicrosecondPosition() / 1000) {
                    currentLyric = lyrics.get(currentLyricIndex);
                    currentLyricIndex++;
                }
                clearConsole();
                if (currentLyric != null) {
                    printLyricLine(currentLyric);
                } else {
                    printNoLyricLine();
                }
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });
        lyricsThread.start();
    }

    public void stopLyricsThread() {
        if (lyricsThread != null) {
            lyricsThread.interrupt();
        }
    }

    private void printLyricLine(LyricLine lyricLine) {
        // Calculate time and progress
        long timestamp = clip.getMicrosecondPosition() / 1000;
        long minutes = timestamp / (60 * 1000);
        long remainingMillis = timestamp % (60 * 1000);
        long seconds = remainingMillis / 1000;
        double progress = (double) clip.getMicrosecondPosition() / clip.getMicrosecondLength();

        // Generate progress bar
        int progressBarWidth = 105;
        int progressPosition = (int) (progressBarWidth * progress);
        StringBuilder progressBar = new StringBuilder("[");
        for (int i = 0; i < progressBarWidth; i++) {
            if (i < progressPosition) progressBar.append("=");
            else if (i == progressPosition) progressBar.append(">");
            else progressBar.append(" ");
        }
        progressBar.append("]");

        // Format time strings
        String timeString = String.format("%02d:%02d", minutes, seconds);
        String totalTime = String.format("%02d:%02d", clip.getMicrosecondLength() / 60000000, (clip.getMicrosecondLength() / 1000000) % 60);

        // Determine highlight
        String highlightStart = (counter % 2 == 0) ? "\033[0;1m" : ""; // ANSI code for bold
        String highlightEnd = (counter % 2 == 0) ? "\033[0m" : ""; // ANSI code to reset

        String repeatOption = (NowPlaying.wasInRepeatB4) ? highlightStart + "[4] Repeat" + highlightEnd : "[4] Repeat";

        // Print information
        System.out.println("\033[1;34m-----------------------------------------------------------------------------------------------------------\033[0m");
        System.out.println("\033[1;32mNow Playing:\033[0m ");
        System.out.println("\033[1;33m" + title + "\033[0m");
        System.out.println("\033[1;33m" + artist + "\033[0m");
        System.out.println("\033[1;33m" + album + "\033[0m");
        System.out.printf("\033[1;32m%s\033[0m / %s\n\n", highlightStart + timeString + highlightEnd, totalTime);
        if (lyricLine != null) {
            System.out.println("\033[1;35m" + lyricLine.getLyric() + "\n\033[0m");
        } else {
            System.out.println("");
        }
        System.out.println(progressBar.toString());
        System.out.println("\033[1;34m-----------------------------------------------------------------------------------------------------------\033[0m");
        System.out.println("\033[1;36m[1] Play | [2] Pause | [3] Stop | " + repeatOption + "\033[1;36m | [5] Jump | [6] Library | [7] Main Menu | [8] View Album Art \033[0m");
        System.out.println("\033[1;34m-----------------------------------------------------------------------------------------------------------\033[0m");

        counter++;
    }

    private void printNoLyricLine() {
        clearConsole();

        String highlightStart = (counter % 2 == 0) ? "\033[0;1m" : "";
        String highlightEnd = (counter % 2 == 0) ? "\033[0m" : "";

        long timestamp = clip.getMicrosecondPosition() / 1000;
        long minutes = timestamp / (60 * 1000);
        long remainingMillis = timestamp % (60 * 1000);
        long seconds = remainingMillis / 1000;

        double progress = (double) clip.getMicrosecondPosition() / clip.getMicrosecondLength();
        int progressBarWidth = 105;
        int progressPosition = (int) (progressBarWidth * progress);
        StringBuilder progressBar = new StringBuilder("[");
        for (int i = 0; i < progressBarWidth; i++) {
            if (i < progressPosition) progressBar.append("=");
            else if (i == progressPosition) progressBar.append(">");
            else progressBar.append(" ");
        }
        progressBar.append("]");

        String timeString = String.format("%02d:%02d", minutes, seconds);
        String totalTime = String.format("%02d:%02d", clip.getMicrosecondLength() / 60000000, (clip.getMicrosecondLength() / 1000000) % 60);

        String repeatOption = (NowPlaying.wasInRepeatB4) ? highlightStart + "[4] Repeat" + highlightEnd : "[4] Repeat";

        System.out.println("\033[1;34m-----------------------------------------------------------------------------------------------------------\033[0m");
        System.out.println("\033[1;32mNow Playing:\033[0m ");
        System.out.println("\033[1;33m" + title + "\033[0m");
        System.out.println("\033[1;33m" + artist + "\033[0m");
        System.out.println("\033[1;33m" + album + "\033[0m");
        System.out.printf("\033[1;32m%s\033[0m / %s\n\n", highlightStart + timeString + highlightEnd, totalTime);

        if (lyrics == null || lyrics.isEmpty()) {
            System.out.println("\n");
        }

        System.out.println(progressBar.toString());
        System.out.println("\033[1;34m-----------------------------------------------------------------------------------------------------------\033[0m");
        System.out.println("\033[1;36m[1] Play | [2] Pause | [3] Stop | " + repeatOption + "\033[1;36m | [5] Jump | [6] Library | [7] Main Menu | [8] View Album Art \033[0m");
        System.out.println("\033[1;34m-----------------------------------------------------------------------------------------------------------\033[0m");

        counter++;
    }

    private long parseTimestamp(String timestamp) {
        String[] parts = timestamp.split(":");
        long minutes = Long.parseLong(parts[0]);
        String[] secondParts = parts[1].split("\\.");
        long seconds = Long.parseLong(secondParts[0]);
        long millis = Long.parseLong(secondParts[1]);
        return (minutes * 60 * 1000) + (seconds * 1000) + millis;
    }

    public void run(String filePath, String lyricsFile, String title, String artist, String album) {
        try {
            Scanner scan = new Scanner(System.in);
            if (lyrics == null || lyrics.isEmpty()) {
                printNoLyricLine();
            } else {
                displayLyrics();
            }

            while (running) {
                if (isPaused) {
                    Thread.sleep(1000);
                } else if (lyrics == null || lyrics.isEmpty()) {
                    printNoLyricLine();
                }

                Thread.sleep(1000);
            }
        } catch (InterruptedException e) {
            System.out.println("ElapsedTimeTracker thread was interrupted");
        }
    }

    public void runNoLyrics(String filePath, String title, String artist, String album) { // Without this, your cpu will die!
        try {
            printNoLyricLine();
            while (running && (clip.isRunning() || isPaused)) {
                if (!isPaused) {
                    if (!clip.isRunning()) {
                        // Song has ended
                        System.out.println("Song has ended.");
                        break;
                    } else if (lyrics == null || lyrics.isEmpty()) {
                        printNoLyricLine();
                    } else {
                    }
                }
                Thread.sleep(1000);
            }
        } catch (InterruptedException e) {
            System.out.println("ElapsedTimeTracker thread was interrupted");
        }
    }

    public void run(String filePath){
    }
}