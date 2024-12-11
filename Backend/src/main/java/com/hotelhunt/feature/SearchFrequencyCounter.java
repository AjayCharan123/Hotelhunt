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
 * SearchFrequencyCounter manages a Trie structure to store and process city names
 * from CSV files, and provides functionality to track and update search frequencies.
 */
@Component
public class SearchFrequencyCounter {

    private static final Random RANDOM = new Random();
    private final TrieNode root;

    /**
     * Constructor initializes the Trie root node.
     */
    public SearchFrequencyCounter() {
        this.root = new TrieNode();
    }

    /**
     * PostConstruct method to initialize the Trie with city names from CSV files.
     * This method is called after dependency injection is done to perform any initialization.
     */
    @PostConstruct
    public void init() {
        String folderPath = "src/main/resources/data/Booking/";
        try {
            loadCsvCitiesToTrie(new File(folderPath));
        } catch (IOException | CsvValidationException e) {
            System.err.println("Error initializing SearchFrequencyCounter: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Loads city names from CSV files into the Trie, assigning random initial frequencies.
     *
     * @param folder The folder containing CSV files.
     * @throws IOException            If an IO error occurs while reading the files.
     * @throws CsvValidationException If a CSV validation error occurs.
     */
    private void loadCsvCitiesToTrie(File folder) throws IOException, CsvValidationException {
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
     * @param header The header row of the CSV file.
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
     * Processes a column by splitting it into words and inserting them into the Trie.
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
     * Inserts a keyword into the Trie with the specified initial frequency.
     *
     * @param keyword   The keyword to insert.
     * @param frequency The initial frequency of the keyword.
     */
    public void insertKeyword(String keyword, int frequency) {
        TrieNode currentNode = root;
        for (char c : keyword.toCharArray()) {
            currentNode = currentNode.getChildren().computeIfAbsent(c, k -> new TrieNode());
        }
        currentNode.setEndOfWord(true);
        currentNode.setFrequency(frequency);
    }

    /**
     * Searches for a keyword in the Trie and updates its frequency.
     * If the keyword does not exist, it is added with a frequency of 1.
     *
     * @param keyword The keyword to search and update.
     * @return The updated frequency of the keyword.
     */
    public int searchAndUpdateFrequency(String keyword) {
        TrieNode currentNode = root;
        for (char c : keyword.toLowerCase().toCharArray()) {
            currentNode = currentNode.getChildren().get(c);
            if (currentNode == null) {
                insertKeyword(keyword, 1); // Add new keyword with frequency 1
                return 1;
            }
        }
        if (currentNode.isEndOfWord()) {
            currentNode.setFrequency(currentNode.getFrequency() + 1);
            return currentNode.getFrequency();
        }
        return 0; // This shouldn't be reached
    }

    /**
     * Retrieves the top N most frequently searched keywords from the Trie.
     *
     * @param n The number of top frequent keywords to retrieve.
     * @return A list of the top N most frequently searched keywords and their frequencies.
     */
    public List<Map.Entry<String, Integer>> getTopFrequentSearches(int n) {
        List<Map.Entry<String, Integer>> result = new ArrayList<>();
        traverseTrie(root, new StringBuilder(), result);

        result.sort((entry1, entry2) -> Integer.compare(entry2.getValue(), entry1.getValue()));
        return result.subList(0, Math.min(n, result.size()));
    }

    /**
     * Traverses the Trie to collect all keywords and their frequencies.
     *
     * @param currentNode The current Trie node.
     * @param currentWord The current word being built.
     * @param result      The list to store keywords and their frequencies.
     */
    private void traverseTrie(TrieNode currentNode, StringBuilder currentWord, List<Map.Entry<String, Integer>> result) {
        if (currentNode.isEndOfWord()) {
            result.add(new AbstractMap.SimpleEntry<>(currentWord.toString(), currentNode.getFrequency()));
        }
        for (Map.Entry<Character, TrieNode> entry : currentNode.getChildren().entrySet()) {
            currentWord.append(entry.getKey());
            traverseTrie(entry.getValue(), currentWord, result);
            currentWord.deleteCharAt(currentWord.length() - 1);
        }
    }

    /**
     * TrieNode class to represent each node in the Trie.
     */
    private class TrieNode {
        private final Map<Character, TrieNode> children;
        private int frequency;
        private boolean isEndOfWord;

        public TrieNode() {
            this.children = new HashMap<>();
            this.frequency = 0;
            this.isEndOfWord = false;
        }

        public Map<Character, TrieNode> getChildren() {
            return children;
        }

        public int getFrequency() {
            return frequency;
        }

        public void setFrequency(int frequency) {
            this.frequency = frequency;
        }

        public boolean isEndOfWord() {
            return isEndOfWord;
        }

        public void setEndOfWord(boolean endOfWord) {
            isEndOfWord = endOfWord;
        }
    }
}

