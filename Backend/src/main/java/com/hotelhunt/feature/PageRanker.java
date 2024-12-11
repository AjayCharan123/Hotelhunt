package com.hotelhunt.feature;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.File;
import java.util.*;
import java.util.stream.Collectors;

/**
 * PageRanker class provides functionality to rank files based on the frequency
 * of their names' occurrences in the text data.
 */
@Service
public class PageRanker {

    private final FrequencyCounter frequencyCounter;

    /**
     * Constructor for PageRanker.
     *
     * @param frequencyCounter An instance of FrequencyCounter to get word frequencies.
     */
    @Autowired
    public PageRanker(FrequencyCounter frequencyCounter) {
        this.frequencyCounter = frequencyCounter;
    }

    /**
     * Gets the frequencies of city names based on the frequency of their occurrences in the text data.
     * It considers file names as potential city names.
     *
     * @return A map of file names and their corresponding frequencies, sorted in descending order of frequency.
     */
    public Map<String, Integer> getCityNameFrequencies() {
        String folderPath = "src/main/resources/data/Booking/";
        File folder = new File(folderPath);

        // Ensure the folder exists and is a directory
        if (!folder.exists() || !folder.isDirectory()) {
            System.err.println("Directory not found: " + folderPath);
            return Collections.emptyMap();
        }

        // List CSV files in the directory
        File[] files = folder.listFiles((dir, name) -> name.endsWith(".csv"));

        if (files == null) {
            System.err.println("No files found in directory: " + folderPath);
            return Collections.emptyMap();
        }

        // Map to store file names and their frequencies
        Map<String, Integer> fileNameFrequency = new HashMap<>();

        for (File file : files) {
            String fileNameWithoutExtension = getFileNameWithoutExtension(file.getName());
            int frequency = frequencyCounter.getWordFrequency(fileNameWithoutExtension);
            fileNameFrequency.put(file.getName(), frequency);
        }

        // Sort files by frequency in descending order
        return fileNameFrequency.entrySet().stream()
                .sorted(Map.Entry.<String, Integer>comparingByValue().reversed())
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        Map.Entry::getValue,
                        (e1, e2) -> e1,
                        LinkedHashMap::new)); // LinkedHashMap maintains insertion order
    }

    /**
     * Extracts the file name without its extension.
     *
     * @param fileName The file name with extension.
     * @return The file name without its extension.
     */
    private String getFileNameWithoutExtension(String fileName) {
        int lastDotIndex = fileName.lastIndexOf('.');
        return (lastDotIndex == -1) ? fileName : fileName.substring(0, lastDotIndex);
    }
}
