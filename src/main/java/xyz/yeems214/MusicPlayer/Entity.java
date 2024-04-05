package xyz.yeems214.MusicPlayer;
public class Entity {
    public int id;
    public String filePath;
    public String title;
    public String artist;
    public String album;
    public String genre;
    public String year;
    public String composers;
    public int trackNumber;
    public int discNumber;
    public String lyricsFile;
    public String albumArt;
    public Entity(int id, String filePath, String title, String artist, String album, String genre, String year, String composers, String lyricsFile, int trackNumber, int discNumber, String  albumArt) {
        this.id = id;
        this.filePath = filePath;
        this.title = title;
        this.artist = artist;
        this.album = album;
        this.genre = genre;
        this.year = year;
        this.composers = composers;
        this.lyricsFile = lyricsFile;
        this.trackNumber = trackNumber;
        this.discNumber = discNumber;
        this.albumArt = albumArt;
    }
    public Entity(String filePath, String title, String artist, String album, String genre, String year, int trackNumber, int discNumber, String lyricsFile, String albumArt, String composers) {
        this.filePath = filePath;
        this.title = title;
        this.artist = artist;
        this.album = album;
        this.genre = genre;
        this.year = year;
        this.trackNumber = trackNumber;
        this.discNumber = discNumber;
        this.lyricsFile = lyricsFile;
        this.albumArt = albumArt;
        this.composers = composers;
    }

    public int getId() {
        return id;
    }

    public String getFilePath() {
        return filePath;
    }

    public String getTitle() {
        return title;
    }

    public String getArtist() {
        return artist;
    }

    public String getAlbum() {
        return album;
    }

    public String getGenre() {
        return genre;
    }

    public String getYear() {
        return year;
    }

    public String getComposers() {
        return composers;
    }
    public String getLyricsFile() {
        return lyricsFile;
    }
    public int getTrackNumber() {
        return trackNumber;
    }
    public int getDiscNumber() {
        return discNumber;
    }
    public String getAlbumArt() {
        return albumArt;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setArtist(String artist) {
        this.artist = artist;
    }

    public void setAlbum(String album) {
        this.album = album;
    }

    public void setGenre(String genre) {
        this.genre = genre;
    }

    public void setYear(String year) {
        this.year = year;
    }

    public void setComposers(String composers) {
        this.composers = composers;
    }
    public void setLyricsFile(String lyricsFile) {
        this.lyricsFile = lyricsFile;
    }
    public void setTrackNumber(int trackNumber) {
        this.trackNumber = trackNumber;
    }
    public void setDiscNumber(int discNumber) {
        this.discNumber = discNumber;
    }
    public void setAlbumArt(String albumArt) {
        this.albumArt = albumArt;
    }
    public void setMetadata(int id, String filePath, String title, String artist, String album, String genre, String year, String composers, String lyricsFile, int trackNumber, int discNumber, String albumArt) {
        this.id = id;
        this.filePath = filePath;
        this.title = title;
        this.artist = artist;
        this.album = album;
        this.genre = genre;
        this.year = year;
        this.composers = composers;
        this.lyricsFile = lyricsFile;
        this.trackNumber = trackNumber;
        this.discNumber = discNumber;
        this.albumArt = albumArt;
    }
}
