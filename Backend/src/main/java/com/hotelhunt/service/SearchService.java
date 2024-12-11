package com.hotelhunt.service;

import com.hotelhunt.feature.AutoComplete;
import com.hotelhunt.feature.FrequencyCounter;
import com.hotelhunt.feature.InvertedIndex;
import com.hotelhunt.feature.SearchFrequency;
import com.hotelhunt.feature.SearchFrequencyCounter;
import com.hotelhunt.feature.SpellChecker;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

/**
 * Service class for handling search-related business logic.
 * Provides functionalities such as spell checking, keyword searching,
 * autocomplete suggestions, and frequency analysis.
 */
@Service
public class SearchService {

    private final SpellChecker spellChecker;
    private final AutoComplete autoComplete;
    private final InvertedIndex invertedIndex;
    private final FrequencyCounter frequencyCounter;
    private final SearchFrequency searchFrequencyCounter;

    /**
     * Constructor for SearchService to inject dependencies.
     *
     * @param autoComplete            The AutoComplete instance for generating suggestions.
     * @param frequencyCounter        The FrequencyCounter instance for word frequency analysis.
     * @param invertedIndex           The InvertedIndex instance for keyword-based hotel searching.
     * @param searchFrequencyCounter  The SearchFrequencyCounter instance for tracking search frequencies.
     * @param spellChecker            The SpellChecker instance for spelling verification and corrections.
     */
    @Autowired
    public SearchService(AutoComplete autoComplete, FrequencyCounter frequencyCounter, InvertedIndex invertedIndex,
                         SearchFrequency searchFrequencyCounter, SpellChecker spellChecker) {
        this.spellChecker = spellChecker;
        this.autoComplete = autoComplete;
        this.invertedIndex = invertedIndex;
        this.frequencyCounter = frequencyCounter;
        this.searchFrequencyCounter = searchFrequencyCounter;
    }

    /**
     * Checks if a given word is spelled correctly.
     *
     * @param word The word to check for spelling accuracy.
     * @return True if the word is correctly spelled, otherwise false.
     * @throws IllegalArgumentException if the word is null or empty.
     */
    public boolean isCorrectlySpelled(String word) {
        validateWord(word);
        try {
            return spellChecker.isCorrectlySpelled(word);
        } catch (Exception e) {
            // Log the exception
            System.err.println("An error occurred while checking spelling: " + e.getMessage());
            throw new RuntimeException("Failed to check spelling accuracy.");
        }
    }

    /**
     * Suggests possible corrections for a misspelled word.
     *
     * @param word The word to get correction suggestions for.
     * @return A list of suggested corrections or an empty list if the word is correctly spelled.
     * @throws IllegalArgumentException if the word is null or empty.
     */
    public List<String> suggestCorrections(String word) {
        validateWord(word);
        try {
            return spellChecker.suggestCorrections(word);
        } catch (Exception e) {
            // Log the exception
            System.err.println("An error occurred while suggesting corrections: " + e.getMessage());
            throw new RuntimeException("Failed to suggest corrections.");
        }
    }

    /**
     * Retrieves autocomplete suggestions for a given prefix.
     *
     * @param prefix The prefix to generate autocomplete suggestions for.
     * @return A list of suggested words that match the prefix or an empty list if no matches are found.
     * @throws IllegalArgumentException if the prefix is null or empty.
     */
    public List<String> getSuggestions(String prefix) {
        validateWord(prefix);
        try {
            return autoComplete.getSuggestions(prefix);
        } catch (Exception e) {
            // Log the exception
            System.err.println("An error occurred while retrieving autocomplete suggestions: " + e.getMessage());
            throw new RuntimeException("Failed to retrieve autocomplete suggestions.");
        }
    }

    /**
     * Retrieves the top N most frequent words from the frequency counter.
     *
     * @param n The number of top frequent words to retrieve.
     * @return A list of the top N most frequent words and their counts, or an empty list if no data is available.
     * @throws IllegalArgumentException if n is less than or equal to 0.
     */
    public List<Map.Entry<String, Integer>> getTopFrequentWords(int n) {
        if (n <= 0) {
            throw new IllegalArgumentException("The number of top frequent words (n) must be greater than 0.");
        }
        try {
            return frequencyCounter.getTopFrequentWords(n);
        } catch (Exception e) {
            // Log the exception
            System.err.println("An error occurred while retrieving top frequent words: " + e.getMessage());
            throw new RuntimeException("Failed to retrieve top frequent words.");
        }
    }

    /**
     * Retrieves the frequency of a specific word.
     *
     * @param word The word to check for its frequency.
     * @return The frequency count of the word or 0 if the word does not exist.
     * @throws IllegalArgumentException if the word is null or empty.
     */
    public int getWordFrequency(String word) {
        validateWord(word);
        try {
            return frequencyCounter.getWordFrequency(word);
        } catch (Exception e) {
            // Log the exception
            System.err.println("An error occurred while retrieving word frequency: " + e.getMessage());
            throw new RuntimeException("Failed to retrieve word frequency.");
        }
    }

    /**
     * Searches a keyword and updates its frequency count.
     *
     * @param keyword The keyword to search and update.
     * @return The updated frequency count of the keyword.
     * @throws IllegalArgumentException if the keyword is null or empty.
     */
    public int searchAndUpdateFrequency(String keyword) {
        validateWord(keyword);
        try {
            return searchFrequencyCounter.searchAndUpdateFrequency(keyword);
        } catch (Exception e) {
            // Log the exception
            System.err.println("An error occurred while searching and updating frequency: " + e.getMessage());
            throw new RuntimeException("Failed to search and update frequency.");
        }
    }

    /**
     * Searches for hotels based on provided keywords using an inverted index.
     *
     * @param keywords The keywords to search for hotels.
     * @return A list of maps containing hotel information that matches the keywords, or an empty list if no matches are found.
     * @throws IllegalArgumentException if the keywords are null or empty.
     */
    public List<Map<String, String>> getHotelsByKeywords(String keywords) {
        validateWord(keywords);
        try {
            return invertedIndex.searchHotelsByKeywords(keywords);
        } catch (Exception e) {
            // Log the exception
            System.err.println("An error occurred while retrieving hotels by keywords: " + e.getMessage());
            throw new RuntimeException("Failed to retrieve hotels by keywords.");
        }
    }

    /**
     * Retrieves the top N most frequently searched keywords.
     *
     * @param n The number of top frequent searches to retrieve.
     * @return A list of top N frequently searched keywords and their counts, or an empty list if no data is available.
     * @throws IllegalArgumentException if n is less than or equal to 0.
     */
    public List<Map.Entry<String, Integer>> getTopFrequentSearches(int n) {
        if (n <= 0) {
            throw new IllegalArgumentException("The number of top frequent searches (n) must be greater than 0.");
        }
        try {
            return searchFrequencyCounter.getTopFrequentSearches(n);
        } catch (Exception e) {
            // Log the exception
            System.err.println("An error occurred while retrieving top frequent searches: " + e.getMessage());
            throw new RuntimeException("Failed to retrieve top frequent searches.");
        }
    }

    /**
     * Validates if a word is not null or empty.
     *
     * @param word The word to validate.
     * @throws IllegalArgumentException if the word is null or empty.
     */
    private void validateWord(String word) {
        if (word == null || word.trim().isEmpty()) {
            throw new IllegalArgumentException("Input must not be null or empty.");
        }
    }
}
