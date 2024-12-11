package com.hotelhunt.controller;

import com.hotelhunt.service.SearchService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * REST controller for managing search-related requests.
 */
@CrossOrigin
@RestController
@RequestMapping("/api/search")
public class SearchController {

    // Injecting the SearchService for business logic
    @Autowired
    private SearchService searchService;
    
    /**
     * Retrieves all hotels matching a given keyword.
     *
     * @param keyword The keyword to search for hotels.
     * @return A ResponseEntity containing a list of hotels matching the keyword or an appropriate HTTP status code.
     */
    @GetMapping("/keyword")
    public ResponseEntity<?> getAllHotelsByKeywords(@RequestParam("keyword") String keyword) {
        // Validate the keyword parameter
        if (keyword == null || keyword.trim().isEmpty()) {
            return ResponseEntity.badRequest().body("Keyword must not be null or empty.");
        }

        try {
            // Fetch hotels by keyword
            List<Map<String, String>> hotelsByKeyword = searchService.getHotelsByKeywords(keyword);
            
            // Check if any hotels are found
            if (hotelsByKeyword.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NO_CONTENT).body("No hotels found for the specified keyword.");
            }

            // Return list of hotels with OK status
            return ResponseEntity.ok(hotelsByKeyword);
        } catch (Exception e) {
            // Handle any unexpected errors
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An error occurred while processing your request.");
        }
    }


    /**
     * Endpoint to suggest corrections for a given word.
     *
     * @param word The word to check for spelling suggestions.
     * @return A ResponseEntity containing a list of suggested corrections or an appropriate HTTP status code.
     */
    @GetMapping("/suggest")
    public ResponseEntity<?> suggestCorrections(@RequestParam("word") String word) {
        // Validate the word parameter
        if (word == null || word.trim().isEmpty()) {
            return ResponseEntity.badRequest().body("Word must not be null or empty.");
        }
        
        try {
            // Check if the word is correctly spelled
            if (searchService.isCorrectlySpelled(word)) {
                return ResponseEntity.ok(List.of());  // Return an empty list if the word is correct
            } else {
                // Return suggestions for misspelled words
                List<String> suggestions = searchService.suggestCorrections(word);
                return ResponseEntity.ok(suggestions);
            }
        } catch (Exception e) {
            // Handle any unexpected errors
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An error occurred while processing your request.");
        }
    }

    /**
     * Endpoint to get autocomplete suggestions for a given prefix.
     *
     * @param prefix The prefix to get autocomplete suggestions for.
     * @return A ResponseEntity containing a list of autocomplete suggestions or an appropriate HTTP status code.
     */
    @GetMapping("/autocomplete")
    public ResponseEntity<?> getSuggestions(@RequestParam("prefix") String prefix) {
        // Validate the prefix parameter
        if (prefix == null || prefix.trim().isEmpty()) {
            return ResponseEntity.badRequest().body("Prefix must not be null or empty.");
        }

        try {
            // Retrieve autocomplete suggestions
            List<String> autocompleteSuggestions = searchService.getSuggestions(prefix);
            return ResponseEntity.ok(autocompleteSuggestions);
        } catch (Exception e) {
            // Handle any unexpected errors
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An error occurred while processing your request.");
        }
    }

    /**
     * Endpoint to retrieve the top N most frequent words.
     *
     * @param count The number of top frequent words to retrieve (default is 50).
     * @return A ResponseEntity containing a list of the top N most frequent words and their counts or an appropriate HTTP status code.
     */
    @GetMapping("/frequency/top")
    public ResponseEntity<?> getTopFrequentWords(@RequestParam(value = "n", defaultValue = "50") int count) {
        if (count <= 0) {
            return ResponseEntity.badRequest().body("The count must be greater than 0.");
        }

        try {
            // Retrieve the top N most frequent words
            List<Map.Entry<String, Integer>> topFrequentWords = searchService.getTopFrequentWords(count);

            // Check if there are any results
            if (topFrequentWords.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NO_CONTENT).body("No frequent words found.");
            }

            return ResponseEntity.ok(topFrequentWords);
        } catch (Exception e) {
            // Handle any unexpected errors
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An error occurred while processing your request.");
        }
    }

    /**
     * Endpoint to retrieve the frequency of a specific word.
     *
     * @param word The word to check the frequency for.
     * @return A ResponseEntity containing the frequency of the word or an appropriate HTTP status code.
     */
    @GetMapping("/frequency/word")
    public ResponseEntity<?> getWordFrequency(@RequestParam("word") String word) {
        // Validate the word parameter
        if (word == null || word.trim().isEmpty()) {
            return ResponseEntity.badRequest().body("Word must not be null or empty.");
        }

        try {
            // Retrieve the frequency of the word
            int frequency = searchService.getWordFrequency(word);
            return ResponseEntity.ok(frequency);
        } catch (Exception e) {
            // Handle any unexpected errors
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An error occurred while processing your request.");
        }
    }

    /**
     * Endpoint to retrieve the top N most frequent searches.
     *
     * @param count The number of top frequent searches to retrieve (default is 10).
     * @return A ResponseEntity containing a list of the top N most frequent searches and their counts or an appropriate HTTP status code.
     */
    @GetMapping("/searches/top")
    public ResponseEntity<?> getTopSearches(@RequestParam(value = "n", defaultValue = "10") int count) {
        if (count <= 0) {
            return ResponseEntity.badRequest().body("The count must be greater than 0.");
        }

        try {
            // Retrieve the top N most frequent searches
            List<Map.Entry<String, Integer>> topFrequentSearches = searchService.getTopFrequentSearches(count);

            // Check if there are any results
            if (topFrequentSearches.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NO_CONTENT).body("No frequent searches found.");
            }

            return ResponseEntity.ok(topFrequentSearches);
        } catch (Exception e) {
            // Handle any unexpected errors
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An error occurred while processing your request.");
        }
    }

    /**
     * Endpoint to retrieve the search frequency of a specific word.
     *
     * @param word The word to check the search frequency for.
     * @return A ResponseEntity containing the search frequency of the word or an appropriate HTTP status code.
     */
    @GetMapping("/searchfrequency/word")
    public ResponseEntity<?> getWordSearchFrequency(@RequestParam("word") String word) {
        // Validate the word parameter
        if (word == null || word.trim().isEmpty()) {
            return ResponseEntity.badRequest().body("Word must not be null or empty.");
        }

        try {
            // Retrieve the search frequency of the word
            int searchFrequency = searchService.searchAndUpdateFrequency(word);
            return ResponseEntity.ok(searchFrequency);
        } catch (Exception e) {
            // Handle any unexpected errors
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An error occurred while processing your request.");
        }
    }
}
