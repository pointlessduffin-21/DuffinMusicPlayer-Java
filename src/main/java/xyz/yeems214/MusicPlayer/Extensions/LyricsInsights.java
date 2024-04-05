package xyz.yeems214.MusicPlayer.Extensions;

import xyz.yeems214.MusicPlayer.Interfaces.FileManager;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Properties;

// This source code passes through Lyrics Text from LyricsView through Process Builder to Python CLI for insights
// And sends the insights to Google's Gemini 1.0 Pro API for analysis then pulls back the insights to Java

public class LyricsInsights {
    public static String Directory;
    static {
        try {
            Properties properties = new Properties();
            properties.load(FileManager.class.getClassLoader().getResourceAsStream("application.properties"));
            Directory = properties.getProperty("extensions.directory");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public static void getLyricInsights(String lyricsFile) throws IOException, InterruptedException {
        File dir = new File(Directory);
        String diana = dir + "/diana.py";
        ProcessBuilder builder = new ProcessBuilder("python3", diana, lyricsFile);
        Process process = builder.start();

        BufferedReader stdOutReader = new BufferedReader(new InputStreamReader(process.getInputStream()));
        BufferedReader stdErrReader = new BufferedReader(new InputStreamReader(process.getErrorStream()));

        String line;
        StringBuilder insights = new StringBuilder();

        while ((line = stdOutReader.readLine()) != null) {
            insights.append(line).append("\n");
        }

        while ((line = stdErrReader.readLine()) != null) {
            insights.append(line).append("\n");
        }

        process.waitFor();

        System.out.println(insights.toString());
    }
}