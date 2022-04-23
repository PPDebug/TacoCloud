import { Component, OnInit } from '@angular/core';
import { CartService } from 'src/app/service/cart-service';
import { ApiService } from 'src/app/service/api.service';
import { TacoItem } from 'src/app/service/taco-item';

@Component({
  selector: 'app-cart',
  templateUrl: './cart.component.html',
  styleUrls: ['./cart.component.css']
})
export class CartComponent implements OnInit {

  model = {
    deliveryName: '',
    deliveryStreet: '',
    deliveryState: '',
    deliveryCity: '',
    deliveryZip: '',
    ccNumber: '',
    ccExpiration: '',
    ccCVV: '',
    tacos: [],
  }  

  constructor(
    private cart: CartService,
    private api: ApiService
  ) { }

  ngOnInit(): void {
  }

  get cartItems() {
    return this.cart.getItemsInCart();
  }

  get cartTotal() {
    return this.cart.geteCartTotal();
  }

  onSubmit() {
    this.cart.getItemsInCart().forEach(item => {
      this.model.tacos.push((item.taco as never));
    })
  }

}
