import { Routes } from '@angular/router';
import { SearchResultsComponent } from './components/search-results/search-results.component';
import { HomeComponent } from './components/home/home.component';

export const routes: Routes = [
    {path: '', redirectTo: "/home", pathMatch:"full"},
    {path: 'home', component: HomeComponent},
    {path: 'hotels/:city', component: SearchResultsComponent},
    {path: '**', component: HomeComponent}
];
