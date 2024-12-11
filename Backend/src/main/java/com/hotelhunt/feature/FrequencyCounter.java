package com.hotelhunt.feature;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.springframework.stereotype.Component;
import com.opencsv.CSVReader;
import com.opencsv.exceptions.CsvValidationException;
import jakarta.annotation.PostConstruct;

/**
 * FrequencyCounter class processes CSV files to count the frequency of words.
 * It supports functionality to get the top N most frequent words, retrieve the entire frequency map,
 * and query the frequency of specific words.
 */
@Component
public class FrequencyCounter {

    private final Map<String, Integer> frequencyMap = new HashMap<>();
    private final Pattern wordPattern = Pattern.compile("\\b[a-zA-Z]+\\b");
    private final Set<String> excludeWords = new HashSet<>(Arrays.asList("https", "o", "cf", "bstatic", "k"));

    /**
     * Initializes the frequency counter by reading CSV files from the specified folder.
     * This method is executed after the bean's properties have been set.
     */
    @PostConstruct
    public void init() {
        String folderPath = "src/main/resources/data/Booking/";
        try {
            readCsvFilesAndCountWords(new File(folderPath));
        } catch (IOException | CsvValidationException e) {
            System.err.println("Error initializing FrequencyCounter: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Reads CSV files from the specified folder and counts the frequency of words.
     *
     * @param folder The folder containing CSV files.
     * @throws IOException            If an I/O error occurs while reading the files.
     * @throws CsvValidationException If a CSV validation error occurs.
     */
    private void readCsvFilesAndCountWords(File folder) throws IOException, CsvValidationException {
        File[] files = folder.listFiles((dir, name) -> name.endsWith(".csv"));

        if (files != null) {
            for (File file : files) {
                processCsvFile(file);
            }
        } else {
            System.err.println("No CSV files found in directory: " + folder.getPath());
        }
    }

    /**
     * Processes a single CSV file to count word frequencies.
     *
     * @param file The CSV file to process.
     * @throws IOException            If an I/O error occurs while reading the file.
     * @throws CsvValidationException If a CSV validation error occurs.
     */
    private void processCsvFile(File file) throws IOException, CsvValidationException {
        try (CSVReader csvReader = new CSVReader(new FileReader(file))) {
            String[] line;
            boolean isFirstLine = true;
            while ((line = csvReader.readNext()) != null) {
                if (isFirstLine) {
                    isFirstLine = false; // Skip header line
                    continue;
                }
                processLine(line);
            }
        } catch (IOException | CsvValidationException e) {
            System.err.println("Error processing file: " + file.getName() + " - " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Processes a line from a CSV file to update the word frequency map.
     *
     * @param line The line to process.
     */
    private void processLine(String[] line) {
        for (String column : line) {
            Matcher matcher = wordPattern.matcher(column);
            while (matcher.find()) {
                String word = matcher.group().toLowerCase();
                if (!excludeWords.contains(word) && word.length() > 1) {
                    frequencyMap.put(word, frequencyMap.getOrDefault(word, 0) + 1);
                }
            }
        }
    }

    /**
     * Gets the top N most frequent words from the frequency map.
     *
     * @param n The number of top frequent words to return.
     * @return A list of the top N most frequent words and their counts.
     */
    public List<Map.Entry<String, Integer>> getTopFrequentWords(int n) {
        // Create a min-heap (priority queue) with a custom comparator for sorting by frequency
        PriorityQueue<Map.Entry<String, Integer>> minHeap = new PriorityQueue<>(
            Comparator.comparingInt(Map.Entry::getValue)
        );

        // Iterate over each entry in the frequency map
        for (Map.Entry<String, Integer> entry : frequencyMap.entrySet()) {
            // Add the entry to the min-heap
            minHeap.offer(entry);

            // Ensure the heap does not exceed size n
            if (minHeap.size() > n) {
                minHeap.poll(); // Remove the least frequent element
            }
        }

        // Convert the heap to a list and reverse it to get descending order by frequency
        List<Map.Entry<String, Integer>> topFrequentWords = new ArrayList<>(minHeap);
        topFrequentWords.sort((entry1, entry2) -> Integer.compare(entry2.getValue(), entry1.getValue()));

        return topFrequentWords;
    }


    /**
     * Retrieves the entire frequency map.
     *
     * @return A copy of the frequency map containing word counts.
     */
    public Map<String, Integer> getFrequencyMap() {
        return new HashMap<>(frequencyMap); // Return a copy for safety
    }

    /**
     * Retrieves the frequency of a specific word.
     *
     * @param word The word to check.
     * @return The frequency of the word.
     */
    public int getWordFrequency(String word) {
        return frequencyMap.getOrDefault(word.toLowerCase(), 0);
    }
}
