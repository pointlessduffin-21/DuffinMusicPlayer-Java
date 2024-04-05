package xyz.yeems214.MusicPlayer.TestingShit;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import org.apache.commons.io.IOUtils;
import org.jaudiotagger.audio.AudioFile;
import org.jaudiotagger.audio.AudioFileIO;
import org.jaudiotagger.tag.FieldKey;
import org.jaudiotagger.tag.Tag;
import xyz.yeems214.MusicPlayer.Interfaces.LyricsView;
import xyz.yeems214.MusicPlayer.Main;

import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;
import java.util.Scanner;
import java.util.logging.LogManager;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.jaudiotagger.audio.AudioHeader;
import xyz.yeems214.MusicPlayer.Entity;

public class NewMusicDatabase {
    private static final String DB_URL = "jdbc:sqlite:music.db";
    private static final String CREATE_TABLE_SQL = "CREATE TABLE IF NOT EXISTS songs (" +
            "id INTEGER PRIMARY KEY AUTOINCREMENT," +
            "filePath TEXT NOT NULL," +
            "title TEXT NOT NULL," +
            "artist TEXT NOT NULL," +
            "album TEXT NOT NULL," +
            "genre TEXT NOT NULL," +
            "year TEXT NOT NULL," +
            "composers TEXT NOT NULL," +
            "lyricsFile TEXT NOT NULL," +
            "trackNumber INTEGER NOT NULL," +
            "discNumber INTEGER NOT NULL" +
            ")";

    private static final String INSERT_SONG_SQL = "INSERT INTO songs (filePath, title, artist, album, genre, year, composers, lyricsFile, trackNumber, discNumber) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
    private static final String SELECT_ALL_SONGS_SQL = "SELECT * FROM songs";
    private static final String SEARCH_SONGS_SQL = "SELECT * FROM songs WHERE title LIKE ? OR artist LIKE ? OR album LIKE ? OR genre LIKE ?";
    private static final String UPDATE_SONG_SQL = "UPDATE songs SET filePath = ?, title = ?, artist = ?, album = ?, genre = ?, year = ?, composers = ?, lyricsFile = ?, trackNumber = ?, discNumber = ? WHERE id = ?";

    private static Connection connection;
    private static Scanner scanner = new Scanner(System.in);
    private static final String MUSIC_DIRECTORY = "src/main/resources/music-files/";
    private static final String LYRICS_DIRECTORY = "src/main/resources/lyrics-files/";
    private static void initializeDatabase() {
        try {
            connection = DriverManager.getConnection(DB_URL);
            Statement statement = connection.createStatement();
            statement.execute(CREATE_TABLE_SQL);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private static void insertSong(Entity song) {
        try {
            PreparedStatement statement = connection.prepareStatement(INSERT_SONG_SQL);
            statement.setString(1, song.getFilePath());
            statement.setString(2, song.getTitle());
            statement.setString(3, song.getArtist());
            statement.setString(4, song.getAlbum());
            statement.setString(5, song.getGenre());
            statement.setString(6, song.getYear());
            statement.setString(7, song.getComposers());
            statement.setString(8, song.getLyricsFile());
            statement.setInt(9, song.getTrackNumber());
            statement.setInt(10, song.getDiscNumber());
            statement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private static List<Entity> getAllSongs() {
        List<Entity> songs = new ArrayList<>();
        try {
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery(SELECT_ALL_SONGS_SQL);
            while (resultSet.next()) {
                int id = resultSet.getInt("id");
                String filePath = resultSet.getString("filePath");
                String title = resultSet.getString("title");
                String artist = resultSet.getString("artist");
                String album = resultSet.getString("album");
                String genre = resultSet.getString("genre");
                String year = resultSet.getString("year");
                String composers = resultSet.getString("composers");
                String lyricsFile = resultSet.getString("lyricsFile");
                int trackNumber = resultSet.getInt("trackNumber");
                int discNumber = resultSet.getInt("discNumber");
                Entity song = new Entity(id, filePath, title, artist, album, genre, year, composers, lyricsFile, trackNumber, discNumber, null);
                songs.add(song);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return songs;
    }

    private static List<Entity> searchSongs(String query) {
        List<Entity> songs = new ArrayList<>();
        try {
            PreparedStatement statement = connection.prepareStatement(SEARCH_SONGS_SQL);
            statement.setString(1, "%" + query + "%");
            statement.setString(2, "%" + query + "%");
            statement.setString(3, "%" + query + "%");
            statement.setString(4, "%" + query + "%");
            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()) {
                int id = resultSet.getInt("id");
                String filePath = resultSet.getString("filePath");
                String title = resultSet.getString("title");
                String artist = resultSet.getString("artist");
                String album = resultSet.getString("album");
                String genre = resultSet.getString("genre");
                String year = resultSet.getString("year");
                String composers = resultSet.getString("composers");
                String lyricsFile = resultSet.getString("lyricsFile");
                int trackNumber = resultSet.getInt("trackNumber");
                int discNumber = resultSet.getInt("discNumber");
                Entity song = new Entity(id, filePath, title, artist, album, genre, year, composers, lyricsFile, trackNumber, discNumber, null);
                songs.add(song);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return songs;
    }

    private static void editSongDetails(Entity song) {
        try {
            System.out.println("\nEdit Song Details:");
            System.out.print("New File Path (leave blank to keep current): ");
            String newFilePath = scanner.nextLine().trim();
            if (newFilePath.isEmpty()) {
                newFilePath = song.getFilePath();
            }

            System.out.print("New Title (leave blank to keep current): ");
            String newTitle = scanner.nextLine().trim();
            if (newTitle.isEmpty()) {
                newTitle = song.getTitle();
            }

            System.out.print("New Artist (leave blank to keep current): ");
            String newArtist = scanner.nextLine().trim();
            if (newArtist.isEmpty()) {
                newArtist = song.getArtist();
            }

            System.out.print("New Album (leave blank to keep current): ");
            String newAlbum = scanner.nextLine().trim();
            if (newAlbum.isEmpty()) {
                newAlbum = song.getAlbum();
            }

            System.out.print("New Genre (leave blank to keep current): ");
            String newGenre = scanner.nextLine().trim();
            if (newGenre.isEmpty()) {
                newGenre = song.getGenre();
            }

            System.out.print("New Year (leave blank to keep current): ");
            String newYear = scanner.nextLine().trim();
            if (newYear.isEmpty()) {
                newYear = song.getYear();
            }

            System.out.print("New Composers (leave blank to keep current): ");
            String newComposers = scanner.nextLine().trim();
            if (newComposers.isEmpty()) {
                newComposers = song.getComposers();
            }

            System.out.print("New Lyrics File (leave blank to keep current): ");
            String newLyricsFile = scanner.nextLine().trim();
            if (newLyricsFile.isEmpty()) {
                newLyricsFile = song.getLyricsFile();
            }

            System.out.print("New Track Number (leave blank to keep current): ");
            String newTrackNumberInput = scanner.nextLine().trim();
            int newTrackNumber = newTrackNumberInput.isEmpty() ? song.getTrackNumber() : Integer.parseInt(newTrackNumberInput);

            System.out.print("New Disc Number (leave blank to keep current): ");
            String newDiscNumberInput = scanner.nextLine().trim();
            int newDiscNumber = newDiscNumberInput.isEmpty() ? song.getDiscNumber() : Integer.parseInt(newDiscNumberInput);

            PreparedStatement statement = connection.prepareStatement(UPDATE_SONG_SQL);
            statement.setString(1, newFilePath);
            statement.setString(2, newTitle);
            statement.setString(3, newArtist);
            statement.setString(4, newAlbum);
            statement.setString(5, newGenre);
            statement.setString(6, newYear);
            statement.setString(7, newComposers);
            statement.setString(8, newLyricsFile);
            statement.setInt(9, newTrackNumber);
            statement.setInt(10, newDiscNumber);
            statement.setInt(11, song.getId());
            statement.executeUpdate();

            System.out.println("Song details updated successfully.");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private static boolean databaseExists() {
        try {
            Connection connection = DriverManager.getConnection(DB_URL);
            connection.close();
            return true;
        } catch (SQLException e) {
            return false;
        }
    }

    public static void main(String[] args) {
        initializeDatabase();

        if (!databaseExists()) {
            populateDatabaseFromDirectory();
        }

        boolean exit = false;
        while (!exit) {
            System.out.println("\nMusic Library");
            System.out.println("1. View All Songs");
            System.out.println("2. Search Songs");
            System.out.println("3. Delete and Recreate Database");
            System.out.println("4. Edit Song Details");
            System.out.println("5. Exit");
            System.out.print("Enter your choice: ");
            int choice = scanner.nextInt();
            scanner.nextLine(); // Consume newline

            switch (choice) {
                case 1:
                    viewAllSongs();
                    break;
                case 2:
                    searchSongs();
                    break;
                case 3:
                    deleteAndRecreateDatabase();
                    break;
                case 4:
                    editSongDetails();
                    break;
                case 5:
                    exit = true;
                    break;
                default:
                    System.out.println("Invalid choice. Try again.");
            }
        }
    }

    private static void editSongDetails() {
        System.out.print("Enter the song ID to edit (or 'q' to quit): ");
        String input = scanner.nextLine();
        if (input.equalsIgnoreCase("q")) {
            return;
        }
        try {
            int songId = Integer.parseInt(input);
            Entity song = getSongById(songId);
            if (song != null) {
                editSongDetails(song);
            } else {
                System.out.println("Song not found.");
            }
        } catch (NumberFormatException e) {
            System.out.println("Invalid input. Please enter a number or 'q' to quit.");
        }
    }

    private static Entity getSongById(int songId) {
        try {
            PreparedStatement statement = connection.prepareStatement("SELECT * FROM songs WHERE id = ?");
            statement.setInt(1, songId);
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                int id = resultSet.getInt("id");
                String filePath = resultSet.getString("filePath");
                String title = resultSet.getString("title");
                String artist = resultSet.getString("artist");
                String album = resultSet.getString("album");
                String genre = resultSet.getString("genre");
                String year = resultSet.getString("year");
                String composers = resultSet.getString("composers");
                String lyricsFile = resultSet.getString("lyricsFile");
                int trackNumber = resultSet.getInt("trackNumber");
                int discNumber = resultSet.getInt("discNumber");
                return new Entity(id, filePath, title, artist, album, genre, year, composers, lyricsFile, trackNumber, discNumber, null);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    private static void viewAllSongs() {
        List<Entity> songs = getAllSongs();
        if (songs.isEmpty()) {
            System.out.println("No songs found in the library.");
        } else {
            System.out.println("\nAll Songs:");
            for (int i = 0; i < songs.size(); i++) {
                Entity song = songs.get(i);
                System.out.println((i + 1) + ". " + song.getTitle() + " - " + song.getArtist());
            }
            selectSong(songs);
        }
    }

    private static void searchSongs() {
        System.out.print("Enter search query: ");
        String query = scanner.nextLine();
        List<Entity> songs = searchSongs(query);
        if (songs.isEmpty()) {
            System.out.println("No songs found for the search query: " + query);
        } else {
            System.out.println("\nSearch Results:");
            for (int i = 0; i < songs.size(); i++) {
                Entity song = songs.get(i);
                System.out.println((i + 1) + ". " + song.getTitle() + " - " + song.getArtist());
            }
            selectSong(songs);
        }
    }

    private static void selectSong(List<Entity> songs) {
        System.out.print("\nEnter the number of the song to view details (or 'q' to quit): ");
        String input = scanner.nextLine();
        if (input.equalsIgnoreCase("q")) {
            return;
        }
        try {
            int songIndex = Integer.parseInt(input) - 1;
            if (songIndex >= 0 && songIndex < songs.size()) {
                Entity song = songs.get(songIndex);
                displaySongDetails(song);
            } else {
                System.out.println("Invalid song number.");
            }
        } catch (NumberFormatException e) {
            System.out.println("Invalid input. Please enter a number or 'q' to quit.");
        }
    }

    private static void displaySongDetails(Entity song) {
        System.out.println("\nSong Details:");
        System.out.println("Title: " + song.getTitle());
        System.out.println("Artist: " + song.getArtist());
        System.out.println("Album: " + song.getAlbum());
        System.out.println("Genre: " + song.getGenre());
        System.out.println("Year: " + song.getYear());
        System.out.println("Composers: " + song.getComposers());
        System.out.println("Track Number: " + song.getTrackNumber());
        System.out.println("Disc Number: " + song.getDiscNumber());
        System.out.print("\nPress 'm' to view lyrics (if available), or any other key to go back: ");
        String input = scanner.nextLine();
        if (input.equalsIgnoreCase("m")) {
            displayLyrics(song.getLyricsFile());
        }
    }

    private static void displayLyrics(String lyricsFile) {
        try {
            System.out.println("\nLyrics:");
            LyricsView.fileReader(lyricsFile);
        } catch (Exception e) {
            System.out.println("An error occurred while reading the lyrics file.");
            e.printStackTrace();
        }
    }

    private static boolean songExists(String filePath) {
        try {
            PreparedStatement statement = connection.prepareStatement("SELECT 1 FROM songs WHERE filePath = ?");
            statement.setString(1, filePath);
            ResultSet resultSet = statement.executeQuery();
            return resultSet.next();
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    private static void populateDatabaseFromDirectory() {
        try (Stream<Path> paths = Files.walk(Paths.get(MUSIC_DIRECTORY))) {
            List<Path> musicFiles = paths.filter(Files::isRegularFile)
                    .filter(path -> path.toString().endsWith(".mp3") || path.toString().endsWith(".flac") || path.toString().endsWith(".wav"))
                    .collect(Collectors.toList());

            for (Path musicFile : musicFiles) {
                String filePath = musicFile.toString();
                if (filePath.endsWith(".wav") || filePath.endsWith(".WAV")) {
                    insertWavFile(filePath);
                } else if (!songExists(filePath)) {
                    audioMetadata(filePath);
                } else {
                    System.out.println("Song already exists in the database: " + filePath);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void deleteAndRecreateDatabase() {
        Scanner scan = new Scanner(System.in);
        System.out.println("Are you sure you want to delete and recreate the database? (y/n)");
        String choice = scan.nextLine();

        switch (choice) {
            case "y":
                break;
            case "n":
                System.out.println("Database was not created!");
                return;
            default:
                System.out.println("Invalid choice. Please enter 'y' or 'n'.");
                return;
        }

        try {
            connection.close();
            File dbFile = new File("music.db");
            dbFile.delete();
            initializeDatabase();
            populateDatabaseFromDirectory();
            System.out.println("Database has been recreated successfully.");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private static void insertWavFile(String filePath) {
        String title = new File(filePath).getName().replaceFirst("\\.wav$", "").replaceFirst("\\.WAV$", "");
        try {
            PreparedStatement statement = connection.prepareStatement(INSERT_SONG_SQL);
            statement.setString(1, filePath);
            statement.setString(2, title);
            statement.setString(3, "");
            statement.setString(4, "");
            statement.setString(5, "");
            statement.setString(6, "");
            statement.setString(7, "");
            statement.setString(8, "");
            statement.setInt(9, 0);
            statement.setInt(10, 0);
            statement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void audioMetadata(String filePath) {
        try {
            File file = new File(filePath);
            AudioFile audioFile = AudioFileIO.read(file);
            Tag tag = audioFile.getTag();
            if (filePath.endsWith(".wav") || filePath.endsWith(".WAV")) {
                wavChunkInfo(filePath);
            } else if (filePath.endsWith(".mp3") || filePath.endsWith(".flac")) {
                jAudioMetadata(filePath, audioFile, tag);
            } else {
                System.out.println("File not supported!");
                return;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void jAudioMetadata(String filePath, AudioFile audioFile, Tag tag) {
        try {
            if (tag != null) {
                int id = 0;
                String title = tag.getFirst(FieldKey.TITLE);
                String artist = tag.getFirst(FieldKey.ARTIST);
                String album = tag.getFirst(FieldKey.ALBUM);
                String genre = tag.getFirst(FieldKey.GENRE);
                String year = tag.getFirst(FieldKey.YEAR);
                String composers = tag.getFirst(FieldKey.COMPOSER);
                String trackNumber = tag.getFirst(FieldKey.TRACK);
                String discNumber = tag.getFirst(FieldKey.DISC_NO);
                String lyrics = tag.getFirst(FieldKey.LYRICS);
                String albumArt = tag.getFirst(FieldKey.COVER_ART);

                AudioHeader audioHeader = audioFile.getAudioHeader();
                String bitRate = audioHeader.getBitRate();
                String sampleRate = audioHeader.getSampleRate();
                long fileSize = new File(filePath).length();

                String lyricsFile = LYRICS_DIRECTORY + filePath.substring(filePath.lastIndexOf('/') + 1, filePath.lastIndexOf('.')) + ".txt";

//                Entity song = new Entity(id, filePath, title, artist, album, genre, year, composers, lyricsFile, trackNumber != null ? Integer.parseInt(trackNumber) : 0, discNumber != null ?, albumArt != null ? Integer.parseInt(discNumber) : 0);
//                insertSong(song);
            } else {
                System.out.println("Failed to read metadata from " + filePath);
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
            // Check for RIFF signature (4 bytes)
            if (data[0] != 'R' || data[1] != 'I' || data[2] != 'F' || data[3] != 'F') {
                throw new IOException("Not a WAV file");
            }
            // Skip chunk size (4 bytes)
            int chunkStartPosition = 8;
            while (chunkStartPosition < data.length - 8) {
                // Print Chunk ID and Size
                String chunkId = new String(data, chunkStartPosition, 4);
                int chunkSize = (data[chunkStartPosition + 4] << 24) + (data[chunkStartPosition + 5] << 16) + (data[chunkStartPosition + 6] << 8) + data[chunkStartPosition + 7];
                // Update chunk start position for next iteration
                chunkStartPosition += 8 + chunkSize;
            }
        } catch (IOException e) {
            System.err.println("Error reading file: " + e.getMessage());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}