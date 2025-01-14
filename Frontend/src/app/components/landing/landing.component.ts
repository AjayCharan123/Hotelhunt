import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormControl, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { HotelService } from '../../services/hotel.service';
import { Router, RouterModule } from '@angular/router';
import { CommonModule, NgClass } from '@angular/common';
import { HotelsGridComponent } from "../hotels-grid/hotels-grid.component";

interface City {
  name: string;
  imageUrl: string;
}

@Component({
  selector: 'app-landing',
  standalone: true,
  imports: [ReactiveFormsModule, NgClass, CommonModule, RouterModule, HotelsGridComponent],
  templateUrl: './landing.component.html',
  styleUrl: './landing.component.css'
})
export class LandingComponent implements OnInit {
  searchForm!: FormGroup;
  todayDate = new Date();

  constructor(
    private hotelService: HotelService,
    private formBuilder: FormBuilder,
    private router: Router,
  ) {}

  ngOnInit(): void {
    this.searchForm = this.formBuilder.group({
      keyword: ['', Validators.required],
      checkinDate: ['', Validators.required],
      checkoutDate: ['', Validators.required]
    });
  }

  get Keyword(): FormControl {
    return this.searchForm.get('keyword') as FormControl;
  }

  get CheckinDate(): FormControl {
    return this.searchForm.get('checkinDate') as FormControl;
  }

  get CheckoutDate(): FormControl {
    return this.searchForm.get('checkoutDate') as FormControl;
  }

  cities: City[] = [
    {
      name: 'Calgary',
      imageUrl:
        'https://images.unsplash.com/photo-1665457926665-87d9c477e73f?w=600&auto=format&fit=crop&q=60&ixlib=rb-4.0.3&ixid=M3wxMjA3fDB8MHxzZWFyY2h8M3x8Y2FsZ2FyeXxlbnwwfDF8MHx8fDA%3D',
    },
    {
      name: 'Edmonton',
      imageUrl:
        'https://images.unsplash.com/photo-1709999370331-8343162e267f?w=600&auto=format&fit=crop&q=60&ixlib=rb-4.0.3&ixid=M3wxMjA3fDB8MHxzZWFyY2h8N3x8ZWRtb250b258ZW58MHwxfDB8fHww',
    },
    {
      name: 'Halifax',
      imageUrl:
        'https://images.unsplash.com/photo-1674676489937-a78540ff8cea?w=600&auto=format&fit=crop&q=60&ixlib=rb-4.0.3&ixid=M3wxMjA3fDB8MHxzZWFyY2h8Mnx8aGFsaWZheHxlbnwwfDF8MHx8fDA%3D',
    },
    {
      name: 'Hamilton',
      imageUrl:
        'https://images.unsplash.com/photo-1575222421927-3d83afc1e671?w=600&auto=format&fit=crop&q=60&ixlib=rb-4.0.3&ixid=M3wxMjA3fDB8MHxzZWFyY2h8MTB8fGhhbWlsdG9ufGVufDB8MXwwfHx8MA%3D%3D',
    },
    {
      name: 'Kelowna',
      imageUrl:
        'https://images.unsplash.com/photo-1604956472041-845bc1b4eeed?w=600&auto=format&fit=crop&q=60&ixlib=rb-4.0.3&ixid=M3wxMjA3fDB8MHxzZWFyY2h8MTN8fGtlbG93bmF8ZW58MHwxfDB8fHww',
    },
    {
      name: 'Kitchener',
      imageUrl:
        'data:image/jpeg;base64,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',
    },
    {
      name: 'Montreal',
      imageUrl:
        'https://images.pexels.com/photos/6073227/pexels-photo-6073227.jpeg?auto=compress&cs=tinysrgb&w=600',
    },
    {
      name: 'Niagara Falls',
      imageUrl:
        'https://images.pexels.com/photos/3490963/pexels-photo-3490963.jpeg?auto=compress&cs=tinysrgb&w=600',
    },
    {
      name: 'Ottawa',
      imageUrl:
        'https://images.pexels.com/photos/3656756/pexels-photo-3656756.jpeg?auto=compress&cs=tinysrgb&w=600',
    },
    {
      name: 'Quebec City',
      imageUrl:
        'https://images.pexels.com/photos/20419680/pexels-photo-20419680/free-photo-of-chateau-frontenac-historic-hotel-in-quebec-city-canada.jpeg?auto=compress&cs=tinysrgb&w=600',
    },
    {
      name: 'Regina',
      imageUrl:
        'https://images.pexels.com/photos/22915775/pexels-photo-22915775/free-photo-of-garden-at-the-villa-della-regina-turin-piedmont-italy.jpeg?auto=compress&cs=tinysrgb&w=600',
    },
    {
      name: 'Saskatoon',
      imageUrl:
        'https://images.pexels.com/photos/8170192/pexels-photo-8170192.jpeg?auto=compress&cs=tinysrgb&w=600',
    },
    {
      name: 'Toronto',
      imageUrl:
        'https://images.pexels.com/photos/139769/pexels-photo-139769.jpeg?auto=compress&cs=tinysrgb&w=600',
    },
    {
      name: 'Vancouver',
      imageUrl:
        'https://images.pexels.com/photos/26977125/pexels-photo-26977125/free-photo-of-modern-skyscrapers-in-vancouver-canada.jpeg?auto=compress&cs=tinysrgb&w=600',
    },
    {
      name: 'Victoria',
      imageUrl:
        'https://images.pexels.com/photos/15266795/pexels-photo-15266795/free-photo-of-view-of-the-the-twelve-apostles-rock-formations-port-campbell-national-park-the-great-ocean-road-in-victoria-australia.jpeg?auto=compress&cs=tinysrgb&w=600',
    },
    {
      name: 'Waterloo',
      imageUrl:
        'https://images.pexels.com/photos/18612177/pexels-photo-18612177/free-photo-of-wellington-waterloo-museum-in-belgium.jpeg?auto=compress&cs=tinysrgb&w=600',
    },
    {
      name: 'Windsor',
      imageUrl:
        'https://images.pexels.com/photos/10492318/pexels-photo-10492318.jpeg?auto=compress&cs=tinysrgb&w=600',
    },
    {
      name: 'Winnipeg',
      imageUrl:
        'https://images.pexels.com/photos/19841645/pexels-photo-19841645/free-photo-of-calvary-temple-in-winnipeg-canada.jpeg?auto=compress&cs=tinysrgb&w=600',
    },
  ];
}
