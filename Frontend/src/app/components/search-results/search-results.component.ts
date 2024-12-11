import { Component } from '@angular/core';
import { HotelsGridComponent } from "../hotels-grid/hotels-grid.component";
import { FooterComponent } from "../footer/footer.component";
import { HeaderComponent } from "../header/header.component";
import { ActivatedRoute } from '@angular/router';
import { HotelService } from '../../services/hotel.service';

@Component({
  selector: 'app-search-results',
  standalone: true,
  imports: [HotelsGridComponent, FooterComponent, HeaderComponent],
  templateUrl: './search-results.component.html',
  styleUrl: './search-results.component.css'
})
export class SearchResultsComponent {
  hotelCount: number = 0;
  city: string = this.actRoute.snapshot.params['city'];

  constructor(
    public actRoute: ActivatedRoute, public hotelService: HotelService
  ) {}

  ngOnInit(): void {
      this.hotelService.getHotelsByCity(this.city).subscribe(data => {
        //this.hotels = data.hotels;
        this.hotelCount = data.count;
      });
  }
}
