import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { catchError, map, Observable, throwError } from 'rxjs';
import { Hotel } from '../models/Hotel';

@Injectable({
  providedIn: 'root'
})
export class HotelService {
  private apiURL = 'http://localhost:8080/api/hotels/';

  constructor(private httpClient: HttpClient) { }

  /*getHotelsByCity(city: string): Observable<Hotel[]> {
    return this.httpClient
      .get<Hotel[]>(this.apiURL + 'city/' + city)
      .pipe(catchError(this.errorHandler));
  }*/

  getHotelsByCity(city: string): Observable<{ count: number; hotels: Hotel[] }> {
    return this.httpClient
      .get<Hotel[]>(this.apiURL + 'city/' + city)
      .pipe(
        map(hotels => ({
          count: hotels.length,
          hotels: hotels
        })),
        catchError(this.errorHandler)
      );
  }


  getMostRatedHotels(): Observable<Hotel[]> {
    return this.httpClient
      .get<Hotel[]>(this.apiURL + 'top-rated')
      .pipe(catchError(this.errorHandler));
  }

  /*searchCars(searchBy: string, searchValue: string): Observable<Hotel[]> {
    return this.httpClient
      .get<Hotel[]>(this.apiURL + '/search?searchBy=' + searchBy + '&searchValue=' + searchValue)
      .pipe(catchError(this.errorHandler));
  } */ 
  
  errorHandler(error: {
    error: { message: string };
    status: any;
    message: any;
  }) {
    console.log(error);
    return throwError(
      () => new Error('Something bad happened; please try again later.')
    );
  }
}
