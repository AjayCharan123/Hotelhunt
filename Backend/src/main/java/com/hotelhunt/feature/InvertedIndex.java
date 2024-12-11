package com.hotelhunt.feature;

import com.opencsv.CSVReader;
import com.opencsv.exceptions.CsvValidationException;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Component;

import java.io.*;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * InvertedIndex class builds an inverted index for hotel data from CSV files.
 * It supports searching for hotels by single or multiple keywords with prefix matching.
 */
@Component
public class InvertedIndex {

    private final ResourceLoader resourceLoader;
    private final Map<String, List<Integer>> invertedIndex; // Stores the inverted index
    private final List<Map<String, String>> allHotels; // Stores all hotels data

    /**
     * Constructor for InvertedIndex. Initializes the resource loader and index.
     *
     * @param resourceLoader The ResourceLoader to load CSV files.
     */
    public InvertedIndex(ResourceLoader resourceLoader) {
        this.resourceLoader = resourceLoader;
        this.invertedIndex = new HashMap<>();
        this.allHotels = new ArrayList<>();
        loadAndIndexHotels();
    }

    /**
     * Loads hotel data from CSV files and builds the inverted index.
     */
    private void loadAndIndexHotels() {
        try {
            // Load the directory containing CSV files
            Resource resource = resourceLoader.getResource("classpath:data/Booking/");
            File folder = resource.getFile();

            if (!folder.exists() || !folder.isDirectory()) {
                throw new RuntimeException("Directory not found: " + folder.getAbsolutePath());
            }

            // Process each CSV file in the directory
            File[] csvFiles = folder.listFiles((dir, name) -> name.endsWith(".csv"));
            if (csvFiles != null) {
                for (File csvFile : csvFiles) {
                    List<Map<String, String>> hotelsInFile = readHotelsFromFile(csvFile);
                    allHotels.addAll(hotelsInFile);
                }
            } else {
                System.err.println("No CSV files found in directory: " + folder.getPath());
            }

            // Build the inverted index from all hotels
            buildInvertedIndex();

        } catch (IOException | CsvValidationException e) {
            System.err.println("Error loading and indexing hotels: " + e.getMessage());
            e.printStackTrace();
            throw new RuntimeException("Error loading and indexing hotels", e);
        }
    }

    /**
     * Reads hotel data from a given CSV file.
     *
     * @param file The CSV file to read from.
     * @return List of hotel records as maps.
     * @throws IOException            If an I/O error occurs.
     * @throws CsvValidationException If a CSV validation error occurs.
     */
    private List<Map<String, String>> readHotelsFromFile(File file) throws IOException, CsvValidationException {
        List<Map<String, String>> hotelData = new ArrayList<>();

        try (Reader reader = new InputStreamReader(new FileInputStream(file));
             CSVReader csvReader = new CSVReader(reader)) {

            String[] headers = csvReader.readNext();  // Read the header row
            if (headers == null) {
                return hotelData;
            }

            String[] values;
            while ((values = csvReader.readNext()) != null) {
                if (values.length == headers.length) {
                    Map<String, String> hotelMap = new HashMap<>();
                    for (int i = 0; i < headers.length; i++) {
                        hotelMap.put(headers[i].trim(), values[i].trim());
                    }
                    hotelData.add(hotelMap);
                }
            }
        }

        return hotelData;
    }

    /**
     * Builds an inverted index where each word points to a list of document IDs containing that word.
     */
    private void buildInvertedIndex() {
        Pattern pattern = Pattern.compile("\\w+"); // Pattern to match words

        for (int docId = 0; docId < allHotels.size(); docId++) {
            Map<String, String> hotel = allHotels.get(docId);

            // Tokenize and index each hotel's fields
            for (String value : hotel.values()) {
                Matcher matcher = pattern.matcher(value.toLowerCase());
                while (matcher.find()) {
                    String word = matcher.group();
                    // Index all prefixes of the word for partial matching
                    for (int i = 1; i <= word.length(); i++) {
                        String prefix = word.substring(0, i);
                        invertedIndex.computeIfAbsent(prefix, k -> new ArrayList<>()).add(docId);
                    }
                }
            }
        }
    }

    /**
     * Searches for hotels containing the specified keyword.
     * Supports prefix matching.
     *
     * @param keyword The keyword to search for.
     * @return List of matching hotel records.
     */
    public List<Map<String, String>> searchHotelsByKeyword(String keyword) {
        String keywordLower = keyword.toLowerCase();

        // Retrieve document IDs for all prefix matches
        List<Integer> docIds = invertedIndex.keySet().stream()
                .filter(word -> word.startsWith(keywordLower))
                .flatMap(word -> invertedIndex.get(word).stream())
                .distinct()
                .collect(Collectors.toList());

        return docIds.stream()
                .map(allHotels::get)
                .collect(Collectors.toList());
    }

    /**
     * Searches for hotels containing multiple keywords using AND logic with prefix matching.
     *
     * @param keywordString The string of keywords to search for.
     * @return List of matching hotel records.
     */
    public List<Map<String, String>> searchHotelsByKeywords(String keywordString) {
        // Split the input string into individual keywords and remove duplicates
        List<String> keywords = Arrays.stream(keywordString.split("\\s+"))
                .map(String::toLowerCase) // Convert keywords to lowercase
                .distinct() // Remove duplicates
                .collect(Collectors.toList());

        if (keywords.isEmpty()) {
            return Collections.emptyList();
        }

        // Retrieve document IDs for prefix matches of each keyword
        List<Set<Integer>> listOfDocIdSets = keywords.stream()
                .map(keyword -> invertedIndex.keySet().stream()
                        .filter(word -> word.startsWith(keyword))
                        .flatMap(word -> invertedIndex.get(word).stream())
                        .collect(Collectors.toSet()))
                .collect(Collectors.toList());

        // Find common document IDs across all keyword sets (AND logic)
        Set<Integer> resultDocIds = new HashSet<>(listOfDocIdSets.get(0));
        for (Set<Integer> docIdSet : listOfDocIdSets.subList(1, listOfDocIdSets.size())) {
            resultDocIds.retainAll(docIdSet);
        }

        return resultDocIds.stream()
                .map(allHotels::get)
                .collect(Collectors.toList());
    }
}


