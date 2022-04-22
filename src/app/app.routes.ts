import { Routes } from '@angular/router'; 
import { CartComponent } from './view/cart/cart.component';
import { DesignComponent } from './view/design/design.component';
import { HomeComponent } from './view/home/home.component';
import { LocationsComponent } from './view/locations/locations.component';
import { LoginComponent } from './view/login/login.component';
import { RecentsComponent } from './view/recents/recents.component';
import { SpecialsComponent } from './view/specials/specials.component';

export const routes: Routes = [
    {
        path: 'home',
        component: HomeComponent
      },
      {
        path: 'login',
        component: LoginComponent
      },
      {
        path: 'recents',
        component: RecentsComponent
      },
      {
        path: 'specials',
        component: SpecialsComponent
      },
      {
        path: 'locations',
        component: LocationsComponent
      },
      {
        path: 'design',
        component: DesignComponent
      },
      {
        path: 'cart',
        component: CartComponent
      },
      {
        path: '**',
        redirectTo: 'home',
        pathMatch: 'full'
      },

]