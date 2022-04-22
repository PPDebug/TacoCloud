import { Injectable } from '@angular/core';
import { TacoItem } from './taco-item';
import { IngredientItem } from './ingredient-item';
import { ApiService } from './api.service';

@Injectable({
  providedIn: 'root'
})
export class TacoService {
  allIngredients: any;
  wraps = [];
  proteins = [];
  veggies = [];
  cheeses = [];
  sauces = [];

  private taco = new TacoItem;

  constructor(private api: ApiService) {
  }

  ngOninit(){
    // get Ingredient from server:
    this.api.getAllIngredients()
      .subscribe( (data) => {
        this.allIngredients = data;
        this.wraps = this.allIngredients.filter( (w: IngredientItem) => w.type === 'WRAP');
        this.proteins = this.allIngredients.filter( (p: IngredientItem) => p.type === 'PROTEIN');
        this.veggies = this.allIngredients.filter((v: IngredientItem) => v.type === 'VEGGIES');
        this.cheeses = this.allIngredients.filter((c: IngredientItem) => c.type === 'CHEESE');
        this.sauces = this.allIngredients.filter((s: IngredientItem) => s.type === 'SAUCE');
      })
  }

  updateIngredients(ingredient: IngredientItem, event:any){
    if (event.target.checked) {
      this.taco.ingredients.push(ingredient);
    } else {
      this.taco.ingredients.splice(this.taco.ingredients.findIndex(i=>i === ingredient), 1);
    }
  }

  

}
