package xyz.yeems214.MusicPlayer.Interfaces;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.*;

import org.apache.commons.io.IOUtils;
import org.jaudiotagger.audio.AudioFile;
import org.jaudiotagger.audio.AudioFileIO;
import org.jaudiotagger.audio.exceptions.CannotReadException;
import org.jaudiotagger.audio.exceptions.InvalidAudioFrameException;
import org.jaudiotagger.audio.exceptions.ReadOnlyFileException;
import org.jaudiotagger.tag.FieldKey;
import org.jaudiotagger.tag.Tag;
import org.jaudiotagger.tag.TagException;
import org.jaudiotagger.tag.datatype.Artwork;
import xyz.yeems214.MusicPlayer.Main;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Scanner;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.jaudiotagger.audio.AudioHeader;
import xyz.yeems214.MusicPlayer.Entity;
import xyz.yeems214.MusicPlayer.Extensions.AlbumArtViewer;

import static org.fusesource.jansi.AnsiConsole.getTerminalWidth;
import static xyz.yeems214.MusicPlayer.Main.clearConsole;
import static xyz.yeems214.MusicPlayer.Main.refreshConsole;

// This class is responsible for managing the music library database and file system.
// It allows users to view, search, edit, and update song details in the database.
// This uses SQLite to store song details and file paths. It also allows users to add lyrics and album art to songs.
// This is mainly designed to cater the difficulty of storing metadata for WAV songs.

public class FileManager {
    private static boolean databaseUpdated = false;
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
            "discNumber INTEGER NOT NULL," +
            "albumArt TEXT NOT NULL" +
            ")";

    private static final String INSERT_SONG_SQL = "INSERT INTO songs (filePath, title, artist, album, genre, year, composers, lyricsFile, trackNumber, discNumber, albumArt) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
    private static final String SELECT_ALL_SONGS_SQL = "SELECT * FROM songs";
    private static final String SEARCH_SONGS_SQL = "SELECT * FROM songs WHERE title LIKE ? OR artist LIKE ? OR album LIKE ? OR genre LIKE ?";
    private static final String UPDATE_SONG_SQL = "UPDATE songs SET filePath = ?, title = ?, artist = ?, album = ?, genre = ?, year = ?, composers = ?, lyricsFile = ?, trackNumber = ?, discNumber = ?, albumArt = ? WHERE id = ?";

    private static Connection connection;
    private static Scanner scan = new Scanner(System.in);
    private static final String MUSIC_DIRECTORY = "src/main/resources/music-files/";
    private static final String LYRICS_DIRECTORY = "src/main/resources/lyrics-files/";
    private static final String ALBUMART_DIRECTORY = "src/main/resources/albumart-files/";
    public static void initializeDatabase() {
        try {
            connection = DriverManager.getConnection(DB_URL);
            Statement statement = connection.createStatement();
            statement.execute(CREATE_TABLE_SQL);
            statement.execute("PRAGMA busy_timeout = 3000");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void shutDownDatabase() {
        try {
            connection.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void uninitializeDatabase() {
        try {
            connection.close();
            connection = DriverManager.getConnection(DB_URL);
            Statement statement = connection.createStatement();
            statement.execute("DROP TABLE IF EXISTS songs");
            statement.execute(CREATE_TABLE_SQL);
            System.out.println("Database uninitialized successfully.");
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
            statement.setString(11, song.getAlbumArt());
            statement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    static List<Entity> getAllSongs() {
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
                String albumArt = resultSet.getString("albumArt");
                Entity song = new Entity(id, filePath, title, artist, album, genre, year, composers, lyricsFile, trackNumber, discNumber, albumArt);
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
                String albumArt = resultSet.getString("albumArt");
                Entity song = new Entity(id, filePath, title, artist, album, genre, year, composers, lyricsFile, trackNumber, discNumber, albumArt);
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
            System.out.print("New File Path (leave blank for no change): ");
            String newFilePath = scan.nextLine().trim();
            if (newFilePath.isEmpty()) {
                newFilePath = song.getFilePath();
            }

            System.out.print("New Title (leave blank for no change): ");
            String newTitle = scan.nextLine().trim();
            if (newTitle.isEmpty()) {
                newTitle = song.getTitle();
            }

            System.out.print("New Artist (leave blank for no change): ");
            String newArtist = scan.nextLine().trim();
            if (newArtist.isEmpty()) {
                newArtist = song.getArtist();
            }

            System.out.print("New Album (leave blank for no change): ");
            String newAlbum = scan.nextLine().trim();
            if (newAlbum.isEmpty()) {
                newAlbum = song.getAlbum();
            }

            System.out.print("New Genre (leave blank for no change): ");
            String newGenre = scan.nextLine().trim();
            if (newGenre.isEmpty()) {
                newGenre = song.getGenre();
            }

            System.out.print("New Year (leave blank for no change): ");
            String newYear = scan.nextLine().trim();
            if (newYear.isEmpty()) {
                newYear = song.getYear();
            }

            System.out.print("New Composers (leave blank for no change): ");
            String newComposers = scan.nextLine().trim();
            if (newComposers.isEmpty()) {
                newComposers = song.getComposers();
            }

            System.out.print("New Lyrics File (leave blank for no change): ");
            String newLyricsFile = scan.nextLine().trim();
            if (newLyricsFile.isEmpty()) {
                newLyricsFile = song.getLyricsFile();
            }

            System.out.print("New Track Number (leave blank for no change): ");
            String newTrackNumberInput = scan.nextLine().trim();
            int newTrackNumber = newTrackNumberInput.isEmpty() ? song.getTrackNumber() : Integer.parseInt(newTrackNumberInput);

            System.out.print("New Disc Number (leave blank for no change): ");
            String newDiscNumberInput = scan.nextLine().trim();
            int newDiscNumber = newDiscNumberInput.isEmpty() ? song.getDiscNumber() : Integer.parseInt(newDiscNumberInput);

            System.out.print("New Album Art (leave blank for no change): ");
            String newAlbumArt = scan.nextLine().trim();
            if (newAlbumArt.isEmpty()) {
                newAlbumArt = song.getAlbumArt();
            }

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
            statement.setString(11, newAlbumArt);
            statement.setInt(12, song.getId());
            statement.executeUpdate();

            System.out.println("Song details updated successfully.");
            System.out.println("Press any key to continue.");
            String choice2 = scan.nextLine();
            switch (choice2) {
                default:
                    clearConsole();
                    FileMenu();
                    break;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (Exception e) {
            throw new RuntimeException(e);
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

    public static void FileMenu() throws Exception {
        try {
            if (!databaseExists()) {
                populateDatabaseFromDirectory();
            }

            boolean exit = false;
            while (!exit) {
                String header = "Music Library";
                int padding = (getTerminalWidth() - header.length()) / 2;
                System.out.printf("%" + padding + "s\033[1;33m%s\033[0m\n\n", "", header);

                String[] options = {
                        "[1] - View All Songs",
                        "[2] - Search Songs",
                        "[3] - Delete and Recreate Database",
                        "[4] - Edit Song Details",
                        "[5] - Add Song Lyrics",
                        "[6] - Add Album Art to Song",
                        "[7] - Update Database",
                        "[8] - Go back to Main Menu"
                };

                for (String option : options) {
                    padding = (getTerminalWidth() - option.length()) / 2;
                    System.out.printf("%" + padding + "s\033[1m%s\033[0m\n", "", option);
                }

                int choice = -1;
                while (choice == -1) {
                    try {
                        choice = scan.nextInt();
                        scan.nextLine();
                    } catch (InputMismatchException e) {
                        System.out.println("Invalid input. Please enter a number.");
                        scan.nextLine();
                    }
                }

                switch (choice) {
                    case 1:
                        viewAllSongs();
                        break;
                    case 2:
                        searchSongs();
                        break;
                    case 3:
                        clearConsole();
                        deleteAndRecreateDatabase();
                        break;
                    case 4:
                        clearConsole();
                        editSongDetails();
                        break;
                    case 5:
                        clearConsole();
                        addLyricsToSong();
                        break;
                    case 6:
                        clearConsole();
                        addAlbumArtToSong();
                        break;
                    case 7:
                        clearConsole();
                        updateDatabase();
                        break;
                    case 8:
                        Main.mainMenu();
                        break;
                    case 9:
                        uninitializeDatabase();
                        Main.mainMenu();
                        break;
                    case 0:
                        clearConsole();
                        refreshConsole();
                        break;
                    default:
                        System.out.println("Invalid choice. Try again.");
                        break;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("An error has occured!" + e);
            System.out.println("App is restarting!");
            Main.mainMenu();
        }
    }

    private static void addLyricsToSong() {
        try {
            onlyViewAllSongs();
            System.out.print("Enter the song ID to add lyrics (or 'q' to quit): ");
            String songId = scan.nextLine();
            if (songId.equalsIgnoreCase("q")) {
                return;
            }
            Entity song = getSongById(Integer.parseInt(songId));
            if (song != null) {
                clearConsole();
                System.out.print("Enter your lyrics (leave blank for no change): ");
                String lyrics = scan.nextLine().trim();
                String newLyricsFile = song.getLyricsFile();
                if (!lyrics.isEmpty()) {
                    File lyricsFile = new File(lyrics);
                    if (lyricsFile.exists() && !lyricsFile.isDirectory()) {
                        String fileContents;
                        try (Scanner fileScanner = new Scanner(lyricsFile)) {
                            if (fileScanner.hasNext()) {
                                fileContents = fileScanner.useDelimiter("\\A").next();
                            } else {
                                fileContents = "";
                                System.out.println("File is blank!");
                                addLyricsToSong();
                            }
                        }

                        // Write the contents to a new file in the lyrics-files directory
                        String lyricsFileName = "src/main/resources/lyrics-files/" + song.getArtist() + " - " + song.getTitle() + ".txt";
                        try (PrintWriter out = new PrintWriter(lyricsFileName)) {
                            out.println(fileContents);
                        }
                        newLyricsFile = lyricsFileName;

                        System.out.println("Lyrics added successfully.");
                    }
                }
                PreparedStatement statement = connection.prepareStatement(UPDATE_SONG_SQL);
                statement.setString(1, song.getFilePath());
                statement.setString(2, song.getTitle());
                statement.setString(3, song.getArtist());
                statement.setString(4, song.getAlbum());
                statement.setString(5, song.getGenre());
                statement.setString(6, song.getYear());
                statement.setString(7, song.getComposers());
                statement.setString(8, newLyricsFile);
                statement.setInt(9, song.getTrackNumber());
                statement.setInt(10, song.getDiscNumber());
                statement.setString(11, song.getAlbumArt());
                statement.setInt(12, song.getId());
                statement.executeUpdate();

                System.out.println("Press any key to continue.");
                String choice2 = scan.nextLine();

                switch (choice2) {
                    default:
                        clearConsole();
                        FileMenu();
                        break;
                }
            } else {
                System.out.println("Song not found.");
            }
        } catch (NumberFormatException e) {
            System.out.println("An error occurred: " + e.getMessage());
        } catch (SQLException | FileNotFoundException e) {
            throw new RuntimeException(e);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private static void addAlbumArtToSong() {
        try {
            onlyViewAllSongs();
            System.out.print("Enter the song ID to add album art (or 'q' to quit): ");
            String songId = scan.nextLine();
            if (songId.equalsIgnoreCase("q")) {
                return;
            }
            Entity song = getSongById(Integer.parseInt(songId));
            if (song != null) {
                clearConsole();
                System.out.print("Enter the path to your album art (leave blank for no change): ");
                String albumArtPath = scan.nextLine().trim();
                String newAlbumArtFile = song.getAlbumArt();
                if (!albumArtPath.endsWith(".jpg") && !albumArtPath.endsWith(".jpeg") && !albumArtPath.endsWith(".png")) {
                    clearConsole();
                    System.out.println("Invalid file type. Please enter a .jpg, .jpeg, or .png file.");
                    System.out.println("Press any key to go back.");
                    String choice = scan.nextLine();
                    switch (choice) {
                        default:
                            clearConsole();
                            return;
                    }
                }
                else if (!albumArtPath.isEmpty()) {
                    File albumArtFile = new File(albumArtPath);
                    if (albumArtFile.exists() && !albumArtFile.isDirectory()) {
                        // Read the contents of the file
                        byte[] fileContents;
                        try (InputStream is = new FileInputStream(albumArtFile)) {
                            fileContents = IOUtils.toByteArray(is);
                        }

                        // Write the contents to a new file in the albumart-files directory
                        String artist = song.getArtist();
                        String title = song.getTitle();
                        String albumArtFileName;
                        if (artist != null && !artist.isEmpty() && albumArtPath.endsWith(".png") || albumArtPath.endsWith(".PNG")) {
                            albumArtFileName = ALBUMART_DIRECTORY + artist + " - " + title + ".png";
                        } else if ( artist != null && !artist.isEmpty() && albumArtPath.endsWith(".jpeg") || albumArtPath.endsWith(".JPEG") || albumArtPath.endsWith(".jpg") || albumArtPath.endsWith(".JPG")){
                            albumArtFileName = ALBUMART_DIRECTORY + artist + " - " + title + ".jpeg";
                            albumArtFileName = ALBUMART_DIRECTORY + artist + " - " + title + ".jpg";
                        } else {
                            System.out.println("File not supported!");
                            return;
                        }

                        try (FileOutputStream fos = new FileOutputStream(albumArtFileName)) {
                            fos.write(fileContents);
                        }
                        newAlbumArtFile = albumArtFileName;
                    }
                }
                PreparedStatement statement = connection.prepareStatement(UPDATE_SONG_SQL);
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
                statement.setString(11, newAlbumArtFile);
                statement.setInt(12, song.getId());
                statement.executeUpdate();

                System.out.println("Album art added successfully.");
                System.out.println("Press any key to continue.");
                String choice2 = scan.nextLine();

                switch (choice2) {
                    default:
                        clearConsole();
                        FileMenu();
                        break;
                }
            } else {
                System.out.println("Song not found.");
            }
        } catch (NumberFormatException | IOException | SQLException e) {
            System.out.println("An error occurred: " + e.getMessage());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private static void editSongDetails() {
        onlyViewAllSongs();
        System.out.print("Enter the song ID to edit (or 'q' to quit): ");
        String input = scan.nextLine();
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

    static Entity getSongById(int songId) {
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
                String albumArt = resultSet.getString("albumArt");

                return new Entity(id, filePath, title, artist, album, genre, year, composers, lyricsFile, trackNumber, discNumber, albumArt);
            } else {
                System.out.println("Song not found in the database.");
                return null;
            }
        } catch (SQLException e) {
            System.out.println("An error occurred: " + e.getMessage());
            return null;
        }
    }

    static void viewAllSongs() throws Exception {
        clearConsole();
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

    private static void onlyViewAllSongs() {
        List<Entity> songs = getAllSongs();
        System.out.println("\nAll Songs:");
        for (int i = 0; i < songs.size(); i++) {
            Entity song = songs.get(i);
            System.out.println((i + 1) + ". " + song.getTitle() + " - " + song.getArtist());
        }
    }

    private static void searchSongs() throws Exception {
        clearConsole();
        System.out.println("Search Songs\n");
        onlyViewAllSongs();
        System.out.print("Enter search query: ");
        String query = scan.nextLine();
        List<Entity> songs = searchSongs(query);
        if (songs.isEmpty()) {
            System.out.println("No songs found for the search query: " + query);
            searchSongs();
        } else {
            clearConsole();
            System.out.println("\nSearch Results:");
            for (int i = 0; i < songs.size(); i++) {
                Entity song = songs.get(i);
                System.out.println((i + 1) + ". " + song.getTitle() + " - " + song.getArtist());
            }
            selectSong(songs);
        }
    }

    private static void selectSong(List<Entity> songs) throws Exception {
        System.out.print("Enter the number of the song to view details (or 'q' to quit): ");
        String input = scan.nextLine();
        if (input.equalsIgnoreCase("q")) {
            clearConsole();
            return;
        }
        try {
            int songIndex = Integer.parseInt(input) - 1;
            if (songIndex >= 0 && songIndex < songs.size()) {
                Entity song = songs.get(songIndex);
                displaySongDetails(song);
            } else {
                System.out.println("Invalid song number.");
                return;
            }
        } catch (NumberFormatException e) {
            System.out.println("Invalid input. Please enter a number or 'q' to quit.");
        } catch (CannotReadException e) {
            throw new RuntimeException(e);
        } catch (TagException e) {
            throw new RuntimeException(e);
        } catch (InvalidAudioFrameException e) {
            throw new RuntimeException(e);
        } catch (ReadOnlyFileException e) {
            throw new RuntimeException(e);
        } catch (Exception e) {
            System.out.println("An error occurred! We apologize for the inconvenience!");
            System.out.println("\nNow restarting the application!");
            clearConsole();
            Main.mainMenu();
        }
    }

    private static void displaySongDetails(Entity song) throws Exception {
        clearConsole();
        int id = song.getId();
        String filePath = song.getFilePath();
        System.out.println("\nSong Details:");
        System.out.println("Title: " + song.getTitle());
        System.out.println("Artist: " + song.getArtist());
        System.out.println("Album: " + song.getAlbum());
        System.out.println("Genre: " + song.getGenre());
        System.out.println("Year: " + song.getYear());
        System.out.println("Composers: " + song.getComposers());
        System.out.println("Track Number: " + song.getTrackNumber());
        System.out.println("Disc Number: " + song.getDiscNumber());
        System.out.println("Lyrics File Address: " + song.getLyricsFile());
        System.out.println("Album Art Address: " + song.getAlbumArt());
        System.out.print("\n Press '1' to Play the song, '2' to view lyrics (if available), '3' to view Album Art, or any other key to go back: ");
        String input = scan.nextLine();
        switch (input) {
            case "1":
                clearConsole();
                NowPlaying.Player(song.getFilePath(), song.getLyricsFile(), song.getTitle(), song.getArtist(), song.getAlbum(), song.getAlbumArt());
                break;
            case "2":
                clearConsole();
                displayLyrics(song.getLyricsFile(), song);
                break;
            case "3":
                clearConsole();
                AlbumArtViewer.deez(song.getFilePath(), song.getAlbumArt());
                displaySongDetails(song);
                break;
            default:
                clearConsole();
        }
    }

    private static void displayLyrics(String lyricsFile, Entity song) {
        try {
            if (lyricsFile.endsWith(".txt")) {
                LyricsView.fileReader(lyricsFile);
            } else {
                System.out.println("Lyrics file not found.");
                displaySongDetails(song);
            }
        } catch (Exception e) {
            System.out.println(lyricsFile);
            System.out.println("An error occurred while reading the lyrics file.");
            e.printStackTrace();
        }
    }

    private static boolean songExists(String filePath) throws Exception {
        try {
            PreparedStatement statement = connection.prepareStatement("SELECT 1 FROM songs WHERE filePath = ?");
            statement.setString(1, filePath);
            ResultSet resultSet = statement.executeQuery();
            return resultSet.next();
        } catch (SQLException e) {
            System.out.println("An error occurred! We apologize for the inconvenience!");
            System.out.println("\nNow restarting the application!");
            clearConsole();
            Main.mainMenu();
            return false;
        }
    }

    private static void populateDatabaseFromDirectory() throws Exception {
        try (Stream<Path> paths = Files.walk(Paths.get(MUSIC_DIRECTORY))) {
            List<Path> musicFiles = paths.filter(Files::isRegularFile)
                    .filter(path -> path.toString().endsWith(".mp3") || path.toString().endsWith(".flac") || path.toString().endsWith(".wav"))
                    .collect(Collectors.toList());
            clearDatabase();
            for (Path musicFile : musicFiles) {
                String filePath = musicFile.toString();
                if (filePath.endsWith(".wav") || filePath.endsWith(".WAV")) {
                    insertWavFile(filePath);
                } else {
                    audioMetadata(filePath);
                }
            }
        } catch (IOException e) {
            System.out.println("An error occurred while reading the music files.");
            return;
        } catch (Exception e) {
            System.out.println("An error occurred! We apologize for the inconvenience!");
            System.out.println("\nNow restarting the application!");
            clearConsole();
            Main.mainMenu();
        }
    }

    private static void clearDatabase() {
        try {
            Statement statement = connection.createStatement();
            statement.execute("DELETE FROM songs");
        } catch (SQLException e) {
            System.out.println("An error occurred while clearing the database.");
        }
    }

    private static void deleteAndRecreateDatabase() throws Exception {
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

            Statement statement = connection.createStatement();
            statement.executeUpdate("UPDATE sqlite_sequence SET seq = 0 WHERE name = 'songs';");

            System.out.println("Database has been recreated successfully.");
            System.out.println("Press any key to continue.");
            String choice2 = scan.nextLine();
            switch (choice2) {
                default:
                    clearConsole();
                    FileMenu();
                    break;
            }
        } catch (SQLException e) {
            System.out.println("An error occurred while deleting and recreating the database.");
            return;
        } catch (Exception e) {
            System.out.println("An error occurred! We apologize for the inconvenience!");
            System.out.println("\nNow restarting the application!");
            clearConsole();
            Main.mainMenu();
        }
    }

    public synchronized static void updateDatabase() throws Exception {
        boolean anyUpdates = false;

        try (Stream<Path> paths = Files.walk(Paths.get(MUSIC_DIRECTORY))) {
            List<Path> musicFiles = paths.filter(Files::isRegularFile)
                    .filter(path -> path.toString().endsWith(".mp3") || path.toString().endsWith(".flac") || path.toString().endsWith(".wav"))
                    .collect(Collectors.toList());

            for (Path musicFile : musicFiles) {
                String filePath = musicFile.toString();
                try {
                    if (!songExists(filePath)) {
                        if (filePath.endsWith(".wav") || filePath.endsWith(".WAV")) {
                            insertWavFile(filePath);
                        } else {
                            audioMetadata(filePath);
                        }
                    } else {
                        updateMetadata(filePath);
                        anyUpdates = true;
                    }
                } catch (Exception e) {
                    System.out.println("Database update failed");
                }
            }

            if (!anyUpdates) {
                for (Path musicFile : musicFiles) {
                    String filePath = musicFile.toString();
                    if (filePath.endsWith(".wav") || filePath.endsWith(".WAV")) {
                        insertWavFile(filePath);
                    } else {
                        audioMetadata(filePath);
                    }
                }
                System.out.println("All songs inserted as new.");
            } else {
                System.out.println("Database updated successfully.");
            }

            System.out.println("Press any key to continue.");
            String choice2 = scan.nextLine();
            switch (choice2) {
                default:
                    clearConsole();
                    FileMenu();
                    break;
            }
        } catch (IOException e) {
            System.out.println("An error occurred while reading the music files.");
        }
    }



    private static void updateWavFile(String filePath) {
        String title = new File(filePath).getName().replaceFirst("\\.wav$", "").replaceFirst("\\.WAV$", "");
        try {
            PreparedStatement statement = connection.prepareStatement("UPDATE songs SET title = ? WHERE filePath = ?");
            statement.setString(1, title);
            statement.setString(2, filePath);
            statement.executeUpdate();
        } catch (SQLException e) {
            System.out.println("An error occurred while updating the database.");
        }
    }

    private static void updateMetadata(String filePath) throws Exception {
        try {
            if (filePath.toLowerCase().endsWith(".wav")) {
                updateWavFile(filePath);
                return;
            } else if (filePath.toLowerCase().endsWith(".flac") || filePath.toLowerCase().endsWith(".mp3")) {
                File file = new File(filePath);
                AudioFile audioFile = AudioFileIO.read(file);
                Tag tag = audioFile.getTag();

                if (songExists(filePath)) {
                    // Update existing song
                    if (tag != null) {
                        String title = tag.getFirst(FieldKey.TITLE);
                        String artist = tag.getFirst(FieldKey.ARTIST);
                        String album = tag.getFirst(FieldKey.ALBUM);
                        String genre = tag.getFirst(FieldKey.GENRE);
                        String year = tag.getFirst(FieldKey.YEAR);
                        String composers = tag.getFirst(FieldKey.COMPOSER);
                        String lyrics = tag.getFirst(FieldKey.LYRICS);
                        String lyricsFile = LYRICS_DIRECTORY;

                        // Extract track and disc numbers + handling empty values
                        String trackNumber = tag.getFirst(FieldKey.TRACK);
                        int trackNum = (trackNumber != null && !trackNumber.isEmpty()) ? Integer.parseInt(trackNumber) : 0;

                        String discNumber = tag.getFirst(FieldKey.DISC_NO);
                        int discNum = (discNumber != null && !discNumber.isEmpty()) ? Integer.parseInt(discNumber) : 0;

                        Artwork artwork = tag.getFirstArtwork();
                        String albumArtFile = null;

                        if (artwork != null) {
                            byte[] artworkData = artwork.getBinaryData();
                            albumArtFile = ALBUMART_DIRECTORY + filePath.substring(filePath.lastIndexOf('/') + 1, filePath.lastIndexOf('.')) + ".jpg";
                            try (FileOutputStream fos = new FileOutputStream(albumArtFile)) {
                                fos.write(artworkData);
                            }
                        } else {

                        }

                        PreparedStatement statement = connection.prepareStatement("UPDATE songs SET title = ?, artist = ?, album = ?, genre = ?, year = ?, composers = ?, lyricsFile = ?, trackNumber = ?, discNumber = ?, albumArt = ? WHERE filePath = ?");
                        statement.setString(1, title);
                        statement.setString(2, artist);
                        statement.setString(3, album);
                        statement.setString(4, genre);
                        statement.setString(5, year);
                        statement.setString(6, composers);
                        statement.setString(7, lyricsFile);
                        statement.setInt(8, trackNum);
                        statement.setInt(9, discNum);
                        statement.setString(10, albumArtFile != null ? albumArtFile : "");
                        statement.setString(11, filePath);
                        statement.executeUpdate();
                    } else {
                        System.out.println("Failed to read metadata from " + filePath);
                    }
                } else {
                    // Insert new song
                    if (tag != null) {
                        String title = tag.getFirst(FieldKey.TITLE);
                        String artist = tag.getFirst(FieldKey.ARTIST);
                        String album = tag.getFirst(FieldKey.ALBUM);
                        String genre = tag.getFirst(FieldKey.GENRE);
                        String year = tag.getFirst(FieldKey.YEAR);
                        String composers = tag.getFirst(FieldKey.COMPOSER);
                        String trackNumber = tag.getFirst(FieldKey.TRACK);
                        String discNumber = tag.getFirst(FieldKey.DISC_NO);
                        String lyrics = tag.getFirst(FieldKey.LYRICS);
                        String lyricsFile = LYRICS_DIRECTORY;

                        // Extract album art then save it to the albumart-files directory
                        Artwork artwork = tag.getFirstArtwork();
                        String albumArtFile = null;
                        if (artwork != null) {
                            byte[] artworkData = artwork.getBinaryData();
                            albumArtFile = ALBUMART_DIRECTORY + filePath.substring(filePath.lastIndexOf('/') + 1, filePath.lastIndexOf('.')) + ".jpg";
                            try (FileOutputStream fos = new FileOutputStream(albumArtFile)) {
                                fos.write(artworkData);
                            }
                        }

                        PreparedStatement statement = connection.prepareStatement(INSERT_SONG_SQL);
                        statement.setString(1, filePath);
                        statement.setString(2, title);
                        statement.setString(3, artist);
                        statement.setString(4, album);
                        statement.setString(5, genre);
                        statement.setString(6, year);
                        statement.setString(7, composers);
                        statement.setString(8, lyricsFile);
                        statement.setInt(9, trackNumber != null ? Integer.parseInt(trackNumber) : 0);
                        statement.setInt(10, discNumber != null ? Integer.parseInt(discNumber) : 0);
                        statement.setString(10, albumArtFile != null ? albumArtFile : "");
                        statement.executeUpdate();
                    } else {
                        System.out.println("Failed to read metadata from " + filePath);
                    }
                }
            }
        } catch (Exception e) {
            System.out.println("An error occurred! We apologize for the inconvenience!");
            System.out.println("\nNow restarting the application!");
            clearConsole();
            Main.mainMenu();
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
            statement.setString(11, "");
            statement.executeUpdate();
        } catch (SQLException e) {
            System.out.println("An error occurred while inserting the WAV file into the database.");
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
            System.out.println("An error occurred while reading the metadata from the file.");
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
                String albumArt = null;

                AudioHeader audioHeader = audioFile.getAudioHeader();
                String bitRate = audioHeader.getBitRate();
                String sampleRate = audioHeader.getSampleRate();
                long fileSize = new File(filePath).length();

                String lyricsFile = LYRICS_DIRECTORY;

                Entity song = new Entity(id, filePath, title, artist, album, genre, year, composers, lyricsFile,
                        trackNumber != null && !trackNumber.isEmpty() ? Integer.parseInt(trackNumber) : 0,
                        discNumber != null && !discNumber.isEmpty() ? Integer.parseInt(discNumber) : 0,
                        albumArt != null ? albumArt : "");
                insertSong(song);
            } else {
                System.out.println("Failed to read metadata from " + filePath);
            }
        } catch (Exception e) {
            System.out.println("An error occurred while reading the metadata from the file.");
        }
    }

    public static void wavChunkInfo(String filePath) throws Exception {
        File file = new File(filePath);
        byte[] data;
        try {
            data = IOUtils.toByteArray(new FileInputStream(file));
            if (data[0] != 'R' || data[1] != 'I' || data[2] != 'F' || data[3] != 'F') {
                throw new IOException("Not a WAV file");
            }
            int chunkStartPosition = 8;
            while (chunkStartPosition < data.length - 8) {
                // Print Chunk ID and Size
                String chunkId = new String(data, chunkStartPosition, 4);
                int chunkSize = (data[chunkStartPosition + 4] << 24) + (data[chunkStartPosition + 5] << 16) + (data[chunkStartPosition + 6] << 8) + data[chunkStartPosition + 7];
                chunkStartPosition += 8 + chunkSize;
            }
        } catch (IOException e) {
            System.err.println("Error reading file: " + e.getMessage());
        } catch (Exception e) {
            System.out.println("An error occurred! We apologize for the inconvenience!");
            System.out.println("\nNow restarting the application!");
            clearConsole();
            Main.mainMenu();
        }
    }
}