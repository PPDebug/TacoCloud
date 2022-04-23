import { Component, OnInit } from '@angular/core';
import { ApiService } from 'src/app/service/api.service';
import { CartService } from 'src/app/service/cart-service';
import { TacoItem } from 'src/app/service/taco-item';

@Component({
  selector: 'app-recents',
  templateUrl: './recents.component.html',
  styleUrls: ['./recents.component.css']
})
export class RecentsComponent implements OnInit {

  constructor(private api: ApiService, 
    private cart: CartService) { }

  recentTacos: any;

  ngOnInit(): void {
    this.api.getCommon("design/recents")
    .subscribe( res => {
      this.recentTacos = res;
    })
  }

  onSubmit(buttonName: String, taco: TacoItem): void{
    this.cart.addToCart(taco);
    console.log(taco);
    window.alert(buttonName);
  }

}
