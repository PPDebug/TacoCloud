import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders} from '@angular/common/http';

@Injectable({
  providedIn: 'root'
})
export class ApiService {

  rootUrl = "http://localhost:8080/test";

  constructor(
    private httpClient: HttpClient
    ) { }

  public getCommon(subUrl: String){
    // return this.httpClient.get(this.rootUrl + subUrl, {
    //   headers: new HttpHeaders()
    //     .append("Access-Control-Allow-Origin","*")
    //     .append("Access-Control-Allow-Methods","GET, POST, PUT, OPTIONS, PATCH, DELETE")
    //     .append("Access-Control-Allow-Headers","X-Requested-With, content-type")
    //     ,
    // });
    // for test:
    return this.httpClient.get("/assets/" + subUrl + ".json");
  }

  public postCommon(subUrl:String, data: any) {
    console.log("api post: ", data);
    return this.httpClient.post(
      this.rootUrl + subUrl,
      data,
      {
        headers: new HttpHeaders().set("content-type", "application/json")
      }
    );
  }

}
