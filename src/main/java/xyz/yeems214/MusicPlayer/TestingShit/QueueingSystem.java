//package xyz.yeems214.MusicPlayer.TestingShit;
//
//import java.io.File;
//import java.io.FileReader;
//import java.io.IOException;
//import java.util.*;
//import javax.sound.sampled.*;
//
//import com.opencsv.bean.CsvToBean;
//import com.opencsv.bean.CsvToBeanBuilder;
//import com.opencsv.bean.HeaderColumnNameMappingStrategy;
//import org.jflac.sound.spi.FlacAudioFileReader;
//import xyz.yeems214.MusicPlayer.Entity;
//import xyz.yeems214.MusicPlayer.Extensions.ElapsedTimeTracker;
//import xyz.yeems214.MusicPlayer.Interfaces.FileManager;
//
//public class QueueingSystem {
//    public static String Directory;
//    static {
//        try {
//            Properties properties = new Properties();
//            properties.load(FileManager.class.getClassLoader().getResourceAsStream("application.properties"));
//            Directory = properties.getProperty("playlist.directory");
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//    }
//    private List<Entity> playlist;
//    private int currentIndex;
//    private Clip clip;
//    private boolean isPlaying;
//    private boolean isRepeating;
//    private boolean isShuffling;
//    private Scanner scanner;
//
//    public QueueingSystem(String csvPath) {
//        playlist = loadPlaylistFromCSV(csvPath);
//        currentIndex = 0;
//        isPlaying = false;
//        isRepeating = false;
//        isShuffling = false;
//        scanner = new Scanner(System.in);
//    }
//
//    private List<Entity> loadPlaylistFromCSV(String csvPath) {
//        List<Entity> playlist = new ArrayList<>();
//        try {
//            HeaderColumnNameMappingStrategy<Entity> strategy = new HeaderColumnNameMappingStrategy<>();
//            strategy.setType(Entity.class);
//
//            CsvToBean<Entity> csvToBean = new CsvToBeanBuilder<Entity>(new FileReader(csvPath))
//                    .withType(Entity.class)
//                    .withMappingStrategy(strategy)
//                    .build();
//
//            playlist = csvToBean.parse();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//        return playlist;
//    }
//
//    public void play() {
//        if (!isPlaying) {
//            Entity song = playlist.get(currentIndex);
//            File file = new File(song.filePath);
//            System.out.println("Trying to play: " + file.getAbsolutePath()); // Verify file path
//
//            try {
//                AudioInputStream audioStream = getAudioInputStream(song.filePath);
//                clip = AudioSystem.getClip();
//                clip.open(audioStream);
//                clip.start();
//                isPlaying = true;
//            } catch (UnsupportedAudioFileException e) {
//                System.err.println("Error: Unsupported audio file format.");
//            } catch (IOException e) {
//                System.err.println("Error: Could not read audio file.");
//            } catch (LineUnavailableException e) {
//                System.err.println("Error: Audio line unavailable.");
//            }
//        }
//    }
//
//    private AudioInputStream getAudioInputStream(String filePath) throws UnsupportedAudioFileException, IOException {
//        File file = new File(filePath);
//        if (filePath.endsWith(".flac")) {
//            return playFlac(filePath);
//        } else {
//            return AudioSystem.getAudioInputStream(file);
//        }
//    }
//
//    private AudioInputStream playFlac(String filePath) throws UnsupportedAudioFileException, IOException {
//        File file = new File(filePath);
//        AudioInputStream originalStream = new FlacAudioFileReader().getAudioInputStream(file);
//        AudioFormat baseFormat = originalStream.getFormat();
//        AudioFormat decodedFormat = new AudioFormat(AudioFormat.Encoding.PCM_SIGNED,
//                baseFormat.getSampleRate(),
//                16,
//                baseFormat.getChannels(),
//                baseFormat.getChannels() * 2,
//                baseFormat.getSampleRate(),
//                false);
//        return AudioSystem.getAudioInputStream(decodedFormat, originalStream);
//    }
//
//    private void handleUserInput() throws Exception {
//        while (true) {
//            System.out.println("Music player commands:");
//            System.out.println("1. Play");
//            System.out.println("2. Pause");
//            System.out.println("3. Stop");
//            System.out.println("4. Next");
//            System.out.println("5. Previous");
//            System.out.println("6. Repeat");
//            System.out.println("7. Shuffle");
//            System.out.println("8. Adjust Volume");
//            System.out.println("9. Adjust Speed");
//            System.out.println("10. Jump to Song Time");
//            System.out.println("11. Show Queue");
//            System.out.println("12. Main Menu");
//            System.out.println("13. Exit");
//
//            System.out.print("Enter your choice: ");
//            int choice = scanner.nextInt();
//
//            switch (choice) {
//                case 1:
//                    play();
//                    break;
//                case 2:
//                    pause();
//                    break;
//                case 3:
//                    stop();
//                    break;
//                case 4:
//                    next();
//                    break;
//                case 5:
//                    previous();
//                    break;
//                case 6:
//                    toggleRepeat();
//                    break;
//                case 7:
//                    toggleShuffle();
//                    break;
//                case 8:
//                    adjustVolume();
//                    break;
//                case 9:
//                    adjustSpeed();
//                    break;
//                case 10:
//                    jumpToTime();
//                    break;
//                case 11:
//                    showQueue();
//                    break;
//                case 12:
//                    stop();
//                    try {
//                        mainMenu.extraFeatures();
//                    } catch (Exception e) {
//                        e.printStackTrace();
//                    }
//                    return;
//                case 13:
//                    stop();
//                    System.out.println("Exiting...");
//                    scanner.close();
//                    System.exit(0);
//                default:
//                    System.out.println("Invalid choice. Please try again.");
//            }
//        }
//    }
//
//    private void showQueue() {
//        System.out.println("Current Queue:");
//        for (int i = 0; i < playlist.size(); i++) {
//            Entity song = playlist.get(i);
//            System.out.printf("%d. %s - %s\n", i + 1, song.title, song.artist);
//        }
//    }
//
//    private void jumpToTime() {
//        pause();
//        System.out.println("Enter time (MM:SS) to jump to part: ");
//        String timeInput = scanner.next();
//        String[] timeParts = timeInput.split(":");
//        if (timeParts.length != 2) {
//            System.out.println("Invalid format. Please enter time in MM:SS format.");
//            return;
//        }
//        int minutes = Integer.parseInt(timeParts[0]);
//        int seconds = Integer.parseInt(timeParts[1]);
//        long microseconds = (minutes * 60L + seconds) * 1000000L;
//        clip.setMicrosecondPosition(microseconds);
//        play();
//    }
//
//    private void adjustSpeed() {
//        if (isPlaying) {
//            pause();
//            System.out.print("Enter speed factor (e.g., 0.5 for half speed, 2.0 for double speed): ");
//            float speedFactor = scanner.nextFloat();
//            FloatControl sampleRateControl = (FloatControl) clip.getControl(FloatControl.Type.SAMPLE_RATE);
//            float currentSampleRate = sampleRateControl.getValue();
//            sampleRateControl.setValue(currentSampleRate * speedFactor);
//            play();
//        }
//    }
//
//    private void adjustVolume() {
//        if (isPlaying) {
//            System.out.print("Enter volume level: ");
//            float volumeLevel = scanner.nextFloat();
//            FloatControl volumeControl = (FloatControl) clip.getControl(FloatControl.Type.MASTER_GAIN);
//            volumeControl.setValue(volumeLevel);
//        }
//    }
//
//    public void pause() {
//        if (isPlaying) {
//            clip.stop();
//            isPlaying = false;
//        }
//    }
//
//    public void stop() {
//        if (isPlaying) {
//            clip.stop();
//            clip.close();
//            isPlaying = false;
//        }
//    }
//
//    public void next() {
//        if (isPlaying) {
//            clip.stop();
//            clip.close(); // Close the current clip
//        }
//
//        if (isShuffling) {
//            currentIndex = getRandomIndex();
//        } else {
//            currentIndex = (currentIndex + 1) % playlist.size();
//        }
//
//        play(); // Start playing the next song
//    }
//
//    public void previous() {
//        if (isPlaying) {
//            clip.stop();
//        }
//        if (isShuffling) {
//            currentIndex = getRandomIndex();
//        } else {
//            currentIndex = (currentIndex - 1 + playlist.size()) % playlist.size();
//        }
//        play();
//    }
//
//    public void toggleRepeat() {
//        isRepeating = !isRepeating;
//    }
//
//    public void toggleShuffle() {
//        isShuffling = !isShuffling;
//        if (isShuffling) {
//            Collections.shuffle(playlist);
//        }
//    }
//
//    private int getRandomIndex() {
//        return (int) (Math.random() * playlist.size());
//    }
//
//    public static void deez(String playlistPath) {
//        QueueingSystem player = new QueueingSystem(playlistPath);
//        System.out.println("Now Playing: " + player.playlist.get(player.currentIndex).title);
//
//    }
//
//    public static void main(String[] args) {
//        System.out.println("Testing");
//        QueueingSystem player = new QueueingSystem(Directory + "/playlist.csv");
//        System.out.println("Now Playing: " + player.playlist.get(player.currentIndex).title);
//        try {
//            player.handleUserInput();
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//    }
//}