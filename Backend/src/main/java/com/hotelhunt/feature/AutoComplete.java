package com.hotelhunt.feature;

import com.opencsv.CSVReader;
import com.opencsv.exceptions.CsvException;
import org.springframework.stereotype.Component;

import jakarta.annotation.PostConstruct;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

/**
 * TrieNode class represents a single node in the Trie structure.
 */
class TrieNode {
    TrieNode[] children;  // Array of children nodes for each letter and space
    boolean isEndOfWord;  // Flag to indicate the end of a word

    TrieNode() {
        this.children = new TrieNode[27];  // 26 letters + 1 space
        this.isEndOfWord = false;
    }
}

/**
 * Trie class provides methods to insert words and search for autocomplete suggestions.
 */
class Trie {
    private final TrieNode root;  // Root of the Trie

    Trie() {
        this.root = new TrieNode();
    }

    /**
     * Inserts a word into the Trie.
     *
     * @param key The word to insert.
     */
    void insert(String key) {
        TrieNode currentNode = root;
        for (char currentChar : key.toCharArray()) {
            int index = getIndex(currentChar);
            if (currentNode.children[index] == null) {
                currentNode.children[index] = new TrieNode();
            }
            currentNode = currentNode.children[index];
        }
        currentNode.isEndOfWord = true;
    }

    /**
     * Searches for words with the given prefix in the Trie.
     *
     * @param prefix The prefix to search for.
     * @return A list of words with the given prefix.
     */
    List<String> search(String prefix) {
        List<String> results = new ArrayList<>();
        TrieNode currentNode = root;
        for (char c : prefix.toCharArray()) {
            int index = getIndex(c);
            if (currentNode.children[index] == null) {
                return results;  // Return empty list if no match
            }
            currentNode = currentNode.children[index];
        }
        searchHelper(currentNode, results, new StringBuilder(prefix));
        return results;
    }

    /**
     * Helper function for recursively searching for words with a given prefix.
     *
     * @param node    The current TrieNode.
     * @param results The list to store found words.
     * @param prefix  The current prefix being built.
     */
    private void searchHelper(TrieNode node, List<String> results, StringBuilder prefix) {
        if (node.isEndOfWord) {
            results.add(prefix.toString());
        }
        for (int i = 0; i < 26; i++) { // Iterate over letters 'a' to 'z'
            if (node.children[i] != null) {
                searchHelper(node.children[i], results, prefix.append((char) ('a' + i)));
                prefix.setLength(prefix.length() - 1);  // Backtrack
            }
        }
        if (node.children[26] != null) { // Check for space
            searchHelper(node.children[26], results, prefix.append(' '));
            prefix.setLength(prefix.length() - 1);  // Backtrack
        }
    }

    /**
     * Gets the index for the character, treating space as the 27th character.
     *
     * @param c The character to get the index for.
     * @return The index corresponding to the character.
     */
    private int getIndex(char c) {
        return (c == ' ') ? 26 : c - 'a';
    }
}

/**
 * AutoComplete class reads CSV files to populate the Trie and provides autocomplete suggestions.
 */
@Component
public class AutoComplete {
    private final Trie trie = new Trie();

    /**
     * Initializes the Trie by reading words from CSV files and inserting them.
     */
    @PostConstruct
    public void init() {
        try {
            // Initialize and populate the Trie with CSV data
            String folderPath = "src/main/resources/data/Booking/"; // Replace with the actual folder path
            readCsvFilesAndInsertToTrie(new File(folderPath));
        } catch (IOException | CsvException e) {
            System.err.println("Error initializing Trie with CSV data: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Reads CSV files from a specified folder and inserts words into the Trie.
     *
     * @param folder The folder containing the CSV files.
     * @throws IOException  If an I/O error occurs.
     * @throws CsvException If a CSV parsing error occurs.
     */
    public void readCsvFilesAndInsertToTrie(File folder) throws IOException, CsvException {
        File[] files = folder.listFiles((dir, name) -> name.toLowerCase().endsWith(".csv"));

        if (files == null || files.length == 0) {
            System.err.println("No CSV files found in the directory: " + folder.getPath());
            return;
        }

        for (File file : files) {
            try (CSVReader csvReader = new CSVReader(new FileReader(file, StandardCharsets.UTF_8))) {
                String[] nextLine;
                while ((nextLine = csvReader.readNext()) != null) {
                    for (String word : nextLine) {
                        word = sanitizeInput(word);
                        if (!word.isEmpty()) {
                            trie.insert(word);
                        }
                    }
                }
            } catch (IOException | CsvException e) {
                System.err.println("Error reading CSV file: " + file.getName() + " - " + e.getMessage());
                e.printStackTrace();
            }
        }
    }

    /**
     * Returns a list of autocomplete suggestions for a given prefix.
     *
     * @param prefix The prefix to search for.
     * @return A list of autocomplete suggestions.
     */
    public List<String> getSuggestions(String prefix) {
        if (prefix == null || prefix.trim().isEmpty()) {
            throw new IllegalArgumentException("Prefix cannot be null or empty.");
        }
        try {
            return trie.search(prefix.toLowerCase());
        } catch (ArrayIndexOutOfBoundsException e) {
            System.err.println("Error while searching for prefix: " + prefix + " - " + e.getMessage());
            e.printStackTrace();
            return new ArrayList<>(); // Return an empty list if there's an exception
        }
    }

    /**
     * Sanitizes input by removing non-alphabetical characters and trimming spaces.
     *
     * @param input The input string to sanitize.
     * @return The sanitized string.
     */
    private String sanitizeInput(String input) {
        return input.trim().toLowerCase().replaceAll("[^a-z ]", "");
    }
}
