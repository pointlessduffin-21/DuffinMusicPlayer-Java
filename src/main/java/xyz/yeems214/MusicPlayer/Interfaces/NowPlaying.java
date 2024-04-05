package xyz.yeems214.MusicPlayer.Interfaces;

import org.jflac.sound.spi.FlacAudioFileReader;
import xyz.yeems214.MusicPlayer.Entity;
import xyz.yeems214.MusicPlayer.Main;
import xyz.yeems214.MusicPlayer.Extensions.ElapsedTimeTracker;
import xyz.yeems214.MusicPlayer.Extensions.AlbumArtViewer;

import javax.sound.sampled.*;
import java.io.File;
import java.io.IOException;
import java.util.InputMismatchException;
import java.util.List;
import java.util.Scanner;

import static java.lang.System.exit;
import static xyz.yeems214.MusicPlayer.Interfaces.FileManager.FileMenu;

public class NowPlaying extends Main {
    public static Clip clip;
    public static boolean isPaused = false;
    private static boolean autoResume = false;
    private static volatile boolean isPlaying = false;
    private static volatile boolean alreadyPlaying = false;
    private static volatile boolean songEnded = false;
    private static boolean repeat = false;
    public static boolean wasInRepeatB4 = false;
    private static int pausePosition = 0;
    private static int currentSongIndex = -1;
    private static ElapsedTimeTracker elapsedTimeTracker;
    private static void stopIfPlaying() {
        if (clip != null && clip.isRunning()) {
            clip.stop();
            clip.setFramePosition(0);
        }
    }

    public static void Player(Entity song) throws Exception {
        if (song.getFilePath() == null) {
            System.out.println("No music file loaded. Please select a song to play.");
            return;
        }
        try {
            clearConsole();
            File file = new File(song.getFilePath());
            String extension = getFileExtension(file);
            stopIfPlaying(); // Stop any currently playing song
            switch (extension.toLowerCase()) {
                case "wav":
                    handleWav(song.getFilePath(), song.getLyricsFile(), song.getTitle(), song.getArtist(), song.getAlbum(), song.getAlbumArt());
                    break;
                case "mp3":
                    System.out.println("MP3 Playback is currently disabled!");
                    break;
                case "flac":
                    playFlac(song.getFilePath(), song.getLyricsFile(), song.getTitle(), song.getArtist(), song.getAlbum(), song.getAlbumArt());
                    break;
                default:
                    System.out.println("Unsupported file format.");
                    break;
            }
        } catch (Exception e) {
            System.out.println("An error occurred! We apologize for the inconvenience!");
            System.out.println("\nNow restarting the application!");
            clearConsole();
            Main.mainMenu();
        }
    }


    public static void Player(String filePath) throws Exception {
        if (filePath == null) {
            System.out.println("No music file loaded. Please select a song to play.");
            return;
        }
        try {
            clearConsole();
            Scanner scan = new Scanner(System.in);
            File file = new File(filePath);

            String extension = getFileExtension(file);
            stopIfPlaying(); // Stop any currently playing song
            switch (extension.toLowerCase()) {
                case "wav":
                    handleWav(filePath);
                    break;
                case "mp3":
                    System.out.println("MP3 Playback is currently disabled!");
                    break;
                case "flac":
                    playFlac(filePath);
                    break;
                default:
                    System.out.println("Unsupported file format.");
                    break;
            }
        } catch (Exception e) {
            System.out.println("An error occurred! We apologize for the inconvenience!");
            System.out.println("\nNow restarting the application!");
            clearConsole();
            Main.mainMenu();
        }
    }

    public static void Player(int id, String filePath, String lyricsFile, String title, String artist, String album, String albumArt) throws Exception {
        if (filePath == null) {
            System.out.println("No music file loaded. Please select a song to play.");
            return;
        }
        try {
            clearConsole();
            Scanner scan = new Scanner(System.in);
            File file = new File(filePath);

            String extension = getFileExtension(file);
            stopIfPlaying(); // Stop any currently playing song
            switch (extension.toLowerCase()) {
                case "wav":
                    handleWav(filePath, lyricsFile, title, artist, album, albumArt);
                    break;
                case "mp3":
                    System.out.println("MP3 Playback is currently disabled!");
                    break;
                case "flac":
                    playFlac(id, filePath, lyricsFile, title, artist, album, albumArt, FileManager.getAllSongs());
                    break;
                default:
                    System.out.println("Unsupported file format.");
                    break;
            }
        } catch (Exception e) {
            System.out.println("An error occurred! We apologize for the inconvenience!");
            System.out.println("\nNow restarting the application!");
            clearConsole();
            Main.mainMenu();
        }
    }

    public static void Player(String filePath, String lyricsFile, String title, String artist, String album, String albumArt) throws Exception {
        if (filePath == null) {
            System.out.println("No music file loaded. Please select a song to play.");
            return;
        }
        try {
            clearConsole();
            Scanner scan = new Scanner(System.in);
            File file = new File(filePath);

            String extension = getFileExtension(file);
            stopIfPlaying(); // Stop any currently playing song
            switch (extension.toLowerCase()) {
                case "wav":
                    handleWav(filePath, lyricsFile, title, artist, album, albumArt);
                    break;
                case "mp3":
                    System.out.println("MP3 Playback is currently disabled!");
                    break;
                case "flac":
                    playFlac(filePath, lyricsFile, title, artist, album, albumArt);
                    break;
                default:
                    System.out.println("Unsupported file format.");
                    break;
            }
        } catch (Exception e) {
            System.out.println("An error occurred! We apologize for the inconvenience!");
            System.out.println("\nNow restarting the application!");
            clearConsole();
            Main.mainMenu();
        }
    }

    // Handles WAV playback with metadata
    public static void handleWav(String filePath, String lyricsFile, String title, String artist, String album, String albumArt) throws Exception {
        if (filePath == null || filePath.isEmpty()) {
            System.out.println("File path is null or empty.");
            return;
        }

        File file = new File(filePath);
        if (!file.exists()) {
            Scanner scan = new Scanner(System.in);
            clearConsole();
            System.out.println("File does not exist: " + filePath);
            System.out.println("Press any key to continue.");
            String choice = scan.nextLine();
            switch (choice) {
                default:
                    clearConsole();
                    return;
            }
        }

        try {
            clearConsole();
            Scanner scan = new Scanner(System.in);

            AudioInputStream audioStream = AudioSystem.getAudioInputStream(file);
            clip = AudioSystem.getClip();
            clip.open(audioStream);

            clip.addLineListener(new LineListener() {
                public void update(LineEvent evt) {
                    if (evt.getType() == LineEvent.Type.STOP) {
                        if (repeat) {
                            clip.setFramePosition(0);
                            clip.start();
                        }
                    }
                }
            });

            actualPlay(filePath, lyricsFile, title, artist, album);
            while (true) {
                System.out.print("Enter your choice: ");
                int choice = scan.nextInt();

                switch (choice) {
                    case 1:
                        actualPlay(filePath, lyricsFile, title, artist, album);
                        break;
                    case 2:
                        if (repeat = !true) {
                            repeat = true;
                            wasInRepeatB4 = false;
                            pauseMusic();
                        } else {
                            pauseMusic();
                        }
                        break;
                    case 3:
                        if (repeat = !true) {
                            repeat = true;
                            wasInRepeatB4 = false;
                            stopMusic();
                        } else {
                            stopMusic();
                        }
                        break;
                    case 4:
                        repeatMusic();
                        break;
                    case 5:
                        if (repeat = !true) {
                            repeat = true;
                            wasInRepeatB4 = false;
                            jumpToMusic(filePath, lyricsFile, title, artist, album);
                        } else {
                            jumpToMusic(filePath, lyricsFile, title, artist, album);
                        }
                        break;
                    case 6:
                        if (repeat = !true) {
                            repeat = true;
                            wasInRepeatB4 = false;
                            stopMusic();
                            FileManager.viewAllSongs();
                        } else {
                            stopMusic();
                            FileManager.viewAllSongs();
                        }
                        return;
                    case 7:
                        if (repeat = !true) {
                            repeat = true;
                            wasInRepeatB4 = false;
                            stopMusic();
                            mainMenu();
                        } else {
                            stopMusic();
                            mainMenu();
                        }
                        return;
                    case 8:
                        AlbumArtViewer.deez(filePath, albumArt);
                        break;
                    case 0:
                        clearConsole();
                        refreshConsole();
                        break;
                    default:
                        System.out.println("Invalid choice. Please try again.");
                        break;
                }
            }
        } catch (Exception e) {
            System.out.println("An error occurred! We apologize for the inconvenience!");
            System.out.println("\nNow restarting the application!");
            clearConsole();
            Main.mainMenu();
        }
    }

    // Handles WAV playback raw
    public static void handleWav(String filePath) throws Exception {
        if (filePath == null || filePath.isEmpty()) {
            System.out.println("File path is null or empty.");
            return;
        }

        File file = new File(filePath);
        if (!file.exists()) {
            Scanner scan = new Scanner(System.in);
            clearConsole();
            System.out.println("File does not exist: " + filePath);
            System.out.println("Press any key to continue.");
            String choice = scan.nextLine();
            switch (choice) {
                default:
                    clearConsole();
                    return;
            }
        }

        try {
            clearConsole();
            Scanner scan = new Scanner(System.in);

            AudioInputStream audioStream = AudioSystem.getAudioInputStream(file);
            clip = AudioSystem.getClip();
            clip.open(audioStream);

            clip.addLineListener(new LineListener() {
                public void update(LineEvent evt) {
                    if (evt.getType() == LineEvent.Type.STOP) {
                        if (repeat) {
                            clip.setFramePosition(0);
                            clip.start();
                        }
                    }
                }
            });

            actualPlayNoMetadata(filePath);

            while (true) {
                System.out.print("Enter your choice: ");
                int choice = scan.nextInt();

                switch (choice) {
                    case 1:
                        actualPlayNoMetadata(filePath);
                        break;
                    case 2:
                        if (repeat = !true) {
                            repeat = true;
                            wasInRepeatB4 = false;
                            pauseMusic();
                        } else {
                            pauseMusic();
                        }
                        break;
                    case 3:
                        if (repeat = !true) {
                            repeat = true;
                            wasInRepeatB4 = false;
                            stopMusic();
                        } else {
                            stopMusic();
                        }
                        break;
                    case 4:
                        repeatMusic();
                        break;
                    case 5:
                        if (repeat = !true) {
                            repeat = true;
                            wasInRepeatB4 = false;
                            jumpToMusic(filePath, lyricsFile, title, artist, album);
                        } else {
                            jumpToMusic(filePath, lyricsFile, title, artist, album);
                        }
                        break;
                    case 6:
                        if (repeat = !true) {
                            repeat = true;
                            wasInRepeatB4 = false;
                            stopMusic();
                            FileManager.viewAllSongs();
                        } else {
                            stopMusic();
                            FileManager.viewAllSongs();
                        }
                        return;
                    case 7:
                        if (repeat = !true) {
                            repeat = true;
                            wasInRepeatB4 = false;
                            stopMusic();
                            mainMenu();
                        } else {
                            stopMusic();
                            mainMenu();
                        }
                        return;
                    case 8:
                        System.out.println("No album art found.");
                        break;
                    case 0:
                        clearConsole();
                        refreshConsole();
                        break;
                    default:
                        System.out.println("Invalid choice. Please try again.");
                        break;
                }
            }
        } catch (Exception e) {
            System.out.println("An error occurred! We apologize for the inconvenience!");
            System.out.println("\nNow restarting the application!");
            clearConsole();
            Main.mainMenu();
        }
    }

    public static void playbackControlsFLAC(int id, String filePath, String lyricsFile, String title, String artist, String album, String albumArt, List<Entity> songs) throws Exception {
        // Handle user commands
        Scanner scan = new Scanner(System.in);
        while (true) {
            try {
                int choice = scan.nextInt();

                switch (choice) {
                    case 1:
                        actualPlay(filePath, lyricsFile, title, artist, album);
                        break;
                    case 2:
                        if (repeat = !true) {
                            repeat = true;
                            wasInRepeatB4 = false;
                            pauseMusic();
                        } else {
                            pauseMusic();
                        }
                        break;
                    case 3:
                        if (repeat = !true) {
                            repeat = true;
                            wasInRepeatB4 = false;
                            stopMusic();
                        } else {
                            stopMusic();
                        }
                        break;
                    case 4:
                        repeatMusic();
                        break;
                    case 5:
                        if (repeat = !true) {
                            repeat = true;
                            wasInRepeatB4 = false;
                            jumpToMusic(filePath, lyricsFile, title, artist, album);
                        } else {
                            jumpToMusic(filePath, lyricsFile, title, artist, album);
                        }
                        break;
                    case 6:
                        if (repeat = !true) {
                            repeat = true;
                            wasInRepeatB4 = false;
                            stopMusic();
                            FileManager.viewAllSongs();
                        } else {
                            stopMusic();
                            FileManager.viewAllSongs();
                        }
                        return;
                    case 7:
                        if (repeat = !true) {
                            repeat = true;
                            wasInRepeatB4 = false;
                            stopMusic();
                            mainMenu();
                        } else {
                            stopMusic();
                            mainMenu();
                        }
                        return;
                    case 8:
                        AlbumArtViewer.deez(filePath, albumArt);
                        break;
                    case 9: // Next song
                        currentSongIndex = (currentSongIndex + 1) % songs.size();
                        Entity nextSong = songs.get(currentSongIndex);
                        Player(nextSong.getId(), songs); // Play the next song
                        break;

                    case 10: // Previous song
                        currentSongIndex = (currentSongIndex - 1 + songs.size()) % songs.size(); // Handle wrap-around
                        Entity prevSong = songs.get(currentSongIndex);
                        Player(prevSong.getId(), songs); // Play the previous song
                        break;
                    case 0:
                        clearConsole();
                        refreshConsole();
                        break;
                    default:
                        System.out.println("Invalid choice. Please try again.");
                        break;
                }
            } catch (InputMismatchException e) {
                System.out.println("Invalid input. Please enter a number.");
                scan.next();
            } catch (Exception e) {
                System.out.println("An error occurred! We apologize for the inconvenience!");
                System.out.println("\nNow restarting the application!");
                clearConsole();
                Main.mainMenu();
            }
        }
    }

    private static void Player(int id, List<Entity> songs) throws Exception {
        // Find the song with the given ID
        for (Entity song : songs) {
            if (song.getId() == id) {
                // Play the song
                Player(song);
                return;
            }
        }
        // If the song with the given ID is not found, print an error message
        System.out.println("Song with ID " + id + " not found.");
    }

    public static void playbackControlsFLAC(String filePath, String lyricsFile, String title, String artist, String album, String albumArt) throws Exception {
        // Handle user commands
        Scanner scan = new Scanner(System.in);
        while (true) {
            try {
                int choice = scan.nextInt();

                switch (choice) {
                    case 1:
                        actualPlay(filePath, lyricsFile, title, artist, album);
                        break;
                    case 2:
                        if (repeat = !true) {
                            repeat = true;
                            wasInRepeatB4 = false;
                            pauseMusic();
                        } else {
                            pauseMusic();
                        }
                        break;
                    case 3:
                        if (repeat = !true) {
                            repeat = true;
                            wasInRepeatB4 = false;
                            stopMusic();
                        } else {
                            stopMusic();
                        }
                        break;
                    case 4:
                        repeatMusic();
                        break;
                    case 5:
                        if (repeat = !true) {
                            repeat = true;
                            wasInRepeatB4 = false;
                            jumpToMusic(filePath, lyricsFile, title, artist, album);
                        } else {
                            jumpToMusic(filePath, lyricsFile, title, artist, album);
                        }
                        break;
                    case 6:
                        if (repeat = !true) {
                            repeat = true;
                            wasInRepeatB4 = false;
                            stopMusic();
                            FileManager.viewAllSongs();
                        } else {
                            stopMusic();
                            FileManager.viewAllSongs();
                        }
                        return;
                    case 7:
                        if (repeat = !true) {
                            repeat = true;
                            wasInRepeatB4 = false;
                            stopMusic();
                            mainMenu();
                        } else {
                            stopMusic();
                            mainMenu();
                        }
                        return;
                    case 8:
                        AlbumArtViewer.deez(filePath, albumArt);
                        break;
                    case 0:
                        clearConsole();
                        refreshConsole();
                        break;
                    default:
                        System.out.println("Invalid choice. Please try again.");
                        break;
                }
            } catch (InputMismatchException e) {
                System.out.println("Invalid input. Please enter a number.");
                scan.next();
            } catch (Exception e) {
                System.out.println("An error occurred! We apologize for the inconvenience!");
                System.out.println("\nNow restarting the application!");
                clearConsole();
                Main.mainMenu();
            }
        }
    }

    public static void playMusic() throws Exception {
        if (clip != null || songEnded ) {
            if (clip.getFramePosition() == clip.getFrameLength()) {
                int choice;
                songEnded = true;
                System.out.println("The song has already ended. What would you like to do next?");
                System.out.println("1. Go back to File Manager");
                System.out.println("2. Play the song again");
                Scanner scan = new Scanner(System.in);

                while (true) {
                    System.out.print("Enter your choice: ");
                    choice = scan.nextInt();
                    switch (choice) {
                        case 1:
                            if (elapsedTimeTracker != null) {
                                elapsedTimeTracker.stopRunning();
                            }
                            FileMenu();
                            return;
                        case 2:
                            clip.setFramePosition(0);
                            clip.start();

                            if (elapsedTimeTracker == null) {
                                elapsedTimeTracker = new ElapsedTimeTracker(clip);
                            }
                            elapsedTimeTracker.run(filePath);
                            return;
                        default:
                            System.out.println("Invalid choice. Please try again.");
                            break;
                    }
                }
            } else {
                clip.start();
                alreadyPlaying = true;

                if (elapsedTimeTracker == null) {
                    elapsedTimeTracker = new ElapsedTimeTracker(clip);
                }

                Thread trackerThread = new Thread(() -> elapsedTimeTracker.run(filePath));
                trackerThread.start();
            }
        } else {
            System.out.println("No music file loaded.");
        }
    }
    private static void pauseMusic() throws Exception {
        if (clip != null && clip.isRunning()) {
            isPaused = true;
            alreadyPlaying = false;
            pausePosition = clip.getFramePosition(); // Remember the position
            ElapsedTimeTracker elapsedTimeTracker = new ElapsedTimeTracker(clip);
            elapsedTimeTracker.pause();
            clip.stop();
            System.out.println("Song has been paused!");
            autoResume = false; // Reset the flag
            if (wasInRepeatB4 == true) {
                repeat = true;
            }
            if (autoResume) {
                // Automatically resume playback
                playMusic();
            }
        } else {
            System.out.println("No music is playing.");
        }
    }

    private static void stopMusic() {
        if (clip != null) {
            isPaused = false;
            alreadyPlaying = false;
            clip.stop();
            clip.setFramePosition(0);
            System.out.println("Song has been stopped!");
            if (wasInRepeatB4 == true) {
                repeat = true;
            }
        } else {
            System.out.println("No music is playing.");
        }
    }

    private static void repeatMusic() {
        if (clip != null) {
            repeat = !repeat;
            if (repeat) {
                wasInRepeatB4 = true;
                System.out.println("Repeat: On");
            } else {
                wasInRepeatB4 = false;
                System.out.println("Repeat: Off");
            }
        } else {
            System.out.println("No music file loaded.");
        }
    }

    private static void playAudioStream(AudioInputStream stream) throws Exception {
        if (clip != null && clip.isOpen()) {
            clip.close(); // Close existing clip if open
        }
        clip = AudioSystem.getClip();
        try {
            clip.open(stream);
            if (clip.isOpen()) {
                clip.start();
                long startTime = System.currentTimeMillis(); // Record start time
                long totalLength = stream.getFrameLength() * (long) (1000000.0 / stream.getFormat().getFrameRate()); // Calculate total duration in microseconds
                System.out.println("Audio format: " + stream.getFormat()); // Print audio format details
                System.out.println("Total length (ms): " + totalLength / 1000); // Print total length in milliseconds
                while (clip.isRunning()) {
                    ElapsedTimeTracker elapsedTimeTracker = new ElapsedTimeTracker(clip);
                    elapsedTimeTracker.run();
                }
                long endTime = System.currentTimeMillis();
                System.out.println("Playback completed in: " + (endTime - startTime) + " ms");
            } else {
                System.err.println("Failed to open clip");
            }
        } catch (Exception e) {
            System.out.println("An error occurred! We apologize for the inconvenience!");
            System.out.println("\nNow restarting the application!");
            clearConsole();
            Main.mainMenu();
        } finally {
            stream.close();
        }
    }

    private static void actualPlay(String filePath, String lyricsFile, String title, String artist, String album) throws Exception {
        if (alreadyPlaying == true) {
            System.out.println("Song is already playing!");
        } else {
            Thread trackerThread = new Thread(() -> {
                ElapsedTimeTracker elapsedTimeTracker = new ElapsedTimeTracker(clip, filePath, lyricsFile, title, artist, album);
                if (lyricsFile != null && lyricsFile.endsWith(".txt")) { // This exists so that this fucker wont fry your cpu!
                    elapsedTimeTracker.run(filePath, lyricsFile, title, artist, album);
                } else {
                    elapsedTimeTracker.runNoLyrics(filePath, title, artist, album);
                }
            });
            trackerThread.start();
            autoResume = true;
            playMusic();
        }
    }

    private static void actualPlay(int id, String filePath, String lyricsFile, String title, String artist, String album) throws Exception {
        if (alreadyPlaying == true) {
            System.out.println("Song is already playing!");
        } else {
            Thread trackerThread = new Thread(() -> {
                ElapsedTimeTracker elapsedTimeTracker = new ElapsedTimeTracker(clip, filePath, lyricsFile, title, artist, album);
                if (lyricsFile != null && lyricsFile.endsWith(".txt")) { // This exists so that this fucker wont fry your cpu!
                    elapsedTimeTracker.run(filePath, lyricsFile, title, artist, album);
                } else {
                    elapsedTimeTracker.runNoLyrics(filePath, title, artist, album);
                }
            });
            trackerThread.start();
            autoResume = true;
            playMusic();
        }
    }

    private static void actualPlayNoMetadata(String filePath) throws Exception {
        if (alreadyPlaying == true) {
            System.out.println("Song is already playing!");
        } else {
            Thread trackerThread = new Thread(() -> {
                ElapsedTimeTracker elapsedTimeTracker = new ElapsedTimeTracker(clip, filePath, null, filePath, null, null);
                elapsedTimeTracker.run(filePath, null, filePath, null, null);
            });
            trackerThread.start();
            playMusic();
        }
    }

    public static void jumpToMusic(String filePath, String lyricsFile, String title, String artist, String album) throws Exception {
        Scanner scan = new Scanner(System.in);
        pauseMusic();
        System.out.println("Enter time (MM:SS) to jump to part: ");
        String timeInput = scan.next();
        String[] timeParts = timeInput.split(":");
        if (timeParts.length != 2) {
            System.out.println("Invalid format. Please enter time in MM:SS format.");
            return;
        }
        int minutes = Integer.parseInt(timeParts[0]);
        int seconds = Integer.parseInt(timeParts[1]);
        long microseconds = (minutes * 60L + seconds) * 1000000L;
        clip.setMicrosecondPosition(microseconds);
        System.out.println("Press 1 to resume playback.");
        int choice = scan.nextInt();
        while (true) {
            switch (choice) {
                case 1:
                    if (wasInRepeatB4) {
                        repeat = true;
                        actualPlay(filePath, lyricsFile, title, artist, album);
                    } else {
                        actualPlay(filePath, lyricsFile, title, artist, album);
                    }
                    break;
                default:
                    System.out.println("Invalid choice. Please try again.");
                    break;
            }
            break;
        }
    }

    // Handles FLAC playback with metadata
    private static void playFlac(int id, String filePath, String lyricsFile, String title, String artist, String album, String albumArt, List<Entity> songs) throws Exception {
        if (filePath == null || filePath.isEmpty()) {
            System.out.println("File path is null or empty.");
            return;
        }

        File file = new File(filePath);
        if (!file.exists()) {
            Scanner scan = new Scanner(System.in);
            clearConsole();
            System.out.println("File does not exist: " + filePath);
            System.out.println("Press any key to continue.");
            String choice = scan.nextLine();
            switch (choice) {
                default:
                    clearConsole();
                    return;
            }
        }

        if (isPlaying) {
            stopMusic();
        }

        AudioInputStream originalStream = new FlacAudioFileReader().getAudioInputStream(file);
        AudioFormat baseFormat = originalStream.getFormat();
        AudioFormat decodedFormat = new AudioFormat(AudioFormat.Encoding.PCM_SIGNED,
                baseFormat.getSampleRate(),
                16,
                baseFormat.getChannels(),
                baseFormat.getChannels() * 2,
                baseFormat.getSampleRate(),
                false);
        AudioInputStream decodedStream = AudioSystem.getAudioInputStream(decodedFormat, originalStream);

        // Create a Clip from the AudioInputStream
        clip = AudioSystem.getClip();
        clip.open(decodedStream);

        clip.addLineListener(new LineListener() {
            public void update(LineEvent evt) {
                if (evt.getType() == LineEvent.Type.STOP) {
                    songEnded = true;
                    if (repeat) {
                        clip.setFramePosition(0);
                        clip.start();
                    }
                }
            }
        });

        actualPlay(id, filePath, lyricsFile, title, artist, album);
        playbackControlsFLAC(id, filePath, lyricsFile, title, artist, album, albumArt, songs);
    }



    // Handles FLAC playback with metadata
    private static void playFlac(String filePath, String lyricsFile, String title, String artist, String album, String albumArt) throws Exception {
        if (filePath == null || filePath.isEmpty()) {
            System.out.println("File path is null or empty.");
            return;
        }

        File file = new File(filePath);
        if (!file.exists()) {
            Scanner scan = new Scanner(System.in);
            clearConsole();
            System.out.println("File does not exist: " + filePath);
            System.out.println("Press any key to continue.");
            String choice = scan.nextLine();
            switch (choice) {
                default:
                    clearConsole();
                    return;
            }
        }

        if (isPlaying) {
            stopMusic();
        }

        AudioInputStream originalStream = new FlacAudioFileReader().getAudioInputStream(file);
        AudioFormat baseFormat = originalStream.getFormat();
        AudioFormat decodedFormat = new AudioFormat(AudioFormat.Encoding.PCM_SIGNED,
                baseFormat.getSampleRate(),
                16,
                baseFormat.getChannels(),
                baseFormat.getChannels() * 2,
                baseFormat.getSampleRate(),
                false);
        AudioInputStream decodedStream = AudioSystem.getAudioInputStream(decodedFormat, originalStream);

        // Create a Clip from the AudioInputStream
        clip = AudioSystem.getClip();
        clip.open(decodedStream);

        clip.addLineListener(new LineListener() {
            public void update(LineEvent evt) {
                if (evt.getType() == LineEvent.Type.STOP) {
                    songEnded = true;
                    if (repeat) {
                        clip.setFramePosition(0);
                        clip.start();
                    }
                }
            }
        });

        actualPlay(filePath, lyricsFile, title, artist, album);
        playbackControlsFLAC(filePath, lyricsFile, title, artist, album, albumArt);
    }

    // Handles FLAC playback raw
    private static void playFlac(String filePath) throws Exception {
        if (filePath == null || filePath.isEmpty()) {
            System.out.println("File path is null or empty.");
            return;
        }

        File file = new File(filePath);
        if (!file.exists()) {
            Scanner scan = new Scanner(System.in);
            clearConsole();
            System.out.println("File does not exist: " + filePath);
            System.out.println("Press any key to continue.");
            String choice = scan.nextLine();
            switch (choice) {
                default:
                    clearConsole();
                    return;
            }
        }

        if (isPlaying) {
            stopMusic();
        }

        AudioInputStream originalStream = new FlacAudioFileReader().getAudioInputStream(file);
        AudioFormat baseFormat = originalStream.getFormat();
        AudioFormat decodedFormat = new AudioFormat(AudioFormat.Encoding.PCM_SIGNED,
                baseFormat.getSampleRate(),
                16,
                baseFormat.getChannels(),
                baseFormat.getChannels() * 2,
                baseFormat.getSampleRate(),
                false);
        AudioInputStream decodedStream = AudioSystem.getAudioInputStream(decodedFormat, originalStream);

        // Create a Clip from the AudioInputStream
        clip = AudioSystem.getClip();
        clip.open(decodedStream);

        clip.addLineListener(new LineListener() {
            public void update(LineEvent evt) {
                if (evt.getType() == LineEvent.Type.STOP) {
                    songEnded = true;
                    if (repeat) {
                        clip.setFramePosition(0);
                        clip.start();
                    }
                }
            }
        });
        playbackControlsFLAC(filePath, null, filePath, null, null, filePath);
    }
    private static String getFileExtension(File file) {
        String fileName = file.getName();
        int dotIndex = fileName.lastIndexOf('.');
        return (dotIndex == -1) ? "" : fileName.substring(dotIndex + 1);
    }
}