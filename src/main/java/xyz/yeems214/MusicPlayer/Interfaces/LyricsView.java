package xyz.yeems214.MusicPlayer.Interfaces;

import xyz.yeems214.MusicPlayer.Extensions.LyricsInsights;
import xyz.yeems214.MusicPlayer.Main;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.*;

// This class prints all the lyrics available in the Lyrics Directory which allows to view lyrics inside the files.

public class LyricsView extends Main {
    public static String Directory;

    static {
        try {
            Properties properties = new Properties();
            properties.load(FileManager.class.getClassLoader().getResourceAsStream("application.properties"));
            Directory = properties.getProperty("lyrics.directory");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) throws Exception {
        LyricsView lyricsView = new LyricsView();
        lyricsView.lyricsPicker();
    }

    public static void lyricsPicker() throws Exception {
        clearConsole();
        Scanner input = new Scanner(System.in);
        File dir = new File(Directory);
        String[] files = dir.list();
        if (files == null) {
            System.out.println("The system cannot find the path specified! \n");
            return;
        }
        System.out.println("\033[1;34m            Lyrics Picker\033[0m");
        Map<Integer, String> lyricsMap = new HashMap<>();
        int count = 1;
        for (String file : files) {
            if (file.endsWith(".txt")) {
                String lyricsName = file.replace(".txt", "");
                lyricsMap.put(count, lyricsName);
                System.out.printf("\033[1;32m[%d] - %s\033[0m\n", count, lyricsName);
                count++;
            }
        }
        System.out.println("\nEnter the number of the lyrics you want to view: \nPress o to Open Lyrics by Link or q to quit");
        String choiceStr = input.nextLine();
        if (choiceStr.equalsIgnoreCase("q")) {
            Main.mainMenu();
        } else if (choiceStr.equalsIgnoreCase("o")) {
            Scanner scan = new Scanner(System.in);
            System.out.println("Enter the file path of the lyrics you want to display (.txt): ");
            String lyricsFile = scan.nextLine();
            fileReader(lyricsFile);
        }
        else {
            try {
                int choice = Integer.parseInt(choiceStr);
                if (lyricsMap.containsKey(choice)) {
                    String lyricsName = lyricsMap.get(choice);
                    String filePath = Directory + lyricsName + ".txt";
                    clearConsole();
                    fileReader(filePath);
                } else {
                    System.out.println("Invalid choice! Please try again.");
                    lyricsPicker();
                }
            } catch (NumberFormatException e) {
                System.out.println("Invalid choice! Please enter a number.");
                lyricsPicker();
            }
        }
    }

    public static void displayLyricInsights(String lyricsFile) {
        try {
            clearConsole();
            Scanner scan = new Scanner(System.in);
            LyricsInsights.getLyricInsights(lyricsFile);
            System.out.println("Do you want to view another lyrics? (yes/no)");
            String conditional = scan.nextLine().toUpperCase();
            switch (conditional) {
                case "YES":
                    LyricsView.lyricsPicker();
                    break;
                default:
                    Main.mainMenu();
                    break;
            }
        } catch (IOException | InterruptedException e) {
            System.out.println("Error getting lyric insights: " + e.getMessage());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    // Basic Text Reader
    public static void fileReader(String lyricsFile) throws Exception {
        clearConsole();
        System.out.println("\u001B[1mReading:\u001B[0m " + lyricsFile + "\n");
        Scanner scan = new Scanner(System.in);
        List<String> lines = readFile(lyricsFile);
        for (String line : lines) {
            String cleanedLine = line.replaceAll("\\[\\d{2}:\\d{2}\\.\\d{2}\\] ", "");
            System.out.println(cleanedLine);
        }
        System.out.println("\n\u001B[32mOptions:\u001B[0m");
        System.out.println("  [1] - View another Lyrics");
        System.out.println("  [2] = Get Insight");
        System.out.println("  [3] = Go back to Main Menu");

        int response = scan.nextInt();
        switch (response) {
            case 1:
                lyricsPicker();
                break;
            case 2:
                try {
                    clearConsole();
                    System.out.println("Sending to Diana for insights... Please wait!");
                    displayLyricInsights(lyricsFile);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;
            case 3:
                Main.mainMenu();
                break;
            case 0:
                clearConsole();
                refreshConsole();
                break;
            default:
                System.out.println("Invalid response. Please try again.");
                fileReader(lyricsFile);
        }
    }

    public static List<String> readFile(String lyricsFile) {
        List<String> lines = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new java.io.FileReader(lyricsFile))) {
            String line;
            while ((line = reader.readLine()) != null) {
                lines.add(line);
            }
        } catch (FileNotFoundException e) {
            System.out.println("The system cannot find the path specified! Please edit this using the editor! \n");
        } catch (IOException e) {
            e.printStackTrace();
        }
        return lines;
    }
}