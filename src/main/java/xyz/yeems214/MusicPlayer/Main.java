package xyz.yeems214.MusicPlayer;

import xyz.yeems214.MusicPlayer.Extensions.AudioConverterUnix;
import xyz.yeems214.MusicPlayer.Extensions.AudioConverterWindows;
import xyz.yeems214.MusicPlayer.Extensions.DianaChat;
import xyz.yeems214.MusicPlayer.Interfaces.FileManager;
import xyz.yeems214.MusicPlayer.Interfaces.LyricsView;
import xyz.yeems214.MusicPlayer.Interfaces.playByLink;

import java.io.File;
import java.util.*;

import java.util.Scanner;

import static org.fusesource.jansi.AnsiConsole.getTerminalWidth;
import static xyz.yeems214.MusicPlayer.Interfaces.FileManager.FileMenu;
import static xyz.yeems214.MusicPlayer.Interfaces.playByLink.songOptions;

// This is the main class of the project.
// This class is responsible for the main menu of the application and the initialization of the database.
public class Main {
    public static File file;
    public static String filePath;
    public static String lyricsFile;
    public static String title;
    public static String artist;
    public static String album;
    public static boolean songEnded = false;
    public static void clearConsole() {
        try {
            if (System.getProperty("os.name").contains("Windows")) {
                new ProcessBuilder("cmd", "/c", "cls").inheritIO().start().waitFor();
            } else {
                new ProcessBuilder("bash", "-c", "clear").inheritIO().start().waitFor();
            }
        } catch (Exception e) {
            System.out.println("Error clearing console: " + e.getMessage());
        }
    }

    public static void refreshConsole() {
        System.out.print("\033[H\033[2J");
    }

    public static void mainMenu() throws Exception {
        FileManager.initializeDatabase();
        try {
            String os = System.getProperty("os.name").toLowerCase();
            clearConsole();
            Scanner scan = new Scanner(System.in);
            scan.reset();
            while (true) {
                System.out.print("     _____         __  __ _       _       __  __           _        _____  _                       \n");
                System.out.print("    |  __ \\       / _|/ _(_)     ( )     |  \\/  |         (_)      |  __ \\| |                      \n");
                System.out.print("    | |  | |_   _| |_| |_ _ _ __ |/ ___  | \\  / |_   _ ___ _  ___  | |__) | | __ _ _   _  ___ _ __ \n");
                System.out.print("    | |  | | | | |  _|  _| | '_ \\  / __| | |\\/| | | | / __| |/ __| |  ___/| |/ _` | | | |/ _ \\ '__|\n");
                System.out.print("    | |__| | |_| | | | | | | | | | \\__ \\ | |  | | |_| \\__ \\ | (__  | |    | | (_| | |_| |  __/ |   \n");
                System.out.print("    |_____/ \\__,_|_| |_| |_|_| |_| |___/ |_|  |_|\\__,_|___/_|\\___| |_|    |_|\\__,_|\\__, |\\___|_|   \n");
                System.out.print("                                                                                    __/ |          \n");
                System.out.print("                                                                                   |___/           \n");

                String[] options = {
                        "[1] - Library View",
                        "[2] - Play via Link",
                        "[3] - Check Lyrics",
                        "[4] - Chat with Diana",
                        "[5] - FFmpeg Converter",
                        "[6] - Exit",
                        "[0] - Refresh Console",
                };
                for (String option : options) {
                    int padding = (getTerminalWidth() - option.length()) / 2;
                    System.out.printf("%" + padding + "s\033[1m%s\033[0m\n", "", option); // Bold text
                }
                int choice = scan.nextInt();
                switch (choice) {
                    case 1:
                        clearConsole();
                        FileMenu();
                        break;
                    case 2:
                        playByLink.address();
                        break;
                    case 3:
                        LyricsView.lyricsPicker();
                        break;
                    case 4:
                        DianaChat.deez();
                        break;
                    case 5:
                        clearConsole();
                        if (os.contains("win")) {
                            AudioConverterWindows.deez();
                        } else if (os.contains("mac") || os.contains("nix") || os.contains("nux") || os.contains("aix")) {
                            AudioConverterUnix.deez();
                        } else {
                            System.out.println("OS unknown! Please send an email to f.abarca@yeems214.xyz for support.");
                            mainMenu();
                        }
                    case 6:
                        clearConsole();
                        FileManager.shutDownDatabase();
                        System.out.println("Goodbye!");
                        System.exit(0);
                        break;
                    case 0:
                        clearConsole();
                        refreshConsole();
                        break;
                    default:
                        System.out.println("Invalid choice.");
                        break;
                }
                songEnded = false;
            }
        } catch (InputMismatchException e) {
            System.out.println("Invalid input. Please enter a number.");
            mainMenu();
        } catch (Exception e) {
            System.out.println("An error occurred: " + e.getMessage());
            System.out.println("App is restarting!");
            mainMenu();
        }
    }

    public static void main(String[] args) throws Exception {
        if (args.length == 1) { // This reads the file path from the command line then passes it through songOptions for playback.
            String filePath = args[0];
            songOptions(filePath);
        } else {
            mainMenu();
        }
    }
}