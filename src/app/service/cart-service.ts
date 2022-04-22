import { Injectable } from '@angular/core';
import { CartItem } from "./cart-item";

@Injectable({
  providedIn: 'root'
})
export class CartService {

  items$: CartItem[] = [];

  constructor() {
    this.items$ = []
  }

  addToCart(taco: any) {
    this.items$.push(new CartItem(taco));
  }

  getItemsInCart() {
    return this.items$;
  }

  geteCartTotal() {
    let total = 0;
    this.items$.forEach(item => {
      total += item.lineToTatal;
    })
    return total;
  }
  
  emptyCart() {
    this.items$ = [];
  }
}
