package model.util;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

public class TextLoader {
    /**
     * Reads in title screen text from file location.
     * @param textPath location of the text file
     * @return string representation of title screen
     */
    public static String loadText(String textPath) {
        String output = "";
        try {
            File titleFile = new File(textPath);
            Scanner scanner = new Scanner(titleFile, "utf-8");
            while (scanner.hasNextLine())
                output += scanner.nextLine() + "\n";
            scanner.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            output = textPath + " text file not found.";
        }
        return output;
    }
}
