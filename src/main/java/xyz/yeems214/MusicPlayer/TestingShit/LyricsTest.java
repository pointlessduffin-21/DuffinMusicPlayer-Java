package xyz.yeems214.MusicPlayer.TestingShit;

import xyz.yeems214.MusicPlayer.Extensions.ElapsedTimeTracker;
import xyz.yeems214.MusicPlayer.Interfaces.NowPlaying;

import javax.sound.sampled.*;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import static org.apache.commons.io.FileUtils.readFileToByteArray;

public class LyricsTest {

    public static void main(String[] args) throws Exception {
        String filePath = "src/main/resources/music-files/California.wav"; // Replace with your music file path
        byte[] wavData = getAudioInputStream(filePath).readAllBytes();

        System.out.println("Playing California.wav...");

        // Load lyrics from file
        List<String> lyrics = readLyricsFromFile("src/main/resources/lyric-files/88rising-california.txt");

        // Play the WAV file and display lyrics
        playWavAndShowLyrics(wavData, lyrics);
    }

    private static AudioInputStream getAudioInputStream(String filePath) throws IOException, UnsupportedAudioFileException {
        File file = new File(filePath);
        try (FileInputStream fis = new FileInputStream(file)) {
            return AudioSystem.getAudioInputStream(fis);
        }
    }

    private static List<String> readLyricsFromFile(String filePath) throws IOException {
        List<String> lyrics = new ArrayList<>();
        try (Scanner scanner = new Scanner(new File(filePath))) {
            while (scanner.hasNextLine()) {
                lyrics.add(scanner.nextLine());
            }
        }
        return lyrics;
    }

    private static void playWavAndShowLyrics(byte[] wavData, List<String> lyrics) throws IOException, LineUnavailableException, UnsupportedAudioFileException {
        AudioInputStream audioStream = AudioSystem.getAudioInputStream(new ByteArrayInputStream(wavData));
        AudioFormat format = audioStream.getFormat();
        DataLine.Info info = new DataLine.Info(SourceDataLine.class, format);
        SourceDataLine line = (SourceDataLine) AudioSystem.getLine(info);
        line.open(format);
        line.start();

        // Create LyricsView thread with playback logic
        LyricsView lyricsView = new LyricsView(lyrics, (SourceDataLine) line);
        new Thread(lyricsView).start();

        byte[] buffer = new byte[4096];
        int bytesRead;
        while ((bytesRead = audioStream.read(buffer)) != -1) {
            line.write(buffer, 0, bytesRead);
        }

        line.drain();
        line.close();
        audioStream.close();
    }
}

class LyricsView extends Thread {

    private List<String> lyrics;
    private SourceDataLine line;

    public LyricsView(List<String> lyrics, SourceDataLine line) {
        this.lyrics = lyrics;
        this.line = line;
    }

    @Override
    public void run() {
        long startTime = System.currentTimeMillis();
        while (line.isActive()) {
            long currentTime = System.currentTimeMillis() - startTime;
            for (String lyricLine : lyrics) {
                long lyricTime = getTimestampFromLine(lyricLine);
                if (currentTime >= lyricTime) {
                    System.out.println(lyricLine);
                    break; // Only print the first matching lyric for efficiency
                }
            }
            try {
                Thread.sleep(50); // Adjust sleep time based on your needs
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        System.out.println("Song finished.");
    }

    private long getTimestampFromLine(String line) {
        // Extract timestamp from the beginning of the line (assuming format [00:02.62])
        String timestampString = line.substring(line.indexOf("[") + 1, line.indexOf("]"));
        // Convert timestamp string to milliseconds (parse minutes, seconds, milliseconds)
        long minutes = Long.parseLong(timestampString.substring(0, timestampString.indexOf(":")));
        long seconds = Long.parseLong(timestampString.substring(timestampString.indexOf(":") + 1, timestampString.lastIndexOf(":")));
        long milliseconds = Long.parseLong(timestampString.substring(timestampString.lastIndexOf(":") + 1));
        return (minutes * 60 * 1000) + (seconds * 1000) + milliseconds;
    }
}
