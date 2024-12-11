package com.hotelhunt.feature;

import org.apache.commons.text.similarity.LevenshteinDistance;
import org.springframework.stereotype.Component;
import jakarta.annotation.PostConstruct;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;

@Component
public class SpellChecker {

    private Trie trie;
    private final Map<String, Integer> wordFrequency = new HashMap<>();
    private static final int MAX_SUGGESTIONS = 5;  // Limit the number of suggestions

    /**
     * Initializes the SpellChecker by loading words from CSV files into the Trie and frequency map.
     */
    @PostConstruct
    private void init() {
        trie = new Trie();
        String folderPath = "src/main/resources/data/Booking/";  // Replace with the actual folder path

        try {
            loadWordsFromCsv(new File(folderPath));
        } catch (IOException e) {
            System.err.println("Error loading words from CSV: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Loads words from CSV files into the Trie and updates the word frequency map.
     *
     * @param folder The folder containing CSV files.
     * @throws IOException If an IO error occurs while reading the files.
     */
    private void loadWordsFromCsv(File folder) throws IOException {
        File[] files = folder.listFiles((dir, name) -> name.toLowerCase().endsWith(".csv"));

        if (files == null || files.length == 0) {
            System.err.println("No CSV files found in the directory: " + folder.getPath());
            return;
        }

        for (File file : files) {
            try (BufferedReader br = new BufferedReader(new FileReader(file))) {
                String line;
                boolean firstLine = true; // Skip headers
                while ((line = br.readLine()) != null) {
                    if (firstLine) {
                        firstLine = false;
                        continue;
                    }
                    String[] words = line.split(",");
                    for (String word : words) {
                        word = word.trim().toLowerCase().replaceAll("[^a-z ]", "");
                        if (!word.isEmpty()) {
                            trie.insert(word);
                            wordFrequency.put(word, wordFrequency.getOrDefault(word, 0) + 1);
                        }
                    }
                }
            } catch (IOException e) {
                System.err.println("Error reading file: " + file.getName() + " - " + e.getMessage());
                e.printStackTrace();
            }
        }
    }

    /**
     * Checks if the provided word is correctly spelled according to the Trie.
     *
     * @param word The word to check.
     * @return True if the word is correctly spelled, false otherwise.
     */
    public boolean isCorrectlySpelled(String word) {
        try {
            return trie.search(word.toLowerCase()).contains(word.toLowerCase());
        } catch (Exception e) {
            System.err.println("Error checking spelling for word: " + word + " - " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Suggests corrections for a misspelled word based on Levenshtein distance and word frequency.
     *
     * @param word The misspelled word.
     * @return A list of suggested corrections.
     */
    public List<String> suggestCorrections(String word) {
        List<String> suggestions = new ArrayList<>();
        if (isCorrectlySpelled(word)) {
            return suggestions;  // Return empty list if the word is correctly spelled
        }

        LevenshteinDistance levenshtein = new LevenshteinDistance();
        PriorityQueue<Map.Entry<String, Integer>> pq = new PriorityQueue<>(
            (entry1, entry2) -> Integer.compare(entry1.getValue(), entry2.getValue())
        );

        for (Map.Entry<String, Integer> entry : wordFrequency.entrySet()) {
            String dictWord = entry.getKey();
            int distance = levenshtein.apply(word, dictWord);
            if (distance <= 2) {  // Consider words within a distance of 2
                pq.offer(new AbstractMap.SimpleEntry<>(dictWord, entry.getValue()));
                if (pq.size() > MAX_SUGGESTIONS) {
                    pq.poll();  // Remove least relevant suggestion
                }
            }
        }

        while (!pq.isEmpty()) {
            suggestions.add(pq.poll().getKey());
        }

        Collections.reverse(suggestions);  // Reverse to get highest frequency suggestions first
        return suggestions;
    }
}
