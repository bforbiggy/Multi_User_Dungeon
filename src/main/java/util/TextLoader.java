package util;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Scanner;

public class TextLoader {
    /**
     * Reads in title screen text from file location.
     * @param textPath location of the text file
     * @return string representation of title screen
     */
    public static String loadText(String textPath) {
        StringBuilder output = new StringBuilder();
        
        File titleFile = new File(textPath);
        try (Scanner scanner = new Scanner(titleFile, StandardCharsets.UTF_8)){
            while (scanner.hasNextLine())
                output.append(scanner.nextLine() + "\n");
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            output.append(textPath + " text file not found.");
        } catch (IOException e) {
            e.printStackTrace();
        }
        return output.toString();
    }
}
