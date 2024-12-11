package com.hotelhunt.controller;

import com.hotelhunt.service.HotelService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * REST controller for managing hotel-related requests.
 */
@CrossOrigin
@RestController
@RequestMapping("/api/hotels")
public class HotelController {

    // Injecting the HotelService for business logic
    @Autowired
    private HotelService hotelService;

    /**
     * Retrieves all hotels by a given city.
     *
     * @param city The name of the city.
     * @return A ResponseEntity containing a list of hotels in the specified city or an appropriate HTTP status code.
     */
    @GetMapping("/city/{city}")
    public ResponseEntity<?> getAllHotelsByCity(@PathVariable("city") String city) {
        // Validate the city parameter
        if (city == null || city.trim().isEmpty()) {
            return ResponseEntity.badRequest().body("City name must not be null or empty.");
        }
        
        try {
            // Fetch hotels by city
            List<Map<String, String>> hotelsByCity = hotelService.getHotelsByCity(city);
            
            // Check if any hotels are found
            if (hotelsByCity.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NO_CONTENT).body("No hotels found for the specified city.");
            }

            // Return list of hotels with OK status
            return ResponseEntity.ok(hotelsByCity);
        } catch (Exception e) {
            // Handle any unexpected errors
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An error occurred while processing your request.");
        }
    }

    
    /**
     * Retrieves a list of top-rated hotels.
     *
     * @return A ResponseEntity containing a list of top-rated hotels or an appropriate HTTP status code.
     */
    @GetMapping("/top-rated")
    public ResponseEntity<?> getTopRatedHotels() {
        try {
            // Fetch top-rated hotels
            List<Map<String, String>> topRatedHotels = hotelService.getHotelsSortedByRating();

            // Check if any hotels are found
            if (topRatedHotels.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NO_CONTENT).body("No top-rated hotels available at the moment.");
            }

            // Return list of top-rated hotels with OK status
            return ResponseEntity.ok(topRatedHotels);
        } catch (Exception e) {
            // Handle any unexpected errors
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An error occurred while processing your request.");
        }
    }

    /**
     * Retrieves a ranking of cities based on the number of hotels available.
     *
     * @return A map containing city names and their respective rankings.
     */
    @GetMapping("/rank")
    public ResponseEntity<?> getRankedCities() {
        try {
            // Fetch ranked cities
            Map<String, Integer> rankedCities = hotelService.getRankedCities();

            // Check if any rankings are available
            if (rankedCities.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NO_CONTENT).body("No ranking data available at the moment.");
            }

            // Return ranked cities with OK status
            return ResponseEntity.ok(rankedCities);
        } catch (Exception e) {
            // Handle any unexpected errors
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An error occurred while processing your request.");
        }
    }
}
