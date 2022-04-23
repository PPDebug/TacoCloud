import { NgModule } from '@angular/core';
import { BrowserModule } from '@angular/platform-browser';
import { RouterModule } from '@angular/router';
import { ReactiveFormsModule, FormsModule} from '@angular/forms';
import { HttpClientModule } from '@angular/common/http';

import { routes } from './app.routes'

import { NonWrapsPipe } from './view/recents/non-wraps-pipe';
import { WrapsPipe } from './view/recents/wraps-pipe';

import { AppComponent } from './app.component';
import { HeaderComponent } from './component/header/header.component';
import { HomeComponent } from './view/home/home.component';
import { CloudTitleComponent } from './component/cloud-title/cloud-title.component';
import { LoginComponent } from './view/login/login.component';
import { RecentsComponent } from './view/recents/recents.component';
import { SpecialsComponent } from './view/specials/specials.component';
import { LocationsComponent } from './view/locations/locations.component';
import { DesignComponent } from './view/design/design.component';
import { CartComponent } from './view/cart/cart.component';
import { LittleButtonComponent } from './component/little-button/little-button.component';
import { BigButtonComponent } from './component/big-button/big-button.component';
import { FooterComponent } from './component/footer/footer.component';
import { GroupBoxComponent } from './component/group-box/group-box.component';

@NgModule({
  imports: [
    BrowserModule,
    HttpClientModule,
    ReactiveFormsModule,
    FormsModule,
    RouterModule.forRoot(routes),
  ],
  declarations: [
    AppComponent,
    HeaderComponent,
    HomeComponent,
    CloudTitleComponent,
    LoginComponent,
    RecentsComponent,
    NonWrapsPipe,
    WrapsPipe,
    SpecialsComponent,
    LocationsComponent,
    DesignComponent,
    CartComponent,
    LittleButtonComponent,
    BigButtonComponent,
    FooterComponent,
    GroupBoxComponent,
  ],
  bootstrap: [AppComponent],
})
export class AppModule {}

/*
Copyright Google LLC. All Rights Reserved.
Use of this source code is governed by an MIT-style license that
can be found in the LICENSE file at https://angular.io/license
*/
