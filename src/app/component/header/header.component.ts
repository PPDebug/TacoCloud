import { Component, OnInit } from '@angular/core';
import { CartService } from 'src/app/service/cart-service';

@Component({
  selector: 'app-header',
  templateUrl: './header.component.html',
  styleUrls: ['./header.component.css']
})

export class HeaderComponent implements OnInit {
  private cart: CartService;

  constructor(cart: CartService) { 
    this.cart = cart;
  }

  ngOnInit(): void {
  }

  price(): any { 
    return this.cart.geteCartTotal();
  }

}
