# 🏨 HotelHunt - Hotel Price Analysis Platform

HotelHunt is a full-stack Java web application that helps users find the best hotel deals by crawling multiple travel websites and comparing prices and ratings. It combines real-time data scraping with intelligent search features like spell-check, autocomplete, and keyword-based filtering.

## 🚀 Features

- 🔍 **Web Crawling & Data Extraction**: Automated scraping from sites like Booking.com, Trivago, and Hotwire using Selenium.
- ✨ **Spell Checking & Autocomplete**: Corrects typos in city names and provides real-time search suggestions using Trie and Edit-Distance algorithms.
- 🔑 **Keyword-Based Search**: Implements inverted indexing and regex-based pattern recognition for efficient search.
- 📊 **Filter & Ranking**: Filters hotels by price, rating, and amenities. Includes frequency-based page ranking and user behavior tracking.
- 📈 **Search Analytics**: Tracks the frequency of searched terms and filters using hash maps and min-heaps.
- 🖼️ **Responsive UI**: Frontend built with HTML, CSS, and Bootstrap for seamless user interaction.
- 🧠 **Backend Architecture**: Developed in Java with Spring Boot. REST APIs handle hotel data, search, and filters.
- 🗃️ **Data Storage**: Parsed hotel data is stored in CSV files and processed for visualization.

## 🧩 Technologies Used

**Frontend:** HTML, CSS, Bootstrap  
**Backend:** Java, Spring Boot  
**Data Extraction:** Selenium WebDriver  
**Data Structures:** Trie, HashMap, Priority Queue (Min-Heap)  
**Validation:** Regex  
**Tools:** Eclipse, CSVReader, Maven

## 📂 Project Structure

- `HotelController.java` – APIs for retrieving hotel info by city and rating.
- `SearchController.java` – APIs for search queries, frequency count, and autocomplete.
- `SearchService.java` – Business logic for keyword matching, spell-check, and search.
- `Filters.java`, `FrequencyCount.java` – Core features for filtering and analytics.


## 🧠 Contributors

- Ajay Charan – Frequency count, filters, controller & backend logic  
- Sumandeep Kaur – Page ranking, GUI, most-rated hotel logic  
- Priyam Suratwala – Word completion, pattern detection  
- Aanchal Jariwala – Spell check, inverted index  
- Gurpreet Singh – Search frequency, data validation

## 📌 How to Run

1. Clone the repository
2. Start the Spring Boot application via `mvn spring-boot:run`
3. Open `index.html` to access the frontend
4. Ensure ChromeDriver and Selenium setup is properly configured

## 📄 License

This project is for educational purposes only. No commercial use intended.

---
