# 🏨 HotelHunt – Smart Hotel Price Aggregator

## 📖 About HotelHunt  
HotelHunt is a smart travel assistant that helps users compare real-time hotel prices across multiple booking platforms in one place — no need to switch tabs or visit different websites.

HotelHunt is designed for students and budget-conscious travelers who want to find the best hotel deals without jumping across multiple websites. Often, hotel prices vary between platforms like Booking.com and Trivago, and some hotels appear on one site but not the others. This makes it hard and time-consuming to compare options. HotelHunt solves this by collecting real-time hotel prices and details from different platforms and showing them all in one place, with helpful features like autocomplete, spell correction, and smart filters for price, rating, and amenities.

I used Selenium to automatically browse hotel websites, collect data like hotel names, ratings, and prices, and save it into CSV files. This approach saved time and ensured accurate, updated information. The backend, built with Spring Boot, reads this data and applies logic for search, filtering, and ranking. Features like autocomplete for city names and spell correction help users find what they’re looking for quickly, even with typos. HotelHunt makes hotel search easier, faster, and smarter.

---
## 📂 Folder Structure  
/scraper → Selenium scripts to scrape hotel websites <br>
/data → CSV files with scraped hotel data <br>
/backend → Spring Boot backend (controllers, services) <br>
/frontend → HTML, CSS, Bootstrap files <br>
/hotelhunt.csv → Consolidated data file used as backend input <br>

---

## 🔑 Key Features  
✅ **Real-Time Price Aggregation**  
Selenium scrapes prices, ratings, and hotel names from Booking.com and Trivago. Data is saved in CSV and processed by the backend.

✅ **Spell Correction + Smart Search**  
If users mistype (e.g., “Tornto” instead of “Toronto”), the backend auto-corrects it and still shows results.

✅ **Autocomplete & Suggestions**  
Typing “Van…” shows “Vancouver”, “Vanier”, etc. instantly.  

✅ **Filtering & Ranking**  
Sort and filter by price, rating, and amenities. Also, see most searched cities and hotels.

✅ **Mobile-Friendly UI**  
Built with Bootstrap for clean look and responsive design.

---

## 🗃️ Data Pipeline Overview  
1. **Selenium WebDriver** simulates a real user visiting hotel booking sites.  
2. It extracts hotel data and saves it in a CSV file (`hotelhunt.csv`).  
3. The **Spring Boot backend** reads this CSV and exposes REST APIs.  
4. The **Frontend UI** (HTML + CSS + Bootstrap) calls the backend and displays results with filters, suggestions, and corrections.

---

## 🖥️ Frontend Interface  
A clean, responsive UI allows users to:
- Enter city, check-in, and check-out dates  
- Apply filters like price, rating, and amenities  
- See suggestions and corrected results  
- View the most popular hotel options  

Built using HTML, CSS, and Bootstrap with instant response from backend APIs.

---

## ⚙️ Technologies Used  
| Component        | Technology              |
|------------------|--------------------------|
| Web Scraping     | Selenium WebDriver       |
| Backend          | Java + Spring Boot       |
| Frontend         | HTML, CSS, Bootstrap     |
| Data Store       | CSV files (via Selenium) |
| APIs             | RESTful API (Spring)     |
| Spell Correction | Custom dictionary-based  |
| Autocomplete     | Frequency-ranked dataset |

---

## 📊 System Performance  
| Component            | Description                                      | Metric / Behavior                           |
|----------------------|--------------------------------------------------|---------------------------------------------|
| Scraping             | Data collected via Selenium                     | ~3–5 seconds per site (local)               |
| Backend API Response | Query + spell check + filters                   | ~200–300ms                                  |
| Search Accuracy      | Auto-corrects and ranks relevant hotels         | Top 5 hotels returned                       |
| Autocomplete Speed   | Suggestions while typing                        | Instant (<100ms)                            |
| UI Latency           | Frontend to backend result rendering            | Real-time display (perceived <1s)           |

---

## 📝 Conclusion  
HotelHunt brings together automation, smart search, and modern web development to solve a real-world student problem—comparing hotel prices without visiting multiple websites.

With scraping automation, a logic-heavy backend, and a responsive UI, it demonstrates how simple technologies can come together to deliver a powerful, time-saving travel tool.
