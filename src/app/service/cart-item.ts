import { TacoItem } from "./taco-item";
export class CartItem{
    quantity = 1;

    taco: TacoItem;

    constructor(taco: TacoItem){
        this.taco = taco;
    }

    get lineToTatal() {
        return this.quantity * 4.99;
    }
}