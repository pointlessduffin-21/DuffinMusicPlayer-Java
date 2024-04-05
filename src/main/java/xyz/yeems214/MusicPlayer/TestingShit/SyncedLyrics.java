package xyz.yeems214.MusicPlayer.TestingShit;

import xyz.yeems214.MusicPlayer.Main;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;

import static xyz.yeems214.MusicPlayer.Main.clearConsole;

public class SyncedLyrics {
    private boolean isPlaying;
    private Clip clip;
    private AudioInputStream audioStream;
    private long startTime;
    private List<LyricLine> lyrics;
    private Scanner scanner;
    private String title;
    private String artist;
    private String album;

    public SyncedLyrics(String filePath, String lyricsFile) {
        try {
            audioStream = AudioSystem.getAudioInputStream(new File(filePath));
            clip = AudioSystem.getClip();
            clip.open(audioStream);
            lyrics = loadLyrics(lyricsFile);
            scanner = new Scanner(System.in);
        } catch (UnsupportedAudioFileException | IOException | LineUnavailableException e) {
            e.printStackTrace();
        }
        isPlaying = false;
    }

    private List<LyricLine> loadLyrics(String file) {
        List<LyricLine> lyrics = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
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
            e.printStackTrace();
        }
        return lyrics;
    }

    private long parseTimestamp(String timestamp) {
        String[] parts = timestamp.split(":");
        long minutes = Long.parseLong(parts[0]);
        String[] secondParts = parts[1].split("\\.");
        long seconds = Long.parseLong(secondParts[0]);
        long millis = Long.parseLong(secondParts[1]);
        return (minutes * 60 * 1000) + (seconds * 1000) + millis;
    }

    public void play(String title, String artist, String album) {
        this.title = title;
        this.artist = artist;
        this.album = album;
        startTime = System.currentTimeMillis();
        clip.start();
        displayLyrics();
        isPlaying = true;
        new Thread(() -> {
            while (scanner.hasNextLine()) {
                String command = scanner.nextLine();
                try {
                    handleCommand(command);
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }
        }).start();
    }

    public void printSongStatus(String title, String artist, String album) {
        long clipTimePosition = 0;
        int currentDuration = (int) clip.getMicrosecondPosition() / 1000000;
        int totalDuration = (int) clip.getMicrosecondLength() / 1000000;
        int currentMinutes = currentDuration / 60;
        int currentSeconds = currentDuration % 60;
        int totalMinutes = totalDuration / 60;
        int totalSeconds = totalDuration % 60;

        System.out.println("Now Playing: " + title + " by " + artist + " from the album " + album);
        System.out.print("\033[1;1H");
        System.out.println(String.format("%02d:%02d", currentMinutes, currentSeconds) + " / " + String.format("%02d:%02d", totalMinutes, totalSeconds));
        System.out.print("\033[1;1B");
        System.out.println("Type 'j' to jump to a specific time, 'r' to repeat the song, 'w' to pause, or 's' to stop.");
    }

    private void displayLyrics() {
        new Thread(() -> {
            LyricLine lastLyric = null;
            int currentLyricIndex = 0;
            while (currentLyricIndex < lyrics.size() && isPlaying) {
                LyricLine currentLyric = lyrics.get(currentLyricIndex);
                long timeDiff = clip.getMicrosecondPosition() / 1000;
                if (timeDiff >= currentLyric.getTimestamp()) {
                    clearConsole();
                    printLyricLine(currentLyric);
                    lastLyric = currentLyric;
                    currentLyricIndex++;
                } else if (lastLyric != null) {
                    printLyricLine(lastLyric);
                }
                try {
                    Thread.sleep(1000); // Change this to 1000 milliseconds
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    private void displayLyrics2() {
        new Thread(() -> {
            LyricLine lastLyric = null;
            int currentLyricIndex = 0;
            while (currentLyricIndex < lyrics.size()) {
                LyricLine currentLyric = lyrics.get(currentLyricIndex);
                long timeDiff = clip.getMicrosecondPosition() / 1000;
                if (timeDiff >= currentLyric.getTimestamp()) {
                    lastLyric = currentLyric;
                    currentLyricIndex++;
                }
                clearConsole();
                printLyricLine(lastLyric != null ? lastLyric : currentLyric);
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    private void printLyricLine(LyricLine lyricLine) {
        long timestamp = clip.getMicrosecondPosition() / 1000;
        long minutes = timestamp / (60 * 1000);
        long remainingMillis = timestamp % (60 * 1000);
        long seconds = remainingMillis / 1000;
        String timeString = String.format("%02d:%02d", minutes, seconds);
        String commands = "Type 'j' to jump to a specific time, 'r' to repeat the song, 'w' to pause, or 's' to stop.";
        String totalTime = String.format("%02d:%02d", clip.getMicrosecondLength() / 60000000, (clip.getMicrosecondLength() / 1000000) % 60);


        System.out.println(title + "\n" + artist + "\n" + album);
        System.out.printf("%s / %s\n\n", timeString, totalTime);
        System.out.printf("%s\n\n", lyricLine.getLyric());
//        System.out.printf("%s %s\n\n", timeString, lyricLine.getLyric());
        System.out.println(commands);
    }



    public static void deez(String filePath, String lyricsFile, String title, String artist, String album) {
        SyncedLyrics player = new SyncedLyrics(filePath, lyricsFile);;
        player.play(title, artist, album);
    }

    public static void main(String[] args) {
        String filePath = "src/main/resources/music-files/California.wav";
        String lyricsFile = "src/main/resources/lyrics-files/88rising-california.txt";
        deez(filePath, lyricsFile, "California", "88rising", "Head in the Clouds");
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

    private void handleCommand(String command) throws Exception {
        switch (command.toLowerCase()) {
            case "j":
                clip.stop();
                isPlaying = false;
                System.out.println("Enter time (MM:SS) to jump to part: ");
                String timeInput = scanner.next();
                String[] timeParts = timeInput.split(":");
                if (timeParts.length != 2) {
                    System.out.println("Invalid format. Please enter time in MM:SS format.");
                    break;
                }
                int minutes = Integer.parseInt(timeParts[0]);
                int seconds = Integer.parseInt(timeParts[1]);
                long microseconds = (minutes * 60L + seconds) * 1000000L;
                clip.setMicrosecondPosition(microseconds);
                isPlaying = true;
                displayLyrics();
                break;
            case "r":
                clip.stop();
                isPlaying = false;
                clip.setMicrosecondPosition(0);
                clip.start();
                isPlaying = true;
                displayLyrics();
                break;
            case "w":
                clip.stop();
                isPlaying = false;
                System.out.println("Paused. Type 'p' to resume.");
                String resumeCommand = scanner.nextLine();
                if (resumeCommand.equalsIgnoreCase("p")) {
                    clip.setMicrosecondPosition(clip.getMicrosecondPosition());
                    clip.start();
                    isPlaying = true;
                    displayLyrics();
                }
                break;
            case "p":
                clip.setMicrosecondPosition(clip.getMicrosecondPosition());
                clip.start();
                isPlaying = true;
                displayLyrics();
            case "s":
                clip.stop();
                clip.close();
                System.out.println("Stopped.");
                isPlaying = false;
                Main.mainMenu(); // Add this line
                break;
            default:
                System.out.println("Invalid command. Type 'w' to pause or 's' to stop.");
                break;
        }
    }
}