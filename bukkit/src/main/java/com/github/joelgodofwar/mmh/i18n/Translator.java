package com.github.joelgodofwar.mmh.i18n;

import com.github.joelgodofwar.mmh.util.Utils;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.function.Function;


public class Translator {
    private static String lang;
    private static File dataFolder;

    static Properties props = new Properties();

    public static void load(String lang, File dataFolder, Function<String, InputStream> getResource) {
        Translator.lang = formatLanguageCode(lang);
        Translator.dataFolder = dataFolder;

        Map<String, String> bundle = new TreeMap<>();
        try (InputStream input = getResource.apply("lang/lang_" + Translator.lang + ".properties")) {
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(input, StandardCharsets.UTF_8))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    if (!line.contains("=") || line.trim().startsWith("#")) continue;
                    String[] split = line.split("=", 2);
                    bundle.put(split[0], split[1]);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

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
                props.setProperty(key, bundle.get(key));
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
        Utils.createFileIfNotExists(file);
        // Write sorted properties to file
        try (FileWriter fw = new FileWriter(file, StandardCharsets.UTF_8, false)) {
            try (BufferedWriter writer = new BufferedWriter(fw)) {
                for (Map.Entry<String, String> entry : sortedProps.entrySet()) {
                    writer.write(entry.getKey() + "=" + entry.getValue() + "\n");
                }
            }
        }
    }

    public static String formatLanguageCode(String lang) {
        String[] parts = lang.split("_");
        return parts[0].toLowerCase() + "_" + parts[1].toUpperCase();
    }

}