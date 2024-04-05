package xyz.yeems214.MusicPlayer.TestingShit;

import org.apache.commons.io.IOUtils;
import org.jaudiotagger.audio.AudioFile;
import org.jaudiotagger.audio.AudioFileIO;
import org.jaudiotagger.audio.exceptions.CannotReadException;
import org.jaudiotagger.audio.exceptions.InvalidAudioFrameException;
import org.jaudiotagger.audio.exceptions.ReadOnlyFileException;
import org.jaudiotagger.tag.FieldKey;
import org.jaudiotagger.tag.Tag;
import org.jaudiotagger.tag.TagException;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

public class TestWAVMetadata {
    public static void readMetadata(String filePath) throws IOException, CannotReadException, TagException, InvalidAudioFrameException, ReadOnlyFileException {
        File file = new File(filePath);
        AudioFile audioFile = AudioFileIO.read(file);
        Tag tag = audioFile.getTag();

        // Access specific metadata fields
        String title = tag.getFirst(FieldKey.TITLE);
        String artist = tag.getFirst(FieldKey.ARTIST);
        // (Optional) Access other fields like album, year, etc.

        System.out.println("Title: " + title);
        System.out.println("Artist: " + artist);
    }
    public static void printChunkInfo(String filePath) {
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
                int chunkSize = (data[chunkStartPosition + 4] << 24) +
                        (data[chunkStartPosition + 5] << 16) +
                        (data[chunkStartPosition + 6] << 8) +
                        data[chunkStartPosition + 7];
                System.out.printf("Chunk ID: %s, Size: %d\n", chunkId, chunkSize);

                // Update chunk start position for next iteration
                chunkStartPosition += 8 + chunkSize;
            }
        } catch (IOException e) {
            System.err.println("Error reading file: " + e.getMessage());
        }
    }

    public static void main(String[] args) throws CannotReadException, TagException, InvalidAudioFrameException, ReadOnlyFileException, IOException {
        printChunkInfo("src/main/resources/music-files/Queen of Wands.wav");
    }
}
