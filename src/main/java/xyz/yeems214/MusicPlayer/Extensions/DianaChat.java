package xyz.yeems214.MusicPlayer.Extensions;

import xyz.yeems214.MusicPlayer.Interfaces.FileManager;
import xyz.yeems214.MusicPlayer.Main;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Properties;
import java.util.Scanner;

import static xyz.yeems214.MusicPlayer.Main.clearConsole;

// This source code contains the Process Builder responsible for passing through Python CLI responses to Java without the use of API
// This DianaChat is based on Google's Gemini 1.0 Pro API code which I inherited from my previous project
public class DianaChat {
    public static String Directory;
    static {
        try {
            Properties properties = new Properties();
            properties.load(FileManager.class.getClassLoader().getResourceAsStream("application.properties"));
            Directory = properties.getProperty("extensions.directory");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void PythonParse(String userInput) throws Exception {
        String dir = Directory;
        String diana = dir + "/dianaChat.py";
        ProcessBuilder builder = new ProcessBuilder("python3", diana, userInput);
        Process process = builder.start();

        BufferedReader stdOutReader = new BufferedReader(new InputStreamReader(process.getInputStream()));
        BufferedReader stdErrReader = new BufferedReader(new InputStreamReader(process.getErrorStream()));

        String line;
        StringBuilder insights = new StringBuilder();

        while ((line = stdOutReader.readLine()) != null) {
            insights.append(line).append("\n");
        }

        while ((line = stdErrReader.readLine()) != null) {
            insights.append(line).append("\n");
        }

        process.waitFor();

        System.out.println("\n"+ insights.toString());

        System.out.println("Do you want to ask more questions? (Type yes to continue)");
        Scanner scan = new Scanner(System.in);
        String user = scan.nextLine().toLowerCase();
        switch (user) {
            case "yes":
                DianaChat.deez();
                break;
            default:
                Main.mainMenu();
                break;
        }
    }

    private static void dianaBanner() {
        String[] asciiArt = {
                " /$$$$$$$  /$$                              ",
                "| $$__  $$|__/                              ",
                "| $$  \\ $$ /$$  /$$$$$$  /$$$$$$$   /$$$$$$ ",
                "| $$  | $$| $$ |____  $$| $$__  $$ |____  $$",
                "| $$  | $$| $$  /$$$$$$$| $$  \\ $$  /$$$$$$$",
                "| $$  | $$| $$ /$$__  $$| $$  | $$ /$$__  $$",
                "| $$$$$$$/| $$|  $$$$$$$| $$  | $$|  $$$$$$$",
                "|_______/ |__/ \\_______/|__/  |__/ \\_______/",
                "                                             "
        };

        String[] logo = {
                "  .--. ",
                " (___) ",
                " /   \\ ",
                "/     \\",
                "  `-'  "
        };

        for (int i = 0; i < asciiArt.length; i++) {
            if (i < logo.length) {
                System.out.println(logo[i] + asciiArt[i]);
            } else {
                System.out.println("      " + asciiArt[i]);
            }
        }
    }

    public static void deez() throws Exception {
        clearConsole();
        dianaBanner();
        Scanner scan = new Scanner(System.in);

        System.out.println("Hi, I'm Diana! Your Personal Music AI! \nWhat would you like to ask me today? (Press q to quit)");
        String userInput = scan.nextLine();
        switch (userInput) {
            case "q":
                clearConsole();
                Main.mainMenu();
                break;
            default:
                clearConsole();
                System.out.println("Sending message to Diana!");
                PythonParse(userInput);
                break;
        }
    }
}
