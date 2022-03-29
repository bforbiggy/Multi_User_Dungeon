package model.util;

import java.io.*;

import model.entities.*;

public class JSONSaver {
    // NOTE: USES XML CODE THEN CONVERTS XML TO JSON
    public static void savePlayer(Player player, String outputPath){
        try {
            File file = new File(outputPath);
            FileWriter writer = new FileWriter(file);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
