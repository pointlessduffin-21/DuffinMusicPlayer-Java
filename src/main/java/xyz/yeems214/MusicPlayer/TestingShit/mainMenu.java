package xyz.yeems214.MusicPlayer.TestingShit;

import xyz.yeems214.MusicPlayer.Interfaces.LyricsView;
import xyz.yeems214.MusicPlayer.Interfaces.playByLink;
import xyz.yeems214.MusicPlayer.Main;

import java.util.Scanner;

import static xyz.yeems214.MusicPlayer.Main.*;
import static xyz.yeems214.MusicPlayer.Main.lyricsFile;

public class mainMenu {
    public static void extraFeatures()  throws Exception {
        Scanner scan = new Scanner(System.in);
        clearConsole();
        scan.reset();
        System.out.println("Extra Features!");
        System.out.println("Some of these features may or may not work! Please proceed with caution!");
        while (true) {
            System.out.println("1. Audio Bar Visualizer");
            System.out.println("2. MP3 Player");
            System.out.println("3. Original Synced Lyrics");
            System.out.println("4. Queueing System");
            System.out.println("5. Back to Main Menu");
            System.out.print("Enter your choice: ");
            int choice = scan.nextInt();
            scan.nextLine();

            switch (choice) {
                case 1:
                    clearConsole();
                    System.out.println("Audio Bar Visualizer");
                    System.out.println("Enter the file path of the song you want to visualize (.WAV): (Press q to quit)");
                    String filePath = scan.nextLine();
                    AudioBarVisualizer.deez(filePath);
                    break;
                case 2:
                    clearConsole();
                    System.out.println("MP3 Player");
                    System.out.println("Enter the file path of the song you want to play (.MP3): (Press q to quit)");
                    String filePath2 = scan.nextLine();
                    if (filePath2.equals("q")) {
                        break;
                    } else {
                        System.out.println("Do you want to include lyrics here? (yes/no)");
                        String conditional = scan.nextLine().toUpperCase();
                        if (conditional == "yes") {
                            System.out.println("Enter the file path of the lyrics you want to display (.MP3): ");
                            String lyricsFile = scan.nextLine();
//                            MP3Player.playMP3(filePath2, lyricsFile, null, null, null);
//                        } else {
//                            MP3Player.playMP3(filePath2, null, null, null, null);
                        }
                    }
                    break;
                case 3:
                    System.out.println("Original Synced Lyrics");
                    System.out.println("Enter the file path of the song you want to play (.WAV): (Press q to quit)");
                    String filePath3 = scan.nextLine();
                    if (filePath3.equals("q")) {
                        break;
                    } else {
                        System.out.println("Do you want to include lyrics here? (yes/no)");
                        String conditional = scan.nextLine().toUpperCase();
                        if (conditional == "yes") {
                            System.out.println("Enter the file path of the lyrics you want to display (.MP3): ");
                            String lyricsFile = scan.nextLine();
                            SyncedLyrics.deez(filePath3, lyricsFile, null, null, null);
                        } else {
                            SyncedLyrics.deez(filePath3, null, null, null, null);
                        }
                    }
                case 4:
                    System.out.println("Queueing System");
                    System.out.println("Enter the file path of your Playlist file (.csv) (Press q to quit)");
                    System.out.println("Format for CSV: id,filePath,title,artist,album,genre,year,composers,trackNumber,discNumber,lyricsFile");
                    String filePath4 = scan.nextLine();
                    if (filePath4.equals("q")) {
                        break;
                    } else {
//                        QueueingSystem.deez(filePath4);
                    }
                    break;
                case 5:
                    Main.mainMenu();
                    break;
                case 0:
                    clearConsole();
                    refreshConsole();
                    break;
                default:
                    System.out.println("Invalid choice.");
                    break;
            }
            songEnded = false; // Set the flag to false when leaving "Now Playing"
        }
    }
    public static void main(String[] args) throws Exception {
        clearConsole();
        extraFeatures();
    }
}
