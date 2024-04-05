package xyz.yeems214.MusicPlayer.Extensions;

import org.jaudiotagger.audio.AudioFile;
import org.jaudiotagger.audio.AudioFileIO;
import org.jaudiotagger.audio.exceptions.CannotReadException;
import org.jaudiotagger.audio.exceptions.InvalidAudioFrameException;
import org.jaudiotagger.audio.exceptions.ReadOnlyFileException;
import org.jaudiotagger.tag.Tag;
import org.jaudiotagger.tag.TagException;
import org.jaudiotagger.tag.datatype.Artwork;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;

// This source code is responsible for displaying album art in a JFrame.
// It reads the album art from both the Database and the Song Metadata (only on FLAC songs).

public class AlbumArtViewer {
    public static void deez(String filePath, String albumArt) throws CannotReadException, TagException, InvalidAudioFrameException, ReadOnlyFileException, IOException {
        try {
            File file = new File(albumArt);
            if (albumArt.endsWith(".jpg") || albumArt.endsWith(".jpeg") || albumArt.endsWith(".png") || filePath.endsWith(".jpg") || filePath.endsWith(".jpeg") || filePath.endsWith(".png")) {
                BufferedImage image = ImageIO.read(file);
                displayImage(image);
            } else if (filePath.endsWith(".FLAC") || filePath.endsWith(".flac") || filePath.endsWith(".MP3") || filePath.endsWith(".mp3")) {
                File flac = new File(filePath);
                AudioFile audioFile = AudioFileIO.read(flac);

                Tag tag = audioFile.getTag();

                Artwork artwork = tag.getFirstArtwork();

                byte[] imageData = artwork.getBinaryData();
                BufferedImage image = ImageIO.read(new ByteArrayInputStream(imageData));

                displayImage(image);
            } else {
                System.out.println("Error: File type not supported");
                return;
            }
        } catch (IOException e) {
            if (filePath.endsWith(".FLAC") || filePath.endsWith(".flac") || filePath.endsWith(".MP3") || filePath.endsWith(".mp3")) {
                File flac = new File(filePath);
                AudioFile audioFile = AudioFileIO.read(flac);

                Tag tag = audioFile.getTag();

                Artwork artwork = tag.getFirstArtwork();

                byte[] imageData = artwork.getBinaryData();
                BufferedImage image = ImageIO.read(new ByteArrayInputStream(imageData));

                displayImage(image);
                System.out.println("File didnt exist, but we got the album art from the tag");
            } else {
                System.out.println("Album art not found!");
                return;
            }

            } catch (NullPointerException e) {
                System.out.println("Album art not found!");
            } catch(Exception e) {
                System.out.println("Error: " + e.getMessage());
                e.printStackTrace();
            }
        }

        public static void main (String[]args) throws CannotReadException, TagException, InvalidAudioFrameException, ReadOnlyFileException, IOException {
            System.out.println("Test Album Art viewer");
            String filePath = "C:\\Users\\USER\\DuffinMusicPlayer-Java\\src\\main\\resources\\albumart-files\\05. Roosevelt - Paralyzed converted.jpg";
            String albumArt = null;
            deez(albumArt, filePath);
        }

        public static void displayImage (BufferedImage image){
            JFrame frame = new JFrame("Album Art");
            frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
            frame.setSize(640, 640);

            Image scaledImage = image.getScaledInstance(frame.getWidth(), frame.getHeight(), Image.SCALE_SMOOTH);
            JLabel label = new JLabel(new ImageIcon(scaledImage));

            frame.getContentPane().setBackground(Color.BLACK);
            label.setOpaque(true);
            label.setBackground(Color.BLACK);

            frame.getContentPane().add(label, BorderLayout.CENTER);
            frame.setVisible(true);
        }
    }
