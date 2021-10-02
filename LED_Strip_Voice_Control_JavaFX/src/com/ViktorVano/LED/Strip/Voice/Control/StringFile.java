package com.ViktorVano.LED.Strip.Voice.Control;

import java.io.File;
import java.io.FileWriter;
import java.util.Scanner;

public class StringFile {
    public static void saveStringToFile(String filename, String value)
    {
        try
        {
            File file = new File(filename);
            file.createNewFile();
            //Write Content
            FileWriter writer = new FileWriter(file);
            writer.write(value);
            writer.close();
        }catch (Exception e)
        {
            System.out.println("Failed to create the \"" + filename + "\" file.");
        }
    }

    public static String loadStringFromFile(String filename, String defaultValue)
    {
        String value;
        try
        {
            File file = new File(filename);
            Scanner scanner = new Scanner(file);
            if(scanner.hasNextInt())
            {
                value = scanner.nextLine();
            }else
            {
                value = defaultValue;
                saveStringToFile(filename, value);
            }
            scanner.close();
        }catch (Exception e)
        {
            System.out.println("Failed to read the \"" + filename + "\" file.");
            value = defaultValue;
            saveStringToFile(filename, value);
        }
        return value;
    }
}
