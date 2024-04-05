//package xyz.yeems214.MusicPlayer.TestingShit;
//
//import java.io.File;
//import java.io.FileInputStream;
//import java.io.BufferedInputStream;
//import java.util.Scanner;
//
//import javazoom.jl.decoder.JavaLayerException;
//import javazoom.jl.player.Player;
//import xyz.yeems214.MusicPlayer.Extensions.ElapsedTimeTracker;
//
//import javax.sound.sampled.AudioInputStream;
//import javax.sound.sampled.AudioSystem;
//import javax.sound.sampled.Clip;
//
//public class MP3Player {
//
//    private static boolean isPlaying = false;
//    private static Player player;
//    private static ElapsedTimeTracker tracker;
//
//    public static void main(String[] args) {
//
//    }
//
//    public static void deez() {
//        Scanner scanner = new Scanner(System.in);
//        while (true) {
//            System.out.println("MP3 Player Menu:");
//            System.out.println("1. Play MP3");
//            System.out.println("2. Exit");
//            System.out.print("Enter your choice: ");
//            int choice = scanner.nextInt();
//            switch (choice) {
//                case 1:
////                    playMP3(filePath, lyricsFile, title, artist, album);
//                    break;
//                case 2:
//                    System.exit(0);
//                default:
//                    System.out.println("Invalid choice. Please try again.");
//            }
//        }
//    }
//
//    public static void playMP3(String filePath, String lyricsFile, String title, String artist, String album) {
//        try {
//            if (isPlaying) {
//                stopMusic();
//            }
//
//            Scanner scanner = new Scanner(System.in);
//
//            AudioInputStream audioInputStream = AudioSystem.getAudioInputStream(new File(filePath));
//            Clip clip = AudioSystem.getClip();
//            clip.open(audioInputStream);
//
//            tracker = new ElapsedTimeTracker(clip, filePath, lyricsFile, title, artist, album);
//            tracker.start(); // Start the tracker thread
//
//            clip.start(); // Start playback
//            isPlaying = true;
//
//            while (isPlaying) {
//                System.out.println("Music player commands:");
//                System.out.println("1. Pause");
//                System.out.println("2. Resume");
//                System.out.println("3. Stop");
//                System.out.println("4. Jump to Song time");
//                System.out.print("Enter your choice: ");
//                int choice = scanner.nextInt();
//                switch (choice) {
//                    case 1:
//                        tracker.pause();
//                        break;
//                    case 2:
//                        tracker.resumeSong();
//                        break;
//                    case 3:
//                        stopMusic();
//                        break;
//                    case 4:
//                        // Implement jump to song time functionality
//                        break;
//                    default:
//                        System.out.println("Invalid choice. Please try again.");
//                }
//            }
//        } catch (Exception e) {
//            System.out.println("Error playing MP3: " + e.getMessage());
//        }
//    }
//
//    private static void stopMusic() {
//        if (isPlaying) {
//            isPlaying = false;
//            player.close();
//            tracker.stopRunning(); // Stop the tracker thread
//        }
//    }
//
//    // Implement this method to extract song metadata using a suitable library
//    private static String getSongMetadata(String filePath, String key) {
//        // ...
//        return null; // Replace with actual metadata value
//    }
//
//    public class oldMP3Player {
//        // Handles MP3 playback raw
//        private static void playMp3(String filePath) throws Exception {
//            if (filePath == null || filePath.isEmpty()) {
//                System.out.println("File path is null or empty.");
//                return;
//            }
//
//            File file = new File(filePath);
//            if (!file.exists()) {
//                System.out.println("File does not exist: " + filePath);
//                return;
//            }
//
//            if (isPlaying) {
//                stopMusic();
//            }
//
//            FileInputStream fis = new FileInputStream(file);
//            BufferedInputStream bis = new BufferedInputStream(fis);
//            Player player = new Player(bis);
//
//            // Create a new thread to play the music
//            Thread playerThread = new Thread(() -> {
//                try {
//                    player.play();
//                } catch (JavaLayerException e) {
//                    e.printStackTrace();
//                }
//            });
//
//            playerThread.start();
//            isPlaying = true;
//
//            // Handle user commands
//            Scanner scan = new Scanner(System.in);
//            while (true) {
//                System.out.println("Music player commands:");
//                System.out.println("1. Play");
//                System.out.println("2. Pause");
//                System.out.println("3. Stop");
//                System.out.println("4. Repeat");
//                System.out.println("5. Jump to Song time");
//                System.out.println("6. Main Menu");
//
//                System.out.print("Enter your choice: ");
//                int choice = scan.nextInt();
//
//                switch (choice) {
//                    case 1:
//                        if (playerThread.isAlive()) {
//                            playerThread.suspend(); // Pause the music
//                        }
//                        break;
//                    case 2:
//                        if (playerThread.isAlive()) {
//                            playerThread.resume(); // Resume the music
//                        }
//                        break;
//                    case 3:
//                        if (playerThread.isAlive()) {
//                            playerThread.stop(); // Stop the music
//                            isPlaying = false;
//                        }
//                        break;
//                    case 4:
//                        if (playerThread.isAlive()) {
//                            playerThread.stop(); // Stop the current song
//                            playMp3(filePath); // Play the song again
//                        }
//                        break;
//                    case 5:
//                        if (playerThread.isAlive()) {
//                            playerThread.stop(); // Stop the music
//                            isPlaying = false;
//                        }
//                        new mainMenu();
//                        return;
//                    default:
//                        System.out.println("Invalid choice. Please try again.");
//                }
//            }
//        }
//
//        // Handles MP3 playback raw
//        private static void playMp3(String filePath, String lyricsFile, String title, String artist, String album) throws Exception {
//            if (filePath == null || filePath.isEmpty()) {
//                System.out.println("File path is null or empty.");
//                return;
//            }
//
//            File file = new File(filePath);
//            if (!file.exists()) {
//                System.out.println("File does not exist: " + filePath);
//                return;
//            }
//
//            if (isPlaying) {
//                stopMusic();
//            }
//
//            FileInputStream fis = new FileInputStream(file);
//            BufferedInputStream bis = new BufferedInputStream(fis);
//            Player player = new Player(bis);
//
//            // Create a new thread to play the music
//            Thread playerThread = new Thread(() -> {
//                try {
//                    player.play();
//                } catch (JavaLayerException e) {
//                    e.printStackTrace();
//                }
//            });
//
//            playerThread.start();
//            isPlaying = true;
//
//            // Handle user commands
//            Scanner scan = new Scanner(System.in);
//            while (true) {
//                System.out.println("Music player commands:");
//                System.out.println("1. Play");
//                System.out.println("2. Pause");
//                System.out.println("3. Stop");
//                System.out.println("4. Repeat");
//                System.out.println("5. Jump to Song time");
//                System.out.println("6. Main Menu");
//
//                System.out.print("Enter your choice: ");
//                int choice = scan.nextInt();
//
//                switch (choice) {
//                    case 1:
//                        if (playerThread.isAlive()) {
//                            playerThread.suspend(); // Pause the music
//                        }
//                        break;
//                    case 2:
//                        if (playerThread.isAlive()) {
//                            playerThread.resume(); // Resume the music
//                        }
//                        break;
//                    case 3:
//                        if (playerThread.isAlive()) {
//                            playerThread.stop(); // Stop the music
//                            isPlaying = false;
//                        }
//                        break;
//                    case 4:
//                        if (playerThread.isAlive()) {
//                            playerThread.stop(); // Stop the current song
//                            playMp3(filePath); // Play the song again
//                        }
//                        break;
//                    case 5:
//                        if (playerThread.isAlive()) {
//                            playerThread.stop(); // Stop the music
//                            isPlaying = false;
//                        }
////                        Main.mainMenu();
//                        return;
//                    default:
//                        System.out.println("Invalid choice. Please try again.");
//                }
//            }
//        }
//        private static String getExtension(File file) {
//            String name = file.getName();
//            int dotIndex = name.lastIndexOf('.');
//            return (dotIndex == -1) ? "" : name.substring(dotIndex + 1);
//        }
//
//    }
//}