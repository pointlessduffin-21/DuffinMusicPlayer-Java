package xyz.yeems214.MusicPlayer.Interfaces;

import org.apache.commons.io.IOUtils;
import org.jaudiotagger.audio.AudioFile;
import org.jaudiotagger.audio.AudioFileIO;
import org.jaudiotagger.tag.FieldKey;
import org.jaudiotagger.tag.Tag;
import xyz.yeems214.MusicPlayer.Extensions.AudioConverterUnix;
import xyz.yeems214.MusicPlayer.Extensions.AudioConverterWindows;
import xyz.yeems214.MusicPlayer.Main;
import xyz.yeems214.MusicPlayer.Extensions.AlbumArtViewer;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.BufferedReader;
import java.util.Scanner;
import java.util.logging.LogManager;

import static xyz.yeems214.MusicPlayer.Extensions.AudioConverterWindows.extensionsDirectory;
import static xyz.yeems214.MusicPlayer.Interfaces.FileManager.FileMenu;

// This class is responsible for allowing the user to play music by entering a link to a song even when the song isn't in the library.

public class playByLink extends Main {
    public static void address() throws Exception {
        clearConsole();
        System.out.println("\033[1;34mPlay by Link\033[0m");
        System.out.println("Please enter the address of a song to play (Supported formats: .wav, .flac): Press q to quit. \n");
        Scanner input = new Scanner(System.in);
        String filePath = input.nextLine();
        if (input == null) {
            System.out.println("The system cannot find the path specified! \n");
            return;
        } else if (filePath.equals("q")) {
            mainMenu();
        } else {
            clearConsole();
            System.out.println("Do you want to add lyrics to the song? (y/n)");
            if (input.nextLine().equals("y")) {
                clearConsole();
                System.out.println("Please enter the address of the lyrics file: ");
                String lyricsFile = input.nextLine();
                songWithLyricsOptions(filePath, lyricsFile);
                if (lyricsFile.equals("q")) {
                    mainMenu();
                }
            }
        }
        songOptions(filePath);
    }

    public static void songWithLyricsOptions(String filePath, String lyricsFile) throws Exception {
        String os = System.getProperty("os.name").toLowerCase();
        while (true) {
            clearConsole();
            System.out.println("\n\033[1;32mYou have selected: " + filePath + "\033[0m");
            System.out.println("Enter your choice: ");
            System.out.println("[1] - Play");
            System.out.println("[2] - Check Metadata");
            System.out.println("[3] - Pick another song");
            System.out.println("[4] - Pick another link");
            System.out.println("[5] - Show Album Art");
            System.out.println("[6] - Main Menu");
            System.out.println("[7] - Quit");
            Scanner input = new Scanner(System.in);
            int choice = input.nextInt();
            switch (choice) {
                case 1:
                    String audioFormat = getAudioFormat(filePath);
                    if (audioFormat != null && !audioFormat.contains("s16")) {
                        System.out.println("The audio format is not 16 bit. Do you want to convert it? (y/n)");
                        Scanner input2 = new Scanner(System.in);
                        String choice2 = input2.nextLine().toLowerCase();
                        if (choice2.equals("y")) {
                            if (os.contains("win")) {
                                AudioConverterWindows.flacConverter(filePath);
                            } else if (os.contains("mac") || os.contains("nix") || os.contains("nux") || os.contains("aix")) {
                                AudioConverterUnix.flacConverter(filePath);
                            } else {
                                System.out.println("OS unknown! Please send an email to f.abarca@yeems214.xyz for support.");
                                return;
                            }
                        }
                        return;
                    }
                    if (filePath.endsWith(".wav") || filePath.endsWith(".WAV")) {
                        NowPlaying.Player(filePath, null, filePath, "Unknown Artist", "Unknown Album", null);
                    } else if (filePath.endsWith(".flac") || filePath.endsWith(".FLAC")) {
                        jAudioMetadata(filePath);
                        NowPlaying.Player(filePath, lyricsFile, title, artist, album, null);
                    } else if (filePath.endsWith(".mp3") || filePath.endsWith(".MP3") || filePath.endsWith(".ogg") || filePath.endsWith(".OGG") || filePath.endsWith(".m4a") || filePath.endsWith(".M4A") || filePath.endsWith(".wma") || filePath.endsWith(".WMA") || filePath.endsWith(".aiff") || filePath.endsWith(".AIFF") || filePath.endsWith(".aif") || filePath.endsWith(".AIF") || filePath.endsWith(".aifc") || filePath.endsWith(".AIFC") || filePath.endsWith(".au") || filePath.endsWith(".AU") || filePath.endsWith(".snd") || filePath.endsWith(".SND") || filePath.endsWith(".MP4") || filePath.endsWith(".mp4")) {
                        System.out.println("Format not supported!");
                        Scanner input3 = new Scanner(System.in);
                        System.out.println("Do you want to convert this as a FLAC? (y/n)");
                        String choice3 = input3.nextLine().toLowerCase();
                        switch (choice3) {
                            case "y":
                                if (os.contains("win")) {
                                    AudioConverterWindows.flacConverter(filePath);
                                } else if (os.contains("mac") || os.contains("nix") || os.contains("nux") || os.contains("aix")) {
                                    AudioConverterUnix.flacConverter(filePath);
                                } else {
                                    System.out.println("OS unknown! Please send an email to f.abarca@yeems214.xyz for support.");
                                    return;
                                }
                        }
                        return;
                    } else {
                        System.out.println("Format not supported!");
                        return;
                    }
                    break;
                case 2:
                    audioMetadata(filePath);
                    break;
                case 3:
                    FileMenu();
                    break;
                case 4:
                    playByLink.address();
                    break;
                case 5:
                    AlbumArtViewer.deez(filePath, null);
                    break;
                case 6:
                    mainMenu();
                    break;
                case 7:
                    System.out.println("Goodbye!");
                    System.exit(0);
                    break;
                default:
                    System.out.println("Invalid choice.");
                    break;
            }
        }
    }

    public static void songOptions(String filePath) throws Exception {
        String os = System.getProperty("os.name").toLowerCase();
        while (true) {
            clearConsole();
            System.out.println("\n\033[1;32mYou have selected: " + filePath + "\033[0m");
            System.out.println("Options: ");
            System.out.println("[1] - Play");
            System.out.println("[2] - Check Metadata");
            System.out.println("[3] - Pick another song");
            System.out.println("[4] - Pick another link");
            System.out.println("[5] - Show Album Art");
            System.out.println("[6] - Main Menu");
            System.out.println("[7] - Quit");
            Scanner input = new Scanner(System.in);
            int choice = input.nextInt();
            switch (choice) {
                case 1:
                    String audioFormat = getAudioFormat(filePath);
                    if (audioFormat != null && !audioFormat.contains("s16")) {
                        System.out.println("The audio format is not 16-bit quality. Do you want to convert it? (y/n)");
                        Scanner input2 = new Scanner(System.in);
                        String choice2 = input2.nextLine().toLowerCase();
                        if (choice2.equals("y")) {
                            if (os.contains("win")) {
                                AudioConverterWindows.flacConverter(filePath);
                            } else if (os.contains("mac") || os.contains("nix") || os.contains("nux") || os.contains("aix")) {
                                AudioConverterUnix.flacConverter(filePath);
                            } else {
                                System.out.println("OS unknown! Please send an email to f.abarca@yeems214.xyz for support.");
                                return;
                            }
                        }
                        return;
                    }
                    if (filePath.endsWith(".wav") || filePath.endsWith(".WAV")) {
                        NowPlaying.Player(filePath, null, filePath, "Unknown Artist", "Unknown Album", null);
                    } else if (filePath.endsWith(".flac") || filePath.endsWith(".FLAC")) {
                        jAudioMetadata(filePath);
                        NowPlaying.Player(filePath, null, title, artist, album, null);
                    } else if (filePath.endsWith(".mp3") || filePath.endsWith(".MP3") || filePath.endsWith(".ogg") || filePath.endsWith(".OGG") || filePath.endsWith(".m4a") || filePath.endsWith(".M4A") || filePath.endsWith(".wma") || filePath.endsWith(".WMA") || filePath.endsWith(".aiff") || filePath.endsWith(".AIFF") || filePath.endsWith(".aif") || filePath.endsWith(".AIF") || filePath.endsWith(".aifc") || filePath.endsWith(".AIFC") || filePath.endsWith(".au") || filePath.endsWith(".AU") || filePath.endsWith(".snd") || filePath.endsWith(".SND") || filePath.endsWith(".MP4") || filePath.endsWith(".mp4")) {
                        System.out.println("Format not supported!");
                        Scanner input3 = new Scanner(System.in);
                        System.out.println("Do you want to convert this as a FLAC? (y/n)");
                        String choice3 = input3.nextLine().toLowerCase();
                        switch (choice3) {
                            case "y":
                                if (os.contains("win")) {
                                    AudioConverterWindows.flacConverter(filePath);
                                } else if (os.contains("mac") || os.contains("nix") || os.contains("nux") || os.contains("aix")) {
                                    AudioConverterUnix.flacConverter(filePath);
                                } else {
                                    System.out.println("OS unknown! Please send an email to f.abarca@yeems214.xyz for support.");
                                    return;
                                }
                        }
                        return;
                    } else {
                        System.out.println("Format not supported!");
                        return;
                    }
                    break;
                case 2:
                    audioMetadata(filePath);
                    break;
                case 3:
                    FileMenu();
                    break;
                case 4:
                    playByLink.address();
                    break;
                case 5:
                    AlbumArtViewer.deez(filePath, null);
                    break;
                case 6:
                    mainMenu();
                    break;
                case 7:
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
        }
    }

    public static void audioMetadata(String filePath) {
        clearConsole();
        LogManager.getLogManager().reset();
        try {
            File file = new File(filePath);
            AudioFile audioFile = AudioFileIO.read(file);
            Tag tag = audioFile.getTag();

            if (filePath.endsWith(".wav") || filePath.endsWith(".WAV")) {
                wavChunkInfo(filePath);
            } else if (filePath.endsWith(".mp3") || filePath.endsWith(".flac")) {
                jAudioMetadata(filePath);
            } else {
                System.out.println("File not supported!");
                return;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void jAudioMetadata(String filePath) {
        LogManager.getLogManager().reset();
        try {
            File file = new File(filePath);
            AudioFile audioFile = AudioFileIO.read(file);
            Tag tag = audioFile.getTag();

            if (tag != null) {
                title = tag.getFirst(FieldKey.TITLE);
                artist = tag.getFirst(FieldKey.ARTIST);
                album = tag.getFirst(FieldKey.ALBUM);

                System.out.println("Here's the metadata for: " + filePath);
                System.out.println("Title: " + tag.getFirst(FieldKey.TITLE));
                System.out.println("Artist: " + tag.getFirst(FieldKey.ARTIST));
                System.out.println("Album: " + tag.getFirst(FieldKey.ALBUM));
                System.out.println("Genre: " + tag.getFirst(FieldKey.GENRE));
                System.out.println("Year: " + tag.getFirst(FieldKey.YEAR));
                System.out.println("Composers: " + tag.getFirst(FieldKey.COMPOSER));
                System.out.println("Track Number: " + tag.getFirst(FieldKey.TRACK));
                System.out.println("Disc Number: " + tag.getFirst(FieldKey.DISC_NO) + "\n");
                System.out.println("Bit Rate: " + audioFile.getAudioHeader().getBitRate());
                System.out.println("Sample Rate: " + audioFile.getAudioHeader().getSampleRate());
                System.out.println("File Size: " + file.length() + " bytes");
                System.out.println("Lyrics: " + tag.getFirst(FieldKey.LYRICS) + "\n" + "\n");
            } else {
                System.out.println("Failed to read metadata from " + filePath + "\n");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void wavChunkInfo(String filePath) {
        File file = new File(filePath);
        byte[] data;

        try {
            data = IOUtils.toByteArray(new FileInputStream(file));

            if (data[0] != 'R' || data[1] != 'I' || data[2] != 'F' || data[3] != 'F') {
                throw new IOException("Not a WAV file");
            }

            int chunkStartPosition = 8;

            while (chunkStartPosition < data.length - 8) {
                String chunkId = new String(data, chunkStartPosition, 4);
                int chunkSize = (data[chunkStartPosition + 4] << 24) +
                        (data[chunkStartPosition + 5] << 16) +
                        (data[chunkStartPosition + 6] << 8) +
                        data[chunkStartPosition + 7];
                System.out.printf("Chunk ID: %s, Size: %d\n", chunkId, chunkSize);

                chunkStartPosition += 8 + chunkSize;
            }
        } catch (IOException e) {
            System.err.println("Error reading file: " + e.getMessage());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static String getAudioFormat(String filePath) throws IOException {
        String os = System.getProperty("os.name").toLowerCase();
        String[] command;

        if (os.contains("win")) {
            String ffmpegPath = extensionsDirectory + "ffmpeg/bin/ffmpeg.exe"; // Replace with the actual path to FFmpeg executable
            command = new String[]{
                    ffmpegPath,
                    "-i", filePath
            };
        } else if (os.contains("mac") || os.contains("nix") || os.contains("nux") || os.contains("aix")) {
            command = new String[]{
                    "ffmpeg",
                    "-i", filePath
            };
        } else {
            System.out.println("OS unknown! Please send an email to f.abarca@yeems214.xyz for support.");
            return null;
        }

        ProcessBuilder processBuilder = new ProcessBuilder(command);
        processBuilder.redirectErrorStream(true);

        Process process = processBuilder.start();

        try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.contains("Audio:")) {
                    return line;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}
