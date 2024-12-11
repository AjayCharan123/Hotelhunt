package com.hotelhunt.repository;

import com.opencsv.CSVReader;
import com.opencsv.exceptions.CsvValidationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Repository;

import java.io.*;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Repository class responsible for reading hotel data from CSV files.
 * Provides functionalities to fetch hotels by city, keywords, and sort by rating.
 */
@Repository
public class HotelRepository {

    private static final String CSV_DIRECTORY = "classpath:data/Booking/";

    @Autowired
    private ResourceLoader resourceLoader;

    /**
     * Retrieves all hotels in a specified city by reading CSV files.
     *
     * @param city The name of the city to search for hotels.
     * @return A list of hotels in the specified city, represented as maps with column headers as keys.
     * @throws RuntimeException if the directory does not exist or an error occurs while reading the files.
     */
    public List<Map<String, String>> getAllHotelsInCity(String city) {
        if (city == null || city.trim().isEmpty()) {
            throw new IllegalArgumentException("City name cannot be null or empty.");
        }

        List<Map<String, String>> allHotels = new ArrayList<>();
        Resource resource = resourceLoader.getResource(CSV_DIRECTORY);

        if (!resource.exists()) {
            throw new RuntimeException("Directory not found: " + CSV_DIRECTORY);
        }

        try {
            File[] files = resource.getFile().listFiles();
            if (files != null) {
                for (File file : files) {
                    // Check if filename (without extension) matches the city
                    String filename = file.getName();
                    String fileCityName = filename.substring(0, filename.lastIndexOf('.'));
                    if (fileCityName.equalsIgnoreCase(city)) {
                        // Read and process the file
                        List<Map<String, String>> hotelsInFile = readHotelsFromFile(file);
                        allHotels.addAll(hotelsInFile);
                    }
                }
            }
        } catch (IOException | CsvValidationException e) {
            System.err.println("An error occurred while reading CSV files: " + e.getMessage());
            throw new RuntimeException("Error reading CSV files", e);
        }

        return allHotels;
    }

    /**
     * Retrieves all hotels from all files and sorts them by rating in descending order.
     *
     * @return A list of hotels sorted by rating.
     * @throws RuntimeException if the directory does not exist or an error occurs while reading the files.
     */
    public List<Map<String, String>> getHotelsSortedByRating() {
        List<Map<String, String>> allHotels = new ArrayList<>();
        Resource resource = resourceLoader.getResource(CSV_DIRECTORY);

        if (!resource.exists()) {
            throw new RuntimeException("Directory not found: " + CSV_DIRECTORY);
        }

        try {
            File[] files = resource.getFile().listFiles();
            if (files != null) {
                for (File file : files) {
                    if (file.getName().endsWith(".csv")) {
                        List<Map<String, String>> hotelsInFile = readHotelsFromFile(file);
                        allHotels.addAll(hotelsInFile);
                    }
                }
            }
        } catch (IOException | CsvValidationException e) {
            System.err.println("An error occurred while reading CSV files: " + e.getMessage());
            throw new RuntimeException("Error reading CSV files", e);
        }

        // Sort by rating (assuming the column header is "rating")
        return allHotels.stream()
                .sorted((h1, h2) -> {
                    String rating1 = h1.get("rating");
                    String rating2 = h2.get("rating");
                    if (rating1 == null || rating2 == null) {
                        return 0; // Treat null ratings as equal
                    }
                    try {
                        return Double.compare(Double.parseDouble(rating2), Double.parseDouble(rating1)); // Sort in descending order
                    } catch (NumberFormatException e) {
                        System.err.println("Invalid number format for rating: " + e.getMessage());
                        return 0;
                    }
                })
                .collect(Collectors.toList());
    }

    /**
     * Retrieves all hotels from all files that match the given keyword in any field.
     *
     * @param keyword The keyword to search for in hotel records.
     * @return A list of hotels that match the keyword.
     * @throws IllegalArgumentException if the keyword is null or empty.
     * @throws RuntimeException if the directory does not exist or an error occurs while reading the files.
     */
    public List<Map<String, String>> getHotelsByKeyword(String keyword) {
        if (keyword == null || keyword.trim().isEmpty()) {
            throw new IllegalArgumentException("Keyword cannot be null or empty.");
        }

        List<Map<String, String>> matchingHotels = new ArrayList<>();
        Resource resource = resourceLoader.getResource(CSV_DIRECTORY);

        if (!resource.exists()) {
            throw new RuntimeException("Directory not found: " + CSV_DIRECTORY);
        }

        try {
            File[] files = resource.getFile().listFiles();
            if (files != null) {
                for (File file : files) {
                    if (file.getName().endsWith(".csv")) {
                        List<Map<String, String>> hotelsInFile = readHotelsFromFile(file);
                        for (Map<String, String> hotel : hotelsInFile) {
                            if (hotel.values().stream().anyMatch(value -> value.toLowerCase().contains(keyword.toLowerCase()))) {
                                matchingHotels.add(hotel);
                            }
                        }
                    }
                }
            }
        } catch (IOException | CsvValidationException e) {
            System.err.println("An error occurred while reading CSV files: " + e.getMessage());
            throw new RuntimeException("Error reading CSV files", e);
        }

        return matchingHotels;
    }

    /**
     * Reads hotel data from a specific CSV file and converts it to a list of maps with column headers as keys.
     *
     * @param file The CSV file to read.
     * @return A list of hotel records as maps.
     * @throws IOException            if an error occurs during file reading.
     * @throws CsvValidationException if the CSV file is not valid.
     */
    private List<Map<String, String>> readHotelsFromFile(File file) throws IOException, CsvValidationException {
        List<Map<String, String>> hotelData = new ArrayList<>();

        try (Reader reader = new InputStreamReader(new FileInputStream(file));
             CSVReader csvReader = new CSVReader(reader)) {
            String[] headers = csvReader.readNext();  // Read the header line
            if (headers == null) {
                throw new RuntimeException("CSV file is empty: " + file.getName());
            }
            String[] values;
            while ((values = csvReader.readNext()) != null) {
                if (values.length == headers.length) {
                    Map<String, String> hotelMap = new HashMap<>();
                    for (int i = 0; i < headers.length; i++) {
                        hotelMap.put(headers[i].trim(), values[i].trim());
                    }
                    hotelData.add(hotelMap);
                } else {
                    //System.err.println("Row skipped due to mismatch in column count: " + Arrays.toString(values));
                }
            }
        }
        return hotelData;
    }
}
