package com.github.joelgodofwar.mmh.i18n;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.*;


public class Translator {
    private static String lang;
    private static File dataFolder;

    static Properties props = new Properties();

    public static void load(String lang, File dataFolder) {
        Translator.lang = formatLanguageCode(lang);
        Translator.dataFolder = dataFolder;

        ResourceBundle bundle = ResourceBundle.getBundle("lang/lang", new Locale(Translator.lang));
        //ResourceBundle bundle = ResourceBundle.getBundle("lang/" + lang);

        File langFile = new File(Translator.dataFolder, "lang/" + Translator.lang + ".properties");
        props.clear();
        if (langFile.exists()) {
            try (FileReader reader = new FileReader(langFile, StandardCharsets.UTF_8)) {
                props.load(reader);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        boolean sort = false;
        for (String key : bundle.keySet()) {
            if (!props.containsKey(key)) {
                props.setProperty(key, bundle.getString(key));
                sort = true;
            }
        }
        if (sort || !langFile.exists()) try {
            sortPropertiesFile(langFile, props);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static String get(String key, String defaultValue) {
        return props.getProperty(key, defaultValue);
    }

    public static void sortPropertiesFile(File file, Properties props) throws IOException {
        // Create a TreeMap to store the sorted properties
        Map<String, String> sortedProps = new TreeMap<>();
        // Iterate over the Properties object and add each key-value pair to the TreeMap
        for (String key : props.stringPropertyNames()) {
            sortedProps.put(key, props.getProperty(key));
        }
        if (!file.exists()) {
            file.createNewFile();
        }
        // Clear the properties file
        try (FileOutputStream outputStream = new FileOutputStream(file)) {
            outputStream.getChannel().truncate(0);
        }
        // Write sorted properties to file
        try (FileWriter writer = new FileWriter(file)) {
            for (Map.Entry<String, String> entry : sortedProps.entrySet()) {
                writer.write(entry.getKey() + "=" + entry.getValue() + "\n");
            }
        }
    }

    public static String formatLanguageCode(String lang) {
        String[] parts = lang.split("_");
        return parts[0].toLowerCase() + "_" + parts[1].toUpperCase();
    }

}