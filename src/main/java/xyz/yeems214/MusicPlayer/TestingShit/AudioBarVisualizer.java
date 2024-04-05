package xyz.yeems214.MusicPlayer.TestingShit;

import javax.sound.sampled.*;
import java.io.*;
import java.awt.*;
import java.awt.image.*;
import java.awt.geom.*;
import javax.swing.*;

import static xyz.yeems214.MusicPlayer.Main.filePath;

public class AudioBarVisualizer extends JPanel {
    // Audio input stream
    private AudioInputStream audioInputStream;
    // Audio format
    private AudioFormat audioFormat;
    // Buffer to store audio data
    private byte[] audioBytes;
    // Image to store the spectrogram
    private BufferedImage spectrogramImage;
    // Width and height of the spectrogram
    private int spectrogramWidth = 500;
    private int spectrogramHeight = 200;
    // Number of samples to read from the audio stream
    private int numSamples = 1024;
    // Color to use for the spectrogram
    private Color spectrogramColor = Color.BLUE;

    public AudioBarVisualizer(String filePath) {
        File file = new File(filePath);
        if (!file.exists()) {
            System.out.println("File not found: " + filePath);
            return;
        }
        try {
            // Initialize the audio input stream
            audioInputStream = AudioSystem.getAudioInputStream(file);
            audioFormat = audioInputStream.getFormat();
            audioBytes = new byte[(int) audioInputStream.getFrameLength() * audioFormat.getFrameSize()];
            audioInputStream.read(audioBytes);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    // Method to visualize the audio as a spectrogram
    public void visualizeAudio() {
        // Create the spectrogram image
        spectrogramImage = new BufferedImage(spectrogramWidth, spectrogramHeight, BufferedImage.TYPE_INT_RGB);
        Graphics2D g2d = spectrogramImage.createGraphics();
        g2d.setColor(spectrogramColor);

        // Calculate the number of samples per pixel
        int samplesPerPixel = audioBytes.length / spectrogramWidth;

        // Iterate over each pixel in the spectrogram
        for (int x = 0; x < spectrogramWidth; x++) {
            // Calculate the average amplitude for the samples in this pixel
            double avgAmplitude = 0;
            for (int i = 0; i < samplesPerPixel; i++) {
                avgAmplitude += Math.abs(audioBytes[(x * samplesPerPixel) + i]);
            }
            avgAmplitude /= samplesPerPixel;

            // Draw a line with the calculated amplitude
            g2d.draw(new Line2D.Double(x, spectrogramHeight, x, spectrogramHeight - (avgAmplitude / 256.0 * spectrogramHeight)));
        }

        // Dispose the graphics object
        g2d.dispose();
    }

    // Override the paintComponent method to draw the spectrogram
    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        g.drawImage(spectrogramImage, 0, 0, null);
    }

    public static void deez(String filePath) {
        if (filePath == null || filePath.isEmpty()) {
            System.out.println("File path is empty. Please enter a valid file path.");
            return;
        }
        AudioBarVisualizer audioVisualizer = new AudioBarVisualizer(filePath);
        if (audioVisualizer.isInitialized()) {
            audioVisualizer.visualizeAudio();

            JFrame frame = new JFrame("Audio Visualizer");
            frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
            frame.add(audioVisualizer);
            frame.setSize(500, 239);
            frame.setVisible(true);
        }
    }

    public boolean isInitialized() {
        return audioBytes != null;
    }

    // Main method to run the program
    public static void main(String[] args) {
        String filePath = "V:\\Plex\\Music\\WAV\\TheFatRat - Monody (feat. Laura Brehm).wav";
        deez(filePath);
    }
}
