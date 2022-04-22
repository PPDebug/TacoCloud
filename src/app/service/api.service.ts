import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders} from '@angular/common/http';

@Injectable({
  providedIn: 'root'
})
export class ApiService {

  rootUrl = "http://localhost:8080";

  constructor(
    private httpClient: HttpClient
    ) { }

  getCommonn(subUrl: String){
    // return this.httpClient.get(this.rootUrl + subUrl);
    // for test:
    return this.httpClient.get("/assets/" + subUrl + ".json");
  }

  postCommon(subUrl:String, data: Object) {
    return this.httpClient.post(
      this.rootUrl + subUrl,
      data,
      {
        headers: new HttpHeaders().set("content-type", "applicatiion/json")
      }
    );
  }

  getAllIngredients() {
    return this.getCommonn("/design");
  }

  postDesign(taco: object){
    return this.postCommon("/design", taco);
  }
}
