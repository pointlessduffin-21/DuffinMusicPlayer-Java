package xyz.yeems214.MusicPlayer.Extensions;

import xyz.yeems214.MusicPlayer.Interfaces.FileManager;
import xyz.yeems214.MusicPlayer.Main;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Paths;
import java.util.Properties;
import java.util.Scanner;

import static xyz.yeems214.MusicPlayer.Main.clearConsole;

// This code is similar to DianaChat and LyricsInsights but passes through ffmpeg commands to convert audio files to different formats
// This is for UNIX systems only (i.e. macOS and Linux)
// This requires the user to have ffmpeg already installed and set on its environment variables

public class AudioConverterUnix {
    private static boolean hasConverted = false;
    public static String musicAbsoluteDirectory;
    static {
        try {
            Properties properties = new Properties();
            properties.load(FileManager.class.getClassLoader().getResourceAsStream("application.properties"));
            String musicDirectoryProperty = properties.getProperty("music.directory");
            musicAbsoluteDirectory = Paths.get(System.getProperty("user.dir"), musicDirectoryProperty).toString();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public static void main(String[] args) {
        System.out.println("Used for testing");
        String inputFile = "/home/yeems214/Downloads/Green Day - American Idiot (1998) [FLAC] [24B-96kHz]/03. Green Day - Holiday (Explicit).flac";
        String outputFile = "src/main/resources/music-files/Holiday converted.flac";
        convertFlac(inputFile, outputFile);
    }

    public static void flacConverter(String inputFile) throws Exception {
        Scanner scan = new Scanner(System.in);
        System.out.println("Do you want to save this to the library or externally?");
        System.out.println("1. Library" + "\n2. External \nor Any key to go back to main menu");
        String choice = scan.nextLine().toLowerCase();
        switch (choice) {
            case "1":
                String inputFileName = Paths.get(inputFile).getFileName().toString();
                String baseName = inputFileName.substring(0, inputFileName.lastIndexOf('.'));
                String outputFile = Paths.get(musicAbsoluteDirectory, baseName + " converted.flac").toString();
                convertFlac(inputFile, outputFile);
                break;
            case "2":
                System.out.println("Enter the file path of the song you want to convert: ");
                String outputFile2 = scan.nextLine();
                convertFlac(inputFile, outputFile2);
                break;
            default:
                AudioConverterWindows.deez();
        }
    }

    public static void wavConverter(String inputFile) throws Exception {
        Scanner scan3 = new Scanner(System.in);
        System.out.println("Do you want to save this to the library or externally?");
        System.out.println("1. Library" + "\n2. External \nor Any key to go back to main menu");
        String choice2 = scan3.nextLine().toLowerCase();
        switch (choice2) {
            case "1":
                String inputFileName = Paths.get(inputFile).getFileName().toString();
                String baseName2 = inputFileName.substring(0, inputFileName.lastIndexOf('.'));
                String outputFile3 = Paths.get(musicAbsoluteDirectory, baseName2 +" converted.wav").toString();
                convertWav(inputFile, outputFile3);
                break;
            case "2":
                System.out.println("Enter the file path of the song you want to convert: ");
                String outputFile2 = scan3.nextLine();
                convertWav(inputFile, outputFile2);
                break;
            default:
                deez();
                break;
        }

    }

    public static void deez() throws Exception {
        if (!hasConverted) {
            System.out.println("FFmpeg Audio Converter (macOS / Linux)");
            Scanner scan = new Scanner(System.in);

            System.out.println("What format do you want to convert?: ");
            System.out.println("1. FLAC" + "\n2. WAV" + "\n3. Go back to main menu");
            String format = scan.nextLine().toLowerCase();
            switch (format) {
                case "1":
                    clearConsole();
                    System.out.println("Enter the file path of the song to convert: ");
                    String inputFile = scan.nextLine();
                    flacConverter(inputFile);
                    break;
                case "2":
                    clearConsole();
                    System.out.println("Enter the file path of the song to convert: ");
                    String inputFile2 = scan.nextLine();
                    wavConverter(inputFile2);
                    break;
                default:
                    Main.mainMenu();
                    break;
            }
            System.out.println("Enter the file path of the song to convert: ");
        } else {
            System.out.println("You can only convert one file at a time per session.");
            System.out.println("Press any key to go back to the main menu.");
            Scanner scan = new Scanner(System.in);
            String conditional = scan.nextLine().toUpperCase();
            switch (conditional) {
                case "YES":
                    deez();
                    break;
                default:
                    Main.mainMenu();
                    break;
            }
        }

    }
    public static void convertFlac(String inputFile, String outputFile) {
        String[] command = {
                "ffmpeg",
                "-i", inputFile,
                "-c:a", "flac",
                "-ar", "44100",
                "-sample_fmt", "s16",
                outputFile
        };
        ProcessBuilder processBuilder = new ProcessBuilder(command);
        processBuilder.redirectErrorStream(true);

        try {
            Process process = processBuilder.start();
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    System.out.println(line);
                }
            }
            hasConverted = true;
            FileManager.updateDatabase();
            System.out.println("Encoding successful! Press any key to go back to the main menu.");
            Scanner scan = new Scanner(System.in);
            String conditional = scan.nextLine().toUpperCase();
            switch (conditional) {
                default:
                    Main.mainMenu();
                    break;
            }
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static void convertWav(String inputFile, String outputFile) {
        String[] command = {
                "ffmpeg",
                "-i", inputFile,
                "-c:a", "pcm_s16le",
                outputFile
        };

        System.out.println("Executing ffmpeg: " + String.join(" ", command));

        ProcessBuilder processBuilder = new ProcessBuilder(command);
        processBuilder.redirectErrorStream(true);

        try {
            Process process = processBuilder.start();
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    System.out.println(line);
                }
            }

            hasConverted = true;
            FileManager.updateDatabase();
            System.out.println("Encoding successful! Press any key to go back to the main menu.");
            Scanner scan = new Scanner(System.in);
            String conditional = scan.nextLine().toUpperCase();
            switch (conditional) {
                default:
                    Main.mainMenu();
                    break;
            }
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}