package com.hotelhunt.feature;
 
import org.springframework.stereotype.Component;
 
import com.opencsv.CSVReader;
import com.opencsv.exceptions.CsvValidationException;
import jakarta.annotation.PostConstruct;
 
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;
 
/**
* SearchFrequencyCounter manages a HashMap to store and process city names
* from CSV files, and provides functionality to track and update search frequencies.
*/
@Component
public class SearchFrequency {
 
    private static final Random RANDOM = new Random();
    private final Map<String, Integer> keywordFrequencyMap;
 
    /**
     * Constructor initializes the keyword frequency map.
     */
    public SearchFrequency() {
        this.keywordFrequencyMap = new HashMap<>();
    }
 
    /**
     * PostConstruct method to initialize the keyword frequency map with city names from CSV files.
     * This method is called after dependency injection is done to perform any initialization.
     */
    @PostConstruct
    public void init() {
        String folderPath = "src/main/resources/data/Booking/";
 
        try {
            loadCsvCitiesToMap(new File(folderPath));
        } catch (IOException | CsvValidationException e) {
            System.err.println("Error initializing SearchFrequencyCounter: " + e.getMessage());
            e.printStackTrace();
        }
    }
 
    /**
     * Loads city names from CSV files into the map, assigning random initial frequencies.
     *
     * @param folder The folder containing CSV files.
     * @throws IOException            If an IO error occurs while reading the files.
     * @throws CsvValidationException If a CSV validation error occurs.
     */
    private void loadCsvCitiesToMap(File folder) throws IOException, CsvValidationException {
        File[] files = folder.listFiles((dir, name) -> name.endsWith(".csv"));
 
        if (files == null || files.length == 0) {
            System.err.println("No CSV files found in directory: " + folder.getAbsolutePath());
            return;
        }
 
        for (File file : files) {
            try (CSVReader csvReader = new CSVReader(new FileReader(file))) {
                String[] header = csvReader.readNext();
                if (header == null) {
                    continue; // Skip if there is no header
                }
 
                // Determine column index for city
                int cityIndex = findIndex(header, "Location");
 
                // Read the rest of the CSV file
                String[] line;
                while ((line = csvReader.readNext()) != null) {
                    if (cityIndex != -1 && line.length > cityIndex) {
                        processColumn(line[cityIndex]); // Process city name
                    }
                }
            }
        }
    }
 
    /**
     * Finds the index of the column with the given name in the header.
     *
     * @param header     The header row of the CSV file.
     * @param columnName The name of the column to find.
     * @return The index of the column, or -1 if not found.
     */
    private int findIndex(String[] header, String columnName) {
        for (int i = 0; i < header.length; i++) {
            if (header[i].trim().equalsIgnoreCase(columnName)) {
                return i;
            }
        }
        return -1;
    }
 
    /**
     * Processes a column by splitting it into words and inserting them into the map.
     *
     * @param column The column value to process.
     */
    private void processColumn(String column) {
        String[] words = column.split("\\s+");
        for (String word : words) {
            word = word.toLowerCase().replaceAll("[^a-z]", "");
            if (word.length() > 1) {
                int frequency = 1 + RANDOM.nextInt(50); // Frequency between 1 and 50
                insertKeyword(word, frequency);
            }
        }
    }
 
    /**
     * Inserts a keyword into the map with the specified initial frequency.
     *
     * @param keyword   The keyword to insert.
     * @param frequency The initial frequency of the keyword.
     */
    public void insertKeyword(String keyword, int frequency) {
        keywordFrequencyMap.put(keyword, frequency);
    }
 
    /**
     * Searches for a keyword in the map and updates its frequency.
     * If the keyword does not exist, it is added with a frequency of 1.
     *
     * @param keyword The keyword to search and update.
     * @return The updated frequency of the keyword.
     */
    public int searchAndUpdateFrequency(String keyword) {
        keyword = keyword.toLowerCase().replaceAll("[^a-z]", "");
        int newFrequency = keywordFrequencyMap.getOrDefault(keyword, 0) + 1;
        keywordFrequencyMap.put(keyword, newFrequency);
        return newFrequency;
    }
 
    /**
     * Retrieves the top N most frequently searched keywords from the map.
     *
     * @param n The number of top frequent keywords to retrieve.
     * @return A list of the top N most frequently searched keywords and their frequencies.
     */
    public List<Map.Entry<String, Integer>> getTopFrequentSearches(int n) {
        PriorityQueue<Map.Entry<String, Integer>> heap = new PriorityQueue<>(Map.Entry.comparingByValue());
 
        for (Map.Entry<String, Integer> entry : keywordFrequencyMap.entrySet()) {
            heap.offer(entry);
            if (heap.size() > n) {
                heap.poll();
            }
        }
 
        List<Map.Entry<String, Integer>> result = new ArrayList<>();
        while (!heap.isEmpty()) {
            result.add(heap.poll());
        }
 
        // Since we used a min-heap, we need to reverse the result to have the highest frequencies first
        Collections.reverse(result);
        return result;
    }
}