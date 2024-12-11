// import { Component, Input, OnInit } from '@angular/core';
// import { HotelService } from '../../services/hotel.service';
// import { ActivatedRoute } from '@angular/router';
// import { Hotel } from '../../models/Hotel';
// import { CommonModule } from '@angular/common';
// import { FormBuilder, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';

// @Component({
//   selector: 'app-hotels-grid',
//   standalone: true,
//   imports: [CommonModule, ReactiveFormsModule],
//   templateUrl: './hotels-grid.component.html',
//   styleUrl: './hotels-grid.component.css'
// })
// export class HotelsGridComponent implements OnInit {
//   public hotels: Hotel[] = [];
//   @Input() mostRated: boolean = false;
//   city: string = this.actRoute.snapshot.params['city'];
//   filterForm!: FormGroup;

//   constructor(
//     private hotelService: HotelService, public actRoute: ActivatedRoute, private formBuilder: FormBuilder
//   ) {}

//   ngOnInit(): void {

//     this.filterForm = this.formBuilder.group({
//       minPrice: [null, [Validators.min(0)]],
//       maxPrice: [null, [Validators.min(0)]],
//       rating: [null, [Validators.min(0)]],
//       sortBy: ['default']
//     });


//     if (this.mostRated) {
//       this.getMostRatedHotels();
//     } else if (this.city) {
//       this.getHotelsByCity(this.city);
//     } else {
//       console.log("Error Occured");
//     }
//   }

//   getMostRatedHotels(): void {
//     this.hotelService.getMostRatedHotels().subscribe((data: Hotel[]) => {
//       this.hotels = this.filterHotelsWithRating10(data);
//     });
//   }

//   getHotelsByCity(city: string): void {
//     this.hotelService.getHotelsByCity(city).subscribe(data => {
//       this.hotels = data.hotels;
//       //this.hotelCount = data.count;
//     });
//   }


//   private filterHotelsWithRating10(hotels: Hotel[]): Hotel[] {
//     return hotels.filter(hotel => this.parseRating(hotel.Rating) === 10);
//   }

//   /**
//    * Convert rating string to number.
//    *
//    * @param ratingStr - The rating as a string
//    * @return The rating as a number, or 0 if invalid
//    */
//   private parseRating(ratingStr: string): number {
//     const parsed = parseFloat(ratingStr.trim());
//     return isNaN(parsed) ? 0 : parsed;
//   }
// }

import { Component, Input, OnInit } from '@angular/core';
import { HotelService } from '../../services/hotel.service';
import { ActivatedRoute } from '@angular/router';
import { Hotel } from '../../models/Hotel';
import { CommonModule } from '@angular/common';
import { FormBuilder, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';

@Component({
  selector: 'app-hotels-grid',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule],
  templateUrl: './hotels-grid.component.html',
  styleUrls: ['./hotels-grid.component.css'] // Corrected "styleUrls"
})
export class HotelsGridComponent implements OnInit {
  public hotels: Hotel[] = [];
  private originalHotels: Hotel[] = []; // To store the original list of hotels for resetting purposes
  @Input() mostRated: boolean = false;
  city: string = this.actRoute.snapshot.params['city'];
  filterForm!: FormGroup;

  constructor(
    private hotelService: HotelService,
    public actRoute: ActivatedRoute,
    private formBuilder: FormBuilder
  ) { }

  ngOnInit(): void {
    // Initialize the filter form with default values
    this.filterForm = this.formBuilder.group({
      minPrice: [null, [Validators.min(1)]], // Default minPrice = 10
      maxPrice: [null, [Validators.min(1)]],
      rating: ["", [Validators.min(0)]], // Default rating = 4
      sortBy: ['default']
    });

    // Listen to form changes and validate negative values
    this.filterForm.valueChanges.subscribe(() => {
      this.checkForNegativeValues();
    });

    // Load the hotels based on the component's state
    if (this.mostRated) {
      this.getMostRatedHotels();
    } else if (this.city) {
      this.getHotelsByCity(this.city);
    } else {
      console.log('Error Occurred');
    }
  }

  // Method to reload page if negative values are entered
  private checkForNegativeValues(): void {
    const { minPrice, maxPrice } = this.filterForm.value;

    // Reload if negative values are found
    if (minPrice < 0 || (maxPrice !== null && maxPrice < 0)) {
      alert("Negative values are not allowed. Reloading the page to reset filters.");
      window.location.reload();
    }
  }

  /**
   * Get most rated hotels with a rating of 10.
   */
  getMostRatedHotels(): void {
    this.hotelService.getMostRatedHotels().subscribe((data: Hotel[]) => {
      this.hotels = this.filterHotelsWithRating10(data);
      this.originalHotels = [...this.hotels]; // Store original hotels
    });
  }

  /**
   * Get hotels by city.
   *
   * @param city - The city to search hotels in
   */
  getHotelsByCity(city: string): void {
    this.hotelService.getHotelsByCity(city).subscribe(data => {
      this.hotels = data.hotels;
      this.originalHotels = [...this.hotels]; // Store original hotels
      //this.hotelCount = data.count;
    });
  }

  /**
   * Filter hotels with a perfect rating of 10.
   *
   * @param hotels - The list of hotels to filter
   * @return Filtered hotels with a rating of 10
   */
  private filterHotelsWithRating10(hotels: Hotel[]): Hotel[] {
    return hotels.filter(hotel => this.parseRating(hotel.Rating) === 10);
  }

  /**
   * Parse rating string to number.
   *
   * @param ratingStr - The rating as a string
   * @return The parsed rating as a number, or 0 if invalid
   */
  private parseRating(ratingStr: string): number {
    const parsed = parseFloat(ratingStr.trim());
    return isNaN(parsed) ? 0 : parsed;
  }

  // Function to apply filters and sort hotels
  applyFilters(): void {
    const { minPrice, maxPrice, rating, sortBy } = this.filterForm.value;

    // Copy the original hotel list to apply filters
    this.hotels = [...this.originalHotels];

     // Check if minPrice is greater than maxPrice
     if (minPrice && maxPrice && minPrice > maxPrice) {
      alert("Minimum price cannot be greater than maximum price.");
      window.location.reload();
      
    }

    // Filter by price range if both minPrice and maxPrice are provided
    if (minPrice !== null || maxPrice !== null) {
      this.hotels = this.hotels.filter(hotel => {
        const price = this.parsePrice(hotel.Price);
        return (minPrice !== null ? price >= minPrice : true) &&
               (maxPrice !== null ? price <= maxPrice : true);
      });
    }

    // Filter by minimum rating
    if (rating) {
      this.hotels = this.hotels.filter(hotel => this.parseRating(hotel.Rating) >= parseFloat(rating));
    }

    // Sort hotels based on the selected option
    if (sortBy === 'rating') {
      this.hotels.sort((a, b) => this.parseRating(b.Rating) - this.parseRating(a.Rating));
    } else if (sortBy === 'price-low') {
      this.hotels.sort((a, b) => this.parsePrice(a.Price) - this.parsePrice(b.Price));
    } else if (sortBy === 'price-high') {
      this.hotels.sort((a, b) => this.parsePrice(b.Price) - this.parsePrice(a.Price));
    }
  }

  // Helper method to parse price from string to number
  private parsePrice(priceStr: string): number {
    // Regex to extract number from price string
    const extractedPrice = priceStr.replace(/[^0-9.]/g, '');
    return parseFloat(extractedPrice);
  }
}
