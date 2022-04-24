import { Component, OnInit } from '@angular/core';
import { Router } from '@angular/router'
import { TacoItem } from 'src/app/service/taco-item';
import { IngredientItem } from 'src/app/service/ingredient-item';
import { ApiService } from 'src/app/service/api.service';

@Component({
  selector: 'app-design',
  templateUrl: './design.component.html',
  styleUrls: ['./design.component.css']
})
export class DesignComponent implements OnInit {

  allIngredients: any;
  wraps: IngredientItem[] | undefined;
  proteins: IngredientItem[] | undefined;
  veggies: IngredientItem[] | undefined;
  cheeses: IngredientItem[] | undefined;
  sauces: IngredientItem[] | undefined;

  tacoName = '';

  private taco = new TacoItem();

  constructor(
    private router: Router,
    private api: ApiService
    ) {
  }

  ngOnInit(){
    // get Ingredient from server:
    this.api.getCommon("design/ingredients")
      .subscribe( (data ) => {
        this.allIngredients = data;
        this.wraps = this.allIngredients.filter( (w: IngredientItem) => w.type === 'WRAP');
        this.proteins = this.allIngredients.filter( (p: IngredientItem) => p.type === 'PROTEIN');
        this.veggies = this.allIngredients.filter((v: IngredientItem) => v.type === 'VEGGIES');
        this.cheeses = this.allIngredients.filter((c: IngredientItem) => c.type === 'CHEESE');
        this.sauces = this.allIngredients.filter((s: IngredientItem) => s.type === 'SAUCE');
        console.log(this.allIngredients);
      })
  }

  updateIngredients(ingredient: IngredientItem, event:any){
    if (event.target.checked) {
      this.taco.ingredients.push(ingredient);
    } else {
      this.taco.ingredients.splice(this.taco.ingredients.findIndex(i=>i === ingredient), 1);
    }
  }

  onSubmit() {
    this.taco.name = this.tacoName;
    this.api.postCommon("design", this.taco)
      .subscribe(res => {
        window.alert("Success");
        this.router.navigateByUrl("/recents")
      })
  }

}
