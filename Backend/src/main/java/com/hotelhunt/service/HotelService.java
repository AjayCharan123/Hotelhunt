package com.hotelhunt.service;

import com.hotelhunt.feature.PageRanker;
import com.hotelhunt.repository.HotelRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

/**
 * Service class for handling hotel-related business logic.
 */
@Service
public class HotelService {

    // Injecting the HotelRepository for data access operations
    @Autowired
    private HotelRepository hotelRepository;

    // Instance of PageRanker for calculating city ranking
    private final PageRanker pageRanker;

    /**
     * Constructor for HotelService to inject dependencies.
     *
     * @param pageRanker The PageRanker instance.
     */
    @Autowired
    public HotelService(PageRanker pageRanker) {
        this.pageRanker = pageRanker;
    }

    /**
     * Retrieves a list of hotels located in a specified city.
     *
     * @param city The name of the city for which hotels are to be retrieved.
     * @return A list of maps containing hotel information or an empty list if no hotels are found.
     * @throws IllegalArgumentException if the city name is null or empty.
     */
    public List<Map<String, String>> getHotelsByCity(String city) {
        // Validate the city input
        if (city == null || city.trim().isEmpty()) {
            throw new IllegalArgumentException("City name must not be null or empty.");
        }

        try {
            // Retrieve hotels from the repository based on city
            return hotelRepository.getAllHotelsInCity(city);
        } catch (Exception e) {
            // Log the exception (logging framework assumed to be configured)
            System.err.println("An error occurred while retrieving hotels by city: " + e.getMessage());
            
            // Handle specific exceptions as needed
            // Example: Handle database connectivity issues, data access exceptions, etc.

            // Rethrow or wrap the exception as a custom service exception
            throw new RuntimeException("Failed to retrieve hotels for the specified city.");
        }
    }

    /**
     * Retrieves a list of hotels sorted by their rating in descending order.
     *
     * @return A list of maps containing hotel information sorted by rating, or an empty list if no hotels are available.
     */
    public List<Map<String, String>> getHotelsSortedByRating() {
        try {
            // Retrieve hotels sorted by rating from the repository
            return hotelRepository.getHotelsSortedByRating();
        } catch (Exception e) {
            // Log the exception
            System.err.println("An error occurred while retrieving hotels sorted by rating: " + e.getMessage());

            // Handle specific exceptions as needed
            // Example: Handle database connectivity issues, data access exceptions, etc.

            // Rethrow or wrap the exception as a custom service exception
            throw new RuntimeException("Failed to retrieve hotels sorted by rating.");
        }
    }

    /**
     * Retrieves a map of city names and their respective frequencies, ranked based on popularity.
     *
     * @return A map where the key is the city name and the value is the frequency of occurrences.
     * @throws RuntimeException if the operation fails.
     */
    public Map<String, Integer> getRankedCities() {
        try {
            // Retrieve ranked cities from the PageRanker
            return pageRanker.getCityNameFrequencies();
        } catch (Exception e) {
            // Log the exception
            System.err.println("An error occurred while retrieving ranked cities: " + e.getMessage());

            // Handle specific exceptions as needed
            // Example: Handle data processing exceptions, etc.

            // Rethrow or wrap the exception as a custom service exception
            throw new RuntimeException("Failed to retrieve ranked cities.");
        }
    }
}
